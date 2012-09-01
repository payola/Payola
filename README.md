<a name="top"></a>

![Payola logo (credits Martin Mraz)](https://raw.github.com/siroky/Payola/develop/docs/img/logo_medium.png)

Payola is an HTML5 web application which lets you work with graph data in a completely new way. You can visualize [Linked Data](http://linkeddata.org/) using several preinstalled plugins as graphs, tables, etc. This also means, that you no longer need [Pubby](http://www4.wiwiss.fu-berlin.de/pubby/) to browse through a Linked Data storage (via its [SPARQL](http://www.w3.org/TR/rdf-sparql-query/) endpoint). Moreover, you can create an analysis and run it against a set of SPARQL endpoints without deep knowledge of SPARQL language itself. Analysis results are processed and visualized using the embedded visualization plugins.

For more information about functions and features of the Payola, please refer to the [User Guide](https://github.com/siroky/Payola/develop/docs/user_guide.md) where all of them are described. If you're interested in forking or modifying the Payola, please look at the [Developer Guide](https://github.com/siroky/Payola/develop/docs/developer_guide.md)

![Payola overview](https://raw.github.com/siroky/Payola/develop/docs/img/screenshots/payola_overview.png)

## Should you be interested in Payola?

If you work with RDF data on a daily basis, yes, you should. Payola may help you simplify or automatize some tasks. Also the visualizations may be used when describing linked data to people who are not that familiar with them.

If you just work with RDF data - it will help you access your data sources, browse them, visualize them and analyze the data. We've tried to minimize the need to learn the SPARQL language to work with linked data.

If you are a developer, you can also contribute with your own analytical plugin and share it to the users of your Payola installation. You can also come up with your own visualization plugin and compile your own version of Payola.

## What are the most common use cases?

- Private tool to work with RDF data.
- Company tool to work with internal RDF data.
- Public website with community around a specific type of RDF data.
- Company/government website to present RDF data to the public.

## Installation & Running

Prerequisities of the Payola are installed and running local Virtuoso server and H2 database. The following text assumes, that both are installed with default settings. It's just a head start, for complete description of installation and configuration, see the [Installation Guide](https://github.com/siroky/Payola/develop/docs/installation_guide.md)

First of all, clone the Payola git repository: `git://github.com/siroky/Payola.git` to a local folder. As the cloned repository contains just source code, it is necessary to compile Payola in order to run it. Open a command line (console, terminal) and make payola subdirectory the current working directory (e.g. by `cd payola`). Launch SBT (using the sbt.sh command or the sbt.bat on Windows) and enter the following commands:

```
> cp
...
> project initializer
> run
...
> project server
> run
```

Voilà! Your Payola server is running. The initializer project sets up your database to include an admin user (login admin@payola.cz, password payola!), a sample analysis and some data sources. You can, of course, remove those and create your own later.