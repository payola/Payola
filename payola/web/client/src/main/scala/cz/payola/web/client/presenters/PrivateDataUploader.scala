package cz.payola.web.client.presenters

import s2js.adapters.browser._
import cz.payola.web.client._
import cz.payola.web.client.views.bootstrap._
import cz.payola.web.client.views.elements._
import s2js.compiler.javascript
import cz.payola.web.client.views.bootstrap.modals.AlertModal
import cz.payola.web.client.views.elements.form.fields._

class PrivateDataUploader(fileUploaderDivID: String,
                                        urlUploaderDivID: String,
                                        redirectURL: String) extends Presenter
{
    val fileUploadDiv = document.getElementById(fileUploaderDivID)
    val fileInput = new InputControl("File: ", new FileInput("graphFile", "", "span10"), Some("span2"))
    val fileUploadButton = new Button(new Text(" Upload File"), "btn-primary input", new Icon(Icon.upload, true))

    fileInput.field.setAttribute("accept", "application/rdf+xml, application/xml,text/turtle")
    fileInput.render(fileUploadDiv)

    fileUploadButton.mouseClicked += { event =>
        if (fileInput.field.value == ""){
            AlertModal.display("You must choose a file first.", "")
        }else{
            submitForm("file-uploader-form")
        }
        false
    }
    fileUploadButton.render(fileUploadDiv)

    val urlUploadDiv = document.getElementById(urlUploaderDivID)
    val urlField = new InputControl("Graph URL:", new TextInput("graphURL", "", "", "span10"), Some("span2"))
    val urlUploadButton = new Button(new Span(List(new Icon(Icon.upload, true), new Text(" Upload from URL"))), "btn-primary input")
    urlField.render(urlUploadDiv)

    urlUploadButton.mouseClicked += { event =>
        if (urlField.field.value == ""){
            AlertModal.display("URL field mustn't be empty.", "")
        }else{
            submitForm("url-uploader-form")
        }
        false
    }
    urlUploadButton.render(urlUploadDiv)

    @javascript("document.forms[formName].submit();")
    private def submitForm(formName: String){

    }

    def initialize() {

    }
}
