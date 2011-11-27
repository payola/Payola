package s2js.compiler

import tools.nsc.Global
import collection.mutable.{HashMap, HashSet, ListBuffer}

trait PackageCompiler {
    val global: Global

    import global._

    private val buffer = new ListBuffer[String]

    private val classDefMap = new HashMap[String, ClassDefCompiler]

    private val classDefDependencyGraph = new HashMap[String, HashSet[String]]

    private val requireHashSet = new HashSet[String]

    private val provideHashSet = new HashSet[String]

    private val internalTypes = Array(
        """^java\.lang$""",
        """^java\.lang\.Object$""",
        """^s2js\.adapters\.js\.browser.*$""",
        """^s2js\.adapters\.js\.dom.*$""",
        """^scala\.Any$""",
        """^scala\.AnyRef""",
        """^scala\.Boolean$""",
        """^scala\.Equals$""",
        """^scala\.Function[0-9]+$""", // TODO maybe shoudn't be internal.
        """^scala\.Int$""",
        """^scala\.Predef$""",
        """^scala\.Product$""",
        """^scala\.ScalaObject$""",
        """^scala\.Serializable""", // TODO maybe shoudn't be internal.
        """^scala\.Tuple[0-9]+$""", // TODO maybe shoudn't be internal.
        """^scala\.package$""",
        """^scala\.reflect\.Manifest$""",
        """^scala\.runtime$""",
        """^scala\.runtime\.AbstractFunction[0-9]+$""",
        """^scala\.xml"""
    )

    private val jsAdapterPackages = Array(
        "s2js.adapters",
        "s2js.adapters.js.browser",
        "s2js.adapters.js.dom"
    )

    private val jsKeywords = Array(
        "abstract", "boolean", "break", "byte", "case", "catch", "char", "class", "const", "continue", "debugger",
        "default", "delete", "do", "double", "else", "enum", "export", "extends", "false", "final", "finally", "float",
        "for", "function", "goto", "if", "implements", "import", "in", "instanceof", "int", "interface", "long",
        "native", "new", "null", "package", "private", "protected", "public", "return", "short", "static", "super",
        "switch", "synchronized", "this", "throw", "throws", "transient", "true", "try", "typeof", "var", "void",
        "volatile", "while", "with"
    )

    private val jsDefaultMembers = Array(
        "constructor", "hasOwnProperty", "isPrototypeOf", "propertyIsEnumerable", "toLocaleString", "valueOf", "apply",
        "arguments", "call", "caller", "length", "prototype", "superClass_"
    )

    private val operatorTokenMap = Map(
        "$eq$eq" -> "==",
        "$bang$eq" -> "!=",
        "$greater" -> ">",
        "$greater$eq" -> ">=",
        "$less" -> "<",
        "$less$eq" -> "<=",
        "$amp$amp" -> "&&",
        "$plus" -> "+",
        "$minus" -> "-",
        "$times" -> "*",
        "$div" -> "/",
        "$percent" -> "%",
        "$bar$bar" -> "||",
        "unary_$bang" -> "!"
    )

    private def isInternalType(symbol: Symbol): Boolean = {
        internalTypes.exists(symbol.fullName.matches(_))
    }

    private def isInternalTypeMember(symbol: Symbol): Boolean = {
        isInternalType(symbol.enclClass)
    }

    private def hasReturnValue(symbol: Symbol): Boolean = {
        symbol.nameString != "Unit"
    }

    private def getJsString(value: String): String = {
        "'" + value.replace("\\", "\\\\").replace("'", "\\'") + "'"
    }

    private def getFullJsName(symbol: Symbol): String = {
        var name = symbol.fullName;

        // Drop the internal namespace name.
        val jsInternalNamespace = jsAdapterPackages.find(name.startsWith(_))
        if (jsInternalNamespace.isDefined) {
            name = name.stripPrefix(jsInternalNamespace.get + ".")
        }

        // Drop the "package" package that isn't used in JS.
        name.replace(".package", "")
    }

    private def getLocalJsName(symbol: Symbol): String = {
        getLocalJsName(symbol.nameString, symbol.isSynthetic)
    }

