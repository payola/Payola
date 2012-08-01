package cz.payola.web.client.presenters

import cz.payola.web.client._
import s2js.adapters.js.browser._
import cz.payola.web.shared.managers.DataSourceManager
import cz.payola.web.client.views.elements._
import s2js.adapters.js
import cz.payola.common.entities.Plugin
import cz.payola.common.entities.plugins.Parameter
import cz.payola.web.client.views.bootstrap.inputs.TextInputControl
import s2js.compiler.javascript
import s2js.adapters.js.dom.Element
import cz.payola.web.client.models.Model
import cz.payola.web.client.views.bootstrap.modals.AlertModal
import s2js.runtime.shared.rpc.RpcException

class DataSourceCreator(val dataFetcherDivID: String,
    val optionsDivID: String,
    val submitButtonDivID: String,
    val nameFieldDivID: String,
    val descriptionFieldDivID: String,
    val listingURL: String) extends Presenter
{

    // Define internal <select> ID
    val dataFetcherListID = "data_fetcher_list"

    // Get the divs from IDs
    val optionsDiv = document.getElementById(optionsDivID)
    val dataFetcherListWrapper = document.getElementById(dataFetcherDivID)
    val submitButtonDiv = document.getElementById(submitButtonDivID)
    val nameFieldDiv = document.getElementById(nameFieldDivID)
    val descriptionFieldDiv = document.getElementById(descriptionFieldDivID)

    // Create name & description fields
    val nameField = new TextInputControl("Data source name:", "__dataSourceName__", "My data source", "")
    nameField.render(nameFieldDiv)
    val descriptionField = new TextInputControl("Description:", "__dataSourceDescription__", "", "")
    descriptionField.render(descriptionFieldDiv)

    // Create a data fetcher list
    val dataFetcherList: js.dom.Input = document.createElement[js.dom.Input]("select")
    dataFetcherList.setAttribute("id", dataFetcherListID)
    dataFetcherList.setAttribute("name", "__dataSourceFetcherType__")
    dataFetcherListWrapper.appendChild(dataFetcherList)

    // Add a onchange event
    dataFetcherList.onchange = { event =>
        reloadOptions()
        true
    }

    // Load data fetchers
    blockPage("Loading accessible data fetchers...")
    private var accessibleDataFetchers: Seq[Plugin] = null
    Model.accessibleDataFetchers { dataFetchers =>
        accessibleDataFetchers = dataFetchers
        dataFetchers.foreach { d =>
            val option = new SelectOption(List(new Text(d.name)))
            option.render(dataFetcherList)
        }
        // Reload plugin options
        reloadOptions()
        unblockPage()
    } { e =>
        unblockPage()
        e match {
            case exc: RpcException => {
                val alert = new AlertModal("Error loading accessible data fetchers", exc.message)
                alert.confirming += { args =>
                    window.location.href = listingURL
                    true
                }
                alert.render()
            }
            case _ => fatalErrorHandler(e)
        }
    }

    // Create a submit button
    val submitButton = new Button(new Text("Create Data Source"), "btn-primary")
    submitButton.mouseClicked += { event =>
        if (validateInputFields) {
            submitForm()
            true
        }else{
            false
        }
    }
    submitButton.render(submitButtonDiv)


    /** Returns selected plugin according to selected name.
      *
      * @return Selected plugin.
      */
    private def getSelectedPlugin: Plugin = {
        if (accessibleDataFetchers == null){
            null
        }else{
            accessibleDataFetchers.find(_.name == dataFetcherList.value).get
        }
    }

    /** Redirects to the data source listing page.
      *
      */
    private def redirectToListing() {
        window.location.href = listingURL
    }

    def initialize() {

    }

    /** Lists all options for the selected plugin.
      *
      */
    private def reloadOptions() {
        // Remove all old options
        optionsDiv.innerHTML = ""

        val plugin: Plugin = getSelectedPlugin
        plugin.parameters foreach { param: Parameter[_] =>
            val inputControl = new TextInputControl(param.name, param.name, param.defaultValue.toString, "")
            inputControl.render(optionsDiv)
        }
    }

    /** Submit a form using JS.
      *
      */
    @javascript("document.forms['create_form'].submit();")
    private def submitForm(){

    }

    /** Validates input fields. Goes through the name field, description,
      * makes sure that a data source with this name doesn't exist yet.
      *
      * @return True when all fields are valid.
      */
    private def validateInputFields: Boolean = {
        var result = false
        if (nameField.input.value == "") {
            AlertModal.display("Validation failed", "Data source name may not be empty!")
        }else if (DataSourceManager.dataSourceExistsWithName(nameField.input.value)) {
            AlertModal.display("Validation failed", "Data source with this name already exists!")
        }else if (descriptionField.input.value == ""){
            AlertModal.display("Validation failed", "Data source description musn't be empty!")
        }else{
            result = true
        }
        result
    }

    def render(parent: Element) = {
        // TODO
    }

    def destroy() {
        // TODO
    }

    def blockDomElement: Element = null // TODO

}
