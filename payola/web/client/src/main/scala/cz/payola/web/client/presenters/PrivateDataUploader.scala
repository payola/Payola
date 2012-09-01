package cz.payola.web.client.presenters

import cz.payola.web.client._
import s2js.compiler.javascript
import s2js.adapters.browser._
import cz.payola.web.client.views.bootstrap.modals.AlertModal
import cz.payola.web.client.views.entity.PrivateDataUploaderView

class PrivateDataUploader(fileUploaderDivID: String,
    urlUploaderDivID: String,
    redirectURL: String) extends Presenter
{
    @javascript("document.forms[formName].submit();")
    private def submitForm(formName: String) {
    }

    def initialize() {
        val view = new PrivateDataUploaderView(fileUploaderDivID, urlUploaderDivID)
        view.render(document.body)

        view.fileUploadButton.mouseClicked += { event =>
            if (view.fileInput.field.value == "") {
                AlertModal.display("You must choose a file first.", "")
            } else {
                submitForm("file-uploader-form")
            }
            false
        }
        view.urlUploadButton.mouseClicked += { event =>
            if (view.urlField.field.value == "") {
                AlertModal.display("URL field mustn't be empty.", "")
            } else {
                submitForm("url-uploader-form")
            }
            false
        }
    }
}
