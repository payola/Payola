package s2js.compiler.components

import tools.nsc.Global
import collection.mutable

/** A compiler of a ClassDef that corresponds to a singleton object. */
class ObjectCompiler(packageDefCompiler: PackageDefCompiler, classDef: Global#ClassDef)
    extends ClassDefCompiler(packageDefCompiler, classDef)
{
    override def compile(buffer: mutable.ListBuffer[String]) {
        // A synthetic object is compiled only if it has some members that should be compiled.
        if (!classDef.symbol.isSynthetic || valOrDefDefs.exists(_.hasSymbolWhich(!symbolIsIgnoredMember(_)))) {
            super.compile(buffer)
        }
    }

    protected def compileConstructor(parentConstructorCall: Option[Global#Apply]) {
        if (parentClass.isDefined && parentConstructorCall.isDefined) {
            // Because the object may be a package object or a companion object, the members that already exist
            // there need to be preserved.
            buffer += "s2js.runtime.client.core.mixIn(%s, new %s".format(
                fullJsName,
                packageDefCompiler.getSymbolJsName(parentClass.get.symbol)
            )
            compileParameterValues(parentConstructorCall.get.args)
            buffer += ");\n"
        }

        // Mix in the inherited traits.
        mixInInheritedTraits(fullJsName)
    }
}
