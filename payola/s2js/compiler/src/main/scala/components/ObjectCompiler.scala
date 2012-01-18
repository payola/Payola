package s2js.compiler.components

import tools.nsc.Global
import reflect.{Super, Select, Apply}
import collection.mutable

class ObjectCompiler(packageDefCompiler: PackageDefCompiler, classDef: Global#ClassDef)
    extends ClassDefCompiler(packageDefCompiler, classDef)
{
    override def compile(buffer: mutable.ListBuffer[String]) {
        // A synthetic object is compiled only if it has some members that should be compiled.
        if (!classDef.symbol.isSynthetic || valOrDefDefs.exists(_.hasSymbolWhich(!isIgnoredMember(_)))) {
            super.compile(buffer)
        }
    }

    protected def compileConstructor(parentConstructorCall: Option[Global#Apply]) {
        if (parentClass.isDefined && parentConstructorCall.isDefined) {
            // Because the object may be a package object or a companion object, the members that already exist
            // there need to be preserved.
            buffer += "goog.object.extend(%s, new %s".format(
                fullJsName,
                packageDefCompiler.getSymbolJsName(parentClass.get.symbol)
            )
            compileParameterValues(parentConstructorCall.get.args)
            buffer += ");\n"
        }

        // Inherit the traits.
        compileInheritedTraits(fullJsName);
    }
}
