package s2js.compiler.components

import s2js.compiler.ScalaToJsException
import scala.tools.nsc.Global
import scala.collection.mutable
import collection.mutable.{LinkedHashMap, ListBuffer}

/**A factory for ClassDefCompiler objects. */
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

/**A compiler of a ClassDef. */
abstract class ClassDefCompiler(val packageDefCompiler: PackageDefCompiler, val classDef: Global#ClassDef)
{
    import packageDefCompiler.global._

    /**Full name of the JavaScript object that corresponds to the ClassDef. */
    protected lazy val fullJsName = packageDefCompiler.getSymbolJsName(classDef.symbol)

    /**Full name of the JavaScript object that should contains members (fields, methods) of the ClassDef. */
    protected val memberContainerName = fullJsName

    /**Parent class and inherited traits (doesn't contain internal classes). */
    protected val predecessors = classDef.impl.parents.filter(c => !packageDefCompiler.symbolIsInternal(c.symbol))

    /**The parent class. */
    protected val parentClass = predecessors.headOption

    /**The inherited traits. */
    protected val inheritedTraits = if (predecessors.nonEmpty) predecessors.tail else Nil

    /**The ValDef or DefDef members. */
    protected val valOrDefDefs = classDef.impl.body.filter(_.isInstanceOf[Global#ValOrDefDef])

    /**The ValDef members. */
    protected val valDefs = valOrDefDefs.collect {
        case valDef: Global#ValDef => valDef
    }

    /**The DefDef members. */
    protected val defDefs = valOrDefDefs.collect {
        case defDef: Global#DefDef => defDef
    }

    /**The constructor DefDef objects. */
    protected val constructors = classDef.impl.body.filter(_.hasSymbolWhich(_.isPrimaryConstructor))

    /**The first and currently the only used constructor. */
    protected val constructorDefDef: Option[Global#DefDef] = constructors.headOption.map(_.asInstanceOf[Global#DefDef])

    /**Parameters of the constructor. */
    protected val constructorParameters: Option[List[Global#ValDef]] = constructorDefDef.map(_.vparamss.flatten)

    /**Buffer containing the compiled JavaScript code. */
    protected var buffer: mutable.ListBuffer[String] = null

    /**Map of operators indexed by the corresponding method name. */
    private val operatorTokenMap = Map[String, String](
        "eq" -> "===",
        "ne" -> "!==",
        "$eq$eq" -> "==",
        "$bang$eq" -> "!=",
        "$greater" -> ">",
        "$greater$eq" -> ">=",
        "$less" -> "<",
        "$less$eq" -> "<=",
        "$plus" -> "+",
        "$minus" -> "-",
        "$times" -> "*",
        "$div" -> "/",
        "$percent" -> "%",
        "unary_$minus" -> "-",
        "$amp$amp" -> "&&",
        "$bar$bar" -> "||",
        "unary_$bang" -> "!"
    )

    /**The special JavaScript characters and their escape sequences. */
    private val stringEscapeMap = LinkedHashMap[String, String](
        "\\" -> """\\""",
        "\b" -> """\b""",
        "\f" -> """\f""",
        "\n" -> """\n""",
        "\r" -> """\r""",
        "\t" -> """\t""",
        "'" -> """\'""",
        "\"" -> """\""""
    )

    /**
     * Compiles the ClassDef.
     * @param buffer The buffer where the compiled JavaScript code is appended.
     */
    def compile(buffer: mutable.ListBuffer[String]) {
        this.buffer = buffer

        compileSymbol(classDef.symbol) {
            internalCompile()
        }
    }

    /**
     * Compiles the ClassDef.
     */
    protected def internalCompile() {
        // Try to find the parent constructor call within the constructor.
        val parentConstructorCall: Option[Global#Apply] = constructorDefDef.flatMap { constructor =>
            constructor.rhs.children.collect {
                case apply@Apply(Select(Super(_, _), name), _) if name.toString == "<init>" => apply
            }.headOption
        }

        compileConstructor(parentConstructorCall)
        compileMembers()
        instantiateClass()
    }

    /**
     * Compiles the constructor.
     * @param parentConstructorCall Optional parent constructor call within the constructor.
     */
    protected def compileConstructor(parentConstructorCall: Option[Global#Apply])

    /**
     * Compiles members of a ClassDef: ValDefs (field vals and vars) and DefDefs (methods).
     */
    protected def compileMembers() {
        valDefs.foreach(compileMember(_))
        defDefs.foreach(compileMember(_))
    }

    /**
     * Creates an instance of the Class corresponding to the ClassDef.
     */
    private def instantiateClass() {
        buffer += "%s.__class__ = new s2js.runtime.client.core.Class('%s', [%s]);\n".format(
            memberContainerName,
            fullJsName,
            predecessors.map(c => packageDefCompiler.getSymbolJsName(c.symbol)).mkString(", ")
        )
    }

    /**
     * Compiles a member.
     * @param memberAst The member AST.
     * @param containerName Full name of the JavaScript object that should contain the member.
     */
    protected def compileMember(memberAst: Global#Tree, containerName: String = memberContainerName) {
        if (memberAst.hasSymbolWhich(!symbolIsIgnoredMember(_))) {
            memberAst match {
                case valDef: Global#ValDef => compileValDef(valDef, containerName)
                case defDef: Global#DefDef => compileDefDef(defDef, containerName)
                case _: Global#ClassDef => // NOOP, wrapped classes are compiled separately
                case _ => {
                    throw new ScalaToJsException("Unsupported member %s of type %s".format(
                        memberAst.toString,
                        memberAst.getClass
                    ))
                }
            }
        }
    }

    /**
     * Compiles a ValDef member.
     * @param valDef The ValDef to compile.
     * @param containerName Full name of the JavaScript object that should contain the member.
     */
    protected def compileValDef(valDef: Global#ValDef, containerName: String = memberContainerName) {
        buffer += "%s.%s = ".format(containerName, packageDefCompiler.getSymbolLocalJsName(valDef.symbol))
        compileSymbol(valDef.symbol) {
            compileAst(valDef.rhs)
        }
        buffer += ";\n"
    }

    /**
     * Compiles a function.
     * @param parameters The list of parameters.
     * @param declareSelf Whether the first statement of the function body should be declaration of the self
     *                    variable (var self = this;).
     * @param compileBody An action that compiles the function bodye.
     */
    private def compileFunction(parameters: List[Global#ValDef], declareSelf: Boolean)(compileBody: => Unit) {
        // Function header.
        buffer += "function("
        compileParameterDeclaration(parameters)
        buffer += ") {\n"

        // Function body.
        if (declareSelf) {
            buffer += "var self = this;\n"
        }
        compileParameterInitialization(parameters)
        compileBody
        buffer += "}"
    }

    /**
     * Compiles a DefDef member.
     * @param defDef The DefDef to compile.
     * @param containerName Full name of the JavaScript object that should contain the member.
     */
    protected def compileDefDef(defDef: Global#DefDef, containerName: String = memberContainerName) {
        buffer += "%s.%s = ".format(containerName, packageDefCompiler.getSymbolLocalJsName(defDef.symbol))
        compileFunction(defDef.vparamss.flatten, true) {
            compileSymbol(defDef.symbol) {
                compileAstStatement(defDef.rhs, !packageDefCompiler.typeIsEmpty(defDef.tpt.tpe))
            }
        }
        buffer += ";\n"
    }

    /**
     * Compiles an anonymous function.
     * @param function The anonymous function to compile.
     */
    private def compileAnonymousFunction(function: Global#Function) {
        compileFunction(function.vparams, false) {
            compileAstStatement(function.body, !packageDefCompiler.typeIsEmpty(function.body.tpe))
        }
    }

    /**
     * Compiles call of a method that is declared in a super class or trait.
     * @param apply The method application AST.
     * @param methodName Name of the method to call.
     */
    protected def compileSuperCall(apply: Global#Apply, methodName: String) {
        apply.fun.symbol match {
            case m: MethodSymbol => {
                buffer += "%s.prototype.%s.apply(self, ".format(
                    packageDefCompiler.getSymbolFullJsName(m.owner),
                    packageDefCompiler.getLocalJsName(methodName)
                )
                compileParameterValues(apply.args, withParentheses = false, asArray = true)
                buffer += ")"
            }
            case _ => throw new ScalaToJsException("Not implemented super call of type %s.".format(apply))
        }
    }

    /**
     * Compiles declaration of method or function parameters.
     * @param parameters The parameter list.
     */
    protected def compileParameterDeclaration(parameters: List[Global#ValDef]) {
        compileParameterDeclaration(Option(parameters))
    }

    /**
     * Compiles declaration of method or function parameters.
     * @param parameters Optional parameter list.
     */
    protected def compileParameterDeclaration(parameters: Option[List[Global#ValDef]]) {
        if (parameters.isDefined) {
            val nonVariadicParameters = parameters.get.filter(p => !typeIsVariadic(p.tpt))
            buffer += nonVariadicParameters.map(p => packageDefCompiler.getSymbolLocalJsName(p.symbol)).mkString(", ")
        }
    }

    /**
     * Compiles initialization of method or function parameters.
     * @param parameters The parameter list.
     */
    protected def compileParameterInitialization(parameters: List[Global#ValDef]) {
        compileParameterInitialization(Option(parameters))
    }

    /**
     * Compiles initialization of method or function parameters.
     * @param parameters Optional parameter list.
     */
    protected def compileParameterInitialization(parameters: Option[List[Global#ValDef]]) {
        parameters.foreach { p =>
            // Parameters with default values.
            p.filter(_.symbol.hasDefault).foreach { parameter =>
                buffer += "if (typeof(%1$s) === 'undefined') { %1$s = ".format(
                    packageDefCompiler.getSymbolLocalJsName(parameter.symbol)
                )
                parameter.asInstanceOf[Global#ValDef].rhs match {
                    case ident: Global#Ident if ident.symbol.owner == parameter.symbol.owner => {
                        buffer += "self.%s".format(ident.symbol.nameString)
                    }
                    case x => compileAst(x)
                }
                buffer += "; }\n"
            }

            // Variadic parameter.
            p.filter(t => typeIsVariadic(t.tpt)).foreach { parameter =>
                packageDefCompiler.dependencies.addRequiredSymbolName(
                    "scala.collection.immutable.List",
                    declarationRequired = false
                )
                buffer += "var %s = scala.collection.immutable.List.get().fromJsArray(".format(
                    packageDefCompiler.getSymbolLocalJsName(parameter.symbol)
                )

                // In fact, the "arguments" JS variable only behaves like an array, but isn't an array. The
                // following trick described on http://www.mennovanslooten.nl/blog/post/59 is used to turn it
                // into an array that doesn't contain the normal named parameters.
                buffer += "[].splice.call(arguments, %1$s, arguments.length - %1$s)".format(p.length - 1)
                buffer += ");\n"
            }
        }
    }

    /**
     * Compiles parameter values passed to a method call or a function call.
     * @param parameterValues List of parameter value ASTs.
     * @param withParentheses Whether the compiled parameter values should be enclosed in parentheses.
     * @param asArray Whether the parameters should be compiled as an array.
     */
    protected def compileParameterValues(parameterValues: List[Global#Tree], withParentheses: Boolean = true,
        asArray: Boolean = false) {
        if (withParentheses || asArray) {
            buffer += (if (asArray) "[" else "(")
        }
        if (parameterValues.nonEmpty) {
            // Default parameter values are handled in the function body so they are ignored.
            parameterValues.foreach { parameterValue =>
                if (!parameterValue.hasSymbolWhich(_.name.toString.contains("$default$"))) {
                    parameterValue match {
                        case Block(_, expr) => compileAst(expr)
                        case _ => compileAst(parameterValue)
                    }
                } else {
                    buffer += "undefined"
                }
                buffer += ", "
            }
            buffer.remove(buffer.length - 1)
        }
        if (withParentheses || asArray) {
            buffer += (if (asArray) "]" else ")")
        }
    }

    /**
     * Compiles parameter types as a comma separated sequence of their fully qualified names.
     * @param parameterValues List of parameter value ASTs.
     */
    protected def compileParameterTypeNames(parameterValues: List[Global#Tree]) {
        def typeToFullName(tpe: Global#Type): String = {
            val typeArguments = tpe.typeArgs.map(typeToFullName)
            tpe.typeSymbol.fullName + (if (typeArguments.isEmpty) "" else typeArguments.mkString("[", ", ", "]"))
        }

        buffer += parameterValues.map(p => "'%s'".format(typeToFullName(p.tpe))).mkString(", ")
    }

    /**
     * Compiles an AST.
     * @param ast The AST to compile.
     * @param hasReturnValue Whether the last statement of the AST should be prepended with "return" keyword in the
     *                       compiled JavaScript code.
     */
    protected def compileAst(ast: Global#Tree, hasReturnValue: Boolean = false) {
        // A Block handles the return value itself so it has to be compiled besides all other ast types.
        if (ast.isInstanceOf[Global#Block]) {
            compileBlock(ast.asInstanceOf[Global#Block], hasReturnValue)
        } else {
            val compiledAstIndex = buffer.length
            ast match {
                case EmptyTree => buffer += "undefined"
                case thisAst: Global#This => compileThis(thisAst)
                case Return(expr) => compileAst(expr)
                case literal: Global#Literal => compileLiteral(literal, hasReturnValue)
                case identifier: Global#Ident => compileIdentifier(identifier)
                case valDef: Global#ValDef if valDef.symbol.isLocal => compileLocalValDef(valDef)
                case _: Global#TypeDef => // NOOP
                case function: Global#Function => compileAnonymousFunction(function)
                case constructorCall: Global#New => compileNew(constructorCall)
                case select: Global#Select => compileSelect(select)
                case apply: Global#Apply => compileApply(apply)
                case typeApply: Global#TypeApply => compileTypeApply(typeApply)
                case typed: Global#Typed => compileInstanceOf(typed.tpe.typeSymbol, true, typed.expr)
                case assign: Global#Assign => compileAssign(assign)
                case ifAst: Global#If => compileIf(ifAst)
                case labelDef: Global#LabelDef => compileLabelDef(labelDef)
                case tryAst: Global#Try => compileTry(tryAst)
                case throwAst: Global#Throw => compileThrow(throwAst)
                case matchAst: Global#Match => compileMatch(matchAst)
                case _ => throw new ScalaToJsException("Not implemented AST of type %s: %s".format(
                    ast.getClass,
                    ast.toString
                ))
            }

            // If the last statement should be returned, prepend it with the "return" keyword.
            if (hasReturnValue) {
                buffer.update(compiledAstIndex, "return " + buffer(compiledAstIndex))
            }
        }
    }

    /**
     * Compiles an AST statement. The statement is terminated with the ";" in the compiled JavaScript code.
     * @param ast The AST statement to compile.
     * @param hasReturnValue Whether the last statement of the AST should be prepended with "return" keyword in the
     *                       compiled JavaScript code.
     */
    protected def compileAstStatement(ast: Global#Tree, hasReturnValue: Boolean = false) {
        val previousBufferLength = buffer.length
        compileAst(ast, hasReturnValue)
        val somethingWasCompiled = buffer.length > previousBufferLength
        val terminateWithSemicolon = ast match {
            case _: Global#Block => false
            case _: Global#TypeDef => false
            case _: Global#Try => false
            case _: Global#If => hasReturnValue
            case _ => true
        }

        buffer += (if (somethingWasCompiled && terminateWithSemicolon) ";" else "") + "\n"
    }

    /**
     * Compiles a Block of statements.
     * @param block The Block to compile.
     * @param hasReturnValue Whether the last statement of the Block should be prepended with "return" keyword in the
     *                       compiled JavaScript code.
     */
    private def compileBlock(block: Global#Block, hasReturnValue: Boolean = false) {
        block.stats.foreach(compileAstStatement(_))
        compileAstStatement(block.expr, hasReturnValue)
    }

    /**
     * Compiles a Literal value.
     * @param literal The Literal to compile.
     * @param isReturnValue Whether the literal is a return value of a function.
     */
    private def compileLiteral(literal: Global#Literal, isReturnValue: Boolean = false) {
        literal match {
            case Literal(Constant(value)) => {
                value match {
                    case _: Unit => if (isReturnValue) {
                        buffer += "undefined"
                    }
                    case null => {
                        buffer += "null"
                    }
                    case _: String | _: Char => {
                        buffer += toJsString(value.toString)
                    }
                    case r: Global#UniqueTypeRef => {
                        // The result of classOf[X].
                        packageDefCompiler.dependencies.addRequiredSymbol(r.typeSymbol)
                        buffer += "%s.prototype.__class__".format(packageDefCompiler.getSymbolFullJsName(r.typeSymbol))
                    }
                    case _ => buffer += value.toString
                }
            }
            case _ => {
                throw new ScalaToJsException("Non-constant literal %s.".format(literal.toString))
            }
        }
    }

    /**
     * Compiles a This reference.
     * @param thisAst The This reference AST.
     */
    private def compileThis(thisAst: Global#This) {
        if (thisAst.hasSymbolWhich(s => s.isModule || s.isModuleClass)) {
            buffer += packageDefCompiler.getSymbolFullJsName(thisAst.symbol)
            buffer += ".get()"
        } else {
            buffer += "self"
        }
    }

    /**
     * Compiles an identifier.
     * @param identifier The Ident to compile.
     */
    private def compileIdentifier(identifier: Global#Ident) {
        if (identifier.hasSymbolWhich(s => s.isModule || s.isModuleClass)) {
            buffer += packageDefCompiler.getSymbolFullJsName(identifier.symbol)
            buffer += ".get()"
        } else if (identifier.symbol.isGetter) {
            buffer += "self.%s".format(packageDefCompiler.getSymbolLocalJsName(identifier.symbol))
        } else {
            buffer += packageDefCompiler.getSymbolJsName(identifier.symbol)
        }
    }

    /**
     * Compiles declaration of a local variable.
     * @param localValDef The local variable declaration to compile.
     */
    private def compileLocalValDef(localValDef: Global#ValDef) {
        buffer += "var %s = ".format(packageDefCompiler.getSymbolLocalJsName(localValDef.symbol))
        compileAst(localValDef.rhs)
    }

    /**
     * Compiles instantiation of a class.
     * @param instantiation The class instantiation to compile.
     */
    private def compileNew(instantiation: Global#New) {
        buffer += "new %s".format(packageDefCompiler.getSymbolJsName(instantiation.tpe.typeSymbol))
        packageDefCompiler.dependencies.addRequiredSymbol(instantiation.tpe.typeSymbol)
    }

    /**
     * Compiles a Select.
     * @param select The Select to compile.
     * @param isSubSelect Whether the select is within a chain of selections (another selection is applied on the
     *                    result of the current Select).
     * @param isInsideApply Whether the select is within an Apply (the result of the current Select is target of an
     *                      Apply).
     */
    private def compileSelect(select: Global#Select, isSubSelect: Boolean = false, isInsideApply: Boolean = false) {
        val subSelectToken = if (isSubSelect) "." else ""
        val name = packageDefCompiler.getSymbolLocalJsName(select.symbol)

        select match {
            case _ if selectIsOnRemote(select) && select.hasSymbolWhich(s => s.isMethod) => {
                compileRpcCall(select, select.tpe, Nil)
            }
            case Select(qualifier, _) if selectIsIgnored(select) => {
                compileAst(qualifier)
                buffer += subSelectToken
            }
            case select@Select(qualifier, _) if symbolIsOperator(select.symbol) => {
                compileOperator(qualifier, None, name)
            }
            case _ if select.hasSymbolWhich(s => s.isModule || s.isModuleClass) => {
                val jsName = packageDefCompiler.getSymbolFullJsName(select.symbol)
                if (jsName != "") {
                    buffer += jsName
                    if (!packageDefCompiler.adapterPackagesNames.exists(select.symbol.fullName.startsWith(_))) {
                        buffer += ".get()"
                    }
                    buffer += subSelectToken
                }
                packageDefCompiler.dependencies.addRequiredSymbol(select.symbol)
            }
            case Select(qualifier, _) => {
                val bufferLength = buffer.length
                compileAst(qualifier)
                if (buffer.takeRight(buffer.length - bufferLength).mkString != "") {
                    buffer += "."
                }

                buffer += (if (select.hasSymbolWhich(s => s.isSetter)) name.stripSuffix("_$eq") else name)
                if (!isInsideApply && select.hasSymbolWhich(s => s.isMethod && !s.isGetter)) {
                    // If the select is actually a method call, parentheses has to be added after the name.
                    buffer += "()"
                }

                buffer += subSelectToken
            }
        }
    }

    /**
     * Compiles a value application (Apply).
     * @param apply The Apply to compile.
     */
    private def compileApply(apply: Global#Apply) {
        apply match {
            case Apply(s@Select(q, name), args) if symbolIsOperator(s.symbol) => {
                compileOperator(q, Some(args.head), name.toString)
            }
            case Apply(select@Select(_, _), args) if select.symbol.isSetter => {
                compileAssign(select, args.head)
            }
            case Apply(Select(s: Super, name), _) => {
                compileSuperCall(apply, name.toString)
            }
            case Apply(Select(qual, name), _)
                if name.toString == "apply" && symbolIsCallable(qual.symbol.tpe.typeSymbol) => {
                compileAst(qual)
                compileParameterValues(apply.args)
            }
            case apply@Apply(Apply(Apply(select@Select(_, _), p), s), e) if (selectIsOnRemote(select)) => {
                compileRpcCall(select, apply.tpe, p ::: s ::: e)
            }
            case apply@Apply(Apply(select@Select(_, _), p), e) if (selectIsOnRemote(select)) => {
                compileRpcCall(select, apply.tpe, p ::: e)
            }
            case apply@Apply(select@Select(_, _), p) if (selectIsOnRemote(select)) => {
                compileRpcCall(select, apply.tpe, p)
            }
            case Apply(subApply@Apply(_, _), args) => {
                // Apply of a function with multiple parameter lists.
                compileApply(subApply)

                // Add the additional parameters to the subApply method call.
                val ignoreApply = args.isEmpty || args.head.toString.startsWith("reflect.this.ClassManifest")
                if (!ignoreApply) {
                    buffer.update(buffer.length - 1, buffer.last.dropRight(1))
                    buffer += ", "
                    compileParameterValues(args, withParentheses = false)
                    buffer += ")"
                }
            }
            case Apply(select@Select(_, _), _) => {
                compileSelect(select, isInsideApply = true)
                compileParameterValues(apply.args)
            }
            case Apply(typeApply@TypeApply(_, _), _) => {
                compileTypeApply(typeApply, isInsideApply = true)
                compileParameterValues(apply.args)
            }
            case _ => {
                compileAst(apply.fun)
                compileParameterValues(apply.args)
            }
        }
    }

    /**
     * Compiles a synchronous RPC call (instead of a method call).
     * @param select The method selection from the remote object.
     * @param returnType Return type of the RPC call.
     * @param parameters The parameters.
     */
    private def compileRpcCall(select: Global#Select, returnType: Global#Type, parameters: List[Global#Tree]) {
        val requiredTypes = ListBuffer[Global#Type](returnType)
        val isAsync = packageDefCompiler.symbolHasAnnotation(select.symbol, "s2js.compiler.async")
        val isSecured = List(select.symbol, select.symbol.owner).exists { symbol =>
            packageDefCompiler.symbolHasAnnotation(symbol, "s2js.compiler.secured")
        }

        var realParameters = parameters
        var successCallback: Option[Global#Tree] = None
        var errorCallback: Option[Global#Tree] = None

        // Check the parameter values.
        if (isAsync) {
            val callbacks = parameters.takeRight(2)
            successCallback = Some(callbacks.head)
            errorCallback = Some(callbacks.last)
            requiredTypes += successCallback.get.tpe.typeArgs.head
            realParameters = parameters.dropRight(2)
        }
        if (isSecured) {
            if (realParameters.isEmpty) {
                throw new ScalaToJsException("A secured remote method must have a security context as a parameter.")
            }
            realParameters = realParameters.dropRight(1)
        }

        // Add the required dependencies.
        requiredTypes.foreach { tpe =>
            if (!typeIsPrimitive(tpe)) {
                packageDefCompiler.dependencies.addRequiredSymbol(tpe.typeSymbol)
            }
        }

        // Compile the call itself.
        buffer += "s2js.runtime.client.rpc.Wrapper.get().call%s('%s', ".format(
            if (isAsync) "Async" else "Sync",
            select.toString
        )
        compileParameterValues(realParameters, asArray = true)
        buffer += ", ["
        compileParameterTypeNames(realParameters)
        buffer += "]"
        if (isAsync) {
            buffer += ", "
            compileAst(successCallback.get)
            buffer += ", "
            compileAst(errorCallback.get)
        }
        buffer += ")"
    }

    /**
     * Compiles an operator application.
     * @param firstOperand The first operand.
     * @param secondOperand The second operand.
     * @param operatorToken The operator token ("+", "-" etc.).
     */
    private def compileOperator(firstOperand: Global#Tree, secondOperand: Option[Global#Tree], operatorToken: String) {
        buffer += "("
        if (operatorToken.startsWith("unary_")) {
            buffer += operatorTokenMap(operatorToken) + " "
            compileAst(firstOperand)
        } else {
            compileAst(firstOperand)
            buffer += " " + operatorTokenMap(operatorToken) + " "
            compileAst(secondOperand.get)
        }
        buffer += ")"
    }

    /**
     * Compiles a type parameter application.
     * @param typeApply The TypeApply to compile.
     * @param isInsideApply Whether the type apply is within an Apply (the result of the current Type Apply is target
     *                      of an Apply).
     */
    private def compileTypeApply(typeApply: Global#TypeApply, isInsideApply: Boolean = false) {
        typeApply.fun match {
            case Select(qualifier, name) if name.toString.matches("(is|as)InstanceOf") => {
                typeApply.args.head.tpe match {
                    case uniqueTypeRef: Global#UniqueTypeRef => {
                        compileInstanceOf(uniqueTypeRef.typeSymbol, name.toString.take(2) == "is", qualifier)
                    }
                    case tpe => throw new ScalaToJsException("Unsupported type check/conversion: " + tpe.toString)
                }
            }
            case select: Global#Select => compileSelect(select, isInsideApply = isInsideApply)
            case fun => compileAst(fun)
        }
    }

    /**
     * Compiles a type check or type conversion.
     * @param typeSymbol Symbol of the type.
     * @param isTypeCheck True in case of type check, false in case of type conversion.
     * @param qualifier The qualifier that is compiled as a generic AST.
     */
    private def compileInstanceOf(typeSymbol: Global#Symbol, isTypeCheck: Boolean, qualifier: Global#Tree) {
        compileInstanceOf(typeSymbol, isTypeCheck) {
            compileAst(qualifier)
        }
    }

    /**
     * Compiles a type check or type conversion.
     * @param typeSymbol Symbol of the type.
     * @param isTypeCheck True in case of type check, false in case of type conversion.
     * @param compileQualifier An action that compiles the target object qualifier.
     */
    private def compileInstanceOf(typeSymbol: Global#Symbol, isTypeCheck: Boolean)(compileQualifier: => Unit) {
        buffer += "s2js.runtime.client.core.get().%sInstanceOf(".format(if (isTypeCheck) "is" else "as")
        compileQualifier
        buffer += ", '%s')".format(packageDefCompiler.getSymbolJsName(typeSymbol))
    }

    /**
     * Compiles an assignment.
     * @param assign The assignment to compile.
     */
    private def compileAssign(assign: Global#Assign) {
        compileAssign(assign.lhs, assign.rhs)
    }

    /**
     * Compiles an assignment.
     * @param assignee The target of the assignment.
     * @param value The value to assign.
     */
    private def compileAssign(assignee: Global#Tree, value: Global#Tree) {
        assignee match {
            case select: Global#Select => compileSelect(select, isInsideApply = true)
            case _ => compileAst(assignee)
        }
        buffer += " = "
        compileAst(value)
    }

    /**
     * Compiles an if-then-else statement.
     * @param condition The If statement to compile.
     */
    private def compileIf(condition: Global#If) {
        val hasReturn = !packageDefCompiler.typeIsEmpty(condition.tpe)
        if (hasReturn) {
            buffer += "(function() {\n"
        }

        // If.
        buffer += "if ("
        compileAst(condition.cond)
        buffer += ") {\n"
        compileAstStatement(condition.thenp, hasReturn)

        // Else.
        condition.elsep match {
            case Literal(Constant(_: Unit)) => // NOOP, else branch is empty.
            case _ => {
                buffer += "} else {\n"
                compileAstStatement(condition.elsep, hasReturn)
            }
        }

        buffer += "}\n"
        if (hasReturn) {
            buffer += "})()"
        }
    }

    /**
     * Compiles a label definition.
     * @param labelDef The LabelDef to compile.
     */
    private def compileLabelDef(labelDef: Global#LabelDef) {
        labelDef.name match {
            case name if name.toString.startsWith("while$") => {
                // A while cycle is transformed into a tail recursive function with AST similar to:
                //     def while$1() {
                //         if([while-condition]) {
                //            [while-body]
                //            while$1()
                //         }
                //     }
                val If(cond, Block(body, _), _) = labelDef.rhs
                buffer += "while("
                compileAst(cond)
                buffer += ") {\n"
                compileAstStatement(body.head)
                buffer += "}"
            }
            case _ => {
                throw new ScalaToJsException("Unsupported LabelDef: " + labelDef.toString)
            }
        }
    }

    /**
     * Compiles a try-catch-finally statement.
     * @param tryAst The Try to compile.
     */
    private def compileTry(tryAst: Global#Try) {
        if (!tryAst.finalizer.isEmpty) {
            throw new ScalaToJsException("The finally statement in try-catch-finally isn't supported.")
        }

        val hasReturn = !packageDefCompiler.typeIsEmpty(tryAst.tpe)
        val returnValueName = if (hasReturn) packageDefCompiler.getUniqueLocalName("tryReturnValue") else ""
        if (hasReturn) {
            buffer += "(function() {\n"
            buffer += "var %s = undefined;\n".format(returnValueName)
        }

        // Try.
        buffer += "try {\n"
        if (hasReturn) {
            buffer += "%s = (function() {\n".format(returnValueName)
        }
        compileAstStatement(tryAst.block, hasReturn)
        if (hasReturn) {
            buffer += "})();\n"
        }

        // Catch.
        val exceptionName = packageDefCompiler.getUniqueLocalName("ex")
        buffer += "} catch (%s) {\n".format(exceptionName)
        if (hasReturn) {
            buffer += "%s = ".format(returnValueName)
        }
        buffer += "(function() {\n"
        tryAst.catches.foreach(c => compileCase(c, exceptionName, hasReturn))

        // If no one of the catch cases matched the exception, then the exception should be propagated further.
        buffer += "throw %s;\n".format(exceptionName)
        buffer += "})();\n"
        buffer += "}\n"

        if (hasReturn) {
            buffer += "return %s;\n".format(returnValueName)
            buffer += "})()"
        }
    }

    /**
     * Compiles a throw statement.
     * @param throwAst The Throw to compile.
     */
    private def compileThrow(throwAst: Global#Throw) {
        // The throw statement has to be wrapped into an anonymous function to avoid "return throw ...".
        buffer += "(function() {\nthrow "
        compileAstStatement(throwAst.expr)
        buffer += "})()"
    }

    /**
     * Compiles a match statement.
     * @param matchAst the Match to compile.
     */
    private def compileMatch(matchAst: Global#Match) {
        val selectorName = packageDefCompiler.getUniqueLocalName("selector")
        val hasReturn = !packageDefCompiler.typeIsEmpty(matchAst.tpe)
        buffer += "(function(%s) {\n".format(selectorName)
        matchAst.cases.foreach(compileCase(_, selectorName, hasReturn))
        buffer += "})("
        compileAst(matchAst.selector)
        buffer += ")"
    }

    /**
     * Compiles a case statement within a match statement.
     * @param caseDef The CaseDef to compile.
     */
    private def compileCase(caseDef: Global#CaseDef, selectorName: String, hasReturn: Boolean) {
        buffer += "if ("
        compilePattern(caseDef.pat, selectorName)
        buffer += ") {\n"

        compileBindings(caseDef.pat, selectorName)

        val hasGuard = !caseDef.guard.isEmpty // caseDef.guard.isDef doesn't work properly.
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

    /**
     * Compiles a pattern in a case statement.
     * @param patternAst The pattern AST to compile.
     * @param selectorName Name of the selector variable.
     */
    private def compilePattern(patternAst: Global#Tree, selectorName: String) {
        patternAst match {
            case Ident(name) if name.toString == "_" => buffer += "true"
            case literal: Global#Literal => compileLiteralPattern(literal, selectorName)
            case typed: Global#Typed => compileTypedPattern(typed, selectorName)
            case bind: Global#Bind => compilePattern(bind.body, selectorName)
            case select: Global#Select => compileSelectPattern(select, selectorName)
            case apply: Global#Apply => compileApplyPattern(apply, selectorName)
            case alternative: Global#Alternative => compileAlternativePattern(alternative, selectorName)
            case _ => throw new ScalaToJsException("Unsupported pattern in case: %s.".format(patternAst.toString))
        }
    }

    /**
     * Compiles a literal pattern in a case statement.
     * @param literal The literal pattern value.
     * @param selectorName Name of the selector variable.
     */
    private def compileLiteralPattern(literal: Global#Literal, selectorName: String) {
        buffer += "%s === ".format(selectorName)
        compileLiteral(literal)
    }

    /**
     * Compiles a type check pattern in a case statement.
     * @param typed The type check pattern value.
     * @param selectorName Name of the selector variable.
     */
    private def compileTypedPattern(typed: Global#Typed, selectorName: String) {
        compileInstanceOf(typed.tpe.typeSymbol, true) {
            buffer += selectorName
        }
    }

    /**
     * Compiles a selection pattern in a case statement.
     * @param select The selection pattern value.
     * @param selectorName Name of the selector variable.
     */
    private def compileSelectPattern(select: Global#Select, selectorName: String) {
        buffer += "%s === ".format(selectorName)
        compileAst(select)
    }

    /**
     * Compiles a value application pattern in a case statement.
     * @param apply The value application pattern value.
     * @param selectorName Name of the selector variable.
     */
    private def compileApplyPattern(apply: Global#Apply, selectorName: String) {
        compileInstanceOf(apply.tpe.typeSymbol, true) {
            buffer += selectorName
        }
        buffer += " && "

        apply.args.zipWithIndex.foreach { arg => // _1 is the argument, _2 is the index
            buffer += "("
            compilePattern(arg._1, "%s.productElement(%s)".format(selectorName, arg._2))
            buffer += ")"
            buffer += " && "
        }
        buffer.remove(buffer.length - 1)
    }

    /**
     * Compiles a disjunction of two patterns in a case statement.
     * @param alternative The disjunction of two patterns.
     * @param selectorName Name of the selector variable.
     */
    private def compileAlternativePattern(alternative: Global#Alternative, selectorName: String) {
        alternative.trees.foreach { subPatternAst =>
            buffer += "("
            compilePattern(subPatternAst, selectorName)
            buffer += ")"
            buffer += " || "
        }
        buffer.remove(buffer.length - 1)
    }

    /**
     * Compiles variable bindings within a pattern.
     * @param patternAst The pattern whose bindings to compile.
     * @param selectorName Name of the selector variable.
     */
    private def compileBindings(patternAst: Global#Tree, selectorName: String) {
        patternAst match {
            case bind: Global#Bind => {
                buffer += "var %s = %s;\n".format(
                    packageDefCompiler.getLocalJsName(bind.name.toString),
                    selectorName
                )
            }
            case apply: Global#Apply => {
                apply.args.zipWithIndex.foreach { arg => // _1 is the argument, _2 is the index
                    compileBindings(arg._1, "%s.productElement(%s)".format(selectorName, arg._2))
                }
            }
            case _ =>
        }
    }

    /**
     * Returns whether the specified symbol corresponds to a JavaScript operator.
     * @param symbol The symbol to check.
     * @return True if the symbol corresponds to an operator, false otherwise.
     */
    private def symbolIsOperator(symbol: Symbol): Boolean = {
        val anyRefOperators = Set("eq", "ne", "$eq$eq", "$bang$eq")

        val symbolName = symbol.name.toString
        operatorTokenMap.contains(symbolName) &&
            (typeIsPrimitive(symbol.owner.tpe) || anyRefOperators.contains(symbolName))
    }

    /**
     * Returns whether the specified symbol is a method or an anonymous function.
     * @param symbol The symbol to check.
     * @return True if the symbol is a method or an anonymous function.
     */
    private def symbolIsCallable(symbol: Symbol): Boolean = {
        val isScalaFunctionObject = symbol.fullName.matches( """scala\.Function[0-9]+""")

        symbol.isMethod ||
            symbol.isAnonymousFunction ||
            symbol.isLiftedMethod ||
            isScalaFunctionObject
    }

    /**
     * Returns whether the symbol is a member of an internal type.
     * @param symbol The symbol to check.
     * @return True if the symbol belongs to an internal type, false otherwise.
     */
    private def symbolIsInternalMember(symbol: Global#Symbol): Boolean = {
        packageDefCompiler.symbolIsInternal(symbol.enclClass)
    }

    /**
     * Returns whether the specified symbol corresponding to a ClassDef member should be ignored during compilation.
     * @param member The member t check.
     * @return True if the member should be ignored during compilation, false otherwise.
     */
    protected def symbolIsIgnoredMember(member: Global#Symbol): Boolean = {
        val internalMemberNames = Set("hashCode", "equals", "canEqual", "readResolve")

        internalMemberNames.contains(member.nameString) || // An internal member
            symbolIsInternalMember(member) || // A member inherited from an internal Type
            member.owner != classDef.symbol || // A member that isn't directly owned by the class
            member.isDeferred || // An abstract member without implementation
            member.isConstructor || // Multiple constructors aren't currently supported.
            member.isParameter || // A parameter of a member method
            member.hasAccessorFlag || // A generated accesor method
            member.nameString.matches( """^.*\$default\$[0-9]+$""") // A member generated for default parameter value
    }

    /**
     * Returns whether the type is a primitive type (either scala.AnyVal or java.lang.String).
     * @param tpe the type to check.
     * @return True if the type is primitive, false otherwise.
     */
    private def typeIsPrimitive(tpe: Global#Type): Boolean = {
        tpe.typeSymbol.fullName == "java.lang.String" || tpe.baseClasses.exists(_.fullName.toString == "scala.AnyVal")
    }

    /**
     * Returns whether the type is a variadic parameter type.
     * @param typeAst The type AST.
     * @return True if the type is variadic parameter type, false otherwise.
     */
    private def typeIsVariadic(typeAst: Global#Tree): Boolean = {
        typeAst.toString.endsWith("*")
    }

    /**
     * Returns whether the select should be ignored during compilation.
     * @param select The select to check.
     * @return True if the select should be ignored, false otherwise.
     */
    private def selectIsIgnored(select: Global#Select): Boolean = {
        val ignoredNames = Set("<init>")
        val ignoredAnyValNames = Set("toLong", "toInt", "toShort", "toDouble", "toFloat")

        val selectName = select.name.toString
        ignoredNames.contains(selectName) ||
            (typeIsPrimitive(select.qualifier.tpe) && ignoredAnyValNames.contains(selectName))
    }

    /**
     * Returns whether the select is invoked on a remote object.
     * @param select The select to check.
     * @return True if the select is invoked on a remote object.
     */
    private def selectIsOnRemote(select: Global#Select): Boolean = {
        select.qualifier.hasSymbolWhich(packageDefCompiler.symbolHasAnnotation(_, "s2js.compiler.remote"))
    }

    /**
     * Converts the specified value to a quoted escaped JavaScript string.
     * @param value The value to convert.
     * @return The JavaScript string.
     */
    private def toJsString(value: String): String = {
        "'" + stringEscapeMap.foldLeft(value)((z, escape) => z.replace(escape._1, escape._2)) + "'"
    }

    /**
     * Compiles the specified symbol. If the symbol has a s2js.compiler.javascript annotation,
     * then the native JavaScript
     * code from the annotation is used. Otherwise compiles the symbol using the specified action.
     * @param symbol The symbol to compile.
     * @param ifNotNativeAction The action that is invoked the symbol isn't annotated with the s2js.compiler.javascript
     *                          annotation. Typically the action invokes direct compilation of the symbol.
     */
    private def compileSymbol(symbol: Global#Symbol)(ifNotNativeAction: => Unit) {
        val nativeAnnotations = packageDefCompiler.getSymbolAnnotations(symbol, "s2js.compiler.javascript")
        if (nativeAnnotations.nonEmpty) {
            nativeAnnotations.head.args.foreach {
                case Literal(Constant(value: String)) => buffer += value
            }
        } else {
            ifNotNativeAction
        }

        // Check for the dependency annotation.
        val dependencyAnnotations = packageDefCompiler.getSymbolAnnotations(symbol, "s2js.compiler.dependency")
        dependencyAnnotations.foreach { annotationInfo =>
            annotationInfo.args.foreach {
                case Literal(Constant(d: String)) => {
                    packageDefCompiler.dependencies.addRequiredSymbolName(d)
                }
            }
        }
    }
}
