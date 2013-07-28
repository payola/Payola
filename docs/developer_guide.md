<a name="top"></a>
<a name="developer"></a>

# Developer Guide

Purpose of this document is to describe in depth implementation ideas behind the Payola and help the developers, who'd like to extend or modify the application, to get familiar with the project. 

The Payola application consists of several layers and libraries that are all enclosed within a solution project `payola`. The following sections describe structure of the solution, the functionality hidden within the layers and libraries and their relations. For information how to install, compile and run the Payola, please refer to the [Installation Guide](https://raw.github.com/payola/Payola/master/docs/installation_guide.md).

## Motivation

The term *graph data* describes data consisting of entities and relations among them. Their main property is, that they have no or loosely defined (and abided) schema, which distincts them from the strictly defined data stored in the relational databases. Graph data are for example RDF data that are accessible on the internet as so called [Linked Data](http://linkeddata.org), or data that are provided by social networks such as Facebook or LinkedIn. Many companies also have their internal data in the form of graph data.

So a tool that could interconnect, analyze and visualize the graph data is desired by many people nowadays. There already are some applications that provide such functions (<http://www.gooddata.com>, <http://www.google.com/publicdata/>, <http://opendata.socrata.com>), however those tools support only data in table-like format. They also have many general features, that may be applied even on graph data, e.g. allowing the users to create their analyses of the data and share or sell the analyses to others.

There are already some tools, that are capable of visualizing the graph data, yet they're usually strictly fixed to just one data set. For example <http://obchodni-rejstrik.podnikani.cz/applet>.

## Aim of the project

The Payola is a web application, that allows its users to work with any graph data. That is to import the data to the application, analyze them and visualize them. The main functions are:

- The user owns and manages his private data space. He can upload graph data there from his computer or provide an URL from which the data should be imported.
- The user can share his data with other users.
- The user can create and share data sources (entities that provide access to the graph data, for example a SPARQL endpoint).
- The user can create, edit and share an analyses (tree-like structures where the data flow from the leaves to the root and are processed when passing through a node).
- The user can browse through the data sources and the analysis results using a generic visualization tool. He can also alter the visualization using ontologies.

## Solution structure

The solution is defined using the [SBT](https://github.com/harrah/xsbt/wiki/ "SBT") build file which isn't tightly bound to any particular IDE, so you may generate corresponding project files for the most commonly used IDEs (e.g. [IntelliJ IDEA](http://www.jetbrains.com/idea/), [Eclipse](http://www.eclipse.org)). SBT doesn't support any concept that can be directly used as a solution, but it can be emulated using projects and subprojects. In our case, the `payola` solution is just a project with no source files. The solution structure is:

- `payola`
	- [`common`](#common)
	- [`data`](#data)
	- [`domain`](#domain)
	- [`model`](#model)
	- [`project`](#project)
	- [`s2js`](#s2js)
		- [`adapters`](#adapters)
		- [`compiler`](#compiler)
		- [`runtime`](#runtime)
			- [`client`](#runtime-client)
			- [`shared`](#runtime-shared)
	- [`scala2json`](#scala2json)
	- [`web`](#web)
		- [`client`](#client)
		- [`initializer`](#initializer)
		- [`shared`](#shared)
		- [`server`](#server)

![Project depedencies](https://raw.github.com/payola/Payola/master/docs/img/project_dependencies.png)

Briefly, the project [`payola/project`](#project) defines this structure, dependencies between the projects, external dependencies and the build process, so it can be understood as a Makefile equivalent. Somewhat standalone libraries are the [`payola/scala2json`](#scala2json) project which provides means of Scala object serialization into the JSON format and [`payola/s2js`](#s2js) project which with all its subprojects enables us to write web applications in Scala (to compile Scala code to equivalent JavaScript code).

The Payola application itself is spread within the rest of the projects, namely [`payola/common`](#common) that defines classes that are used throughout all layers and even on the client side. The [`payola/domain`](#domain) mostly extends classes from the [`payola/common`](#common) with backend logic. The [`payola/data`](#data) is a persistence, data access layer. The [`payola/model`](#model) wraps up the previous three modules with a uniform interface. It's meant as a standard programmatic access point to Payola. Finally, the web application itself consists of the [`payola/web/initializer`](#initializer) which is a console application initializing the database (i.e an installer), [`payola/web/server`](#server) that is a [Play](http://www.playframework.org/) web application, the [`payola/web/client`](#client) which contains a browser MVP application (compiled to JavaScript) and last but not least the [`payola/web/shared`](#shared) which consists of objects that are called from the client, but executed on the server.

This structure also determines package names, which follow the pattern `cz.payola.[project path where '/' is replaced with '.']`. So, for example, a class declared in the `payola/web/client` project can be found in the `cz.payola.web.client` package or one of its subpackages. The [`payola/s2js`](#s2js) project uses different package naming conventions, all packages and subpackages have the `cz.payola` prefix left out, so they start with `s2js`.

<a name="project"></a>
## Project payola/project

This project contains only two files: `plugins.sbt` and `PayolaBuild.scala`. The former one is just a configuration SBT file, i.e. SBT plugins that should be used on top of standard SBT and additional Maven repository declarations to download dependencies from.

The `PayolaBuild.scala` is a [build definition file](https://github.com/harrah/xsbt/wiki/Getting-Started-Full-Def) of the whole solution. The solution structure, projects, dependencies, compilation, test settings and other concepts used there are described in depth in the [SBT Wiki](https://github.com/harrah/xsbt/wiki). Moreover, there is a template for all projects that should be compiled to JavaScript, that adds the [s2js](#s2js) compiler plugin to the standard Scala compiler. To create a project that should be compiled to JavaScript, use `ScalaToJsProject(...)` instead of the standard `Project(...)`.

<a name="cp"></a>
### The cp task

The build file defines a custom SBT Task called `cp` which is an abbreviation for 'compile and package'. In order to support compilation of the payola solution in one step, we had to introduce this non-standard task. Because the solution contains both the [s2js](#s2js) compiler plugin project and also projects that use that compiler plugin, it's not sufficient to mark the compiler plugin project as a dependency of projects that should be compiled to Javascript. The Scala compiler accepts only `.jar` plugin files so the compiler plugin project has to be not only compiled, but also packed into a `.jar` package before it can be used.

### Compilation of payola/web/server using cp

Another compilation customization required by [s2js](#s2js) is added to the compilation process of the [server project](#server). During compilation of a `ScalaToJsProject`, the generated `.js` files are written to the `payola/web/server/public/javascripts` directory. Each file provides some symbols (classes and objects declared in the file) and requires some (classes and objects used in the file). All files in the previously mentioned directory are traversed, while extracting the dependency declarations to the `payola/web/server/public/dependencies` file, which is used later.

### The clean task

The `clean` SBT task is overridden to ensure all generated files are deleted in addition to the standard behavior of `clean`.

<a name="scala2json"></a>
## Package cz.payola.scala2json

To transfer data from the server side to the client side, one needs to serialize the data. Not only to save bandwidth, we've chosen [JSON](http://www.json.org) as the data format. It is a lightweight format that's also easy to decode in JavaScript, which is used on the client side.

While other solutions for serializing Scala objects to JSON do exist (for example [scala-json](https://github.com/stevej/scala-json)), they mostly work on collections only, maps and numeric types. Other objects need to implement their own `toJSON()` method.

This seemed to us as too much unnecessary code, so we've decided to write our own serializer. This serializer is capable of serializing any Scala or Java object using the Java language reflection.

The serialization process has to deal with a few obstacles, such as cyclic object dependencies (i.e. one object's variable is pointing to a second object which has a variable pointing back to the first one).

- **Cyclic dependencies**: The serializer has an option to either serialize objects in depth (this means that a cyclic dependency will cause an exception), or to handle cycles using object references. The first option has an advantage that no references need to be created, hence the resulting JSON is exactly the object's representation, with no additional fields and is very easy to deserialize into the resulting object.<br/><br/>A cyclic dependency, however, is fairly common, hence it had to be dealt with: the serializer keeps a list of objects it encounters during serialization - each object is then assigned an object ID which is simply an index of the object in the encountered-objects list. The object ID is then appended to the serialized object as an `__objectID__` field . Once the object is encountered for the second time, instead of serializing the object again, such a construct is used: `"object_for_the_second_time": { "__ref__": 4 }` - i.e. a reference to an object with an object ID `4`.
- **Classes**: when deserializing the object on the client, a class of the object is required, so it needs to be included in the serialized object:
	- *Regular objects*: For regular objects, `__class__` field is added which contains the class name: `"some_obj": { "__class__": "cz.payola.some.class", ... }`
	- *Maps*: Even for maps and other collection, a class name is needed. Maps are translated to a JSON object with two field: `__class__` and `__value__`: `{ "map": { "__class__": "scala.collection.Map", "__value__": { "key": "value", ... } } }`
	- *Collections*: Other collections get translated to a JSON object with two fields as well: `{ "collection": { "__arrayClass__": "scala.collection.mutable.ArrayBuffer", "__value__": [ "obj1", ... ] } }`
	- *Arrays*: Regular array (i.e. `scala.Array`) is translated directly to a JSON array without any wrapper.
- **Fields**: As the Scala language doesn't have its own reflection API yet, [Java reflection API](http://docs.oracle.com/javase/tutorial/reflect/index.html) has to be used. This presents several problems regarding retrieving the object fields: some fields in Scala are translated into methods - a getter with no parameters and a setter with one parameter, in case it's a `var` field. The serializer must therefore look for fields even within methods when looking for a field of a particular name. Also, when requesting fields on an object, an empty array is returned - only declared fields get listed, so the serializer must go through the whole class hierarchy itself, listing fields of all interfaces and superclasses.

> Assume this code:
>```scala
class Class1 {
    val map = Map("my key" -> "my value", "key2" -> "value")
    val list = List("Hello")
    var obj: Any = null
}
val o = new Class1
o.obj = o
```
> The object `o` will be serialized to:
>```js
{
	"__class__": "cz.payola.scala2json.test.Class1",
	"__objectID__": 0,
	"map": {
		"__class__": "scala.collection.immutable.Map$Map2",
		"__value__": {
			"my key": "my value",
			"key2": "value"
		}
	},
	"list": {
		"__arrayClass__": "scala.collection.immutable.List",
		"__value__": [
			"Hello"
		]
	},
	"obj": {
		"__ref__": 0
	}
}
```

For some purposes, customizing the serialization process is necessary - it has proven useful to skip or add some fields of the object, etc. - this leads to serialization rules. For example, you might want to hide an implementation detail that a class' private fields are prefixed with an underscore (`_`) - it is possible to do so simply by adding a new `BasicSerializationRule`, where you can define a class (or trait) whose fields should be serialized (e.g. you want to serialize only fields of a common superclass, ignoring fields of subclasses), a list of fields that should be omitted (transient fields) and a list of field name aliases (a map of string &rarr; string translations).

The rules are applied in the same order as they are added to the serializer. You can explore additional serialization rules in the generated API documentation.

<a name="s2js"></a>
## Project payola/s2js

In order to implement the whole application in one language and to avoid code duplication that arises during development of rich internet applications (duplication of domain class declarations), we have decided to use a tool that compiles Scala code to JavaScript. First of all, we have investigated tools that already exist:

- [https://github.com/alvaroc1/s2js](https://github.com/alvaroc1/s2js)
- [http://scalagwt.github.com/](http://scalagwt.github.com/)
- [https://github.com/efleming969/scalosure](https://github.com/efleming969/scalosure)

The first two unfortunately didn't match our needs, mostly because they're still in a development phase and could be marked experimental. The build process of Scala+GWT seemed to be integrable into our build system only with huge difficulties and complexity of the tool (e.g. the compilation process) discouraged us from potential modifications of our own. The third one, Scalosure, successor of the s2js, appealed to us the most thanks to its integration of [Google Closure Library](http://closure-library.googlecode.com/svn/docs/index.html) and relative lightweightness. Abandoned development of the Scalosure, however, was definitely disadvantage number one.

We have commenced with Scalosure, but rather sooner than later, we got to a point where we had to modify and extend the tool itself. As we dug deeper and deeper into the Scalosure, we started to dislike its implementation. Having in mind that the core of Scalosure was just about 1000 LOC (including many duplicities), we have decided to start fresh and implement our own tool, heavily inspired by Scalosure.

To make everything work, not only the [Scala to JavaScript compiler](#compiler) is necessary. One often needs to use already existing JavaScript libraries without the necessity to rewrite them to Scala. That's what the [adapters project](#adapters) is for. The somehow opposite direction is usage of classes from the [Scala Library](http://www.scala-lang.org/api/current/index.html#package) which can't be currently compiled using any compiler, not even ours. So the [runtime project](#runtime) contains simplified mirrors of the Scala Library classes compilable to JavaScript. There are also our own classes that are needed for the s2js runtime both in the browser and on the server.

Note that the tool was created just to match the requirements of Payola, so there are many gaps in implementation and ad-hoc solutions. Supported adapters and Scala Library classes are only those, we needed.

<a name="compiler"></a>
### Package s2js.compiler

The heart of the Scala to JavaScript process is surely the compiler. In fact, it's not a standalone compiler, it's a [Scala Compiler Plugin](http://www.scala-lang.org/node/140). So it takes advantage of the standard Scala compiler, which does the 'dirty' work of lexical analysis, syntax analysis and construction of the [abstract syntax trees](http://en.wikipedia.org/wiki/Abstract_syntax_tree) (ASTs) corresponding to the code that is being compiled. The Scala compiler consists of a sequence of phases, that can be perceived as functions taking an AST and producing an AST. There are some [standard phases](https://wiki.scala-lang.org/display/SIW/Overview+of+Compiler+Phases) that continually alter the AST, so Java bytecode can be finally generated. A Scala compiler plugin is just another sequence of phases that is mixed into the sequence of the standard phases on the specified places.

#### Class s2js.compiler.ScalaToJsPlugin

This is the definition of the Scala compiler plugin, its only phase `ScalaToJsPhase` and its components as it's described it the official [Scala Compiler Plugin tutorial](http://www.scala-lang.org/node/140). The plugin doesn't change the input AST at all, it behaves like an identity function. But as a side-effect, it generates JavaScript code that should be equivalent to the input AST. The following custom plugin options are defined here:

- `outputDirectory`: The directory where the generated JavaScript files are placed. Default value is the current directory.
- `createPackageStructure`: If set to `true` a directory structure mirroring the packages in compiled files is created in the output directory. If set to `false`, all generated files are created right in the output directory, which is taken advantage of during the compiler plugin tests.

Usage of the options can be found in the `PayolaBuild.scala` within the 
[`project`](#project) project.

#### Class s2js.compiler.ScalaToJsCompiler

An extension of the Scala compiler, that has the `ScalaToJsPlugin` plugged in, so the compilation of Scala files into JavaScript can be invoked programatically.

#### Package s2js.compiler.components

The previous two classes are utility classes, that don't participate in the compilation. They just invoke it. On the other hand, classes from the `s2js.compiler.components` directly take part in the compilation process. 

##### Class s2js.compiler.components.PackageDefCompiler

Purpose of this class is to compile the `PackageDef` AST nodes (representation of a package and all its content within a file) into JavaScript. Because the plugin compiler input AST is always a `PackageDef` node, the class is used as an entry point to the compilation process. 

> **Note**: The `ClassDef` is a type of an AST node, that defines a class, a trait, an object or a package object.

The compilation algorithm works basically in the following way:

1. Retrieve the structure of the package using the [`s2js.compiler.components.DependencyManager`](#DependencyManager). It traverses the AST, finds all `ClassDef`s and initializes the dependency graph of them. 
2. Compile the `ClassDef`s using the [`s2js.compiler.components.ClassDefCompiler`](#ClassDefCompiler) in the topological ordering determined by the `ClassDef` dependency graph. If there is a cycle in the dependency graph, an exception is thrown. During the compilation of `ClassDef`s, the `DependencyManager` is informed about the inter-file dependencies (e.g. when a compiled class extends a class that is not part of the current compilation unit ~ file).
3. Add the inter-file dependency declarations to the beginning of the compiled JavaScript file.

Moreover, the `PackageDefCompiler` defines additional public service methods (e.g. `symbolHasAnnotation`, `typeIsFunction` etc.) that can be used by other components with a reference to the `PackageDefCompiler`.

<a name="DependencyManager"></a>
##### Class s2js.compiler.components.DependencyManager

Tracks all kinds of so-called dependencies among symbols that are declared inside a `PackageDef` node:

- *ClassDef dependency graph*: Dependencies among `ClassDef`s within the current compilation unit (`ClassDef` `A` depends on `ClassDef` `B` if `A` extends or mixins `B`). The graph is used to determine an order of the class compilation. 
- *Inter-file dependencies*
	- *Provided symbols*: The `ClassDef`s that the current compilation unit provides (i.e. the API). Some other compilation units may require them.
	- *Declaration-required symbols*: The `ClassDef`s that have to be declared in the generated JavaScript before the `ClassDef`s from the current compilation unit are declared.
	- *Runtime-required symbols*: The `ClassDef`s that have to be declared in the generated JavaScript so that the current compilation unit can run (they're not needed when declaring the current compilation unit in generated JavaScript).

<a name="ClassDefCompiler"></a>
##### Class s2js.compiler.components.ClassDefCompiler

In terms of code lines, the `ClassDefCompiler` is the largest class of the project, with objective to compile `ClassDef` AST nodes with everything they contain. The `ClassDefCompiler` has subclasses for some types of `ClassDef`s that slightly alter behavior of the compiler to fit the particular `ClassDef` needs:

- `ClassCompiler` for classes and traits.
- `ObjectCompiler` for objects.
- `PackageObjectCompiler` for package objects.

Compilation of a `ClassDef` is composed of the three following steps:

1. Compile the `ClassDef` constructor.
2. Compile the members (fields, methods) of the `ClassDef`. Inner classes or objects aren't currently supported.
3. Bind the `ClassDef` JavaScript prototype to an instance of the  [`s2js.runtime.core.Class`](#Class) class, so all instances of the `ClassDef` reference it.

Most of the Scala language constructs are compiled into JavaScript pretty naturally, majority of them have direct equivalents in the target language, so these naturally translated constructs won't be described here, as it would be a waste of space. We'll concentrate on how the differences between the languages are solved and on some other interesting details or extensions.

###### Expressions

In Scala, everything is an expression with a return value, even if the return value is `Unit` (void). As a consequence, return values of statements can be directly assigned to a variable. For example the `if-then-else` statement has a return value in Scala, yet in JavaScript, it doesn't. This is solved by wrapping the statement in an anonymous function, which is immediately invoked.

> Scala code:

```scala
val x = if (a) { b } else { c }
````

> compiles into:

```js
var x = (function() { if (a) { return b; } else { return c; } })();
````

###### Operators

There are no operators in Scala, only methods with names like `+` or `<=` that the Scala compiler transforms into `$plus` or `$less$eq`. On primitive types that are compiled into JavaScript primitive types (`Number`, `String` and `Boolean`), those methods are compiled as the JavaScript operators. On other types, the methods are left untouched.

###### Match statement

JavaScript has no usable equivalent of the Scala match expression, so a sequence of `if-then` statements, that correspond to the match cases, is used instead. A lot of technical details has to be taken into account as there are a few pattern types, variable bindings, guards, etc.

###### Classes and inheritance

JavaScript is a prototype-based language, so it has no notion of classes at all. But it can be emulated using object constructor functions and prototypes. The constructor function defines and initializes fields of the object (`val`s and `var`s) and executes the class constructor body. Methods of the class are set to the object constructor prototype. When the JavaScript runtime resolves object properties (fields, methods), it first looks directly into the object and then it traverses the object prototype chain so all class methods are found there.

Inheritance is implemented by setting the prototype of the sub-class prototype to the super-class prototype (i.e. linking the prototypes to a chain). Trait inheritance is divided into two steps. First of all, references to all methods of the trait prototype are copied into the class prototype. Secondly, within the class constructor, the trait is instantiated and all fields of the trait instance are copied to the class instance that is being constructed.

A little problem arises with the objects and package objects whose declaration is also their initialization (correspondence to a static constructor). The problem is, that the objects may access other objects during their initialization. What's even worse is, that this access may be indirect by for example instantiation a class that accesses other object in its constructor. So it's almost impossible to determine the object initialization order. We've solved this by introducing the [lazy pattern](http://en.wikipedia.org/wiki/Lazy_initialization); the object initialization is delayed till the moment when it's actually accessed for the first time. This ensures that the objects are initialized in the proper order.

###### Annotation `@javascript`

This annotation can be used on a method or a field, enabling the programmer to implement the method body or the field value directly in JavaScript. The method body or field value can be anything in Scala, because it gets replaced with the value provided to the annotation.


###### Annotation `@remote`

An object or class marked with this annotation isn't compiled to JavaScript.

<a name="s2js_rpc"></a>
###### RPC (Remote Procedure Call)

In order to simplify the client-server communication and to hide the low-level JavaScript constructs necessary for it (`XmlHttpRequest` or `ActiveXObject`) from the programmer, an [RPC mechanism](http://en.wikipedia.org/wiki/Remote_procedure_call) is used. The compiler takes a small but irreplaceable part in the whole process, the rest is done in the [shared runtime](#runtime-shared) and [client-side runtime](#runtime-client). 

All methods that are marked with the `@remote` annotation or defined on an object with the `@remote` annotation are considered RPC methods (remote methods), so their invocations are compiled to JavaScript in a different way. Remote methods may also be marked with the `@async` annotation, which makes them behave asynchronously, introducing additional constraints on them (i.e. they must have the success callback function and fail callback function parameters and Unit return type). The compilation of a remote method invocation can be seen in the following example:

> Scala code: 

```scala
@remote object remote {
    def foo(bar: Int, baz: String): Int = bar * baz.length

    @async def asyncFoo(bar: Int, baz: String)
        (successCallback: Int => Unit)
        (errorCallback: Throwable => Unit) {
                                
        successCallback(bar * baz.length)
    }
}

.
.
.

// Somewhere in an object or class that is compiled into JavaScript.
val x = remote.foo(123, "RPC rocks.")

remote.asyncFoo(123, "RPC rocks.") { result: Int =>
    window.alert("Method asyncFoo returned " + result)
} { throwable: Throwable =>
    window.alert("Exception " + throwable.message)
}
```

> compiles into: 

```js
// Somewhere in the compiled object or class.
s2js.runtime.client.rpc.Wrapper.callSync(
	'remote.foo', 
	[123, 'RPC rocks.'], 
	['scala.Int', 'java.lang.String']
);

s2js.runtime.client.rpc.Wrapper.callAsync(
	'remote.asyncFoo',
	[123, 'RPC rocks.'], 
	['scala.Int', 'java.lang.String'],
	function(i) { window.alert('Method asyncFoo returned ' + i); },
	function(e) { window.alert('Exception ' + e.message); }
}
```

The array with types (`['scala.Int', 'java.lang.String']`) is there because Scala 2.9.1 doesn't provide full reflection support and the server-side runtime can't distinguish between `List[Double]` and `List[Int]`.

Another related annotation is `@secured` which can be used both on remote methods or remote objects. If used on a method or if a method is declared within an object that is marked with that annotation, then the method is considered a secured remote method. The consequence is, that the last parameter is regarded as a security context, which isn't sent from the client, so the server-side runtime has to resolve it by itself (i.e. retrieve a user from the database by his id, which is stored in the cookie).

> Scala code: 

```scala
@remote @secured def foo(bar: Int, baz: String, user: User = null): Int = bar * baz.length
```

> compiles into: 

```js
s2js.runtime.client.rpc.Wrapper.callSync(
	'remote.foo', 
	[123, 'RPC rocks.'], 
	['scala.Int', 'java.lang.String']
);
```

<a name="adapters"></a>
### Package s2js.adapters

As mentioned before, the adapters are defined to allow a programmer to access core JavaScript functionality or to use already existing JavaScript libraries. Without them, it would be impossible to use for example the `document.getElementById` method in Scala code that will be compiled into JavaScript, because there is no such object `document` in the Scala standard library. The adapter classes don't have to contain any implementation, therefore they're mostly abstract classes or traits. They're not compiled to JavaScript, and neither are they used anywhere during Scala application runtime. The class and method names have to be exactly the same as in the adapted libraries.

#### Package s2js.adapters.js

Adapters of some of the JavaScript core classes and the global functions, objects and constants. The adapters are based on the 'JavaScript Objects' section of the [JavaSript and HTML DOM Reference](http://www.w3schools.com/jsref/default.asp).

#### Package s2js.adapters.dom

Adapters of all interfaces and objects (`Node`, `Element` etc.) defined in the [DOM Level 3 Core Specification](http://www.w3.org/TR/DOM-Level-3-Core/).

#### Package s2js.adapters.events

Adapters of all interfaces and objects (`Event`, `MouseEvent` etc.) defined in the [DOM Level 3 Events Specification](http://www.w3.org/TR/DOM-Level-3-Events/) including some of the the [DOM Level 4 Events](http://www.w3.org/TR/dom/#events) extensions.

#### Package s2js.adapters.html

Selected HTML related interfaces and elements (`Document`, `Anchor`, `Canvas` etc.), based both on the [HTML Standard](http://www.whatwg.org/html) and on the 'HTML DOM Objects' section of the [JavaSript and HTML DOM Reference](http://www.w3schools.com/jsref/default.asp)

#### Package s2js.adapters.browser

Adapters of web browser related objects (`Window`, `History` etc.), based on the 'Browser Objects' section of the [JavaSript and HTML DOM Reference](http://www.w3schools.com/jsref/default.asp) and also on the same resources as the `s2js.adapters.html` package.

<a name="runtime"></a>
### Package s2js.runtime

Unlike the compiler and adapters which are used during the compile time, the runtime project defines classes and objects that are necessary during runtime of an s2js application. There are actually two kinds of runtime: the client-side runtime and the shared runtime, whose code can be executed both on the server side and on the client side. Subprojects of the runtime project correspond to this separation.

<a name="runtime-client"></a>
#### Package s2js.runtime.client

Code of this project is compiled into JavaScript and is executed only on the client side. 

##### Package s2js.runtime.client.core

The package object `s2js.runtime.client.core` defines the core methods, that are required by the compiler (almost any compiled code requires them). They're mainly used when emulating some features of Scala in JavaScript, for example `inherit`, `mixIn`, `isInstanceOf`, etc. Most of them are implemented natively, some of them may be perceived as 'smart adapters' (e.g. `isUndefined` or `isObject`).

There is also the class `Class` which stores information about a class (name, super classes) and is used for type-checks and type-conversions.

The `ClassLoader` keeps track of declared (i.e. loaded) classes and makes sure that a class is already declared when it's declaration-required. Otherwise it throws an exception. Interesting thing is, that the `ClassLoader` is also a class so before it's declared, it notifies the current class loader that the `ClassLoader` class is being declared. To overcome this 'ad-infinitum' issue, a temporary class loader, which is declared natively in JavaScript, was introduced. It serves the purpose of the full-featured class loader. When the `ClassLoader` is declared (so it may be used), all classes that are registered in the temporary class loader are also registered in the full-featured class loader and the current class loader is set to the full-featured one.

##### Package s2js.runtime.client.js

The `JsObject` and `JsArray` are just wrappers around native JavaScript objects and arrays, so their dynamic properties may be retrieved and set in Scala code.

Another helper class is the `JsonTraverser` which defines an interface of generic JSON traverser. It, however, doesn't traverse the string representation of JSON, it's used to traverse an object, that may have been created using the `eval` function on the JSON string representation. There is just one method `traverse` which visits all the properties and items of the object and invokes the abstract visitor methods corresponding to the visited object types. Subclasses of the `JsonTraverser` must implement the visitor methods.

##### Package s2js.runtime.client.scala

In order to allow the programmer to use classes and objects that are available in the standard Scala Library (e.g. `Option`, `List`, `Map` etc.), their equivalent had to be created in JavaScript. An ideal way would be to take the sources of the Scala Library and compile them into JavaScript using the [compiler](#compiler). But this task is too complex to accomplish, so another approach was used.

The sources from the Scala Library that were fully or with minor changes compilable to JavaScript were used as is. That's the case of most of the simple classes like `Option`, `Tuple` or `Product`. The complex classes, mainly in the collection library were partially written from scratch, but most of the logic was ported from the Scala Library sources.

> The classes and objects (and their methods) in this package were written when they were needed, not in advance, so there aren't any unnecessary. Therefore this package can be hardly seen as a port of the Scala Library to JavaScript. So when a programmer uses a class from the standard library and isn't sure, whether it's supported in the runtime or not, then he should examine the `s2js.runtime.client.scala` package. If it's not there, then there is only way to solve this - he should port the class (or method) by himself.

##### Package s2js.runtime.client.rpc

Rather than describing classes in this package one by one, an example RPC call will be examined:

1. From the runtime point of view, it starts with a call on the `Wrapper` object like this: `s2js.runtime.client.rpc.Wrapper.callSync('remote.foo', [123], ['scala.Int']);`
2. The `Wrapper` processes the parameters and creates a `XmlHttpRequest`.
3. The request body is filled with the method name and parameters and the request is sent to the [RPC controller](#server) on the server.
4. The RPC controller processes the request and returns the result serialized using [scala2json](#scala2json).
5. If an error occurs, an `RpcException` is thrown. Otherwise the result is deserialized with the `Deserializer`:
	1. The JSON string is transformed to an object using the `eval` function.
	2. The object is traversed using the `ClassNameRetriever` subclass of the `JSONTraverser`. So classes of all instances in the result are known.
	3. All the classes that aren't yet loaded in the class loader are retrieved from the server via an RPC call to the `s2js.runtime.shared.DependencyProvider` object.
	4. The classes are declared using the `eval` function.
	5. By now, all the classes that are used in the resulting object, have already been loaded. The resulting object is traversed again using the `Deserializer`, which instantiates classes corresponding to the objects in the result and copies field values from the result to the newly created instances. If the field value is a reference to an object (`__ref__`), the reference is tracked as a `ReferenceToResolve`, because the reference target may have not been instantiated yet.
	6. By now, all the objects from the result have been instantiated, so the references are resolved.
6. If the deserialized object is an instance of the `Throwable` trait, then it's thrown (or passed to the `failCallback` in case of an asynchronous RPC call). Otherwise the result is returned (or passed to the `successCallback`).

<a name="runtime-shared"></a>
#### Package s2js.runtime.shared

Currently, there is only one class - the `DependencyProvider`. It provides just one method `get`, which returns a `DependencyPackage` containing the JavaScript source code of symbols specified in the `symbols` parameter and all their dependencies. The sources of `symbolsToIgnore` aren't included in the dependency package. Moreover, the order of symbol source codes is determined by dependencies among them which can be found in the generated dependency file (which is generated during the [`cp`](#cp) SBT task). So for example a class isn't declared before its super-class.

> There is a tight coupling between the `s2js` project and other projects (e.g. hardcoded path to the dependency package in the `DependencyProvider`, RPC controller logic defined in the [`web`](#web) project), so it can't be used as a standalone library/toolchain. However, making the `s2js` standalone wouldn't be much work, it just wasn't our priority to make it completely reusable.

<a name="common"></a>
## Package cz.payola.common

As the name of the project suggests, common classes that can be accessed from all other projects are defined here. And because the project is also compiled into JavaScript, they may be accessed and used even on the client side. The classes and traits were designed with that in mind so they don't provide any sensitive information. The main aim of this project is to reduce code duplication among client-side and server-side projects.

### Package cz.payola.common.entities

This package includes classes representing the basic entities (user, analysis, plugin, etc.) that ensure the core functionality of Payola. Each entity has its own ID (string-based, 128-bit UUID) and can be stored in a relational database (see the [data package](#data) for more information).

![Common entites model](https://raw.github.com/payola/Payola/master/docs/img/common_entities.png)

This image captures the most important classes of the `entities` package. The `User` entity stands in the middle of everything - a user can own groups (and be their member as well), plugins, analyses, data sources and ontology customizations. Each `OntologyCustomization` instance consists of several `ClassCustomization`s, each consisting of `PropertyCustomization`s. A `DataSource` is simply a special subclass of `PluginInstance` which fetches data. Then there's the `Plugin` class where it starts to be slightly more complicated.

The `Plugin` class represents the plugin itself with all the logic. Each `Plugin` may have some `Parameter`s which define which values the plugin requires on the input. For example, the `Typed` plugin which comes pre-installed with Payola has one `Parameter` named `RDF Type URI`. When a `Plugin` is to be evaluated, it receives a corresponding `PluginInstance` and a sequence of `Graph`s as its input.

A `PluginInstance` is a container for `ParameterValue`s: a `ParameterValue` contains the concrete value for that particular `Parameter` (a string, numeric value, ...). Hence when a `Plugin` is being evaluated, it queries the `PluginInstance` for all required parameter values.

An `Analysis` forms various plugin instances into a tree-like structure using `PluginInstanceBinding`s. A `PluginInstanceBinding` can be viewed on as an edge in the resulting tree structure (`PluginInstance`s being vertices). When an `Analysis` is run, each `PluginInstance` is evaluated by its peer `Plugin`. The evaluation process begins at the leaf vertices and forms a chain taking output of one or more plugins and passing it to the next plugin as input. Because a valid `Analysis` forms a tree, the output is just one `Graph`. For more information about the analysis evaluation process see the [domain section](#domain).

#### Package cz.payola.common.entities.privileges

![Privilege model](https://raw.github.com/payola/Payola/master/docs/img/common_privileges.png)

To share entities between users, privileges are used. This makes it easy to extend the model in the future, or to change the granularity of privilege granting. Currently, there are only privileges granting access to a resource - analysis, data source, ontology customization and plugin; however, a privilege type that grants a user the right to edit some entity, for instance, can be easily added.

As can be seen in the picture above, each entity that needs to be shared, has to have the `ShareableEntity` trait mixed in which adds a `isPublic` field to the object (denoting whether the entity may be seen by everyone or just those you share it to).

Each `PrivilegeableEntity` has a collection of privileges. A `Privilege` is a simple class containing three fields: the granter (i.e. the user who issued this privilege), the grantee (a `PrivilegeableEntity` that the privilege is issued to) and an object of the privilege (e.g. an analysis).

For each class that can be currently shared (`Analysis`, `DataSource`, `OntologyCustomization` and `Plugin`), a corresponding `Privilege` subclass exists. But this model can be obviously very easily extended to other classes.

#### Package cz.payola.common.entities.settings

The settings package encapsulates ontology customizations (used on the client side to change display settings of a graph using ontologies).

<a name="rdf-common"></a>
### Package cz.payola.common.rdf

This package contains classes representing RDF graphs and ontologies. Only core functionality is included in this package - class declarations to represent the data, some basic methods that are used on the client. More functionality, such as converting an RDF/XML file to a `Graph` object is added in the [`domain`](#domain) project.

<a name="domain"></a>
## Package cz.payola.domain

The `domain` project builds upon the [`common`](#common) project, inheriting from classes and traits in the `common` project. Additional functionality and logic is hence added as well as dependencies on other libraries, such as [Jena](http://jena.apache.org) for parsing RDF/XML files into Graph objects.

The idea behind the `domain` project is for it to be fully independent on other projects within Payola (other than `common`) - you can take the domain project and use it in a different project without any modification.

### Package cz.payola.domain.entities

Domain entities extend the `common` entities that are mostly traits and fully functional classes are formed - as with the whole package, you can take the `domain.entities` package and use it completely elsewhere - outside of Payola without any modification.

### Package cz.payola.domain.entities.plugins

One of the crucial features is to allow the users to create their own plugins and use them in their analyses later. That's what the subpackage `compiler` is for. There are also some predefined plugins in the subpackage `concrete`.

#### Package cz.payola.domain.entities.plugins.compiler

The `PluginCompiler` is a wrapper of the Scala compiler, that is tweaked to support compilation of analytical plugins. It provides just one method `compile` that, given the plugin source code, compiles the plugin and returns information about the compiled plugin in a `PluginInfo` instance.

The internal Scala compiler, that actually performs the compilation is plugged with the `PluginVerifier` [Scala compiler plugin](http://www.scala-lang.org/node/140). So the compilation process consists of the standard Scala compiler phases and two more additional phases that are defined in the `PluginVerifier`:

1. Verification phase which checks whether the compilation unit contains a package with one subclass of the `Plugin` class, whether the plugin class has both the parameterless constructor and the setter constructor and whether the constructors have proper parameter types. If any of these assumptions is broken, an exception is thrown.
2. Name transformer phase that changes name of the plugin class to the `Plugin_[randomUUID]`. This way plugin class name uniqueness is ensured and it's not a problem when two users upload plugins with the same name.

The last piece in the puzzle is the `cz.payola.domain.entities.plugins.PluginClassLoader` which has to be used when instantiating plugin classes. The method `instantiatePlugin` on the `PluginClassLoader` should be used instead of standard means of reflection instantiation.

#### Package cz.payola.domain.entities.plugins.concrete

There are basically two types of plugins - the data fetchers and the rest. A `DataFetcher` doesn't have any inputs and is capable of SPARQL query execution and node neighborhood fetching, so that it can be used as a plugin of a data source. Implementation of the plugins is quite straightforward, please refer to the user guide on what they do.

As of version 1.1, the DataFetcher plugin was changed to be a ConstructQuery in order to allow some optimizations.

#### DataCube

Also, a new plugin, the `DataCube` was introduced. For every Data Cube Data Structure definition, a new plugin attached to the codebase is created. That is because a variable count of parameters (dimensions, attributes, measures). The evaluation is a combination of a SPARQL query and appending a custom RDF graph. It executes a special query in order to map the input graph to comply with the DCV DSD and appends the DSD.

#### VirtuosoSecuredEndpointFetcher

Based on the [Apache HTTP Client library](http://hc.apache.org/httpclient-3.x/) the plugin provides a way of accessing a secured endpoint ([HTTP Digest Auth](http://www.ietf.org/rfc/rfc2617.txt) is supported). Since this is a Virtuoso-specific feature, it is named as a Virtuoso endpoint fetcher, but it is possible that it would work also with other endpoints.

### Package cz.payola.domain.entities.analyses

The second big part of the domain package are analyses and the whole process of their validation, optimization and evaluation. An `Analysis` is composed of `PluginInstance`s and `PluginInstanceBindng`s, which specify connections between `PluginInstance`s. Analyses in the system don't necessarily have to be always valid (in order to support interrupted analysis creation).

### Package cz.payola.domain.entities.analyses.evaluation

Evaluation of an analysis isn't a trivial process, because it introduces parallelism. Everything starts with the `evaluate` method call on an `Analysis` instance which creates a new instance of the `AnalysisEvaluation` and starts it. From now on, the evaluation runs in parallel to the thread which started the evaluation, because it's an [`scala.actors.Actor`](http://www.scala-lang.org/node/242). The `AnalysisEvaluation` object can however be queried about its current state (using methods `getProgress`, `getResult`, `isFinished`). An `AnalysisEvaluation` works in the following steps:

0. The analysis is checked for the presence of an instance derived from the `cz.payola.domain.entities.plugins.concrete.Analysis` plugin. If present, it is expanded with a corresponding plugins of an inner analysis.
1. Validity of the analysis is checked (whether it's not empty, whether there are any invalid bindings, whether the analysis has one output etc.).
2. The analysis is optimized using the `AnalysisOptimizer`. It consists of phases, that usually merge plugin instances together. The phases that are sequentially executed and produce an `OptimizedAnalysis`. For more information about the phases and what they do, please refer to the API documentation of the `cz.payola.domain.entities.analyses.optimization` package.
3. The `Timer` actor is started, so the evaluation is notified in case of timeout.
4. For each `PluginInstance`, an `PluginInstanceEvaluation` actor is created and started. Within the constructor, it receives a method `outputProcessor`, that should be used to return the result. The `PluginInstanceEvaluation` does the following:
	1. Wait until all input graphs are received (if the plugin has no inputs, immediately proceed to the next step).
	2. Evaluate the plugin.
	3. Return the result using the `outputProcessor`. If output of the plugin instance is bound to another plugin instance, it actually sends a message with the result to the `PluginInstanceEvaluation` actor corresponding to the plugin instance. Otherwise, if the plugin instance is the last one, the result is sent to the `AnalysisEvaluation` actor.
5. Wait until the message with result is received from the output plugin instance. During that time, process messages that report plugin instance evaluation progress or errors. Also reply to messages that query for the current state of evaluation.
6. Process the `AnalysisEvaluationControl` messages.

An example of the analysis evaluation process, that succeeds without any errors can found on the following picture. The messages 1, 1.1 and 1.2 can be interleaved with messages 2, 2.1, 2.2, 3, 3.1, 3.2, 3.3, because these two branches of the evaluation can run in parallel.

![Analysis evaluation using actors](https://raw.github.com/payola/Payola/master/docs/img/analysis_evaluation_actors.png)

#### Inner analyses

The Analysis plugin (with its codebase at `cz.payola.domain.entities.plugins.concrete.AnalysisPlugin`) has no implementation at all. Moreover, it rises an exception, if executed. That is, because it needs to be substituted by instances of the inner analysis by invoking `anaylsis.expand()` on the evaluated analysis. The plugin is used only to hold a list of parameters (analysis parameters). Those, separated by the character `$` carries in the name field the name of the plugin and a reference to the plugin instance parameter value that should be subsitited.

The analysis is not persisted after the expansion. The expansion is done in-memory only and that should not take a long time. We also check for recursion and try to avoid it.

### Package cz.payola.domain.rdf

The `common` Graph class is enriched by the ability to execute SPARQL queries as well as to convert the Graph object to an RDF/XML or TTL textual representation. A companion object is present as well capable of creating Graph objects from an RDF/XML or TTL representation. To perform these tasks, [Jena](http://jena.apache.org) library is used.

### Package cz.payola.domain.rdf.ontology

Just like with the `cz.payola.domain.rdf` package, the ontology package adds the ability to create Ontology objects from OWL or RDFS formats. No reverse conversion from Ontology object to textual representation is supported at this moment, however, as this functionality isn't required by Payola.

### Package cz.payola.domain.sparql

Because the [SPARQL](http://www.w3.org/TR/rdf-sparql-query/) is used throughout the application (mainly in plugins) and queries are often programmatically constructed, it's been decided to use object oriented query abstraction instead of string concatenation. Therefore, there are classes that somehow correspond to rules in the [SPARQL query grammar](http://www.w3.org/TR/rdf-sparql-query/#grammar). A query is built by composition of these classes; to obtain it's string representation, the `toString` method can be called on the `ConstructQuery` instance. As a benefit of this representation, `GraphPattern`s can be easily merged together, which wouldn't be that trivial if they were represented as strings.

> The only supported query type is the CONSTRUCT, because other types weren't needed. However it's easy to implement them in a similar fashion.

<a name="data"></a>
## Package cz.payola.data

This whole package represents the data layer. Trait `DataContextComponent` defines an API for communication between the data layer and Payola [model](#model) component. The two vital tasks of the data layer are:

- to store and fetch the [domain layer](#domain) entities
- to use the [Virtuoso](http://virtuoso.openlinksw.com/) server as a private RDF data storage

Architecture of Payola implies that the domain layer is independent on the data layer and since Payola is an open-source project, the data layer can be replaced by another implementation (implementing data layer API) that fits different platform-specific needs.

### Package cz.payola.data.squeryl

In this version of Payola, [Squeryl](http://squeryl.org) (an ORM for Scala) is used for persisting entities into an H2 database. Squeryl generates a database schema from the structure of objects to be stored. Every entity class is persisted in its own table, definition of this table is derived from the entity's object structure. In order to have the domain layer independent on the data layer, were implemented [entities](#squeryl-entities) that:

- represent entities from the domain layer and 
- can be stored/loaded via Squeryl ORM into/from the database

<a name="schema-component"></a>
The relational database schema definition locates in `SchemaComponent` trait that uses `org.squeryl.Schema` object to define:

- session with connection to the database
- table for every entity class to be persisted
	- the table structure is based on entity public fields
	- constraints (such as PrimaryKey, Unique constraint) or column types of some fields are defined manually to match the Payola project requirements exactly
- foreign-key constraints for relations between entities
	- including the reaction on removing related entity
- factory for every persisted entity
	- the factory serves as a constructor for fetched entity
- `persist`, `associate` and `dissociate` methods


##### Why Squeryl?

Squeryl is an existing, tested, functional and easy to use ORM for Scala applications that had met the needs of Payola during the process of making a decision whether to use an existing ORM or implement our own ORM tool.

<a name="about-squeryl"></a>
##### Briefly about Squeryl

[Squeryl](http://squeryl.org) is a free ORM tool for Scala projects, it can use any relational database supported by JDBC drivers.

A database structure needs to be defined in an object extending the `org.squeryl.Schema` object. This object contains a table definition - definition that says which entity is persisted in which table. Squeryl allows to:

- redefine column types of tables
- declare 1:N and M:N relations between entities
- define foreign key constraints for those relations.

Squeryl provides lazy fetching of entities from "N" side of 1:N or M:N relations, which is a desirable feature of an ORM tool. The query that fetches the entities of a relation is defined in a lazy field of the related entity, on the first data request, the query is evaluated. There is an `associate` method in Squeryl for creating a relation between entities. Simplified code may look like this:

<a name="squeryl-code-examle"></a>
```scala
class Analysis extends cz.payola.domain.entities.Analysis {

	// Definition of a lazy field with a query fetching plugin instances of this analysis
	// The relation between analyses and plugin instances is defined in SchemaComponent.pluginInstancesOfAnalyises
	private lazy val _pluginInstancesQuery = schema.pluginInstancesOfAnalyises.left(this)

    // Returns plugin instances of this analysis
    override def pluginInstances = {
        inTransaction {
            _pluginInstancesQuery.toList
        }
    }

	// Creates a relation between the analysis and given plugin instance
	override def addPluginInstance(instance: PluginInstance) {
		inTransaction {
			_pluginInstancesQuery.associate(instance)
		}	

		super.addPluginInstance(instance)
	}	
}
```

Every query that fetches any data from the database needs to be wrapped inside a transaction block (as can be seen in the previous sample code). Squeryl provides two ways to wrap code into a transaction - `transaction { ... }` and `inTransaction { ... }` blocks. Every `transaction` block creates a new transaction which establishes a new database connection, whereas `inTransaction` block nests transactions together. If there is no parent transaction, the top level `inTransaction` block behaves as a `transaction` block.

<a name="squeryl-entities"></a>
#### Package cz.payola.data.squeryl.entities

All higher layers of Payola work with [domain layer](#domain) entities, but those entities aren't persisted in data layer directly. For every domain layer entity class that should be persisted, a class in the data layer that provides database persistence to the corresponding domain layer entity exists, as is shown in the following picture:

![Data layer entities](https://raw.github.com/payola/Payola/master/docs/img/data_entities.png) 

Every entity in this package extends the trait `Entity`, which provides Squeryl functionality. It could be compared to [Adapter](#http://en.wikipedia.org/wiki/Adapter_pattern) design pattern, where `Entity` from [`common`](#common) package is the Adaptee, `Entity` in this package is the Adapter, and Squeryl functionality is the Target.

Data layer entities extend the represented domain layer entities (with two exceptions that will be explained later - in the picture above they are displayed with dashed arrows), which allows to treat data layer entities like domain layer entities. There is no additional business logic in the data layer entities - their only purpose is to store/load domain layer entities into/from the database. In order to ensure the proper persistence, data entities had duplicated some protected fields of domain layer entities.

In order to persist a domain layer entity, the entity must be converted to a data layer entity. The conversion is performed by its companion object (extending `EntityConverter`). Every data layer entity has its own converter. When the conversion fails, a `DataException` is thrown. Since every data layer entity extends a domain layer entity, there is no need for reverse conversion. Data layer returns data layer entities, which can be handled as domain layer entities in higher application layers.

The two mentioned exceptions are `PluginDbRepresentation` and `PrivilegeDbRepresentation`. These data layer entities do not extend `Plugin` and `Privilege` from the domain layer, because the real plugins and privileges may be added at the runtime (even by a user). These domain layer entities are just abstract parents of the real plugins and privileges, so that they are simply wrapped into data layer entities. The data layer entities are persisted and the domain layer entities are reconstructed from them via the Java reflection API.

Domain layer entities allow adding other entities into collections (e.g. a plugin instance can be added to an analysis via an `analysis.addPluginInstance(pluginInstance)` statement). The data layer entities override this behavior by adding a code to persist this relation into the database and leaving the domain layer behavior unchanged (as shown in this [example](#squeryl-code-examle)). 

<a name="squeryl-repositories"></a>
#### Package cz.payola.data.squeryl.repositories

Repositories provide entity persistence and fetching (entities must extend `Entity` trait in the [squeryl](#squeryl) package. The API for repositories is defined in the `DataContextComponent` trait in the [data](#data) package. The API is a set of traits, their structure is shown in the next picture:

![Data layer repositories](https://raw.github.com/payola/Payola/master/docs/img/data_repositories.png)

For every repository defined in the API a repository component that implements the repository exists. Traits `Repository`, `NamedEntityRepository`, `OptionallyOwnedEntityRepository`, `ShareableEntityRepository` are implemented within the `TableRepositoryComponent` trait. The rest of the API repositories are implemented by the corresponding repository components (e.g. `UserRepository` is implemented by `UserRepositorComponent`).

##### Eager vs Lazy loading
Squeryl provides only lazy fetching from the database. The lazy fetching is used when loading users or plugin instance bindings from the database. These entities are loaded in the most simple fashion: 

- plugin instance bindings are loaded only with IDs of the target and source plugin instances, those plugin instances are loaded only when needed
- users are loaded with unevaluated queries for related items, all their groups, privileges, analyses, plugins, data sources are loaded only when needed

The rest of entities is loaded eagerly (i.e. entity is loaded with some of its relations evaluated). Eager-loading is provided by the `TableRepository` abstract class. Every repository component that extends `TableRepository` must implement `getSelectQuery` and `processSelectResults` methods. `getSelectQuery` method defines a query to load the entity (and all related entities) from the database, `processSelectResults` method evaluates the defined query result and returns loaded entities. Entities are loaded by their repositories in the following way:

- groups are loaded only with their owner
- privileges are loaded in their `PrivilegeDbRepresentation` form, the granter, grantee and object are lazy-loaded from the database, finally the whole privilege is instantiated using the Java reflection API
- plugins are loaded in the their `PluginDbRepresentation` form with parameters and owner and then they are instantiated using the Java reflection API
- plugin instances are loaded with parameter values and with related plugins and parameters
- data sources are loaded with the owner and parameter values and with related domain layer `DataFetcher` plugin with its parameters
- analyses are loaded only with their owner
	- when there is an access to plugin instances or to plugin instance bindings, the complete analysis is loaded (i.e. no further fetching-query to database will be needed)

It is crucial to mention that two queries loading entity from repository by the same id (`getById()` method) results in two different objects representing the same entity. That is why the standard `equals` method needed to be overridden - the two entities are equal when they have the same ID (since ID is an UUID, which is unique through the whole database, the override `equals` method is valid).

<a name="virtuoso"></a>
### Package cz.payola.data.virtuoso

Virtuoso is used for storing private RDF data of a user - classes in this package let you communicate with a Virtuoso instance and perform some tasks - create a graph group, store a graph in the graph group, and then retrieve all graphs within a graph group.

Some of these tasks are performed at Virtuoso's SPARQL endpoint which is as simple as posting a regular HTTP request, but some require a connection to its SQL database, for which a `virtuoso.jdbc3.Driver` driver is required. This driver is included in the `lib` directory of Payola project.

<a name="model"></a>
## Package cz.payola.model

The classes in this package build up a wrapper which encapsulates all the business and data access logic. The goal of the code in this package is to decouple any presentation layer from the application logic and data access. In fact, all of the existing presentation layers (web application controllers and RPC remote objects) are built on top of this package.

It is crucial to mention, that the model package does not make up the whole model. The model is spread into more packages, the domain, data, and common. All of those packages provide some functionalities and the model package itself uses them all to get specific tasks done.

If you want to understand the following text (and the code) better, please, get familiar with the [Scala Cake pattern for DI](http://jonasboner.com/2008/10/06/real-world-scala-dependency-injection-di/).

The whole model is divided into several components. Each component has its own data subdomain which it wraps up. E.g. `DataSourceModelComponent` for data sources, `UserModelComponent` for users, etc. As you can see, the components bring us an API which makes us able to work with the data stored in the relational DB.

While utilizing the subcomponents the `ModelComponent` trait is built up to provide a single entry point to the model infrastructure. While utilizing the cake pattern, dependencies like the persistence layer, RDF data storage layer and plugin compiler are injected to the model components.

Later on, when you get familiar with the `cz.payola.web.shared` package, you will find out more about an object named `cz.payola.web.shared.Payola`, whicg is an example implementation of the `ModelComponent` trait.

### Interesting features
#### Partial analyses
In order to support the Data Cube Vocabulary query-by-example mechanism (pattern selection),
we had to introduce the mechanism, which creates a sub-analysis from a specified one. It comes from the idea, that when a plugin instance needs a preview, not all the plugins has to be evaluated. Some of them are redundant and its output would not be used. To speed-up the preview proccess and to aquire the ability to obtain a semi-result, we introduce the mechanism. 

The `makePartial` method int the `AnalysisModelComponent` takes the analysis from which the sub-pipeline is extracted as well as the plugin instance. The plugin instance serves as a marker - it tells us, which part of the analysis is needed to be extracted.

![Partial analysis](https://raw.github.com/payola/Payola/master/docs/img/screenshots/dcv_subpipeline.png)

#### Analysis cloning
The `clone` method of the `AnalysisModelComponent` is designed to clone the analysis, if the specified user has access to it. Moreover, the user that triggered the action, is given a token (via cookie), which might be used in the future to take the ownership of the analysis. The token is a random GUID.

In order to support that feature, we had to implement some other methods, which, in the right order clones parameters, compensate changes in plugin instance bindigns and mainly, makes persistance with SQUERYL. Especially the persistance part is very *fragile* and one is not recommended to change it.

<a name="web"></a>
## Package cz.payola.web

![Client-server-structure](https://raw.github.com/payola/Payola/master/docs/img/client_server.png)

All the code connected with the web presentation layer could be found in this package. The web application is, as the rest of the project, written in the Scala language. To make the job easier, we have built up the presentation layer on top of the [Play 2.0 framework](http://www.playframework.org/), which is also completely written in the Scala language, so it was easy to integrate with our application.

Since we have started using the framework in the stage of early access preview, we currently do not take advantage of all the features provided by its API. If you want to know, what can be done better nowadays, just look into the Future work section of the documentation. As an example, one can mention utilizing the Promise API. When fully available (depends on Servlet 3.0 implementation in a wide spectrum of Java web servers), we should also take advantage of the possibility of exporting the web application into a single WAR file which can be deployed into a servlet container.

Since the Play framework is an MVC framework, our web presentation layer built on top of it also uses the MVC pattern. If you look closely into the `cz.payola.web.server` package, you can see a classic code separation in there - controllers and views, model is imported from its own separate package.

Since it improves the usability of the application in a crucial way, a rather high count of functionalities is built on top of the AJAX technology, so the calls from the client side of the application to the server side of the application are realized via XHR requests. Since we knew from the past, that in many applications, the AJAX technology is a weak spot in the application architecture and security (and the code style is often terrible), we have decided to make a standard for our XHR calls and came up with an idea of JavaScript RPC. Inspired by [Google Web Toolkit](https://developers.google.com/web-toolkit/) technology, we wanted to introduce a simple-to-use standard with a minimal overhead for the developer. Also, we wanted the RPC to be as secure as a standard HTTP request via a controller is. Moreover, since the application can be extended in a several ways, we wanted the RPC to be based on a single programming language. This is how we came up with the idea of the s2js compiler which enabled us to do all the things described in this paragraph. If you want to know more about how the RPC works, refer to the appropriate section.

Since the web application is not based on a monolithic architecture, we have divided the code into several packages - client, initializer, server and shared. The initializer subproject is responsible for initialization of the databases used by the web application. The rest builds up the web application itself. The server package contains the code which runs on the server side, the client package contains the code running on the client side. The code in the shared package may be run on both the client side and the server side. Also *remote objects* should be placed into this package, since they run on server and can be called from the client side.

###RPC

The code separation is also one of the restrictions of our RPC (with benefits). You need to separate code which is executed on the client side from the code which is executed on the server side. But you can make a call from the client side to the server side as there is no such thing as client-server architecture.

####Remote object example

How to write a remote object? See the following commented example:

```scala
package cz.payola.web.shared

import s2js.compiler.async
import s2js.compiler.remote
import cz.payola.domain.rdf.Graph
import cz.payola.domain.entities.User

@remote
object RPCTester
{

    def testParamArray(param: List[Int]): Int = {
        param.sum
    }

    @async
    def testParamArrayAsync(param: List[Int])(successCallback: (Int => Unit))(failCallback: (Throwable => Unit)) = {
        successCallback(param.sum)
    }
    
    @secured def secureAdd(first: Int, second: Int, user: User = null): Int = {
    	first + second
    }
    
    @secured def maybeSecureAdd(first: Int, second: Int, user: Option[User] = None): Int = {
    	first + second
    }
}

```
    
In thix example, you can see how to define a remote object. There are just a few things you need to know before writing your first remote object. Since it is called an `object`, you really need to define it as an `object`, not as a `class`. This is very important. Since objects behave as singletons in a certain point of view, they are created automatically and have only one "instance", it is much easier for the RPC to work with them. It prevents the RPC from a big overhead while working with classes and instances. That's why classes are not supported, so, please, do not use them.

<a name="sync-async"></a>
#####Synchronous vs. asynchronous remote methods

You also need to annotate the whole object with the `@remote` annotation. Only with the proper annotation, the object gets available to the client side code and may be invoked by the RPC Dispatcher.

While speaking about XHR requests, we define two categories:

- synchronous
- asynchronous

A synchronous call should be used in a very few use cases. Such a call completely blocks the UI on the current page in the browser. That means, that user cannot do a thing until the request gets completed. That is not advised.

An asynchronous call gets processed in the background and fires a callback when completed. This is not conflicting with the UI in the browser. But there are moments when you need to make a call which should appear to the user as a synchronous one. You are strongly recommended to invoke an asynchronous call and block the UI somehow in a user-friendly way.

> You can use the **blockPage** method of the **View** class.

If you make a synchronous call, e.g. the method `testParamArray`, the code on the client side will look as follows:

```scala
val sum = RPCTester.testParamArray(List(1,2,3))
doSomethingWithSum(sum)
```

Which is quite a standard fragment of code. That differs a lot from the asynchronous variant:

```scala
RPCTester.testParamArray(List(1,2,3)) { sum =>
	doSomethingWithSum(sum)
}(errorHandler(_))
```

> Thanks to Scala, the syntax is more simple than the same fragment written in JavaScript. In fact, what is utilized here is multiple parameter list currying.

You should keep in mind that the asynchronous call invocation returns immediately. So the code that should be executed after the call gets completed has to be invoked in the success callback.

Let's discuss an example of an asynchronous method definition:

```scala
@async def testParamArrayAsync(param: List[Int])
    (successCallback: (Int => Unit))
    (failCallback: (Throwable => Unit)) {
        successCallback(param.sum)
    }
```
First of all, you need to specify the `@async` annotation. It makes the compiler compile the method to JavaScript correctly. After doing that, there are three mandatory parameters lists you need to define:

- The first one is the parameter list which contains parameters you need to work with in the body of the method.
- The second one defines the type of the success callback. When you invoke the `successCallback`, its parameter gets serialized into JSON and it is sent as a response to the client. On the client, the parameter is deserialized and passed as a parameter of the `successCallback` you specified while writing the client code. Basically, if you return a String, the type would be `(String => _)`, if you return a graph, it would be `(Graph => _)`.
- The last one is the failCallback definition. Its type is always `(Throwable => Unit)` and works very similar to the successCallback.



#####Security
The RPC mechanism provides an API to make you able to secure the RPC calls. This means that you can (while utilizing the Play! security) get an instance which represents the authenticated user who invoked the RPC call. 

To enable this, you need to annotate your remote method with the `@secured` annotation. If all the methods in a remote object should be secured, you can annotate just the object itself. If a method is annotated with the secured annotation, it is expected to have one more parameter of one of the following types:

- `Option[User]`(with default value scala.None)
- `User` (with default value null)

> The User class is from the package cz.payola.domain.entities
> 
> **The parameter needs to be always the last one!**

Now, you probably know, which one use in which use case. But here are some scenarios:

- User has to be logged in, we need to authenticate the RPC call and verify access to the requested resources. We use `User`. We get instance of the logged in User. If no user is logged in, **the method will never get executed**.
- User might be logged in, the response depends on which user is logged in (while no user means e.g. a guest). We use `Option[User]`, we get `Some(User)` if the user is logged in, we get `None` if no user is logged in.

You are also probably getting an idea why the default parameters are needed. If you do not define a default value, the parameter becomes mandatory. That means, you need to specify the parameter when invoking the method. It would be strongly uncomfortable to pass the logged in user on client side for each RPC call. Moreover, the parameter gets overridden by the request binder on the server whether you specify it on client side or not. If it has a default value, you don't need to specify it at all.

<a name="how_it_works"></a>
#####How it works

> If you look closely into the RPC Dispatcher source code, you will find out, that it heavily uses the standard Java reflection to invoke the specified method on the specified remote object. While objects compile in a very specific way, you will find fragments of code which might seem like some kind of magic. Especially the following one:

```scala
val runnableObj = clazz.getField("MODULE$").get(objectName)
``` 

> In fact, that is the only way, how to get the reference to the object during runtime. It is placed under a special field `MODULE$` of the `Class` instance of the class which you made a companion object of.


What happens if a presenter on client side calls a method of a remote object? Learn more from the following diagram:

![Synchronous RPC call](https://raw.github.com/payola/Payola/master/docs/img/rpc_call_sync.png)

When you call a remote method from a presenter on the client side, you in fact trigger an XHR request to the server. The request gets parsed by the RPC controller and delegated to the RPC Dispatcher. The dispatcher extracts parameters for the remote method, transforms them into the right data types and gets the method which should be invoked via reflection. While this is being done, it checks, if the method's object has the `@remote` annotation. After that, the `@secured` annotation presence is checked. If present on the method or its object, authorization takes place. If all goes well, the remote method gets executed and the result is returned. By default every XHR request is synchronous, asynchronous call can be triggered using `@async` annotation, more [here](#sync-async). The diagram above shows synchronous call.

Since we use Scala Actors to make an asynchronous call synchronous in the RPC controller (we need to send the result as a response to the same request which activated the call), you can use whatever you need to use on the server side, including threads. Just do not forget to call one of the `successCallback` or `failCallback` when you are done. If no callback is invoked during the lifetime of a RPC call, an exception is thrown (and serialized to the client where it gets rethrown).

<a name="initializer"></a>
### Package cz.payola.web.initializer

This project should be run only during installation as described [here](#run-initializer). 

Created database contains:

- a user with login name "admin@payola.cz" and password "payola!"
- a public analysis owned by this user
- two public data sources owned by this user
- a public ontology customization for [Public contracts](http://opendata.cz/pco/public-contracts.xml) ontology
- a set of pre-implemented plugins with their parameters

<a name="shared"></a>
### Package cz.payola.web.shared

As described before, in this package, you can find the code which could be executed on both the client side and the server side, or the code which is executed on the server side, but called from the client side. The so-called *remote objects* are further described in the section about s2js compiler. One can see the remote objects as controllers for the RPC calls. You just define an action in the remote object and call it from the client side.

The most important object in this package is the `Payola` object. It is an instance of the previously mentioned `ModelComponent` trait. It serves as an entry point to the whole model of the application. Since it is not a remote object, you cannot use it on the client side, but it is heavily used by the remote objects on the server side. Also, controllers from the package `cz.payola.web.server` use the `Payola` object to gain access to the data and business logic of the application. Since it is a `Scala object`, it behaves as a Java singleton in the right point of view - exactly one instance exists in the whole application and you don't need to create it, it is given to you for free by the Scala runtime. If it reminds you of something, you are kind of right, it is very similar to a classic DI container (but affected a lot with the `Scala cake pattern for DI`).

<a name="server"></a>
### Package cz.payola.web.server

The code in this package is built on top of the MVC API of the Play 2.0 framework. Since we don't use their standard DAL (Anorm) and since we have a custom model, you will not be able to find a package named model anywhere in this package.

What you can find is a directory named app which contains controllers and views of the web application. They are standard controllers and scala templates as introduced in the [Play 2.0 framework docs](http://www.playframework.org/documentation/2.0.2/Home).

There are two special controllers you should know more about:

- PayolaController, which is the base controller of all the others, since it contains code which is more or less used by all the controllers
- RPC, which is the controller that processes XHR calls from the client side

Since the RPC controller is not a simple thing, we will discuss the controller a bit more. When the RPC call arrives from the client side, it holds the information about which remote method should be invoked on the server and, more important, invocation parameters. Example of such a call follows:

```
Request URL:http://localhost:9000/RPC/async
Request Method:POST
Status Code:200 OK
Request Headersview source

Accept:*/*
Accept-Charset:ISO-8859-1,utf-8;q=0.7,*;q=0.3
Accept-Encoding:gzip,deflate,sdch
Accept-Language:en-US,en;q=0.8,cs;q=0.6
Cache-Control:no-cache
Connection:keep-alive
Content-Length:133
Content-Type:application/x-www-form-urlencoded
Cookie:__utma=1.992379703.1320156835.1320156835.1320156835.1; COOKIE_SUPPORT=true; LOGIN=74657374406c6966657261792e636f6d; SCREEN_NAME=6c4878796f336d694e4832672f39694238436f4a71773d3d; GUEST_LANGUAGE_ID=en_US
Host:localhost:9000
Origin:http://localhost:9000
Pragma:no-cache
Referer:http://localhost:9000/analysis/4d1c0607-3652-4ad4-9628-a643bfba58b7
User-Agent:Mozilla/5.0 (Macintosh; Intel Mac OS X 10_8_0) AppleWebKit/537.1 (KHTML, like Gecko) Chrome/21.0.1180.79 Safari/537.1
Form Dataview URL encoded
method:cz.payola.web.shared.DomainData.getAnalysisById
paramTypes:["java.lang.String"]
0:4d1c0607-3652-4ad4-9628-a643bfba58b7
Response Headersview source
Content-Length:11464
Content-Type:text/plain; charset=utf-8
```

As you can see, the `POST` request, which represents an asynchronous RPC call, contains the name of the remote method which should be invoked by the RPC controller (which delegates most of the work to a class called `RPCDispatcher`). Since one can make his own RPC call very simply, we have restricted the "invokable" methods to those that are defined in the body of an object annotated with the `s2js.compiler.remote` annotation. If you try to invoke a method which does not belong to a remote object, an exception will be thrown and sent as a response to such a call.

> One could ask now, why haven't we used the standard Scala @remote annotation, or, moreover, why haven't we used a Java annotation. Since Scala annotations [are not visible during runtime](http://stackoverflow.com/questions/5177010/how-to-create-annotations-and-get-them-in-scala), we were forced to use a Java annotation with a special retention policy to get this done.

```java
package s2js.compiler;
import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface remote
{

}
```

> As you can see, we need to explicitly set the RetentionPolicy to `RUNTIME` to make annotations visible for the RPC Dispatcher.


> On the second hand, this is more secure. We protect the standard Scala @remote objects from being executed.

You can also notice the `paramTypes` field, which holds a JSON-serialized array of types of invocation parameters. This helps the RPC Dispatcher to parse the parameters and make them properly typed on the server. In this example, the `paramTypes:["java.lang.String"]` means that the first parameter will be parsed as String, which is the most simple case.

If you look closely, you can see the `0:4d1c0607-3652-4ad4-9628-a643bfba58b7` fragment in the request body. That means that the first parameter of the invoked method will be `4d1c0607-3652-4ad4-9628-a643bfba58b7`.

Since the RPC Dispatcher needs to parse the parameters and make them typed properly, it is the dispatcher who constraints the RPC and defines the types that could be sent from client to the server. Since complex type deserialization is not an easy task and we did not need it as much as we needed to make other things working, only the following types are supported right now:

- scala.collection (types derived from sequences, more specifically, sequences of the types described below)
- Boolean
- Int
- Long
- String
- Char
- Float

And Java equivalents.

If you want to learn more about the RPC mechanism on the client-side, please, see the corresponding section in the [text about s2js.rpc package](#s2js_rpc).

<a name="client"></a>
### Package cz.payola.web.client

This package contains all the code which is converted into JavaScript using the `s2js` compiler. Since the architectonic style of the client side application is [Model-View-Presenter](http://martinfowler.com/eaaDev/ModelViewPresenter.html), one can find models, views and presenters here. In order to be able to use HTML DOM Elements in the Scala code, classes in this package heavily use a series of adapters, which work as wrappers of the original classes described in the [MDN](https://developer.mozilla.org/en-US/docs/DOM). See the package `s2js.adapters` to learn more.

To avoid using jQuery as much as possible (using jQuery often leads to writing spaghetti code), we also needed to come up with a way of wrapping browser events and propagating them into presenters. In order to achieve this, we've built up a completely new system of events. Its design is based on events as known from the C# programming language, flavored by the benefits of the Scala language. Learn more in the section dedicated to the `cz.payola.web.client.events` package.

Many of the views in the corresponding subpackage describe all the implemented types of visualization. All the views used to render the user interface are based on the `View` trait, you should definitely get familiar with it, as well as with the derived `ComposedView` trait.

To bring better browser compatibility and basic responsive web support, we've decided to use [Twitter Bootstrap](http://twitter.github.com/bootstrap), a great front end framework, which is a collection of CSS and JS code. It comes with basic support of CSS grids. Moreover, it comes with a bunch of prepared components like buttons, menus, tabs, progress bars and more. In order to use those components comfortably from the Scala client code, we've introduced some additional views. You can find them in the `cz.payola.web.client.views.bootstrap` package.

<a name="events"></a>
#### Package cz.payola.web.client.events

The whole event processing system for the client-side code resides in this package. As stated before, we have introduced here a completely new (but probably not unique) system of event processing. Let us walk you through our decision process to get the idea why we did what we did.

All starts with the following code fragment:

```js
element.addEventListener('click',function () {
	this.style.backgroundColor = '#cc0000'
},false)
```

This is a typical example of registering an event listener in JavaScript. It has many disadvantages we don't like very much:
- it is not strongly typed as the whole JavaScript language is dynamic
- it is very easy to mistype the name of the event
- a very few people do know, what the last boolean parameter does - [do you?](http://www.quirksmode.org/js/events_advanced.html)
- preventing the triggered event from bubbling is a little bit more complicated
- one is not able to aggregate the result of all the event listeners
- it is not Scala

As we told you, we've based our system on what you can know from the world of C# programming language. So, let's present a short example of events from C#:

```csharp
public Form1()
{
    InitializeComponent();
    // Use a lambda expression to define an event handler.
    this.Click += (s,e) => { MessageBox.Show(
       ((MouseEventArgs)e).Location.ToString());};
}
```
Example taken from [http://msdn.microsoft.com/en-us/library/ms366768.aspx](http://msdn.microsoft.com/en-us/library/ms366768.aspx).

That is much better, we especially love the `+=` operator usage. That is basically the syntax we will use:

```scala
div.mouseMoved += { e =>
    instance.addCssClass("highlight")
    false
}
```

We can fully utilize the closure mechanism of the Scala language. Since the `div.mouseMoved` has a declared type, we don't need to repeat ourselves, the Scala compiler will know, which type to use. We can just create the handler in-place.

> OK. Nice. But what is the breath-taker?

Look at the implementation of the `cz.payola.web.client.events.Event` class, especially the `trigger` method:

```scala
def trigger(eventArgs: B): C = {
   handlers.map(_(eventArgs)).fold(resultsFolderInitializer)(resultsFolderReducer _)
}
```

On this one line of Scala magic, all the handlers are executed (in the order they have registered) and their results are aggregated by the `resultsFolderInitializer` and `resultsFolderReducer` methods which you define when introducing a new event. The `resultsFolderInitializer` defines, how the fold stack gets initialized. The `resultsFolderReducer` method defines then, how results of the two handlers, or more accurately, how the result of the currently executed handler should be processed. To make it clear, let's see the following example (the `Boolean` event implementation):

```scala
protected def resultsFolderInitializer: Boolean = {
   true
}

protected def resultsFolderReducer(stackTop: Boolean, currentHandlerResult: Boolean): Boolean = {
   stackTop && currentHandlerResult
}
```

We just initialize the stack to `true`, and boolean-and the value of every next event handler. Effectively, if any of handlers returns `false`, the result will be `false`. In other words, the trigger method will return `true` if and only if all the handlers return `true`.

This example shows the structure of the events logic: The Button class (from [elements](#elements) package) represents a button in the generated web page. It serves as a trigger of some operation. Its super class ElementView contains a HTML element (an [adapter](#adapters)) - the button DOM element; and an event handler - a list of functions to be performed, if the button is pressed. In the generated web page with the button, pressing it triggers a DOM element event, which calls a function of the ElementView class. This function triggers all event handlers added to the button's mousePressed event handler (a container of the handler functions).

You are advised to derive all your new events from the `cz.payola.web.client.events.Event` abstract class. It contains the implementation of the `+=` and `-=` operators, so you don't need to reimplement those. There is one more thing you should understand before writing a new event - the type arguments of the Event class. Each event is triggered with an instance of `cz.payola.web.client.events.EventArgs` class which carries at least information on which element was the event triggered.

```scala
class EventArgs[+A](val target: A)
```

Yes, that's it. Let's continue with an example - we will prepare `Clicked` event for a `Div` element. Since you are clicking on the `Div`, which is the event target, you will trigger the event with something like:
```scala
new EventArgs[Div](this)
```
Therefore, the definition of the event will look like this:
```scala
val clicked = new Event[Div, EventArgs[Div], Boolean]
```
Note that the first generic parameter of the event is used as a generic parameter of the EventArgs type (second parameter of the Event). The third parameter is the return type of each of registered handlers.

Since we have already done that, you can just use the prepared classes in the `cz.payola.web.client.event` package.

#### Package cz.payola.web.client.models

The Model object in this package provides communication with the server side of the application. Its routines take care of getting available datasources or getting, creating or editing ontology customizations. All the classes contain methods which call the remote objects with appropriate parameters and return results of such calls.

<a name="view"></a>
#### Package cz.payola.web.client.views

This package contains structures describing the user interface for graph viewing.

The UI is an HTML web page with JavaScript language functions handling its logic. The base of the viewing structure is the trait View. It is an encapsulation of an HTML element with basic render and destroy routines for adding and removing the element to and from the web page. Every section of the web page is based on this trait. An abstract class ElementView is its successor providing more tools for manipulation of the HTML element it represents. It contains event handlers bound to the event listeners of the element, attributes getter and setter and it implements the render and destroy routines. To simulate the HTML document structure ElementView contains a list of its sub-views - structured or basic View object (objects representing basic HTML elements of web page, e.g. Anchor, Div, Span). The render operation calls render of every sub-view recursively, that the rendered result appears is a structured HTML document.

The visualization of a graph is done by visual plugins based on an abstract class PluginView. It is a View and GraphView implementation. The abstract class GraphView defines handling of the visualized graph. The available plugins are textual, graphical and charts.

###### Textual plugins

Base abstract class is TablePluginView. It defines the shown graph updating and generation of a table listing the vertices of the graph and their attribute types and values. There are two textual plugins available. Triple Table shows the graph data structure. Select Result Table shows database select queries results.

###### Chart plugins

The only available chart plugin is the Column Chart, which shows the graph as an enumeration of bars representing the values of vertices. Its structure is described in the class ColumnChartPlugin which extends the PluginView abstract class. For this plugin the graph has to be in certain structure. It must be a tree graph (without cycles) consisting of three levels. The first level is the root of the tree. The second level contains vertices representing the bars of the drawn chart and the third level contains title and value of every vertex in the second level. The values are processed as sizes of bars and titles are labels of bars. Graphs in this structure are accessible by SPARQL query execution tool.

###### Graphical plugins

The graph is visualized as an HTML5 Canvas drawing. The base abstract class is VisualPluginView. It defines the structure of the drawing space - the controls and the multiple Canvas elements used for drawing - and the event handlers - defining mouse and keyboard operations controlling the visualization. It is extended by an abstract class BaseTechnique that defines basic routine for performing initial vertex positioning animation. During the update of the graph, this routine creates a chain of animations. Its implementations CircleTechnique, TreeTechnique and GraphTechnique provide a plugin specific animation.

To visualize a graph structure, a pack of classes based on a trait View is provided (the trait is in sub-package views.graph.visual.graph and is not the same as the base trait for visualization plugins). This trait contains drawing routines - e.g. drawArrow, drawCircle, drawText - that are used to draw the graph visualization to the HTML5 element Canvas and its surface CanvasRenderingContext2D. It provides an abstraction over the basic Canvas draw functions - e.g. arc, lineTo, fillText. The View class is extended by InformationView - a label visualization; VertexView - a graphical representation of the [IdentifiedVertex](#rdf-common); EdgeView - the [Edge](#rdf-common) representation; and GraphView - a container of Component classes that contain VertexViews and EdgeViews of a logical graph components (parts of a graph that are not connected with each other by edges).

Every View implementation contains a draw function that draws the object to a canvas and a drawQuick function that is used for redrawing the object during animation. This drawQuick function is supposed to leave out drawing of unimportant elements of the graph visualization, e.g. labels (InformationViews) of vertices. The GraphView's draw and drawQuick functions require, in contrary to other View implementations, a container of Canvas objects. The GraphView's draw and drawQuick functions decide which element is drawn to which canvas. Splitting the View objects to distinct Canvas elements speeds up redrawing of the whole graph during movement of selected elements. The selected elements are drawn to one set of canvases and the deselected elements are to another one. This allows to redraw only the canvases for selected elements during movement operation. The View implementations also contain its basic configuration and an ontology customization setter. The ontology defines a specific drawing configuration for certain types - based URI of the graph element.

The animations used to move the visualized vertices to their initial position are based on the JavaScript setTimeout function. An animation function e.g. flipGraph calculates the destination of every vertex and creates a list of AnimationVertexView objects, which contain a VertexView object, a translation vector - a vector translating the vertex to its calculated destination - and a speed value - that determines a step size in one animation's iteration. The function that performs the animation is animateTranslation. It takes a part of the vertex' translation vector according to its speed value, adds the part of the vector to the vertex' position, increases the vertex' speed value, redraws the graph and sets a timeout for the next iteration. The translation animation stops when all the vertices are moved to their destination. The movement of vertices ends at the same time since the step in every animation depends on the vertex' speed value, which is the same for all translated vertices.

The most important decision for animations was whether to use HTML5 Web Workers standard to simulate threads. Using this standard would allow us to perform animations and their computations of vertices positions at the same time (from the user's perspective). This would not block the browser during a long running calculation. For example, the Gravity visualization recalculates in each of its iterations forces between vertices and applies them to their positions. Because of that the calculation cannot be performed discretely, but has to count every position of all vertices before their final location can be calculated. For graphs with over a hundred vertices, the gravity animation requires more time than for a graph with ten vertices. Web workers would allow to run a background computation of vertex positions and redraw the graph regularly without blocking the web browser's input. Without using the standard the calculation has to be regularly stopped to provide time for the redrawing and processing of the browser's input. The use of the web workers standard appears to be a better decision, but since it is not yet supported by the usual web browsers (Microsoft Internet Explorer, Mozilla Firefox, Google Chrome, Opera), the standard was not implemented.

#### Package cz.payola.web.client.presenters

As was already mentioned, the client HTML application is based on the MVP (Model-View-Presenter) pattern. This means that it is separated into three logical parts. The [model](#model) contains the data accessing tools. The [view](#view) describes how are the data shown to user. The presenter defines the logic of the data representation in the view. It describes which view is used for which data and how is handled the user input and output.

The package contains e.g. GraphPresenter, AnalysisBuilder, PluginCreator  presenters. We will take DataSourceBrowser as an example to describe the funcionality. The DataSourceBrowser class contains a PluginView object. Looking as an user at the application, you can see the view - a drop-down list of visualization plugins, a controls menu and a currently selected visual plugin. The one difference between our MVP pattern and the usual one is that we used [events](#events) system. DataSourceBrowser defines event handlers and adds them to the view object. If for example a graphBrowsing event of the PluginView is triggered, the presenter tells the view that it has to wait for a response (the view shows a loading dialog). The presenter asks the model for neighbours of a vertex that user has selected, which creates a request to the server and finally it tells the view that the operation has finished and gives the recieved data from the model - server response - to the view to update itself. This whole operation is described in an event handler.

To see how is the request to the model processed go to [How it works](#how_it_works) in [web](#web).

#### Dynamic PluginInstanceView loading
The Payola framework now lets you define an override for the default plugin instance visualization in the analysis editor/visualizer. This can be done by creating a custom class in the package `cz.payola.web.client.views.entity.plugins.custom`. An example of such a class is the `cz.payola.web.client.views.entity.plugins.custom.DataCubeEditablePluginInstanceView`, which is currently the most complicated one. 

For each plugin instance type in a rendered analysis, the visualizer queries the remote Dependency provider for the code of an override. The returned code (empty if no code) is evaluated and after that, the `PluginInstanceViewFactory` tries to create an instance of the override. If it succeeds, the override is returned, if it fails, it falls back to the generic visualizer.

<a name="used_libraries"></a>
## Used libraries, frameworks & tools

While developing the Payola, we used the following technologies:

- [SBT 0.11.2](https://github.com/harrah/xsbt/wiki/) (Scala Build Tool) - [License](https://github.com/harrah/xsbt/blob/0.11/LICENSE)
- [Apache Jena 2.7.0-incubating](http://jena.apache.org/) (Java framework for building Semantic Web applications) - [License](http://jena.sourceforge.net/license.html)
- [Squeryl](http://squeryl.org/) (Scala ORM) - [Apache 2.0 license](http://www.apache.org/licenses/LICENSE-2.0.html)
- [Play! 2.0](http://www.playframework.org/) (Scala MVC web framework)- [Apache 2.0 license](http://www.apache.org/licenses/LICENSE-2.0.html)
- [jQuery](http://jquery.com/) (JavaScript Library) - [MIT License](https://github.com/jquery/jquery/blob/master/MIT-LICENSE.txt)
- [jQuery autosize](http://www.jacklmoore.com/autosize) (jQuery plugin for autosizing textareas) - [MIT License](http://opensource.org/licenses/mit-license.php)
- [jQuery blockUI](http://jquery.malsup.com/block/) (jQuery plugin for blocking UI nicely)
- [LiveQuery](http://docs.jquery.com/Plugins/livequery) (jQuery plugin) - [MIT License](http://opensource.org/licenses/mit-license.php)
- [Twitter Bootstrap](http://twitter.github.com/bootstrap/) (collection of CSS and JS code from Twitter) - [Apache 2.0 license](http://www.apache.org/licenses/LICENSE-2.0.html)
- [Colorpicker for bootstrap](http://www.eyecon.ro/bootstrap-colorpicker/) (JS module for Twitter Bootstrap)
- [Ace](http://ace.ajax.org/) (web editor for programming languages with syntax highlighting) - [Mozilla tri-license](http://www.mozilla.org/MPL/)
- [Select2](http://ivaynberg.github.com/select2/) (JavaScript autocomplete plugin) - [Apache 2.0 license](http://www.apache.org/licenses/LICENSE-2.0.html)
- [sprintf](http://code.google.com/p/sprintf/) (SprintF implementation for JS) - [BSD License](http://opensource.org/licenses/bsd-license.php)
- [Flot](http://code.google.com/p/flot/) (JavaScript charts plugin) - [MIT License](http://opensource.org/licenses/mit-license.php)
- Geocoder (Gisgraphy wrapper by Matej Snoha)
- [Google Maps](https://developers.google.com/maps/)

## Unit tests

To run all tests, use the `test` SBT task on the root project. Or if you want to run tests for a concrete project, switch to the project in the SBT using `project [projectName]` (e.g. `project compiler`). The tests don't cover everything, only some portions of the code are unit tested. Tests of the data project might be quite useful in case you'd like to use different database server. They verify that persistence of all entities and their properties work.

## API documentation

The generated API documentation isn't included, however you can generate it using the SBT. To generate API documentation, use the `doc` SBT task on the root project. Each project has its own API documentation which can be found in the `target/scala-2.9.1/api` subdirectory of the project.

## Continuous integration
In order to have the code in the repository compilable all the time, we use a [TeamCity](http://www.jetbrains.com/teamcity/) as continuous integration tool. Since the integration rules are currently set that the only rule is that the application should compile, only a simple build ant script is used.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project name="Application" default="build" basedir="payola">
	
	<target name="build" depends="sbt" />	
	
	<!-- New directories -->
	<property name="sbtloc" value="E:\sbt.bat" />

	<target name="sbt" description="simple build tool">
		<exec executable="${sbtloc}">
			<arg line="cp" />
		</exec>
	</target>
</project>
```

This prevents us from situations, when somebody pushes a commit and does not test it properly. It may prevent the application from compiling, so that the rest of the team cannot pull a broken code from the repository. With TeamCity, such a broken commit is revealed in about 2 minutes and the whole team gets noticed about it. It takes us just a few moment to get it right.

![TeamCity](https://raw.github.com/payola/Payola/master/docs/img/teamcity.png)

In the future, we will work hard to integrate test suites into the continuous integration process, as well as automatic deployment to our production server.

##Chronologically ordered description of work progress
1. Analysis of existing implementations and approaches [M1-M2]
2. Detailed specification of concrete system functions, architecture and interfaces between modules [M1-M3]
3. definition of application type
    a. overall application design (client-server architecture)
    b. definition of server-side application architecture
    c. domain entities specification
    d. definition of analysis evaluation subsystem architecture
    e. client-side architecture
    f. definition of client-server communication; architecture of corresponding systems
4. Prototype implementation [M1-M8]
    a. creating an empty web application based on Play 2.0 RC
    b. s2js
    c. domain entities implementation
    d. data layer implementation
    e. RPC
    f. JENA integration
    g. squeryl integration
    h. creating web application prototype based on fake data
    i. integrating squeryl and data layer
    j. integrating data layer into the web application
    k. virtuoso DB communication
    l. analysis evaluation
    m. RPC security
    n. DB initializer
    o. plugin compiler
    p. client-side code development
    q. entity sharing
    r. ontology parser
    s. ontology customizations
    t. error handling
    u. Tests on real data, debugging [M8-M9]
5. Documentation (developer, user, installation) [M7-M9]

## Critical evaluation of accepted solutions and technologies

### Scala language, SBT

The first decision, our team faced, was which programming language and platform to use. Our team was quite diverse in terms of programming language symphaties, we wanted to learn something during the project implementation and to use a state-of-the-art technology. The project also had to be platform independent, which practically narrowed our choices to Java and languages built on the top of JRE. And because Scala seemed to be the most mature one (besides Java), we chose it. Nobody had any experience with it, but we got used to it quite quickly and now, we don't regret that choice. The only problem was that as we were getting more and more familiar with the language and the functional programming paradigms, the code that was written at the beginning became obsolete after couple of months, so we refactored a lot.

The SBT (Scala Build Tool) was the choice number one in the Scala ecosystem, because everything is done in Scala and nobody needs to learn a new language or notation in order to configure the build. It was going through rapid development at the time we started using it, so there were some problems at the beginning, but they went away with the new versions.

### Jena

As RDF-related technologies are still relatively young, at least in the real world (i.e. not in research), there aren't many choices when it comes to libraries for parsing RDF/XML or TTL files in Scala (or Java). Jena was our choice number one as it is being actively developed by the [Apache Software Foundation](http://www.apache.org) and was likely to be well-debugged.

Other options were either [JRDF](http://jrdf.sourceforge.net) which is no longer maintained as of May, 2011; or [Sesame](http://www.openrdf.org) which, however, lacks support for ontologies (OWL, ...).

### Virtuoso

In general, there are three (actively developed) different RDF databases available - [Virtuoso](http://virtuoso.openlinksw.com), [Dydra](http://dydra.com) and [4store](http://4store.org). At the time of deciding, we hadn't had any experience with either of them and we've eventually decided to go with Virtuoso as it is being used by [OpenData.cz](http://opendata.cz), which we were aware of.

Nevertheless, switching to a different RDF database isn't a hard task, simply replacing the `VirtuosoStorage` class with another `Storage` subclass is sufficient.

### Play 2.0
Since the list of available Scala web frameworks is not really a long one, we've chosen Play 2.0 rather quickly. The other web frameworks for Scala more or less tried to breake the MVC pattern (e.g. Lift) so it was clear we will choose this one. When we started to develop Payola, the Play 2.0 wasn't even in release candidate stage, so from time to time, it was a little bit hard to maintain the changes between versions. The most difficult part was to find a solution for a problem since not many people have used the Play 2.0, at least in the beginning. After a while, some documentation became available so the work with the framework was more comfortable.

The API of the framework is good, the only negative thing was that we had to write some custom action wrappers to achieve secured functionalities.

### s2js

To use our own Scala to JavaScript compiler seemed to be risky, we had to spend not a small amount of time on it and several bugs appeared during the development. The debugging is slower and much less developer friendly. The advantages, on the other hand, are full intellisense and refactoring support in the IDE, the fact that whole project is written in one language and, not to forget, RPC. We also exactly know how it works, so questions about supported language constructs and bug fixes were resolved rather immediately (unlike in case of third-party tools). Looking back, it's undecidable whether writing the client side in Scala and not directly in JavaScript was a pro or con.

The next step is to separate the s2js from the Payola and develop it independently as a standalone project. 

### RPC
It may seem odd that while developing a graph visualisation tool, we came up with Scala to JavaScript compiler and RPC implementation. But it was worth it. After the s2js was done and RPC implemented, it was much easier to implement Payola. As we expected, it made the code transparent. The typed client-server call architecture prevented us from a lot of debugging, as well as standardized call to server and global error handling on client RPC wrapper did. In conclusion, the RPC saved us a lot of time.

### HTML5 (canvas element)
It was a good decision to make a web application because we can target many platforms, including mobile ones (after some more work). Because of this, it was definitely a great decision to use canvas element and avoid using Flash. But we are not really sure if we shouldn't start visualising graphs with SVG instead of canvas HTML5 element. It is still a rather new technology and it has got some limitations. E.g., in Firefox, it is not possible to use font-face fonts in canvas, because the rendering of the text depends on time which browser spends on loading the font from the webserver. If it takes a long time, the browser won't use the font while rendering. It is a known and annoying issue of Firefox but it has no known solution, even preloading does not work properly. That's why we had to render glyphs over vertices as span elements. Maybe, SVG would have solved some our problems, but, as we haven't tried using it, some other could have arisen.

### Actors

Scala provides many means to handle concurrency and issues connected to it, we used parallel collection quite a few times, but the best choice was to use actors within the analysis evaluation process. The actors almost naturally fitted to the problem, the implementation was therefore straightforward. In our opinion, implementing the same thing using just threads would take much more time, lines of code and debugging time.

### Squeryl
During the first months, we wanted to use an ORM library to work with relational data. In the beginning, while designing the application, it was great to count on it. After a while, when we acquired some experiences with Squeryl, we started to dislike it a bit. It brought us some performance issues (it lacks eager relation loading - `Relations are lazy, and are subject to the N+1 problem`) and over a time, we had to conclude that the main benefit of the technology is out-of-the-box DB independence of the code you write. Unfortunately, there weren't many Scala ORM libraries when we started the project and using Java ones, like Hibernate, did not look very reasonable.

### Custom events vs. JS libraries

At first, some members of our team were a little bit sceptic about using our custom MVP implementation instead of an out-of-the box MVC JavaScript framework with automatic data and events binding. Over some time, they recognized that it was a great way how to do this. Those MVC frameworks are still in an early development stage and do not have a very good documentation. Moreover it took several hours to write our own event handling system or implement the MVP pattern. As a bonus, we do not rely on 3rd party library which would mean a lot of additional unused code as we would not use the whole library.

On the other hand, Payola depends on some other JavaScript libraries. Besides jQuery, which is rather a dependency for another libraries, we use them completely. That's because they help us accomplish small specific tasks (colorpicker, modal dialogs, syntax highlight, ...). It saved us a lot of time to use them instead of writing our own solution. Especially because they are small and have simple APIs.

The event system also simplified our code and made it more readable because, while utilizing Scala syntactic sugar, it minimizes boilerplate code.

## Future work
Since there is always something that you can do better or more sophisticated, we also have a list of things which we are looking forward to change in Payola. Here are some examples:

- Fully implement the Play! 2.0 Promise API
- Improve Continuous Integration workflow, including autodeploy
- Prepare build script for WAR packages
- Let the user to start an analysis evaluation, close the browser window and come back later for the results (generally support for really long-running analyses)
- Administration panel (user management, statistics, etc.)
- Implement more visualization plugins (pie charts, more universal bar charts, 3D visualization)
- Perform a security audit to prevent CSRF, XSS and more web-specific attacks
- Add mechanism which determines the version of user's browser, especially advise users of incompatible browsers to update
- Implement analysis result persistence into the personal Data Source
- Make the s2js compiler a completely standalone product
- Support for large graphs that wouldn't fit into the memory (i.e. lazy loading of vertices)
- Add full support for all [Squeryl-compatible](http://squeryl.org/supported-databases.html)databases
- Allow update database structure in a way that preserves stored data (currently - running database initializer with a new database structure drops existing schema)
- Support for other RDF databases besides Virtuoso

> You can find a list of improvements that is currently being worked on at [GitHub](https://github.com/siroky/Payola/issues?sort=updated&state=open).

## Complex/standalone features
### DataCube support

The Data Cube vocabulary support is quite a complex feature, which introduces a bunch of new mechanisms and features. Some of them are described in corresponding sections. The rest of presented here.

#### Parsing the DCV
The classic Downloader is used to obtain the TTL DCV graph from the URL specified by the user. The downloaded content is passed to Jena, which builds an RDF graph, if possible.

We introduced a couple of case-classes, which are capable of holding the necessary information. They are capable of describing the interesting parts of the DCV. See `cz.payola.common.rdf.DataCubeVocabulary`. Those are utilited by the object `cz.payola.domain.rdf.DataCubeVocabulary`, which defines an `apply` function. That takes the URL, runs the downloader, queries the graph for predicates from the `http://purl.org/linked-data/cube#` namespace and builds up the object representation of the vocabulary.

#### Pattern selection
The next interesting part of the DCV implementation is the newly integrated query-by-example principle. A new, very simple graph visualizer was introduced, the `SimpleGraphView`. It has one special event to bind on, the `patternUpdated`. That event is triggered, when the user selected the specified amount of vertices. Only `TreeTechnique` is used to render the graph (becuase it is reasonably fast).

In order to deliver a preview for the pattern selection, the following was done:

- [Partial analyses](#partial-analyses)
- [Client-side vertex name generator](#lodvis-integration)
- [Limit plugin with optimizations](https://github.com/payola/Payola/blob/master/docs/user_guide.md#limit) - User guide

When running a preview, a sub-analysis is created, appended with a Limit plugin (to make the preview reasonably fast and large). The sub-analysis is evaluated and results presented to the user via the new `SimpleGraphView` component.

#### Custom plugin instance views
The [DataCube plugin](#datacube) handles a variable count of parameters, but it uses only a one to obtain a pattern. Therefore, the following was made:

- new flag for `StringParameter`: `isPattern`
- [Dynamic PluginInstanceView loading](#dynamic-plugininstanceview-loading)
- parameters ordering - `order` flag

Based on the [Dynamic PluginInstanceView loading](#dynamic-plugininstanceview-loading), we were able to create a custom visual overrides, which handles rendering of an instance derived from the DataCube plugin codebase.

#### DataCube visualizers
A couple of new visualizers has been implemented, the part interesting from a developer's point of view is that they search the supplied graph for DCV definition and build a UI based on what they find. That is also why the `DataCube` plugin adds the DCV definition to the results. The next level would be to utilize *LDVM input signature*.

[Read more in User Guide](https://github.com/payola/Payola/blob/master/docs/user_guide.md#data-cube-vocabulary)

The [Client-side vertex name generator](#client-side-vertex-name-generator) is used to build up a valid SPARQL pattern.

### Client-side vertex name generator
A simple class was introduced to generate an endless (+- range of Integer) series of vertex names. Every get triggers an increment, therefore a series v1, v2, ... is the result.

### MapView
We have integrated the Google Maps API in order to provide a view, which is based on a map of the planet Earth. It has one specific control generated from a passed list of years. Those are used to build a control, which enables and disables layers with heatmaps. In future, it will be further refactored to be more generic. (But you can do it, if you want :-)).

### Geocoding
With the Java 1.7 (!!!) Geocoder (by Matej Snoha) wrapper, we integrate a mechanism, which enables the developer to translate a location name into GPS coordinates. See `GeocodeModelComponent` and `Geo` shared to use it. Both of them has only simple methods (`geocode(location: String)` or `geocodeBatch(places: Seq[String])`), so no further description is needed.

## Changelog
### version 1.1 [data-cube by Jiri Helmich]
- [Inner analyses support](#inner-analyses)
- [Dynamic PluginInstanceView loading](#dynamic-plugininstanceview-loading)
- [Partial analyses](#partial-analyses)
- [Analysis cloning](#analysis-cloning)
- [DataCube Support](#datacube-support)
- [Limit plugin with optimizations](https://github.com/payola/Payola/blob/master/docs/user_guide.md#limit) - User guide
- [Pattern selection](#pattern-selection)
- [LodVis integration](https://github.com/payola/Payola/blob/master/docs/user_guide.md#lodvis-integration) - User guide
- [Client-side vertex name generator](#client-side-vertex-name-generator)
- [MapView](#mapview)
- [Geocoding](#geocoding)
- [Virtuoso Secured SPARQL Endpoint Fetcher](#virtuososecuredendpointfetcher)
- THEAD, TH and TBODY elements support