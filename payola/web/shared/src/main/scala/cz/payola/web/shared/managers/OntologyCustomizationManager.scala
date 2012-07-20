package cz.payola.web.shared.managers

import s2js.compiler._
import cz.payola.domain.entities.User
import cz.payola.domain.entities.settings.OntologyCustomization
import cz.payola.web.shared.Payola

@remote
@secured object OntologyCustomizationManager
    extends ShareableEntityManager[OntologyCustomization, cz.payola.common.entities.settings.OntologyCustomization](
        Payola.model.ontologyCustomizationModel)
{
    @async def create(name: String, ontologyURL: String, owner: User = null)
        (successCallback: cz.payola.common.entities.settings.OntologyCustomization => Unit)
        (failCallback: Throwable => Unit) {

        successCallback(Payola.model.ontologyCustomizationModel.create(name, ontologyURL, owner))
    }

    @secured def getCustomizationByID(id: String, user: User = null): cz.payola.common.entities.settings.OntologyCustomization = {
        val opt = Payola.model.ontologyCustomizationModel.getAccessibleToUserById(Some(user), id)
        opt.getOrElse {
            throw new Exception("Couldn't find customization.")
        }
    }

    @secured def getUsersCustomizations(user: User = null): Seq[OntologyCustomization] = {
        Payola.model.ontologyCustomizationModel.getAccessibleToUser(Some(user))
    }
}
