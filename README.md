##System Requirements

Payola requires a Scala# environment, which is supported on virtually all platforms capable of running Java code - both Unix and Windows-based systems are capable of running Payola. The system should have at least 1GB of memory dedicated to Payola itself.

Aside from the actual Payola server, you need to be capable of running any [Squeryl-compatible](http://squeryl.org) relational database for storing user data and a Virtuoso# server for storing personal RDF data. Neither of those need to necessarily be running on the same system as Payola itself (this is configurable in the `payola.conf` file as described later on).

##Installation Guide

You need to have a working Scala environment to install Payola with SBT (Scala Build Tool)# installed. Clone Payola git repository: `git://github.com/siroky/Payola.git` to a local folder.

###Configuring

Payola comes configured to work with default settings of a Virtuoso server and a H2 database installed on the same server as Payola is running (i.e. localhost). To change this configuration, edit  payola/web/shared/src/main/resources/payola.conf  - it’s a regular text file with various options on each line. Comment lines start with a hash symbol (#).

###Compiling and Running Payola

As you clone just source codes from the git repository, it’s necessary to compile Payola. To do so, you need to have SBT installed as noted above. Open command line, make  payola  subdirectory current working subdirectory. Launch SBT (probably using the  sbt  command