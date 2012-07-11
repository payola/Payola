package cz.payola.data

import cz.payola.data.squeryl.SquerylDataContextComponent
import cz.payola.domain.virtuoso.VirtuosoStorage
import cz.payola.domain.RdfStorageComponent

abstract class TestDataContextComponent(name: String) extends SquerylDataContextComponent with RdfStorageComponent
{
    private val db = "jdbc:h2:tcp://localhost/~/h2/payola-test-" + name
    println("H2 db for " + name + ": " + db)
    
    lazy val schema = new Schema(db, "sa", "")

    lazy val rdfStorage = new VirtuosoStorage()
}
