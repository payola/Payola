package cz.payola.model

import cz.payola.data.DataContextComponent
import cz.payola.domain._
import cz.payola.model.components._

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
}
