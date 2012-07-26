package cz.payola.web.client.presenters

import cz.payola.web.client._
import s2js.adapters.js.dom.Element
import cz.payola.web.client.views.bootstrap._
import cz.payola.web.shared.AnalysisBuilderData
import s2js.adapters.js.browser._
import scala.Some
import cz.payola.web.client.views.elements._
import scala.Some
import s2js.compiler.javascript
import cz.payola.web.client.views.bootstrap.inputs._
import scala.Some
import cz.payola.web.client.views.bootstrap.modals.AlertModal

class PrivateDataUploader(fileUploaderDivID: String,
                                        urlUploaderDivID: String,
                                        redirectURL: String) extends Presenter
{
    val fileUploadDiv = document.getElementById(fileUploaderDivID)
    val fileInput = new FileInputControl("File: ", "graphFile", "", "span10")
    val fileUploadButton = new Button(new Span(List(new Icon(Icon.upload, true), new Text(" Upload File"))), cssClass = "btn-primary span2")

    fileInput.input.setAttribute("accept", "application/rdf+xml, application/xml,text/turtle")
    fileInput.render(fileUploadDiv)

    fileUploadButton.mouseClicked += { event =>
        if (fileInput.input.value == ""){
            AlertModal.display("You must choose a file first.")
        }else{
            submitForm("file-uploader-form")
        }
        false
    }
    fileUploadButton.render(fileUploadDiv)

    val urlUploadDiv = document.getElementById(urlUploaderDivID)
    val urlField = new TextInputControl("Graph URL:", "graphURL", "", "", "span10")
    val urlUploadButton = new Button(new Span(List(new Icon(Icon.upload, true), new Text(" Upload from URL"))), "btn-primary span2")
    urlField.render(urlUploadDiv)

    urlUploadButton.mouseClicked += { event =>
        if (urlField.input.value == ""){
            AlertModal.display("URL field mustn't be empty.")
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
