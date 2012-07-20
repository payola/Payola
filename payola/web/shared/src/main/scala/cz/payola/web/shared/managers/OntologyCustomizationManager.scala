package cz.payola.web.shared.managers

import s2js.compiler._
import cz.payola.domain.entities.User
import cz.payola.domain.entities.settings._
import cz.payola.web.shared.Payola
import s2js.runtime.shared.rpc.RpcException
import scala.Some
import cz.payola.common.ValidationException

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

    private def getClassCustomizationFromCustomization(customization: OntologyCustomization, classURI: String, user: User, failCallback: Throwable => Unit): Option[ClassCustomization] = {
        val classCustomizationOpt = customization.classCustomizations.find(_.uri == classURI)
        if (classCustomizationOpt.isEmpty) {
            failCallback(new RpcException("Class cannot be found in this ontology customization!"))
            None
        }else{
            classCustomizationOpt
        }
    }

    private def setClassAttribute(customizationID: String, classURI: String, setter: ClassCustomization => Unit, user: User, successCallback: String => Unit, failCallback: Throwable => Unit) {
        val optCustomization = Payola.model.ontologyCustomizationModel.getAccessibleToUserById(Some(user), customizationID)
        if (optCustomization.isEmpty){
            // No such customization
            failCallback(new RpcException("No such customization found!"))
            None
        }else{
            val customization = optCustomization.get
            val optClassCustomization = getClassCustomizationFromCustomization(customization, classURI, user, failCallback)
            if (optClassCustomization.isDefined){
                val classCustomization = optClassCustomization.get
                setter(classCustomization)
                Payola.model.ontologyCustomizationModel.persist(customization)
                successCallback("")
            }
        }
    }


    @async @secured def setClassFillColor(customizationID: String, classURI: String, value: String, user: User = null)
        (successCallback: String => Unit)
        (failCallback: Throwable => Unit) {
        setClassAttribute(customizationID, classURI, { _.fillColor = value }, user, successCallback, failCallback)
    }

    @async @secured def setClassGlyph(customizationID: String, classURI: String, value: Option[Char], user: User = null)
        (successCallback: String => Unit)
        (failCallback: Throwable => Unit) {
        setClassAttribute(customizationID, classURI, { _.glyph = value }, user, successCallback, failCallback)
    }

    @async @secured def setClassRadius(customizationID: String, classURI: String, value: Int, user: User = null)
        (successCallback: String => Unit)
        (failCallback: Throwable => Unit) {
        setClassAttribute(customizationID, classURI, { _.radius = 66 }, user, successCallback, failCallback)
    }


    private def setPropertyAttribute(customizationID: String, classURI: String, propertyURI: String, setter: PropertyCustomization => Unit, user: User, successCallback: String => Unit, failCallback: Throwable => Unit) {
        val optCustomization = Payola.model.ontologyCustomizationModel.getAccessibleToUserById(Some(user), customizationID)
        if (optCustomization.isEmpty){
            // No such customization
            failCallback(new RpcException("No such customization found!"))
            None
        }else{
            val customization = optCustomization.get
            val optClassCustomization = getClassCustomizationFromCustomization(customization, classURI, user, failCallback)
            if (optClassCustomization.isDefined){
                val classCustomization = optClassCustomization.get
                val propertyOpt = classCustomization.propertyCustomizations.find(_.uri == propertyURI)
                if (propertyOpt.isDefined) {
                    setter(propertyOpt.get)
                    Payola.model.ontologyCustomizationModel.persist(customization)
                    successCallback("")
                }else{
                    failCallback(new RpcException("Couldn't find property!"))
                    None
                }
            }
        }
    }

    @async @secured def setPropertyStrokeColor(customizationID: String, classURI: String, propertyURI: String, value: String, user: User = null)
        (successCallback: String => Unit)
        (failCallback: Throwable => Unit) {
        setPropertyAttribute(customizationID, classURI, propertyURI, { _.strokeColor = value }, user, successCallback, failCallback)
    }

    @async @secured def setPropertyStrokeWidth(customizationID: String, classURI: String, propertyURI: String, value: Int, user: User = null)
        (successCallback: String => Unit)
        (failCallback: Throwable => Unit) {
        setPropertyAttribute(customizationID, classURI, propertyURI, { _.strokeWidth = value }, user, successCallback, failCallback)
    }

}


