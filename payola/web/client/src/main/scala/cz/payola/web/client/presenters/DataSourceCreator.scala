package cz.payola.web.client.presenters

import cz.payola.web.client.View
import s2js.adapters.js.browser._
import cz.payola.web.shared.DataSourceManager
import cz.payola.web.client.views.elements
import cz.payola.web.client.views.elements._
import s2js.adapters.js
import s2js.adapters.js.dom._
import cz.payola.domain.entities.Plugin
import cz.payola.domain.entities.plugins.Parameter
import cz.payola.web.client.views.bootstrap.InputControl
import s2js.compiler.javascript

class DataSourceCreator(val dataFetcherDivID: String,
    val optionsDivID: String,
    val submitButtonDivID: String,
    val nameFieldID: String,
    val descriptionFieldID: String,
    val listingURL: String) extends View
{

    // Define internal <select> ID
    val dataFetcherListID = "data_fetcher_list"

    // Get the divs from IDs
    val optionsDiv = document.getElementById(optionsDivID)
    val dataFetcherListWrapper = document.getElementById(dataFetcherDivID)
    val submitButtonDiv = document.getElementById(submitButtonDivID)

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
    val availableDataFetchers = DataSourceManager.getAvailableDataFetchers()
    availableDataFetchers foreach { dataFetcher =>
        new SelectOption(List(new Text(dataFetcher.name))).render(dataFetcherList)
    }

    if (availableDataFetchers.size == 0){
        // No available data fetchers
        window.alert("There are no data fetcher plugins available!")
        redirectToListing()
    }

    // Reload plugin options
    reloadOptions()

    // Create a submit button
    val submitButton = new elements.Button("Create Data Source")
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
        availableDataFetchers.find(_.name == dataFetcherList.value).get
    }

    private def getValueOfFieldWithID(id: String) = document.getElementById(id).getAttribute("value")

    /** Redirects to the data source listing page.
      *
      */
    private def redirectToListing() {
        window.location.href = listingURL
    }

    /** Lists all options for the selected plugin.
      *
      */
    private def reloadOptions() {
        // Remove all old options
        optionsDiv.innerHTML = ""

        val plugin: Plugin = getSelectedPlugin
        plugin.parameters foreach { param: Parameter[_] =>
            // TODO distinguish between string/bool/etc. parameters?
            new InputControl(param.name, param.name, param.defaultValue.toString, "").render(optionsDiv)
        }
    }

    @javascript("document.forms['create_form'].submit();")
    private def submitForm(){

    }

    private def validateInputFields: Boolean = {
        var result = false
        if (getValueOfFieldWithID(nameFieldID) == "") {
            window.alert("Data source name may not be empty!")
        }else if (DataSourceManager.dataSourceExistsWithName(getValueOfFieldWithID(nameFieldID))) {
            window.alert("Data source with this name already exists!")
        }else if (getValueOfFieldWithID(descriptionFieldID) == ""){
            window.alert("Data source description musn't be empty!")
        }else if (!validateOptionsInputFields){
            // leave result false
        }else{
            result = true
        }
        result
    }

    private def validateOptionsInputFields: Boolean = {
        val nodes = optionsDiv.childNodes
        var result = true
        var i = 0
        while (result && i < nodes.length) {
            val inputControl = nodes.item(i).asInstanceOf[InputControl]
            if (inputControl.input.value == ""){
                window.alert("Parameter value cannot be empty for " + inputControl.label + ".")
                result = false
            }
            i = i + 1
        }
        result
    }

    def render(parent: Element) = {
        // TODO
    }

    def destroy() {
        // TODO
    }

}
