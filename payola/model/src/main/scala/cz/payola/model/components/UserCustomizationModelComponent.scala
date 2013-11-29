package cz.payola.model.components

import cz.payola.model.EntityModelComponent
import cz.payola.data.DataContextComponent
import cz.payola.domain.RdfStorageComponent
import cz.payola.domain.entities.settings._
import cz.payola.domain.entities.User
import scala.collection.mutable

trait UserCustomizationModelComponent extends EntityModelComponent
{
    self: DataContextComponent with RdfStorageComponent with PrivilegeModelComponent =>

    lazy val userCustomizationModel = new ShareableEntityModel(customizationRepository,
        classOf[Customization])
    {
        def createUserBased(name: String, owner: User): UserCustomization = {

            val customization = UserCustomization.empty(name, Some(owner))

            persist(customization)
            customization
        }

        def persistUserDefined(userCustomization: UserCustomization) {
            persist(userCustomization)
        }

        def persistClassCustomization(customization: ClassCustomization) {
            customizationRepository.persistClassCustomization(customization)
        }

        def persistPropertyCustomization(customization: PropertyCustomization) {
            customizationRepository.persistPropertyCustomization(customization)
        }

        def getAccessibleCustomizationsToUserById(user: Option[User], id: String): Option[UserCustomization] = {
            val customization = getAccessibleToUser(user).find(_.id == id)
            if(customization.isDefined) { customization.get.toUserCustomization() } else { None }
        }

        def removeClassCustomization(id: String) {
            customizationRepository.removeClassCustomizationById(id)
        }

        def removePropertyCustomization(id: String) {
            customizationRepository.removePropertyCustomizationById(id)
        }
    }
}
