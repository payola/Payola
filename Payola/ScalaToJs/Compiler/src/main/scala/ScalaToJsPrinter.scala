package s2js

import scala.reflect.generic.Flags._

import scala.collection.{ mutable => mu }

import scala.collection.mutable.{
    ListBuffer, StringBuilder
}

import scala.xml.Utility
import scala.tools.nsc.Global
import scala.util.matching.Regex

abstract class JsType
case class JsObject(items:List[(String, JsType)]) extends JsType
case class JsArray(items:List[JsType]) extends JsType
case class JsString(value:String) extends JsType
case class JsNumber(value:Number) extends JsType
case class JsFunction(value:String) extends JsType
case class JsBoolean(value:Boolean) extends JsType

trait ScalaToJsPrinter {
  
    val global:Global

    import global._

    def debug(name:String, thing:Any) {
      //print(name+" ")
      //println(thing.toString)
    }

    case class RichTree(t:Tree) {
        val ownerName = t.symbol.owner.fullName
        val nameString = t.symbol.nameString
        val inModuleClass = t.symbol.owner.isModuleClass
    }

    implicit def tree2richtree(tree:Tree):RichTree = RichTree(tree)

    val cosmicNames = List("java.lang.Object", "scala.ScalaObject", "scala.Any", "scala.AnyRef", "scala.Product")

    def isCosmicType(x:Tree):Boolean = cosmicNames.contains(x.symbol.fullName)
    def isLocalMember(x:Symbol):Boolean = x.isLocal
    def isCosmicMember(x:Symbol):Boolean = cosmicNames.contains(x.enclClass.fullName)

    object BinaryOperator {
        def unapply(name:Name):Option[String] = Map(
            "$eq$eq"      -> "==",
            "$bang$eq"    -> "!=",
            "$greater"    -> ">",
            "$greater$eq" -> ">=",
            "$less"       -> "<",
            "$less$eq"    -> "<=",
            "$amp$amp"    -> "&&",
            "$plus"       -> "+",
            "$minus"      -> "-",
            "$percent"    -> "%",
            "$bar$bar"    -> "||").get(name.toString)
    }

    def tree2string(tree:Tree):String = {

        // using a list buffer for simplicity and to add hard returns for formatting
        val lb = new ListBuffer[String]

        // identifying all the top-level members of the file, which translate to provide statements in closure-library terms
        val provideList = tree.children filter {
            x => (x.isInstanceOf[PackageDef] || x.isInstanceOf[ClassDef] || x.isInstanceOf[ModuleDef]) && !x.symbol.isSynthetic
        }
        
        val provideSet = provideList.foldLeft(Set.empty[String]) { 
            (a,b) => a + "goog.provide('%s');\n".format(b.symbol.fullName) 
        }

        lb += provideSet.mkString

        // determine the necessary dependencies for requires statements
        findRequiresFrom(tree) foreach {
            x => lb += "goog.require('%s');\n".format(x)
        }

        // this starts the big traverse
        // TODO: should be able to do this in the buildTree function
        lb += tree.children.map(buildPackageLevelItem).mkString

        lb.mkString
    }

    def isDefaultThing(tree:Tree):Boolean = (tree.hasSymbol && tree.symbol.nameString.contains("$default$"))

    def isAnIgnoredMember(tree:Tree):Boolean = if(tree.hasSymbol) {
      List("readResolve", "copy$default$1", "this").contains(tree.symbol.nameString) || isDefaultThing(tree)
    } else { false }

    def buildPackageLevelItem(t:Tree):String = t match {
        case x @ ClassDef(_, _, _, _) => buildClass(x)
        case x @ ModuleDef(_, _, _) if(!x.symbol.hasFlag(SYNTHETIC)) => buildModule(x) 
        case x @ ModuleDef(_, _, _) if(x.symbol.hasFlag(SYNTHETIC)) => buildSyntheticModule(x)
        case x @ PackageDef(_, stats) => stats.map(buildPackageLevelItemMember).mkString
        case x =>  ""
    }

    def buildSyntheticMember(t:Tree):String = t match {
      case x @ DefDef(mods, _, _, _, _, rhs) => buildMethod(x, x.symbol.owner.isPackageObjectClass)
      case x => "nonthing"
    }

    def isDefaultNull(t:DefDef):Boolean = !(t.symbol.nameString.contains("default$") && t.rhs.toString == "null") && !t.mods.hasAccessorFlag && !t.symbol.isConstructor

    def buildPackageLevelItemMember(t:Tree):String = t match {
      case x @ DefDef(mods, _, _, _, _, rhs) if isDefaultNull(x) => buildMethod(x, x.symbol.owner.isPackageObjectClass)
      case x @ ValDef(_, _, _, _) => buildField(x, x.symbol.owner.isPackageObjectClass)
      case x @ ModuleDef(_, _, _) => buildPackageLevelItem(x)
      case x => ""
    }

