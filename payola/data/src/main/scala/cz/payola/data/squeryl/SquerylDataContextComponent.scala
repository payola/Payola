package cz.payola.data.squeryl

import cz.payola.domain.RdfStorageComponent
import cz.payola.data.DataContextComponent
import cz.payola.data.squeryl.repositories._

trait SquerylDataContextComponent
    extends DataContextComponent
    with SchemaComponent
    with UserRepositoryComponent
    with GroupRepositoryComponent
    with PrivilegeRepositoryComponent
    with AnalysisRepositoryComponent
    with DataSourceRepositoryComponent
    with PluginRepositoryComponent
    with PluginInstanceRepositoryComponent
    with PluginInstanceBindingRepositoryComponent
    with OntologyRepositoryComponent
{
    self: RdfStorageComponent =>

    implicit val context = this
}
