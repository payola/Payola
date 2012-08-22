<a name="top"></a>
<a name="user"></a>
# Payola!
---
Payola is a HTML5 web application which lets you work with graph data in a completely new way. You can visualize Linked Data using several plugins (which produce a table, graph, etc.). That also means, that you no longer needs Pubby to browse through a Linked Data storage (via its SPARQL endpoint). Moreover, you can create an analysis and run it against a set of SPARQL endpoints without any knowledge of SPARQL language itself. Analysis results are processed and visualized using the embedded visualization plugins.

Since Payola is rather a platform than a closed project, you can fork the project and write your own plugins, extensions and more on [https://github.com/siroky/Payola](https://github.com/siroky/Payola).

# Setting up Payola
## System Requirements

Payola requires a [Scala](http://www.scala-lang.org) environment, which is supported on virtually any platform capable of running Java code - both Unix and Windows-based systems are fully supported. The system should have at least 1GB of memory dedicated to Payola itself.

Aside from the actual Payola server, you need to have a running [Squeryl-compatible](http://squeryl.org) relational database for storing user data, a [Virtuoso](http://virtuoso.openlinksw.com) server for storing personal RDF data and a SMTP server for the plugin approval process. Neither of these need to necessarily be running on the same system as Payola itself (this is configurable in the `payola.conf` file as described later on).

To work with Payola, a web browser capable of displaying HTML5 web pages is required. Payola takes advantage of many HTML5 features - keep your web browser up-to-date all the time. Recommended are the *latest versions* of WebKit-based browsers (e.g. Chrome, Safari), Firefox, Opera, or IE. A 1440x900 or larger display is highly recommended.

## Installation Guide

You need to have a working Scala environment with [SBT (Scala Build Tool)](https://github.com/harrah/xsbt/wiki/) installed to run Payola. Clone Payola git repository: `git://github.com/siroky/Payola.git` to a local folder.

### <a name="configuration"></a>Configuration 

Payola comes pre-configured to work with default settings of a Virtuoso server and an H2 database installed on the same server as Payola is running (i.e. localhost). To change this configuration, edit `payola/web/shared/src/main/resources/payola.conf` - it’s a regular text file with various key-value options separated by an equal sign (`=`) on each line. Comment lines start with a hash symbol (`#`).

> **Virtuoso Settings**

> ** **

> *virtuoso.server* - address of the Virtuoso server

> *virtuoso.endpoint.port* - port of the Virtuoso server's SPARQL endpoint

> *virtuoso.endpoint.ssl* - enter true if the connection to the Virtuoso SPARQL endpoint should use SSL

> *virtuoso.sql.port* - port of the Virtuoso server's SQL database

> *virtuoso.sql.user* - SQL database login name

> *virtuoso.sql.password* - SQL database login password

> ** **

> **Relational Database Settings**

> ** **

> *database.location* - JDBC URL of the database

> *database.user* - database login name

> *database.password* - database login password

> ** **

> **User**

> ** **

> *admin.email* - Email of the admin user. A user with this email address also gets created when the initializer project is run.

> ** **

> **Web**

> ** **

> *web.url* - URL of the website, by default `http://localhost:9000`. The URL must start with `http://` or `https://` and mustn't end with a trailing `/`.

> ** **

> **Email**

> ** **

> *mail.smtp.server* - Mail server.

> *mail.smtp.port* - Mail server port.

> *mail.smtp.user* - Username.

> *mail.smtp.password* - Password.

> ** **

> **Libraries**

> ** **

> *lib.directory* - storage for 3rd-party libraries

> ** **

> **Plugins Directory**

> ** **

> *plugin.directory* - where to store plugins uploaded by users


<a name="compiling"></a>
### Compiling and Running Payola

As the cloned repository contains just source codes, it is necessary to compile Payola in order to run it. To do so, you need to have SBT installed as noted above. Open a command line (console, terminal) and make `payola` subdirectory the current working directory (e.g. by `cd payola`). Launch SBT (most likely using the `sbt` command) and enter the following commands:

<a name="run-initializer"></a>
```
> cp
...
> project initializer
> run
...
> project server
> run
```

Voilà! Your Payola server is running. The `initializer` project sets up your database to include an admin user (login `admin@payola.cz`, password `payola!`), a sample analysis and some data sources. You can, of course, remove those and create your own later.

> <a name="drop-create-warning"></a> **WARNING:** The `initializer` project drop-creates all required tables - hence all previous data will be lost. Run this project only when installing Payola or if you want to reset Payola to factory settings.

### Security

Payola allows users to store their own private RDF data using Virtuoso graph groups that are identified by a generated 128-bit UUID (and the H2 database is secured by a username-password combination). Both the Virtuoso server and H2 database allow incoming connections from outside of your network, or localhost, by default.

While a simple guess of another user's group identifier is unlikely (and a brute-force attack on the username-password combination for the relational database is highly noticeable), it is advisable to secure your local Virtuoso storage and your relational database by denying all incoming and outgoing connections outside of localhost, or if on a secure company network, outside of that particular network. This is up to each administrator to correctly set up the server's firewall.

---
# Using Payola

## Launching

To launch Payola, open SBT just like when you were [compiling](#compiling) it and enter these three commands (you do not need to run the `cp` command unless you have modified the source code since the last compilation):

```
> cp
...
> project server
...
> run
```

> *Warning:* Do **not** run the `initializer` project. All users, analyses, data sources, etc. would be lost. (See [this note](#drop-create-warning) for details.)

Once the server is running, enter the following address in your web browser:

><http://localhost:9000/>

Of course, the port may be different depending on your configuration file (see section [Configuration](#configuration) for details).

## Basic usage

You can use Payola both as a guest and a logged-in user. A guest is limited to analyses and data sources marked as public by other users and only in a read-only mode (i.e. can't edit them).

A logged-in user can create new data sources, analyses, plugins (you can actually write your own plugin, more about that [later](#plugins)), edit them and share them; and upload your own private RDF data.

---

### Data Sources

A data source is - as its name hints - a source of data. Payola needs to know where to get its data from for evaluating analyses, etc. - data sources.

##### Creating

Let's start by creating a new data source. In the toolbar, click on the `My Data Sources` button and select `Create New`. You will need to enter a data source name and description, decide whether it's public (then it's visible even to guest users) and which data fetcher to use.

A data fetcher is a plugin which can communicate with a data source of a specific type. For example, `SPARQL Endpoint` is a data fetcher. SPARQL is a query language for fetching data and such a data fetcher can work with any SPARQL endpoint.

Select a data fetcher of your choice, fill in the data fetcher's parameters (for example, `EndpointURL` parameter in `SPARQL Endpoint` data fetcher's case) and press the `Create Data Source` button. You have just created your first data source.

##### Editing

Use the toolbar at the top of the page to list available data sources (click on the `My Data Sources` button and select `View All`).

You can view all data sources available to you. If you wish to edit one (e.g. change its name or description), click on the Edit button on the same row. You'll be redirected to the edit page which contains a delete button as well, for removing the data source. The sharing functionality will be described in the [Sharing section](#sharing).

The same steps to list and edit apply to any other entity in the system (analyses, plugins, etc.).

##### Viewing

When on the Dashboard, or listing all available data sources, click on a data source to view it.

You'll be presented with a neighborhood of an initial vertex.

Such a subgraph can be viewed in many ways. The default one, presented to you, is a simple table. You can change the visualization plugin using the `Change visualization plugin` button. `Circle`, `Gravity` and `Tree` visualizations will display a regular graph using vertices and edges and differ only in the way they lay out the vertices. 

> TODO - OK describe what can be done with a graph

The `Column Chart` visualization will display a column bar graph, but works only with graphs of a specific structure. The graph must have one identified vertex, whose incoming edges are of a [`rdf:type`](http://www.w3.org/1999/02/22-rdf-syntax-ns#type) URI - the source of each edge must then have exactly three edges - one going to the aforementioned vertex and then two directed to a literal vertex, one with a string value (name of the column), the second one with a numeric value.

![Graph representation](https://raw.github.com/siroky/Payola/develop/docs/img/column_chart_data.png)

##### Ontology Customization

By default, each vertex and edge is of the same color and has the same size (width) when viewed using the graph-based visualizations. To emphasize or diminish some parts of the graph, you can customize the visual appearance using an ontology customization.

While viewing a graph, press the `Change appearance using ontologies` button. If you have already saved some customizations, they are listed here - if you haven't created any yet, select the `Create New` menu item. Enter name of the customization, ontology URL and press `Create`.

You will be presented with a customization dialog. On the left, ontology classes are listed - select one. On the right, properties of that class are listed. At the very top of the right column, you can customize the appearance of the class itself (in the graph displayed as vertices), below, you can modify appearance of that property (in the graph displayed as edges).

When done, simply press the `Done` button. If you want to further modify the customization, click on the `Edit` button in the `Change appearance using ontologies` button's menu.

---
### Groups

You can create user groups to make sharing easier (as described in the next chapter). Imagine you want to share a resource (e.g. an analysis) to a group of co-workers. One approach would be to share it to each one of them, but this can be tedious considering you might want to share something to them every week or every day. Hence there's a possibility to create a user group - in the top toolbar, click on the `User Groups` button and select `Create New`.

Enter the group name (e.g. 'My co-workers') and press the `Create Group` button. After the group has been created, you can start adding members to the group. To do so, make the `Members` field active and start typing - a suggestion box will appear offering users whose name matches the entered text. Click on the user to add him or her. If you decide to remove a user, click on the `x` button in front of his or her name. Remember to use the `Save Group` button before leaving the edit page, otherwise all changes made will be lost.

To delete a group, use the `Delete` button at the top-right corner of the edit page, or on the groups listing.


[Editing a Group](https://github.com/siroky/Payola/raw/develop/docs/img/group_edit.png)

---
### <a name="sharing"></a>Sharing

Now that you know how to create a group, let's share a data source. In the toolbar, click on the `My Data Sources` button and select `View All`. This lists all your data sources. You can use the `Edit` button to edit the data source, the `Private`/`Public` button to toggle whether the data source is private (only you and people you share it to can use it), or public - anyone can use it, even people who are not logged in; or use the delete button to remove the data source.

Then there's the `Share` button. When you click on it, a menu pops up, allowing you to share the data source either to users or groups. When you select the `To Users` menu item, a dialog is shown with a text field which will suggest users as you type just like when you were adding members to a group. 

The other option is to share the data source to groups - again a dialog will appear, letting you select multiple groups using the suggestion box. Add groups you want and confirm the dialog. All users within the selected groups will be now able to use this data source.

If you no longer want to share a resource with a group or a user, follow the same steps as if you wanted to share it with someone - the dialog which appears will contain the users or groups whom you've shared the resource to previously. Press the `Share` button to confirm the dialog. The list of users and groups allowed to access the resource will be updated accordingly.

---
### Private Data Storage

While listing data sources, you might have noticed a data source called `Private Storage of ...` - when you sign up, a new private data storage is created in your Virtuoso instance. You can add your own data to this storage. Of course, you can share this data source as well.

##### Adding data to data storage

To add data to your private data storage, use toolbar's `Private RDF Storage` button and select `Upload Data`.

Here you are presented with two options: to upload an RDF/XML or TTL file, or load the RDF/XML from a URL. Retrieving a graph in a TTL format from a URL isn't currently supported.

---
### Analyses

Creating a new analysis is similar to creating any other resource - in the toolbar, select `Create New` from `My Analyses` button's menu. You will be prompted to enter a name - enter the analysis' name - you can change it later on.

You will be presented with a blank page with a control box in the top-left corner. Start by filling in the analysis description.

First, you'll need a data source to start with. You can do so either using the `Add data source` button which will offer you available data sources, or `Add plugin` which lets you add a data fetcher - an anonymous data source. This can be useful if you decide to use a data source that you don't want to save right away (e.g. you know you'll use it just once).

Now that you've added a data source, you need to do something with the data. Click on the `Add Connection` button on your data source box. Payola comes with pre-installed plugins, which are described one by one below. Of course, you can add your own plugin (see [section Plugins](#plugins)). Plugins are ordered in a sequence (though more branches can be created, see below) - a plugin always gets the result of the previous one as its input.

##### Typed

This plugin selects vertices of a type that's filled in as a parameter `TypeURI` from its input graph.

##### Projection

Projection plugin takes property URIs separated by a newline as a single parameter. It will select vertices that are connected to other vertices using one of the listed URIs. 
> **Note:** Payola performs some optimizations, potentially merging several consecutive plugins together. For example, two consecutive projection plugins are always merged - hence their result isn't an empty graph as one could expect even if each of them lists completely different set of URIs, but a graph that contains both projections (if this optimization hadn't taken place, the first plugin would create a graph containing vertices connected to each other using URIs declared in the first plugin, which would then be filtered using the second plugin, resulting in an empty intersection).

##### Selection

Selection plugin lets you select vertices with a particular attribute - for example select cities with more than 2 million inhabitants.

> *Example:* Let's create an analysis which selects all cities with more than 2 million inhabitants. First, add a `DBpedia.org` data source, then connect a new `Typed` plugin with `TypeURI` `http://dbpedia.org/ontology/City`. Continue with a `Projection` plugin with `PropertyURIs` `http://dbpedia.org/ontology/populationTotal`, then a `Selection` plugin with `PropertyURI` `http://dbpedia.org/ontology/populationTotal`, `Operator` `>` and `Value` `2000000`. And that's it: your first analysis.

##### Ontological Filter

Ontological Filter plugin filters a graph using ontologies located at URLs listed in the OntologyURLs parameter.

##### SPARQL Query

This is a more advanced plugin letting you perform your own custom SPARQL query on the output of the previous plugin.

#### Branches

You can add multiple data sources, creating numerous branches that need to be merged before the analysis can be run (trying to run an analysis with branches that aren't merged will yield in an error). Of course, you can have such an incomplete analysis saved to work on it later.

Merging branches can be done using the `Merge branches` button. You will be given a choice to use either Join or Union. After selecting one (each is described below), you need to specify which branches to be merged - at the bottom of the dialog, there are wells for each input of the merge plugin. At the top of the dialog, you have each branch represented by the name of the last plugin in each branch. If you hover your mouse over the box representing a branch, that particular branch gets highlighted in the background. You need to drag the branch boxes to the input boxes (see picture attached).

![Branches on Input Boxes](https://github.com/siroky/Payola/raw/develop/docs/img/analysis_branches.png)

##### Union

Union simply merges two graphs together as one would expect. Vertices with the same URI will be unified, their properties merged.

##### Join

Join can be either inner or outer (default).

*Inner join:* Only edges from the first graph with URI defined in the `PropertyURI` parameter are included. Also, the origin of the edge must be present in the second graph. Otherwise, the edge is omitted.

> *Example:* You have two graphs:
>> ###### Graph A
>> `payola.cz/dog` - `payola.cz/barks-at` - `payola.cz/tree`<br/>
>>
>> ###### Graph B
>> `payola.cz/wolf` - `payola.cz/evolved-to` - `payola.cz/dog` 
>>
>
> If graph A is joined with graph B using the `payola.cz/barks-at` property, the `dog - barks-at - tree` triple is included in the result (`payola.cz/dog` is a vertex in graph B).
><br/><br/>
> When tried the other way around - joining graph B with graph A using the `payola.cz/evolved-to` property, an empty graph is returned because `payola.cz/wolf` isn't a vertex in graph A.


*Outer join:* All vertices from the first graph that are origins of edges with URI defined in the `PropertyURI` parameter are included. Moreover, if origin of the edge is included in the second graph, destination of the edge and the edge itself are both included in the result as well.

> *Example:* Using the same graphs as before, merging graph A with graph B will yield in the same result. Merging B with A, however, will include a single vertex `payola.cz/wolf` and no edges.

#### Running Analyses

Either on your dashboard, or on analyses listing, click on an analysis to display details of it. You are presented with an overview of the analysis (which plugins with which parameters and bindings are going to be used).

As some analyses can take a really long time to finish (some may be theoretically infinite), there's a timeout field in the top-right corner as well as a `Stop` button. By default, an analysis times out in 30 seconds. If you find it's too short time to evaluate your analysis, change it to a higher value.

Now press the `Run Analysis` button. If the analysis succeeds, you will be automatically switched to a result tab - you can browse the resulting graph here just as when browsing a data source.

If the evaluation fails, the plugin boxes turn red and an error description is shown when you hover the mouse cursor over them. You can then either try to run the analysis again, or to Edit it using the `Edit` button next to the analysis' title.

---
<a name="plugins"></a>
### Plugins

Creating a new plugin requires at least basic programming skills in Scala. A detailed reference of the Plugin class is described in the [Developer Guide](#developer) and in the [generated docs](#gen-docs). Here is a code of a sample plugin:

```
package my.custom.plugin

import collection.immutable
import cz.payola.domain._
import cz.payola.domain.entities._
import cz.payola.domain.entities.plugins._
import cz.payola.domain.entities.plugins.parameters._
import cz.payola.domain.rdf._

class ValuesInbetween(name: String, inputCount: Int, parameters:
				immutable.Seq[Parameter[_]], id: String)
	extends Plugin(name, inputCount, parameters, id)
{
	def this() = {
		this("Filter Values in Between", 1, 
			List(new IntParameter("MinValue", 0), 
				new IntParameter("MaxValue", 10)), 
			IDGenerator.newId)
	}

	def evaluate(instance: PluginInstance, 
			inputs: collection.IndexedSeq[Option[Graph]],
			progressReporter: Double => Unit) = {
		...
	}
}
```

In this example, a new plugin named `Filter Values in Between` is created. The parameterless constructor `this()` is called to fill in values to the default constructor. Here you set up required parameters as well.

The `evaluate` method is the one doing all the work. Here would be your code filtering the input graph. The `instance` variable contains all parameter values, `inputs` is a sequence of `Option[Graph]`'s - in our case just one as defined in `this()`. You can optionally report progress using the `progressReporter` function passed, which reports the progress to the user (values between 0.0 and 1.0).

Once you upload the plugin, an email is sent to the admin to review the plugin source code for security reasons. After he reviews it, you will receive an email with the admin's decision.

More information about plugin architecture can be found in the [Developer Guide](#developer). If you intend to write your own plugin, please, refer there.

---
<a name="developer"></a>
##### TODO - information about which libraries are used (where, why)

# Developer Guide

The Payola application consists of several layers and libraries that are all enclosed within a solution project ```payola```. The following sections describe structure of the solution, the functionality hidden within the layers and libraries and their relations.

## Solution structure

The solution is defined using the [SBT](https://github.com/harrah/xsbt/wiki/ "SBT") which isn't tightly bound to any particular IDE, so you may generate corresponding project files for the most commonly used IDEs (e.g. [IntelliJ IDEA](http://www.jetbrains.com/idea/), [Eclipse](http://www.eclipse.org)). SBT doesn't support any concept that can be directly used as a solution, but it can be emulated using projects and subprojects. In our case, the ```payola``` solution is just a project with no source files. The solution structure is:

- ```payola```
	- [```common```](#common)
	- [```data```](#data)
	- [```domain```](#domain)
	- [```model```](#model)
	- [```project```](#project)
	- [```s2js```](#s2js)
		- [```adapters```](#adapters)
		- [```compiler```](#compiler)
		- [```runtime```](#runtime)
			- [```client```](#runtime-client)
			- [```shared```](#runtime-shared)
	- [```scala2json```](#scala2json)
	- [```web```](#web)
		- [```client```](#client)
		- [```initializer```](#initializer)
		- [```shared```](#shared)
		- [```server```](#server)

Briefly, the project [```payola/project```](#project) defines this structure, dependencies between the projects, external dependencies and the build process, so it can be understood as a Makefile equivalent. Somewhat standalone libraries are the [```payola/scala2json```](#scala2json) project which provides means of Scala object serialization into the JSON format and [```payola/s2js```](#s2js) project which with all its subprojects enables us to write web applications in Scala (to compile Scala code to equivalent JavaScript code).

The Payola application itself is spread within the rest of the projects, namely [```payola/common```](#common) that defines classes that are used throughout all layers and even on the client side. The [```payola/domain```](#domain) mostly extends classes from the [```payola/common```](#common) with backend logic. The [```payola/data```](#data) is a persistence, data access layer. The [```payola/model```](#model) wraps up the previous three modules with a uniform interface. It's meant as a standard programmatic access point to Payola. Finally, the web application itself consists of the [```payola/web/initializer```](#initializer) which is a console application initializing the database (i.e an installer), [```payola/web/server```](#server) that is a [Play](http://www.playframework.org/) web application, the [```payola/web/client```](#client) which contains a browser MVP application (compiled to JavaScript) and last but not least the [```payola/web/shared```](#shared) which consists of objects that are called from the client, but executed on the server.

This structure also determines package names, which follow the pattern ```cz.payola.[project path where '/' is replaced with '.']```. So, for example, a class declared in the ```payola/web/client``` project can be found in the ```cz.payola.web.client``` package or one of its subpackages. The [```payola/s2js```](#s2js) project uses different package naming conventions, all packages and subpackages have the ```cz.payola``` prefix left out, so they start with ```s2js```.

<a name="project"></a>
## Project payola/project

This project contains only two files: ```plugins.sbt``` and ```PayolaBuild.scala```. The former one is just a configuration SBT file, i.e. SBT plugins that should be used on top of standard SBT and additional Maven repository declarations to download dependencies from.

The ```PayolaBuild.scala``` is a [build definition file](https://github.com/harrah/xsbt/wiki/Getting-Started-Full-Def) of the whole solution. The solution structure, projects, dependencies, compilation, test settings and other concepts used there are described in depth in the [SBT Wiki](https://github.com/harrah/xsbt/wiki). Moreover, there is a template for all projects that should be compiled to JavaScript, that adds the [s2js](#s2js) compiler plugin to the standard Scala compiler. To create a project that should be compiled to JavaScript, use ```ScalaToJsProject(...)``` instead of the standard ```Project(...)```.

### The cp task

The build file defines a custom SBT Task called ```cp``` which is an abbreviation for 'compile and package'. In order to support compilation of the payola solution in one step, we had to introduce this non-standard task. Because the solution contains both the [s2js](#s2js) compiler plugin project and also projects that use that compiler plugin, it's not sufficient to mark the compiler plugin project as a dependency of projects that should be compiled to Javascript. The Scala compiler accepts only ```.jar``` plugin files so the compiler plugin project has to be not only compiled, but also packed into a ```.jar``` package before it can be used.

### Compilation of payola/web/server using cp

Another compilation customization required by [s2js](#s2js) is added to the compilation process of the [server project](#server). During compilation of a ```ScalaToJsProject```, the generated ```.js``` files are written to the ```payola/web/server/public/javascripts``` directory. Each file provides some symbols (classes and objects declared in the file) and requires some (classes and objects used in the file). All files in the previously mentioned directory are traversed, while extracting the dependency declarations to the ```payola/web/server/public/dependencies``` file, which is used later.

### The clean task

The ```clean``` SBT task is overridden to ensure all generated files are deleted in addition to the standard behavior of ```clean```.

<a name="scala2json"></a>
## Package cz.payola.scala2json

To transfer data from the server side to the client side, one needs to serialize the data. Not only to save bandwidth, we've chosen [JSON](http://www.json.org) as the data format. It is a lightweight format that's also easy to decode in JavaScript, which is used on the client side.

While other solutions for serializing Scala objects to JSON do exist (for example [scala-json](https://github.com/stevej/scala-json)), they mostly work only on collections, maps and numeric types. Other objects need to implement their own `toJSON()` method.

This seemed to us as too much unnecessary code, so we've decided to write our own serializer. This serializer is capable of serializing any Scala or Java object using Java language reflection.

The serialization process has to deal with a few obstacles, such as cyclic object dependencies (i.e. one object's variable is pointing to a second object which has a variable pointing back to the first one).

- **Cyclic dependencies**: The serializer has an option to either serialize objects in depth (this means that a cyclic dependency will cause an exception), or to handle cycles using object references. The first option has an advantage that no references need to be created, hence the resulting JSON is exactly the object's representation, with no additional fields and is very easy to deserialize into the resulting object.<br/><br/>A cyclic dependency, however, is fairly common, hence it had to be dealt with: the serializer keeps a list of objects it encounters during serialization - each object is then assigned an object ID which is simply an index of the object in the encountered-objects list. The object ID is then appended to the serialized object as an `__objectID__` field . Once the object is encountered for the second time, instead of serializing the object again, such a construct is used: `"object_for_the_second_time": { "__ref__": 4 }` - i.e. a reference to an object with an object ID `4`.
- **Classes**: when deserializing the object on the client, a class of the object is required, so it needs to be included in the serialized object:
	- *Regular objects*: For regular objects, `__class__` field is added which contains the class name: `"some_obj": { "__class__": "cz.payola.some.class", ... }`
	- *Maps*: Even for maps and other collection, a class name is needed. Maps are translated to a JSON object with two field: `__class__` and `__value__`: ```{ "map": { "__class__": "scala.collection.Map", "__value__": { "key": "value", ... } } }```
	- *Collections*: Other collections get translated to a JSON object with two fields as well: ```{ "collection": { "__arrayClass__": "scala.collection.mutable.ArrayBuffer", "__value__": [ "obj1", ... ] } }```
	- *Arrays*: Regular array (i.e. `scala.Array`) is translated directly to a JSON array without any wrapper.
- **Fields**: As the Scala language doesn't have its own reflection API yet, [Java reflection API](http://docs.oracle.com/javase/tutorial/reflect/index.html) has to be used. This presents several problems regarding retrieving the object fields: some fields in Scala are translated into methods - a getter with no parameters and a setter with one parameter, in case it's a `var` field. The serializer must therefore look for fields even within methods when looking for a field of a particular name. Also, when requesting fields on an object, an empty array is returned - only declared fields get listed, so the serializer must go through the whole class hierarchy itself, listing fields of all interfaces and superclasses.

> Example: Assume this code:
>```
class Class1 {
    val map = Map("my key" -> "my value", "key2" -> "value")
    val list = List("Hello")
    var obj: Any = null
}
val o = new Class1
o.obj = o
```
>It will be translated to:
>```
{
	"__class__": "cz.payola.scala2json.test.Class1",
	"__objectID__": 0,
	"map": {
		"__class__": scala.collection.immutable.Map$Map2,
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

For some purposes, customizing the serialization process is necessary - it has proven useful to skip or add some fields of the object, etc. - this lead to serialization rules. For example, you might want to hide an implementation detail that a class' private fields are prefixed with an underscore (`_`) - it is possible to do so simply by adding a new `BasicSerializationRule`, where you can define a class (or trait) whose fields should be serialized (e.g. you want to serialize only fields of a common superclass, ignoring fields of subclasses), a list of fields that should be omitted (transient fields) and a list of field name aliases (a map of string &rarr; string translations).

The rules are applied in the same order as they are added to the serializer. You can explore additional serialization rules in our generated [docset](#gen-doc).

<a name="s2js"></a>
## Project payola/s2js

In order to implement the whole application in one language and to avoid code duplication that arises during development of rich internet applications (duplication of domain class declarations), we decided to use a tool that compiles Scala code to JavaScript. First of all, we have investigated tools that already exist:

- [https://github.com/alvaroc1/s2js](https://github.com/alvaroc1/s2js)
- [http://scalagwt.github.com/](http://scalagwt.github.com/)
- [https://github.com/efleming969/scalosure](https://github.com/efleming969/scalosure)


We have commenced with Scalosure, but rather sooner than later, we got to a point where we had to modify and extend the tool itself. As we dug deeper and deeper into the Scalosure, we started to dislike its implementation. Having in mind that the core of Scalosure was just about 1000 LOC (including many duplicities), we have decided to start fresh and implement our own tool, heavily inspired by Scalosure.

To make everything work, not only the [Scala to JavaScript compiler](#compiler) is necessary. One often needs to use already existing JavaScript libraries without the necessity to rewrite them to Scala. That's what the [adapters project](#adapters) is for. The somehow opposite direction is usage of classes from the [Scala Library](http://www.scala-lang.org/api/current/index.html#package) which can't be currently compiled using any compiler, not even ours. So the [runtime project](#runtime) contains simplified mirrors of the Scala Library classes compilable to JavaScript. There are also our own classes that are needed for the s2js runtime both in the browser and on the server.

Note that the tool was created just to match the requirements of Payola, so there are many gaps in implementation and ad-hoc solutions. Supported adapters and Scala Library classes are only those, we needed.

<a name="compiler"></a>
### Package s2js.compiler

The heart of the Scala to JavaScript process is surely the compiler. In fact, it's not a standalone compiler, it's a [Scala Compiler Plugin](http://www.scala-lang.org/node/140). So it takes advantage of the standard Scala compiler, which does the 'dirty' work of lexical analysis, syntax analysis and construction of the [abstract syntax trees](http://en.wikipedia.org/wiki/Abstract_syntax_tree) (ASTs) corresponding to the code that is being compiled. The Scala compiler consists of a sequence of phases, that can be perceived as functions taking an AST and producing an AST. There are some [standard phases](https://wiki.scala-lang.org/display/SIW/Overview+of+Compiler+Phases) that continually alter the AST, so Java bytecode can be finally generated. A Scala compiler plugin is just another sequence of phases that is mixed into the sequence of the standard phases on the specified places.

#### Class s2js.compiler.ScalaToJsPlugin

This is the definition of the scala compier plugin, its only phase ```ScalaToJsPhase``` and its components as it's descried it the official [Scala Compiler Plugin tutorial](http://www.scala-lang.org/node/140). The plugin doesn't change the input AST at all, it behaves like an identity function. But as a side-effect, it generates JavaScript code that should be equivalent to the input AST. The following custom plugin options are defined here:

- ```outputDirectory```: The directory where the generated JavaScript files are placed. Default value is the current directory.
- ```createPackageStructure```: If set to ```true``` a directory structure mirroring the packages in compiled files is created in the output directory. If set to ```false```, all generated files are created right in the output directory, which is taken advantage of during the compiler plugin tests.

Usage of the options can be found in the ```PayolaBuild.scala``` within the 
[```project```](#project) project.

#### Class s2js.compiler.ScalaToJsCompiler

An extension of the Scala compiler, that has the ```ScalaToJsPlugin``` plugged in, so the compilation of Scala files into JavaScript can be invoked programatically.

#### Package s2js.compiler.components

The previous two classes are utility classes, that don't participate in the compilation. They just invoke it. On the other hand, classes from the ```s2js.compiler.components``` directly take part in the compilation process. 

##### Class s2js.compiler.components.PackageDefCompiler

Purpose of this class is to compile the ```PackageDef``` AST nodes (representation of a package and all its content within a file) into JavaScript. Because the plugin compiler input AST is always a ```PackageDef``` node, the class is used as an entry point to the compilation process. 

> *Note*: The ```ClassDef``` is a type an AST node, that defines a class, a trait, an object or a package object.

The compilation algorithm works basically in the following way:

1. Retrieve the structure of the package using the [```s2js.compiler.components.DependencyManager```](#DependencyManager). It traverses the AST, finds all ```ClassDef```s and initializes the dependency graph of them. 
2. Compile the ```ClassDef```s using the [```s2js.compiler.components.ClassDefCompiler```](#ClassDefCompiler) in the topological ordering determined by the ```ClassDef``` dependency graph. If there is a cycle in the dependency graph, an exception is thrown. During the compilation of ```ClassDef```s, the ```DependencyManager``` is informed about the inter-file dependencies (e.g. when a compiled class extends a class that is not part of the current compilation unit ~ file).
3. Add the inter-file dependency declarations to the beginning of the compiled JavaScript file.

Moreover, the ```PackageDefCompiler``` defines additional public service methods (e.g. ```symbolHasAnnotation```, ```typeIsFunction``` etc.) that can be used by other components with a reference to the ```PackageDefCompiler```.

<a name="DependencyManager"></a>
##### Class s2js.compiler.components.DependencyManager

Tracks all kinds of so-called dependencies among symbols that are declared inside a ```PackageDef``` node:

- *ClassDef dependency graph*: Dependencies among ```ClassDef```s among the current compilation unit (```ClassDef``` ```A``` depends on ```ClassDef``` ```B``` if ```A``` extends or mixins ```B```). The graph is used to determine an order of the class compilation. 
- *Inter-file dependencies*
	- *Provided symbols*: The ```ClassDef```s that the current compilation unit provides (i.e. the API). Some other compilation units may require them.
	- *Declaration-required symbols*: The ```ClassDef```s that have to be declared in the generated JavaScript before the ```ClassDef```s from the current compilation unit are declared.
	- *Runtime-required symbols*: The ```ClassDef```s that have to be declared in the generated JavaScript so that the current compilation unit can run (they're not needed when declaring the current compilation unit in generated JavaScript).

<a name="ClassDefCompiler"></a>
##### Class s2js.compiler.components.ClassDefCompiler

In terms of code lines, the ```ClassDefCompiler``` is the largest class of the project, with objective to compile ```ClassDef``` AST nodes with everything they contain. The ```ClassDefCompiler``` has subclasses for some types of ```ClassDef```s that slightly alter behavior of the compiler to fit the particular ```ClassDef``` needs:

- ```ClassCompiler``` for classes and traits.
- ```ObjectCompiler``` for objects.
- ```PackageObjectCompiler``` for package objects.

Compilation of a ```ClassDef``` is composed of the three following steps:

1. Compile the ```ClassDef``` constructor.
2. Compile the members (fields, methods) of the ```ClassDef```. Inner classes or objects aren't currently supported.
3. Bind the ```ClassDef``` JavaScript prototype to an instance of the  [```s2js.runtime.core.Class```](#Class) class, so all instances of the ```ClassDef``` reference it.

Most of the Scala language constructs are compiled into JavaScript pretty naturally, majority of them have direct equivalents in the target language, so these naturally translated constructs won't be described here, as it would be a waste of space. We'll concentrate on how the differences between the languages are solved and on some other interesting details or extensions.

###### Expressions

In Scala, everything is an expression with a return value, even if the return value is ```Unit``` (void). As a consequence, return values of statements can be directly assigned to a variable. For example the ```if-then-else``` statement has a return value in Scala, yet in JavaScript, it doesn't. This is solved by wrapping the statement in an anonymous function, which is immediately invoked.

> Scala code:

```val x = if (a) { b } else { c }```

> compiles into:

```var x = (function() { if (a) { return b; } else { return c; } })();```

###### Operators

There are no operators in Scala, only methods with names like ```+``` or ```<=``` that the Scala compiler transforms into ```$plus``` or ```$less$eq```. On primitive types that are compiled into JavaScript primitive types (```Number```, ```String``` and ```Boolean```), those methods are compiled as the JavaScript operators. On other types, the methods are left untouched.

###### Match statement

JavaScript has no usable equivalent of the Scala match expression, so a sequence of ```if-then``` statements, that correspond to the match cases, is used instead. A lot of technical details has to be taken into account as there are a few pattern types, variable bindings, guards, etc.

###### Classes and inheritance

JavaScript is a prototype-based language, so it has no notion of classes at all. But it can be emulated using object constructor functions and prototypes. The constructor function defines and initializes fields of the object (```val```s and ```var```s) and executes the class constructor body. Methods of the class are set to the object constructor prototype. When the JavaScript runtime resolves object properties (fields, methods), it first looks directly into the object and then it traverses the object prototype chain so all class methods are found there. 

Inheritance is implemented by setting the prototype of the sub-class prototype to the super-class prototype (i.e. linking the prototypes to a chain). Trait inheritance is divided into two steps. First of all, references to all methods of the trait prototype are copied into the class prototype. Secondly, within the class constructor, the trait is instantiated and all fields of the trait instance are copied to the class instance that is being constructed.

A little problem arises with the objects and package objects whose declaration is also their initialization (correspondence to a static constructor). The problem is, that the objects may access other objects during their initialization. What's even worse is, that this access may be indirect by for example instantiation a class that accesses other object in its constructor. So it's almost impossible to determine the object initialization order. We've solved this by introducing the [lazy pattern](http://en.wikipedia.org/wiki/Lazy_initialization); the object initialization is delayed till the moment when it's actually accessed for the first time. This ensures that the objects are initialized in the proper order.

###### Annotation ```@javascript```

This annotation can be used on a method or a field, enabling the programmer to implement the method body or the field value directly in JavaScript. The method body or field value can be anything in Scala, because it gets replaced with the value provided to the annotation.


###### Annotation ```@remote```

An object or class marked with this annotation isn't compiled to JavaScript.  

<a name="s2js_rpc"></a>
###### RPC (Remote Procedure Call)

In order to simplify the client-server communication and to hide the low-level JavaScript constructs necessary for it (```XmlHttpRequest``` or ```ActiveXObject```) from the programmer, an [RPC mechanism](http://en.wikipedia.org/wiki/Remote_procedure_call) is used. The compiler takes a small but irreplaceable part in the whole process, the rest is done in the [server-side runtime](#runtime-server) and [client-side runtime](#runtime-client). 

All methods that are marked with the ```@remote``` annotation or defined on an object with the ```@remote``` annotation are considered RPC methods (remote methods), so their invocations are compiled to JavaScript in a different way. Remote methods may also be marked with the ```@async``` annotation, which makes them behave asynchronously, but introduces additional constraints on them (i.e. they must have the success callback function and fail callback function parameters and Unit return type). The compilation of a remote method invocation can be seen in the following example:

> Scala code: 

```
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

```
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

The array with types (```['scala.Int', 'java.lang.String']```) is there because Scala 2.9.1 doesn't provide full reflection support and the server-side runtime can't distinguish between ```List[Double]``` and ```List[Int]```.

Another related annotation is ```@secured``` which can be used both on remote methods or remote objects. If used on a method or if a method is declared within an object that is marked with that annotation, then the method is considered a secured remote method. The consequence is, that the last parameter is regarded as a security context, which isn't sent from the client, so the server-side runtime has to resolve it by itself (e.g. retrieve a user from the database by his id, which is stored in the cookie).

> Scala code: 

```
@remote @secured def foo(bar: Int, baz: String, user: User = null): Int = bar * baz.length
```

> compiles into: 

```
s2js.runtime.client.rpc.Wrapper.callSync(
	'remote.foo', 
	[123, 'RPC rocks.'], 
	['scala.Int', 'java.lang.String']
);
```

<a name="adapters"></a>
### Package s2js.adapters

As mentioned before, the adapters are defined to allow a programmer to access core JavaScript functionality or use already existing JavaScript libraries. Without them, it would be impossible to use for example ```document.getElementById``` method in the Scala code that will be compiled into JavaScript, because there is no such object ```document``` in the Scala standard library. The adapter classes don't have to contain any implementation, therefore they're mostly abstract classes or traits. They're not compiled to JavaScript, and neither are they used anywhere during Scala application runtime. The class and method names have to be exactly the same as in the adapted libraries.

#### Package s2js.adapters.js

Adapters of some of the JavaScript core classes and the global functions, objects and constants. The adapters are based on the 'JavaScript Objects' section of the [JavaSript and HTML DOM Reference](http://www.w3schools.com/jsref/default.asp).

#### Package s2js.adapters.dom

Adapters of all interfaces and objects (```Node```, ```Element``` etc.) defined in the [DOM Level 3 Core Specification](http://www.w3.org/TR/DOM-Level-3-Core/).

#### Package s2js.adapters.events

Adapters of all interfaces and objects (```Event```, ```MouseEvent``` etc.) defined in the [DOM Level 3 Events Specification](http://www.w3.org/TR/DOM-Level-3-Events/) including some of the the [DOM Level 4 Events](http://www.w3.org/TR/dom/#events) extensions.

#### Package s2js.adapters.html

Selected HTML related interfaces and elements (```Document```, ```Anchor```, ```Canvas``` etc.), based both on the [HTML Standard](http://www.whatwg.org/html) and on the 'HTML DOM Objects' section of the [JavaSript and HTML DOM Reference](http://www.w3schools.com/jsref/default.asp)

#### Package s2js.adapters.browser

Adapters of web browser related objects (```Window```, ```History``` etc.), based on the 'Browser Objects' section of the [JavaSript and HTML DOM Reference](http://www.w3schools.com/jsref/default.asp) and also on the same resources as the ```s2js.adapters.html``` package.

<a name="runtime"></a>
### Project payola/s2js/runtime

> TODO: H.S.

<a name="runtime-client"></a>
#### Package s2js.runtime.client

> TODO: H.S.

##### Package s2js.runtime.client.js

> TODO: H.S.

##### Package s2js.runtime.client.rpc

> TODO: H.S.

##### Package s2js.runtime.client.scala

> TODO: H.S.

<a name="runtime-shared"></a>
#### Package s2js.runtime.shared

> TODO: H.S.

<a name="common"></a>
## Package cz.payola.common

> TODO: H.S.

### Package cz.payola.common.entities

This package includes classes representing the basic entities (user, analysis, plugin, etc.) that ensure the core functionality of Payola. Each entity has its own ID (string-based, 128-bit UUID) and can be stored in a relational database (see the [data package](#data) for more information).

#### Package cz.payola.common.entities.plugins

> TODO: H.S.

#### Package cz.payola.common.entities.privileges

To share entities between users, privileges are used. This makes it easy to extend the model in the future, or to change the granularity of privilege granting. Currently, there are only privileges granting access to a resource - analysis, data source, ontology customization and plugin; however, a privilege type that grants a user the right to edit some entity, for instance, can be easily added.

#### Package cz.payola.common.entities.settings

The settings package encapsulates ontology customizations (used on the client side to change display settings of a graph using ontologies).

<a name="rdf-common"></a>
### Package cz.payola.common.rdf

This package contains classes representing RDF graphs and ontologies. Only core functionality is included in this package - class declarations to represent the data, some basic methods that are used on the client. More functionality, such as converting an RDF/XML file to a `Graph` object is added in the [`domain`](#domain) project.

<a name="domain"></a>
## Package cz.payola.domain

The `domain` project builds on the [`common`](#common) project, inheriting from classes and traits in the `common` project. Additional functionality and logic is hence added as well as dependencies on other libraries, such as [Jena](http://jena.apache.org) for parsing RDF/XML files into Graph objects.

The idea behind the `domain` project is for it to be fully independent on other projects within Payola (other than `common`) - you can take the domain project and use it in a different project without any modification.

### Package cz.payola.domain.entities

Domain entities extend the `common` entities that are mostly traits and fully functional classes are formed - as with the whole package, you can take the `domain.entities` package and use it completely elsewhere - outside of Payola without any modification.

### Package cz.payola.domain.entities.analyses

> TODO: H.S.

### Package cz.payola.domain.entities.plugins

> TODO: H.S.

### Package cz.payola.domain.rdf

The `common` Graph class is enriched by the ability to execute SPARQL queries as well as to convert the Graph object to an RDF/XML or TTL textual representation. A companion object is present as well capable of creating Graph objects from an RDF/XML or TTL representation. To perform these tasks, [Jena](http://jena.apache.org) library is used.

### Package cz.payola.domain.rdf.ontology

Just like with the `cz.payola.domain.rdf` package, the ontology package adds the ability to create Ontology objects from OWL or RDFS formats. No reverse conversion from Ontology object to textual representation is supported at this moment, however, as this functionality isn't required by Payola.

### Package cz.payola.domain.sparql

> TODO: H.S.

<a name="data"></a>
## Package cz.payola.data

This whole package represents the data layer. Trait `DataContextComponent` defines an API for communication between the data layer and other Payola components. The two vital tasks of the data layer are:

- to store and fetch the [domain layer](#domain) entities
- to use [Virtuoso](http://virtuoso.openlinksw.com/) server as a private RDF data storage

Architecture of Payola implies that the domain layer is independent on the data layer and since Payola is an open-source project, the data layer can be replaced by another implementation that fits different platform-specific needs. 

### Package cz.payola.data.squeryl

In this version of Payola, [Squeryl](http://squeryl.org) (an ORM for Scala) is used for persisting entities into an H2 database. Squeryl generates a database schema from the structure of objects to be stored. Every persistable entity is persisted in its own table, definition of this table is derived from the entity's object structure. In order to have the domain layer independent of the data layer, were implemented [entities](#squeryl-entities) that:

- represent entities from the domain layer and 
- can be stored/loaded via Squeryl ORM into/from the database

<a name="schema-component"></a>
The relational database schema definition is placed in `SchemaComponent` trait that uses `org.squeryl.Schema` object. This trait defines:

- session with connection to the database
- table for every entitiy that is persisted
	- the table structure is based on entity that is persisted in the table
	- contraints (such as PrimaryKey, Unique contraint)or column types of some fields are defined manually to match the Payola project requirements exactly
- foreign-key constraints for relations between entities
	- including the reaction on removing related entity
- factory for every persisted entity
	- the factory serves as a constructor for fetched entity
- `persist`, `associate` and `dissociate` methods


##### Why Squeryl?

Squeryl is an existing, tested, functional and simple ORM for Scala applications that had met the needs of Payola during the process of making a decision whether to use an existing ORM or implement our own ORM tool.

<a name="about-squeryl"></a>
##### Briefly about Squeryl

[Squeryl](http://squeryl.org) is a free ORM tool for Scala projects, it can be connected to any relational database supported by JDBC drivers.

A database structure needs to be defined in an object extending `org.squeryl.Schema` object. This object contains definition of tables - definition that says which entity is persisted in which table. Squeryl allows to redefine column types of tables, to declare 1:N and M:N relations between entities, to define foreign key constraints for those relations.

Squeryl provides lazy fetching of entities from "N" side of 1:N or M:N relations, which is a desirable feature of an ORM tool. The query that fetches the entities of a relation is defined in a lazy field of the related entity, on the first data request, the query is evaluated. There is a method `associate` in Squeryl for creating a relation between entities. Simplified code may look something like this:

<a name="squeryl-code-examle"></a>
```
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

	// Creates relation between the analysis and given plugin instance
	override def addPluginInstance(instance: PluginInstance) {
		inTransaction {
			_pluginInstancesQuery.associate(instance)
		}	

		super.addPluginInstance(instance)
	}	
}
```

Every query that fetches any data from database needs to be wrapped inside a transaction block (as can be seen in the previous sample code). Squeryl provides `transaction { ... }` and `inTransaction { ... }` blocks. Every `transaction` block creates a new transaction which establishes a new database connection, whereas `inTransaction` block nests transactions together.

<a name="squeryl-entities"></a>
#### Package cz.payola.data.squeryl.entities

All higher layers of Payola work with [domain layer](#domain) entities, but those entities can't be persisted in data layer. For every class of domain layer entity that should be persisted, there exists a class in the data layer that provides database persistence to the corresponding domain layer entity, as is shown in the following picture:

![Data layer entities](https://raw.github.com/siroky/Payola/develop/docs/img/data_entities.png) 

Every entity in this package extends the trait `Entity`, which provides Squeryl functionality. It could be compared to [Adapter](#http://en.wikipedia.org/wiki/Adapter_pattern) design pattern, where `Entity` from [`common`](#common) package is the Adaptee, `Entity` in this package is the Adapter, and Squeryl functionality is the Target.

Data layer entities extend the represented domain layer entities (with two exceptions that will be explained later), which allows to treat data layer entities like domain layer entities. There is no added business logic in data layer entities - their only purpose is to store/load domain layer entities into/from the database.

In order to persist a domain layer entity, the entity must be converted to a data layer entity. The conversion is run by companion object (extending `EntityConverter`) of data layer entity. Every data layer entity has its own converter. When the conversion fails, a `DataException` is thrown. Since every data layer entity extends a domain layer entity, there is no need for reverse conversion. Data layer returns data layer entities, which can be handled as domain layer entities in higher application layers.

The two mentioned exceptions are `PluginDbRepresentation` and `PrivilegeDbRepresentation`. These data layer entities do not extend `Plugin` and `Privilege` from the domain layer, because the real plugins and privileges may be added at the runtime (even by a user). These domain layer entities are just abstract parents of the real plugins and privileges, so that they are simply wrapped into data layer entities. The data layer entities are persisted and the domain layer entities are reconstructed from them via Java reflection API.

Domain layer entities allow adding other entities into collections (e.g. a plugin instance can be added to an analysis via an `analysis.addPluginInstance(pluginInstance)` statement). The data layer entities override this behavior by adding a code to persist this relation into the database and leaving the domain layer behavior unchanged (as shown in this [example](#squeryl-code-examle)). 

<a name="squeryl-repositories"></a>
#### Package cz.payola.data.squeryl.repositories

Repositories provide persistence and fetching of entities (entities must extend `Entity` trait in the [squeryl](#squeryl) package. The API for repositories is defined in trait `DataContextComponent` in [data](#data) package. The API is a set of traits, their structure is shown in the next picture:

![Data layer repositories](https://raw.github.com/siroky/Payola/develop/docs/img/data_repositories.png)

For every repository defined in the API there exists a repository component, that implements the repository. Traits `Repository`, `NamedEntityRepository`, `OptionallyOwnedEntityRepository`, `ShareableEntityRepository` are implemented within the `TableRepositoryComponent` trait. The rest of the API repositories are implemented by the corresponding repository components (e.g. `UserRepository` is implemented by `UserRepositorComponent`).

##### Eager vs Lazy loading
Squeryl provides only lazy data fetching from the database. The lazy fetching is used when loading usersplugin instance bindings from the database. These entities are loaded in the most simple fashion: 

- **plugin instance bindings** are loaded only with IDs of the target and source *plugin instances*, those *plugin instances* are loaded only when needed
- **users** are loaded with no data, all their *groups*, *privileges*, *analyses*, *plugins*, *data sources* are loaded only when needed

The rest of entities is loaded eagerly (i.e. entity is loaded with some of its relations evaluated). Eager-loading is provided by `TableRepository` abstract class. Every repository component that extends `TableRepository` must implement `getSelectQuery` and `processSelectResults` methods. `getSelectQuery` method defines a query to load the entity (and all related entities) from the database, `processSelectResults` method evaluates the defined query result and returns loaded entities. Entities are loaded by their repositories in the following way:

- **groups** are loaded only with their *owner*
- **privileges** are loaded in `PrivilegeDbRepresentation` form, the the *granter*, *grantee* and *object* are lazy-loded from database, finally the whole privilege is instantiated using Java reflection API
- **plugins** are loaded in the `PluginDbRepresentation` form with *parameters* and *owner* and then they are instantiated using Java reflection API
- **plugin instances** are loaded with *parameter values* and with related *plugins* and *parameters*
- **data sources** are loaded with *owner* and *parameter values* and with related domain layer `DataFetcher` plugin with its *parameters*
- **analyses** are loaded only with their *owner*
	- when there is an access to *plugin instances* or to *plugin instance bindings*, the complete analysis is loaded (i.e. no further fetching-query to database will be needed)

<a name="virtuoso"></a>
### Package cz.payola.data.virtuoso

Virtuoso is used for storing private RDF data of a user - classes in this package let you communicate with a Virtuoso instance and perform some tasks - create a graph group, store a graph in the graph group, and then retrieve all graphs within a graph group.

Some of these tasks are performed at Virtuoso's SPARQL endpoint which is as simple as posting a regular HTTP request, but some require a connection to its SQL database, for which a `virtuoso.jdbc3.Driver` driver is required. This driver is included in the `lib` directory of Payola project.

<a name="model"></a>
## Package cz.payola.model

The classes in this package build up a wrapper which encapsulates all the business and data access logic. The goal of the code in this package is to decouple any presentation layer from the application logic and data access. In fact, all of the existing presentation layers (web application controllers and RPC remote objects) are built on top of this package.

It is crucial to mention, that the model package does not make up the whole model. The model is spread into more packages, the domain, data, and common. All of those packages provide some capabilities and the model package itself uses them all to get specific tasks done.

If you want to understand the following text (and the code) better, please, get familiar with the [Scala Cake pattern for DI](http://jonasboner.com/2008/10/06/real-world-scala-dependency-injection-di/).

The whole model is divided into a several components. Each component has its own data subdomain which it wraps up. E.g. `DataSourceModelComponent` for data sources, `UserModelComponent` for users, etc. As you can see, the components bring us an API which makes us able to work with the data stored in the relational DB.

While utilizing the subcomponents the `ModelComponent` trait is build up to provide a single entrypoint to the model infrastructure. While utilizing the cake pattern, dependencies like persistance layer, RDF data storage layer and plugin compiler are injected to the model components.

Later on, when you will get familiar with the `cz.payola.web.shared` package, you will find out something more about an object named `cz.payola.web.shared.Payola`. This is an example implementation of the `ModelComponent` trait.

<a name="web"></a>
## Package cz.payola.web

All the code connected with the web presentation layer could be found in this package. The web application is, as the rest of the project written in the Scala language. To make the job easier, we built up the presentation layer on top of the [Play 2.0 framework](http://www.playframework.org/), which is also completely written in the Scala language, so it was easy to integrate with our appliaction.

Since we started to use the framework in the stage of early access preview, we currently do not take advantage of all the features provided by its API. If you want to know, what can be done better nowadays, just look into the Future work section of the documentation. As an example, one can mention utilizing the Promise API. When fully available (depends on Servlet 3.0 implementation in a wide spectrum of Java web servers), we should also take advantage of the possibility of exporting the web application into a single WAR file which can be deployed into a servlet conatiner.

Since the Play framework is a MVC framework, our web presentation layer build on top of it also uses the MVC pattern. If you look closely into the `cz.payola.web.server` package, you can see a classic code separation in there - controllers and views, model is imported from its own separate package.

Since it crucially improves the usability of the application, a rather high count of functionalities is built on top of the AJAX technology, so the calls from the client side of the appliaction to the server side of the application are realized via XHR requests. Since we knew from the past, that in many appliactions, the AJAX technology is a weak spot in the application architecture and security (and the code style is often terrible), we decided to make a standard for our XHR calls and came up with an idea of JavaScript RPC. Inspired by [Google Web Toolkit](https://developers.google.com/web-toolkit/) technology, we wanted to introduce a simple-to-use standard with a minimal overhead for the developer. Also, we wanted the RPC to be as secure as standard HTTP request via controller is. Moreover, since the application can be extended in a several ways, we wanted the RPC to be based on a single programming language. This is how we came up with the idea of the s2js compiler which enabled us to do all the things described in this paragraph. If you want to know more about how the RPC works, read the appropriate section.

Since the web application is not based on a monolithic architecture, we divide the code into several packages - client, initializer, server and shared. The initializer subproject is responsible for initialization of databases used by the web application. The rest builds up the web application itself. The server package contains the code which runs on the server side, the client package the code which runs on the client side. The code in the shared package could be run on both the client side and the server side. Also *remote objects* should be placed into this package, since they run on server and can be called from the client side.

This is also one of the restrictions of our RPC. You need to separate code which is executed on the client side from the code which is executed on the server side. But you can make a call from the client side to the server side as there is no such thing as client-server architecture. 

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

The code in this package is a collection of remote objects and code shared between the client side and the server side as our web application needed it. 

The most important object in this package is the `Payola` object. It is an instance of the previously mentioned `ModelComponent` trait. It serves as an entrypoint to the whole model of the application. Since it is not a remote object, you cannot use it on the client side, but it is heavily used by the remote objects on the server side. Also, controllers from the package `cz.payola.web.server` uses the `Payola` object to gain access to the data and business logic of the application. Since it is a `Scala object`, it behaves as an Java singleton in the right point of view. So you have exactly one instance in the application and you don't need to create it, it is given to you for free by the Scala runtime. If it reminds you of something, you are kind of right, it is very similiar to a classic DI container (but affected a lot with the `Scala cake pattern for DI`).

<a name="server"></a>
### Package cz.payola.web.server

The code in this package is built on top of the MVC API of the Play 2.0 framework. Since we don't use their standard DAL (Anorm) and since we have a custom model, you will not be able to find a package named model nowhere in this package.

What you can find is a directory named app which contains controllers and views of the web application. They are standard controllers and scala templates as introduced int the [Play 2.0 framework docs](http://www.playframework.org/documentation/2.0.2/Home).

There are two special controllers you should know more about:

- PayolaController, which is the base controller of all the others, since it contains code which is more or less used by all the controllers
- RPC, which is controller that processes the XHR calls from the client side

Since the RPC controller is not a simple thing, we will dicuss the controller a bit more. When the RPC call arrives from the client side, it holds the information about which remote method should be invoked on the server and, more important, invokation parameters. Example of such a call follows:

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

As you can see, the `POST` request, which represents an asynchronous RPC call, conatins the name of the remote method which should be invoked by the RPC controller (which delegates most of the work to a class called `RPCDispatcher`). Since one can make his own RPC call very simply, we restricted the "invokable" methods to those that are defined in the body of an object annotated with the `s2js.compiler.remote` annotation. If you try to invoke a method which does not belong to an remote object, an exception will be thrown and sent as a response to such a call.

> One could ask now, why we did not use the standard Scala @remote annotation, or, moreover, why we did use a Java annotation. Since scala annotations [are not visible during runtime](http://stackoverflow.com/questions/5177010/how-to-create-annotations-and-get-them-in-scala), we were forced to use a Java annotation with a special retention policy to get this done.

```
package s2js.compiler;
import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface remote
{

}
```

> As you can see, we need to explicitly set the RetentionPolicy to `RUNTIME` to make annotations visible for the RPC Dispatcher.


> On the second hand, this is more secure. We protect the standard scala @remote objects from being executed.

You can also notice the `paramTypes` field, which holds a JSON-serialized array of types of invokation paramters. This helps the RPC Dispatcher to parse the parameters and make them properly typed on the server. In the example, the `paramTypes:["java.lang.String"]` means that the first parameter will be parsed as String, which is the most simple case.

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

If you want to learn more about the RPC mechanism on the client-side, please, see the correspondent section in the [text about s2js.rpc package](#s2js_rpc).

<a name="client"></a>
### Package cz.payola.web.client

> TODO: O.K.

#### Package cz.payola.web.client.events

> TODO: O.K.

#### Package cz.payola.web.client.models

> TODO: O.K.

#### Package cz.payola.web.client.views

> TODO: O.K.

#### Package cz.payola.web.client.presenters

> TODO: O.K.