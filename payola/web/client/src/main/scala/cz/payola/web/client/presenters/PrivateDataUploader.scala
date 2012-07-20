package cz.payola.web.client.presenters

import cz.payola.web.client.View
import s2js.adapters.js.dom.Element
import cz.payola.web.client.views.bootstrap._
import cz.payola.web.shared.AnalysisBuilderData
import s2js.adapters.js.browser._
import scala.Some
import cz.payola.web.client.views.elements._
import scala.Some
import s2js.compiler.javascript
import cz.payola.web.client.views.bootstrap.inputs.TextInputControl

class PrivateDataUploader(val uploaderFormID: String, val redirectURL: String) extends View
{
    val uploaderForm = document.getElementById(uploaderFormID)
    var uploadFile: Boolean = false

    val nameDialog = new Modal("Would you like to upload a file or enter a graph URL?", Nil, Some("Upload File"), Some("Load URL"), false)
    nameDialog.render()
    nameDialog.saving += { e =>
        createUploaderElements()
        true
    }
    nameDialog.closing += { e =>
        createURLLoadingElements()
        true
    }

    val urlField = new TextInputControl("Graph URL:", "graphURL", "", "")
    val fileInput = new Input("graphFile", "", Some("RDF XML file"), "", "file")
    fileInput.setAttribute("accept", "application/rdf+xml, application/xml")
    val submitButtonText = new Text("")
    val submitButton = new Button(submitButtonText)
    submitButton.mouseClicked += { event =>
        if (validateFields){
            submitForm()
        }
        false
    }
    submitButton.render(uploaderForm)

    private def createURLLoadingElements(){
        urlField.render(uploaderForm)
        submitButtonText.text = "Load URL"
    }

    private def createUploaderElements() {
        uploadFile = true
        fileInput.render(uploaderForm)
        submitButtonText.text = "Upload File"
        uploaderForm.setAttribute("enctype", "multipart/form-data")
    }

    @javascript("document.forms['create_form'].submit();")
    private def submitForm(){

    }

    private def validateFields: Boolean = {
        if (uploadFile){
            fileInput.value != ""
        }else{
            urlField.input.value != ""
        }
    }


    def render(parent: Element) = {
        // TODO
    }

    def destroy() {
        // TODO
    }

    def blockDomElement: Element = null // TODO
}
