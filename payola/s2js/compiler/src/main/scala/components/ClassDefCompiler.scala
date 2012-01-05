package s2js.compiler.components

import s2js.compiler.ScalaToJsException
import scala.tools.nsc.Global
import scala.collection.mutable

/** A factory for ClassDefCompiler objects. */
object ClassDefCompiler
{
    /**
      * Creates a ClassDefCompiler object corresponding to the specified ClassDef.
      * @param packageDefCompiler The compiler of the package which contains the class.
      * @param classDef The ClassDef of the class.
      * @return The ClassDefCompiler object.
      */
    def apply(packageDefCompiler: PackageDefCompiler, classDef: Global#ClassDef): ClassDefCompiler = {
        if (classDef.symbol.isPackageObjectClass) {
            new PackageObjectCompiler(packageDefCompiler, classDef)
        } else if (classDef.symbol.isModuleClass) {
            new ObjectCompiler(packageDefCompiler, classDef)
        } else {
            new ClassCompiler(packageDefCompiler, classDef)
        }
    }
}

/** A compiler of a ClassDef. */
abstract class ClassDefCompiler(val packageDefCompiler: PackageDefCompiler, val classDef: Global#ClassDef)
{

    import packageDefCompiler.global._

    /** Full name of the JavaScript object that corresponds to the ClassDef. */
    protected lazy val fullJsName = getJsName(classDef.symbol)

    /** Full name of the JavaScript object that should contains members (fields, methods) of the ClassDef. */
    protected val memberContainerName = fullJsName;

    /** Parent class and inherited traits (doesn't contain internal classes). */
    protected val predecessors = classDef.impl.parents.filter(c => !packageDefCompiler.symbolIsInternal(c.symbol))

    /** The parent class. */
    protected val parentClass = predecessors.headOption

    /** The inherited traits. */
    protected val inheritedTraits = if (predecessors.nonEmpty) predecessors.tail else Nil

    /** The ValDef or DefDef members. */
    protected val valOrDefDefs = classDef.impl.body.filter(_.isInstanceOf[Global#ValOrDefDef])

    /** The ValDef members. */
    protected val valDefs = valOrDefDefs.filter(_.isInstanceOf[Global#ValDef])

    /** The DefDef members. */
    protected val defDefs = valOrDefDefs.filter(_.isInstanceOf[Global#DefDef])

    /** The constructor DefDef objects. */
    protected val constructors = classDef.impl.body.filter(_.hasSymbolWhich(_.isPrimaryConstructor))

    /** The first and currently the only used constructor. TODO support multiple constructors. */
    protected val constructorDefDef = constructors.headOption.map(_.asInstanceOf[Global#DefDef])

    private var uniqueId = 0

    protected var buffer: mutable.ListBuffer[String] = null

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

    private def symbolIsOperator(symbol: Symbol): Boolean = {
        val symbolName = symbol.name.toString
        operatorTokenMap.contains(symbolName) &&
            (typeIsPrimitive(symbol.owner.tpe) || symbolName == "eq" || symbolName == "$eq$eq")
    }

    private def getUniqueId(): Int = {
        uniqueId += 1
        uniqueId
    }

    private def isInternalTypeMember(symbol: Global#Symbol): Boolean = {
        packageDefCompiler.symbolIsInternal(symbol.enclClass)
    }

    private def hasReturnValue(symbol: Global#Symbol): Boolean = {
        symbol.nameString != "Unit"
    }

    private def getJsString(value: String): String = {
        "'" + value.replace("\\", "\\\\").replace("'", "\\'") + "'"
    }

    protected def getJsName(symbol: Global#Symbol): String = {
        if (symbol.isLocal) getLocalJsName(symbol) else packageDefCompiler.getSymbolJsFullName(symbol)
    }

    protected def getLocalJsName(symbol: Global#Symbol): String = {
        getLocalJsName(symbol.name.toString.trim, !symbol.isMethod && symbol.isSynthetic)
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

    private def bufferAppendNativeCodeOr(symbol: Global#Symbol, otherwise: () => Unit) {
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

    def compile(buffer: mutable.ListBuffer[String]) {
        this.buffer = buffer

        bufferAppendNativeCodeOr(classDef.symbol, () => internalCompile())
    }

    protected def internalCompile() {
        val parentConstructorCall: Option[Global#Apply] = constructorDefDef.flatMap {constructor =>
            constructor.rhs.children.collect {
                case apply@Apply(Select(Super(_, _), name), _) if name.toString == "<init>" => apply
            }.headOption
        }

        compileConstructor(parentConstructorCall)
        compileMembers()
        compileMetaClass()
    }

    protected def compileConstructor(parentConstructorCall: Option[Global#Apply])

    protected def compileInheritedTraits(extendedObject: String) {
        // Traits should be compiled in reverse order as mentioned in the specification of stackable modifications.
        inheritedTraits.reverse.foreach {traitAst =>
            buffer += "goog.object.extend(%s, new %s());\n".format(extendedObject, getJsName(traitAst.symbol))
        }
    }

    protected def compileMembers() {
        valDefs.foreach(compileMember(_))
        defDefs.foreach(compileMember(_))
    }

    protected def compileMember(memberAst: Global#Tree, containerName: String = memberContainerName) {
        if (memberAst.hasSymbolWhich(!isIgnoredMember(_))) {
            memberAst match {
                case valDef: Global#ValDef => compileValDef(valDef, containerName)
                case defDef: Global#DefDef => compileDefDef(defDef, containerName)
                case _: Global#ClassDef => // NOOP, wrapped classes are compiled separately
                case _ => {
                    throw new ScalaToJsException("Unknown member %s of type %s".format(
                        memberAst.toString,
                        memberAst.getClass
                    ))
                }
            }
        }
    }

    protected def isIgnoredMember(member: Global#Symbol): Boolean = {
        internalMembers.contains(member.nameString) || // An internal member
            isInternalTypeMember(member) || // A member inherited from an internal Type
            member.owner != classDef.symbol || // A member that isn't directly owned by the class
            member.isDeferred || // An abstract member without implementation
            member.isConstructor || // TODO support multiple constructors
            member.isParameter || // A parameter of a member method
            member.hasAccessorFlag || // A generated accesor method
            member.nameString.matches("""^.*\$default\$[0-9]+$""") // A member generated for default parameter value
    }

    protected def compileValDef(valDef: Global#ValDef, containerName: String = memberContainerName) {
        val n = getLocalJsName(valDef.symbol)
        buffer += "%s.%s = ".format(containerName, n)
        bufferAppendNativeCodeOr(valDef.symbol, () => compileAst(valDef.rhs))
        buffer += ";\n"
    }

    protected def compileDefDef(defDef: Global#DefDef, containerName: String = memberContainerName) {
        buffer += "%s.%s = function(".format(containerName, getLocalJsName(defDef.symbol))
        compileParameterDeclaration(defDef.vparamss.flatten)
        buffer += ") {\nvar self = this;\n"
        compileParameterInitialization(defDef.vparamss.flatten)
        val hasReturn = hasReturnValue(defDef.tpt.symbol)
        bufferAppendNativeCodeOr(defDef.symbol, () => compileAstStatement(defDef.rhs, hasReturn))
        buffer += "};\n"
    }

    protected def compileParentCall(parameters: List[Global#Tree], methodName: Option[Global#Name] = None) {
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

    protected def compileParameterDeclaration(parameters: List[Global#ValDef]) {
        buffer += parameters.filter(p => !isVariadicType(p.tpt)).map(p => getLocalJsName(p.symbol)).mkString(", ")
    }

    protected def isVariadicType(typeAst: Global#Tree): Boolean = {
        typeAst.toString.endsWith("*")
    }

    protected def compileParameterInitialization(parameters: List[Global#ValDef]) {
        // Parameters with default values.
        parameters.filter(_.symbol.hasDefault).foreach {parameter =>
            buffer += "if (typeof(%1$s) === 'undefined') { %1$s = ".format(getLocalJsName(parameter.symbol))
            parameter.asInstanceOf[Global#ValDef].rhs match {
                case ident: Global#Ident if ident.symbol.owner == parameter.symbol.owner => {
                    buffer += "self.%s".format(ident.symbol.nameString)
                }
                case x => compileAst(x)
            }
            buffer += "; }\n"
        }

        // Variadic parameter.
        parameters.filter(p => isVariadicType(p.tpt)).foreach {parameter => // TODO rather use Seq instead of array
            packageDefCompiler.dependencyManager.addRequiredSymbol("scala.Array");
            buffer += "var %s = scala.Array.fromNative(".format(getLocalJsName(parameter.symbol));

            // In fact, the "arguments" JS variable only behaves like an array, but isn't an array. The
            // following trick described on http://www.mennovanslooten.nl/blog/post/59 is used to turn it
            // into an array that doesn't contain the normal named parameters.
            buffer += "[].splice.call(arguments, %1$s, arguments.length - %1$s)".format(parameters.length - 1)
            buffer += ");\n";
        }
    }

    protected def compileParameterValues(parameterValues: List[Global#Tree], withParentheses: Boolean = true) {
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

    protected def compileAst(ast: Global#Tree, hasReturnValue: Boolean = false) {
        // A Block handles the return value itself so it has to be compiled besides all other ast types.
        if (ast.isInstanceOf[Global#Block]) {
            compileBlock(ast.asInstanceOf[Global#Block], hasReturnValue)

            // Other ast types don't handle return value themselves.
        } else {
            val compiledAstIndex = buffer.length;
            ast match {
                case EmptyTree => buffer += "undefined"
                case thisAst: Global#This => compileThis(thisAst)
                case Return(expr) => compileAst(expr)
                case literal: Global#Literal => compileLiteral(literal)
                case identifier: Global#Ident => compileIdentifier(identifier)
                case valDef: Global#ValDef if valDef.symbol.isLocal => compileLocalValDef(valDef)
                case function: Global#Function => compileFunction(function)
                case constructorCall: Global#New => compileNew(constructorCall)
                case select: Global#Select => compileSelect(select)
                case apply: Global#Apply => compileApply(apply)
                case typeApply: Global#TypeApply => compileTypeApply(typeApply)
                case assign: Global#Assign => compileAssign(assign)
                case ifAst: Global#If => compileIf(ifAst)
                case labelDef: Global#LabelDef => compileLabelDef(labelDef)
                case tryAst: Global#Try => // TODO
                case throwAst: Global#Throw => compileThrow(throwAst)
                case matchAst: Global#Match => compileMatch(matchAst)
                case _ => throw new ScalaToJsException(
                    "Not implemented AST of type %s: %s".format(ast.getClass, ast.toString))
            }

            // If the last statement should be returned, prepend it with the "return" keyword.
            if (hasReturnValue) {
                buffer.update(compiledAstIndex, "return " + buffer(compiledAstIndex));
            }
        }
    }

    protected def compileAstStatement(ast: Global#Tree, hasReturnValue: Boolean = false) {
        val previousBufferLength = buffer.length

        compileAst(ast, hasReturnValue)

        if (!ast.isInstanceOf[Global#Block] && buffer.length > previousBufferLength) {
            buffer += ";\n"
        }
    }

    protected def compileBlock(block: Global#Block, hasReturnValue: Boolean = false) {
        block.stats.foreach(compileAstStatement(_))
        compileAstStatement(block.expr, hasReturnValue)
    }

    protected def compileLiteral(literal: Global#Literal) {
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
                throw new ScalaToJsException("Non-constant literal " + literal.toString)
            }
        }
    }

    protected def compileThis(thisAst: Global#This) {
        if (thisAst.hasSymbolWhich(_.fullName.toString.startsWith("scala"))) {
            buffer += thisAst.symbol.fullName.toString
        } else {
            buffer += "self"
        }
    }

    protected def compileIdentifier(identifier: Global#Ident) {
        buffer += getJsName(identifier.symbol)
    }

    protected def compileLocalValDef(localValDef: Global#ValDef) {
        buffer += "var %s = ".format(getLocalJsName(localValDef.symbol))
        compileAst(localValDef.rhs)
    }

    protected def compileFunction(function: Global#Function) {
        // TODO maybe somehow merge it with defdef compilation.
        buffer += "function("
        compileParameterDeclaration(function.vparams)
        buffer += ") { "
        compileParameterInitialization(function.vparams)
        compileAstStatement(function.body, hasReturnValue(function.body.tpe.typeSymbol))
        buffer += " }"
    }

    protected def compileNew(constructorCall: Global#New) {
        buffer += "new %s".format(getJsName(constructorCall.tpe.typeSymbol))
        packageDefCompiler.dependencyManager.addRequiredSymbol(constructorCall.tpe.typeSymbol)
    }

    protected def typeIsPrimitive(tpe: Global#Type): Boolean = {
        var x = tpe.typeSymbol.fullName == "java.lang.String"
        tpe.typeSymbol.fullName == "java.lang.String" ||
            tpe.baseClasses.exists(_.fullName.toString == "scala.AnyVal")
    }

    protected def compileSelect(select: Global#Select, isSubSelect: Boolean = false, isInsideApply: Boolean = false) {
        val subSelectToken = if (isSubSelect) "." else ""
        val nameString = getLocalJsName(select.name.toString)
        val name = if (nameString.endsWith("_$eq")) nameString.stripSuffix("_$eq") else nameString
        def isNestedPackageSelect(select: Global#Select): Boolean = {
            select.symbol.isPackage && (select.qualifier match {
                case subSelect: Global#Select => isNestedPackageSelect(subSelect)
                case identifier: Global#Ident => identifier.symbol.isPackage
                case _ => false
            })
        }

        def isIgnoredSelect(select: Global#Select): Boolean = {
            val ignoredNames = Set("<init>")
            val ignoredAnyValNames = Set("toLong", "toInt", "toShort", "toDouble", "toFloat")
            val selectName = select.name.toString

            ignoredNames.contains(selectName) ||
                (typeIsPrimitive(select.qualifier.tpe) && ignoredAnyValNames.contains(selectName))
        }

        select match {
            case Select(qualifier, _) if isIgnoredSelect(select) => {
                compileAst(qualifier)
            }
            case Select(subSelect@Select(_, _), _) if select.name.toString == "package" => {
                // Delegate the compilation to the subSelect and don't do anything with the subSelectToken.
                compileSelect(subSelect, isSubSelect)
            }
            case select@Select(qualifier, _) if symbolIsOperator(select.symbol) => {
                compileOperator(qualifier, None, name)
            }
            case s if isNestedPackageSelect(s) && packageDefCompiler.symbolPackageReplacement(s.symbol).isDefined => {
                val name = getJsName(select.symbol)
                buffer += name + (if (name.isEmpty) "" else subSelectToken)
            }
            case Select(qualifier, _) => {
                qualifier match {
                    case subSelect@Select(_, _) => compileSelect(subSelect, true)
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
        Array(select, select.qualifier).foreach {ast =>
            if (ast.hasSymbolWhich(_.toString.startsWith("object "))) {
                packageDefCompiler.dependencyManager.addRequiredSymbol(ast.symbol)
            }
        }
    }

    protected def compileApply(apply: Global#Apply) {
        apply match {
            case Apply(s@Select(q, name), args) if symbolIsOperator(s.symbol) => {
                compileOperator(q, Some(args.head), name.toString)
            }
            case Apply(select@Select(qualifier, name), args) if name.toString.endsWith("_$eq") => {
                compileAssign(select, args.head)
            }
            case Apply(Select(superClass@Super(_, _), name), _) => {
                compileParentCall(apply.args, Some(name))
            }
            case Apply(Select(qual, name), _) if name.toString == "apply" && qual.symbol.owner.isMethod => {
                compileAst(qual)
                compileParameterValues(apply.args)
            }
            case Apply(subApply@Apply(_, _), args) => {
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
            case Apply(select@Select(_, _), _) if select.hasSymbolWhich(_.isMethod) => {
                compileSelect(select, isInsideApply = true)
                compileParameterValues(apply.args)
            }
            case _ => {
                compileAst(apply.fun)
                compileParameterValues(apply.args)
            }
        }
    }

    protected def compileOperator(firstOperand: Global#Tree, secondOperand: Option[Global#Tree], name: String) {
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

    protected def compileTypeApply(typeApply: Global#TypeApply) {
        typeApply.fun match {
            case Select(qualifier, name) if name.toString == "isInstanceOf" || name
                .toString == "asInstanceOf" => {
                typeApply.args.head.tpe match {
                    case uniqueTypeRef: Global#UniqueTypeRef => {
                        val typeApplyType = name.toString.take(2)
                        compileInstanceOf(() => compileAst(qualifier), uniqueTypeRef.typeSymbol, typeApplyType)
                    }
                    case tpe => throw new ScalaToJsException("Unsupported type check/conversion: " + tpe.toString)
                }
            }
            case select: Global#Select => compileSelect(select, isInsideApply = true)
            case fun => compileAst(fun)
        }
    }

    protected def compileInstanceOf(objectQualifierCompiler: () => Unit, classSymbol: Global#Symbol, prefix: String) {
        buffer += "s2js.%sInstanceOf(".format(prefix)
        objectQualifierCompiler()
        buffer += ", '%s')".format(getJsName(classSymbol))
    }

    protected def compileAssign(assign: Global#Assign) {
        compileAssign(assign.lhs, assign.rhs)
    }

    protected def compileAssign(assignee: Global#Tree, value: Global#Tree) {
        assignee match {
            case select: Global#Select => compileSelect(select, isInsideApply = true)
            case _ => compileAst(assignee)
        }
        buffer += " = "
        compileAst(value)
    }

    protected def compileIf(condition: Global#If) {
        buffer += "(function() {\nif ("
        compileAst(condition.cond)
        buffer += ") {\n"
        compileAstStatement(condition.thenp, hasReturnValue(condition.tpe.typeSymbol))
        buffer += "} else {\n"
        compileAstStatement(condition.elsep, hasReturnValue(condition.tpe.typeSymbol))
        buffer += "}})()"
    }

    protected def compileLabelDef(labelDef: Global#LabelDef) {
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
                throw new ScalaToJsException("Unknown labelDef: " + labelDef.toString)
            }
        }
    }

    protected def compileThrow(throwAst: Global#Throw) {
        buffer += "(function() {\nthrow "
        compileAstStatement(throwAst.expr)
        buffer += "})()"
    }

    protected def compileMatch(matchAst: Global#Match) {
        val selectorName = getLocalJsName("selector_" + getUniqueId(), true)
        val hasReturn = hasReturnValue(matchAst.tpe.typeSymbol)
        buffer += "(function(%s) {\n".format(selectorName)
        matchAst.cases.foreach(caseDef => compileCase(caseDef, selectorName, hasReturn))
        buffer += "})("
        compileAst(matchAst.selector)
        buffer += ")"
    }

    protected def compileCase(caseDef: Global#CaseDef, selectorName: String, hasReturn: Boolean) {
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

    protected def compilePattern(patternAst: Global#Tree, selectorName: String) {
        patternAst match {
            case Ident(name) if name.toString == "_" => buffer += "true"
            case literal: Global#Literal => compileLiteralPattern(literal, selectorName)
            case typed: Global#Typed => compileTypedPattern(typed, selectorName)
            case bind: Global#Bind => compilePattern(bind.body, selectorName)
            case select: Global#Select => compileSelectPattern(select, selectorName)
            case apply: Global#Apply => compileApplyPattern(apply, selectorName)
            case alternative: Global#Alternative => compileAlternativePattern(alternative, selectorName)
            case _ => throw new ScalaToJsException("Not implemented pattern in case: %s".format(patternAst.toString))
        }
    }

    protected def compileLiteralPattern(literal: Global#Literal, selectorName: String) {
        buffer += "%s === ".format(selectorName)
        compileLiteral(literal)
    }

    protected def compileTypedPattern(typed: Global#Typed, selectorName: String) {
        compileInstanceOf(() => buffer += selectorName, typed.tpe.typeSymbol, "is")
    }

    protected def compileSelectPattern(select: Global#Select, selectorName: String) {
        buffer += "%s === ".format(selectorName)
        compileAst(select)
    }

    protected def compileApplyPattern(apply: Global#Apply, selectorName: String) {
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

    protected def compileAlternativePattern(alternative: Global#Alternative, selectorName: String) {
        alternative.trees.foreach {subPatternAst =>
            buffer += "("
            compilePattern(subPatternAst, selectorName)
            buffer += ")"
            buffer += " || "
        }
        buffer.remove(buffer.length - 1)
    }

    protected def compileBindings(patternAst: Global#Tree, selectorName: String) {
        patternAst match {
            case bind: Global#Bind => {
                buffer += "var %s = %s;\n".format(getLocalJsName(bind.name.toString), selectorName)
            }
            case apply: Global#Apply => {
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
            fullJsName,
            predecessors.map(c => getJsName(c.symbol)).mkString(", ")
        )
    }
}