    private def getLocalJsName(name: String, forceSuffix: Boolean = false): String = {
        // Synthetic symbols get a suffix to avoid name collision with other symbols. Also if the symbol name is a js
        // keyword then it gets the suffix.
        if (forceSuffix || jsKeywords.contains(name) || jsDefaultMembers.contains(name)) {
            name + "_s2js"
        } else {
            name
        }
    }

    def compile(packageAst: PackageDef): String = {
        retrievePackageStructure(packageAst)
        compileClassDefs()
        compileDependencies()

        buffer.mkString
    }

    private def retrieveStructure(ast: Tree) {
        ast match {
            case packageDef: PackageDef => {
                retrievePackageStructure(packageDef)
            }
            case classDef: ClassDef => {
                retrieveClassStructure(classDef)
            }
            case _ =>
        }
    }

    private def retrievePackageStructure(packageDef: PackageDef) {
        // Retrieve structure of child items.
        packageDef.children.foreach(retrieveStructure)
    }

    private def retrieveClassStructure(classDef: ClassDef) {
        val symbol = classDef.symbol

        if (!symbol.isSynthetic) {
            val classDefCompiler: ClassDefCompiler =
                if (classDef.symbol.isPackageObjectClass) {
                    new PackageObjectClassCompiler(classDef)
                } else if (classDef.symbol.isModuleClass) {
                    new ModuleClassCompiler(classDef)
                } else {
                    new ClassCompiler(classDef)
                }
            val name = getClassDefStructureName(classDef.symbol)
            val dependencies = new HashSet[String]

            classDefMap += name -> classDefCompiler
            classDefDependencyGraph += name -> dependencies

            // If a class is declared inside another class or object, then it depends on the another class/object.
            if (!symbol.owner.isPackageClass) {
                dependencies += symbol.owner.fullName
            }

            // The class depends on parent classes that are defined in the same file.
            classDef.impl.parents.foreach {
                parentClassAst =>
                    val parentClassSymbol = parentClassAst.symbol
                    if (!isInternalType(parentClassSymbol)) {
                        if (symbol.sourceFile.name == parentClassSymbol.sourceFile.name) {
                            dependencies += getClassDefStructureName(parentClassSymbol)
                        } else {
                            addRequiredSymbol(parentClassAst.symbol)
                        }
                    }
            }

            // Retrieve structure of child items.
            classDef.impl.body.foreach(retrieveStructure)
        }
    }

    private def getClassDefStructureName(classDefSymbol: Symbol): String = {
        (if (classDefSymbol.isModuleClass) "object" else "class") + " " + classDefSymbol.fullName
    }

    private def compileClassDefs() {
        while (!classDefDependencyGraph.isEmpty && !classDefDependencyGraphContainsCycle) {
            val structureName = classDefDependencyGraph.toArray.filter(_._2.isEmpty).map(_._1).sortBy(n => n).head

            // Compile the class.
            classDefMap.get(structureName).get.compile()

            // Remove the compiled class from dependencies of all classDefs, that aren't compiled yet. Also remove the
            // compiled class from the working sets.
            classDefDependencyGraph.foreach(_._2 -= structureName)
            classDefDependencyGraph.remove(structureName)
            classDefMap.remove(structureName)
        }

        // If there are some classDefs left, then there is a cyclic dependency.
        if (!classDefDependencyGraph.isEmpty) {
            error("Illegal cyclic dependency in the class/object declaration graph.")
        }
    }

    private def classDefDependencyGraphContainsCycle: Boolean = {
        if (classDefDependencyGraph.isEmpty) {
            false
        } else {
            // If there isn't a classDef without dependencies, then starting in any ClassDef, sooner or later we arrive
            // to an already visited.
            !classDefDependencyGraph.exists(_._2.isEmpty)
        }
    }

    private abstract class ClassDefCompiler(val classDef: ClassDef) {
        val fullNameSymbol = classDef.symbol

        lazy val fullName = getFullJsName(fullNameSymbol)

        lazy val memberContainerName = fullName;

        val parentClasses = classDef.impl.parents

        val parentClass = parentClasses.head

        val parentClassIsInternal = isInternalType(parentClass.symbol)

        val inheritedTraits = parentClasses.tail

        def compile() {
            compileConstructor()

            addProvidedSymbol(fullNameSymbol)
        }