    def buildSyntheticModule(moduleDef:ModuleDef):String = {

      val lb = new ListBuffer[String]

      val objectNAme = moduleDef.symbol.fullName

      def neededSyntheticMember(x:Tree) = x.hasSymbol && List("unapply", "apply").exists(x.symbol.fullName.endsWith(_))

      lb += moduleDef.impl.filter(neededSyntheticMember).map(buildPackageLevelItemMember).mkString

      lb.mkString
    }

    def buildModule(moduleDef:ModuleDef):String = {

      val lb = new ListBuffer[String]

      val objectName = moduleDef.symbol.fullName

      val superClass = moduleDef.impl.parents filterNot {
        x => List("java.lang.Object", "scala.ScalaObject").contains(x.symbol.fullName) 
      } headOption

      moduleDef.impl.foreach { 
        case x @ Apply(Select(Super(qual, mix), name), args) if(name.toString == "<init>") => superClass match {
          case Some(sc) => lb += "%s = new %s(%s);\n".format(objectName, sc.symbol.fullName, args.map(buildTree).mkString(","))
          case None => 
        }
        case _ => 
      }

      val superClasses = moduleDef.impl.parents filterNot {
        x => List("java.lang.Object", "scala.ScalaObject").contains(x.symbol.fullName)
      } 
      
      def isIgnoredMember(x:Symbol):Boolean = isCosmicMember(x) || x.isConstructor || x.hasFlag(ACCESSOR)

      def buildMembersFromTrait(t:Tree) = t.tpe.members.filterNot(isIgnoredMember) map {
        mem => "%s.%s = %s.prototype.%s;\n".format(objectName, mem.nameString, mem.owner.fullName, mem.nameString)
      }

      superClasses match {
        case baseClass :: traits => traits foreach { t => lb += buildMembersFromTrait(t).mkString }
        case _ => 
      }

      lb += moduleDef.impl.body.filterNot(isDefaultThing).map(buildPackageLevelItemMember).mkString

      lb.mkString
    }

    def buildClass(t:ClassDef):String = {

      val lb = new ListBuffer[String]

      val className = t.symbol.fullName

      val superClassName = t.impl.parents filterNot { isCosmicType } headOption

      def isIgnoredMember(x:Symbol):Boolean = isCosmicMember(x) || x.isConstructor || x.hasFlag(ACCESSOR)

      if(t.symbol.isTrait) {

        lb += "\n/** @constructor*/\n"
        lb += "%s = function() {};\n".format(className)

      } else {

        val ctorDef = t.impl.body.filter {
          x => x.isInstanceOf[DefDef] && x.symbol.isPrimaryConstructor
        }.head.asInstanceOf[DefDef]

        val ctorArgs = ctorDef.vparamss.flatten.map(_.symbol.nameString)

        lb += "/** @constructor*/\n"
        lb += "%s = function(%s) {\n".format(className, ctorArgs.mkString(","))

        lb += "var self = this;\n"

        // handle defaults in a javascript way
        ctorDef.vparamss.flatten filter { _.symbol.hasDefault } foreach { 
          x => lb += "if (typeof(%1$s) === 'undefined') { %1$s = %2$s; };\n".format(x.nameString, buildTree(x.asInstanceOf[ValDef].rhs))   
        }

        debug("1a ", t)

        // superclass construction and field initialization
        t.impl.foreach {
          case x @ Apply(Select(Super(qual, mix), name), args) if(name.toString == "<init>") => superClassName match {
            case Some(y) => 
              val filteredArgs = args.filter(!_.toString.contains("$default$")).map(_.toString)
              lb += "%s.call(%s);\n".format(y.symbol.fullName, (List("self") ++ filteredArgs).mkString(","))
              ctorArgs.diff(filteredArgs).foreach {
                y => lb += "self.%1$s = %1$s;\n".format(y)
              }
            case None => 
              ctorArgs.foreach {
                y => lb += "self.%1$s = %1$s;\n".format(y)
              }
          }
          case x => 
        }

        val tms = t.impl.parents.filter(_.symbol.isTrait).filterNot(x => isIgnoredMember(x.symbol)) map { 
          x => x.tpe.members filterNot { isIgnoredMember } filter { x => !x.isMethod } map { m => (m.owner.fullName, buildName(m)) }
        }

        tms.flatten foreach {
          x => lb += "self.%s = %s.prototype.%s;\n".format(x._2, x._1, x._2)
        }

        t.impl.body filterNot { isAnIgnoredMember } foreach {
          case x @ Apply(fun, args) => lb += buildTree(x)+";\n"
          case x @ ClassDef(_, _, _, _) => lb += buildClass(x)
          case x @ ValDef(mods, name, tpt, rhs) if !x.symbol.isParamAccessor => lb += "self.%s = %s;\n".format(name.toString.trim, buildTree(rhs))
          case x => 
        }

        lb += "};\n"
      }

      superClassName.foreach {
        x => lb += "goog.inherits(%s, %s);\n".format(className, if(x.symbol.isTrait) "ScalosureObject" else x.symbol.fullName)
      }

      val traits = t.impl.parents filterNot { isCosmicType } filter { _.symbol.isTrait }

      def isValDef(x:Tree):Boolean = x match {
        case ValDef(_, _, _, _) => true
        case _ => false
      }

      val traitMembers = traits map { 
        x => x.tpe.members filterNot { isIgnoredMember } filter { _.isMethod } map { m => (m.owner.fullName, buildName(m)) }
      }

      traitMembers.flatten foreach {
        x => lb += "%s.prototype.%s = %s.prototype.%s;\n".format(className, x._2, x._1, x._2)
      }

      val caseMemberNames = List("productPrefix", "productArity", "productElement", "equals", "toString", "canEqual", "hashCode", "copy")

      def isCaseMember(x:Tree):Boolean = caseMemberNames.exists(x.symbol.fullName.endsWith(_))

      def isSynthetic(x:Tree):Boolean = x.symbol.isSynthetic

      def isValidMember(x:Tree):Boolean = x.isInstanceOf[ValDef] || (x.hasSymbol && x.symbol.hasFlag(ACCESSOR))

      if(t.symbol.hasFlag(CASE)) {
        lb ++= t.impl.body.filterNot(isCaseMember).map(buildPackageLevelItemMember)
      } else if(t.symbol.isTrait) {
        lb ++= t.impl.body.map(buildPackageLevelItemMember)
      } else {
        lb ++= t.impl.body.filterNot(isValidMember).map(buildPackageLevelItemMember)
      }

      return lb.mkString
    }

