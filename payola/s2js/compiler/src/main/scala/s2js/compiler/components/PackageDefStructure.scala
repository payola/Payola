package s2js.compiler.components

import collection.mutable
import scala.tools.nsc.Global

/** A structure of PackageDef nested packages and classes. */
class PackageDefStructure
{
    /** Map of ClassDefs indexed by their fully qualified names. */
    val classDefMap = new mutable.HashMap[String, Global#ClassDef]

    /** Graph of dependencies among the ClassDef objects in the PackageDef. */
    val classDefDependencyGraph = new mutable.HashMap[String, mutable.HashSet[String]]

    /** The remote objects. */
    val remoteObjects = new mutable.ListBuffer[Global#ClassDef]
}