        def compileConstructor()

        def compileInheritedTraits(extendedObject: String) {
            inheritedTraits.filter(traitAst => !isInternalType(traitAst.symbol)).foreach {
                traitAst =>
                    buffer += "goog.object.extend(%s, new %s());\n"
                        .format(extendedObject, getFullJsName(traitAst.symbol))
            }
        }

        def compileMembers() {
            compileValDefMembers()
            compileDefDefMembers()
        }

        def compileValDefMembers() {
            classDef.impl.body.filter(_.isInstanceOf[ValDef]).foreach(compileMember(_))
        }

        def compileDefDefMembers() {
            classDef.impl.body.filter(_.isInstanceOf[DefDef]).foreach(compileMember(_))
        }

        def compileMember(memberAst: Tree, containerName: String = memberContainerName) {
            if (memberAst.hasSymbolWhich(!isIgnoredMember(_))) {
                memberAst match {
                    case valDef: ValDef => {
                        compileValDef(valDef, containerName)
                    }
                    case defDef: DefDef => {
                        compileDefDef(defDef, containerName)
                    }
                    case _: ClassDef => // NOOP, wrapped classes are compiled separately
                    case _ => {
                        buffer += "/* [s2js-warning] Unknown member %s of type %s */".format(
                            memberAst.toString,
                            memberAst.getClass
                        )
                    }
                }
            }
        }

        def isIgnoredMember(member: Symbol): Boolean = {
            isInternalTypeMember(member) || // A member inherited from an internal Type
                member.owner != classDef.symbol || // A member that isn't directly owned by the class
                member.isSynthetic || // An artificial member that was created by the compiler
                member.isDeferred || // An abstract member without implementation
                member.isConstructor || // TODO support multiple constructors
                member.isParameter || // A parameter of a member method
                member.hasAccessorFlag // A generated accessor method
        }

        def compileValDef(valDef: ValDef, containerName: String = memberContainerName) {
            buffer += "%s.%s = ".format(containerName, getLocalJsName(valDef.symbol))
            compileAstStatement(valDef.rhs)
        }

        def compileDefDef(defDef: DefDef, containerName: String = memberContainerName) {
            buffer += "%s.%s = function(".format(containerName, getLocalJsName(defDef.symbol))
            compileParameterDeclaration(defDef.vparamss.flatten)
            buffer += ") {\nvar self = this;\n"
            compileDefaultParameters(defDef.vparamss.flatten)
            compileAstStatement(defDef.rhs, hasReturnValue(defDef.tpt.symbol))
            buffer += "};\n"
        }

        def compileSelfCall(parameters: List[Tree]) {
            buffer += "call(self"
            if (!parameters.isEmpty) {
                buffer += ", "
                compileParameterValues(parameters, false)
            }
            buffer += ")"
        }

        def compileParameterDeclaration(parameters: List[ValDef]) {
            buffer += parameters.map(parameter => getLocalJsName(parameter.symbol)).mkString(", ")
        }

        def compileDefaultParameters(parameters: List[ValDef]) {
            parameters.filter(_.symbol.hasDefault).foreach {
                parameter =>
                    buffer += "if (typeof(%1$s) === 'undefined') { %1$s = ".format(getLocalJsName(parameter.symbol))
                    compileAst(parameter.asInstanceOf[ValDef].rhs)
                    buffer += "; }\n"
            }
        }

        def compileParameterValues(parameterValues: List[Tree], withParentheses: Boolean = true) {
            if (withParentheses) {
                buffer += "("
            }
            if (!parameterValues.isEmpty) {
                parameterValues.foreach {
                    parameterValue =>
                    // Default parameters are handled in the function body.
                        if (!parameterValue.hasSymbolWhich(_.name.toString.contains("$default$"))) {
                            parameterValue match {
                                case Block(_, expr) => compileAst(expr)
                                case _ => compileAst(parameterValue)
                            }
                            buffer += ", "
                        }
                }
                buffer.update(buffer.length - 1, buffer.last.dropRight(2))
            }
            if (withParentheses) {
                buffer += ")"
            }
        }

