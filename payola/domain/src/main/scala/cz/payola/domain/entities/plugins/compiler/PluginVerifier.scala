package cz.payola.domain.entities.plugins.compiler

import tools.nsc.plugins.{PluginComponent, Plugin}
import tools.nsc.{Phase, Global}
import scala.tools.nsc.transform.Transform

/**
  * A compiler plugin verifying that the compilation unit is actually a correctly defined analytical plugin. It also
  * makes sure that the plugin class name is unique.
  */
class PluginVerifier(val global: Global) extends Plugin
{
    val name = "plugin-verifier"

    val description = "Verifies that the compiled unit is a plugin."

    val components = List[PluginComponent](PluginVerifierComponent, PluginNameTransformerComponent)

    var pluginName: Option[String] = None

    var pluginPackageName: Option[String] = None

    var pluginClassName: Option[String] = None

    /**
      * A component that verifies correctness of the compilation unit.
      */
    private object PluginVerifierComponent extends PluginComponent
    {
        val global = PluginVerifier.this.global

        import global._

        val runsAfter = List[String]("refchecks")

        val phaseName = "plugin-verifier"

        def newPhase(prev: Phase): Phase = new StdPhase(prev)
        {
            val pluginParentClassNames = List(
                classOf[cz.payola.domain.entities.Plugin],
                classOf[cz.payola.domain.entities.plugins.concrete.DataFetcher],
                classOf[cz.payola.domain.entities.plugins.concrete.SparqlQuery]
            ).map(_.getName)

            def apply(unit: CompilationUnit) {
                // Verify that the unit contains one package definition.
                if (!unit.body.isInstanceOf[PackageDef]) {
                    error("The plugin file must contain a package definition.")
                }

                val packageDef = unit.body.asInstanceOf[PackageDef]
                pluginPackageName = Some(packageDef.symbol.fullName)

                // Verify that the package definition contains one class definition.
                packageDef.stats match {
                    case List(classDef: ClassDef) => {
                        // Verify that the class definition isn't an object.
                        if (classDef.symbol.isModuleClass) {
                            error("The plugin must be a class, not an object.")
                        }

                        // Verify that the plugin extends the plugin class.
                        if (!classDef.impl.parents.exists(t => pluginParentClassNames.exists(_ == t.symbol.fullName))) {
                            error("The plugin must extend one of the following classes: %s.".format(
                                pluginParentClassNames.mkString(", ")
                            ))
                        }

                        // Verify that the plugin has valid constructors.
                        val constructors = classDef.impl.body.filter(_.hasSymbolWhich(_.isConstructor)).collect {
                            case constructorDefDef: DefDef => constructorDefDef
                        }
                        if (constructors.length != 2) {
                            error("The plugin must have two constructors.")
                        }

                        // Verify that the plugin has a parameterless constructor.
                        val constructor = constructors.find(_.vparamss.flatten.isEmpty)
                        if (constructor.isEmpty) {
                            error("The plugin must have a parameterless constructor.")
                        }

                        // Retrieve the plugin name from the parameterless constructor.
                        constructor.get.rhs match {
                            case Block(Apply(_, Literal(Constant(name: String)) :: _) :: _, _) => {
                                 pluginName = Some(name)
                            }
                            case _ => error("The parameterless constructor doesn't specify the plugin name.")
                        }

                        // Verify that the plugin has the setter constructor.
                        constructors.find(_.vparamss.flatten.length == 4) match {
                            case Some(constructor) => {
                                val parameterTypes = constructor.vparamss.flatten.map(_.tpt)
                                val parameterTypesAreValid = parameterTypes.map(_.symbol.fullName) match {
                                    case List("java.lang.String", "scala.Int", _, "java.lang.String") => {
                                        parameterTypes(2).toString == "scala.collection.immutable.Seq[" +
                                            "cz.payola.domain.entities.plugins.Parameter[_]]"
                                    }
                                    case _ => false
                                }
                                if (!parameterTypesAreValid) {
                                    error("The 'setter' constructor parameter types are invalid.")
                                }
                            }
                            case None => error("The package must contain the 'setter' constructor.")
                        }

                        pluginClassName = Some(classDef.symbol.fullName)
                    }
                    case _ => error("The package must contain one plugin class definition.")
                }
            }
        }

    }

    /**
      * A component that makes the plugin name unique.
      */
    private object PluginNameTransformerComponent extends PluginComponent with Transform
    {
        val global = PluginVerifier.this.global

        import global._

        val runsAfter = List[String]("plugin-verifier")

        val phaseName = "plugin-name-transformer"

        def newTransformer(unit: CompilationUnit) = new Transformer
        {
            override def transform(tree: Tree): Tree = {
                // If the tree matches the plugin class definition, replace the plugin class name with an unique
                // name. Otherwise leave the tree as it is.
                tree match {
                    case cd@ClassDef(mods, _, tparams, impl) if pluginClassName.exists(_ == cd.symbol.fullName) => {
                        val uniqueId = java.util.UUID.randomUUID.toString.replace("-", "_")
                        val uniquePluginName = (("Plugin_" + uniqueId): Name).toTypeName
                        val copy = treeCopy.ClassDef(cd, cd.mods, uniquePluginName, cd.tparams, cd.impl)
                        copy.symbol.name = uniquePluginName

                        pluginClassName = Some(pluginPackageName.map(_ + ".").getOrElse("") + uniquePluginName)
                        copy
                    }
                    case _ => super.transform(tree)
                }
            }
        }

    }

}
