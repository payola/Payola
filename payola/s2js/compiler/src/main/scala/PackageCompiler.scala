package s2js.compiler

import tools.nsc.Global
import collection.mutable
import tools.nsc.io.AbstractFile

trait PackageCompiler {
    val global: Global

    import global._

    private var sourceFile: AbstractFile = null

    private val buffer = new mutable.ListBuffer[String]

    private val classDefMap = new mutable.HashMap[String, ClassDefCompiler]

    private val classDefDependencyGraph = new mutable.HashMap[String, mutable.HashSet[String]]

    private val requireHashSet = new mutable.HashSet[String]

    private val provideHashSet = new mutable.HashSet[String]

    private var uniqueId = 0

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
        """^scala\.ScalaObject$""",
        """^scala\.Serializable$""", // TODO maybe shoudn't be internal.
        """^scala\.package.*""",
        """^scala\.reflect\.ClassManifest$""",
        """^scala\.reflect\.Manifest$""",
        """^scala\.runtime$""",
        """^scala\.runtime\.AbstractFunction[0-9]+$""",
        """^scala\.xml"""
    )

    // Ordered by transformation priority (if one is a prefix of another, then the longer should be first).
    private val namespaceTransformationMap = Map(
        "java.lang" -> "scala",
        "scala.this" -> "scala",
        "s2js.adapters.js.browser" -> "",
        "s2js.adapters.js.dom" -> "",
        "s2js.adapters" -> "",
        "s2js.runtime" -> ""
    )

    private val internalMembers = Array(
        "hashCode",
        "equals",
        "canEqual",
        "readResolve"
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
        "constructor", "hasOwnProperty", "isPrototypeOf", "propertyIsEnumerable", "apply", "arguments", "call",
        "prototype", "superClass_", "metaClass_"
    )

    private val operatorTokenMap = Map(
        "eq" -> "===",
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

    private def getUniqueId(): Int = {
        uniqueId += 1
        uniqueId
    }

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

        // Perform the namespace transformation (use the longest matching namespace).
        val namespace = namespaceTransformationMap.keys.find(name.startsWith(_))
        if (namespace.nonEmpty) {
            name = name.stripPrefix(namespace.get)

            val newNamespace: String = namespaceTransformationMap.get(namespace.get).get
            if (newNamespace.isEmpty && name.startsWith(".")) {
                name = name.drop(1)
            }
            name = newNamespace + name
        }

        // Drop the "package" package that isn't used in JS.
        name.replace(".package", "")
    }

    private def getJsName(symbol: Symbol): String = {
        if (symbol.isLocal) getLocalJsName(symbol) else getFullJsName(symbol)
    }

    private def getLocalJsName(symbol: Symbol): String = {
        getLocalJsName(symbol.nameString, !symbol.isMethod && symbol.isSynthetic)
    }

    private def isFromSourceFile(symbol: Symbol): Boolean = {
        symbol.sourceFile != null && symbol.sourceFile.name == sourceFile.name
    }

    private def getLocalJsName(name: String, forcePrefix: Boolean = false): String = {
        // Synthetic symbols get a prefix to avoid name collision with other symbols. Also if the symbol name is a js
        // keyword then it gets the prefix.
        if (forcePrefix || jsKeywords.contains(name) || jsDefaultMembers.contains(name)) {
            "$" + name
        } else {
            name
        }
    }

    def compile(compiledSourceFile: AbstractFile, packageAst: PackageDef): String = {
        sourceFile = compiledSourceFile
        buffer.clear()
        classDefMap.clear()
        classDefDependencyGraph.clear()
        requireHashSet.clear()
        provideHashSet.clear()
        uniqueId = 0;

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

        if (isFromSourceFile(symbol)) {
            val classDefCompiler: ClassDefCompiler =
                if (classDef.symbol.isPackageObjectClass) {
                    new PackageObjectClassCompiler(classDef)
                } else if (classDef.symbol.isModuleClass) {
                    new ModuleClassCompiler(classDef)
                } else {
                    new ClassCompiler(classDef)
                }
            val name = getClassDefStructureName(classDef.symbol)
            val dependencies = new mutable.HashSet[String]

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
                        if (isFromSourceFile(parentClassSymbol)) {
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
        while (classDefDependencyGraph.nonEmpty && !classDefDependencyGraphContainsCycle) {
            val classDefStructName = classDefDependencyGraph.toArray.filter(_._2.isEmpty).map(_._1).sortBy(n => n).head

            // Compile the class.
            classDefMap.get(classDefStructName).get.compile()

            // Remove the compiled class from dependencies of all classDefs, that aren't compiled yet. Also remove the
            // compiled class from the working sets.
            classDefDependencyGraph.foreach(_._2 -= classDefStructName)
            classDefDependencyGraph.remove(classDefStructName)
            classDefMap.remove(classDefStructName)
        }

        // If there are some classDefs left, then there is a cyclic dependency.
        if (classDefDependencyGraph.nonEmpty) {
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

    private def bufferAppendNativeCodeOr(symbol: Symbol, otherwise: () => Unit) = {
        val nativeAnnotationInfo = symbol.annotations.find(_.atp.toString == "s2js.compiler.NativeJs")
        if (nativeAnnotationInfo.isDefined) {
            nativeAnnotationInfo.get.args.head match {
                case Literal(Constant(value: String)) => buffer += value
                case _ => otherwise()
            }
        } else {
            otherwise()
        }
    }

    private abstract class ClassDefCompiler(val classDef: ClassDef) {
        protected val fullNameSymbol = classDef.symbol

        protected lazy val fullName = getFullJsName(fullNameSymbol)

        protected lazy val memberContainerName = fullName;

        protected val parentClasses = classDef.impl.parents

        protected val parentClass = parentClasses.head

        protected val parentClassIsInternal = isInternalType(parentClass.symbol)

        protected val inheritedTraits = parentClasses.tail

        def compile() {
            addProvidedSymbol(fullNameSymbol)

            bufferAppendNativeCodeOr(classDef.symbol, () => internalCompile())
        }

        protected def internalCompile() {
            compileConstructor()
            compileMembers()
            compileMetaClass()
        }

        protected def compileConstructor()

        protected def compileInheritedTraits(extendedObject: String) {
            // Traits should be compiled in reverse order as mentioned in the specification of stackable modifications.
            inheritedTraits.filter(traitAst => !isInternalType(traitAst.symbol)).reverse.foreach {
                traitAst =>
                    buffer += "goog.object.extend(%s, new %s());\n".format(
                        extendedObject,
                        getFullJsName(traitAst.symbol)
                    )
            }
        }

        protected def compileMembers() {
            compileValDefMembers()
            compileDefDefMembers()
        }

        protected def compileValDefMembers() {
            classDef.impl.body.filter(_.isInstanceOf[ValDef]).foreach(compileMember(_))
        }

        protected def compileDefDefMembers() {
            classDef.impl.body.filter(_.isInstanceOf[DefDef]).foreach(compileMember(_))
        }

        protected def compileMember(memberAst: Tree, containerName: String = memberContainerName) {
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
                        error("Unknown member %s of type %s".format(memberAst.toString, memberAst.getClass))
                    }
                }
            }
        }

        protected def isIgnoredMember(member: Symbol): Boolean = {
            internalMembers.contains(member.nameString) || // An internal member
                isInternalTypeMember(member) || // A member inherited from an internal Type
                member.owner != classDef.symbol || // A member that isn't directly owned by the class
                member.isDeferred || // An abstract member without implementation
                member.isConstructor || // TODO support multiple constructors
                member.isParameter || // A parameter of a member method
                member.hasAccessorFlag || // A generated accesor method
                member.nameString.matches("""^.*\$default\$[0-9]+$""") // A member generated for default parameter value
        }

    protected def compileValDef(valDef: ValDef, containerName: String = memberContainerName) {
        buffer += "%s.%s = ".format(containerName, getLocalJsName(valDef.symbol))
        bufferAppendNativeCodeOr(valDef.symbol, () => compileAst(valDef.rhs))
        buffer += ";\n"
    }

    protected def compileDefDef(defDef: DefDef, containerName: String = memberContainerName) {
            buffer += "%s.%s = function(".format(containerName, getLocalJsName(defDef.symbol))
            compileParameterDeclaration(defDef.vparamss.flatten)
            buffer += ") {\nvar self = this;\n"
            compileParameterInitialization(defDef.vparamss.flatten)
            val hasReturn = hasReturnValue(defDef.tpt.symbol)
            bufferAppendNativeCodeOr(defDef.symbol, () => compileAstStatement(defDef.rhs, hasReturn))
            buffer += "};\n"
        }

        protected def compileParentCall(parameters: List[Tree], methodName: Option[Name] = None) {
            buffer += "goog.base(self"
            if (methodName.isDefined) {
                buffer += ", '" + getLocalJsName(methodName.get.toString) + "'"
            }
            if (parameters.nonEmpty) {
                buffer += ", "
                compileParameterValues(parameters, false)
            }
            buffer += ")"
        }

        protected def compileParameterDeclaration(parameters: List[ValDef]) {
            buffer += parameters.filter(p => !isVariadicType(p.tpt)).map(p => getLocalJsName(p.symbol)).mkString(", ")
        }

        protected def isVariadicType(typeAst: Tree): Boolean = {
            typeAst.toString.endsWith("*")
        }

        protected def compileParameterInitialization(parameters: List[ValDef]) {
            // Parameters with default values.
            parameters.filter(_.symbol.hasDefault).foreach {
                parameter =>
                    buffer += "if (typeof(%1$s) === 'undefined') { %1$s = ".format(getLocalJsName(parameter.symbol))
                    parameter.asInstanceOf[ValDef].rhs match {
                        case ident: Ident if ident.symbol.owner == parameter.symbol.owner => {
                            buffer += "self.%s".format(ident.symbol.nameString)
                        }
                        case x => compileAst(x)
                    }
                    buffer += "; }\n"
            }

            // Variadic parameter.
            parameters.filter(p => isVariadicType(p.tpt)).foreach {
                parameter => // TODO rather use Seq instead of array
                    addRequiredSymbol("scala.Array");
                    buffer += "var %s = scala.Array.fromNative(".format(getLocalJsName(parameter.symbol));

                    // In fact, the "arguments" JS variable only behaves like an array, but isn't an array. The
                    // following trick described on http://www.mennovanslooten.nl/blog/post/59 is used to turn it
                    // into an array that doesn't contain the normal named parameters.
                    buffer += "[].splice.call(arguments, %1$s, arguments.length - %1$s)".format(parameters.length - 1)
                    buffer += ");\n";
            }
        }

        protected def compileParameterValues(parameterValues: List[Tree], withParentheses: Boolean = true) {
            if (withParentheses) {
                buffer += "("
            }
            if (parameterValues.nonEmpty) {
                parameterValues.foreach {parameterValue =>
                // Default parameters are handled in the function body.
                    if (!parameterValue.hasSymbolWhich(_.name.toString.contains("$default$"))) {
                        parameterValue match {
                            case Block(_, expr) => compileAst(expr)
                            case _ => compileAst(parameterValue)
                        }
                        buffer += ", "
                    }
                }
                buffer.remove(buffer.length - 1)
            }
            if (withParentheses) {
                buffer += ")"
            }
        }

        protected def compileAst(ast: Tree, hasReturnValue: Boolean = false) {
            // A Block handles the return value itself so it has to be compiled besides all other ast types.
            if (ast.isInstanceOf[Block]) {
                compileBlock(ast.asInstanceOf[Block], hasReturnValue)

                // Other ast types don't handle return value themselves.
            } else {
                val compiledAstIndex = buffer.length;
                ast match {
                    case EmptyTree => buffer += "undefined"
                    case thisAst: This => compileThis(thisAst)
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
                    case ifAst: If => compileIf(ifAst)
                    case labelDef: LabelDef => compileLabelDef(labelDef)
                    case tryAst: Try => // TODO
                    case throwAst: Throw => compileThrow(throwAst)
                    case matchAst: Match => compileMatch(matchAst)
                    case _ => error("Not implemented AST of type %s: %s".format(ast.getClass, ast.toString))
                }

                // If the last statement should be returned, prepend it with the "return" keyword.
                if (hasReturnValue) {
                    buffer.update(compiledAstIndex, "return " + buffer(compiledAstIndex));
                }
            }
        }

        protected def compileAstStatement(ast: Tree, hasReturnValue: Boolean = false) {
            val previousBufferLength = buffer.length

            compileAst(ast, hasReturnValue)

            if (!ast.isInstanceOf[Block] && buffer.length > previousBufferLength) {
                buffer += ";\n"
            }
        }

        protected def compileBlock(block: Block, hasReturnValue: Boolean = false) {
            block.stats.foreach(compileAstStatement(_))
            compileAstStatement(block.expr, hasReturnValue)
        }

        protected def compileLiteral(literal: Literal) {
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
                    error("Non-constant literal " + literal.toString)
                }
            }
        }

        protected def compileThis(thisAst: This) {
            if (thisAst.hasSymbolWhich(_.fullName.toString.startsWith("scala"))) {
                buffer += thisAst.symbol.fullName.toString
            } else {
                buffer += "self"
            }
        }

        protected def compileIdentifier(identifier: Ident) {
            buffer += getJsName(identifier.symbol)
        }

        protected def compileLocalValDef(localValDef: ValDef) {
            buffer += "var %s = ".format(getLocalJsName(localValDef.symbol))
            compileAst(localValDef.rhs)
        }

        protected def compileFunction(function: Function) {
            // TODO maybe somehow merge it with defdef compilation.
            buffer += "function("
            compileParameterDeclaration(function.vparams)
            buffer += ") { "
            compileParameterInitialization(function.vparams)
            compileAstStatement(function.body, hasReturnValue(function.body.tpe.typeSymbol))
            buffer += " }"
        }

        protected def compileNew(constructorCall: New) {
            buffer += "new %s".format(getFullJsName(constructorCall.tpe.typeSymbol))
            addRequiredSymbol(constructorCall.tpe.typeSymbol)
        }

        protected def compileSelect(select: Select, isSubSelect: Boolean = false, isInsideApply: Boolean = false) {
            val subSelectToken = if (isSubSelect) "." else ""
            val nameString = getLocalJsName(select.name.toString)
            val name = if (nameString.endsWith("_$eq")) nameString.stripSuffix("_$eq") else nameString

            select match {
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
                case _ if namespaceTransformationMap.contains(select.toString) ||
                    namespaceTransformationMap.contains(select.qualifier.toString) => {
                    // It's just a sequence of packages (selects), so getFullJsName may be used.
                    val name = getFullJsName(select.symbol)
                    buffer += name + (if (name.isEmpty) "" else subSelectToken)
                }
                case Select(qualifier, _) => {
                    qualifier match {
                        case subSelect: Select => compileSelect(subSelect, true)
                        case _ => {
                            compileAst(qualifier)
                            buffer += "."
                        }
                    }

                    // If the select is actually a method call, parentheses has to be added after the name.
                    buffer += name
                    if (!isInsideApply && select.hasSymbolWhich(s => s.isMethod && !s.isGetter)) {
                        buffer += "()"
                    }
                    buffer += subSelectToken
                }
            }

            // TODO find better way how to determine whether the qualifier is an object
            Array(select, select.qualifier).foreach { ast =>
                if (ast.hasSymbolWhich(_.toString.startsWith("object "))) {
                    addRequiredSymbol(ast.symbol)
                }
            }
        }

        protected def compileApply(apply: Apply) {
            apply match {
                case Apply(Select(qualifier, name), args) if operatorTokenMap.contains(name.toString) => {
                    compileOperator(qualifier, Some(args.head), name.toString)
                }
                case Apply(select@Select(qualifier, name), args) if name.toString.endsWith("_$eq") => {
                    compileAssign(select, args.head)
                }
                case Apply(Select(superClass: Super, name), _) => {
                    compileParentCall(apply.args, Some(name))
                }
                case Apply(Select(qual, name), _) if name.toString == "apply" && qual.symbol.owner.isMethod => {
                    compileAst(qual)
                    compileParameterValues(apply.args)
                }
                case Apply(subApply: Apply, args) => {
                    // Apply of a function with multiple parameter lists.
                    // TODO maybe use cleaner way without altering the buffer.
                    compileApply(subApply);

                    // Add the additional parameters to the subApply method call.
                    // TODO introduce some non-ad-hoc solution for the ClassManifest problem.
                    val ignoreApply = args.isEmpty || args.head.toString.startsWith("reflect.this.ClassManifest")
                    if (!ignoreApply) {
                        buffer.update(buffer.length - 1, buffer.last.dropRight(1))
                        buffer += ", "
                        compileParameterValues(args, false)
                        buffer += ")"
                    }
                }
                case Apply(select: Select, _) if select.hasSymbolWhich(_.isMethod) => {
                    compileSelect(select, isInsideApply = true)
                    compileParameterValues(apply.args)
                }
                case _ => {
                    compileAst(apply.fun)
                    compileParameterValues(apply.args)
                }
            }
        }

        protected def compileOperator(firstOperand: Tree, secondOperand: Option[Tree], name: String) {
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

        protected def compileTypeApply(typeApply: TypeApply) {
            typeApply.fun match {
                case Select(qualifier, name) if name.toString == "isInstanceOf" || name.toString == "asInstanceOf" => {
                    typeApply.args.head.tpe match {
                        case uniqueTypeRef: UniqueTypeRef => {
                            val typeApplyType = name.toString.take(2)
                            compileInstanceOf(() => compileAst(qualifier), uniqueTypeRef.typeSymbol, typeApplyType)
                        }
                        case tpe => error("Unsupported type check/conversion: " + tpe.toString)
                    }
                }
                case select: Select => compileSelect(select, isInsideApply = true)
                case fun => compileAst(fun)
            }
        }

        protected def compileInstanceOf(objectQualifierCompiler: () => Unit, classSymbol: Symbol, prefix: String) {
            buffer += "s2js.%sInstanceOf(".format(prefix)
            objectQualifierCompiler()
            buffer += ", '%s')".format(getFullJsName(classSymbol))
        }

        protected def compileAssign(assign: Assign) {
            compileAssign(assign.lhs, assign.rhs)
        }

        protected def compileAssign(assignee: Tree, value: Tree) {
            assignee match {
                case select: Select => compileSelect(select, isInsideApply = true)
                case _ => compileAst(assignee)
            }
            buffer += " = "
            compileAst(value)
        }

        protected def compileIf(condition: If) {
            buffer += "(function() {\nif ("
            compileAst(condition.cond)
            buffer += ") {\n"
            compileAstStatement(condition.thenp, hasReturnValue(condition.tpe.typeSymbol))
            buffer += "} else {\n"
            compileAstStatement(condition.elsep, hasReturnValue(condition.tpe.typeSymbol))
            buffer += "}})()"
        }

        protected def compileLabelDef(labelDef: LabelDef) {
            labelDef.name match {
                case name if name.toString.startsWith("while") => {
                    /*
                        AST of a while cycle is transformed into a tail recursive function with AST similar to:
                        def while$1() {
                            if([while-condition]) {
                               [while-body];
                                while$1()
                            }
                        }
                    */
                    val If(cond, Block(body, _), _) = labelDef.rhs
                    buffer += "while("
                    compileAst(cond)
                    buffer += ") {\n"
                    compileAstStatement(body.head)
                    buffer += "}"
                }
                case _ => {
                    error("Unknown labelDef: " + labelDef.toString)
                }
            }
        }

        protected def compileThrow(throwAst: Throw) {
            buffer += "(function() {\nthrow "
            compileAstStatement(throwAst.expr)
            buffer += "})()"
        }

        protected def compileMatch(matchAst: Match) {
            val selectorName = getLocalJsName("selector_" + getUniqueId(), true)
            val hasReturn = hasReturnValue(matchAst.tpe.typeSymbol)
            buffer += "(function(%s) {\n".format(selectorName)
            matchAst.cases.foreach(caseDef => compileCase(caseDef, selectorName, hasReturn))
            buffer += "})("
            compileAst(matchAst.selector)
            buffer += ")"
        }

        protected def compileCase(caseDef: CaseDef, selectorName: String, hasReturn: Boolean) {
            buffer += "if ("
            compilePattern(caseDef.pat, selectorName)
            buffer += ") {\n"

            compileBindings(caseDef.pat, selectorName)

            val hasGuard = !caseDef.guard.isEmpty
            if (hasGuard) {
                buffer += "if ("
                compileAst(caseDef.guard)
                buffer += ") {\n"
            }

            compileAstStatement(caseDef.body, hasReturn)
            if (!hasReturn) {
                // If the execution gets here, the match function has to be terminated using "return" keyword even
                // though it shouldn't return anything. Because this case matches the selector, no other case should be
                // evaluated.
                buffer += "return;\n"
            }

            if (hasGuard) {
                buffer += "}\n"
            }
            buffer += "}\n"
        }

        protected def compilePattern(patternAst: Tree, selectorName: String) {
            patternAst match {
                case Ident(name) if name.toString == "_" => buffer += "true"
                case literal: Literal => compileLiteralPattern(literal, selectorName)
                case typed: Typed => compileTypedPattern(typed, selectorName)
                case bind: Bind => compilePattern(bind.body, selectorName)
                case select: Select => compileSelectPattern(select, selectorName)
                case apply: Apply => compileApplyPattern(apply, selectorName)
                case alternative: Alternative => compileAlternativePattern(alternative, selectorName)
                case _ => error("Not implemented pattern in case: %s".format(patternAst.toString))
            }
        }

        protected def compileLiteralPattern(literal: Literal, selectorName: String) {
            buffer += "%s === ".format(selectorName)
            compileLiteral(literal)
        }

        protected def compileTypedPattern(typed: Typed, selectorName: String) {
            compileInstanceOf(() => buffer += selectorName, typed.tpe.typeSymbol, "is")
        }

        protected def compileSelectPattern(select: Select, selectorName: String) {
            buffer += "%s === ".format(selectorName)
            compileAst(select)
        }

        protected def compileApplyPattern(apply: Apply, selectorName: String) {
            compileInstanceOf(() => buffer += selectorName, apply.tpe.typeSymbol, "is")
            buffer += " && "

            apply.args.zipWithIndex.foreach {
                case (argAst, index) =>
                    buffer += "("
                    compilePattern(argAst, "%s.productElement(%s)".format(selectorName, index))
                    buffer += ")"
                    buffer += " && "
            }
            buffer.remove(buffer.length - 1)
        }

        protected def compileAlternativePattern(alternative: Alternative, selectorName: String) {
            alternative.trees.foreach {subPatternAst =>
                buffer += "("
                compilePattern(subPatternAst, selectorName)
                buffer += ")"
                buffer += " || "
            }
            buffer.remove(buffer.length - 1)
        }

        protected def compileBindings(patternAst: Tree, selectorName: String) {
            patternAst match {
                case bind: Bind => {
                    buffer += "var %s = %s;\n".format(getLocalJsName(bind.name.toString), selectorName)
                }
                case apply: Apply => {
                    apply.args.zipWithIndex.foreach {
                        case (argAst, index) =>
                            compileBindings(argAst, "%s.productElement(%s)".format(selectorName, index))
                    }
                }
                case _ =>
            }
        }

        protected def compileMetaClass() {
            buffer += "%s.metaClass_ = new s2js.MetaClass('%s', [%s]);\n".format(
                memberContainerName,
                fullName,
                parentClasses.filter(c => !isInternalType(c.symbol)).map(p => getFullJsName(p.symbol)).mkString(", ")
            )
        }
    }

    private class ClassCompiler(classDef: ClassDef) extends ClassDefCompiler(classDef) {
        override lazy val memberContainerName = fullName + ".prototype"

        val initializedValDefSet = new mutable.HashSet[String]

        protected def compileConstructor() {
            val primaryConstructors = classDef.impl.body.filter(ast => ast.hasSymbolWhich(_.isPrimaryConstructor))
            val hasConstructor = primaryConstructors.nonEmpty
            val constructorDefDef = if (hasConstructor) Some(primaryConstructors.head.asInstanceOf[DefDef]) else None

            buffer += fullName + " = function(";
            if (constructorDefDef.nonEmpty) {
                compileParameterDeclaration(constructorDefDef.get.vparamss.flatten)
            }
            buffer += ") {\n"
            buffer += "var self = this;\n"

            if (constructorDefDef.nonEmpty) {
                compileParameterInitialization(constructorDefDef.get.vparamss.flatten)

                // Initialize fields specified as the implicit constructor parameters.
                constructorDefDef.get.vparamss.flatten.map(p => getLocalJsName(p.name.toString)).foreach {
                    parameterName =>
                        initializedValDefSet += parameterName
                        buffer += "self.%1$s = %1$s;\n".format(parameterName)
                }
            }

            // Initialize fields that aren't implicit constructor parameters.
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
                        compileParentCall(parameters)
                        buffer += ";"
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

        override protected def compileValDefMembers() {
            // NOOP, the fields are assigned in the constructor.
        }
    }

    private class ModuleClassCompiler(classDef: ClassDef) extends ClassDefCompiler(classDef) {
        override def compile() {
            val compilableMembers = classDef.impl.body.filter {m =>
                (m.isInstanceOf[ValDef] || m.isInstanceOf[DefDef]) && m.hasSymbolWhich(!isIgnoredMember(_))
            }

            // A synthetic singleton object is compiled only if it has some members that should be compiled.
            if (!classDef.symbol.isSynthetic || compilableMembers.nonEmpty) {
                super.compile()
            }
        }

        protected def compileConstructor() {
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
            addRequiredSymbol(getFullJsName(symbol))
        }
    }

    private def addRequiredSymbol(symbolFullName: String) {
        requireHashSet.add(symbolFullName)
    }
}
