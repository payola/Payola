package cz.payola.web.client.views.entity.analysis

import cz.payola.web.client.views.elements._
import cz.payola.web.client.views.bootstrap._
import cz.payola.web.client.views.ComposedView
import cz.payola.common.entities.Analysis
import cz.payola.web.client.views.elements.lists._
import cz.payola.web.client.views.elements.form.fields._
import s2js.adapters.browser.`package`._
import scala.Some
import cz.payola.web.client.models.PrefixApplier

class AnalysisEditorView(analysis: Analysis, newName: Option[String], newDesc: Option[String], pageTitle: String, prefixApplier: PrefixApplier) extends ComposedView
{
    val name = new InputControl("Analysis name:", new TextInput("name", if(newName.isDefined){newName.get}else{analysis.name}, "Analysis name"), Some("nofloat"), None)

    val description = new InputControl("Description:", new TextArea("description",  if(newDesc.isDefined){newDesc.get}else{analysis.description}, "Anaylsis description"), Some("nofloat"), None)

    protected val properties = new Div(List(name, description))

    val addPluginLink = new Anchor(List(new Icon(Icon.hdd), new Text(" Add plugin")))

    protected val addPluginLinkLi = new ListItem(List(addPluginLink),"list-group-item")

    val addDataCubeLink = new Anchor(List(new Icon(Icon.hdd), new Text(" Add DataCube plugin")))

    protected val addDataCubeLinkLi = new ListItem(List(addDataCubeLink),"list-group-item")

    val addDataSourceLink = new Anchor(List(new Icon(Icon.hdd), new Text(" Add data source")))

    protected val addDataSourceLinkLi = new ListItem(List(addDataSourceLink),"list-group-item")

    val mergeBranches = new Anchor(List(new Icon(Icon.glass), new Text(" Merge branches")))

    protected val mergeBranchesLi = new ListItem(List(mergeBranches),"list-group-item")

    protected val menu = new UnorderedList(List(addPluginLinkLi, addDataSourceLinkLi, mergeBranchesLi),"list-group")

    val visualizer = new EditableAnalysisVisualizer(analysis, prefixApplier)

    protected val panelBody = new Div(List(menu, properties), "panel-body")
    protected val leftColContent = new Div(List(panelBody), "panel panel-default")

    val analysisCanvas = new Div(List(visualizer), "plugin-space")

    protected val leftCol = new Div(List(leftColContent), "col-lg-3")

    protected val rightCol = new Div(List(analysisCanvas), "col-lg-9 relative")

    val h1 = new Heading(List(new Text(pageTitle)),1,"col-lg-10")
    val runButton = new Button(new Text("Run"), "col-lg-1", new Icon(Icon.play))

    val mainHeader = new Div(List(h1, runButton),"main-header row")
    val row = new Div(List(leftCol, rightCol))
    protected val container = new Div(List(mainHeader, row))

    name.field.addCssClass("col-lg-12")
    description.field.addCssClass("col-lg-12")

    def setName(newValue: String) {
        name.field.value = newValue
    }

    def createSubViews = List(container)
}
