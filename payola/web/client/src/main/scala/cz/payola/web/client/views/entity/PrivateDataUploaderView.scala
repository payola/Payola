package cz.payola.web.client.views.entity

import cz.payola.web.client.views.ComposedView
import cz.payola.web.client.views.bootstrap._
import cz.payola.web.client.views.elements.form.fields._
import cz.payola.web.client.views.elements._
import s2js.adapters.browser._
import scala.Some
import s2js.adapters.html

class PrivateDataUploaderView(fileUploaderDivID: String,
    urlUploaderDivID: String) extends ComposedView
{
    val fileUploadDiv = document.getElementById(fileUploaderDivID)
    val fileInput = new InputControl("File: ", new FileInput("graphFile", "", "col-lg-10"), Some("col-lg-10"), Some("col-lg-2"))
    val fileUploadButton = new Button(new Text(" Upload File"), "btn-primary input", new Icon(Icon.upload, true))

    val urlUploadDiv = document.getElementById(urlUploaderDivID)
    val urlField = new InputControl("Graph URL:", new TextInput("graphURL", "", "", "col-lg-10"), Some("col-lg-2"), Some("col-lg-10"))
    val urlUploadButton = new Button(new Span(List(new Icon(Icon.upload, true), new Text(" Upload from URL"))), "btn-primary input")

    def createSubViews = {
        List()
    }

    override def render(parent: html.Element) {
        fileInput.render(fileUploadDiv)
        fileUploadButton.render(fileUploadDiv)
        urlField.render(urlUploadDiv)
        urlUploadButton.render(urlUploadDiv)
    }
}
