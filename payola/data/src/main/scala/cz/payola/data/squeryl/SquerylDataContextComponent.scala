package cz.payola.data.squeryl

import cz.payola.data.DataContextComponent
import cz.payola.data.squeryl.repositories._
import cz.payola.domain._

trait SquerylDataContextComponent
    extends DataContextComponent
    with SchemaComponent
    with UserRepositoryComponent
    with GroupRepositoryComponent
    with PrivilegeRepositoryComponent
    with PluginInstanceRepositoryComponent
    with AnalysisRepositoryComponent
    with DataSourceRepositoryComponent
    with PluginRepositoryComponent
    with OntologyRepositoryComponent
{
    self: RdfStorageComponent with PluginCompilerComponent =>

    implicit val context = this
}
