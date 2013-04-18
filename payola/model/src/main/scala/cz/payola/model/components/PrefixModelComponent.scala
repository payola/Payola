package cz.payola.model.components

import cz.payola.model.EntityModelComponent
import cz.payola.data.DataContextComponent
import cz.payola.domain.RdfStorageComponent

/**
 * @author Ondřej Heřmánek (ondra.hermanek@gmail.com)
 */
trait PrefixModelComponent extends EntityModelComponent
{
    self: DataContextComponent =>

    lazy val prefixModel = new EntityModel(prefixRepository)
    {
    }
}
