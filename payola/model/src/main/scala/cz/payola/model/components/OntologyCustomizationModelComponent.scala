package cz.payola.model.components

import cz.payola.data._
import cz.payola.domain.RdfStorageComponent
import cz.payola.model.EntityModelComponent
import cz.payola.domain.entities.User
import cz.payola.domain.entities.settings._
import scala.collection.mutable

trait OntologyCustomizationModelComponent extends EntityModelComponent
{
    self: DataContextComponent with RdfStorageComponent with PrivilegeModelComponent =>

    lazy val ontologyCustomizationModel = new ShareableEntityModel(customizationRepository,
        classOf[Customization])
    {
        def createOntologyBased(name: String, ontologyURLs: Seq[String], owner: User): OntologyCustomization = {

            val customization = OntologyCustomization.empty(ontologyURLs, name, Some(owner))

            persist(customization)
            customization
        }

        def getAccessibleCustomizationsToUserById(user: Option[User], id: String): Option[OntologyCustomization] = {
            val customization = getAccessibleToUser(user).find(_.id == id)
            if(customization.isDefined) { customization.get.toOntologyCustomization().asInstanceOf[Option[OntologyCustomization]] } else { None }
        }

    }
}
