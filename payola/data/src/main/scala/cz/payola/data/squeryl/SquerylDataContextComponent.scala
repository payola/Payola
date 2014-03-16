package cz.payola.data.squeryl

import cz.payola.data.DataContextComponent
import cz.payola.data.squeryl.repositories._
import cz.payola.domain._

/**
 * Contains context that is passed as an implicit parameters to entities providing them access to repositories.
 *
 * @see Dependency Injection pattern, Cake pattern
 */
trait SquerylDataContextComponent
    extends DataContextComponent
    with SchemaComponent
    with UserRepositoryComponent
    with GroupRepositoryComponent
    with PrivilegeRepositoryComponent
    with PluginInstanceRepositoryComponent
    with AnalysisRepositoryComponent
    with EmbeddingDescriptionRepositoryComponent
    with DataSourceRepositoryComponent
    with PluginRepositoryComponent
    with CustomizationRepositoryComponent
    with PrefixRepositoryComponent
    with AnalysisResultRepositoryComponent
{
    self: RdfStorageComponent with PluginCompilerComponent =>
    /**
     * Implicit context
     */
    implicit val context = this
}
