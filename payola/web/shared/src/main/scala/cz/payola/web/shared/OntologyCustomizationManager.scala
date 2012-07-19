package cz.payola.web.shared

import cz.payola.domain.entities.settings._
import s2js.compiler._
import cz.payola.domain.entities.User

@remote object OntologyCustomizationManager
{
    /** Creates a new ontology customization according to an ontology described
      * at URL passed.
      *
      * @param url URL with OWL (or other) ontology.
      * @param name Name of the customization.
      * @param user User creating it.
      * @param successCallback Success callback returning the created ontology customization.
      * @param failCallback Fail callback returning an exception.
      * @return
      */
    @async @secured def createNewOntologyCustomizationForURL(url: String, name: String, user: User = null) (successCallback: (cz.payola.common.entities.settings.OntologyCustomization => Unit))(failCallback: (Throwable => Unit)) {
        try {
            val ontologyCustomization = OntologyCustomization.empty(url, name, Some(user))

            // Persist the ontology customization
            Payola.model.ontologyCustomizationModel.persist(ontologyCustomization)

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
