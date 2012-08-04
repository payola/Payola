package cz.payola.web.client.views.entity.analysis

import cz.payola.web.client.views.bootstrap.inputs._
import cz.payola.web.client.views.elements._
import cz.payola.web.client.views.bootstrap.Icon
import cz.payola.web.client.views.ComposedView
import cz.payola.common.entities.Analysis

class AnalysisEditorView(analysis: Analysis) extends ComposedView
{
    val nameControl = new TextInputControl("Analysis name:", "name", "", "Analysis name")

    val description = new TextAreaInputControl("Description:", "description", "", "Anaylsis description")

    protected val properties = new Div(List(nameControl, description))

    val addPluginLink = new Anchor(List(new Icon(Icon.hdd), new Text(" Add plugin")))

    protected val addPluginLinkLi = new ListItem(List(addPluginLink))

    val addDataSourceLink = new Anchor(List(new Icon(Icon.hdd), new Text(" Add data source")))

    protected val addDataSourceLinkLi = new ListItem(List(addDataSourceLink))

    val mergeBranches = new Anchor(List(new Icon(Icon.glass), new Text(" Merge branches")))

    protected val mergeBranchesLi = new ListItem(List(mergeBranches))

    protected val menu = new UnorderedList(List(addPluginLinkLi, addDataSourceLinkLi, mergeBranchesLi))

    val visualiser = new EditableAnalysisVisualizer(analysis)

    protected val leftColContent = new Div(List(menu, properties), "well")

    val analysisCanvas = new Div(List(visualiser), "plugin-space")

    protected val leftCol = new Div(List(leftColContent), "span3")

    protected val rightCol = new Div(List(analysisCanvas), "span9")

    protected val container = new Div(List(leftCol, rightCol))

    nameControl.input.addCssClass("span12")
    description.input.addCssClass("span12")

    def setName(name: String) {
        nameControl.input.value_=(name)
    }

    def createSubViews = List(container)
}
