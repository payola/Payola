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
import cz.payola.web.client.views.bootstrap.inputs._
import scala.Some

class PrivateDataUploader(fileUploaderDivID: String,
                                        fileUploaderFormID: String,
                                        urlUploaderDivID: String,
                                        redirectURL: String) extends View
{
    val fileUploadDiv = document.getElementById(fileUploaderDivID)
    val fileUploadForm = document.getElementById(fileUploaderFormID)

    val fileInput = new FileInputControl("File: ", "graphFile", "")
    fileInput.input.setAttribute("accept", "application/rdf+xml, application/xml,text/turtle")
    fileInput.render(fileUploadForm)
    val fileUploadButton = new Button(new Span(List(new Icon(Icon.upload, true), new Text(" Upload File"))), cssClass = "btn-primary")
    fileUploadButton.mouseClicked += { event =>
//        if (validateFields){
//            submitForm()
//        }
        false
    }
    fileUploadButton.render(fileUploadForm)

    val urlUploadDiv = document.getElementById(urlUploaderDivID)
    val urlField = new TextInputControl("Graph URL:", "graphURL", "", "")
    val urlUploadButton = new Button(new Span(List(new Icon(Icon.upload, true), new Text(" Upload from URL"))), cssClass = "btn-primary")
    urlField.render(urlUploadDiv)
    urlUploadButton.render(urlUploadDiv)

    @javascript("document.forms['create_form'].submit();")
    private def submitForm(){

    }


    def render(parent: Element) = {
        // TODO
    }

    def destroy() {
        // TODO
    }

    def blockDomElement: Element = null // TODO
}
