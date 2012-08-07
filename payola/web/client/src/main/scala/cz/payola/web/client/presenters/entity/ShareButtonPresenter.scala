package cz.payola.web.client.presenters.entity

import s2js.adapters.html
import cz.payola.web.shared._
import cz.payola.common.entities._
import cz.payola.web.client._
import cz.payola.web.client.views.entity._
import cz.payola.web.client.events._
import cz.payola.common.Entity
import cz.payola.web.client.views.bootstrap.modals.AlertModal
import s2js.adapters.html.Element
import s2js.adapters

class ShareButtonPresenter(
    val viewElement: html.Element,
    val entityClassName: String,
    val entityId: String,
    val entityName: String,
    entityIsPublic: Boolean,
    val viewToBlock: Option[View] = None)
    extends Presenter
{
    val publicityChanged = new SimpleUnitEvent[Boolean]

    private val view = new ShareButton(entityIsPublic)

    def initialize() {
        view.makePublicButton.mouseClicked += onMakePublicButtonClicked _
        view.dropDownButton.anchor.mouseClicked += onMakePublicButtonClicked _
        view.shareToGroupsButton.mouseClicked += onShareToGroupsButtonClicked _
        view.shareToUsersButton.mouseClicked += onShareToUsersButtonClicked _

        view.render(viewElement)
    }

    private def onMakePublicButtonClicked(e: EventArgs[_]): Boolean = {
        view.setIsEnabled(false)
        val newPublicity = !view.isPublic
        SharingData.setEntityPublicity(entityClassName, entityId, newPublicity) { () =>
            view.isPublic = newPublicity
            view.setIsEnabled(true)
            publicityChanged.triggerDirectly(newPublicity)
        }(fatalErrorHandler(_))
        false
    }

    private def onShareToUsersButtonClicked(e: EventArgs[_]): Boolean = {
        onShareButtonClicked(Entity.getClassName(classOf[User]), "user")
        false
    }

    private def onShareToGroupsButtonClicked(e: EventArgs[_]): Boolean = {
        onShareButtonClicked(Entity.getClassName(classOf[Group]), "user group")
        false
    }

    private def onShareButtonClicked(granteeClassName: String, granteeClassNameText: String) {
        blockView("Fetching share data.")
        SharingData.getEntityGrantees(entityClassName, entityId, granteeClassName) { grantees =>
            unblockView()

            val shareModal = new ShareModal(entityName, granteeClassNameText, grantees)
            shareModal.granteeSearching += { e =>
                SharingData.searchPotentialGrantees(granteeClassName, e.searchTerm) {
                    e.successCallback(_)
                }(fatalErrorHandler(_))
            }
            shareModal.confirming += { e =>
                blockView("Sharing.")
                val granteeIds = shareModal.granteeSelection.field.value
                SharingData.shareEntity(entityClassName, entityId, granteeClassName, granteeIds) { () =>
                    unblockView()
                    AlertModal.display("Success", "The entity was successfully shared to selected " +
                        granteeClassNameText + "s.", "alert-success", Some(4000))
                }(fatalErrorHandler(_))
                true
            }
            shareModal.render()

        }(fatalErrorHandler(_))
    }

    private def blockView(message: String) {
        if (viewToBlock.isDefined) {
            viewToBlock.get.block(message)
        } else {
            blockPage(message)
        }
    }

    private def unblockView() {
        if (viewToBlock.isDefined) {
            viewToBlock.get.unblock()
        } else {
            unblockPage()
        }
    }
}

object ShareButtonPresenter
{
    def apply(viewElement: html.Element, entity: ShareableEntity, viewToBlock: Option[View]): ShareButtonPresenter = {
        new ShareButtonPresenter(viewElement, entity.className, entity.id, entity.name, entity.isPublic, viewToBlock)
    }
}