        def compileAst(ast: Tree, hasReturnValue: Boolean = false) {
            // A Block handles the return value itself so it has to be compiled besides all other ast types.
            if (ast.isInstanceOf[Block]) {
                compileBlock(ast.asInstanceOf[Block], hasReturnValue)
                // Other ast types don't handle return value themselves.
            } else {
                val compiledAstIndex = buffer.length;
                ast match {
                    case EmptyTree => buffer += "undefined"
                    case This(_) => buffer += "self"
                    case Return(expr) => compileAst(expr)
                    case literal: Literal => compileLiteral(literal)
                    case identifier: Ident => compileIdentifier(identifier)
                    case valDef: ValDef if valDef.symbol.isLocal => compileLocalValDef(valDef)
                    case function: Function => compileFunction(function)
                    case constructorCall: New => compileNew(constructorCall)
                    case select: Select => compileSelect(select)
                    case apply: Apply => compileApply(apply)
                    case typeApply: TypeApply => compileTypeApply(typeApply)
                    case assign: Assign => compileAssign(assign)
                    case ifStatement: If => compileIf(ifStatement)
                    case labelDef: LabelDef => compileLabelDef(labelDef)
                    case tryStatement: Try => // TODO
                    case matchStatement: Match => // TODO
                    case _ => {
                        buffer += "/* [s2js-warning] Not implemented AST of type %s: %s */".format(
                            ast.getClass,
                            ast.toString
                        )
                    }
                }

                // If the last statement should be returned, prepend it with the "return" keyword.
                if (hasReturnValue) {
                    buffer.update(compiledAstIndex, "return " + buffer(compiledAstIndex));
                }
            }
        }

        def compileAstStatement(ast: Tree, hasReturnValue: Boolean = false) {
            val previousBufferLength = buffer.length

            compileAst(ast, hasReturnValue)

            if (!ast.isInstanceOf[Block] && buffer.length > previousBufferLength) {
                buffer += ";\n"
            }
        }

        def compileBlock(block: Block, hasReturnValue: Boolean = false) {
            block.stats.foreach(compileAstStatement(_))
            compileAstStatement(block.expr, hasReturnValue)
        }

        def compileLiteral(literal: Literal) {
            literal match {
                case Literal(Constant(value)) => {
                    value match {
                        case _: Unit => // NOOP
                        case null => buffer += "null"
                        case _: String | _: Char => buffer += getJsString(value.toString)
                        case _ => buffer += value.toString
                    }
                }
                case _ => {
                    buffer += "/* [s2js-warning] Non-constant literal %s */".format(literal.toString)
                }
            }
        }

        def compileIdentifier(identifier: Ident) {
            buffer += (
                if (identifier.symbol.isLocal) {
                    getLocalJsName(identifier.symbol)
                } else {
                    getFullJsName(identifier.symbol)
                })
        }

        def compileLocalValDef(localValDef: ValDef) {
            buffer += "var %s = ".format(getLocalJsName(localValDef.symbol))
            compileAst(localValDef.rhs)
        }

        def compileFunction(function: Function) {
            // TODO maybe somehow merge it with defdef compilation.
            buffer += "function("
            compileParameterDeclaration(function.vparams)
            buffer += ") { "
            compileDefaultParameters(function.vparams)
            compileAstStatement(function.body, hasReturnValue(function.body.tpe.typeSymbol))
            buffer += " }"
        }

        def compileNew(constructorCall: New) {
            buffer += "new %s".format(getFullJsName(constructorCall.tpt.symbol))
            addRequiredSymbol(constructorCall.tpt.symbol)
        }

