package s2js

import scala.reflect.generic.Flags._

import tools.nsc.Global
import collection.mutable.{HashMap, HashSet, ListBuffer}

trait PackageCompiler
{
    val global: Global
    import global._

    private val buffer = new ListBuffer[String]
    private val packageDefMap = new HashMap[String, PackageDef]
    private val classDefMap = new HashMap[String, ClassDefCompiler]
    private val classDefDependencyGraph = new HashMap[String, HashSet[String]]
    private val internalTypes = Array[String](
        """^\$default\$$""",
        """^ClassManifest$""",
        """^java.lang$""",
        """^java.lang.Object$""",
        """^js.browser$""",
        """^js.dom$""",
        """^scala.Any$""",
        """^scala.AnyRef""",
        """^scala.Boolean$""",
        """^scala.Equals$""",
        """^scala.Function[0-9]+$""", // TODO maybe shoudn't be internal.
        """^scala.Int$""",
        """^scala.Predef$""",
        """^scala.Product$""",
        """^scala.ScalaObject$""",
        """^scala.Tuple[0-9]+$""", // TODO maybe shoudn't be internal.
        """^scala.package$""",
        """^scala.reflect.Manifest$""",
        """^scala.runtime$""",
        """^scala.runtime.AbstractFunction[0-9]+$""",
        """^scala.xml"""
    )

    private def isInternalType(symbol: Symbol): Boolean = {
        internalTypes.exists(symbol.fullName.matches(_))
    }

    private def isInternalTypeMember(symbol: Symbol): Boolean = {
        isInternalType(symbol.enclClass)
    }

    def compile(packageAst: PackageDef): String = {
        retrievePackageStructure(packageAst)
        compilePackageStructure()
        compileClassDefs()

        /*
        // First, the package structure has to be compiled so classes and objects may be put there.
        compilePackageStructure(ast.asInstanceOf[PackageDef])

        // The class/object dependency DAG has to be created so classes are compiled when all other classes, they depend
        // on are already compiled.
        compilePackageDef(ast.asInstanceOf[PackageDef])

        println("Provides: " + DependencyManager.getProvides)
        println("Requires: " + DependencyManager.getRequires)*/

        buffer.mkString
    }

    private def retrieveStructure(ast: Tree) {
        ast match {
            case packageDef: PackageDef => retrievePackageStructure(packageDef)
            case classDef: ClassDef => retrieveClassStructure(classDef)
            case _ =>
        }
    }

    private def retrievePackageStructure(packageDef: PackageDef) {
        packageDefMap += packageDef.symbol.fullName -> packageDef

        // Retrieve structure of child items.
        packageDef.children.foreach(retrieveStructure)
    }

