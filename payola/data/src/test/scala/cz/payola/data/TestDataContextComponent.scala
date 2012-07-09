package cz.payola.data

import cz.payola.data.squeryl.SquerylDataContextComponent
import cz.payola.domain.virtuoso.VirtuosoStorage
import cz.payola.domain.RdfStorageComponent

trait TestDataContextComponent extends SquerylDataContextComponent with RdfStorageComponent
{
    lazy val schema = new Schema("jdbc:h2:tcp://localhost/~/h2/payola-test", "sa", "")

    lazy val rdfStorage = new VirtuosoStorage()
}
