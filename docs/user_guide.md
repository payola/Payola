<a name="top"></a>
<a name="user"></a>

![Payola logo (credits Martin Mraz)](https://raw.github.com/payola/Payola/master/docs/img/logo_medium.png)

Payola is an HTML5 web application which lets you work with graph data in a completely new way. You can display [Linked Data](http://linkeddata.org/) using several preinstalled visualization plugins as graphs, tables, etc. This also means, that you no longer need [Pubby](http://www4.wiwiss.fu-berlin.de/pubby/) to browse through a Linked Data storage (via its [SPARQL](http://www.w3.org/TR/rdf-sparql-query/) endpoint). Moreover, you can create an analysis and run it against a set of SPARQL endpoints without a deep knowledge of the SPARQL language itself. Analysis results are processed and visualized using the embedded visualization plugins.

## Linked Data

If you **do** know, what Linked Data means, you probably already have an idea, what Payola does, so you may skip the following section. If you **do not**, let us give you a real-life example. People have their profiles on social networks and they can mark other people as their friends. To store information like this in a machine-readable format, a notation has to be introduced. For example purposes, suppose there is the following notation:

```
Bob | is friend with | Alice
```

Because there are many Bobs and many Alices, each person (generally speaking entity) must have a unique identifier assigned so we don't confuse one Bob for another. The [URI](http://cs.wikipedia.org/wiki/Uniform_Resource_Identifier) serves this purpose pretty well. So we have to alter the previous example to:

```
http://facebook.com/Bob | is friend with | http://facebook.com/Alice
```

If you think about it, almost anything can be expressed using this notation, because generally speaking, every fact can be expressed as:

```
Entity | Relation | Value
```

For example:

```
http://facebook.com/Bob | has full name | Bob Doe
http://facebook.com/Bob | was born | 1980
http://facebook.com/Bob | is friend with | http://facebook.com/Alice
http://facebook.com/Bob | is friend with | http://facebook.com/Tom
```

The uniqueness problem arises even with the relations, because friendship on a social network can be something different from a real-life friendship. So the relations are assigned unique identifiers as well. That turns the first example into:

```
http://facebook.com/Bob | http://facebook.com/relations/friendship | http://facebook.com/Alice
```

Actually, this is almost a [Turtle](http://www.w3.org/TeamSubmission/turtle/) notation, which is one of many [RDF model](http://www.w3.org/RDF/) notations. RDF is a standard which specifies what the entities, relations and values may be.

Having this context in mind, we provide a tool that makes it easy to analyze sets of facts (usually stored in a database) and visualize them. As an example of such a visualization, see the following picture, which is a sample visualization of an analysis of relations on social networks for a particular person.

![RDF visualisation example](https://raw.github.com/payola/Payola/master/docs/img/screenshots/fb_graph.png)

For a more complex and sophisticated description of Linked Data, please visit the following [website](http://linkeddata.org/).

## Motivation

Of course, our software is capable of performing analyses on a bit more complicated data. Actually, the first impulse to write such a tool was to come up with an application which would allow the user to build analyses that could help him or her to find people involved in corruption.

> And does it?
> 
> In general, yes, it does. But at the time of writing, machine processing of related data is in conflict with the privacy laws of our country. So no one may legally build such an analysis.

> So that's why the project's name is Payola?
> 
> Yes. We've just inspired ourselves in a dictionary:
>
> *Payola - the practice of bribing someone in return for the unofficial promotion of a product in the media: if a record company spends enough money on payola, it can make any record a hit*

During our studies on the University, we've come across several tools for working with Linked Data. Many of them are really useful, but rather clumsy. Especially if you want to process some data and then visualize them. You need to install and configure several tools to do so. Moreover, these tools work separately - you need to save an RDF file from each of the tools and open it from the next one. Some of them are web-based, some of them only work on Linux, some of them on Windows.

We wanted to bring a single tool which would be able to do it all. We also wanted to make a web application which is, we think, a new synonym for platform-independent (or cross-platform) software. That's also why we take advantage of the new HTML5 standard and avoid using the Flash platform.

We also wanted to present a tool which would be capable of sharing the RDF data between its users, visualizations included. Nowadays, if you share an RDF visualization to somebody, you probably share a screenshot with him or her. If it is a result of an analysis, it changes over time, but the screenshot does not. With Payola, you don't share a static visualization, you share the right to create the visualization whenever the user want. And the person you share it with gets always the most recent results.

The main goal was to come up with a prototype of a Linked Data tool for common users. To make them able to work with [RDF](http://www.w3.org/RDF/) data, explore them, analyze them, and to integrate the [OpenData.cz transparent data infrastructure](http://opendata.cz/) which is being developed mostly at the [Faculty of Mathematics and Physics, Charles University](http://www.mff.cuni.cz/). We would also like to make it into the [LOD2](http://lod2.eu/Welcome.html) technology stack, which is, by the way, one of the reasons, why we've chosen the Scala programming language.

Since Payola is rather a platform (or kind of framework, if you wish), not a closed project, more advanced users may write their own analytical plugins and incorporate them into their analyses. You can also fork the project on [https://github.com/siroky/Payola](https://github.com/siroky/Payola) and easily integrate your own visualization methods, extensions and more.

### Should you be interested in Payola?

If you work with RDF data on a daily basis, yes, you should. Payola may help you simplify or automatize some tasks. Also the visualizations may be used when describing linked data to people who are not that familiar with them.

If you just work with RDF data - it will help you access your data sources, browse them, visualize them and analyze the data. We've tried to minimize the need to learn the SPARQL language to work with linked data.

If you are a developer, you can also contribute with your own analytical plugin and share it to the users of your Payola installation. You can also come up with your own visualization plugin and compile your own version of Payola.

### What are the most common use cases?

Until now, we have thought and discussed the following use cases:

- Private tool to work with RDF data.
- Company tool to work with internal RDF data.
- Public website with community around a specific type of RDF data.
- Company/government website to present RDF data to the public.

In companies, Payola will probably have many non-technical users and a developer who will write new plugins and administer the application.

## Related Work

- http://www.visualdataweb.org/relfinder.php
- http://catalogus-professorum.org/graphicalquerybuilder
- http://data.gov.uk/linked-data

## Vocabulary
Before reading further, you should get familiar with some terms we use frequently to describe the Payola functionalities:

### Data source
A data source is not just a regular data storage. It is a data storage with a defined interface, like a SPARQL endpoint. If we talk about a data source, we almost always think of a RDF data source.

### Analysis
An analysis is a kind of an algorithm which tells Payola, how to process your data. With Payola, you can assemble analyses in a visual manner, which is a way, how you define, what should Payola do with your data before making a visualization.

Let's suppose you have a data set stored at the `http://jergym.cz/sparql` endpoint. The data set describes relations between teachers and students. Each of these relations defines which teacher is a class teacher of which student; e.g.:

```
http://jergym.cz/John_Smith | is a class teacher of | http://jergym.cz/Peter_White
```

The analysis is a tool, how to specify that you want to process a data set from the `http://jergym.cz/sparql` endpoint, filter the data, constrain them on properties and mainly, define relation patterns. In the most simple case, by assembling an analysis, you can create a SPARQL query. The more complex analysis you construct, the more SPARQL queries and dependencies between them are created on the background.

### Graph
When mentioning a **graph** we probably don't mean a plot chart or a pie chart. Despite the fact, that Payola is in a limited way capable of producing such graphs, the main form of Payola output is a set of vertices and edges, which is, in graph theory, called a [graph](http://en.wikipedia.org/wiki/Graph_(mathematics\)). A vertex stands for an entity and an edge represents a relation between two entities.

### Plugin
Since Payola is a platform, you can build your own modules which you can integrate into it. Those modules are called plugins. In the documentation we talk about two different types of plugins:
- analytical plugins: These modules can be integrated into analyses. They get a graph on the input, transform it as needed and return an output graph.
- visual plugins: They receive a graph and visualize it by the implemented set of rules.

### Data fetcher
A data fetcher is a subsystem, which is responsible for querying a data source for a data set. For example, if you want to work with a set of data from the DBPedia, a data fetcher will connect to the DBPedia SPARQL endpoint, query it with a SPARQL query and download the query result into the Payola for further processing.



## Basic usage

You can use Payola both as a guest and a logged-in user. A guest is limited to viewing analyses and data sources marked as public by other users in a read-only mode (i.e. can't edit them). This makes it easier for companies to use Payola in two different modes at a time. In the first one, they internally share data and analyses and control the access to such data. The second mode makes it easy to share any analysis or datasource to the public with a single click.

![Guest Dashboard](https://raw.github.com/payola/Payola/master/docs/img/screenshots/guest_dashboard.png)

To sign in, please, follow the `Sign in` link in the top-right corner of the page. If you have already signed up, simply fill in your email and password and press the `Sign In` button. Otherwise, click `Sign Up` to create a new profile.

![Sign In](https://raw.github.com/payola/Payola/master/docs/img/screenshots/sign_in.png)

To sign up, fill in your email and password. This email will be used as your username, as well as the contact email. We will use this email address to reset your password, if necessary, as well. We don't store the password itself, just its hash. Therefore, we will **never** send you your password in a plaintext. That's also the reason why we cannot tell you your password if you accidentally forget it. We just don't know, what the password is.

Payola will make sure that the provided email address is not used already. If it is, you probably have an active account already. Otherwise, you will need to register using a different email address.

Currently, there is no integration with any external authorization services in Payola.

![Sign Up](https://raw.github.com/payola/Payola/master/docs/img/screenshots/sign_up_credentials.png)

After signing up successfully, you will be automatically logged in and redirected to your Dashboard. The dashboard is the initial page. You can see the content shared to you there, as well as the content you have created. Especially analyses and data sources. On the dashboard, there are only first 5 items of each list. If there is more to be displayed, the 'View all' button will appear to make you able to list them all.

By clicking on the name of any of the displayed items, you will display its detail page.

The listings showing the data shared to you by other users contain the information about the user who has shared the entity with you. On the screenshot below, you can see two sections - 'Accessible analyses' and 'Accessible data sources'. These are the listings of entities shared to you. As you can see, all of these entities are shared by the 'admin@payola.cz' user.

![Logged In Dashboard](https://raw.github.com/payola/Payola/master/docs/img/screenshots/logged_in_dashboard.png)

As a logged-in user, you can now create new data sources, analyses, plugins (you can actually write your own plugin, more about that [later](#plugins)), edit them and share them. You can also upload your own private RDF data (and share it). You can also view your profile page by clicking on your email address in the top-right corner next to the log out link. Please, look into the toolbar for all the possibilities.

![User Page](https://raw.github.com/payola/Payola/master/docs/img/screenshots/user_page.png)

If you forget your password, you can click on the `Forgot Password` link on the login page. Enter your email and a new password - you will be emailed with a confirmation link. When you click on the confirmation link, the new password will be put into effect. Note that the confirmation link is valid for **two hours only**. As stated before, we don't store the original password, just its hash which cannot be decoded back. That's why we cannot send you the lost password and you need to choose a new one instead.

![Password Reset](https://raw.github.com/payola/Payola/master/docs/img/screenshots/forgot_password.png)

<a name="data-source"></a>
### Data Sources

A data source is - as its name hints - a source of data. Payola needs to know where to get its data from for evaluating analyses or displaying visualizations. That's how you provide them:


#### Creating

Let's start by creating a new data source. In the toolbar, click on the `My Data Sources` button and select `Create New`. You will need to enter a data source name, a brief description and which 'data fetcher' to use.

![Creating a Data Source](https://raw.github.com/payola/Payola/master/docs/img/screenshots/create_data_source.png)

A data fetcher is a plugin which can communicate with a data source of a specific type. For example, `SPARQL Endpoint` is a data fetcher. SPARQL is a query language for fetching data and such a data fetcher can work with any SPARQL endpoint. Select a data fetcher of your choice, fill in the data fetcher's parameters (for example, `EndpointURL` parameter in `SPARQL Endpoint` data fetcher's case) and press the `Create Data Source` button. You have just created your first data source.

#### Pre-installed Data Fetchers

Currently, only three data fetchers are shipped with Payola:

##### SPARQL Endpoint

The basic data fetcher, which can operate against any public [SPARQL endpoint](http://www.w3.org/wiki/SparqlEndpoints). It has the following parameters:

- **Endpoint URL** - an absolute URL of a SPARQL endpoint - e.g. `http://dbpedia.org/sparql`. This endpoint URL must respond to a `?query=##SPARQL_query##` GET request.
- **Graph URIs** - URIs of graphs that the queries should be performed on. If you leave the parameter empty, all graphs will be included.

##### Virtuoso Secured SPARQL Endpoint

The basic data fetcher, which can operate against a Virtuoso secured [SPARQL endpoint](http://www.w3.org/wiki/SparqlEndpoints). [Digest HTTP auth](http://www.rfc-editor.org/rfc/rfc2617.txt) is supported. It has the following parameters:

- **Endpoint URL** - an absolute URL of a SPARQL endpoint - e.g. `http://dbpedia.org/sparql`. This endpoint URL must respond to a `?query=##SPARQL_query##` GET request.
- **Graph URIs** - URIs of graphs that the queries should be performed on. If you leave the parameter empty, all graphs will be included.
- **Username** - Username used to make auth to the secured endpoint.
- **Password** - Password used to make auth to the secured endpoint.

##### Open Data Clean Storage

This is currently an experimental plugin that communicates with a testing instance of the [Open Data Clean Store](http://sourceforge.net/projects/odcleanstore/) web service.

- **Output Webservice URL** - URL of the output web service without trailing `/` - `/uri?format=trig&uri=##Vertex_URI##` is appended to the URL in order to fetcher the vertex neighborhood.
- **Sparql Endpoint URL** - a SPARQL endpoint URL. The same rules apply as with the `Endpoint URL` parameter or the `SPARQL Endpoint` data fetcher.

#### Editing

Use the toolbar at the top of the page to list available data sources (click on the `My Data Sources` button and select `View All`).

![Listing Data Sources](https://raw.github.com/payola/Payola/master/docs/img/screenshots/list_data_sources.png)

You can view all the data sources available to you. If you wish to edit one (e.g. change its name or description), click on the Edit button on the same row. You'll be redirected to the edit page which contains a delete button as well, for removing the data source. The sharing functionality will be described in the [Sharing section](#sharing).

The same steps to list and edit apply to any other entity in Payola (analyses, plugins, etc.).

#### Viewing

When on the Dashboard, or listing all available data sources, click on a data source to view its details. In the case of a data source, you will be switched into a browse mode, where you are able to explore the data stored in the data source.

![Loading Initial Vertex](https://raw.github.com/payola/Payola/master/docs/img/screenshots/loading_initial_vertex.png)

You'll be presented with a neighborhood of an initial vertex. What is an initial vertex depends on the implementation of the underlying data fetcher. Those, which come bundled with Payola simply choose one random entity from the data source and use it as the starting point of the data source exploration.

![Initial Vertex](https://raw.github.com/payola/Payola/master/docs/img/screenshots/initial_vertex.png)

The initial graph (neighborhood of the initial vertex) can be viewed in many ways. Payola comes bundled with several visualization plugins. By default, a graph is visualized as a table of triples. Each triple represents a relation between two entities. This makes you able to quickly determine the contents of the graph as well as its size. Later, you can switch to a mode which renders a graph on your screen.

You can navigate through the graph by following the vertex links in the table. Click on the 'server' icon in front of the link to view the vertex using a different data source.

![Browse Using a Different Data Source](https://raw.github.com/payola/Payola/master/docs/img/screenshots/browse_using_data_source.png)

Alternatively, you may use the `SPARQL` button at the top-right to enter your own custom SPARQL query to be evaluated and displayed.

> *Note:* The DBpedia.org data source puts quite heavy restrictions in place on SPARQL queries that may be executed on the data source. Hence, not all queries may be successfully executed. This applies to analyses as well.

![Entering SPARQL Query](https://raw.github.com/payola/Payola/master/docs/img/screenshots/enter_sparql_query.png)

You can, however, change the visualization plugin using the `Change visualization plugin` button. 

`Select Result Table` requires the current graph to be a result of a SELECT SPARQL query, which typically needs to be a custom query using the `SPARQL` button. Otherwise, only a message telling the user that the graph can't be displayed using this visualization plugin is displayed.

![Select Table](https://raw.github.com/payola/Payola/master/docs/img/screenshots/select_table_no_result.png)

`Circle`, `Gravity` and `Tree` visualizations display a regular graph. They represent the entities as circles (identified vertices - entities with a definite URI) interconnected by arrows (relations - oriented edges). Literal vertices (entities not containing a URI) are not displayed, but are identified vertices' attributes and are shown in a table and only if the particular identified vertex is selected. The visualizations also show vertex URIs (or labels if a vertex has such an attribute) and edge URIs (if one of the edge's vertices is selected). The difference between the plugins is only in the initial animated layout. Controls for handling the graph are the same for all three plugins.

These visualizations allow you to select vertices. Single vertex can be selected by pressing a mouse button over it. Multiple vertices can be selected by holding the Shift key while selecting vertices. Deselecting vertices can be done by holding Shift and pressing a mouse button over a selected vertex or simply by selecting vertex that isn't currently selected without holding the Shift key. Deselecting all vertices is done by pressing mouse between vertices (not over a vertex). These visualizations also allow you to move selected vertices around. By pressing and holding down a mouse button over a vertex and moving it (dragging) you can move all selected vertices of the graph. Whole graph can also be moved by dragging mouse between vertices.

A zooming tool is provided as well. Zooming in or out can be done by rolling the wheel of your mouse or by pressing the `Zoom in` or `Zoom out` buttons in the control bar shown on the top of the graph. Between the buttons in the control bar the current zoom status is shown. Its default value is set to 100%.

The control bar also contains a `Stop animation` button. This button can effect the initial layout animation. If the animation takes a long time to finish, this button stops it. The animation is also stopped if any of the other controls are used. Pressing a mouse button or zooming interrupts the animation as well and allows you to control the visualization.

Another button specific for these three visualizations is `Download as PNG`. Pressing this button saves the image of the graph in the PNG format.

The reaction time of the controls and speed of the initial animations may be effected by hardware configuration of your computer. It also may be affected by the web browser you are using.

![PNG Download](https://raw.github.com/payola/Payola/master/docs/img/screenshots/graph_png_download.png)

The `Column Chart` visualization will display a column bar graph, but works only with graphs of a specific structure. The graph must have one identified vertex, whose incoming edges are of a [`rdf:type`](http://www.w3.org/1999/02/22-rdf-syntax-ns#type) URI - the source of each edge must then have exactly three edges - one going to the aforementioned vertex and then two directed to literal vertices, one with a string value (name of the column), the second one with a numeric value.

![Column Chart Graph Representation](https://raw.github.com/payola/Payola/master/docs/img/screenshots/column_chart_data.png)

![Column Chart](https://raw.github.com/payola/Payola/master/docs/img/screenshots/column_chart.png)

#####Data Cube vocabulary
The new version of the Payola application contains new features related to the [Data Cube vocabulary standard](http://www.w3.org/TR/vocab-data-cube/). While this standard is used to add semantics to the statistical data, Payola builds up on it and brings some new visualizers. Those visualizers are specialized on visualizing statistical data. The visualizer looks for a Data Cube Data Structure definition in the supplied graph. If present, it builds controls, which enables the user to switch used attributes, dimensions and measures. Moreover, it makes the user able to slice the data as known from the OLAP cube theory.

######Time Heatmap
The first available visualization plugin is the Time Heatmap plugin. It allows you to visualize the data on a Google Map.

As the name suggests, this visualizer is able to handle datasets with two kinds of dimensions. The first one will express the time of the measurement, the secondone will cover its location. It will support one measure that has to be a number. We will place a heatmap layer over a standard map layer. The layer willexpress the intensity of the measured value in respect to the others. The scale will go from green to red where the latter represents the largest value measured.

We wrap the map with a custom control. It contains a list of yearsdetected in the dataset. The user is able to select an arbitrary subset of years.The visualizer shows and hides a corresponding layer based on what is selected.In order to deliver an easy-to-use visualizer, we decided to ignore GPS dataand employ places names. With the integration of the Geocoder library of a colleagueof ours, Matej Snoha, the visualizer is now able to translate the namesof the places to GPS coordinates and place them into the map. The librarytakes advantage of a locally installed application Gisgraphy. The installationis maintained by the author of the used library.We tried to make the visualization as easy as possible, therefore we decidedto exert the URI resource itself. It usually contains the name of the placein a way, which is convertible into a form recognized by the Gisgraphy application.For instance, http://dbpedia.org/page/Brasserie_Du_Bocq can be convertedinto Brasserie du Bocq very easily.

![TimeHeatmap Visualizer](https://raw.github.com/payola/Payola/master/docs/img/screenshots/timeheatmap.png)

######Universal DataCube
While experimenting with Data Cube, we have experienced on our own some discomfort in reading triple tables.That is why we have decided to implement a visualizer, which takes advantage of the idea behind faceted browsers.Therefore we would like to implement a visualizer that will enable the user to slice visualized datasets and prepare usual visualizations of the slices.

We decided to offer a basic set of usual statistical visualisations. We achieve that by giving the user the ability to slice the dataset.We need them to fix values of n-1 dimensions in order to transform the dataset into a two-dimensional one (1 dimension + 1 measure).We took advantage of the chart library Flot and prepared a visualizer, which allows the user to create bar or pie charts. 
By applying filters, the user slices the dataset. It supports multiple dimensions, attributes and even measures. It is configurableby a Data Cube Vocabulary. We require the user to specify a dimension that gets to be used on x-axis of a bar chart (or defined groups in a pie chart).Based on filters, the visualizer groups DCV measures by the specified dimension (aggregated by applying sum function). In case of a bar chart, multiple measuresare displayed as groups of columns.

![Universal DataCube Visualizer](https://raw.github.com/payola/Payola/master/docs/img/screenshots/unidcv.png)
![Universal DataCube Visualizer](https://raw.github.com/payola/Payola/master/docs/img/screenshots/unidcv2.png)

### Ontology Customization

By default, each vertex and edge is of the same color and has the same size (radius) when viewed using the graph-based visualizations. To emphasize or diminish some parts of the graph, you can customize the visual appearance using an ontology customization.

While viewing a graph, press the `Change appearance using ontologies` button. If you have already saved some customizations, they are listed here - if you haven't created any yet, select the `Create New` menu item.

![Create Ontology Customization](https://raw.github.com/payola/Payola/master/docs/img/screenshots/create_customization.png)

Enter the customization name and URL of that ontology, e.g. [http://opendata.cz/pco/public-contracts.xml](http://opendata.cz/pco/public-contracts.xml), and press `Create`.

![Create Ontology Customization Dialog](https://raw.github.com/payola/Payola/master/docs/img/screenshots/create_customization_dialog.png)

You will be presented with a customization dialog. On the left, ontology classes are listed - select one. On the right, properties of that class are listed. At the very top of the right column, you can customize the appearance of the class itself (in the graph displayed as vertices).

![Editing Customization](https://raw.github.com/payola/Payola/master/docs/img/screenshots/customization_edit.png)

Below, you can modify appearance of that class' property (in the graph displayed as edges). You can change color of the vertex or edge:

![Editing Customization - Color](https://raw.github.com/payola/Payola/master/docs/img/screenshots/customization_edit_color.png)

Or add a glyph to a vertex:

![Editing Customization - Glyph](https://raw.github.com/payola/Payola/master/docs/img/screenshots/customization_edit_glyph.png)

> This feature requires your browser to support the FontFace standard.

When done, just press the `Done` button. If you want to modify the customization further, click on the `Edit` button in the `Change appearance using ontologies` button's menu. Now select the ontology using the `Change appearance using ontologies` button.

![Select Ontology Customization](https://raw.github.com/payola/Payola/master/docs/img/screenshots/select_customization.png)

Your graph will be redrawn automatically according to the customization selected/modified.

![Ontology Customization Graph](https://raw.github.com/payola/Payola/master/docs/img/screenshots/customization_graph.png)

### Groups

You can create user groups to make sharing easier (as described in the next chapter). Imagine you want to share a resource (e.g. an analysis) to a group of co-workers. One approach would be to share it to each one of them, but this can be tedious considering you might want to share something to them every week or every day. Hence there's a possibility to create a user group - in the top toolbar, click on the `User Groups` button and select `Create New`.

Enter the group name (e.g. 'My co-workers') and press the `Create Group` button.

![Group Create Dialog](https://raw.github.com/payola/Payola/master/docs/img/screenshots/group_create_dialog.png)

After the group has been created, you can start adding members to the group. To do so, make the `Members` field active and start typing - a suggestion box will appear offering users whose names match the entered text.

![Group Create AutoFill](https://raw.github.com/payola/Payola/master/docs/img/screenshots/group_create_autofill.png)

Click on the user to add him or her. If you decide to remove a user, click on the `x` button in front of his or her name. Remember to use the `Save Group` button before leaving the edit page, otherwise all changes made will be lost.

![Group Create - User Added](https://raw.github.com/payola/Payola/master/docs/img/screenshots/group_create_user_added.png)

To delete a group, use the `Delete` button at the top-right corner of the edit page, or on the groups listing.

[Group Listing](https://github.com/siroky/Payola/raw/develop/docs/img/screenshots/group_listing.png)

<a name="sharing"></a>
### Sharing

Now that you know how to create a group, let's share a data source. In the toolbar, click on the `My Data Sources` button and select `View All`. This lists all your data sources. You can use the `Edit` button to edit the data source, use the `Private`/`Public` button to toggle whether the data source is private (only you and people you share it to can use it), or public - anyone can use it, even people who are not logged in; or use the delete button to remove the data source.

> You can only share entities owned by you.

![Share Button](https://raw.github.com/payola/Payola/master/docs/img/screenshots/share_button.png)

Then there's the `Share` button. When you click on it, a menu pops up, allowing you to share the data source either to users or groups. When you select the `To Users` menu item, a dialog is shown with a text field which will suggest users as you type just like when you were adding members to a group. 

The other option is to share the data source to groups - again a dialog will appear, letting you select multiple groups using the suggestion box. Add groups you want and confirm the dialog. All users within the selected groups will now be able to use this data source.

![Share to Groups](https://raw.github.com/payola/Payola/master/docs/img/screenshots/share_to_groups.png)

If you no longer want to share a resource with a group or a user, follow the same steps as if you wanted to share it with someone - the dialog which appears will contain the users or groups whom you've shared the resource to previously. Press the `Share` button to confirm the dialog. The list of users and groups allowed to access the resource will be updated accordingly.

### Private Data Storage

While listing data sources, you might have noticed a data source called `Private Storage of ...` - when you sign up, a new private data storage is created in your Virtuoso instance. You can add your own data to this storage. Of course, you can share this data source as well.

#### Adding data to data storage

To add data to your private data storage, use toolbar's `Private RDF Storage` button and select `Upload Data`.

Here you are presented with two options: to upload an RDF/XML or TTL file, or load the RDF/XML from a URL. Retrieving a graph in a TTL format from a URL isn't currently supported.

> The file must be UTF-8 encoded.

Press the `Choose File` button.

![Select File to Upload](https://raw.github.com/payola/Payola/master/docs/img/screenshots/file_to_upload.png)

Press the `Upload File` button. You will be redirected back to the same page after the upload, with the upload result (denoting either success or failure).

![Successful Upload](https://raw.github.com/payola/Payola/master/docs/img/screenshots/successful_upload.png)
![Upload Failed](https://raw.github.com/payola/Payola/master/docs/img/screenshots/upload_failed.png)

If the upload fails, please, use the [RDF validator](http://www.w3.org/RDF/Validator/) to verify that your data is correct and make sure it's UTF-8 encoded as noted above.

### Analyses

Creating a new analysis is similar to creating any other resource - in the toolbar, select `Create New` from the `My Analyses` button's menu. You will be prompted to enter a name - enter the analysis' name - you can change it later on.

![Create Analysis Dialog](https://raw.github.com/payola/Payola/master/docs/img/screenshots/create_analysis_dialog.png)

You will be presented with a blank page with a control box in the top-left corner. Start by filling in the analysis description.

![Create Analysis Page](https://raw.github.com/payola/Payola/master/docs/img/screenshots/create_analysis_page.png)

First, you'll need a data source to start with. You can do so either using the `Add data source` button which will offer you all available existing data sources (including those that are shared to you), or `Add plugin` which lets you add a data fetcher - an anonymous data source (see [Data Source documentation](#data-source) for more information). This can be useful if you decide to use a data source that you don't want to save right away (e.g. you know you'll use it just once).

![Create Analysis - Adding Plugin](https://raw.github.com/payola/Payola/master/docs/img/screenshots/create_analysis_adding_plugin.png)

![Create Analysis - Filling Plugin Parameters](https://raw.github.com/payola/Payola/master/docs/img/screenshots/create_analysis_plugin_parameters.png)

Now that you've added a data source, you need to do something with the data. Click on the `Add Connection` button on your data source box. Payola comes with pre-installed plugins, which are described one by one below. Of course, you can add your own plugin (see [section Plugins](#plugins)). Plugins are ordered in a sequence (though more branches can be created, see below) - a plugin always gets the result of the previous one as its input and passes its result to the next one (unless it's the last plugin, then it's the result of the whole analysis).

If you are familiar with the architectonic style 'pipe and filters', our analysis is an instance of this pattern. Each plugin represents a filter which alters the graph it gets on input and returns a resulting graph.

![Create Analysis - Connecting Plugin](https://raw.github.com/payola/Payola/master/docs/img/screenshots/create_analysis_connecting_plugin.png)

> Beware when editing a shared analysis. Currently, Payola does not lock your analysis nor does it duplicate while being edited. Therefore nobody is prevented from running an evaluation of such an analysis. Because of this fact, we strongly recommend you to stop sharing your analysis before editing it.

#### Pre-installed Plugins

##### Typed

This plugin selects vertices of an RDF type that's filled in as a parameter called `RDF Type URI` from its input graph.

- **RDF Type URI** - A single URI of a vertex RDF type, e.g. `http://dbpedia.org/ontology/City`.

##### Property Selection

Property Selection plugin takes property URIs separated by a newline as a single parameter. It will select vertices that are connected to other vertices using one of the listed URIs.

- **Property URIs** - A list of URIs (each on a new line) of a property RDF type, e.g. `http://dbpedia.org/ontology/populationTotal`.
- **Select property types and labels** - When checked, all properties are also added to the `OPTIONAL` section of the query, fetching each property's type and label.

> **Note:** Payola performs some optimizations, potentially merging several consecutive plugins together. For example, two consecutive property selection plugins are always merged - hence their result isn't an empty graph as one could expect even if each of them lists completely different set of URIs, but a graph that contains both property selections (if this optimization hadn't taken place, the first plugin would create a graph containing vertices connected to each other using URIs declared in the first plugin, which would then be filtered out using the second plugin, resulting in an empty intersection).

##### Filter

Filter plugin lets you select vertices with an attribute of particular value or property - for example select cities with more than 2 million inhabitants.

- **Property URI** - A URI of the property, e.g. `http://dbpedia.org/ontology/populationTotal`.
- **Operator** - An operator - `<`, `>`, `=`, ... (see the *Value* parameter below for details).
- **Value** - The value for the attribute to be compared to, e.g. `20000000`. These three parameters get combined to create a `FILTER` statement in the SPARQL query - with the examples provided, this plugin gets translated to this part of the resulting SPARQL query: `FILTER (?v > 20000000)`. Note that if the value should be handled as a string, it needs to quoted (e.g. `"John Doe"`).

##### Ontological Filter

Ontological Filter plugin filters a graph using ontologies located at URLs listed in the `Ontology URLs` parameter.

- **Ontology URLs** - URLs of ontologies - each on a new line. The resulting graph only contains vertices of classes defined in these ontologies as well as properties defined by them.

##### SPARQL Query

This is a more advanced plugin letting you perform your own custom SPARQL query on the output of the previous plugin.

- **SPARQL Query** - An actual SPARQL query.

#### Branches

You can add multiple data sources, creating numerous branches that need to be merged before the analysis can be run (trying to run an analysis with branches that aren't merged will yield in an error). Of course, you can have such an incomplete analysis saved to work on it later.

![Create Analysis - Multiple Branches](https://raw.github.com/payola/Payola/master/docs/img/screenshots/create_analysis_multiple_branches.png)

Merging branches can be done using the `Merge branches` button. You will be prompted to select a plugin for merging branches. Since a plugin has a predefined number of inputs, Payola will list all plugins with more than one input. You can, for example, write your own merge plugin which will have 4 inputs, so you can merge 4 branches into one using a single plugin. Currently, Payola comes only with 2 merge plugins so you will be asked to use either Join or Union.

![Create Analysis - Merging Branches Dialog](https://raw.github.com/payola/Payola/master/docs/img/screenshots/create_analysis_merge_dialog_choose.png)

After selecting one (each is described below), you need to specify which branches to be merge - at the bottom of the dialog, there are wells for each input of the merge plugin.

![Create Analysis - Merging Branches](https://raw.github.com/payola/Payola/master/docs/img/screenshots/create_analysis_merge_dialog_initial.png)

At the top of the dialog, you have each branch represented by the name of the last plugin in each branch. If you hover your mouse over the box representing a branch, that particular branch gets highlighted in the background. You need to drag the branch boxes to the input boxes.

![Create Analysis - Merging Branches](https://raw.github.com/payola/Payola/master/docs/img/screenshots/sample_analysis_merged.png)

All the inputs need to have a branch connected, otherwise, the dialog won't let you to add a merge plugin to your analysis.

##### Union

Union simply merges two graphs together as one would expect. Vertices with the same URI will be unified, their properties merged.

##### Join

The join plugin can be a little bit tricky. If the joined branches consist of `Typed`, `Filter` and `Property Selection` plugins and the data are fetched from identical data fetchers (same type and parameter values), then an analysis optimization is performed. Both branches and the join plugin are treated as one data fetcher which is connected to a SPARQL query plugin. So the join actually behaves similarly to a relational database join - the first branch selects entities, which are related to entities specified by the second branch (in case of inner join). In case of outer join, all entities of the first branch are selected, even though they aren't related to any entity from the second branch.

On the other hand, if the two joined branches are unrelated, so no optimization can be performed, the join behaves differently. In case of an inner join, only edges from the first graph with URI defined in the `Property URI` parameter are included. Also, the destination of the edge must be present in the second graph. Otherwise, the edge is omitted.

> *Example:*
>
> **Graph A**: `payola.cz/dog - payola.cz/barks-at - payola.cz/tree`
>
> **Graph B**: `payola.cz/wolf - payola.cz/evolved-to - payola.cz/dog` 
>
> If graph A is joined with graph B using the `payola.cz/barks-at` property, an empty graph is returned because `payola.cz/tree` isn't a vertex in the graph B.
>
> When tried the other way around - joining graph B with graph A using the `payola.cz/evolved-to` property, the `wolf - evolved-to - dog` triple is included in the result (`payola.cz/dog` is a vertex in graph A).

In case of outer join, all vertices from the first graph that are origins of edges with URI defined in the `Property URI` parameter are included. Moreover, if origin of the edge is included in the second graph, destination of the edge and the edge itself are both included in the result as well.

> *Example:*
> Using the same graphs as before, merging graph A with graph B will yield just `payola.cz/dog` because the `payola.cz/tree` isn't present in the second graph. Merging B with A will return the same result as in the previous example - `wolf - evolved-to - dog`.


##### Analysis
You may create a custom plugin from an existing analysis. That gives you the ability to make an inner analysis, hence use an analysis in another one. That may be used to simplify an existing analysis, avoid repeating parts of analysis in multiple branches and more.

To create a new analysis plugin, just click the button from the following image and follow the instructions, e.g. select a analysis to make the plugin from.

![Inner analysis - create](https://raw.github.com/payola/Payola/master/docs/img/screenshots/inner_analysis_create.png)

![Inner analysis - create](https://raw.github.com/payola/Payola/master/docs/img/screenshots/inner_analysis_choose.png)

To make the feature even more sophisticated we made a special user interfaceto enable the user to parametrize an inner analysis. When inserting an analysisinto another, the user is able to click the names of parameters of plugin instancesin the inner analysis in order to promote them to analysis parameters.

![Inner analysis - parameter selection](https://raw.github.com/payola/Payola/master/docs/img/screenshots/inner_analysis_parameter_selection.png)

![Inner analysis - simplified analysis](https://raw.github.com/payola/Payola/master/docs/img/screenshots/inner_analysis.png)
![Inner analysis - simplified analysis](https://raw.github.com/payola/Payola/master/docs/img/screenshots/inner_analysis_simplify.png)

##### Limit
A simple plugin that limits the resultset of the produced query. Not every scenario is optimized, therefore the plugin might not speed-up analysis execution. But basic scenarios are handled. For instance, we merge a DataFetcher plugin followed by Typed and Filter, terminated with the Limit plugin into a single SPARQL query. We have also implemented a phase which optimizes the query in the case we combine a common SPARQL query with the Limit plugin.

##### Data Cube Vocabulary
Based on a Data Cube vocabulary, one can create a new plugin. That plugin allows the user to define mapping between an original dataset and the specified Data Cube vocabulary. As a result, the user will get a statistical dataset which is mapped to a format, which complies with the specified DCV. That is usually needed to be done, when one wants to utilitze the Data Cube Vocabulary visualizers (TimeHeatmap, Universal DCV).

To create a new DCV plugin, just click the following button:

![DataCube - create a plugin](https://raw.github.com/payola/Payola/master/docs/img/screenshots/dcv_create.png)

After doing that, one is needed to supply a URL of a TTL document containing the DCV definition (at least one Data Structure Definition):

![DataCube - supply TTL URL](https://raw.github.com/payola/Payola/master/docs/img/screenshots/dcv_url.png)

The graph is parsed and a list of Data Structure Definitions is made for you. Please, choose one:

![DataCube - Data Structure Definitions](https://raw.github.com/payola/Payola/master/docs/img/screenshots/dcv_dsd.png)

A new plugin is created and inserted to an existing analytical pipeline. Now, you are needed to select a transformation/mapping pattern. In order to do that, press the Preview button:

![DataCube - Editable plugin instance](https://raw.github.com/payola/Payola/master/docs/img/screenshots/dcv_editable_pi.png)

A sub-pipeline is created and executed with timeout of 30 seconds. In order to fit into this time, one should probably insert the DCV plugin into such a point of analysis
that the data is refined as much as possible (using OWL, Typed, Filter plugins). To understand, what's happening, see the following picture. A sub-pipeline is created and appended with a Limit plugin.

![Data Cube - created subpipeline](https://raw.github.com/payola/Payola/master/docs/img/screenshots/dcv_subpipeline.png)

After the preview is made, you follow the instructions and selects vertices related to the DCV definition - you should give an example of mapping, for instance, for a population cube, one should select population area, population size and time of measurement. An example is shown in the following picture:

![Data Cube - Mapping example](https://raw.github.com/payola/Payola/master/docs/img/screenshots/dcv_mapping_example.png)

A more theoretical example is presented in this picture:

![Data Cube - mapping mockup](https://raw.github.com/payola/Payola/master/docs/img/screenshots/dcv_theory.png)
![Data Cube theory - what is mapped](https://raw.github.com/payola/Payola/master/docs/img/screenshots/dcv_theory2.png)

#### Example

Let's create an analysis which selects all cities with more than 2 million inhabitants. First, add a `DBpedia.org` data source.

![Create Analysis - Adding Data Source](https://raw.github.com/payola/Payola/master/docs/img/screenshots/create_analysis_adding_data_source.png)

![Create Analysis - Added Data Source](https://raw.github.com/payola/Payola/master/docs/img/screenshots/create_analysis_added_data_source.png)

Then connect a new `Typed` plugin with `RDF Type URI` `http://dbpedia.org/ontology/City`.

![Typed Plugin](https://raw.github.com/payola/Payola/master/docs/img/screenshots/plugin_typed.png)

Continue with a `Property Selection` plugin with `Property URIs` `http://dbpedia.org/ontology/populationTotal`.

![Property Selection Plugin](https://raw.github.com/payola/Payola/master/docs/img/screenshots/plugin_projection.png)

And a `Filter` plugin with `PropertyURI` `http://dbpedia.org/ontology/populationTotal`, `Operator` `>` and `Value` `2000000`.

![Filter Plugin](https://raw.github.com/payola/Payola/master/docs/img/screenshots/plugin_selection.png)

And that's it: your first analysis. Now let's fetch countries of the cities as well.

Add a one more `DBPedia.org` data source and connect a `Typed` plugin with `http://dbpedia.org/ontology/Country` `RDF Type URI` parameter and a `Property Selection` plugin with `http://dbpedia.org/ontology/areaTotal` `Property URIs` parameter as seen on the picture below.

![Two Branches](https://raw.github.com/payola/Payola/master/docs/img/screenshots/two_branches.png)

Click on the `Merge Branches` link and select `Join`. Place the branches on the input boxes as seen below.

![Create Analysis - Merging](https://raw.github.com/payola/Payola/master/docs/img/screenshots/sample_analysis_merging.png)

Now fill in the `Join Property URI` with `http://dbpedia.org/ontology/country` and make sure the `Is Inner` checkbox isn't checked.

![Create Analysis - Merged](https://raw.github.com/payola/Payola/master/docs/img/screenshots/sample_analysis_merged.png)

Now it's really done and ready to be run - scroll up the page and press the `Run` button.

#### Running Analyses

Either on your dashboard, or on analyses listing, click on an analysis to display its details. You are then presented with an overview of the analysis (which plugins with which parameters and bindings are going to be used).

![Analysis Overview](https://raw.github.com/payola/Payola/master/docs/img/screenshots/analysis_overview.png)

As some analyses can take a really long time to finish (some may be theoretically infinite), there's a timeout field in the top-right corner as well as a `Stop` button. By default, an analysis times out in 30 seconds. If you find it's too short to evaluate your analysis, change it to a higher value.

Now press the `Run Analysis` button. The boxes of plugins which are being evaluated will turn yellow (if the analysis is being executed in one single step, you will be switched to the results right away).

As the evaluation is being executed, the progress is indicated in a two different ways simultaneously. Firstly, on the top of the page, you can see a progress bar which indicates the percentage of the evaluated plugins (it does not indicate time since that cannot be estimated). Secondly, background color of plugins changes:

- blue means that the plugin is pending to be evaluated
- yellow means that the plugin is being evaluated right now
- red means that there was an error while evaluating the plugin
- green means that the evaluation of the plugin is done without errors

![Running Analysis](https://raw.github.com/payola/Payola/master/docs/img/screenshots/analysis_running.png)

If the analysis succeeds, you will be automatically switched to the result tab - you can browse the resulting graph here just as when browsing a data source.

![Running Analysis](https://raw.github.com/payola/Payola/master/docs/img/screenshots/analysis_finished.png)

Moreover, you can also download the result of the analysis as an RDF/XML or TTL file.

![Downloading Analysis Result](https://raw.github.com/payola/Payola/master/docs/img/screenshots/analysis_download.png)

If the evaluation fails, the plugin boxes turn red and an error description is shown when you hover the mouse cursor over them.

![Analysis Failed](https://raw.github.com/payola/Payola/master/docs/img/screenshots/analysis_failed.png)

You can then either try to run the analysis again, or to Edit it using the `Edit` button next to the analysis' title.

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
- The parameterless constructor (```def this()```) is used to instantiate the plugin for the first time, so this is the place where you can set the plugin name, number of its inputs, and the parameters. A new ID of the plugin should be obtained using the ```IDGenerator.newId``` method.

> If you wonder why such constraints are used, note that there is only one instance of each plugin class living in the application in every moment. In fact, there may be more than one instance of the plugin class, however all these instances are identical, so they have same IDs and parameters with same IDs. The parameterless constructor is therefore used to create the first instance of the plugin class. The instance is consecutively persisted into the database and whenever it is accessed, it's instantiated using the default constructor with the values retrieved from the database.

The second constraint on the plugin is that it must implement the abstract method ```evaluate(instance: PluginInstance, inputs: IndexedSeq[Option[Graph]], progressReporter: Double => Unit)```. The `instance` parameter contains all parameter values, `inputs` is a sequence of `Option[Graph]`'s - in this case just one as defined in `this()`. You can optionally report progress using the `progressReporter` function passed, which reports the progress to the user (0.0 < progress <= 1.0). Refer to the API documentation to explore which methods you can call on the `instance` or within the plugin class scope (e.g. helper methods like `usingDefined`).

![Plugin Source](https://raw.github.com/payola/Payola/master/docs/img/screenshots/plugin_source.png)

Once you post the plugin source code, it gets compiled to check for syntax errors and that the code is indeed a Plugin subclass.

![Plugin Compiling](https://raw.github.com/payola/Payola/master/docs/img/screenshots/plugin_compiling.png)

After that an email is sent to the admin to review the plugin source code for security reasons. After he reviews it, you will receive an email with the admin's decision.

![Plugin Compiling](https://raw.github.com/payola/Payola/master/docs/img/screenshots/plugin_submitted.png)

#### Other plugin types

You're not required to extend directly the `Plugin` class, you may also extend the `cz.payola.domain.entities.plugins.concrete.DataFetcher` or `cz.payola.domain.entities.plugins.concrete.SparqlQuery`. The former one in case you want to create a data fetcher which may be used as a data source plugin. The second one in case the plugin evaluation function can be expressed as an application of a SPARQL query on the input graph. Both have the method `evaluate` already implemented, but they introduce other abstract methods that have to be implemented. You can get the best insight on how they work from the following examples or from the sources of predefined `DataFetcher`s or `SparqlQuery`s.

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

# User interface enhancements

## LodVis integration
We have discovered, that the provided features are very conducive though completely missingin Payola. The application does not offer an overview of a dataset in order to helpdiscover the most used concepts. Since we want the user to know their dataset before choosing the patternin the preview, we find this tool very interesting. It may help the user to locatethe statistical subset of the data. Therefore, we discovered a simple way of implementing a plain kind of interaction between Payola and LodVis.

### Visualising Payola data sources in LodVisWe utilize a simple URL pattern:> http://lodvisualization.appspot.com/?graphUri={graph-uri}&endpointUri={endpoint-uri}The pattern has only two parameters. The first one, the graph-uri designatesthe URI of the graph we want LodVis to visualise. This parameter is not mandatory.The second parameter, endpoint-uri is required and it tells the LodVisapplication, where to fetch the data from. It is not possible to visualize resultsof an analysis in LodVis. 

### Visualising Payola data sources in LodVisWith cooperation of the LodVis team, we have also implemented a reverse mechanism- an API, which allows the user of the LodVis application to browsea dataset in the scope of the Payola application. To make it easy for the Lod-Vis team to integrate a link to the Payola application, we prepared an API verysimilar to theirs. The URL pattern is very similar:

![LodVis integration](https://raw.github.com/payola/Payola/master/docs/img/screenshots/lodvis.png)
> http://live.payola.cz/visualize?endpointUri={endpoint-uri}&graphUri={graph-uri}In addition, we support referencing a list of graph URIs separated by a comma.Since we wanted to avoid any difficulties arising from passing a URI in an URL,we decided to have the parameters encoded with the Base64 algorithm.The only problem was, that Payola was not able to visualize a dataset notregistered within its database. Therefore, when somebody accesses the URL, the application takes the parameters and creates an anonymous analyzer pipeline.The only plugin in the pipeline is a data source specified by the passed parameters. By evaluating such a pipeline, the user is able to visualize the neighbourhoodof the vertex in the given dataset. The vertex is chosen by the SPARQL endpoint backend, e.g. OpenLink Virutoso.To allow an anonymous user to modify the pipeline, we also set a cookie with an authorization token to their browser. When logged into Payola a userhaving such a token can overtake the ownership of the pipeline and fully modify it.

### Analysis cloning
It enables the user to clone an analysis of another user. After doing that, they get a newly created analysis owned by them. Therefore,they are in a full control of the analysis and may modify it in any way they want.![LodVis integration](https://raw.github.com/payola/Payola/master/docs/img/screenshots/clone.png)

### Data Source browser permalinksAnother minor change was done within the scope of the data source browser. We introduced a mechanism, which enables the user to send a link to a current stateof the triple table data source browser. It detects the specified URI in the URLand fetches an initial vertex based on that. The original behaviour delegates the decision on an underlying engine of the queried data source. We simply adda location hash to the URL that makes the permalink available in the address bar of the user's browser. The implementation was extended with an onload eventhandler, which looks for the location hash and reacts to it.