package cz.payola.data

import cz.payola.data.squeryl.SquerylDataContextComponent
import cz.payola.domain.virtuoso.VirtuosoStorage
import cz.payola.domain.RdfStorageComponent

abstract class TestDataContextComponent(name: String, trace: Boolean = false)
    extends SquerylDataContextComponent with RdfStorageComponent
{
    lazy val schema = new Schema(
        "jdbc:h2:tcp://localhost/~/h2/payola-test-%s%s".format(name, if (trace) ";TRACE_LEVEL_SYSTEM_OUT=3" else ""),
        "sa",
        ""
    )

    lazy val rdfStorage = new VirtuosoStorage()
}
