package cz.payola.web.client.presenters

import s2js.adapters.js.browser.document
import cz.payola.web.client.presenters.components.ShareButton
import s2js.compiler.javascript
import cz.payola.web.shared._
import cz.payola.web.client.views.bootstrap._
import scala.Some
import s2js.adapters.js.browser.window

class Sharing(shareButtonPlaceholderClass: String, entityType: String)
{
    val placeholderList = document.getElementsByClassName(shareButtonPlaceholderClass)

    placeholderList.foreach{ placeholder =>
        val btn = new ShareButton(placeholder.getAttribute("data-shareable-entity-public").toBoolean)
        btn.render(placeholder)

        val id = placeholder.getAttribute("data-shareable-entity-id")

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
        val modal = createModal(entityType,"group",{ value =>
            SharingData.shareToGroup(entityType,id,value){ ok =>
            }{ err => }
        })

        modal.render()
        bindGroupSelect
    }

    def shareToUserHandler(id: String) {
        val modal = createModal(entityType,"user",{ value =>
            SharingData.shareToUser(entityType,id,value){ ok =>
            }{ err => }
        })

        modal.render()
        bindUserSelect
    }

    private def createModal(entityName: String, privilegedType: String, callback: (String => Unit)) : Modal = {

        val privilegedSearchBox = new TextInputControl("Search "+privilegedType+":","privileged","","Enter name")

        val body = List(privilegedSearchBox)
        val modal = new Modal("Share "+entityName+" to a "+privilegedType+":", body, Some("Share"))
        modal.closing += {e =>
            modal.destroy()
            true
        }

        modal.saving += {e =>
            callback(privilegedSearchBox.input.value)
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
              /*initSelection : function (element) {
                  var data = [];
                  $(element.val().split(",")).each(function () {
                      var parts = this.split(":");
                      data.push({id: parts[0], text: parts[1]});
                  });
                  return data;
              }*/
            });
        """)
    def bindUserSelect = {}

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
              /*initSelection : function (element) {
                  var data = [];
                  $(element.val().split(",")).each(function () {
                      var parts = this.split(":");
                      data.push({id: parts[0], text: parts[1]});
                  });
                  return data;
              }*/
            });
        """)
    def bindGroupSelect = {}

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
