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

![Guest Dashboard](https://raw.github.com/siroky/Payola/develop/docs/img/guest-dashboard.png)

A logged-in user can create new data sources, analyses, plugins (you can actually write your own plugin, more about that [later](#plugins)), edit them and share them; and upload your own private RDF data.

---

### Data Sources

A data source is - as its name hints - a source of data. Payola needs to know where to get its data from for evaluating analyses, etc. - data sources.

##### Creating

Let's start by creating a new data source. In the toolbar, click on the `My Data Sources` button and select `Create New`. You will need to enter a data source name and description, decide whether it's public (then it's visible even to guest users) and which data fetcher to use.

A data fetcher is a plugin which can communicate with a data source of a specific type. For example, `SPARQL Endpoint` is a data fetcher. SPARQL is a query language for fetching data and such a data fetcher can work with any SPARQL endpoint.

Select a data fetcher of your choice, fill in the data fetcher's parameters (for example, `EndpointURL` parameter in `SPARQL Endpoint` data fetcher's case) and press the `Create Data Source` button. You have just created your first data source.

##### Editing

Use the toolbar at the top of the page to list available data sources (click on the `My Data Sources` button and select `View All`).

You can view all data sources available to you. If you wish to edit one (e.g. change its name or description), click on the Edit button on the same row. You'll be redirected to the edit page which contains a delete button as well, for removing the data source. The sharing functionality will be described in the [Sharing section](#sharing).

The same steps to list and edit apply to any other entity in the system (analyses, plugins, etc.).

##### Viewing

When on the Dashboard, or listing all available data sources, click on a data source to view it.

You'll be presented with a neighborhood of an initial vertex.

Such a subgraph can be viewed in many ways. The default one, presented to you, is a simple table. You can change the visualization plugin using the `Change visualization plugin` button. `Circle`, `Gravity` and `Tree` visualizations will display a regular graph using vertices and edges and differ only in the way they lay out the vertices. 

> TODO - OK describe what can be done with a graph

The `Column Chart` visualization will display a column bar graph, but works only with graphs of a specific structure. The graph must have one identified vertex, whose incoming edges are of a [`rdf:type`](http://www.w3.org/1999/02/22-rdf-syntax-ns#type) URI - the source of each edge must then have exactly three edges - one going to the aforementioned vertex and then two directed to a literal vertex, one with a string value (name of the column), the second one with a numeric value.

![Graph representation](https://raw.github.com/siroky/Payola/develop/docs/img/column_chart_data.png)

##### Ontology Customization

By default, each vertex and edge is of the same color and has the same size (width) when viewed using the graph-based visualizations. To emphasize or diminish some parts of the graph, you can customize the visual appearance using an ontology customization.

While viewing a graph, press the `Change appearance using ontologies` button. If you have already saved some customizations, they are listed here - if you haven't created any yet, select the `Create New` menu item. Enter name of the customization, ontology URL and press `Create`.

You will be presented with a customization dialog. On the left, ontology classes are listed - select one. On the right, properties of that class are listed. At the very top of the right column, you can customize the appearance of the class itself (in the graph displayed as vertices), below, you can modify appearance of that property (in the graph displayed as edges).

When done, simply press the `Done` button. If you want to further modify the customization, click on the `Edit` button in the `Change appearance using ontologies` button's menu.

---
### Groups

You can create user groups to make sharing easier (as described in the next chapter). Imagine you want to share a resource (e.g. an analysis) to a group of co-workers. One approach would be to share it to each one of them, but this can be tedious considering you might want to share something to them every week or every day. Hence there's a possibility to create a user group - in the top toolbar, click on the `User Groups` button and select `Create New`.

Enter the group name (e.g. 'My co-workers') and press the `Create Group` button. After the group has been created, you can start adding members to the group. To do so, make the `Members` field active and start typing - a suggestion box will appear offering users whose name matches the entered text. Click on the user to add him or her. If you decide to remove a user, click on the `x` button in front of his or her name. Remember to use the `Save Group` button before leaving the edit page, otherwise all changes made will be lost.

To delete a group, use the `Delete` button at the top-right corner of the edit page, or on the groups listing.


[Editing a Group](https://github.com/siroky/Payola/raw/develop/docs/img/group_edit.png)

---
### <a name="sharing"></a>Sharing

Now that you know how to create a group, let's share a data source. In the toolbar, click on the `My Data Sources` button and select `View All`. This lists all your data sources. You can use the `Edit` button to edit the data source, the `Private`/`Public` button to toggle whether the data source is private (only you and people you share it to can use it), or public - anyone can use it, even people who are not logged in; or use the delete button to remove the data source.

Then there's the `Share` button. When you click on it, a menu pops up, allowing you to share the data source either to users or groups. When you select the `To Users` menu item, a dialog is shown with a text field which will suggest users as you type just like when you were adding members to a group. 

The other option is to share the data source to groups - again a dialog will appear, letting you select multiple groups using the suggestion box. Add groups you want and confirm the dialog. All users within the selected groups will be now able to use this data source.

If you no longer want to share a resource with a group or a user, follow the same steps as if you wanted to share it with someone - the dialog which appears will contain the users or groups whom you've shared the resource to previously. Press the `Share` button to confirm the dialog. The list of users and groups allowed to access the resource will be updated accordingly.

---
### Private Data Storage

While listing data sources, you might have noticed a data source called `Private Storage of ...` - when you sign up, a new private data storage is created in your Virtuoso instance. You can add your own data to this storage. Of course, you can share this data source as well.

##### Adding data to data storage

To add data to your private data storage, use toolbar's `Private RDF Storage` button and select `Upload Data`.

Here you are presented with two options: to upload an RDF/XML or TTL file, or load the RDF/XML from a URL. Retrieving a graph in a TTL format from a URL isn't currently supported.

---
### Analyses

> TODO The predefined data fetcher plugins are:

> - ```SparqlEndpointFetcher``` which can operate against any public [SPARQL endpoint](http://www.w3.org/wiki/SparqlEndpoints).
> - ```PayolaStorage``` that is used when accessing the users private data.
> - ```OpenDataCleanStorage``` is currently an experimental plugin that communicates with the [Open Data Clean Store](http://sourceforge.net/projects/odcleanstore/) output web service.

Creating a new analysis is similar to creating any other resource - in the toolbar, select `Create New` from `My Analyses` button's menu. You will be prompted to enter a name - enter the analysis' name - you can change it later on.

You will be presented with a blank page with a control box in the top-left corner. Start by filling in the analysis description.

First, you'll need a data source to start with. You can do so either using the `Add data source` button which will offer you available data sources, or `Add plugin` which lets you add a data fetcher - an anonymous data source. This can be useful if you decide to use a data source that you don't want to save right away (e.g. you know you'll use it just once).

Now that you've added a data source, you need to do something with the data. Click on the `Add Connection` button on your data source box. Payola comes with pre-installed plugins, which are described one by one below. Of course, you can add your own plugin (see [section Plugins](#plugins)). Plugins are ordered in a sequence (though more branches can be created, see below) - a plugin always gets the result of the previous one as its input.

##### Typed

This plugin selects vertices of a type that's filled in as a parameter `TypeURI` from its input graph.

##### Projection

Projection plugin takes property URIs separated by a newline as a single parameter. It will select vertices that are connected to other vertices using one of the listed URIs. 
> **Note:** Payola performs some optimizations, potentially merging several consecutive plugins together. For example, two consecutive projection plugins are always merged - hence their result isn't an empty graph as one could expect even if each of them lists completely different set of URIs, but a graph that contains both projections (if this optimization hadn't taken place, the first plugin would create a graph containing vertices connected to each other using URIs declared in the first plugin, which would then be filtered using the second plugin, resulting in an empty intersection).

##### Selection

Selection plugin lets you select vertices with a particular attribute - for example select cities with more than 2 million inhabitants.

> *Example:* Let's create an analysis which selects all cities with more than 2 million inhabitants. First, add a `DBpedia.org` data source, then connect a new `Typed` plugin with `TypeURI` `http://dbpedia.org/ontology/City`. Continue with a `Projection` plugin with `PropertyURIs` `http://dbpedia.org/ontology/populationTotal`, then a `Selection` plugin with `PropertyURI` `http://dbpedia.org/ontology/populationTotal`, `Operator` `>` and `Value` `2000000`. And that's it: your first analysis.

##### Ontological Filter

Ontological Filter plugin filters a graph using ontologies located at URLs listed in the OntologyURLs parameter.

##### SPARQL Query

This is a more advanced plugin letting you perform your own custom SPARQL query on the output of the previous plugin.

#### Branches

You can add multiple data sources, creating numerous branches that need to be merged before the analysis can be run (trying to run an analysis with branches that aren't merged will yield in an error). Of course, you can have such an incomplete analysis saved to work on it later.

Merging branches can be done using the `Merge branches` button. You will be given a choice to use either Join or Union. After selecting one (each is described below), you need to specify which branches to be merged - at the bottom of the dialog, there are wells for each input of the merge plugin. At the top of the dialog, you have each branch represented by the name of the last plugin in each branch. If you hover your mouse over the box representing a branch, that particular branch gets highlighted in the background. You need to drag the branch boxes to the input boxes (see picture attached).

![Branches on Input Boxes](https://github.com/siroky/Payola/raw/develop/docs/img/analysis_branches.png)

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

#### Running Analyses

Either on your dashboard, or on analyses listing, click on an analysis to display details of it. You are presented with an overview of the analysis (which plugins with which parameters and bindings are going to be used).

As some analyses can take a really long time to finish (some may be theoretically infinite), there's a timeout field in the top-right corner as well as a `Stop` button. By default, an analysis times out in 30 seconds. If you find it's too short time to evaluate your analysis, change it to a higher value.

Now press the `Run Analysis` button. If the analysis succeeds, you will be automatically switched to a result tab - you can browse the resulting graph here just as when browsing a data source.

If the evaluation fails, the plugin boxes turn red and an error description is shown when you hover the mouse cursor over them. You can then either try to run the analysis again, or to Edit it using the `Edit` button next to the analysis' title.

---
<a name="plugins"></a>
### Plugins

Creating a new plugin requires at least basic programming skills in Scala. A detailed reference of the Plugin class is described in the [Developer Guide](#developer) and in the generated documentation. Here is a code of a sample plugin:

```
package my.custom.plugin

import collection.immutable
import cz.payola.domain._
import cz.payola.domain.entities._
import cz.payola.domain.entities.plugins._
import cz.payola.domain.entities.plugins.parameters._
import cz.payola.domain.rdf._

class ValuesInbetween(name: String, inputCount: Int, parameters:
				immutable.Seq[Parameter[_]], id: String)
	extends Plugin(name, inputCount, parameters, id)
{
	def this() = {
		this("Filter Values in Between", 1, 
			List(new IntParameter("MinValue", 0), 
				new IntParameter("MaxValue", 10)), 
			IDGenerator.newId)
	}

	def evaluate(instance: PluginInstance, 
			inputs: collection.IndexedSeq[Option[Graph]],
			progressReporter: Double => Unit) = {
		...
	}
}
```

In this example, a new plugin named `Filter Values in Between` is created. The parameterless constructor `this()` is called to fill in values to the default constructor. Here you set up required parameters as well.

The `evaluate` method is the one doing all the work. Here would be your code filtering the input graph. The `instance` variable contains all parameter values, `inputs` is a sequence of `Option[Graph]`'s - in our case just one as defined in `this()`. You can optionally report progress using the `progressReporter` function passed, which reports the progress to the user (values between 0.0 and 1.0).

Once you upload the plugin, an email is sent to the admin to review the plugin source code for security reasons. After he reviews it, you will receive an email with the admin's decision.

More information about plugin architecture can be found in the [Developer Guide](#developer). If you intend to write your own plugin, please, refer there.
