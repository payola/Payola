<a name="top"></a>

# Installation Guide

## System Requirements

Payola requires a [Scala](http://www.scala-lang.org) environment, which is supported on virtually any platform capable of running Java code (requires JRE 1.6 and higher). Currently supported systems are: Mac OS X 10.7+, Windows 7, Windows Server 2008 R2. While Payola does run on Linux and Unix based platforms as well, our team had limited opportunities to test Payola on various distributions. The system should have at least 1GB of memory dedicated to Payola itself.

Aside from the actual Payola server, you need to have a running [Squeryl-compatible](http://squeryl.org/supported-databases.html) relational database for storing user data, a [Virtuoso](http://virtuoso.openlinksw.com) server for storing personal RDF data and an SMTP server for the plugin approval process. The Virtuoso server needs to be running on the same server as Payola is, or at least share the same file system - when uploading private data, a path to a temporary file is passed to Virtuoso. The SMTP server and the relational database may be running on a different server (this is configurable in the `payola.conf` file as described later on).

To work with Payola, a web browser capable of displaying HTML5 web pages is required. Payola takes an advantage of many HTML5 features - keep your web browser up-to-date all the time. Recommended are the *latest versions* of WebKit-based browsers (e.g. Chrome, Safari), Firefox, Opera, or IE. A 1440x900 or larger display is highly recommended.

## Installation

First of all, clone the Payola git repository: `git://github.com/siroky/Payola.git` to a local folder.

<a name="configuration"></a>
### Configuration 

Payola comes pre-configured to work with default settings of a Virtuoso server and an H2 database installed on the same server as Payola is running (i.e. localhost). To change this configuration, edit `payola/web/shared/src/main/resources/payola.conf` - it’s a regular text file with various key-value options separated by an equal sign (`=`) on each line. Comment lines start with a hash symbol (`#`).

#### Virtuoso Settings

`virtuoso.server` - address of the Virtuoso server

`virtuoso.endpoint.port` - port of the Virtuoso server's SPARQL endpoint

`virtuoso.endpoint.ssl` - enter true if the connection to the Virtuoso SPARQL endpoint should use SSL

`virtuoso.sql.port` - port of the Virtuoso server's SQL database

`virtuoso.sql.user` - SQL database login name

`virtuoso.sql.password` - SQL database login password

#### Relational Database Settings

`database.location` - JDBC URL of the database

`database.user` - database login name

`database.password` - database login password

#### User

`admin.email` - Email of the admin user. A user with this email address also gets created when the initializer project is run. When a user uploads a plugin an email is sent to this address for the admin to approve the plugin, as well.

#### Web

`web.url` - URL of the website, by default `http://localhost:9000`. The URL must start with `http://` or `https://` and mustn't end with a trailing `/`.

`web.mail.noreply` - Email that will be used as a no-reply email address of the web application.

#### Email

`mail.smtp.server` - Mail server.

`mail.smtp.port` - Mail server port.

`mail.smtp.user` - Username.

`mail.smtp.password` - Password.

#### Libraries

`lib.directory` - storage for 3rd-party libraries

#### Plugins Directory

`plugin.directory` - where to store plugins uploaded by users

<a name="compiling"></a>
### Compiling and Running Payola

> **Payola will not start Virtuoso or H2 on its own. You need to configure them and run them by yourself.**

As the cloned repository contains just source code, it is necessary to compile Payola in order to run it. Open a command line (console, terminal) and make `payola` subdirectory the current working directory (e.g. by `cd payola`). Launch SBT (using the `sbt.sh` command or the `sbt.bat` on Windows) ...

>You can edit these commands in a text editor to change the amount of memory given to Payola. By default, Payola uses 1GB of memory (the `-Xmx` argument), 512MB of memory for the [permgen](http://en.wikipedia.org/wiki/Java_virtual_machine#Heap) (the `-XX:MaxPermSize` argument) and 2MB stack size (the `-Xss` argument).

... and enter the following commands:

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

> <a name="drop-create-warning"></a> **Warning:** The `initializer` project drops and recreates all tables in the database - hence all previous data will be lost. Run this project only when installing Payola for the first time or if you want to reset Payola to factory settings.

## Security

Payola allows users to store their own private RDF data using Virtuoso graph groups that are identified by a generated 128-bit UUID (and the H2 database is secured by a username-password combination). Both the Virtuoso server and H2 database allow incoming connections from outside of your network, or localhost, by default.

While a simple guess of another user's group identifier is unlikely (and a brute-force attack on the username-password combination for the relational database is highly noticeable), it is advisable to secure your local Virtuoso storage and your relational database by denying all incoming and outgoing connections outside of localhost, or if on a secure company network, outside of that particular network. This is up to each administrator to correctly set up the server's firewall.

## Launching

To launch Payola, open SBT just like when you were [compiling](#compiling) it and enter these three commands (you do not need to run the `cp` command unless you have modified the source code since the last compilation):

```
> cp
...
> project server
...
> run
```

> **Warning:** Do **not** run the `initializer` project. All users, analyses, data sources, etc. would be lost. (See [this note](#drop-create-warning) for details.)

Once the server is running, enter the following address in your web browser:

><http://localhost:9000/>

Of course, the port may vary depending on your configuration file (see section [Configuration](#configuration) for details).