    def buildTree(t:Tree):String = t match {

        case x @ Literal(Constant(value)) => value match {
            case v:String => "'"+v+"'"
            case v:Unit => ""
            case null => "null"
            case v => v.toString
        }

        case x @ Return(expr) => "return "+buildTree(expr)

        case x @ Apply(TypeApply(y @ Select(Select(_, n), _), _), args) if(n.toString == "Array") =>
          args.map(buildObjectLiteral).mkString("[",",","]")

        case x @ Apply(TypeApply(y @ Select(Select(_, n), _), _), args) if(n.toString.matches("Tuple[0-9]+")) => 
          args.zipWithIndex map { 
            a => "'_%s':%s".format((a._2+1), a._1.toString.replace("\"", "'")) 
          } mkString("{",",","}")

        case x @ Apply(Select(q, n), args) if q.toString.matches("scalosure.JsObject") => args.headOption match {
          case Some(Typed(expr, tpt)) if tpt.toString == "_*" => expr.toString
          case _ => args map { buildObjectLiteral } mkString("{",",","}")
        }

        case x @ Apply(Select(q, n), args) if q.toString.matches("scalosure.JsArray") => args map {
          buildObjectLiteral
        } mkString("[",",","]")

        case x @ Apply(Select(qualifier, BinaryOperator(op)), args) => "(%s %s %s)".format(
          buildTree(qualifier), op, args.map(buildTree).mkString)

        case x @ Apply(Select(qualifier, name), args) if name.toString.endsWith("s2js.Html") => "html"

        case x:ApplyToImplicitArgs => x.fun match {
          case y => buildTree(y)
        }

        case x @ Apply(fun @ Select(q, n), args) if fun.toString == "scalosure.script.literal" =>
          args.mkString.replace("\"", "")

        case x @ Apply(Select(qualifier, name), args) if qualifier.toString == "s2js.Html" =>
            "%s".format(buildXmlLiteral(args.head).mkString)

        case x @ Apply(Select(qualifier, name), args) if name.toString.endsWith("_$eq") =>
            "%s.%s = %s".format(buildTree(qualifier), 
                name.toString.stripSuffix("_$eq"), args.map(buildTree).mkString)

        case x @ Apply(Select(y @ Super(_, _), name), args) =>
            "%s.superClass_.%s.call(%s)".format(y.symbol.fullName, name.toString, (List("self") ++ args.map(buildTree)).mkString(","))

        case x @ Apply(TypeApply(f, _), args) if(f.symbol.owner.nameString == "ArrowAssoc") => 
          "{%s}".format(buildObjectLiteral(x))

        case x @ Apply(TypeApply(fun @ Select(q, n), _), args) if fun.hasSymbol && fun.symbol.owner.nameString == "JsObject" => {
          args.headOption match {
            case Some(Typed(expr, tpt)) if tpt.toString == "_*" => expr.toString
            case _ => args map { buildObjectLiteral } mkString("{",",","}")
          }
        }

        case x @ Apply(fun, args) =>

          val argumentList = x.symbol.paramss

          def buildArgs(t:Tree):List[Tree] = t match {
            case Apply(f, xs) => buildArgs(f) ++ xs
            case _ => Nil
          }

          val passedArgs = (buildArgs(fun) ++ args) filterNot { _.toString.contains("$default$") }

          def buildAnArg(t:Tree):String = t match {
            case x @ Function(_, _) => buildTree(x)
            case x @ Block(stats, y @ Function(vparams, Apply(f, as))) =>
              "function(%s) {return %s(%s)}".format(as.mkString("_", ",", "_"), buildTree(f), as.mkString("_", ",", "_"))
            case x => buildTree(x)
          }

          val processedArgs = passedArgs.zip(x.symbol.paramss.flatten) map { 
            //case (passed, defined) if defined.tpe.typeSymbol.nameString.matches("""(Function0|\<byname\>)""") => debug("1b", passed); "function() {%s}".format(buildTree(passed))
            case (passed, defined) => buildAnArg(passed)
          }

          def ownerName(t:Tree) = if(fun.hasSymbol) Some(fun.symbol.owner.nameString) else  None

          val tmp = ownerName(fun) match {
            case Some("JsArray") if fun.symbol.nameString == "apply" => "%s[%s]"
            case _ => "%s(%s)"
          }

          def buildApply(f:Tree) = tmp.format(buildTree(f), processedArgs.mkString(","))

          def isVarArgs(t:Tree):Boolean = t.tpe.params.headOption match {
            case Some(firstParam) => firstParam.tpe.toString.matches("""\(String, [^)].*\)\*""")
            case None => false
          }

          def isArrayArg(t:Tree):Boolean = t.tpe.params.headOption match {
            case Some(firstParam) => firstParam.tpe.toString.matches("""[a-zA-Z0-9]+\*""")
            case None => false
          }

          fun match {
            case TypeApply(f @ Select(_, _), _) if isVarArgs(f) => {
              "%s(%s)".format(buildTree(f), args map { buildObjectLiteral } mkString("{",",","}"))
            }
            case TypeApply(f @ Select(_, _), _) if isArrayArg(f) => {
              "%s(%s)".format(buildTree(f), args map { buildObjectLiteral } mkString("[",",","]"))
            }
            case Apply(f @ Select(_, _), _) if isVarArgs(f)  => {
              "%s(%s)".format(buildTree(f), args map { buildObjectLiteral } mkString("{",",","}"))
            }
            case Apply(f @ Select(_, _), _) if isArrayArg(f)  => {
              "%s(%s)".format(buildTree(f), args map { buildObjectLiteral } mkString("[",",","]"))
            }
            case Apply(f, xs) => buildApply(f)
            case f @ Select(_, _) if isVarArgs(f) => {
              "%s(%s)".format(buildTree(f), args map { buildObjectLiteral } mkString("{",",","}"))
            }
            case f @ Select(_, _) if isArrayArg(f) => {
              "%s(%s)".format(buildTree(f), args map { buildObjectLiteral } mkString("[",",","]"))
            }
            case f @ Select(q, n) if f.symbol.owner.fullName == "scala.Array" => {
              tmp.format(buildTree(q), processedArgs.mkString(","))
            }
            case f @ Select(q, n) if f.symbol.owner.fullName == "scalosure.JsArray" && n.toString == "apply" => {
              tmp.format(buildTree(q), processedArgs.mkString(","))
            }
            case TypeApply(f @ Select(q, n), _) if f.symbol.fullName == "scalosure.JsArray.empty" => "[]"
            case y => buildApply(fun)
          }

        case x @ TypeApply(Select(q, n), args) if(n.toString == "asInstanceOf") => buildTree(q)

        case x @ TypeApply(fun, args) => buildTree(fun)

        case x @ ValDef(mods, name, tpt, rhs) if(x.symbol.isLocal) => "var %s = %s".format(
          x.symbol.nameString, rhs match {
            case y @ Match(_, _) => buildSwitch(y)
            case y @ Select(q, n) if n.toString == "unary_$bang" => "!"+buildTree(q)
            case y => buildTree(y)
          })

        case x @ Ident(name) => if(x.symbol.isLocal) x.symbol.nameString else x.symbol.fullName

        case x @ If(cond, thenp, elsep) => buildIf(x, (x.tpe.typeSymbol.nameString != "Unit"))

        case x @ Function(vparams, body) =>

          val args = vparams.map(_.symbol.nameString).mkString(",")

          // does the body have a single expression or a block of expressions
          val impl = body match {
            case y @ Block(_, _) => buildBlock(y)
            case y => buildExpression(y, body.tpe.toString != "Unit")
          }

          "function(%s) {\n%s}".format(args, impl)

        case EmptyTree => "null"

        case x @ Select(qualifier, name) if(name.toString == "package") => buildTree(qualifier)

        case x @ Select(qualifier, name) => qualifier match {
            case y @ New(tt) if tt.symbol.fullName == "scalosure.JsArray" => "new Array"
            case y @ New(tt) => "new " + (if(tt.toString.startsWith("browser")) tt.symbol.nameString else scala2scalosure(tt.symbol))
            case y @ Ident(_) if(name.toString == "apply" && (x.symbol.owner.isSynthetic || x.symbol.owner.nameString == "JsObject")) => "%s.appli".format(
              if(y.symbol.isLocal) y.symbol.nameString else y.symbol.fullName)
            case y @ Ident(_) if(y.name.toString == "browser") => name.toString
            case y @ Ident(_) if name.toString == "apply" && y.symbol.isModule => if(y.symbol.isLocal) y.symbol.nameString+"."+translateName(name) else y.symbol.fullName+"."+translateName(name)
            case y @ Ident(_) if name.toString == "apply" => if(y.symbol.isLocal) y.symbol.nameString else y.symbol.fullName
            case y @ This(_) if(x.symbol.owner.isPackageObjectClass) => y.symbol.owner.fullName+"."+name
            case y @ This(_) if(x.symbol.owner.isModuleClass) => translateName(y.symbol.fullName)+"."+name
            case y @ This(_) => "self."+name
            case y @ Select(q, n) if name.toString == "apply" && y.symbol.isModule => if(y.symbol.isLocal) {
              y.symbol.nameString.replace("scala", "scalosure")+"."+translateName(name) 
            } else {
              scala2scalosure(y.symbol)+"."+translateName(name)
            }
            case y @ Select(q, n) if name.toString == "apply" => if(y.symbol.isLocal) {
              buildTree(y)+"."+translateName(name) 
            } else {
              if(y.tpe.typeSymbol.nameString.matches("Function[0-9]")) buildTree(y) else buildTree(y)+"."+translateName(name)
            }
            case y @ Select(q, n) if(n.toString == "Predef" && name.toString == "println") => "console.log"
            case y if(name.toString == "$colon$plus" && y.symbol.nameString == "genericArrayOps") => "%s.push".format(
              y.asInstanceOf[ApplyImplicitView].args.head)
            case y if(name.toString == "unary_$bang") => "!"+buildTree(y)
            case y if(name.toString == "apply") => buildTree(y)
            case y if(name.toString == "any2ArrowAssoc") => buildTree(y)
            case y => buildTree(y)+"."+name
        }

        case x @ Block(stats, expr) => buildBlock(x)

        case x @ This(n) => if(x.symbol.isModuleClass)  n.toString else "self"

        case x @ Assign(lhs, rhs) => "%s = %s".format(buildTree(lhs), buildTree(rhs))

        // TODO: need to finish
        case x @ Try(block, catches, finalizer) => "try {\n%s\n} catch(err) {\n%s\n}".format(buildTree(block), "", "")

        case x @ LabelDef(name, params, rhs) if(name.toString.startsWith("while")) => 

            val If(cond, thenp, _) = rhs

            val transformedThen = thenp match {
                case y @ Block(stats, expr) => stats.map(buildTree).mkString
                case y => buildTree(y)
            }

            "while(%s) {%s}".format(buildTree(cond), transformedThen)

        case x => x match {
          case y @ Match(_, _) => buildSwitch(y)
          case x @ New(tpe:TypeTree) => "new %s".format(tpe.symbol.fullName)
          case y @ TypeApply(fun, args) => ""
          case y @ Typed(expr, tpt) if tpt.toString == "_*" => expr.toString
          case y:TypeTree => "typetree"
          case y => println(y.getClass); "#NOT IMPLEMENTED#"
        }
    }
    
