package cz.payola.web.client.presenters

import s2js.adapters.js.dom.Element
import cz.payola.web.client.presenters.components.ShareButton
import cz.payola.web.shared._
import cz.payola.web.client.views.elements._
import cz.payola.web.client.views.bootstrap.Modal
import cz.payola.web.client.views.bootstrap.inputs.TextInputControl
import s2js.compiler.javascript
import cz.payola.common.entities.NamedEntity
import cz.payola.web.client.Presenter
import s2js.adapters.js.browser._

class ShareButtonPresenter(domElement: Element, entityType: String, entityID: String, isPublic: Boolean) extends Presenter
{
    val btn = new ShareButton(isPublic)
    btn.render(domElement)

    val id = entityID

    btn.makePublicLink.mouseClicked += {e =>
        setIsPublicHandler(id, btn)
        false
    }

    btn.dropDownButton.anchor.mouseClicked += { e =>
        setIsPublicHandler(id, btn)
        false
    }

    btn.shareToGroupLink.mouseClicked += {e =>
        shareToGroupHandler(id)
        false
    }
    btn.shareToUserLink.mouseClicked += {e =>
        shareToUserHandler(id)
        false
    }


    def setIsPublicHandler(id: String, shareButton: ShareButton) {
        val isPublicToSet = !shareButton.getIsPublic
        shareButton.setActive()
        SharingData.setIsPublic(entityType, id, isPublicToSet){ ok =>
            shareButton.setIsPublic(isPublicToSet)
            shareButton.setActive(false)
        }{ error => }
    }

    def shareToGroupHandler(id: String) {
        val modal = createModal(entityType,"group",{ (callback,value) =>
            SharingData.shareToGroup(entityType,id,value){ ok =>
                callback
                showSuccessModal(entityType,"group")
            }{ err => }
        })

        blockPage("Loading initial data.")
        SharingData.getAlreadySharedTo(entityType, id, "group"){ groups =>
            modal.render()
            bindGroupSelect(groups)
            unblockPage()
        }{err => }
    }

    def shareToUserHandler(id: String) {
        val modal = createModal(entityType,"user",{ (callback,value) =>
            SharingData.shareToUser(entityType,id,value){ ok =>
                callback
                showSuccessModal(entityType,"user")
            }{ err => }
        })

        blockPage("Loading initial data.")
        SharingData.getAlreadySharedTo(entityType, id, "user"){ users =>
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

    def initialize(){

    }
}
