<a name="top"></a>
<a name="user"></a>

# Payola!
---
Payola is an HTML5 web application which lets you work with graph data in a completely new way. You can visualize [Linked Data](http://linkeddata.org/) using several preinstalled plugins as graphs, tables, etc. This also means, that you no longer need [Pubby](http://www4.wiwiss.fu-berlin.de/pubby/) to browse through a Linked Data storage (via its [SPARQL](http://www.w3.org/TR/rdf-sparql-query/) endpoint). Moreover, you can create an analysis and run it against a set of SPARQL endpoints without deep knowledge of SPARQL language itself. Analysis results are processed and visualized using the embedded visualization plugins.

![Payola logo (credits Martin Mraz)](https://raw.github.com/siroky/Payola/develop/docs/img/logo.png)

If you **do** know, what Linked Data means, you probably already have an idea, what Payola does. If you **do not**, read more. Let us give you a real-life example. You, as a person, have some friends. Let's suppose, you have a friend named John. Since you are friends, you have a connection. For a computer, one could introduce the following notation:

```
Me | is friend with | John
```

Which, in this example, also means:

```
John | is friend with | Me
```
Actually, this is almost a Linked Data [Turtle](http://www.w3.org/TeamSubmission/turtle/) notation. It describes relations (e.g. friendship) between entities (you and John). What we do is that we analyze those relations and visualize them. As an example of such a visualization, see the following picture, which is a sample visualization of an analysis of relations on social networks for a particular person.

TODO JH - masek data image

For a more complex and sophisticated description of Linked Data, please visit the following [website](http://linkeddata.org/).

Of course, our software is capable of performing analyses on a bit more complicated data. Actually, the first impulse to write such a tool was to come up with a tool which makes the user capable of building analyses which could help him or her to target people involved in corruption.

> And is it?
> 
> In general, yes, it is. But at the time of writing, machine processing of related data is in a conflict with the privacy laws in our country. So nobody is going to build such an analysis.
<!---->
> So that's why the project's name is Payola?
> 
> Yes. We've just inspired ourselves in a dictionary:

> **Payola: the practice of bribing someone in return for the unofficial promotion of a product in the media: if a record company spends enough money on payola, it can make any record a hit**

The main goal is to come up with a prototype of a Linked Data tool for common users. To make them able to work with [RDF](http://www.w3.org/RDF/) data, explore them, analyze them, and to integrate into [OpenData.cz transparent data infrastructure](http://opendata.cz/) which is being developed mostly on the [Faculty of Mathematics and Physics, Charles University](http://www.mff.cuni.cz/). We would also like to make it into the [LOD2](http://lod2.eu/Welcome.html) technology stack, which is, by the way, one of the reasons, why we've chosen the Scala programming language.

Since Payola is rather a platform (or kind of framework, if you want), not a closed project, you can fork the project and write your own plugins, extensions and more on [https://github.com/siroky/Payola](https://github.com/siroky/Payola).

##Why Payola?
During our studies on the University, we've met several tools for working with Linked Data. Many of them are really useful, but it is rather hard to work with them. Especially if you want to process some data and visualise them. You need to install and configure several tools. Morever, those tools work separately, so you need to get an RDF file from each of them and put it into the next one. Some of them are on the web, some of them works under Linux, some of them on Windows.

We wanted to bring a single tool which will be able to do it all. We also wanted to make a web application which is, we think, a new synonymum for platform-independent software. That's also why we take advantage of the new HTML5 standard and avoid using Flash platform.

We also wanted to present a tool which will be capable of sharing the RDF data between its users, visualisations included. Nowadays, if you share an RDF visualisation to somebody, you probably share a screenshot with him. If it is a result of an analysis, it changes over a time, but the screenshot does not. With Payola, you don't share a static visualisation, you share the right to create the visualisation whenever the user want. And he gets the most current results.

###Am I interested in Payola?
If you work with RDF data, yes, you are. 

If you just work with RDF data - we will help to access you data sources, browse through them, analyze the data and visualise them. We've tried to minimize the need to learn the SPARQL language to work with SPARQL data.

If you are a developer, you can also contribute your own analytical plugin and share it with the users of your Payola installation. You can also come up with your own visualisation plugin and compile your own version of Payola.

###What are the most common use cases?
Until now, we have thought and discussed the following use cases:

- private tool to work with RDF data
- company tool to work with internal RDF data
- public website with community around a specific type of RDF data
- company/government website to present RDF data to the public

In companies, Payola will probably have many non-technical users and a developer who will write new plugins and administer the application.

#Related Work
- http://www.visualdataweb.org/relfinder.php
- http://catalogus-professorum.org/graphicalquerybuilder
- http://data.gov.uk/linked-data

## Basic usage

You can use Payola both as a guest and a logged-in user. A guest is limited to analyses and data sources marked as public by other users and only in a read-only mode (i.e. can't edit them).

![Guest Dashboard](https://raw.github.com/siroky/Payola/develop/docs/img/screenshots/guest_dashboard.png)

Follow the `Log In` link in the top-right corner of the page. If you have already signed up, simply fill in your email and password and press Log In. Otherwise, click `Sign Up`.

![Sign In](https://raw.github.com/siroky/Payola/develop/docs/img/screenshots/sign_in.png)

To sign up, fill in your email and password.

![Sign Up](https://raw.github.com/siroky/Payola/develop/docs/img/screenshots/sign_up_credentials.png)

You will be automatically logged in and redirected to your Dashboard.

![Logged In Dashboard](https://raw.github.com/siroky/Payola/develop/docs/img/screenshots/logged_in_dashboard.png)

As a logged-in user, you can now create new data sources, analyses, plugins (you can actually write your own plugin, more about that [later](#plugins)), edit them and share them; and upload your own private RDF data. You can also view your personal page by clicking on your email in the top-right corner next to the log out link.

![User Page](https://raw.github.com/siroky/Payola/develop/docs/img/screenshots/user_page.png)

If you forget your password, you can click on the `Forgot Password` link on the login page. Enter your email and a new password - you will be emailed with a confirmation link. When you click on it, the new password will be put into effect. Note that the confirmation link is valid for two hours only.

![Password Reset](https://raw.github.com/siroky/Payola/develop/docs/img/screenshots/forgot_password.png)

---

### Data Sources

A data source is - as its name hints - a source of data. Payola needs to know where to get its data from for evaluating analyses, etc. - data sources.

##### Creating

Let's start by creating a new data source. In the toolbar, click on the `My Data Sources` button and select `Create New`. You will need to enter a data source name and description and which data fetcher to use.

![Creating a Data Source](https://raw.github.com/siroky/Payola/develop/docs/img/screenshots/create_data_source.png)

A data fetcher is a plugin which can communicate with a data source of a specific type. For example, `SPARQL Endpoint` is a data fetcher. SPARQL is a query language for fetching data and such a data fetcher can work with any SPARQL endpoint.

Select a data fetcher of your choice, fill in the data fetcher's parameters (for example, `EndpointURL` parameter in `SPARQL Endpoint` data fetcher's case) and press the `Create Data Source` button. You have just created your first data source.

##### Editing

Use the toolbar at the top of the page to list available data sources (click on the `My Data Sources` button and select `View All`).

![Listing Data Sources](https://raw.github.com/siroky/Payola/develop/docs/img/screenshots/list_data_sources.png)

You can view all data sources available to you. If you wish to edit one (e.g. change its name or description), click on the Edit button on the same row. You'll be redirected to the edit page which contains a delete button as well, for removing the data source. The sharing functionality will be described in the [Sharing section](#sharing).

The same steps to list and edit apply to any other entity in the system (analyses, plugins, etc.).

##### Viewing

When on the Dashboard, or listing all available data sources, click on a data source to view it.

![Loading Initial Vertex](https://raw.github.com/siroky/Payola/develop/docs/img/screenshots/loading_initial_vertex.png)

You'll be presented with a neighborhood of an initial vertex.

![Initial Vertex](https://raw.github.com/siroky/Payola/develop/docs/img/screenshots/initial_vertex.png)

Such a subgraph can be viewed in many ways. The default one, presented to you, is a simple table.

You can navigate through the graph by following the vertex links in the table. Click on the 'server' icon in front of the link to view the vertex using a different data source.

![Browse Using a Different Data Source](https://raw.github.com/siroky/Payola/develop/docs/img/screenshots/browse_using_data_source.png)

Alternatively, you may use the `SPARQL` button at the top-right to enter your own custom SPARQL query to be evaluated and displayed.

![Entering SPARQL Query](https://raw.github.com/siroky/Payola/develop/docs/img/screenshots/enter_sparql_query.png)

You can, however, change the visualization plugin using the `Change visualization plugin` button. 

`Select Result Table` requires the current graph to be a result of a SELECT SPARQL query, which typically needs to be a custom query using the `SPARQL` button. Otherwise, only a message telling the user that the graph can't be displayed using this visualization plugin is displayed.

![Select Table](https://raw.github.com/siroky/Payola/develop/docs/img/screenshots/select_table_no_result.png)

`Circle`, `Gravity` and `Tree` visualizations will display a regular graph using vertices and edges and differ only in the way they lay out the vertices.

> TODO - OK describe what can be done with a graph

![PNG Download](https://raw.github.com/siroky/Payola/develop/docs/img/screenshots/graph_png_download.png)

The `Column Chart` visualization will display a column bar graph, but works only with graphs of a specific structure. The graph must have one identified vertex, whose incoming edges are of a [`rdf:type`](http://www.w3.org/1999/02/22-rdf-syntax-ns#type) URI - the source of each edge must then have exactly three edges - one going to the aforementioned vertex and then two directed to a literal vertex, one with a string value (name of the column), the second one with a numeric value.

![Column Chart Graph Representation](https://raw.github.com/siroky/Payola/develop/docs/img/screenshots/column_chart_data.png)

![Column Chart](https://raw.github.com/siroky/Payola/develop/docs/img/screenshots/column_chart.png)

##### Ontology Customization

By default, each vertex and edge is of the same color and has the same size (width) when viewed using the graph-based visualizations. To emphasize or diminish some parts of the graph, you can customize the visual appearance using an ontology customization.

While viewing a graph, press the `Change appearance using ontologies` button. If you have already saved some customizations, they are listed here - if you haven't created any yet, select the `Create New` menu item. Enter name of the customization, ontology URL and press `Create`.

![Create Ontology Customization](https://raw.github.com/siroky/Payola/develop/docs/img/screenshots/create_customization.png)

Enter the customization name and URL of that ontology.

![Create Ontology Customization Dialog](https://raw.github.com/siroky/Payola/develop/docs/img/screenshots/create_customization_dialog.png)

You will be presented with a customization dialog. On the left, ontology classes are listed - select one. On the right, properties of that class are listed. At the very top of the right column, you can customize the appearance of the class itself (in the graph displayed as vertices).

![Editing Customization](https://raw.github.com/siroky/Payola/develop/docs/img/screenshots/customization_edit.png)

Below, you can modify appearance of that property (in the graph displayed as edges). You can change color of the vertex or edge:

![Editing Customization - Color](https://raw.github.com/siroky/Payola/develop/docs/img/screenshots/customization_edit_color.png)

Or add a glyph to a vertex:

![Editing Customization - Glyph](https://raw.github.com/siroky/Payola/develop/docs/img/screenshots/customization_edit_glyph.png)

When done, simply press the `Done` button. If you want to further modify the customization, click on the `Edit` button in the `Change appearance using ontologies` button's menu. Now select the ontology using the `Change appearance using ontologies` button.

![Select Ontology Customization](https://raw.github.com/siroky/Payola/develop/docs/img/screenshots/select_customization.png)

Your graph will be redrawn according to the customization selected.

![Ontology Customization Graph](https://raw.github.com/siroky/Payola/develop/docs/img/screenshots/customization_graph.png)

---
### Groups

You can create user groups to make sharing easier (as described in the next chapter). Imagine you want to share a resource (e.g. an analysis) to a group of co-workers. One approach would be to share it to each one of them, but this can be tedious considering you might want to share something to them every week or every day. Hence there's a possibility to create a user group - in the top toolbar, click on the `User Groups` button and select `Create New`.

Enter the group name (e.g. 'My co-workers') and press the `Create Group` button.

![Group Create Dialog](https://raw.github.com/siroky/Payola/develop/docs/img/screenshots/group_create_dialog.png)

After the group has been created, you can start adding members to the group. To do so, make the `Members` field active and start typing - a suggestion box will appear offering users whose name matches the entered text.

![Group Create AutoFill](https://raw.github.com/siroky/Payola/develop/docs/img/screenshots/group_create_autofill.png)

Click on the user to add him or her. If you decide to remove a user, click on the `x` button in front of his or her name. Remember to use the `Save Group` button before leaving the edit page, otherwise all changes made will be lost.

![Group Create - User Added](https://raw.github.com/siroky/Payola/develop/docs/img/screenshots/group_create_user_added.png)

To delete a group, use the `Delete` button at the top-right corner of the edit page, or on the groups listing.

[Group Listing](https://github.com/siroky/Payola/raw/develop/docs/img/screenshots/group_listing.png)

---
### <a name="sharing"></a>Sharing

Now that you know how to create a group, let's share a data source. In the toolbar, click on the `My Data Sources` button and select `View All`. This lists all your data sources. You can use the `Edit` button to edit the data source, the `Private`/`Public` button to toggle whether the data source is private (only you and people you share it to can use it), or public - anyone can use it, even people who are not logged in; or use the delete button to remove the data source.

![Share Button](https://raw.github.com/siroky/Payola/develop/docs/img/screenshots/share_button.png)

Then there's the `Share` button. When you click on it, a menu pops up, allowing you to share the data source either to users or groups. When you select the `To Users` menu item, a dialog is shown with a text field which will suggest users as you type just like when you were adding members to a group. 

The other option is to share the data source to groups - again a dialog will appear, letting you select multiple groups using the suggestion box. Add groups you want and confirm the dialog. All users within the selected groups will be now able to use this data source.

![Share to Groups](https://raw.github.com/siroky/Payola/develop/docs/img/screenshots/share_to_groups.png)

If you no longer want to share a resource with a group or a user, follow the same steps as if you wanted to share it with someone - the dialog which appears will contain the users or groups whom you've shared the resource to previously. Press the `Share` button to confirm the dialog. The list of users and groups allowed to access the resource will be updated accordingly.

---
### Private Data Storage

While listing data sources, you might have noticed a data source called `Private Storage of ...` - when you sign up, a new private data storage is created in your Virtuoso instance. You can add your own data to this storage. Of course, you can share this data source as well.

##### Adding data to data storage

To add data to your private data storage, use toolbar's `Private RDF Storage` button and select `Upload Data`.

Here you are presented with two options: to upload an RDF/XML or TTL file, or load the RDF/XML from a URL. Retrieving a graph in a TTL format from a URL isn't currently supported.

Press the `Choose File` button.

![Select File to Upload](https://raw.github.com/siroky/Payola/develop/docs/img/screenshots/file_to_upload.png)

Press the `Upload File` button. You will be redirected back to the same page after the upload, with information how the upload went.

![Successful Upload](https://raw.github.com/siroky/Payola/develop/docs/img/screenshots/successful_upload.png)
![Upload Failed](https://raw.github.com/siroky/Payola/develop/docs/img/screenshots/upload_failed.png)

---
### Analyses

> TODO The predefined data fetcher plugins are:

> - ```SparqlEndpointFetcher``` which can operate against any public [SPARQL endpoint](http://www.w3.org/wiki/SparqlEndpoints).
> - ```PayolaStorage``` that is used when accessing the users private data.
> - ```OpenDataCleanStorage``` is currently an experimental plugin that communicates with the [Open Data Clean Store](http://sourceforge.net/projects/odcleanstore/) output web service.

Creating a new analysis is similar to creating any other resource - in the toolbar, select `Create New` from `My Analyses` button's menu. You will be prompted to enter a name - enter the analysis' name - you can change it later on.

![Create Analysis Dialog](https://raw.github.com/siroky/Payola/develop/docs/img/screenshots/create_analysis_dialog.png)

You will be presented with a blank page with a control box in the top-left corner. Start by filling in the analysis description.

![Create Analysis Page](https://raw.github.com/siroky/Payola/develop/docs/img/screenshots/create_analysis_page.png)

First, you'll need a data source to start with. You can do so either using the `Add data source` button which will offer you available data sources, or `Add plugin` which lets you add a data fetcher - an anonymous data source. This can be useful if you decide to use a data source that you don't want to save right away (e.g. you know you'll use it just once).


![Create Analysis - Adding Plugin](https://raw.github.com/siroky/Payola/develop/docs/img/screenshots/create_analysis_adding_plugin.png)

![Create Analysis - Filling Plugin Parameters](https://raw.github.com/siroky/Payola/develop/docs/img/screenshots/create_analysis_plugin_parameters.png)

Now that you've added a data source, you need to do something with the data. Click on the `Add Connection` button on your data source box. Payola comes with pre-installed plugins, which are described one by one below. Of course, you can add your own plugin (see [section Plugins](#plugins)). Plugins are ordered in a sequence (though more branches can be created, see below) - a plugin always gets the result of the previous one as its input.

![Create Analysis - Connecting Plugin](https://raw.github.com/siroky/Payola/develop/docs/img/screenshots/create_analysis_connecting_plugin.png)

##### Typed

This plugin selects vertices of a type that's filled in as a parameter `TypeURI` from its input graph.

##### Projection

Projection plugin takes property URIs separated by a newline as a single parameter. It will select vertices that are connected to other vertices using one of the listed URIs. 
> **Note:** Payola performs some optimizations, potentially merging several consecutive plugins together. For example, two consecutive projection plugins are always merged - hence their result isn't an empty graph as one could expect even if each of them lists completely different set of URIs, but a graph that contains both projections (if this optimization hadn't taken place, the first plugin would create a graph containing vertices connected to each other using URIs declared in the first plugin, which would then be filtered using the second plugin, resulting in an empty intersection).

##### Selection

Selection plugin lets you select vertices with a particular attribute - for example select cities with more than 2 million inhabitants.

##### Ontological Filter

Ontological Filter plugin filters a graph using ontologies located at URLs listed in the OntologyURLs parameter.

##### SPARQL Query

This is a more advanced plugin letting you perform your own custom SPARQL query on the output of the previous plugin.

#### Branches

You can add multiple data sources, creating numerous branches that need to be merged before the analysis can be run (trying to run an analysis with branches that aren't merged will yield in an error). Of course, you can have such an incomplete analysis saved to work on it later.

![Create Analysis - Multiple Branches](https://raw.github.com/siroky/Payola/develop/docs/img/screenshots/create_analysis_multiple_branches.png)

Merging branches can be done using the `Merge branches` button. You will be given a choice to use either Join or Union.

![Create Analysis - Merging Branches Dialog](https://raw.github.com/siroky/Payola/develop/docs/img/screenshots/create_analysis_merge_dialog_choose.png)

After selecting one (each is described below), you need to specify which branches to be merged - at the bottom of the dialog, there are wells for each input of the merged plugin.

![Create Analysis - Merging Branches](https://raw.github.com/siroky/Payola/develop/docs/img/screenshots/create_analysis_merge_dialog_initial.png)

At the top of the dialog, you have each branch represented by the name of the last plugin in each branch. If you hover your mouse over the box representing a branch, that particular branch gets highlighted in the background. You need to drag the branch boxes to the input boxes.

![Create Analysis - Merging Branches](https://raw.github.com/siroky/Payola/develop/docs/img/screenshots/create_analysis_merge_final.png)

##### Union

Union simply merges two graphs together as one would expect. Vertices with the same URI will be unified, their properties merged.

##### Join

Join can be either inner or outer (default).

*Inner join:* Only edges from the first graph with URI defined in the `PropertyURI` parameter are included. Also, the origin of the edge must be present in the second graph. Otherwise, the edge is omitted.

> *Example:* You have two graphs:
>> ###### Graph A
>> `payola.cz/dog` - `payola.cz/barks-at` - `payola.cz/tree`<br/>
>>
>> ###### Graph B
>> `payola.cz/wolf` - `payola.cz/evolved-to` - `payola.cz/dog` 
>>
>
> If graph A is joined with graph B using the `payola.cz/barks-at` property, the `dog - barks-at - tree` triple is included in the result (`payola.cz/dog` is a vertex in graph B).
><br/><br/>
> When tried the other way around - joining graph B with graph A using the `payola.cz/evolved-to` property, an empty graph is returned because `payola.cz/wolf` isn't a vertex in graph A.


*Outer join:* All vertices from the first graph that are origins of edges with URI defined in the `PropertyURI` parameter are included. Moreover, if origin of the edge is included in the second graph, destination of the edge and the edge itself are both included in the result as well.

> *Example:* Using the same graphs as before, merging graph A with graph B will yield in the same result. Merging B with A, however, will include a single vertex `payola.cz/wolf` and no edges.


##### Example

Let's create an analysis which selects all cities with more than 2 million inhabitants. First, add a `DBpedia.org` data source.

![Create Analysis - Adding Data Source](https://raw.github.com/siroky/Payola/develop/docs/img/screenshots/create_analysis_adding_data_source.png)

![Create Analysis - Added Data Source](https://raw.github.com/siroky/Payola/develop/docs/img/screenshots/create_analysis_added_data_source.png)

Then connect a new `Typed` plugin with `TypeURI` `http://dbpedia.org/ontology/City`.

![Typed Plugin](https://raw.github.com/siroky/Payola/develop/docs/img/screenshots/plugin_typed.png)

Continue with a `Projection` plugin with `PropertyURIs` `http://dbpedia.org/ontology/populationTotal`.

![Projection Plugin](https://raw.github.com/siroky/Payola/develop/docs/img/screenshots/plugin_projection.png)

And a `Selection` plugin with `PropertyURI` `http://dbpedia.org/ontology/populationTotal`, `Operator` `>` and `Value` `2000000`.

![Selection Plugin](https://raw.github.com/siroky/Payola/develop/docs/img/screenshots/plugin_selection.png)

And that's it: your first analysis.
![Create Analysis - Sample Analysis](https://raw.github.com/siroky/Payola/develop/docs/img/screenshots/create_analysis_sample_analysis.png)

#### Running Analyses

Either on your dashboard, or on analyses listing, click on an analysis to display details of it. You are presented with an overview of the analysis (which plugins with which parameters and bindings are going to be used).

![Analysis Overview](https://raw.github.com/siroky/Payola/develop/docs/img/screenshots/analysis_overview.png)

As some analyses can take a really long time to finish (some may be theoretically infinite), there's a timeout field in the top-right corner as well as a `Stop` button. By default, an analysis times out in 30 seconds. If you find it's too short time to evaluate your analysis, change it to a higher value.

Now press the `Run Analysis` button. The plugin wells will turn yellow.

![Running Analysis](https://raw.github.com/siroky/Payola/develop/docs/img/screenshots/analysis_running.png)

If the analysis succeeds, you will be automatically switched to a result tab - you can browse the resulting graph here just as when browsing a data source.

![Running Analysis](https://raw.github.com/siroky/Payola/develop/docs/img/screenshots/analysis_finished.png)

Moreover, you can also download the result of the analysis as RDF/XML or TTL.

![Downloading Analysis Result](https://raw.github.com/siroky/Payola/develop/docs/img/screenshots/analysis_download.png)

If the evaluation fails, the plugin boxes turn red and an error description is shown when you hover the mouse cursor over them.

![Analysis Failed](https://raw.github.com/siroky/Payola/develop/docs/img/screenshots/analysis_failed.png)

You can then either try to run the analysis again, or to Edit it using the `Edit` button next to the analysis' title.

---
<a name="plugins"></a>
### Plugins

Creating a new plugin requires at least basic programming skills in Scala. Let's start with a basic example of a plugin:

```scala
package my.custom.plugin

import scala.collection._
import cz.payola.domain._
import cz.payola.domain.entities._
import cz.payola.domain.entities.plugins._
import cz.payola.domain.entities.plugins.parameters._
import cz.payola.domain.rdf._

class DelayInSeconds(name: String, inputCount: Int, parameters: immutable.Seq[Parameter[_]], id: String)
	extends Plugin(name, inputCount, parameters, id)
{
	def this() = {
		this("Time Delay in seconds", 1, List(new IntParameter("Delay", 1)), IDGenerator.newId)
	}

	def evaluate(instance: PluginInstance, inputs: IndexedSeq[Option[Graph]], progressReporter: Double => Unit) = {
		usingDefined(instance.getIntParameter("Delay")) { d =>
			(1 to d).foreach { i =>
				Thread.sleep(1000)
				progressReporter(i.toDouble / d)
			}
			inputs(0).getOrElse(Graph.empty)
		}
	}
}
```

A plugin must always have two constructors declared:

- The default constructor (```DelayInSeconds(name: String, inputCount: Int, parameters: immutable.Seq[Parameter[_]], id: String)```) is required to have these parameters of exactly these types. It should just pass those parameters to the super class constructor (```Plugin(name, inputCount, parameters, id)```).
- The parameterless constructor (```def this()```) is used to instantiate the plugin for the first time, so this is the place where you can set the plugin name, count of its inputs, and the parameters. A new ID of the plugin should be obtained using the ```IDGenerator.newId``` method.

> If you wonder why such constraints are used, note that there is only one instance of each plugin class living in the application in every moment. In fact, there may be more than one instance of the plugin class, however all these instances are identical, so they have same IDs and parameters with same IDs. The parameterless constructor is therefore used to create the first instance of the plugin class. The instance is consecutively persisted into the database and whenever it's is accessed by that moment, it's instantiated using the default constructor with the values retrieved from the database.

The second constrait on the plugin is that it must implement the abstract method ```evaluate(instance: PluginInstance, inputs: IndexedSeq[Option[Graph]], progressReporter: Double => Unit)```. The `instance` parameter contains all parameter values, `inputs` is a sequence of `Option[Graph]`'s - in this case just one as defined in `this()`. You can optionally report progress using the `progressReporter` function passed, which reports the progress to the user (0.0 < progress <= 1.0). Refer to the API documentation to explore which methods you can call on the `instance` or within the plugin class scope (e.g. helper methods like `usingDefined`).

![Plugin Source](https://raw.github.com/siroky/Payola/develop/docs/img/screenshots/plugin_source.png)

Once you post the plugin source code, it gets compiled to check for syntax errors and that the code is indeed a Plugin subclass.

![Plugin Compiling](https://raw.github.com/siroky/Payola/develop/docs/img/screenshots/plugin_compiling.png)

After that an email is sent to the admin to review the plugin source code for security reasons. After he reviews it, you will receive an email with the admin's decision.

![Plugin Compiling](https://raw.github.com/siroky/Payola/develop/docs/img/screenshots/plugin_submitted.png)

#### Other plugin types

You're not required to extend directly the `Plugin` class, you may also extend the `cz.payola.domain.entities.plugins.concrete.DataFetcher` or `cz.payola.domain.entities.plugins.concrete.SparqlQuery`. The former one in case you want to create a data fetcher which may be used as a data source plugin. The second one in case the plugin evaluation function can be expressed as an application of SPARQL query on the input graph. Both have the method `evaluate` already implemented, but they introduce other abstract methods that have to be implemeted. You can get the best insight on how they work from the following examples or from the sources of predefined `DataFetcher`s or `SparqlQuery`s.

```scala
package my.custom.plugin

import scala.collection._
import cz.payola.domain._
import cz.payola.domain.entities._
import cz.payola.domain.entities.plugins._
import cz.payola.domain.entities.plugins.concrete._
import cz.payola.domain.entities.plugins.parameters._
import cz.payola.domain.rdf._

class BlackHole(name: String, inputCount: Int, parameters: immutable.Seq[Parameter[_]], id: String)
	extends DataFetcher(name, inputCount, parameters, id)
{
	def this() = {
		this("Black Hole", 0, Nil, IDGenerator.newId)
	}

	def executeQuery(instance: PluginInstance, query: String): Graph = {
        Graph.empty
    }
}
```

```scala
package my.custom.plugin

import scala.collection._
import cz.payola.domain._
import cz.payola.domain.entities._
import cz.payola.domain.entities.plugins._
import cz.payola.domain.entities.plugins.concrete._
import cz.payola.domain.entities.plugins.parameters._
import cz.payola.domain.rdf._

class TopTriples(name: String, inputCount: Int, parameters: immutable.Seq[Parameter[_]], id: String)
	extends SparqlQuery(name, inputCount, parameters, id)
{
	def this() = {
		this("Top Triples", 1, List(new IntParameter("Count", 30)), IDGenerator.newId)
	}

	def getQuery(instance: PluginInstance): String = {
		val limit = instance.getIntParameter("Count").map(l => math.min(math.max(0, l), 1000)).getOrElse(30)
		"CONSTRUCT { ?x ?y ?z } WHERE { ?x ?y ?z } LIMIT %s".format(limit)
	}
}
```