    def buildExpression(t:Tree, hasReturn:Boolean = true):String = buildTree(t) match {
        case z if(t.tpe.toString == "Unit") => if(z == "") "" else if(hasReturn) "return %s;\n".format(z) else "%s;\n".format(z)
        case z => if(hasReturn) "return %s;\n".format(z) else "%s;\n".format(z)
    }

    def buildBlock(t:Block):String = {
        val stats = t.stats map { buildTree } map { _ + ";\n" }
        stats.mkString + buildExpression(t.expr, t.tpe.toString != "Unit")
    }

    def buildIf(t:If, hasReturn:Boolean):String = {

      def buildTreeReturn(t2:Tree) = if(hasReturn) "return %s".format(buildTree(t2)) else buildTree(t2)

      val transformedThen = t.thenp match {
          case y @ Block(_, _) => buildBlock(y)
          case y => buildTreeReturn(y)+";\n"
      }

      val transformedElse = t.elsep match {
          case y @ Block(_, _) => buildBlock(y)
          case y => buildTreeReturn(y)+";\n"
      }

      "%s ? function() {\n%s}() : function() {\n%s}()".format(buildTree(t.cond), transformedThen, transformedElse)
    }

    def buildSwitch(t:Tree):String = {

      val sb = new StringBuilder

      sb.append("\nvar matched;\n")
      val Match(selector, cases) = t

      def buildTheBody(body:Tree) = "return %s".format(buildTree(body)) 

      cases.zipWithIndex foreach {

        case (a, b) => {

          val tmp = b match {
            case 0 => "if(%s) {\n%s\n}"
            case x => "else if(%s) {\n%s\n}"
          }

          a match {
            case CaseDef(pat, guard, body) => pat match {
              case x @ Literal(Constant(_)) => 
                sb.append(tmp.format(buildTree(selector) + " == " + buildTree(x), buildTheBody(body)))
              case x @ Ident(n) if(n.toString == "_") => sb.append(" else {%s}".format(buildTheBody(body)))
              case x @ Bind(n, b) if b.tpe.toString == "String" => sb.append(
                tmp.format("typeof %s == 'string'".format(buildTree(selector)), buildTheBody(body)))
              case x @ Bind(n, b) if b.tpe.toString.matches("(Int|Long|Double|java.lang.Number)") => sb.append(
                tmp.format("typeof %s == 'number'".format(buildTree(selector)), buildTheBody(body)))
              case x @ Bind(n, b) if b.tpe.toString == "Boolean" => sb.append(
                tmp.format("typeof %s == 'boolean'".format(buildTree(selector)), buildTheBody(body)))
              case x @ Bind(n, b) if b.tpe.toString == "Any" => sb.append(" else {%s}".format(buildTheBody(body)))
              case x @ Bind(n, Typed(expr, tpt)) => sb.append(
                tmp.format("typeof %s == 'string'".format(buildTree(selector)), "return function(%s) {%s}(%s)".format(n.toString, buildTheBody(body), buildTree(selector))))
              case x @ Bind(n, b) =>
                val bindList = buildMatchBindList(x)
                val bindListArgs = bindList.mkString(",")
                val bindListValues = bindList.zipWithIndex.map { y => "matched[%s]".format(y._2) } mkString(",")
                sb.append(
                  tmp.format(processMatch(selector, x, guard, body, true), "return function(%s) {%s}(%s)".format(bindListArgs, buildTree(body), bindListValues)))
              case x @ Apply(f, as) => 
                val bindList = buildMatchBindList(x)
                val bindListArgs = bindList.mkString(",")
                val bindListValues = bindList.zipWithIndex.map { y => "matched[%s]".format(y._2) } mkString(",")
                sb.append(
                  tmp.format(processMatch(selector, x, guard, body, false), "return function(%s) {%s}(%s)".format(bindListArgs, buildTree(body), bindListValues)))
              case x => sb.append("f"+x.getClass)
            }
            case _ => sb.append("not here 2")
          }
        }

        case _ => sb.append("not here 3")
      }

      "function() {%s}()".format(sb.toString)
    }