        def compileSelect(select: Select, isSubSelect: Boolean = false) {
            val subSelectToken = if (isSubSelect) "." else ""

            if (!jsAdapterPackages.contains(select.toString)) {
                val nameString = getLocalJsName(select.name.toString)
                val name = if (nameString.endsWith("_$eq")) nameString.stripSuffix("_$eq") else nameString

                select match {
                    case _ if select.toString == "scala.this.Predef" => {
                        buffer += "scala.Predef" + subSelectToken
                    }
                    case Select(qualifier, _) if select.name.toString == "<init>" => {
                        compileAst(qualifier)
                    }
                    case Select(subSelect: Select, _) if select.name.toString == "package" => {
                        // Delegate the compilation to the subSelect and don't do anything with the subSelectToken.
                        compileSelect(subSelect, isSubSelect)
                    }
                    case Select(qualifier, _) if operatorTokenMap.contains(name) => {
                        compileOperator(qualifier, None, name)
                    }
                    case Select(subSelect: Select, _) => {
                        compileSelect(subSelect, true)
                        buffer += name + subSelectToken
                    }
                    case Select(qualifier, _) => {
                        compileAst(qualifier)
                        buffer += "." + name + subSelectToken
                    }
                }

                // TODO find better way how to determine whether the qualifier is an object
                if (select.qualifier.hasSymbolWhich(_.toString.startsWith("object "))) {
                    addRequiredSymbol(select.qualifier.symbol)
                }
            }
        }

        def compileSubSelect(subSelect: Select) {
            if (!jsAdapterPackages.contains(subSelect.toString)) {
                compileSelect(subSelect)
            }
        }

        def compileApply(apply: Apply) {
            apply match {
                case Apply(Select(qualifier, name), args) if operatorTokenMap.contains(name.toString) => {
                    compileOperator(qualifier, Some(args.head), name.toString)
                }
                case Apply(select@Select(qualifier, name), args) if name.toString.endsWith("_$eq") => {
                    compileAssign(select, args.head)
                }
                case Apply(Select(superClass: Super, name), _) => {
                    buffer += getFullJsName(superClass.symbol) + ".superClass_." + getLocalJsName(name.toString) + "."
                    compileSelfCall(apply.args)
                }
                case Apply(Select(qual, name), _) if name.toString == "apply" && qual.symbol.owner.isMethod => {
                    compileAst(qual)
                    compileParameterValues(apply.args)
                }
                case Apply(subApply: Apply, args) => {
                    // TODO maybe use cleaner way without altering the buffer.
                    compileApply(subApply);

                    // Add the additional parameters to the subApply method call.
                    if (!args.isEmpty) {
                        buffer.update(buffer.length - 1, buffer.last.dropRight(1))
                        buffer += ", "
                        compileParameterValues(args, false)
                        buffer += ")"
                    }
                }
                case _ => {
                    compileAst(apply.fun)
                    compileParameterValues(apply.args)
                }
            }
        }

        def compileOperator(firstOperand: Tree, secondOperand: Option[Tree], name: String) {
            buffer += "("
            if (name.startsWith("unary_")) {
                buffer += operatorTokenMap(name) + " "
                compileAst(firstOperand)
            } else {
                compileAst(firstOperand)
                buffer += " " + operatorTokenMap(name) + " "
                compileAst(secondOperand.get)
            }
            buffer += ")"
        }

        def compileTypeApply(typeApply: TypeApply) {
            typeApply.fun match {
                case Select(qualifier, name) if name.toString == "asInstanceOf" => {
                    compileAst(qualifier)
                }
                case fun => {
                    compileAst(fun)
                }
            }
        }

        def compileAssign(assign: Assign) {
            compileAssign(assign.lhs, assign.rhs)
        }

        def compileAssign(assignee: Tree, value: Tree) {
            compileAst(assignee)
            buffer += " = "
            compileAst(value)
        }

        def compileIf(condition: If) {
            buffer += "(function() {\nif ("
            compileAst(condition.cond)
            buffer += ") {\n"
            compileAstStatement(condition.thenp, hasReturnValue(condition.tpe.typeSymbol))
            buffer += "} else {\n"
            compileAstStatement(condition.elsep, hasReturnValue(condition.tpe.typeSymbol))
            buffer += "}})()"
        }

        def compileLabelDef(labelDef: LabelDef) {
            labelDef.name match {
                case name if name.toString.startsWith("while") => {
                    // AST of a while cycle is transformed into a tail recursive function with AST similar to:
                    // def while$1() {
                    //     if([while-condition]) {
                    //         [while-body];
                    //         while$1()
                    //     }
                    // }
                    val If(cond, Block(body, _), _) = labelDef.rhs
                    buffer += "while("
                    compileAst(cond)
                    buffer += ") {\n"
                    compileAstStatement(body.head)
                    buffer += "}"
                }
                case _ => {
                    buffer += "/* [s2js-warning] Unknown labelDef %s */".format(labelDef.toString)
                }
            }
        }
    }

