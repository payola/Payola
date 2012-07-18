package cz.payola.web.shared

import cz.payola.domain.entities.settings._
import s2js.compiler._
import cz.payola.domain.entities.User

@remote object OntologyCustomizationManager
{

    @async @secured def createNewOntologyCustomizationForURL(url: String, name: String, user: User = null) (successCallback: (cz.payola.common.entities.settings.OntologyCustomization => Unit))(failCallback: (Throwable => Unit)) {
        try {
            val ontologyCustomization = OntologyCustomization.empty(url, name, Some(user))

            successCallback(ontologyCustomization)
        } catch {
            case t: Throwable => {
                t.printStackTrace()
                failCallback(new Exception(t.getMessage))
            }
        }
    }

    /** Returns true if a customization with this name already exists.
      *
      * @param name Name of the potential customization.
      * @param user User.
      * @return True or false.
      */
    @secured def customizationExistsWithName(name: String, user: User = null): Boolean = {
        Payola.model.ontologyCustomizationModel.getAccessibleToUser(Some(user)).find(_.name == name).isDefined
    }

}
