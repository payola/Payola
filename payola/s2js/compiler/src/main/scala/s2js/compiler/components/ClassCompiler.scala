package s2js.compiler.components

import collection.mutable
import tools.nsc.Global

/** A compiler of a ClassDef that corresponds to a class or trait. */
class ClassCompiler(packageDefCompiler: PackageDefCompiler, classDef: Global#ClassDef)
    extends ClassDefCompiler(packageDefCompiler, classDef)
{
    override val memberContainerName = fullJsName + ".prototype"

    protected def compileConstructor(parentConstructorCall: Option[Global#Apply]) {
        buffer += fullJsName + " = function("
        compileParameterDeclaration(constructorParameters)
        buffer += ") {\n"
        buffer += "var self = this;\n"

        val initializedValDefs = new mutable.HashSet[String]
        if (constructorDefDef.isDefined) {
            compileParameterInitialization(constructorParameters)

            // Initialize fields specified as the implicit constructor parameters.
            constructorParameters.get.foreach {parameter =>
                initializedValDefs += packageDefCompiler.getSymbolLocalJsName(parameter.symbol)
                buffer += "self.%1$s = %1$s;\n".format(packageDefCompiler.getSymbolJsName(parameter.symbol))
            }
        }

        // Call the parent class constructor.
        parentClass.foreach { c =>
            buffer += packageDefCompiler.getSymbolFullJsName(c.symbol) + ".apply(self, "
            compileParameterValues(parentConstructorCall.map(_.args).getOrElse(Nil), false, true)
            buffer += ");\n"
        }

        // Mix in the fields of the inherited traits.
        inheritedTraits.reverse.foreach { traitAst =>
            buffer += "s2js.runtime.client.core.get().mixInFields(self, new %s());\n".format(
                packageDefCompiler.getSymbolJsName(traitAst.symbol)
            )
        }

        // Initialize fields that aren't implicit constructor parameters.
        valDefs.filter(v => !initializedValDefs.contains(packageDefCompiler.getSymbolLocalJsName(v.symbol))).foreach(
            compileMember(_, "self"))

        // Compile the constructor body.
        classDef.impl.body.filter(!_.isInstanceOf[Global#ValOrDefDef]).foreach {ast =>
            compileAst(ast)
            buffer += ";\n"
        }
        buffer += "};\n"

        if (parentClass.isDefined) {
            buffer += "s2js.runtime.client.core.get().inherit(%s, %s);\n".format(
                fullJsName,
                packageDefCompiler.getSymbolJsName(parentClass.get.symbol)
            )
        }

        // Copy all the functions of the inherited trait prototypes to the current class prototype.
        inheritedTraits.reverse.foreach { traitAst =>
            buffer += "s2js.runtime.client.core.get().mixInFunctions(%s, %s.prototype);\n".format(
                memberContainerName,
                packageDefCompiler.getSymbolJsName(traitAst.symbol)
            )
        }
    }

    override protected def compileMembers() {
        // The valDefs are compiled within the constructor.
        defDefs.foreach(compileMember(_))
    }
}