    private class ClassCompiler(classDef: ClassDef) extends ClassDefCompiler(classDef) {
        override lazy val memberContainerName = fullName + ".prototype"

        val initializedValDefSet = new HashSet[String]

        override def compile() {
            super.compile()

            compileDefDefMembers()
        }

        def compileConstructor() {
            val primaryConstructors = classDef.impl.body.filter(ast => ast.hasSymbolWhich(_.isPrimaryConstructor))
            val hasConstructor = !primaryConstructors.isEmpty
            val constructorDefDef = if (hasConstructor) primaryConstructors.head.asInstanceOf[DefDef] else null

            buffer += "%s = function(".format(fullName);
            compileParameterDeclaration(constructorDefDef.vparamss.flatten)
            buffer += ") {\nvar self = this;\n"

            if (hasConstructor) {
                compileDefaultParameters(constructorDefDef.vparamss.flatten)

                // Initialize vals specified as the implicit constructor parameters.
                constructorDefDef.vparamss.flatten.map(p => getLocalJsName(p.name.toString)).foreach {
                    parameterName =>
                        initializedValDefSet += parameterName
                        buffer += "self.%1$s = %1$s;\n".format(parameterName)
                }
            }

            // Initialize vals that aren't implicit constructor parameters.
            classDef.impl.foreach {
                case valDef: ValDef if !initializedValDefSet.contains(getLocalJsName(valDef.symbol)) => {
                    compileMember(valDef, "self")
                }
                case _ =>
            }

            // Call the parent class constructor.
            if (!parentClassIsInternal) {
                classDef.impl.foreach {
                    case Apply(Select(Super(_, _), name), parameters) if (name.toString == "<init>") => {
                        buffer += "%s.".format(getFullJsName(parentClass.symbol))
                        compileSelfCall(parameters)
                        buffer += ";\n"
                    }
                    case _ =>
                }
            }

            compileInheritedTraits("self");

            // Compile the constructor body.
            classDef.impl.body.filter(!_.isInstanceOf[ValOrDefDef]) foreach {
                ast =>
                    compileAst(ast)
                    buffer += ";\n"
            }

            buffer += "};\n"

            if (!parentClassIsInternal) {
                buffer += "goog.inherits(%s, %s);\n".format(fullName, getFullJsName(parentClass.symbol))
            }
        }
    }

    private class ModuleClassCompiler(classDef: ClassDef) extends ClassDefCompiler(classDef) {
        override def compile() {
            super.compile()

            compileMembers()
        }

        def compileConstructor() {
            // Define the object if it isn't already defined.
            buffer += "if (typeof(%1$s) === 'undefined') { %1$s = {}; }\n".format(fullName)

            if (!parentClassIsInternal) {
                // Because the object may be a package object or a companion object, the members that already exist
                // there need to be preserved.
                buffer += "goog.object.extend(%s, ".format(fullName)
                classDef.impl.foreach {
                    case Apply(Select(Super(_, _), name), args) if (name.toString == "<init>") => {
                        buffer += "new " + getFullJsName(parentClass.symbol)
                        compileParameterValues(args)
                    }
                    case _ =>
                }
                buffer += ");\n"
            }

            // Inherit the traits.
            compileInheritedTraits(fullName);
        }
    }

    private class PackageObjectClassCompiler(classDef: ClassDef) extends ModuleClassCompiler(classDef) {
        override val fullNameSymbol = classDef.symbol.owner
    }

    private def compileDependencies() {
        val nonLocalRequires = requireHashSet.toArray.filter(r => !provideHashSet.contains(r))
        buffer.insert(0, provideHashSet.toArray.sortBy(x => x).map("goog.provide('%s');\n".format(_)).mkString)
        buffer.insert(1, nonLocalRequires.sortBy(x => x).map("goog.require('%s');\n".format(_)).mkString)
    }

    private def addProvidedSymbol(symbol: Symbol) {
        provideHashSet.add(getFullJsName(symbol))
    }

    private def addRequiredSymbol(symbol: Symbol) {
        if (!isInternalType(symbol)) {
            requireHashSet.add(getFullJsName(symbol))
        }
    }
}