    def classType(f:Tree) = JsFunction(f.tpe.finalResultType.toString)

    def toJsType(t:String) = t match {
      case "String" => JsFunction("String")
      case "Int" | "Long" | "Double" => JsFunction("Number")
      case other => JsFunction(other)
    }

    def toJsValue(t:Any) = t match {
      case x:String => JsString(x)
      case x:Number => JsNumber(x)
      case x:Boolean => JsBoolean(x)
      case x => JsObject(Nil)
    }

    def processMatch(selector:Tree, pat:Tree, guard:Tree, body:Tree, isBound:Boolean):String = {

      val matchit = "matched = scalosure.matchit(%s,%s)".format(buildTree(selector), utils.scala2js(processMatchPat(pat, isBound)))

      val bindList = buildMatchBindList(pat)
      val bindListArgs = bindList.mkString(",")
      val bindListValues = bindList.zipWithIndex.map { x => "matched[%s]".format(x._2) } mkString(",")

      if(guard.toString != "<empty>") {
        "(%s) && function(%s) {return %s}(%s)".format(matchit, bindListArgs, buildTree(guard), bindListValues)
      } else {
        matchit
      }
      
    }

    def buildMatchBindList(t:Tree, binds:List[String] = Nil):List[String] = t match {
      case x @ Apply(f, xs) => xs.map { y => buildMatchBindList(y, binds) } flatten
      case x @ Bind(n, b) => binds ++ List(n.toString) ++ buildMatchBindList(b)
      case x => binds
    }

