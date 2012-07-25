package cz.payola.web.client.presenters

import s2js.adapters.js.browser.document
import cz.payola.web.client.views.entity.ShareButton
import s2js.compiler.javascript
import cz.payola.web.shared._
import cz.payola.web.client.views.bootstrap._
import s2js.adapters.js.browser.window
import cz.payola.web.client.views.bootstrap.inputs.TextInputControl
import cz.payola.web.client.Presenter
import cz.payola.web.client.views.elements._
import cz.payola.common.entities._
import cz.payola.web.client.events.BrowserEventArgs

class Sharing(shareButtonPlaceholderClass: String, entityType: String) extends Presenter
{
    val placeholderList = document.getElementsByClassName(shareButtonPlaceholderClass)

    def initialize() {

    }

    var i = 0
    while (i < placeholderList.length) {
        val placeholder = placeholderList.item(i)
        val id = placeholder.getAttribute("data-shareable-entity-id")
        val btn = new ShareButton(id, placeholder.getAttribute("data-shareable-entity-public").toBoolean)

        btn.makePublicButton.mouseClicked += onMakePublicButtonClicked _
        btn.dropDownButton.anchor.mouseClicked += onMakePublicButtonClicked _
        btn.shareToGroupButton.mouseClicked += onShareToGroupButtonClicked _
        btn.shareToUserButton.mouseClicked += onShareToUserButtonClicked _

        btn.render(placeholder)
        i+= 1
    }

    def onMakePublicButtonClicked(e: BrowserEventArgs[ShareButton]): Boolean = {
        val isPublicToSet = !e.target.isPublic
        e.target.setActive()
        SharingData.setIsPublic(entityType, e.target.entityId, isPublicToSet){ ok =>
            e.target.isPublic = isPublicToSet
            e.target.setActive(false)
        }{ error => }
        false
    }

    def onShareToGroupButtonClicked(e: BrowserEventArgs[ShareButton]): Boolean = {
        val modal = createModal(entityType,"group",{ (callback,value) =>
            SharingData.shareToGroup(entityType,e.target.entityId,value){ ok =>
                callback
                showSuccessModal(entityType,"group")
            }{ err => }
        })

        blockPage("Loading initial data.")
        SharingData.getAlreadySharedTo(entityType, e.target.entityId, "group"){ groups =>
            modal.render()
            bindGroupSelect(groups)
            unblockPage()
        }{err => }
        false
    }

    def onShareToUserButtonClicked(e: BrowserEventArgs[ShareButton]): Boolean = {
        val modal = createModal(entityType,"user",{ (callback,value) =>
            SharingData.shareToUser(entityType,e.target.entityId,value){ ok =>
                callback
                showSuccessModal(entityType,"user")
            }{ err => }
        })

        blockPage("Loading initial data.")
        SharingData.getAlreadySharedTo(entityType, e.target.entityId, "user"){ users =>
            modal.render()
            bindUserSelect(users)
            unblockPage()
        }{err => }
    }

    private def showSuccessModal(entityType:String, privilegedType: String){

        val successText = new Heading(List(new Text("The "+entityType+" was successfully shared to the selected "+privilegedType+"!")),3,"alert-heading")
        val body = List(new Div(List(successText),"alert alert-success"))

        val modal = new Modal("Success", body, None)

        modal.render()
        val timeout = window.setTimeout(() => {
            modal.destroy()
        }, 2000)

        modal.closing += {e =>
            window.clearTimeout(timeout)
            true
        }
    }

    private def createModal(entityName: String, privilegedType: String, callback: ((Unit, String) => Unit)) : Modal = {

        val privilegedSearchBox = new TextInputControl("Search "+privilegedType+":","privileged","init","Enter name")

        val body = List(privilegedSearchBox)
        val modal = new Modal("Share "+entityName+" to a "+privilegedType+":", body, Some("Share"))

        modal.closing += {e =>
            modal.destroy()
            true
        }

        modal.confirming += {e =>
            blockPage("Sharing a "+entityName+" to a "+privilegedType+"...")
            callback({ unblockPage() },privilegedSearchBox.input.value)
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

    def fetchUsersByQuery(term: String, itemCallback: (String, String) => Unit, callback: () => Unit){
        DomainData.searchUsers(term){ users =>
            users.map{ u =>
                itemCallback(u.id, u.name)
            }
            callback()
        }{ _ => }
    }

    def fetchGroupsByQuery(term: String, itemCallback: (String, String) => Unit, callback: () => Unit){
        DomainData.searchGroups(term){ groups =>
            groups.map{ g =>
                itemCallback(g.id, g.name)
            }
            callback()
        }{ _ => }
    }

}
