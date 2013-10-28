package cz.payola.data

import cz.payola.domain._
import cz.payola.domain.entities.plugins.PluginClassLoader
import cz.payola.domain.entities.plugins.compiler.PluginCompiler
import cz.payola.data.squeryl.SquerylDataContextComponent
import cz.payola.data.virtuoso.VirtuosoStorage

abstract class TestDataContextComponent(name: String, trace: Boolean = false)
    extends SquerylDataContextComponent
    with RdfStorageComponent
    with PluginCompilerComponent
{
    val db = "jdbc:h2:tcp://localhost/~/h2/payola-test-%s%s".format(name, if (trace) ";TRACE_LEVEL_SYSTEM_OUT=3" else "")
    println("DB for " + name + " on: " + db)

    lazy val schema = new Schema(
        db,
        "sa",
        ""
    )

    lazy val rdfStorage = VirtuosoStorage.virtuosoLocalhost

    lazy val pluginCompiler = new PluginCompiler(new java.io.File(""), new java.io.File(""))

    lazy val pluginClassLoader = new PluginClassLoader(new java.io.File(""), getClass.getClassLoader)
}
