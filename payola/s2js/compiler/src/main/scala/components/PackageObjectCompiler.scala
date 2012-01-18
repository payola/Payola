package s2js.compiler.components

import scala.tools.nsc.Global

class PackageObjectCompiler(packageDefCompiler: PackageDefCompiler, classDef: Global#ClassDef)
    extends ObjectCompiler(packageDefCompiler, classDef)
{
    override protected lazy val fullJsName = packageDefCompiler.getSymbolJsName(classDef.symbol.owner)
}
