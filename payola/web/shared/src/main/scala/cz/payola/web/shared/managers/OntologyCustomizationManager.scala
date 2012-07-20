package cz.payola.web.shared.managers

import s2js.compiler._
import cz.payola.domain.entities.User
import cz.payola.domain.entities.settings._
import cz.payola.web.shared.Payola
import s2js.runtime.shared.rpc.RpcException
import scala.Some

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

    @async @secured def getCustomizationByID(id: String, user: User = null)(successCallback: cz.payola.common.entities.settings.OntologyCustomization => Unit)(failCallback: Throwable => Unit) {
        val opt = Payola.model.ontologyCustomizationModel.getAccessibleToUserById(Some(user), id)
        if (opt.isDefined) {
            successCallback(opt.get)
        }else{
            failCallback(new RpcException("Couldn't find customization."))
        }
    }

    @async @secured def getUsersCustomizations(user: User = null)(successCallback: Seq[cz.payola.common.entities.settings.OntologyCustomization] => Unit)(failCallback: Throwable => Unit) = {
        try {
            successCallback(Payola.model.ontologyCustomizationModel.getAccessibleToUser(Some(user)))
        }catch{
            case t: Throwable => failCallback(t)
        }
    }

    private def getClassCustomizationFromCustomization(customizationID: String, classURI: String, user: User, failCallback: Throwable => Unit): Option[ClassCustomization] = {
        val optCustomization = Payola.model.ontologyCustomizationModel.getAccessibleToUserById(Some(user), customizationID)
        if (optCustomization.isEmpty){
            // No such customization
            failCallback(new RpcException("No such customization found!"))
            None
        }else{
            val customization = optCustomization.get
            val classCustomizationOpt = customization.classCustomizations.find(_.uri == classURI)
            if (classCustomizationOpt.isEmpty) {
                failCallback(new RpcException("Class cannot be found in this ontology customization!"))
                None
            }else{
                classCustomizationOpt
            }
        }
    }

    private def setClassAttribute(customizationID: String, classURI: String, setter: ClassCustomization => Unit, user: User, successCallback: Unit => Unit, failCallback: Throwable => Unit) {
        val optCustomization = getClassCustomizationFromCustomization(customizationID, classURI, user, failCallback)
        if (optCustomization.isDefined){
            val classCustomization = optCustomization.get
            setter(classCustomization)
            Payola.model.ontologyCustomizationModel.persist(classCustomization)
            successCallback()
        }
    }


    @async @secured def setClassFillColor(customizationID: String, classURI: String, value: String, user: User = null)
        (successCallback: Unit => Unit)
        (failCallback: Throwable => Unit) {
        setClassAttribute(customizationID, classURI, { _.fillColor = value }, user, successCallback, failCallback)
    }

    @async @secured def setClassRadius(customizationID: String, classURI: String, value: Int, user: User = null)
        (successCallback: Unit => Unit)
        (failCallback: Throwable => Unit) {
        println("Setting radius to " + value)
        setClassAttribute(customizationID, classURI, { _.radius = value }, user, successCallback, failCallback)
    }

}
