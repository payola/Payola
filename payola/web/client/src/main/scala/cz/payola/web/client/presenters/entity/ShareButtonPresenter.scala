package cz.payola.web.client.presenters.entity

import s2js.adapters.js.dom
import cz.payola.web.shared._
import cz.payola.web.client.views.elements._
import cz.payola.web.client.views.bootstrap.Modal
import cz.payola.web.client.views.bootstrap.inputs.TextInputControl
import s2js.compiler.javascript
import cz.payola.common.entities._
import cz.payola.web.client._
import s2js.adapters.js.browser._
import cz.payola.web.client.views.entity.ShareButton
import cz.payola.web.client.events.EventArgs
import scala.Some

class ShareButtonPresenter(
    val viewElement: dom.Element,
    val entityType: String,
    val entityId: String,
    entityIsPublic: Boolean,
    val viewToBlock: Option[View] = None)
    extends Presenter
{
    private val view = new ShareButton(entityIsPublic)

    def initialize() {
        view.makePublicButton.mouseClicked += onMakePublicButtonClicked _
        view.dropDownButton.anchor.mouseClicked += onMakePublicButtonClicked _
        view.shareToGroupButton.mouseClicked += onShareToGroupButtonClicked _
        view.shareToUserButton.mouseClicked += onShareToUserButtonClicked _

        view.render(viewElement)
    }

    def onMakePublicButtonClicked(e: EventArgs[_]): Boolean = {
        view.setIsEnabled(false)
        SharingData.setIsPublic(entityType, entityId, !view.isPublic) { _ =>
            view.isPublic = !view.isPublic
            view.setIsEnabled(true)
        }(fatalErrorHandler(_))
        false
    }

    def onShareToGroupButtonClicked(e: EventArgs[_]): Boolean = {
        blockView("Fetching share data.")
        SharingData.getAlreadySharedTo(entityType, entityId, "group") { groups =>
            val modal = createModal(entityType, "group", { (callback, value) =>
                blockView("Saving.")
                SharingData.shareToGroup(entityType, entityId, value) { _ =>
                    callback
                    unblockView()
                    showSuccessModal(entityType, "group")
                }(fatalErrorHandler(_))
            })
            unblockView()
            modal.render()
            bindGroupSelect(groups)
        }(fatalErrorHandler(_))
        false
    }

    def onShareToUserButtonClicked(e: EventArgs[_]): Boolean = {
        blockView("Fetching share data.")
        SharingData.getAlreadySharedTo(entityType, entityId, "user") { users =>
            val modal = createModal(entityType, "user", { (callback, value) =>
                blockView("Saving.")
                SharingData.shareToUser(entityType, entityId, value) { _ =>
                    callback
                    unblockView()
                    showSuccessModal(entityType, "user")
                }(fatalErrorHandler(_))
            })
            unblockView()
            modal.render()
            bindUserSelect(users)
        }(fatalErrorHandler(_))
        false
    }

    private def showSuccessModal(entityType: String, privilegedType: String) {
        val successText = new Heading(
            List(new Text("The " + entityType + " was successfully shared to the selected " + privilegedType + "!")), 3,
            "alert-heading")
        val body = List(new Div(List(successText), "alert alert-success"))

        val modal = new Modal("Success", body, None)

        modal.render()
        val timeout = window.setTimeout(() => {
            modal.destroy()
        }, 2000)

        modal.closing += {
            e =>
                window.clearTimeout(timeout)
                true
        }
    }

    private def createModal(entityName: String, privilegedType: String, callback: ((Unit, String) => Unit)): Modal = {
        val privilegedSearchBox = new
                TextInputControl("Search " + privilegedType + ":", "privileged", "init", "Enter name")

        val body = List(privilegedSearchBox)
        val modal = new Modal("Share " + entityName + " to a " + privilegedType + ":", body, Some("Share"))

        modal.closing += {
            e =>
                modal.destroy()
                true
        }

        modal.confirming += {
            e =>
                blockPage("SharingPresenter a " + entityName + " to a " + privilegedType + "...")
                callback({
                    unblockPage()
                }, privilegedSearchBox.input.value)
                modal.destroy()
                true
        }
        modal
    }

    @javascript(
        """
          jQuery("#privileged").select2({
              minimumInputLength: 1,
              multiple: true,
              query: function (query) {
                  var data = {results: []};
                  self.fetchUsersByQuery(
                    query.term,
                    function(id, text){ data.results.push({id: id, text: text}); },
                    function(){ query.callback(data); }
                  );
              },
              initSelection : function (element) {
                var data = [];
                users.foreach(function(x){
                    data.push({id: x.id, text: x.name()});
                });
                return data;
              }
            });
        """)
    def bindUserSelect(users: Seq[NamedEntity]) {}

    @javascript(
        """
          jQuery("#privileged").select2({
              minimumInputLength: 1,
              multiple: true,
              query: function (query) {
                  var data = {results: []};
                  self.fetchGroupsByQuery(
                    query.term,
                    function(id, text){ data.results.push({id: id, text: text}); },
                    function(){ query.callback(data); }
                  );
              },
              initSelection: function (element) {
                  var data = [];
                  groups.foreach(function(x){
                    data.push({id: x.id, text: x.name()});
                  });
                  return data;
              }
            });
        """)
    def bindGroupSelect(groups: Seq[NamedEntity]) {}

    def fetchUsersByQuery(term: String, itemCallback: (String, String) => Unit, callback: () => Unit) {
        DomainData.searchUsers(term) {
            users =>
                users.map {
                    u =>
                        itemCallback(u.id, u.name)
                }
                callback()
        } {
            _ =>
        }
    }

    def fetchGroupsByQuery(term: String, itemCallback: (String, String) => Unit, callback: () => Unit) {
        DomainData.searchGroups(term) {
            groups =>
                groups.map {
                    g =>
                        itemCallback(g.id, g.name)
                }
                callback()
        } {
            _ =>
        }
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
