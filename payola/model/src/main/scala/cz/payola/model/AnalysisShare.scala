package cz.payola.model

import generic.ConcreteEntity
import cz.payola.common
import cz.payola.common.model.SharePrivilege
import cz.payola.scala2json.traits._
import cz.payola.scala2json.annotations.JSONUnnamedClass

@JSONUnnamedClass
class AnalysisShare (val analysis: Analysis, var privilege: Int) extends common.model.AnalysisShare with ConcreteEntity with JSONSerializationCustomFields {
    require(analysis != null, "Analysis cannot be null!")
    require(privilege == SharePrivilege.IncludingData || privilege == SharePrivilege.ResultOnly, "Privilige unknown!")

    /** Creates a new AnalysisShare with SharePrivilegeResultOnly SharePrivilege.
     *
     *  @param a The analysis to be shared.
     */
    def this(a: Analysis) = this(a, SharePrivilege.ResultOnly)

    /** Return the names of the fields.
      *
      * @return Iterable collection for the field names.
      */
    def fieldNamesForJSONSerialization(ctx: Any): scala.collection.Iterable[String] = {
        return List("analysis", "privilege")
    }

    /** Return the value for the field named @key.
      *
      * @param key Value for the field called @key.
      *
      * @return The value.
      */
    def fieldValueForKey(ctx: Any, key: String): Any = {
        key match {
            case "analysis" => analysis.id
            case "privilege" => privilege
            case _ => null
        }
    }

}
