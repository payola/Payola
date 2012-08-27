<a name="top"></a>

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

<a name="top"></a>

# Setting up Payola
## System Requirements

Payola requires a [Scala](http://www.scala-lang.org) environment, which is supported on virtually any platform capable of running Java code - both Unix and Windows-based systems are fully supported. The system should have at least 1GB of memory dedicated to Payola itself.

Aside from the actual Payola server, you need to have a running [Squeryl-compatible](http://squeryl.org) relational database for storing user data, a [Virtuoso](http://virtuoso.openlinksw.com) server for storing personal RDF data and a SMTP server for the plugin approval process. Neither of these need to necessarily be running on the same system as Payola itself (this is configurable in the `payola.conf` file as described later on).

To work with Payola, a web browser capable of displaying HTML5 web pages is required. Payola takes advantage of many HTML5 features - keep your web browser up-to-date all the time. Recommended are the *latest versions* of WebKit-based browsers (e.g. Chrome, Safari), Firefox, Opera, or IE. A 1440x900 or larger display is highly recommended.

## Installation Guide

You need to have a working Scala environment with [SBT (Scala Build Tool)](https://github.com/harrah/xsbt/wiki/) installed to run Payola. Clone Payola git repository: `git://github.com/siroky/Payola.git` to a local folder.

### <a name="configuration"></a>Configuration 

Payola comes pre-configured to work with default settings of a Virtuoso server and an H2 database installed on the same server as Payola is running (i.e. localhost). To change this configuration, edit `payola/web/shared/src/main/resources/payola.conf` - it’s a regular text file with various key-value options separated by an equal sign (`=`) on each line. Comment lines start with a hash symbol (`#`).

> **Virtuoso Settings**

> *virtuoso.server* - address of the Virtuoso server

> *virtuoso.endpoint.port* - port of the Virtuoso server's SPARQL endpoint

> *virtuoso.endpoint.ssl* - enter true if the connection to the Virtuoso SPARQL endpoint should use SSL

> *virtuoso.sql.port* - port of the Virtuoso server's SQL database

> *virtuoso.sql.user* - SQL database login name

> *virtuoso.sql.password* - SQL database login password

> **Relational Database Settings**

> *database.location* - JDBC URL of the database

> *database.user* - database login name

> *database.password* - database login password

> **User**

> *admin.email* - Email of the admin user. A user with this email address also gets created when the initializer project is run.

> **Web**

> *web.url* - URL of the website, by default `http://localhost:9000`. The URL must start with `http://` or `https://` and mustn't end with a trailing `/`.

> *web.mail.noreply* - Email that will be used as a no-reply email of the web application.

> **Email**

> *mail.smtp.server* - Mail server.

> *mail.smtp.port* - Mail server port.

> *mail.smtp.user* - Username.

> *mail.smtp.password* - Password.

> **Libraries**

> *lib.directory* - storage for 3rd-party libraries

> **Plugins Directory**

> *plugin.directory* - where to store plugins uploaded by users

**Payola will not start Virtuoso or H2 on its own. You need to configure it and run by yourself.**

<a name="compiling"></a>
### Compiling and Running Payola

As the cloned repository contains just source code, it is necessary to compile Payola in order to run it. To do so, you need to have SBT installed as noted above. Open a command line (console, terminal) and make `payola` subdirectory the current working directory (e.g. by `cd payola`). Launch SBT (most likely using the `sbt` command) and enter the following commands:

<a name="run-initializer"></a>
```
> cp
...
> project initializer
> run
...
> project server
> run
```

Voilà! Your Payola server is running. The `initializer` project sets up your database to include an admin user (login `admin@payola.cz`, password `payola!`), a sample analysis and some data sources. You can, of course, remove those and create your own later.

![Installing and running Payola](https://raw.github.com/siroky/Payola/develop/docs/img/installscreen.png)

> <a name="drop-create-warning"></a> **WARNING:** The `initializer` project drop-creates all required tables - hence all previous data will be lost. Run this project only when installing Payola or if you want to reset Payola to factory settings.

### Security

Payola allows users to store their own private RDF data using Virtuoso graph groups that are identified by a generated 128-bit UUID (and the H2 database is secured by a username-password combination). Both the Virtuoso server and H2 database allow incoming connections from outside of your network, or localhost, by default.

While a simple guess of another user's group identifier is unlikely (and a brute-force attack on the username-password combination for the relational database is highly noticeable), it is advisable to secure your local Virtuoso storage and your relational database by denying all incoming and outgoing connections outside of localhost, or if on a secure company network, outside of that particular network. This is up to each administrator to correctly set up the server's firewall.

---
# Using Payola

## Launching

To launch Payola, open SBT just like when you were [compiling](#compiling) it and enter these three commands (you do not need to run the `cp` command unless you have modified the source code since the last compilation):

```
> cp
...
> project server
...
> run
```

> *Warning:* Do **not** run the `initializer` project. All users, analyses, data sources, etc. would be lost. (See [this note](#drop-create-warning) for details.)

Once the server is running, enter the following address in your web browser:

><http://localhost:9000/>

Of course, the port may be different depending on your configuration file (see section [Configuration](#configuration) for details).
