package cz.payola.web.client.views.entity.analysis

import cz.payola.web.client.views.bootstrap.inputs._
import cz.payola.web.client.views.elements._
import cz.payola.web.client.views.bootstrap.Icon
import cz.payola.web.client.views.todo.PluginInstance
import cz.payola.web.client.View
import s2js.adapters.js.browser.document
import s2js.adapters.js.dom.Element
import cz.payola.web.client.views.ComposedView

class AnalysisEditorView extends ComposedView
{
    val nameControl = new TextInputControl("Analysis name:", "name", "", "Analysis name")
    val description = new TextAreaInputControl("Description:", "description", "", "Anaylsis description")
    protected val properties = new Div(List(nameControl, description))

    val addPluginLink = new Anchor(List(new Icon(Icon.hdd), new Text(" Add plugin")))
    protected val addPluginLinkLi = new ListItem(List(addPluginLink))
    val addDataSourceLink = new Anchor(List(new Icon(Icon.hdd), new Text(" Add datasource")))
    protected val addDataSourceLinkLi = new ListItem(List(addDataSourceLink))
    val mergeBranches = new Anchor(List(new Icon(Icon.glass), new Text(" Merge branches")))
    protected val mergeBranchesLi = new ListItem(List(mergeBranches))
    protected val menu = new UnorderedList(List(addPluginLinkLi, addDataSourceLinkLi, mergeBranchesLi))

    protected val leftColContent = new Div(List(menu,properties),"well")
    protected val rightColContent = new Div(List(),"plugin-space")

    protected val leftCol = new Div(List(leftColContent),"span3")
    protected val rightCol = new Div(List(rightColContent),"span9")

    protected val container = new Div(List(leftCol, rightCol))
    nameControl.input.addCssClass("span12")
    description.input.addCssClass("span12")

    def setName(name: String){
        nameControl.input.value_=(name)
    }

    def renderInstance(instance: PluginInstance){
        instance.render(rightColContent.domElement)
    }

    def createSubViews = List(container)
}
