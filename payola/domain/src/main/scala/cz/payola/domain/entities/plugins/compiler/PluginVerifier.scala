package cz.payola.domain.entities.plugins.compiler

import tools.nsc.plugins.{PluginComponent, Plugin}
import tools.nsc.{Phase, Global}
import scala.tools.nsc.transform.Transform
import cz.payola.domain.entities

/** A plugin verifier compiler plugin. */
class PluginVerifier(val global: Global) extends Plugin
{
    val name = "plugin-verifier"

    val description = "Verifies that the compiled unit is a plugin."

    val components = List[PluginComponent](PluginVerifierComponent, PluginNameTransformerComponent)

    var pluginPackageName: Option[String] = None

    var pluginClassName: Option[String] = None

    /** A component that verifies the Plugin. */
    private object PluginVerifierComponent extends PluginComponent
    {
        val global = PluginVerifier.this.global

        import global._

        val runsAfter = List[String]("refchecks")

        val phaseName = "plugin-verifier"

        def newPhase(prev: Phase): Phase = new PluginVerifierPhase(prev)

        /** A plugin verifier phase. */
        private class PluginVerifierPhase(prev: Phase) extends StdPhase(prev)
        {
            val pluginParentClassName = classOf[entities.Plugin].getName

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
                        if (!classDef.impl.parents.exists(tree => tree.symbol.fullName == pluginParentClassName)) {
                            error("The plugin must extend the %s class.".format(pluginParentClassName))
                        }

                        // Verify that the plugin has valid constructors.
                        val constructors = classDef.impl.body.filter(_.hasSymbolWhich(_.isConstructor)).collect {
                            case constructorDefDef: DefDef => constructorDefDef
                        }
                        if (constructors.length != 2) {
                            error("The plugin must have two constructors.")
                        }

                        // Verify that the plugin has a parameterless constructor.
                        if (!constructors.exists(_.vparamss.flatten.isEmpty)) {
                            error("The plugin must have a parameterless constructor.")
                        }

                        // Verify that the plugin has the setter constructor.
                        constructors.find(_.vparamss.flatten.length == 4) match {
                            case Some(constructor) => {
                                val parameterTypes = constructor.vparamss.flatten.map(_.tpt)
                                val parameterTypesAreValid = parameterTypes.map(_.symbol.fullName) match {
                                    case List("java.lang.String", "scala.Int", _, "java.lang.String") => {
                                        println(parameterTypes(2).toString)
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

    /** A component that makes the plugin name unique. */
    private object PluginNameTransformerComponent extends PluginComponent with Transform
    {
        val global = PluginVerifier.this.global

        import global._

        val runsAfter = List[String]("plugin-verifier")

        val phaseName = "plugin-name-transformer"

        def newTransformer(unit: CompilationUnit) = new TemplateTransformer

        class TemplateTransformer extends Transformer
        {
            override def transform(tree: Tree): Tree = {
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
