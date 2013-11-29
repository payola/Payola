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

class UserCustomizationsByOwnership(
    val ownedCustomizations: Option[Seq[cz.payola.common.entities.settings.UserCustomization]],
    val othersCustomizations: Seq[cz.payola.common.entities.settings.UserCustomization])

class CustomizationByOwnership(
    val ontologyCustomizations: OntologyCustomizationsByOwnership, val userCustomizations: UserCustomizationsByOwnership)

@remote
@secured object CustomizationManager
    extends ShareableEntityManager[Customization, cz.payola.common.entities.settings.Customization](
        Payola.model.ontologyCustomizationModel)
{
    @async def getOntologyCustomizationsByOwnership(user: Option[User] = null)
        (successCallback: OntologyCustomizationsByOwnership => Unit)
        (failCallback: Throwable => Unit) {

        val accessibleOntoCusts = Payola.model.ontologyCustomizationModel.getAccessibleToUser(user).filter(!_.isUserDefined)

        val (ownedOnto, othersOnto) = accessibleOntoCusts.partition(_.owner == user)

        successCallback(new OntologyCustomizationsByOwnership(
            Some(ownedOnto.map{_.convertToOntologyCustomization()}), othersOnto.map{_.convertToOntologyCustomization()}))
    }

    @async def getUserCustomizationsByOwnership(user: Option[User] = null)
        (successCallback: UserCustomizationsByOwnership => Unit)
        (failCallback: Throwable => Unit) {

        val accessibleUserCusts = Payola.model.userCustomizationModel.getAccessibleToUser(user).filter(_.isUserDefined)

        val (ownedUser, othersUser) = accessibleUserCusts.partition( _.owner == user)

        successCallback(new UserCustomizationsByOwnership(
            Some(ownedUser.map{_.convertToUserCustomization()}), othersUser.map{_.convertToUserCustomization()}))
    }

    @async def getCustomizationsByOwnership(user: Option[User] = null)
        (successCallback: CustomizationByOwnership => Unit)
        (failCallback: Throwable => Unit) {

        val accessibleUserCusts = Payola.model.userCustomizationModel.getAccessibleToUser(user).filter(_.isUserDefined)
        val (ownedUser, othersUser) = accessibleUserCusts.partition(user.isDefined && _.owner == user)

        val accessibleOntoCusts = Payola.model.ontologyCustomizationModel.getAccessibleToUser(user).filter(!_.isUserDefined)
        val (ownedOnto, othersOnto) = accessibleOntoCusts.partition(_.owner == user)

        successCallback(new CustomizationByOwnership(
            new OntologyCustomizationsByOwnership(
                Some(ownedOnto.map{_.convertToOntologyCustomization()}), othersOnto.map{_.convertToOntologyCustomization()}),
            new UserCustomizationsByOwnership(
                Some(ownedUser.map{_.convertToUserCustomization()}), othersUser.map{_.convertToUserCustomization()})))
    }



    @async def createByOntology(name: String, ontologyURLs: String, owner: User = null)
        (successCallback: cz.payola.common.entities.settings.OntologyCustomization => Unit)
        (failCallback: Throwable => Unit) {

        successCallback(Payola.model.ontologyCustomizationModel.createOntologyBased(name, ontologyURLs.split(","), owner))
    }

    @async def createByUser(name: String, owner: User = null)
        (successCallback: cz.payola.common.entities.settings.UserCustomization => Unit)
        (failCallback: Throwable => Unit) {

        successCallback(Payola.model.userCustomizationModel.createUserBased(name, owner))
    }

    @async def createGroupCustomization(customizationID: String, classURI: String, owner: User = null)
        (successCallback: cz.payola.common.entities.settings.UserCustomization => Unit)
        (failCallback: Throwable => Unit)
    {
        createClassCustomization(customizationID, "group_" + classURI, List[String](),
            owner)(successCallback)(failCallback)
    }

    @async def createClassCustomization(customizationID: String, classURI: String,
        propertiesURIs: collection.immutable.Seq[String], owner: User = null)
        (successCallback: cz.payola.common.entities.settings.UserCustomization => Unit)
        (failCallback: Throwable => Unit)
    {
        val propertyCustomizations = propertiesURIs.map { propertyURI =>
            new PropertyCustomization(propertyURI, "", 0)
        }
        val classCustomization = new ClassCustomization(classURI, "", 0, "", "", "", propertyCustomizations.toList, 0)

        val optCustomization =
            Payola.model.userCustomizationModel.getAccessibleCustomizationsToUserById(Some(owner), customizationID)
        if (optCustomization.isEmpty){
            failCallback(new RpcException("No such customization found!"))
        } else {
            optCustomization.get.appendClassCustomization(classCustomization) //append the new class
            Payola.model.userCustomizationModel.persistUserDefined(optCustomization.get) //add the extended userCustomization
            successCallback(optCustomization.get)
        }
    }

    @async def deleteClassCustomization(customizationID: String, classId: String, owner: User = null)
        (successCallback: cz.payola.common.entities.settings.UserCustomization => Unit)
        (failCallback: Throwable => Unit)
    {
        val customOpt = getUserCustomizationForIDWithSecurityChecks(customizationID, owner, failCallback)
        customOpt.foreach { customization =>
            if(customization.classCustomizations.exists(_.id == classId)) {
                Payola.model.userCustomizationModel.removeClassCustomization(classId)
                successCallback(
                    getUserCustomizationForIDWithSecurityChecks(customizationID, owner, failCallback).get) //get the updated customization
            } else {
                failCallback(new RpcException("No such class customization found!"))
            }
        }

        failCallback(new RpcException("No such customization found!"))
    }



    @async def createConditionalCustomization(customizationID: String, classURI: String, owner: User = null)
        (successCallback: cz.payola.common.entities.settings.UserCustomization => Unit)
        (failCallback: Throwable => Unit)
    {
        createClassCustomization(customizationID, "condition_" + classURI, List[String](),
            owner)(successCallback)(failCallback)
    }

    @async def createPropertyCustomization(customizationID: String, classURI: String, propertyURI: String, owner: User = null)
        (successCallback: cz.payola.common.entities.settings.UserCustomization => Unit)
        (failCallback: Throwable => Unit)
    {
        val optCustomization: Option[UserCustomization] =
            Payola.model.userCustomizationModel.getAccessibleCustomizationsToUserById(Some(owner), customizationID) //only for userCustomization
        if (optCustomization.isEmpty){
            failCallback(new RpcException("No such customization found!"))
        } else {

            val classCustomizationOpt = optCustomization.get.classCustomizations.find(_.uri == classURI) //search for the class
            if (classCustomizationOpt.isEmpty) {
                failCallback(new RpcException("No such class customization found!"))
            } else {
                val propertyCustomization = new PropertyCustomization(propertyURI, "", 0)
                classCustomizationOpt.get.appendPropertyCustomization(propertyCustomization)

                Payola.model.userCustomizationModel.persistUserDefined(optCustomization.get) //add the extended userCustomization
                successCallback(Payola.model.userCustomizationModel.getAccessibleCustomizationsToUserById(Some(owner), customizationID).get)
            }
        }
    }

    @async def deletePropertyCustomization(customizationID: String, propertyId: String, owner: User = null)
        (successCallback: cz.payola.common.entities.settings.UserCustomization => Unit)
        (failCallback: Throwable => Unit)
    {
        val customOpt = getUserCustomizationForIDWithSecurityChecks(customizationID, owner, failCallback)
        customOpt.foreach { customization =>

            if(customization.classCustomizations.exists(_.propertyCustomizations.exists(_.id == propertyId))) {
                Payola.model.userCustomizationModel.removePropertyCustomization(propertyId)
                successCallback(
                    getUserCustomizationForIDWithSecurityChecks(customizationID, owner, failCallback).get) //get the updated customization
            } else {
                failCallback(new RpcException("No such property customization found!"))
            }
        }

        failCallback(new RpcException("No such customization found!"))
    }

    @async @secured def getOntologyCustomizationByID(id: String, user: User = null)
        (successCallback: cz.payola.common.entities.settings.OntologyCustomization => Unit)
        (failCallback: Throwable => Unit) {

        val opt = Payola.model.ontologyCustomizationModel.getAccessibleCustomizationsToUserById(Some(user), id)
        if (opt.isDefined) {
            successCallback(opt.get)
        }else{
            failCallback(new RpcException("Couldn't find customization."))
        }
    }

    @async @secured def getUserCustomizationByID(id: String, user: User = null)
        (successCallback: cz.payola.common.entities.settings.UserCustomization => Unit)
        (failCallback: Throwable => Unit) {

        val opt = Payola.model.userCustomizationModel.getAccessibleCustomizationsToUserById(Some(user), id)
        if (opt.isDefined) {
            successCallback(opt.get)
        }else{
            failCallback(new RpcException("Couldn't find customization."))
        }
    }

    @async @secured def getUsersCustomizations(user: User = null)
        (successCallback: Seq[cz.payola.common.entities.settings.UserCustomization] => Unit)
        (failCallback: Throwable => Unit) {

        try {
            successCallback(Payola.model.ontologyCustomizationModel.getAccessibleToUser(
                Some(user)).filter(c => c.isUserDefined).map(_.convertToUserCustomization()))
        }catch{
            case t: Throwable => failCallback(t)
        }
    }

    @async @secured def setClassFillColor(customizationID: String, classURI: String, conditionValue: String, value: String, user: User = null)
        (successCallback: () => Unit)(failCallback: Throwable => Unit) {

        try {
            setClassAttribute(customizationID, classURI, conditionValue, { _.fillColor = value }, user, successCallback, failCallback)
        }catch{
            case t: Throwable => failCallback(t)
        }
    }

    @async @secured def setClassGlyph(customizationID: String, classURI: String, conditionValue: String, value: String, user: User = null)
        (successCallback: () => Unit)(failCallback: Throwable => Unit) {

        try {
            setClassAttribute(customizationID, classURI, conditionValue, { _.glyph = value }, user, successCallback, failCallback)
        } catch {
            case t: Throwable => failCallback(t)
        }
    }

    @async @secured def setClassRadius(customizationID: String, classURI: String, conditionValue: String, value: String, user: User = null)
        (successCallback: () => Unit)(failCallback: Throwable => Unit) {

        try {
            val int = validateInt(value, "radius")
            setClassAttribute(customizationID, classURI, conditionValue, { _.radius = int }, user, successCallback, failCallback)
        } catch {
            case t: Throwable => failCallback(t)
        }
    }

    @async @secured def setClassCondition(customizationID: String, classURI: String, conditionValue: String, value: String, user: User = null)
        (successCallback: () => Unit)(failCallback: Throwable => Unit) {

        try {
            setClassAttribute(customizationID, classURI, conditionValue, { _.conditionalValue = value }, user, successCallback, failCallback)
        } catch {
            case t: Throwable => failCallback(t)
        }
    }

    @async @secured def setClassOrder(customizationID: String, classURI: String, conditionValue: String, value: String, user: User = null)
        (successCallback: () => Unit)(failCallback: Throwable => Unit) {

        try {
            val int = validateInt(value, "order number")
            setClassAttribute(customizationID, classURI, conditionValue, { _.orderNumber = int }, user, successCallback, failCallback)
        } catch {
            case t: Throwable => failCallback(t)
        }
    }

    @async @secured def setClassLabels(customizationID: String, classURI: String, conditionValue: String, value: String, user: User = null)
    (successCallback: () => Unit)(failCallback: Throwable => Unit) {

        try {
            setClassAttribute(customizationID, classURI, conditionValue, { _.labels = value }, user, successCallback, failCallback)
        } catch {
            case t: Throwable => failCallback(t)
        }
    }

    @async @secured def setPropertyStrokeColor(customizationID: String, classURI: String, conditionValue: String, propertyURI: String, value: String, user: User = null)
        (successCallback: () => Unit)(failCallback: Throwable => Unit) {

        try {
            setPropertyAttribute(customizationID, classURI, conditionValue, propertyURI, { _.strokeColor = value }, user, successCallback, failCallback)
        }catch{
            case t: Throwable => failCallback(t)
        }
    }

    @async @secured def setPropertyStrokeWidth(customizationID: String, classURI: String, conditionValue: String, propertyURI: String, value: String, user: User = null)
        (successCallback: () => Unit)(failCallback: Throwable => Unit) {

        try {
            val int = validateInt(value, "strokeWidth")
            setPropertyAttribute(customizationID, classURI, conditionValue, propertyURI, { _.strokeWidth = int }, user, successCallback, failCallback)
        } catch {
            case t: Throwable => failCallback(t)
        }
    }

    @async def delete(customizationID: String, owner: User = null)
        (successCallback: () => Unit)(failCallback: Throwable => Unit)
    {
        val customOpt = getOntologyCustomizationForIDWithSecurityChecks(customizationID, owner, failCallback)
        customOpt.foreach { customization =>
            Payola.model.ontologyCustomizationModel.remove(customization)
            successCallback()
        }

        //if no ontology customization was found try user customizations
        val userCustomOpt = getUserCustomizationForIDWithSecurityChecks(customizationID, owner, failCallback)
        userCustomOpt.foreach { customization =>
            Payola.model.userCustomizationModel.remove(customization)
            successCallback()
        }

        failCallback(new RpcException("No such customization found!"))
    }

    @async def rename(customizationID: String, newName: String, owner: User = null)
        (successCallback: () => Unit)(failCallback: Throwable => Unit)
    {
        val customOpt = getOntologyCustomizationForIDWithSecurityChecks(customizationID, owner, failCallback)
        customOpt.foreach { customization =>
            customization.name = newName
            Payola.model.ontologyCustomizationModel.persist(customization)
            successCallback()
        }

        val customUserOpt = getUserCustomizationForIDWithSecurityChecks(customizationID, owner, failCallback)
        customUserOpt.foreach { customization =>
            customization.name = newName
            Payola.model.userCustomizationModel.persist(customization)
            successCallback()
        }

        failCallback(new RpcException("No such customization found!"))
    }

    private def getClassCustomizationOfCustomization(customizationID: String, classURI: String, conditionValue: String,
        user: User, failCallback: Throwable => Unit): Option[ClassCustomization] = {

        val optOntoCustomization = Payola.model.ontologyCustomizationModel.getAccessibleCustomizationsToUserById(Some(user), customizationID)
        val optUserCustomization = Payola.model.userCustomizationModel.getAccessibleCustomizationsToUserById(Some(user), customizationID)

        if (optOntoCustomization.isEmpty && optUserCustomization.isEmpty){
            // No such customization
            failCallback(new RpcException("No such customization found!"))
            None
        } else {
            val classCustomizationOntoOpt =
                if(optOntoCustomization.isDefined) { optOntoCustomization.get.classCustomizations.find(_.uri == classURI) }
                else { None }
            val classCustomizationUserOpt =
                if(optUserCustomization.isDefined) {
                    optUserCustomization.get.classCustomizations.find(cc => cc.uri == classURI && cc.conditionalValue ==  conditionValue)
                } else { None }

            if (classCustomizationOntoOpt.isEmpty && classCustomizationUserOpt.isEmpty) {
                failCallback(new RpcException("Class cannot be found in any ontology/user customization!"))
                None
            } else {
                if(classCustomizationOntoOpt.isDefined) {
                    classCustomizationOntoOpt
                } else {
                    classCustomizationUserOpt
                }
            }
        }
    }

    private def getPropertyCustomizationOfCustomization(customizationID: String, classURI: String, conditionValue: String,
        propertyURI: String, user: User, failCallback: Throwable => Unit): Option[PropertyCustomization] = {

        val classOpt = getClassCustomizationOfCustomization(customizationID, classURI, conditionValue, user, failCallback)
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

    private def setClassAttribute(customizationID: String, classURI: String,  conditionValue: String,
        setter: ClassCustomization => Unit, user: User, successCallback: () => Unit, failCallback: Throwable => Unit) {

        val optClassCustomization = getClassCustomizationOfCustomization(customizationID, classURI, conditionValue, user, failCallback)
        if (optClassCustomization.isDefined){
            //Console.println("setting to: "+optClassCustomization.get.uri+" value: "+optClassCustomization.get.conditionalValue)
            setter(optClassCustomization.get)
            Payola.model.userCustomizationModel.persistClassCustomization(optClassCustomization.get)
            successCallback()
        }
    }

    private def setPropertyAttribute(customizationID: String, classURI: String, conditionValue: String,
        propertyURI: String, setter: PropertyCustomization => Unit, user: User, successCallback: () => Unit, failCallback: Throwable => Unit) {

        val propertyOpt = getPropertyCustomizationOfCustomization(customizationID, classURI, conditionValue, propertyURI, user, failCallback)
        if (propertyOpt.isDefined) {
            setter(propertyOpt.get)
            Payola.model.userCustomizationModel.persistPropertyCustomization(propertyOpt.get)
            successCallback()
        }

    }

    private def getOntologyCustomizationForIDWithSecurityChecks(id: String, owner: User, failCallback: Throwable => Unit):
    Option[OntologyCustomization] = {

        val customOpt = Payola.model.ontologyCustomizationModel.getById(id)
        if (customOpt.isDefined) {
            val customization = customOpt.get
            if (customization.owner.isDefined && customization.owner.get == owner) {
                customization.toOntologyCustomization().asInstanceOf[Option[OntologyCustomization]]
            }else{
                failCallback(new ModelException("Logged in user isn't owner of this customization."))
                None
            }
        }else{
            failCallback(new ModelException("The customization couldn't be found."))
            None
        }
    }

    private def getUserCustomizationForIDWithSecurityChecks(id: String, owner: User, failCallback: Throwable => Unit):
    Option[UserCustomization] = {

        val customOpt = Payola.model.userCustomizationModel.getById(id)
        if (customOpt.isDefined) {
            val customization = customOpt.get
            if (customization.owner.isDefined && customization.owner.get == owner) {
                customization.toUserCustomization().asInstanceOf[Option[UserCustomization]]
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


