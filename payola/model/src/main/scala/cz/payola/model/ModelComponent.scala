package cz.payola.model

import cz.payola.data.DataContextComponent
import cz.payola.domain._
import cz.payola.model.components._
import cz.payola.common.entities.ShareableEntity
import cz.payola.domain.entities._
import cz.payola.domain.entities.plugins.DataSource
import cz.payola.domain.entities.settings.OntologyCustomization

trait ModelComponent
    extends UserModelComponent
    with GroupModelComponent
    with AnalysisModelComponent
    with PluginModelComponent
    with DataSourceModelComponent
    with OntologyCustomizationModelComponent
    with PayolaStorageModelComponent
{
    self: DataContextComponent with RdfStorageComponent with PluginCompilerComponent =>

    def persistEntity(e: Entity){
        repositoryRegistry(e.getClass()).persist(e)
    }
}
