package cz.payola.web.shared.managers

import s2js.compiler._
import cz.payola.domain.entities.User
import cz.payola.domain.entities.settings._
import cz.payola.web.shared.Payola
import s2js.runtime.shared.rpc.RpcException
import cz.payola.model.ModelException
import cz.payola.common.ValidationException

/**
  * A set of ontology customizations divided by their ownership.
  * @param ownedCustomizations The customizations that are owned by the user. If [[scala.None]], the user can't even
  *                            own or create a customization (that's the case of an anonymous user).
  * @param othersCustomizations Customizations that aren't owned by the user, yet are accessible.
  */
class OntologyCustomizationsByOwnership(
    val ownedCustomizations: Option[Seq[cz.payola.common.entities.settings.OntologyCustomization]],
    val othersCustomizations: Seq[cz.payola.common.entities.settings.OntologyCustomization])

@remote
@secured object OntologyCustomizationManager
    extends ShareableEntityManager[OntologyCustomization, cz.payola.common.entities.settings.OntologyCustomization](
        Payola.model.ontologyCustomizationModel)
{
    @async def getByOwnership(user: Option[User] = null)
        (successCallback: OntologyCustomizationsByOwnership => Unit)
        (failCallback: Throwable => Unit) {

        val accessible = Payola.model.ontologyCustomizationModel.getAccessibleToUser(user)
        val (owned, others) = accessible.partition(user.isDefined && _.owner == user)
        successCallback(new OntologyCustomizationsByOwnership(user.map(_ => owned), others))
    }

    @async def create(name: String, ontologyURLs: String, owner: User = null)
        (successCallback: cz.payola.common.entities.settings.OntologyCustomization => Unit)
        (failCallback: Throwable => Unit) {

        if(ontologyURLs.isEmpty)
            successCallback(Payola.model.ontologyCustomizationModel.create(name, None, owner))
        else
            successCallback(Payola.model.ontologyCustomizationModel.create(name, Some(ontologyURLs.split(",")), owner))
    }

    @async def createGroupCustomization(customizationID: String, classURI: String, propertiesURIs: collection.immutable.Seq[String], owner: User = null)
        (successCallback: cz.payola.common.entities.settings.OntologyCustomization => Unit)
        (failCallback: Throwable => Unit)
    {
        createClassCustomization(customizationID, "group_" + classURI, propertiesURIs, owner)(successCallback)(failCallback)
    }

    @async def createClassCustomization(customizationID: String, classURI: String, propertiesURIs: collection.immutable.Seq[String], owner: User = null)
        (successCallback: cz.payola.common.entities.settings.OntologyCustomization => Unit)
        (failCallback: Throwable => Unit)
    {
        val propertyCustomizations = propertiesURIs.map { propertyURI =>
            new PropertyCustomization(propertyURI, "", 0)
        }
        val classCustomization = new ClassCustomization(classURI, "", 0, "", "", propertyCustomizations.toList)

        val optCustomization = Payola.model.ontologyCustomizationModel.getAccessibleToUserById(Some(owner), customizationID)
        if (optCustomization.isEmpty){
            failCallback(new RpcException("No such customization found!"))
        } else {
            optCustomization.get.appendClassCustomization(classCustomization) //append the new class
            Payola.model.ontologyCustomizationModel.persistUserDefined(optCustomization.get) //add the extended ontologyCustomization
            successCallback(optCustomization.get)
        }
    }

    @async def createPropertyCustomization(customizationID: String, classURI: String, propertyURI: String, owner: User = null)
        (successCallback: cz.payola.common.entities.settings.OntologyCustomization => Unit)
        (failCallback: Throwable => Unit)
    {
        val optCustomization = Payola.model.ontologyCustomizationModel.getAccessibleToUserById(Some(owner), customizationID)
        if (optCustomization.isEmpty){
            failCallback(new RpcException("No such customization found!"))
        } else {

            val classCustomizationOpt = optCustomization.get.classCustomizations.find(_.uri == classURI) //search for the class
            if (classCustomizationOpt.isEmpty) {
                failCallback(new RpcException("No such class customization found!"))
            } else {
                val propertyCustomization = new PropertyCustomization(propertyURI, "", 0)
                classCustomizationOpt.get.appendPropertyCustomization(propertyCustomization)

                Payola.model.ontologyCustomizationModel.persistUserDefined(optCustomization.get) //add the extended ontologyCustomization
                successCallback(optCustomization.get)
            }
        }
    }

    @async @secured def getCustomizationByID(id: String, user: User = null)
        (successCallback: cz.payola.common.entities.settings.OntologyCustomization => Unit)
        (failCallback: Throwable => Unit) {

        val opt = Payola.model.ontologyCustomizationModel.getAccessibleToUserById(Some(user), id)
        if (opt.isDefined) {
            successCallback(opt.get)
        }else{
            failCallback(new RpcException("Couldn't find customization."))
        }
    }

    @async @secured def getUsersCustomizations(user: User = null)
        (successCallback: Seq[cz.payola.common.entities.settings.OntologyCustomization] => Unit)
        (failCallback: Throwable => Unit) {

        try {
            successCallback(Payola.model.ontologyCustomizationModel.getAccessibleToUser(Some(user)))
        }catch{
            case t: Throwable => failCallback(t)
        }
    }

    @async @secured def setClassFillColor(customizationID: String, classURI: String, value: String, user: User = null)
        (successCallback: () => Unit)
        (failCallback: Throwable => Unit) {

        try {
            setClassAttribute(customizationID, classURI, { _.fillColor = value }, user, successCallback, failCallback)
        }catch{
            case t: Throwable => failCallback(t)
        }
    }

    @async @secured def setClassGlyph(customizationID: String, classURI: String, value: String, user: User = null)
        (successCallback: () => Unit)
        (failCallback: Throwable => Unit) {

        try {
            setClassAttribute(customizationID, classURI, { _.glyph = value }, user, successCallback, failCallback)
        } catch {
            case t: Throwable => failCallback(t)
        }
    }

    @async @secured def setClassRadius(customizationID: String, classURI: String, value: String, user: User = null)
        (successCallback: () => Unit)
        (failCallback: Throwable => Unit) {

        try {
            val int = validateInt(value, "radius")
            setClassAttribute(customizationID, classURI, { _.radius = int }, user, successCallback, failCallback)
        } catch {
            case t: Throwable => failCallback(t)
        }
    }

    @async @secured def setClassLabels(customizationID: String, classURI: String, value: String, user: User = null)
        (successCallback: () => Unit)
        (failCallback: Throwable => Unit) {

        try {
            setClassAttribute(customizationID, classURI, { _.labels = value }, user, successCallback, failCallback)
        } catch {
            case t: Throwable => failCallback(t)
        }
    }

    @async @secured def setPropertyStrokeColor(customizationID: String, classURI: String, propertyURI: String, value: String, user: User = null)
        (successCallback: () => Unit)
        (failCallback: Throwable => Unit) {

        try {
            setPropertyAttribute(customizationID, classURI, propertyURI, { _.strokeColor = value }, user, successCallback, failCallback)
        }catch{
            case t: Throwable => failCallback(t)
        }
    }

    @async @secured def setPropertyStrokeWidth(customizationID: String, classURI: String, propertyURI: String, value: String, user: User = null)
        (successCallback: () => Unit)
        (failCallback: Throwable => Unit) {

        try {
            val int = validateInt(value, "strokeWidth")
            setPropertyAttribute(customizationID, classURI, propertyURI, { _.strokeWidth = int }, user, successCallback, failCallback)
        } catch {
            case t: Throwable => failCallback(t)
        }
    }

    @async def delete(customizationID: String, owner: User = null)
        (successCallback: () => Unit)
        (failCallback: Throwable => Unit)
    {
        val customOpt = getOntologyCustomizationForIDWithSecurityChecks(customizationID, owner, failCallback)
        customOpt.foreach { customization =>
            Payola.model.ontologyCustomizationModel.remove(customization)
            successCallback()
        }
    }

    @async def rename(customizationID: String, newName: String, owner: User = null)
        (successCallback: () => Unit)
        (failCallback: Throwable => Unit)
    {
        val customOpt = getOntologyCustomizationForIDWithSecurityChecks(customizationID, owner, failCallback)
        customOpt.foreach { customization =>
            customization.name = newName
            Payola.model.ontologyCustomizationModel.persist(customization)
            successCallback()
        }
    }

    private def getClassCustomizationFromCustomization(customizationID: String, classURI: String, user: User, failCallback: Throwable => Unit): Option[ClassCustomization] = {
        val optCustomization = Payola.model.ontologyCustomizationModel.getAccessibleToUserById(Some(user), customizationID)
        if (optCustomization.isEmpty){
            // No such customization
            failCallback(new RpcException("No such customization found!"))
            None
        }else{
            val classCustomizationOpt = optCustomization.get.classCustomizations.find(_.uri == classURI)
            if (classCustomizationOpt.isEmpty) {
                failCallback(new RpcException("Class cannot be found in this ontology customization!"))
                None
            }else{
                classCustomizationOpt
            }
        }
    }

    private def getPropertyCustomizationFromCustomization(customizationID: String, classURI: String, propertyURI: String, user: User, failCallback: Throwable => Unit): Option[PropertyCustomization] = {
        val classOpt = getClassCustomizationFromCustomization(customizationID, classURI, user, failCallback)
        if (classOpt.isDefined) {
            val propOpt = classOpt.get.propertyCustomizations.find(_.uri == propertyURI)
            if (propOpt.isDefined) {
                propOpt
            }else{
                failCallback(new RpcException("Couldn't find property."))
                None
            }
        }else{
            None
        }
    }

    private def setClassAttribute(customizationID: String, classURI: String, setter: ClassCustomization => Unit, user: User, successCallback: () => Unit, failCallback: Throwable => Unit) {
        val optClassCustomization = getClassCustomizationFromCustomization(customizationID, classURI, user, failCallback)
        if (optClassCustomization.isDefined){
            setter(optClassCustomization.get)
            Payola.model.ontologyCustomizationModel.persistClassCustomization(optClassCustomization.get)
            successCallback()
        }
    }

    private def setPropertyAttribute(customizationID: String, classURI: String, propertyURI: String, setter: PropertyCustomization => Unit, user: User, successCallback: () => Unit, failCallback: Throwable => Unit) {
        val propertyOpt = getPropertyCustomizationFromCustomization(customizationID, classURI, propertyURI, user, failCallback)
        if (propertyOpt.isDefined) {
            setter(propertyOpt.get)
            Payola.model.ontologyCustomizationModel.persistPropertyCustomization(propertyOpt.get)
            successCallback()
        }
    }

    private def getOntologyCustomizationForIDWithSecurityChecks(id: String, owner: User, failCallback: Throwable => Unit): Option[OntologyCustomization] = {
        val customOpt = Payola.model.ontologyCustomizationModel.getById(id)
        if (customOpt.isDefined) {
            val customization = customOpt.get
            if (customization.owner.isDefined && customization.owner.get == owner) {
                customOpt
            }else{
                failCallback(new ModelException("Logged in user isn't owner of this customization."))
                None
            }
        }else{
            failCallback(new ModelException("The customization couldn't be found."))
            None
        }
    }

    private def validateInt(value: String, field: String): Int = {
        if (!value.matches("^[0-9]+$")){
            throw new ValidationException(field, "Value can contain only digits")
        }

        try
        {
            value.toInt
        } catch {
            case t: Throwable => throw new ValidationException(field, "Value is out of range")
        }
    }
}


