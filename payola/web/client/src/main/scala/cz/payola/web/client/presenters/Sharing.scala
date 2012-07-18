package cz.payola.web.client.presenters

import s2js.adapters.js.browser.document
import cz.payola.web.client.presenters.components.ShareButton
import s2js.adapters.js.dom.Element
import s2js.compiler.javascript
import cz.payola.web.shared.SharingData
import cz.payola.web.client.views.elements._

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

    }

    def shareToUserHandler(id: String) {

    }
}
