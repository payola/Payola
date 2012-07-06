package cz.payola.data

import org.squeryl.adapters._
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.dsl.CompositeKey2
import org.squeryl._
import cz.payola.data.entities.analyses.PluginInstanceBinding
import cz.payola.data.entities.plugins.parameters._
import cz.payola.data.entities._
import cz.payola.data.entities.plugins._
import cz.payola.data.dao._
import scala.Some
import org.squeryl.dsl.CompositeKey2

trait SquerylDataContextComponent
    extends DataContextComponent
    with SquerylSchemaComponent
    with AnalysisDAOComponent
    with DataSourceDAOComponent
    with GroupDAOComponent
    with PluginDAOComponent
    with PluginInstanceDAOComponent
    with PluginInstanceBindingDAOComponent
    with UserDAOComponent
{
    implicit val context = this
}
