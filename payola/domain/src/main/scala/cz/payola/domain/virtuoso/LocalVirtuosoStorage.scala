package cz.payola.domain.virtuoso

/** Creates a new VirtuosoStorage object with a local installation.
  *
  */
object LocalVirtuosoStorage extends VirtuosoStorage("localhost", 8890, false, "dba", "dba") {
    // TODO - load from config file
}