    def buildMetaType(tpe:JsType, bind:Boolean, children:List[JsType], cond:JsType=null) = JsObject(
      "type" -> tpe :: "bind" -> JsBoolean(bind) :: "children" -> JsArray(children) :: "cond" -> (if(cond == null) JsFunction("null") else cond) :: Nil)

    def processMatchPat(t:Tree, isBound:Boolean):JsType = t match { 
      case x @ Apply(f, xs) => buildMetaType(classType(x), isBound, xs map { x => processMatchPat(x, false) })
      case x @ Bind(n, b) => processMatchPat(b, true)
      case x @ Ident(n) if n.toString == "_" => buildMetaType(toJsType(x.tpe.toString), isBound, Nil)
      case x @ Literal(Constant(v)) => buildMetaType(toJsType(x.tpe.typeSymbol.nameString), isBound, Nil, toJsValue(v))
      case x => println(x.getClass); JsObject(Nil)
    }

    def findRequiresFrom(tree:Tree):Set[String] = {
        
        val s = new mu.LinkedHashSet[String] 

        var currentFile:scala.tools.nsc.io.AbstractFile = null

        val thingsToIgnore = List("scalosure.script", "s2js.JsObject", "scalosure.JsObject", "scalosure.JsArray", "s2js.Html", "ClassManifest", "scala.runtime.AbstractFunction1",
          "scala.runtime.AbstractFunction2", "scala.runtime.AbstractFunction3", "scala.Tuple2", "scala.Tuple3", "scala.Product", "scala.ScalaObject", 
          "java.lang", "scala.xml", "scala.package", "$default$", "browser", "scala.runtime", "scala.Any", "scala.Equals", "scala.Boolean", "scala.Function0",
          "scala.Function1", "scala.Predef", "scala.Int", "scala.Array", "scala.reflect.Manifest")

        def buildName(s:Symbol):String = s.fullName.replace("scala", "scalosure")
          
        def traverse(t:Tree):Unit = t match {

            // check the body of a class
            case x @ Template(parents, self, body) => 

                currentFile = x.symbol.sourceFile

                parents.foreach {
                    y => if(!thingsToIgnore.exists(y.symbol.fullName.contains)) {
                        if(currentFile != y.symbol.sourceFile) {
                          s += buildName(y.symbol)
                        }
                    } 
                }

                body.foreach(traverse)
                
            case x @ ValDef(_, _, _, rhs) => 

                rhs match {
                    case y @ Block(stats, expr) => 
                        stats.foreach(traverse)
                        traverse(expr)
                    case y => 
                        traverse(y)
                }

            case x @ DefDef(_, _, _, vparamss, _, rhs) =>

                rhs match {
                    case y @ Block(stats, expr) => 
                        stats.foreach(traverse)
                        traverse(expr)
                    case y => traverse(y)
                }

            case x @ Apply(Select(q, _), args) if(q.toString.endsWith("Predef")) =>
                args.foreach(traverse)

            // make sure we check all function calls for needed imports
            case x @ Apply(fun, args) =>

                if(!thingsToIgnore.exists(fun.symbol.fullName.contains)) {
                    traverse(fun)
                }

                args.foreach(traverse)

            case x @ Select(q, _) if(q.toString.endsWith("package")) =>

                if(!thingsToIgnore.exists(x.symbol.fullName.contains)) {
                  s += buildName(q.symbol.owner)
                }

            case x @ Select(New(tpe), _) => tpe match {

                case y @ Select(Select(_, _), _) => 
                  s += buildName(y.symbol)
                case y @ TypeTree() => 
                  if(currentFile != y.symbol.sourceFile) {
                    s += buildName(y.symbol)
                  }
                case y => 
                  if(currentFile != y.symbol.sourceFile) {
                    s += buildName(y.symbol)
                  }
            }

            case x @ Select(Select(_, _), _) if(!x.symbol.isPackage) =>

                if(!thingsToIgnore.exists(x.symbol.fullName.contains)) {
                  s += buildName(x.symbol.owner)
                }

            case x @ Select(_, _) => if(!thingsToIgnore.exists(x.symbol.fullName.contains)) {
              if(x.symbol.sourceFile != currentFile) s += buildName(x.symbol.owner)
            }
               
            case x @ Ident(name) =>
            
            case x => x.children.foreach(traverse)
        }

        traverse(tree)

        s.toSet
    }