    private def retrieveClassStructure(classDef: ClassDef) {
        val symbol = classDef.symbol
        val name = symbol.fullName

        // TODO maybe support of synthetic symbols will be needed
        if (!symbol.isSynthetic) {
            val classDefCompiler: ClassDefCompiler =
                if (classDef.symbol.isPackageObjectClass) {
                    new PackageObjectClassCompiler(classDef)
                } else if (classDef.symbol.isModuleClass) {
                    new ModuleClassCompiler(classDef)
                } else {
                    new ClassCompiler(classDef)
                }
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
                    if (!isInternalType(parentClassSymbol) && symbol.sourceFile.name == parentClassSymbol.sourceFile.name) {
                        dependencies += parentClassSymbol.fullName
                    }
            }

            // Retrieve structure of child items.
            classDef.impl.body.foreach(retrieveStructure)
        }
    }

    private def compilePackageStructure() {
        // For packages in format "package p.q.r.s { ... }", all parent packages ("p", "p.q", "p.q.r") also have to be
        // initialized if they aren't already defined.
        val allPackagesNames = new HashSet[String]
        packageDefMap.values.foreach {
            packageDef =>
                var packageSymbol = packageDef.symbol
                while (packageSymbol.fullName != "package <root>") {
                    allPackagesNames += packageSymbol.fullName
                    packageSymbol = packageSymbol.owner
                }
        }

        allPackagesNames.toArray.sortBy(packageName => packageName).foreach {
            packageName =>
                buffer += "if(typeof %s === 'undefined') %s = {};\n".format(packageName, packageName)
        }
    }

    def compileClassDefs() {
        while (!classDefDependencyGraph.isEmpty && !classDefDependencyGraphContainsCycle) {
            val className = classDefDependencyGraph.toArray.filter(_._2.isEmpty).map(_._1).sortBy(name => name).head

            // Compile the class.
            classDefMap.get(className).get.compile()

            // Remove the compiled class from dependencies of all classDefs, that aren't compiled yet. Also remove the
            // compiled class from the working sets.
            classDefDependencyGraph.foreach(_._2 -= className)
            classDefDependencyGraph.remove(className)
            classDefMap.remove(className)
        }

        // If there are some classDefs left, then there is a cyclic dependency.
        if (!classDefDependencyGraph.isEmpty) {
            error("Illegal cyclic dependency in the class/object declaration graph.")
        }
    }

    def classDefDependencyGraphContainsCycle: Boolean = {
        if (classDefDependencyGraph.isEmpty) {
            false
        } else {
            // If there isn't a classDef without dependencies, then starting in any ClassDef, sooner or later we arrive
            // to an already visited.
            !classDefDependencyGraph.exists(_._2.isEmpty)
        }
    }

    private abstract class ClassDefCompiler(val classDef: ClassDef)
    {
        val nameSymbol = classDef.symbol
        val memberContainerName: String;
        val parentClasses = classDef.impl.parents
        val parentClass = parentClasses.head
        val parentClassIsInternal = isInternalType(parentClass.symbol)
        val inheritedTraits = parentClasses.tail

        def compile() {
            DependencyManager.addProvidedSymbol(nameSymbol)

            compileConstructor()
            compileInheritedTraitMembers()
        }

        def compileConstructor(): Unit

        def compileInheritedTraitMembers() {
            inheritedTraits.foreach {
                traitAst =>
                    traitAst.tpe.members.foreach {
                        traitMemberSymbol =>
                            if (!isIgnoredMember(traitMemberSymbol)) {
                                buffer += "%s.%s = %s.prototype.%s;\n".format(
                                    memberContainerName,
                                    traitMemberSymbol.nameString,
                                    traitAst.symbol.fullName,
                                    traitMemberSymbol.nameString
                                )
                            }
                    }
            }
        }

        def compileMembers() {
            compileValDefMembers()
            compileDefDefMembers()
        }

        def compileValDefMembers() {
            classDef.impl.body.filter(_.isInstanceOf[ValDef]).foreach(compileMember)
        }

        def compileDefDefMembers() {
            classDef.impl.body.filter(_.isInstanceOf[DefDef]).foreach(compileMember)
        }

        def isIgnoredMember(member: Symbol): Boolean = {
            isInternalTypeMember(member) ||
            member.isConstructor ||
            member.hasFlag(ACCESSOR) ||
            member.nameString.contains("default$")
        }

        def compileMember(memberAst: Tree) {
            if (memberAst.hasSymbol && !isIgnoredMember(memberAst.symbol)) {
                memberAst match {
                    case valDef: ValDef => compileValDef(valDef)
                    case defDef: DefDef => compileDefDef(defDef)
                    case _: ClassDef => // NOOP, wrapped classes are compiled separately
                    case _ => {
                        println("[s2js-warning] Unknown member %s of type %s".format(
                            memberAst.toString,
                            memberAst.getClass
                        ))
                    }
                }
            }
        }

        def compileValDef(valDef: ValDef, containerName: String = memberContainerName) {
            buffer += "%s.%s = ".format(containerName, valDef.symbol.nameString)
            compileAst(valDef.rhs)
            buffer += ";\n"
        }

        def compileDefDef(defDef: DefDef) {
            // TODO maybe transform name in special cases.
            buffer += "%s.%s = function(%s) {\n".format(
                memberContainerName,
                defDef.symbol.nameString,
                getDefDefCompiledParameters(defDef)
            )
            buffer += "var self = this;\n"

            compileDefDefDefaultParameters(defDef)
            compileDefDefBody(defDef);

            buffer += "};\n"

            /* TODO implement when needed
            if (ts.symbol.annotations exists {
                _.toString == "s2js.ExportSymbol"
            }) {
                l += "goog.exportSymbol('%1$s', %1$s);".format(ns + "." + name)
            }*/
        }

        def getDefDefCompiledParameters(defDef: DefDef): String = {
            defDef.vparamss.flatten.map(_.symbol.nameString).mkString(",")
        }

        def compileDefDefDefaultParameters(defDef: DefDef) {
            defDef.vparamss.flatten.filter(_.symbol.hasDefault).foreach {
                parameter =>
                    buffer += "if (typeof(%1$s) === 'undefined') { %1$s = %2$s; };\n".format(
                        parameter.nameString,
                        buildTree(parameter.asInstanceOf[ValDef].rhs)
                    )
            }
        }

        def compileDefDefBody(defDef: DefDef) {
            val preCompileBufferLength = buffer.length
            val bodyAst = defDef.rhs

            bodyAst match {
                case blockBody: Block => compileBlock(blockBody)
                case matchBody: Match => // compileMatch(y)
                case _ => compileAst(defDef.rhs)
            }

            // If the function returns something and the body contains a statement, prepend the last statement with the
            // "return" keyword.
            if (defDef.tpt.symbol.nameString != "Unit" && preCompileBufferLength < buffer.length) {
                val lastStatementIndex = buffer.length - 1
                buffer.update(lastStatementIndex, "return " + buffer(lastStatementIndex));
            }
        }

        def compileAst(ast: Tree) {
            buffer += buildTree(ast)
        }

        def compileBlock(block: Block) {
            block.stats.foreach(compileAst(_))
            compileAst(block.expr)
        }
    }

    private class ClassCompiler(classDef: ClassDef) extends ClassDefCompiler(classDef)
    {
        override lazy val memberContainerName = nameSymbol.fullName + ".prototype"
        val initializedValDefSet = new HashSet[String]

        override def compile() {
            super.compile()

            compileDefDefMembers()
            /*
            val caseMemberNames = List("productPrefix", "productArity", "productElement", "equals", "toString", "canEqual", "hashCode", "copy")

            def isCaseMember(x: Tree): Boolean = {
                caseMemberNames.exists(x.symbol.fullName.endsWith(_))
            }

            def isSynthetic(x: Tree): Boolean = {
                x.symbol.isSynthetic
            }

            def isValidMember(x: Tree): Boolean = {
                x.isInstanceOf[ValDef] || (x.hasSymbol && x.symbol.hasFlag(ACCESSOR))
            }

            if (t.symbol.hasFlag(CASE)) {
                //lb ++= t.impl.body.filterNot(isCaseMember).map(buildPackageLevelItemMember)
            } else if (t.symbol.isTrait) {
                // lb ++= t.impl.body.map(buildPackageLevelItemMember)
            } else {
                // lb ++= t.impl.body.filterNot(isValidMember).map(buildPackageLevelItemMember)
            }

            return lb.mkString*/
        }

        def compileConstructor() {
            val primaryConstructors = classDef.impl.body.filter(ast => ast.hasSymbol && ast.symbol.isPrimaryConstructor)
            val hasConstructor = !primaryConstructors.isEmpty
            val constructorDefDef = if (hasConstructor) primaryConstructors.head.asInstanceOf[DefDef] else null
            val compiledParameters = if (hasConstructor) getDefDefCompiledParameters(constructorDefDef) else ""

            buffer += "%s = function(%s) {\n".format(nameSymbol.fullName, compiledParameters)
            buffer += "var self = this;\n"

            if (hasConstructor) {
                compileDefDefDefaultParameters(constructorDefDef)

                // Initialize vals specified as the implicit constructor parameters.
                constructorDefDef.vparamss.flatten.map(_.name.toString).foreach {
                    parameterName =>
                        initializedValDefSet += parameterName
                        buffer += "self.%1$s = %1$s;\n".format(parameterName)
                }
            }

            // Initialize vals that aren't implicit constructor parameters.
            classDef.impl.foreach {
                case valDef: ValDef if !initializedValDefSet.contains(valDef.symbol.nameString) => {
                    compileValDef(valDef, "self")
                }
                case _ =>
            }

            // Call the parent class constructor.
            if (!parentClassIsInternal) {
                classDef.impl.foreach {
                    case Apply(Select(Super(_, _), name), args) if (name.toString == "<init>") => {
                        val parentClassParameters = args.filter(!_.toString.contains("$default$")).map(_.toString)
                        buffer += "%s.call(self,%s);".format(
                            parentClass.symbol.fullName,
                            if (parentClassParameters.isEmpty) "undefined" else parentClassParameters.mkString(",")
                        )
                    }
                    case _ =>
                }
            }

            // Compile the constructor body.
            classDef.impl.body.filter(!_.isInstanceOf[ValOrDefDef])foreach {
                ast =>
                    compileAst(ast)
                    buffer += ";\n"
            }

            buffer += "};\n"

            if (!parentClassIsInternal) {
                buffer += "goog.inherits(%s, %s);\n".format(nameSymbol.fullName, parentClass.symbol.fullName)
            }
        }
    }

    private class TraitCompiler(classDef: ClassDef) extends ClassCompiler(classDef)
    {
        override def compileConstructor() {
            buffer += "%s = function() {};\n".format(nameSymbol.fullName)
        }
    }

    private class ModuleClassCompiler(classDef: ClassDef) extends ClassDefCompiler(classDef)
    {
        override lazy val memberContainerName = nameSymbol.fullName

        override def compile() {
            super.compile()

            compileMembers()
        }

        def compileConstructor() {
            if (!parentClassIsInternal) {
                compileStaticConstructor()
            } else {
                buffer += "%s = {};\n".format(nameSymbol.fullName)
            }
        }

        def compileStaticConstructor() {
            buffer += "%s = %s;\n".format(nameSymbol.fullName, parentClassInstance)
        }

        def parentClassInstance: String = {
            var instance = ""
            classDef.impl.foreach {
                case Apply(Select(Super(_, _), name), args) if (name.toString == "<init>") => {
                    instance = "new %s(%s)".format(parentClass.symbol.fullName, compileArguments(args))
                }
                case _ =>
            }

            instance
        }
    }

    private class PackageObjectClassCompiler(classDef: ClassDef) extends ModuleClassCompiler(classDef)
    {
        override val nameSymbol = classDef.symbol.owner

        override def compileStaticConstructor() {
            // The package object may be declared before or after some other members of the package were declared.
            // If it's declared after, the previous declarations shouldn't be overriden.
            buffer += "goog.object.extend(%s, %s);\n".format(nameSymbol.fullName, parentClassInstance)
        }
    }


















    def compileArguments(args: List[Tree]): String = {
        args.map(buildTree).mkString(",")
    }

    case class RichTree(t: Tree)
    {
        val ownerName = t.symbol.owner.fullName
        val nameString = t.symbol.nameString
        val isModuleClass = t.symbol.owner.isModuleClass
    }

    implicit def treeToRichTree(tree: Tree): RichTree = {
        RichTree(tree)
    }

    val cosmicNames = List(
        "java.lang.Object",
        "scala.ScalaObject",
        "scala.Any",
        "scala.AnyRef",
        "scala.Product")

    def isCosmicType(x: Tree): Boolean = {
        cosmicNames.contains(x.symbol.fullName)
    }

    def isLocalMember(x: Symbol): Boolean = {
        x.isLocal
    }

    def isCosmicMember(x: Symbol): Boolean = {
        cosmicNames.contains(x.enclClass.fullName)
    }

    object BinaryOperator
    {
        def unapply(name: Name): Option[String] = {
            Map(
                "$eq$eq" -> "==",
                "$bang$eq" -> "!=",
                "$greater" -> ">",
                "$greater$eq" -> ">=",
                "$less" -> "<",
                "$less$eq" -> "<=",
                "$amp$amp" -> "&&",
                "$plus" -> "+",
                "$minus" -> "-",
                "$percent" -> "%",
                "$bar$bar" -> "||").get(name.toString)
        }
    }

    /*def findDependencies(ast: Tree): HashSet[String] = {
        def traverse(t: Tree): Unit = {
            t match {
                case x@ValDef(_, _, _, rhs) => {
                    rhs match {
                        case y@Block(stats, expr) => {
                            stats.foreach(traverse)
                            traverse(expr)
                        }
                        case y => {
                            traverse(y)
                        }
                    }
                }

                case x@DefDef(_, _, _, vparamss, _, rhs) => {
                    rhs match {
                        case y@Block(stats, expr) => {
                            stats.foreach(traverse)
                            traverse(expr)
                        }
                        case y => {
                            traverse(y)
                        }
                    }
                }

                case x@Apply(Select(q, _), args) if (q.toString.endsWith("Predef")) => {
                    args.foreach(traverse)
                }

                // make sure we check all function calls for needed imports
                case x@Apply(fun, args) => {
                    if (!nonDependencies.exists(fun.symbol.fullName.contains)) {
                        traverse(fun)
                    }

                    args.foreach(traverse)
                }

                case x@Select(q, _) if (q.toString.endsWith("package")) => {
                    if (!nonDependencies.exists(x.symbol.fullName.contains)) {
                        dependecyHashSet += buildName(q.symbol.owner)
                    }
                }

                case x@Select(New(tpe), _) => {
                    tpe match {
                        case y@Select(Select(_, _), _) => {
                            dependecyHashSet += buildName(y.symbol)
                        }
                        case y@TypeTree() => {
                            if (currentFile != y.symbol.sourceFile) {
                                dependecyHashSet += buildName(y.symbol)
                            }
                        }
                        case y => {
                            if (currentFile != y.symbol.sourceFile) {
                                dependecyHashSet += buildName(y.symbol)
                            }
                        }
                    }
                }

                case x@Select(Select(_, _), _) if (!x.symbol.isPackage) => {
                    if (!nonDependencies.exists(x.symbol.fullName.contains)) {
                        dependecyHashSet += buildName(x.symbol.owner)
                    }
                }

                case x@Select(_, _) => {
                    if (!nonDependencies.exists(x.symbol.fullName.contains)) {
                        if (x.symbol.sourceFile != currentFile) {
                            dependecyHashSet += buildName(x.symbol.owner)
                        }
                    }
                }

                case x@Ident(name) =>

                case x => {
                    x.children.foreach(traverse)
                }
            }
        }

        traverse(ast)

        dependecyHashSet.toSet
        new HashSet[String]()
    }*/

    def isAnIgnoredMember(tree: Tree): Boolean = {
        if (tree.hasSymbol) {
            false
            //List("readResolve", "copy$default$1", "this").contains(tree.symbol.nameString) || isDefaultThing(tree)
        } else {
            false
        }
    }

    def buildSyntheticMember(t: Tree): String = {
        t match {
            case x@DefDef(mods, _, _, _, _, rhs) => {
                buildMethod(x, x.symbol.owner.isPackageObjectClass)
            }
            case x => {
                "nonthing"
            }
        }
    }

    def isDefaultNull(t: DefDef): Boolean = {
        !(t.symbol.nameString.contains("default$") && t.rhs.toString == "null") && !t.mods.hasAccessorFlag && !t.symbol.isConstructor
    }

    def buildSyntheticModule(moduleDef: ModuleDef): String = {

        val lb = new ListBuffer[String]

        val objectNAme = moduleDef.symbol.fullName

        def neededSyntheticMember(x: Tree) = {
            x.hasSymbol && List("unapply", "apply").exists(x.symbol.fullName.endsWith(_))
        }

        //lb += moduleDef.impl.filter(neededSyntheticMember).map(buildPackageLevelItemMember).mkString

        lb.mkString
    }

    def buildTree(t: Tree): String = {
        t match {

            case x@Literal(Constant(value)) => {
                value match {
                    case v: String => {
                        "'" + v + "'"
                    }
                    case v: Unit => {
                        ""
                    }
                    case null => {
                        "null"
                    }
                    case v => {
                        v.toString
                    }
                }
            }

            case x@Return(expr) => {
                "return " + buildTree(expr)
            }

            case x@Apply(TypeApply(y@Select(Select(_, n), _), _), args) if (n.toString == "Array") => {
                args.map(buildObjectLiteral).mkString("[", ",", "]")
            }

            case x@Apply(TypeApply(y@Select(Select(_, n), _), _), args) if (n.toString.matches("Tuple[0-9]+")) => {
                args.zipWithIndex map {
                    a => "'_%s':%s".format((a._2 + 1), a._1.toString.replace("\"", "'"))
                } mkString("{", ",", "}")
            }

            case x@Apply(Select(qualifier, BinaryOperator(op)), args) => {
                "(%s %s %s)".format(
                    buildTree(qualifier), op, args.map(buildTree).mkString)
            }

            case x: ApplyToImplicitArgs => {
                x.fun match {
                    case y => {
                        buildTree(y)
                    }
                }
            }

            case x@Apply(fun@Select(q, n), args) if fun.toString == "scalosure.script.literal" => {
                args.mkString.replace("\\'", "'").replace("\"", "")
            }

            case x@Apply(Select(qualifier, name), args) if qualifier.toString == "s2js.Html" => {
                "%s".format(buildXmlLiteral(args.head).mkString)
            }

            case x@Apply(Select(qualifier, name), args) if name.toString.endsWith("_$eq") => {
                "%s.%s = %s".format(buildTree(qualifier),
                    name.toString.stripSuffix("_$eq"), args.map(buildTree).mkString)
            }

            case x@Apply(Select(y@Super(_, _), name), args) => {
                "%s.superClass_.%s.call(%s)".format(y.symbol.fullName, name.toString, (List("self") ++ args.map(buildTree)).mkString(","))
            }

            case x@Apply(TypeApply(f, _), args) if (f.symbol.owner.nameString == "ArrowAssoc") => {
                "{%s}".format(buildObjectLiteral(x))
            }

            case x@Apply(fun, args) => {
                val argumentList = x.symbol.paramss

                def buildArgs(t: Tree): List[Tree] = {
                    t match {
                        case Apply(f, xs) => {
                            buildArgs(f) ++ xs
                        }
                        case _ => {
                            Nil
                        }
                    }
                }

                val passedArgs = (buildArgs(fun) ++ args) filterNot {
                    _.toString.contains("$default$")
                }

                def buildAnArg(t: Tree): String = {
                    t match {
                        case x@Function(_, _) => {
                            buildTree(x)
                        }
                        case x@Block(stats, y@Function(vparams, Apply(f, as))) => {
                            "function(%s) {return %s(%s)}".format(as.mkString("_", ",", "_"), buildTree(f), as.mkString("_", ",", "_"))
                        }
                        case x => {
                            buildTree(x)
                        }
                    }
                }

                val processedArgs = passedArgs.zip(x.symbol.paramss.flatten) map {
                    //case (passed, defined) if defined.tpe.typeSymbol.nameString.matches("""(Function0|\<byname\>)""") => debug("1b", passed); "function() {%s}".format(buildTree(passed))
                    case (passed, defined) => {
                        buildAnArg(passed)
                    }
                }

                def ownerName(t: Tree) = {
                    if (fun.hasSymbol) {
                        Some(fun.symbol.owner.nameString)
                    } else {
                        None
                    }
                }

                val tmp = ownerName(fun) match {
                    case Some("JsArray") if fun.symbol.nameString == "apply" => {
                        "%s[%s]"
                    }
                    case _ => {
                        "%s(%s)"
                    }
                }

                def buildApply(f: Tree) = {
                    tmp.format(buildTree(f), processedArgs.mkString(","))
                }

                def isVarArgs(t: Tree): Boolean = {
                    t.tpe.params.headOption match {
                        case Some(firstParam) => {
                            firstParam.tpe.toString.matches("""\(String, [^)].*\)\*""")
                        }
                        case None => {
                            false
                        }
                    }
                }

                def isArrayArg(t: Tree): Boolean = {
                    t.tpe.params.headOption match {
                        case Some(firstParam) => {
                            firstParam.tpe.toString.matches("""[a-zA-Z0-9]+\*""")
                        }
                        case None => {
                            false
                        }
                    }
                }

                fun match {
                    case TypeApply(f@Select(_, _), _) if isVarArgs(f) => {
                        "%s(%s)".format(buildTree(f), args map {
                            buildObjectLiteral
                        } mkString("{", ",", "}"))
                    }
                    case TypeApply(f@Select(_, _), _) if isArrayArg(f) => {
                        "%s(%s)".format(buildTree(f), args map {
                            buildObjectLiteral
                        } mkString("[", ",", "]"))
                    }
                    case Apply(f@Select(_, _), _) if isVarArgs(f) => {
                        "%s(%s)".format(buildTree(f), args map {
                            buildObjectLiteral
                        } mkString("{", ",", "}"))
                    }
                    case Apply(f@Select(_, _), _) if isArrayArg(f) => {
                        "%s(%s)".format(buildTree(f), args map {
                            buildObjectLiteral
                        } mkString("[", ",", "]"))
                    }
                    case Apply(f, xs) => {
                        buildApply(f)
                    }
                    case f@Select(_, _) if isVarArgs(f) => {
                        "%s(%s)".format(buildTree(f), args map {
                            buildObjectLiteral
                        } mkString("{", ",", "}"))
                    }
                    case f@Select(_, _) if isArrayArg(f) => {
                        "%s(%s)".format(buildTree(f), args map {
                            buildObjectLiteral
                        } mkString("[", ",", "]"))
                    }
                    case f@Select(q, n) if f.symbol.owner.fullName == "scala.Array" => {
                        tmp.format(buildTree(q), processedArgs.mkString(","))
                    }
                    case y => {
                        buildApply(fun)
                    }
                }
            }

            case x@TypeApply(Select(q, n), args) if (n.toString == "asInstanceOf") => {
                buildTree(q)
            }

            case x@TypeApply(fun, args) => {
                buildTree(fun)
            }

            case x@ValDef(mods, name, tpt, rhs) if (x.symbol.isLocal) => {
                "var %s = %s".format(
                    x.symbol.nameString, rhs match {
                        case y@Match(_, _) => {
                            buildSwitch(y)
                        }
                        case y@Select(q, n) if n.toString == "unary_$bang" => {
                            "!" + buildTree(q)
                        }
                        case y => {
                            buildTree(y)
                        }
                    })
            }

            case x@Ident(name) => {
                if (x.symbol.isLocal) {
                    x.symbol.nameString
                } else {
                    x.symbol.fullName
                }
            }

            case x@If(cond, thenp, elsep) => {
                buildIf(x, (x.tpe.typeSymbol.nameString != "Unit"))
            }

            case x@Function(vparams, body) => {
                val args = vparams.map(_.symbol.nameString).mkString(",")

                // does the body have a single expression or a block of expressions
                val impl = body match {
                    case y@Block(_, _) => {
                        buildBlock(y)
                    }
                    case y => {
                        buildExpression(y, body.tpe.toString != "Unit")
                    }
                }

                "function(%s) {\n%s}".format(args, impl)
            }

            case EmptyTree => {
                "null"
            }

            case x@Select(qualifier, name) if (name.toString == "package") => {
                buildTree(qualifier)
            }

            case x@Select(qualifier, name) => {
                qualifier match {
                    case y@New(tt) => {
                        "new " + (if (tt.toString.startsWith("js.browser")) {
                            tt.symbol.nameString
                        } else {
                            scala2scalosure(tt.symbol)
                        })
                    }
                    case y@Ident(_) if (name.toString == "apply" && x.symbol.owner.isSynthetic) => {
                        "%s.appli".format(
                            if (y.symbol.isLocal) {
                                y.symbol.nameString
                            } else {
                                y.symbol.fullName
                            })
                    }
                    case y@Ident(_) if (y.name.toString == "js.browser") => {
                        name.toString
                    }
                    case y@Ident(_) if name.toString == "apply" && y.symbol.isModule => {
                        if (y.symbol.isLocal) {
                            y.symbol.nameString + "." + translateName(name)
                        } else {
                            y.symbol.fullName + "." + translateName(name)
                        }
                    }
                    case y@Ident(_) if name.toString == "apply" => {
                        if (y.symbol.isLocal) {
                            y.symbol.nameString
                        } else {
                            y.symbol.fullName
                        }
                    }
                    case y@This(_) if (x.symbol.owner.isPackageObjectClass) => {
                        y.symbol.owner.fullName + "." + name
                    }
                    case y@This(_) if (x.symbol.owner.isModuleClass) => {
                        translateName(y.symbol.fullName) + "." + name
                    }
                    case y@This(_) => {
                        "self." + name
                    }
                    case y@Select(q, n) if name.toString == "apply" && y.symbol.isModule => {
                        if (y.symbol.isLocal) {
                            y.symbol.nameString.replace("scala", "scalosure") + "." + translateName(name)
                        } else {
                            scala2scalosure(y.symbol) + "." + translateName(name)
                        }
                    }
                    case y@Select(q, n) if name.toString == "apply" => {
                        if (y.symbol.isLocal) {
                            buildTree(y) + "." + translateName(name)
                        } else {
                            if (y.tpe.typeSymbol.nameString.matches("Function[0-9]")) {
                                buildTree(y)
                            } else {
                                buildTree(y) + "." + translateName(name)
                            }
                        }
                    }
                    case y@Select(q, n) if (n.toString == "Predef" && name.toString == "println") => {
                        "console.log"
                    }
                    case y if (name.toString == "$colon$plus" && y.symbol.nameString == "genericArrayOps") => {
                        "%s.push".format(
                            y.asInstanceOf[ApplyImplicitView].args.head)
                    }
                    case y if (name.toString == "unary_$bang") => {
                        "!" + buildTree(y)
                    }
                    case y if (name.toString == "apply") => {
                        buildTree(y)
                    }
                    case y if (name.toString == "any2ArrowAssoc") => {
                        buildTree(y)
                    }
                    case y => {
                        // The browser package is an implicit => there is no namespace needed in js.
                        (if (y.hasSymbol && y.symbol.fullName == "js.browser.package") {
                            ""
                        } else {
                            buildTree(y) + "."
                        }) + name
                    }
                }
            }

            case x@Block(stats, expr) => {
                buildBlock(x)
            }

            case x@This(n) => {
                if (x.symbol.isModuleClass) {
                    n.toString
                } else {
                    "self"
                }
            }

            case x@Assign(lhs, rhs) => {
                "%s = %s".format(buildTree(lhs), buildTree(rhs))
            }

            // TODO: need to finish
            case x@Try(block, catches, finalizer) => {
                "try {\n%s\n} catch(err) {\n%s\n}".format(buildTree(block), "", "")
            }

            case x@LabelDef(name, params, rhs) if (name.toString.startsWith("while")) => {
                val If(cond, thenp, _) = rhs

                val transformedThen = thenp match {
                    case y@Block(stats, expr) => {
                        stats.map(buildTree).mkString
                    }
                    case y => {
                        buildTree(y)
                    }
                }

                "while(%s) {%s}".format(buildTree(cond), transformedThen)
            }

            case x => {
                x match {
                    case y@Match(_, _) => {
                        buildSwitch(y)
                    }
                    case x@New(tpe: TypeTree) => {
                        "new %s".format(tpe.symbol.fullName)
                    }
                    case y@TypeApply(fun, args) => {
                        ""
                    }
                    case y@Typed(expr, tpt) if tpt.toString == "_*" => {
                        expr.toString
                    }
                    case y: TypeTree => {
                        "typetree"
                    }
                    case y => {
                        println(y.getClass);
                        "#NOT IMPLEMENTED#"
                    }
                }
            }
        }
    }

    def buildExpression(t: Tree, hasReturn: Boolean = true): String = {
        buildTree(t) match {
            case z if (t.tpe.toString == "Unit") => {
                if (z == "") {
                    ""
                } else if (hasReturn) {
                    "return %s;\n".format(z)
                } else {
                    "%s;\n".format(z)
                }
            }
            case z => {
                if (hasReturn) {
                    "return %s;\n".format(z)
                } else {
                    "%s;\n".format(z)
                }
            }
        }
    }

    def buildBlock(t: Block): String = {
        val stats = t.stats map {
            buildTree
        } map {
            _ + ";\n"
        }
        stats.mkString + buildExpression(t.expr, t.tpe.toString != "Unit")
    }

    def buildIf(t: If, hasReturn: Boolean): String = {

        def buildTreeReturn(t2: Tree) = {
            if (hasReturn) {
                "return %s".format(buildTree(t2))
            } else {
                buildTree(t2)
            }
        }

        val transformedThen = t.thenp match {
            case y@Block(_, _) => {
                buildBlock(y)
            }
            case y => {
                buildTreeReturn(y) + ";\n"
            }
        }

        val transformedElse = t.elsep match {
            case y@Block(_, _) => {
                buildBlock(y)
            }
            case y => {
                buildTreeReturn(y) + ";\n"
            }
        }

        "%s ? function() {\n%s}() : function() {\n%s}()".format(buildTree(t.cond), transformedThen, transformedElse)
    }

    def buildSwitch(t: Tree): String = {

        val sb = new StringBuilder

        sb.append("\nvar matched;\n")
        val Match(selector, cases) = t

        def buildTheBody(body: Tree) = {
            "return %s".format(buildTree(body))
        }

        cases.zipWithIndex foreach {

            case (a, b) => {

                val tmp = b match {
                    case 0 => {
                        "if(%s) {\n%s\n}"
                    }
                    case x => {
                        "else if(%s) {\n%s\n}"
                    }
                }

                a match {
                    case CaseDef(pat, guard, body) => {
                        pat match {
                            case x@Literal(Constant(_)) => {
                                sb.append(tmp.format(buildTree(selector) + " == " + buildTree(x), buildTheBody(body)))
                            }
                            case x@Ident(n) if (n.toString == "_") => {
                                sb.append(" else {%s}".format(buildTheBody(body)))
                            }
                            case x@Bind(n, b) if b.tpe.toString == "String" => {
                                sb.append(
                                    tmp.format("typeof %s == 'string'".format(buildTree(selector)), buildTheBody(body)))
                            }
                            case x@Bind(n, b) if b.tpe.toString.matches("(Int|Long|Double|java.lang.Number)") => {
                                sb.append(
                                    tmp.format("typeof %s == 'number'".format(buildTree(selector)), buildTheBody(body)))
                            }
                            case x@Bind(n, b) if b.tpe.toString == "Boolean" => {
                                sb.append(
                                    tmp.format("typeof %s == 'boolean'".format(buildTree(selector)), buildTheBody(body)))
                            }
                            case x@Bind(n, b) if b.tpe.toString == "Any" => {
                                sb.append(" else {%s}".format(buildTheBody(body)))
                            }
                            case x@Bind(n, Typed(expr, tpt)) => {
                                sb.append(
                                    tmp.format("typeof %s == 'string'".format(buildTree(selector)), "return function(%s) {%s}(%s)".format(n.toString, buildTheBody(body), buildTree(selector))))
                            }
                            /*
                            case x@Bind(n, b) => {
                                val bindList = buildMatchBindList(x)
                                val bindListArgs = bindList.mkString(",")
                                val bindListValues = bindList.zipWithIndex.map {
                                    y => "matched[%s]".format(y._2)
                                } mkString (",")
                                sb.append(
                                    tmp.format(processMatch(selector, x, guard, body, true), "return function(%s) {%s}(%s)".format(bindListArgs, buildTree(body), bindListValues)))
                            }
                            case x@Apply(f, as) => {
                                val bindList = buildMatchBindList(x)
                                val bindListArgs = bindList.mkString(",")
                                val bindListValues = bindList.zipWithIndex.map {
                                    y => "matched[%s]".format(y._2)
                                } mkString (",")
                                sb.append(
                                    tmp.format(processMatch(selector, x, guard, body, false), "return function(%s) {%s}(%s)".format(bindListArgs, buildTree(body), bindListValues)))
                            }*/
                            case x => {
                                sb.append("f" + x.getClass)
                            }
                        }
                    }
                    case _ => {
                        sb.append("not here 2")
                    }
                }
            }

            case _ => {
                sb.append("not here 3")
            }
        }

        "function() {%s}()".format(sb.toString)
    }

    /*
    def classType(f: Tree) = {
        JsFunction(f.tpe.finalResultType.toString)
    }

    def toJsType(t: String) = {
        t match {
            case "String" => {
                JsFunction("String")
            }
            case "Int" | "Long" | "Double" => {
                JsFunction("Number")
            }
            case other => {
                JsFunction(other)
            }
        }
    }

    def toJsValue(t: Any) = {
        t match {
            case x: String => {
                JsString(x)
            }
            case x: Number => {
                JsNumber(x)
            }
            case x: Boolean => {
                JsBoolean(x)
            }
            case x => {
                JsObject(Nil)
            }
        }
    }

    def processMatch(selector: Tree, pat: Tree, guard: Tree, body: Tree, isBound: Boolean): String = {

        val matchit = "matched = scalosure.matchit(%s,%s)".format(buildTree(selector), scala2js(processMatchPat(pat, isBound)))

        val bindList = buildMatchBindList(pat)
        val bindListArgs = bindList.mkString(",")
        val bindListValues = bindList.zipWithIndex.map {
            x => "matched[%s]".format(x._2)
        } mkString (",")

        if (guard.toString != "<empty>") {
            "(%s) && function(%s) {return %s}(%s)".format(matchit, bindListArgs, buildTree(guard), bindListValues)
        } else {
            matchit
        }
    }

    def buildMatchBindList(t: Tree, binds: List[String] = Nil): List[String] = {
        t match {
            case x@Apply(f, xs) => {
                xs.map {
                    y => buildMatchBindList(y, binds)
                } flatten
            }
            case x@Bind(n, b) => {
                binds ++ List(n.toString) ++ buildMatchBindList(b)
            }
            case x => {
                binds
            }
        }
    }

    def buildMetaType(tpe: JsType, bind: Boolean, children: List[JsType], cond: JsType = null) = {
        JsObject(
            "type" -> tpe :: "bind" -> JsBoolean(bind) :: "children" -> JsArray(children) :: "cond" -> (if (cond == null) {
                JsFunction("null")
            } else {
                cond
            }) :: Nil)
    }

    def processMatchPat(t: Tree, isBound: Boolean): JsType = {
        t match {
            case x@Apply(f, xs) => {
                buildMetaType(classType(x), isBound, xs map {
                    x => processMatchPat(x, false)
                })
            }
            case x@Bind(n, b) => {
                processMatchPat(b, true)
            }
            case x@Ident(n) if n.toString == "_" => {
                buildMetaType(toJsType(x.tpe.toString), isBound, Nil)
            }
            case x@Literal(Constant(v)) => {
                buildMetaType(toJsType(x.tpe.typeSymbol.nameString), isBound, Nil, toJsValue(v))
            }
            case x => {
                println(x.getClass);
                JsObject(Nil)
            }
        }
    }*/

    def translateName(s: Name): String = {
        s.toString match {
            case "apply" => {
                "appli"
            }
            case "scala" => {
                "scalosure"
            }
            case x => {
                x
            }
        }
    }

    def scala2scalosure(s: Symbol): String = {
        s.fullName.replace("scala", "scalosure")
    }

    def buildName(s: Symbol): String = {
        s.nameString match {
            case "+=" => {
                "$plus$eq"
            }
            case "apply" => {
                "appli"
            }
            case x => {
                x
            }
        }
    }

    def buildXmlLiteral(t: Tree): String = {
        t match {

            case x@Block(_, inner@Block(stats, a@Apply(_, _))) => {
                a match {

                    case y@Apply(Select(New(tpt), _), args) if (tpt.toString == "scala.xml.Elem") => {
                        val tag = args(1).toString.replace("\"", "")

                        val attributes = stats.filter {
                            case Assign(_, _) => {
                                true
                            }
                            case _ => {
                                false
                            }
                        }.map(buildXmlLiteral).mkString("{", ",", "}")

                        val children = if (args.length > 4) {
                            buildXmlLiteral(args(4))
                        } else {
                            "[]"
                        }

                        "goog.dom.createDom('%s',%s,%s)".format(
                            tag, attributes, children)
                    }

                    case y => {
                        "nothing"
                    }
                }
            }

            case x@Typed(Block(stats, expr), tpt) => {
                stats.filter {
                    case Apply(_, _) => {
                        true
                    }
                    case _ => {
                        false
                    }
                }.map(buildXmlLiteral).filter(_ != "").mkString("[", ",", "]")
            }

            case x@Apply(fun, args) if (fun.symbol.fullName == "scala.xml.NodeBuffer.$amp$plus") => {
                buildXmlLiteral(args.head)
            }

            case x@Apply(Select(New(tpt), _), args) if (tpt.toString == "scala.xml.Text") => {
                val value = args.head.toString.replaceAll("""(\\012|[ ]{2,}|[\r\n]|")""", "")
                if (value == "") {
                    ""
                } else {
                    "'%s'".format(value)
                }
            }

            case x@Assign(_, Apply(_, List(name, Apply(_, List(value)), _))) => {
                val stripName = name.toString.replace("\"", "")
                "'%s':%s".format(stripName, buildTree(value))
            }

            case x@Assign(_, Apply(_, List(name, value@Select(_, _), _))) => {
                val stripName = name.toString.replace("\"", "")
                "'%s':%s".format(stripName, buildTree(value))
            }

            case x@Assign(_, Apply(_, List(name, value@Ident(_), _))) => {
                val stripName = name.toString.replace("\"", "")
                "'%s':%s".format(stripName, buildTree(value))
            }

            case x => {
                buildTree(x)
            }
        }
    }

    def buildObjectLiteral(t: Tree): String = {
        t match {

            case x@Literal(Constant(value)) => {
                value match {
                    case v: String => {
                        "'" + v + "'"
                    }
                    case x: Unit => {
                        ""
                    }
                    case v => {
                        v.toString
                    }
                }
            }

            case x@Apply(TypeApply(y@Select(Select(_, n), _), _), args) if (n.toString == "Map") => {
                args.map {
                    buildObjectLiteral
                } mkString("{", ",", "}")
            }

            case x@Apply(TypeApply(Select(q, n), _), args) if (n.toString == "$minus$greater") => {

                // this should be a string
                val key = q.asInstanceOf[ApplyImplicitView].args.head.toString.replace("\"", "'")

                // process nested objects
                val values = args map buildObjectLiteral

                "%s:%s".format(key, values.mkString)
            }

            case x: ApplyToImplicitArgs => {
                x.fun match {
                    case y@Apply(TypeApply(Select(n, _), _), args) if (n.toString == "scala.Array") => {
                        args map {
                            buildObjectLiteral
                        } mkString("[", ",", "]")
                    }
                    case y => {
                        y.getClass.toString
                    }
                }
            }

            case x => {
                buildTree(x)
            }
        }
    }

    private def quotedChar(codePoint: Int) = {
        codePoint match {
            case c if c > 0xffff => {
                val chars = Character.toChars(c)
                "\\u%04x\\u%04x".format(chars(0).toInt, chars(1).toInt)
            }
            case c if c > 0x7e => {
                "\\u%04x".format(c.toInt)
            }
            case c => {
                c.toChar
            }
        }
    }

    private def quote(s: String) = {

        val charCount = s.codePointCount(0, s.length)

        "\"" + 0.to(charCount - 1).map {
            idx =>
                s.codePointAt(s.offsetByCodePoints(0, idx)) match {
                    case 0x0d => {
                        "\\r"
                    }
                    case 0x0a => {
                        "\\n"
                    }
                    case 0x09 => {
                        "\\t"
                    }
                    case 0x22 => {
                        "\\\""
                    }
                    case 0x5c => {
                        "\\\\"
                    }
                    case 0x2f => {
                        "\\/"
                    } // to avoid sending "</"
                    case c => {
                        quotedChar(c)
                    }
                }
        }.mkString("") + "\""
    }

    def scala2json(obj: Any): String = {
        obj match {

            case null => {
                "null"
            }
            case x: Boolean => {
                x.toString
            }
            case x: Number => {
                x.toString
            }
            case x: List[_] => {
                x.map {
                    y => scala2json(y)
                }.mkString("[", ",", "]")
            }
            case x: Map[_, _] => {
                x.map {
                    y => quote(y._1.toString) + ":" + scala2json(y._2)
                }.mkString("{", ",", "}")
            }
            case x => {
                quote(x.toString)
            }
        }
    }

    private object DependencyManager
    {
        val requireHashSet = new HashSet[String]
        val provideHashSet = new HashSet[String]

        def addProvidedSymbol(symbol: Symbol) {
            if (!symbol.isSynthetic &&
                symbol.fullName != "<empty>" &&
                (symbol.isPackage || symbol.isClass || symbol.isModuleClass)) {
                provideHashSet.add(symbol.fullName)
            }
        }

        def addRequiredSymbol(symbol: Symbol) {
            if (!isInternalType(symbol)) {
                requireHashSet.add(symbol.fullName)
            }
        }

        def getProvides: String = {
            provideHashSet.toArray.map("goog.addProvidedSymbol('%s');\n".format(_)).mkString
        }

        def getRequires: String = {
            val filteredLocal = requireHashSet.toArray.filter(r => !provideHashSet.contains(r))
            filteredLocal.map("goog.addRequiredSymbol('%s');\n".format(_)).mkString
        }
    }

}