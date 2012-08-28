package s2js.compiler.components

import tools.nsc.Global
import collection.mutable

/** A compiler of a ClassDef that corresponds to a singleton object. */
class ObjectCompiler(packageDefCompiler: PackageDefCompiler, classDef: Global#ClassDef)
    extends ClassDefCompiler(packageDefCompiler, classDef)
{
    override protected val memberContainerName = "obj"

    override def compile(buffer: mutable.ListBuffer[String]) {
        // A synthetic object is compiled only if it has some members that should be compiled.
        if (!classDef.symbol.isSynthetic || valOrDefDefs.exists(_.hasSymbolWhich(!symbolIsIgnoredMember(_)))) {
            // Because the object may be a package object or a companion object, the members that already exist
            // there need to be preserved. The object is wrapped into a lazy value so the first access to the object
            // actually instantiates it.
            buffer += "s2js.runtime.client.core.get().mixIn(%s, ".format(fullJsName)
            buffer += "new s2js.runtime.client.core.Lazy(function() {\n"
            buffer += "var %s = {};\n".format(memberContainerName)

            super.compile(buffer)

            buffer += "return %s;\n".format(memberContainerName)
            buffer += "})" // Lazy
            buffer += ", true);\n" // mixIn
        }
    }

    protected def compileConstructor(parentConstructorCall: Option[Global#Apply]) {
        if (parentClass.isDefined && parentConstructorCall.isDefined) {
            buffer += "%s = new %s".format(
                memberContainerName,
                packageDefCompiler.getSymbolJsName(parentClass.get.symbol)
            )
            compileParameterValues(parentConstructorCall.get.args)
            buffer += ";\n"
        }

        // Mix in the inherited traits.
        inheritedTraits.reverse.foreach { traitAst =>
            buffer += "s2js.runtime.client.core.get().mixIn(%s, new %s());\n".format(
                memberContainerName,
                packageDefCompiler.getSymbolJsName(traitAst.symbol)
            )
        }
    }
}