    def buildField(tree:ValDef, inPackageObject:Boolean=false):String = { 

        val className = trimNamespace(tree.ownerName)
        val name = tree.nameString
        val rhs = buildTree(tree.rhs)

        if(tree.symbol.owner.isModuleClass) {
          "%s.%s = %s;\n".format(className, buildName(tree.symbol), rhs)
        } else if(tree.symbol.owner.isTrait) {
          "%s.prototype.%s = %s;\n".format(className, buildName(tree.symbol), rhs)
        } else {
          ""
        }
    }

    def trimNamespace(ns:String) = if(ns.endsWith(".package")) {
        ns.stripSuffix(".package")
    } else ns

    def translateName(s:Name):String = s.toString match {
        case "apply" => "appli"
        case "scala" => "scalosure"
        case x => x
    }

    def scala2scalosure(s:Symbol):String = s.fullName.replace("scala", "scalosure")

    def buildName(s:Symbol):String = s.nameString match {
        case "+=" => "$plus$eq"
        case "apply" => "appli"
        case x => x
    }

    def buildMethod(ts:DefDef, inPackageObject:Boolean=false):String = { 

        val ns = trimNamespace(ts.symbol.owner.fullName)
        val name = ts.symbol.nameString
        val args = ts.vparamss.flatten.map(_.symbol.nameString).mkString(",")

        val l = new ListBuffer[String]

        if(ts.symbol.owner.isModuleClass) {
            l += "%s.%s = function(%s) {\n".format(ns, buildName(ts.symbol), args)
        } else {
            l += "%s.prototype.%s = function(%s) {\n".format(ns, buildName(ts.symbol), args)
        }

        // every method gets a self refrence
        l += "var self = this;\n"

        // handle defaults in a javascript way
        ts.vparamss.flatten filter { _.symbol.hasDefault } foreach { 
          x => l += "if (typeof(%1$s) === 'undefined') { %1$s = %2$s; };\n".format(x.nameString, buildTree(x.asInstanceOf[ValDef].rhs))   
        }

        val stats = ts.rhs match {
            case y @ Block(_, _) => buildBlock(y)
            case y @ Match(_, _) => "return "+buildSwitch(y)
            case y => buildTree(y) match {
                case z if(ts.tpt.symbol.nameString == "Unit") => 
                    if(z == "") "" else "%s;\n".format(z)
                case z => "return %s;\n".format(z)
            }
        }
        
        l += stats

        l += "};\n"

        if(ts.symbol.annotations exists {_.toString == "s2js.ExportSymbol"}) {
            l += "goog.exportSymbol('%1$s', %1$s);\n".format(ns+"."+name)
        }

        l.mkString
    }

