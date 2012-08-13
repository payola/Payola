package cz.payola.web.client.views.entity.analysis

import cz.payola.web.client.views.elements._
import cz.payola.web.client.views.bootstrap._
import cz.payola.web.client.views.ComposedView
import cz.payola.common.entities.Analysis
import cz.payola.web.client.views.elements.lists._
import cz.payola.web.client.views.elements.form.fields._

class AnalysisEditorView(analysis: Analysis, newName: Option[String], newDesc: Option[String]) extends ComposedView
{
    val name = new InputControl("Analysis name:", new TextInput("name", if(newName.isDefined){newName.get}else{analysis.name}, "Analysis name"))

    val description = new InputControl("Description:", new TextArea("description",  if(newDesc.isDefined){newDesc.get}else{analysis.description}, "Anaylsis description"))

    protected val properties = new Div(List(name, description))

    val addPluginLink = new Anchor(List(new Icon(Icon.hdd), new Text(" Add plugin")))

    protected val addPluginLinkLi = new ListItem(List(addPluginLink))

    val addDataSourceLink = new Anchor(List(new Icon(Icon.hdd), new Text(" Add data source")))

    protected val addDataSourceLinkLi = new ListItem(List(addDataSourceLink))

    val mergeBranches = new Anchor(List(new Icon(Icon.glass), new Text(" Merge branches")))

    protected val mergeBranchesLi = new ListItem(List(mergeBranches))

    protected val menu = new UnorderedList(List(addPluginLinkLi, addDataSourceLinkLi, mergeBranchesLi))

    val visualizer = new EditableAnalysisVisualizer(analysis)

    protected val leftColContent = new Div(List(menu, properties), "well")

    val analysisCanvas = new Div(List(visualizer), "plugin-space")

    protected val leftCol = new Div(List(leftColContent), "span3")

    protected val rightCol = new Div(List(analysisCanvas), "span9 relative")

    protected val container = new Div(List(leftCol, rightCol))

    name.field.addCssClass("span12")
    description.field.addCssClass("span12")

    def setName(newValue: String) {
        name.field.value = newValue
    }

    def createSubViews = List(container)
}
