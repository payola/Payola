# We &hearts; Payola!
---
# Setting up Payola
## System Requirements

Payola requires a [Scala](http://www.scala-lang.org) environment, which is supported on virtually all platforms capable of running Java code - both Unix and Windows-based systems are capable of running Payola. The system should have at least 1GB of memory dedicated to Payola itself.

Aside from the actual Payola server, you need to be capable of running any [Squeryl-compatible](http://squeryl.org) relational database for storing user data and a [Virtuoso](http://virtuoso.openlinksw.com) server for storing personal RDF data. Neither of those need to necessarily be running on the same system as Payola itself (this is configurable in the `payola.conf` file as described later on).

## Installation Guide

You need to have a working Scala environment to install Payola with [SBT (Scala Build Tool)](https://github.com/harrah/xsbt/wiki/) installed. Clone Payola git repository: `git://github.com/siroky/Payola.git` to a local folder.

### <a name="configuring"></a>Configuring 

Payola comes configured to work with default settings of a Virtuoso server and a H2 database installed on the same server as Payola is running (i.e. localhost). To change this configuration, edit `payola/web/shared/src/main/resources/payola.conf` - it’s a regular text file with various options on each line. Comment lines start with a hash symbol (`#`).

### Compiling and Running Payola

As you clone just source codes from the git repository, it’s necessary to compile Payola. To do so, you need to have SBT installed as noted above. Open command line, make  payola  subdirectory current working subdirectory. Launch SBT (probably using the `sbt` command) and enter the following commands:

```
> cp
...
> project initializer
> run
...
> project server
> run
```

Voilà! Your Payola server is running. The initializer project sets up your database to include an admin user (login `admin@payola.cz`, password `payola!`), a sample analysis and some data sources. You can, of course, remove those and create your own.

### Security

Both the Virtuoso server and H2 database by default allow incoming connections from outside of your network, or localhost - Payola allows users to store their own private RDF data using Virtuoso groups that are identified using a generated 128-bit UUID (and the H2 database is secured by a username-password combination).


While a simple guess of another user's group identifier is unlikely (or a brute-force attack on the username-password combination noticeable), it is advisable to secure your local Virtuoso storage and your relational database by denying all incoming and outgoing connections outside of localhost, or if on a secure company network, outside of that particular network. This is up to each admin to correctly set up the server's firewall.

---
# Using Payola

## Launching

To launch Payola, open SBT just like when you were compiling it and enter these two commands (you need to run `cp` command before these two if you've modified any source code since last compilation):

```
> project server
> run
```

>*Warning:* Do **not** run the `initializer` project. The initializer process would delete the whole relational database - i.e. all users, analyses, data sources, etc. would be lost.

Once the server is running, enter the following address in your web browser:

><http://localhost:9000/>

Of course, the port will be different depending on your configuration file (see section [Configuring](#configuring) for details).

## Basic usage

You can use Payola both as a logged in user, or a guest. A guest is limited to analyses and data sources marked as public by other users and can only view these, but cannot edit them.

A logged-in user can create new data sources, analyses, plugins (you can actually write your own plugin, more about that later), edit them and share them; and upload your own private RDF data.

> TODO: the following chapters are in the order they should be written: the data sources are the easiest to create, groups need to be explained for sharing, private data storage is closely connected to data sources, then analyses and the most advanced topic (plugins) last 

---

### Data Sources

A data source is - as its name hints - a source of data. Payola needs to know where to get its data for evaluating analyses, etc. - data sources.

##### Creating

When creating a data source, you need to enter a data source name and description, decide whether it's public (then it's visible even to logged out users) and which data fetcher to use.

A data fetcher is a plugin which can communicate with a data source of a specific type. A good example is a SPARQL Endpoint data fetcher. SPARQL is a query language for fetching data and such a data fetcher can work with any SPARQL endpoint.

Select a data fetcher of your choice, fill in the data fetcher's parameters (for example, in SPARQL Endpoint data fetcher's case an `EndpointURL` parameter) and hit the `Create Data Source` button. You have just created your first data source.

##### Editing

Just like with the other entities (analyses, plugins, etc.) use the toolbar at the top of the page to list available data sources (click on the `My Data Sources` button and select `View All`).

You can view all available data sources. If you wish to edit it (e.g. change name or description), click on the Edit button next to its name. You'll be redirected to an edit page which contains a delete button as well. The sharing functionality will be described in the [Sharing section](#sharing).

##### Viewing

When on the Dashboard or listing all available data sources, click on a data source's name to view the data source.

You'll be presented with a neighborhood of an initial vertex.

Such a subgraph can be viewed in many ways. The default one, presented to you, is a simple table. You can change the visualization plugin using the `Change visualization plugin` button. `Circle`, `Gravity` and `Tree` visualizations will display a regular graph using vertices and edges. 

###### TODO - describe what can be done with a graph

The `Column Chart` visualization will display a column bar graph, but works only with graphs with a specific structure. The graph must have one identified vertex, whose edges are of a `rdf:type` URI with identified vertex destinations - this destination then must have exactly two edges, both directed to a literal vertex, one with a string value (name of the column), the second one with a numeric value.

##### Ontology Customization
---
### Groups

You can create user groups to make sharing easier (as described in the next chapter). Imagine you want to share a resource (e.g. an analysis) with a group of co-workers. One approach would be to share it with each one of them, but this can be tedious considering you might want to share something with them every week or every day. Hence there's a possibility to create user groups - in the top toolbar, click on the `User Groups` button and select `Create New`.

Enter the group name (e.g. 'My co-workers') and hit the `Create Group` button. After the group has been created, you can start adding members to the group. To do so, make the `Members` field active and start typing - the suggestion box will offer you users with a matching name. Click on the user to add him or her. If you decide to remove a user, click on the `x` button in front of his or her name. Remember to use the `Save Group` button before leaving the edit page, or all changes made will be lost.

To delete a group, use the `Delete` button at the top-right corner of the page.

![Editing a Group](tree/develop/docs/img/group_edit.png)

---
### <a name="sharing"></a>Sharing
---
### Private Data Storage
---
### Analyses
---
### Plugins