    def buildXmlLiteral(t:Tree):String = t match {

        case x @ Block(_, inner @ Block(stats, a @ Apply(_,_))) => a match {

            case y @ Apply(Select(New(tpt), _), args) if(tpt.toString == "scala.xml.Elem") =>

                val tag = args(1).toString.replace("\"", "")

                val attributes = stats.filter {
                    case Assign(_, _) => true
                    case _ => false
                }.map(buildXmlLiteral).mkString("{",",","}")

                val children = if(args.length > 4) {
                    buildXmlLiteral(args(4))
                } else "[]"
                
                "goog.dom.createDom('%s',%s,%s)".format(
                    tag, attributes, children)

            case y => "nothing"

        }

        case x @ Typed(Block(stats, expr), tpt) => 
        
            stats.filter {
                case Apply(_,_) => true
                case _ => false
            }.map(buildXmlLiteral).filter(_ != "").mkString("[",",","]")

        case x @ Apply(fun, args) if(fun.symbol.fullName == "scala.xml.NodeBuffer.$amp$plus") =>
            buildXmlLiteral(args.head)

        case x @ Apply(Select(New(tpt), _), args) if(tpt.toString == "scala.xml.Text") => 
            val value = args.head.toString.replaceAll("""(\\012|[ ]{2,}|[\r\n]|")""", "")
            if(value == "") "" else "'%s'".format(value)

        case x @ Assign(_, Apply(_, List(name, Apply(_,  List(value)), _))) =>
            val stripName = name.toString.replace("\"", "")
            "'%s':%s".format(stripName, buildTree(value))
            
        case x @ Assign(_, Apply(_, List(name, value @ Select(_, _), _))) => 
            val stripName = name.toString.replace("\"", "")
            "'%s':%s".format(stripName, buildTree(value))

        case x @ Assign(_, Apply(_, List(name, value @ Ident(_), _))) => 
            val stripName = name.toString.replace("\"", "")
            "'%s':%s".format(stripName, buildTree(value))

        case x => buildTree(x)
    }

    def buildObjectLiteral(t:Tree):String = t match {

      case x @ Literal(Constant(value)) => value match {
        case v:String => "'"+v+"'"
        case x:Unit => ""
        case v => v.toString
      }

      case x @ Apply(TypeApply(y @ Select(Select(_, n), _), _), args) if(n.toString == "Map") => args.map {
        buildObjectLiteral
      } mkString("{",",","}")

      case x @ Apply(TypeApply(Select(q, n), _), args) if (n.toString == "$minus$greater") => {

        // this should be a string
        val key = q.asInstanceOf[ApplyImplicitView].args.head.toString.replace("\"", "'")

        // process nested objects
        val values = args map buildObjectLiteral

        "%s:%s".format(key, values.mkString)
      }

      case x:ApplyToImplicitArgs => x.fun match {
        case y @ Apply(TypeApply(Select(n, _), _), args) if(n.toString == "scala.Array") => args map {
          buildObjectLiteral 
        } mkString("[", ",", "]")
        case y => y.getClass.toString
      }

      case x => buildTree(x)
    }

}

object utils {

    private def quotedChar(codePoint: Int) = {
        codePoint match {
            case c if c > 0xffff =>
            val chars = Character.toChars(c)
            "\\u%04x\\u%04x".format(chars(0).toInt, chars(1).toInt)
            case c if c > 0x7e => "\\u%04x".format(c.toInt)
            case c => c.toChar
        }
    }

    private def quote(s: String) = {

        val charCount = s.codePointCount(0, s.length)

        "\"" + 0.to(charCount - 1).map { idx =>
            s.codePointAt(s.offsetByCodePoints(0, idx)) match {
                case 0x0d => "\\r"
                case 0x0a => "\\n"
                case 0x09 => "\\t"
                case 0x22 => "\\\""
                case 0x5c => "\\\\"
                case 0x2f => "\\/"     // to avoid sending "</"
                case c => quotedChar(c)
            }
        }.mkString("") + "\""
    }

    def scala2json(obj:Any):String = obj match {

        case null => "null"
        case x:Boolean => x.toString
        case x:Number => x.toString
        case x:List[_] => x.map {
            y => scala2json(y)
        }.mkString("[",",","]")
        case x:Map[_, _] => x.map {
            y => quote(y._1.toString)+":"+scala2json(y._2)
        }.mkString("{",",","}")
        case x => quote(x.toString)
    }

    def scala2js(obj:JsType):String = obj match {
      case JsObject(items) => items map { x => "'%s':%s".format(x._1, scala2js(x._2)) } mkString("{", ",", "}")
      case JsArray(items) => items map { scala2js } mkString("[", ",", "]")
      case JsString(value) => "'%s'".format(value)
      case JsBoolean(value) => value.toString
      case JsNumber(value) => value.toString
      case JsFunction(value) => value.toString
    }
}
