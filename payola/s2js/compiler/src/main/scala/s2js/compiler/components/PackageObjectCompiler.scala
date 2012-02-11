package s2js.compiler.components

import scala.tools.nsc.Global

/** A compiler of a ClassDef that corresponds to a package object. */
class PackageObjectCompiler(packageDefCompiler: PackageDefCompiler, classDef: Global#ClassDef)
    extends ObjectCompiler(packageDefCompiler, classDef)
{
    override protected lazy val fullJsName = packageDefCompiler.getSymbolJsName(classDef.symbol.owner)
}
