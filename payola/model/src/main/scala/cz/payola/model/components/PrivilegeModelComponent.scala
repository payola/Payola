package cz.payola.model.components

import cz.payola.model.EntityModelComponent
import cz.payola.data.DataContextComponent
import cz.payola.domain.entities.PrivilegableEntity

trait PrivilegeModelComponent extends EntityModelComponent
{
    self: DataContextComponent =>

    lazy val privilegeModel = new
    {
        def getAllByObjectIdAndGranteeType(objectId: String, granteeType: Class[_ <: PrivilegableEntity]) = {
            privilegeRepository.getAllByObjectIdAndGranteeType(objectId, granteeType)
        }

    }


}
