package cz.payola.model.components

import cz.payola.data._
import cz.payola.domain.RdfStorageComponent
import cz.payola.domain.entities.privileges._
import cz.payola.domain.entities.settings.OntologyCustomization
import cz.payola.model.EntityModelComponent
import cz.payola.domain.entities.User
import cz.payola.common.entities.settings._

trait OntologyCustomizationModelComponent extends EntityModelComponent
{
    self: DataContextComponent with RdfStorageComponent =>

    lazy val ontologyCustomizationModel = new ShareableEntityModel[OntologyCustomization](
        ontologyCustomizationRepository,
        classOf[UseOntologyCustomizationPrivilege],
        (user: User) => user.ownedOntologyCustomizations)
    {
        def create(name: String, ontologyURL: String, owner: User): OntologyCustomization = {
            val customization = OntologyCustomization.empty(ontologyURL, name, Some(owner))
            persist(customization)
            customization
        }

        def persistClassCustomization(customization: ClassCustomization) {
            ontologyCustomizationRepository.persistClassCustomization(customization)
        }

        def persistPropertyCustomization(customization: PropertyCustomization) {
            ontologyCustomizationRepository.persistPropertyCustomization(customization)
        }

    }
}
