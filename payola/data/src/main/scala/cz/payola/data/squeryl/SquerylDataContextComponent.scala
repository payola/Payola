package cz.payola.data.squeryl

import cz.payola.data
import cz.payola.data.squeryl.repositories._

trait SquerylDataContextComponent
    extends data.DataContextComponent
    with SchemaComponent
    with UserRepositoryComponent
    with GroupRepositoryComponent
    with PrivilegeRepositoryComponent
    with AnalysisRepositoryComponent
    with DataSourceRepositoryComponent
    with PluginRepositoryComponent
    with PluginInstanceRepositoryComponent
    with PluginInstanceBindingRepositoryComponent
{
    implicit val context = this
}
