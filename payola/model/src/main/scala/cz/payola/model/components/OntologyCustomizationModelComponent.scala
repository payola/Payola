package cz.payola.model.components

import cz.payola.data._
import cz.payola.domain.RdfStorageComponent
import cz.payola.domain.entities.settings.OntologyCustomization
import cz.payola.model.EntityModelComponent
import cz.payola.domain.entities.User
import cz.payola.common.entities.settings._

trait OntologyCustomizationModelComponent extends EntityModelComponent
{
    self: DataContextComponent with RdfStorageComponent with PrivilegeModelComponent =>

    lazy val ontologyCustomizationModel = new ShareableEntityModel(ontologyCustomizationRepository,
        classOf[OntologyCustomization])
    {
        def create(name: String, ontologyURLs: Option[Seq[String]], owner: User): OntologyCustomization = {

            val customization =
                if(ontologyURLs.isDefined) {
                    OntologyCustomization.empty(ontologyURLs.get, name, Some(owner))
                }
                else
                    OntologyCustomization.userDefined(name, Some(owner))

            persist(customization)
            customization
        }

        def persistUserDefined(userCustomization: OntologyCustomization) {
            persist(userCustomization)
        }

        def persistClassCustomization(customization: ClassCustomization) {
            ontologyCustomizationRepository.persistClassCustomization(customization)
        }

        def persistPropertyCustomization(customization: PropertyCustomization) {
            ontologyCustomizationRepository.persistPropertyCustomization(customization)
        }

    }
}
