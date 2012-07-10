package cz.payola.data.squeryl

import org.squeryl._
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.adapters.H2Adapter
import cz.payola.data._
import cz.payola.data.squeryl.entities._
import cz.payola.data.squeryl.entities.plugins._
import cz.payola.data.squeryl.entities.plugins.parameters._
import cz.payola.data.squeryl.entities.analyses.PluginInstanceBinding
import cz.payola.data.squeryl.entities.privileges.PrivilegeDbRepresentation

trait SchemaComponent
{
    self: SquerylDataContextComponent =>

    val schema: Schema

    /**
      * Definition of the Payola schema in Squeryl.
      * @see http://www.squeryl.org
      * @param databaseURL A database url of the form 'jdbc:subprotocol:subname'.
      * @param userName The database user on whose behalf the connection is being made.
      * @param password The user's password.
      */
    class Schema(val databaseURL: String, val userName: String, val password: String) extends org.squeryl.Schema
    {
        // Initialize the session factory.
        java.lang.Class.forName("org.h2.Driver")

        // TODO just hypothetically - what about multiple sessions?
        SessionFactory.concreteFactory = Some(() => DataException.wrap {
            Session.create(java.sql.DriverManager.getConnection(databaseURL, userName, password), new H2Adapter)
        })

        /** Table of [[cz.payola.data.squeryl.entities.User]]s */
        val users = table[User]("users")

        /** Table of [[cz.payola.data.squeryl.entities.Group]]s */
        val groups = table[Group]("groups")

        /** Table of [[cz.payola.data.squeryl.entities.Analysis]] items */
        val analyses = table[Analysis]("analyses")

        /** Table of [[cz.payola.data.squeryl.entities.analyses.PluginDbRepresentation]]s */
        val plugins = table[PluginDbRepresentation]("plugins")

        /** Table of [[cz.payola.data.squeryl.entities.analyses.PluginInstance]]s */
        val pluginInstances = table[PluginInstance]("pluginInstances")

        /** Table of  ([[cz.payola.data.squeryl.entities.User]]s)s */
        val booleanParameters = table[BooleanParameter]("booleanParameters")

        /** Table of [[cz.payola.data.squeryl.entities.plugins.parameters.BooleanParameterValues]]s */
        val booleanParameterValues = table[BooleanParameterValue]("booleanParameterValues")

        /** Table of [[cz.payola.data.squeryl.entities.plugins.parameters.FloatParameter]]s */
        val floatParameters = table[FloatParameter]("floatParameters")

        /** Table of [[cz.payola.data.squeryl.entities.plugins.parameters.FloatParameterValue]]s */
        val floatParameterValues = table[FloatParameterValue]("floatParameterValues")

        /** Table of [[cz.payola.data.squeryl.entities.plugins.parameters.IntParameter]]s */
        val intParameters = table[IntParameter]("intParameters")

        /** Table of [[cz.payola.data.squeryl.entities.plugins.parameters.IntParameterValue]]s */
        val intParameterValues = table[IntParameterValue]("intParameterValues")

        /** Table of [[cz.payola.data.squeryl.entities.plugins.parameters.StringParameter]]s */
        val stringParameters = table[StringParameter]("stringParameters")

        /** Table of [[cz.payola.data.squeryl.entities.plugins.parameters.StringParameterValue]]s */
        val stringParameterValues = table[StringParameterValue]("stringParameterValues")

        /** Table of [[cz.payola.data.squeryl.entities.analyses.PluginInstanceBinding]]s */
        val pluginInstanceBindings = table[PluginInstanceBinding]("pluginInstanceBindings")

        /** Table of [[cz.payola.data.squeryl.entities.analyses.DataSource]]s */
        val dataSources = table[DataSource]("dataSources")

        /** Table of [[cz.payola.data.squeryl.entities.privileges.PrivilegeDbRepresentation]]s */
        val privileges = table[PrivilegeDbRepresentation]("privileges")

        /**
          * Relation that associates members ([[cz.payola.data.squeryl.entities.User]]s)
          * to [[cz.payola.data.squeryl.entities.Group]]s
          */
        lazy val groupMembership = manyToManyRelation(users, groups).via[GroupMembership](
            (u, g, gm) => (gm.memberId === u.id, g.id === gm.groupId))

        /**
          * Relation that associates [[cz.payola.data.squeryl.entities.Group]]s to their owners
          * ([[cz.payola.data.squeryl.entities.User]]s)
          */
        lazy val groupOwnership = oneToManyRelation(users, groups).via(
            (u, g) => u.id === g.ownerId)

        /**
          * Relation that associates [[cz.payola.data.squeryl.entities.Analysis]] to its owner
          * ([[cz.payola.data.squeryl.entities.User]]s)
          */
        lazy val analysisOwnership = oneToManyRelation(users, analyses).via(
            (u, a) => u.id === a.ownerId.getOrElse(EMPTY_ID).toString)

        /**
          * Relation that associates [[cz.payola.data.squeryl.entities.PluginDbRepresentation]] to its owner
          * ([[cz.payola.data.squeryl.entities.User]]s)
          */
        lazy val pluginOwnership = oneToManyRelation(users, plugins).via(
            (u, p) => u.id === p.ownerId.getOrElse(EMPTY_ID).toString)

        /**
          * Relation that associates [[cz.payola.data.squeryl.entities.analyses.DataSource]] to its owner
          * ([[cz.payola.data.squeryl.entities.User]]s)
          */
        lazy val dataSourceOwnership = oneToManyRelation(users, dataSources).via(
            (u, ds) => u.id === ds.ownerId.getOrElse(EMPTY_ID).toString)

        /**
          * Relation that associates [[cz.payola.data.squeryl.entities.analyses.PluginDbRepresentation]] to a
          * [[cz.payola.data.squeryl.entities.PluginDbRepresentation]]
          */
        lazy val pluginsPluginInstances = oneToManyRelation(plugins, pluginInstances).via(
            (p, pi) => p.id === pi.pluginId)

        /**
          * Relation that associates [[cz.payola.data.squeryl.entities.analyses.PluginDbRepresentation]] to an
          * [[cz.payola.data.squeryl.entities.Analysis]]
          */
        lazy val analysesPluginInstances = oneToManyRelation(analyses, pluginInstances).via(
            (a, pi) => a.id === pi.analysisId)

        /**
          * Relation that associates [[cz.payola.data.squeryl.entities.analyses.DataSource]]s to
          * a [[cz.payola.data.squeryl.entities.analyses.PluginDbRepresentation]]s
          */
        lazy val pluginsDataSources = oneToManyRelation(plugins, dataSources).via(
            (p, ds) => p.id === ds.pluginId)

        /**
          * Relation that associates [[cz.payola.data.squeryl.entities.analyses.PluginInstanceBinding]]s to a
          * [[cz.payola.data.squeryl.entities.Analysis]]
          */
        lazy val analysesPluginInstancesBindings = oneToManyRelation(analyses, pluginInstanceBindings).via(
            (a, pib) => a.id === pib.analysisId)

        /**
          * Relation that associates [[cz.payola.data.squeryl.entities.analyses.PluginInstanceBinding]]s to a
          * [[cz.payola.data.squeryl.entities.analyses.PluginInstance]] as a source
          */
        lazy val bindingsOfSourcePluginInstances = oneToManyRelation(pluginInstances, pluginInstanceBindings).via(
            (pi, b) => pi.id === b.sourcePluginInstanceId)

        /**
          * Relation that associates [[cz.payola.data.squeryl.entities.analyses.PluginInstanceBinding]]s to a
          * [[cz.payola.data.squeryl.entities.analyses.PluginInstance]] as a target
          */
        lazy val bindingsOfTargetPluginInstances = oneToManyRelation(pluginInstances, pluginInstanceBindings).via(
            (pi, b) => pi.id === b.targetPluginInstanceId)

        /**
          * Relation that associates [[cz.payola.data.squeryl.entities.plugins.parameters.BooleanParameterValues]]s to a
          * [[cz.payola.data.squeryl.entities.plugins.parameters.BooleanParameter]]
          */
        lazy val valuesOfBooleanParameters = oneToManyRelation(booleanParameters, booleanParameterValues).via(
            (bp, bpv) => bp.id === bpv.parameterId)

        /**
          * Relation that associates [[cz.payola.data.squeryl.entities.plugins.parameters.FloatParameterValue]]s to a
          * [[cz.payola.data.squeryl.entities.plugins.parameters.FloatParameter]]
          */
        lazy val valuesOfFloatParameters = oneToManyRelation(floatParameters, floatParameterValues).via(
            (fp, fpv) => fp.id === fpv.parameterId)

        /**
          * Relation that associates [[cz.payola.data.squeryl.entities.plugins.parameters.IntParameterValue]]s to an
          * [[cz.payola.data.squeryl.entities.plugins.parameters.IntParameter]]
          */
        lazy val valuesOfIntParameters = oneToManyRelation(intParameters, intParameterValues).via(
            (ip, ipv) => ip.id === ipv.parameterId)

        /**
          * Relation that associates [[cz.payola.data.squeryl.entities.plugins.parameters.StringParameterValues]] to
          * a [[cz.payola.data.squeryl.entities.plugins.parameters.StringParameter]]
          */
        lazy val valuesOfStringParameters = oneToManyRelation(stringParameters, stringParameterValues).via(
            (sp, spv) => sp.id === spv.parameterId)

        /**
          * Relation that associates [[cz.payola.data.squeryl.entities.plugins.parameters.BooleanParameter]] to a
          * [[cz.payola.data.squeryl.entities.analyses.PluginDbRepresentation]]
          */
        lazy val booleanParametersOfPlugins = oneToManyRelation(plugins, booleanParameters).via(
            (p, bp) => p.id === bp.pluginId)

        /**
          * Relation that associates [[cz.payola.data.squeryl.entities.plugins.parameters.FloatParameter]]s to a
          * [[cz.payola.data.squeryl.entities.analyses.PluginDbRepresentation]]
          */
        lazy val floatParametersOfPlugins = oneToManyRelation(plugins, floatParameters).via(
            (p, fp) => p.id === fp.pluginId)

        /**
          * Relation that associates [[cz.payola.data.squeryl.entities.plugins.parameters.IntParameter]]s to a
          * [[cz.payola.data.squeryl.entities.analyses.PluginDbRepresentation]]
          */
        lazy val intParametersOfPlugins = oneToManyRelation(plugins, intParameters).via(
            (p, ip) => p.id === ip.pluginId)

        /**
          * Relation that associates [[cz.payola.data.squeryl.entities.plugins.parameters.StringParameter]]s to a
          * [[cz.payola.data.squeryl.entities.analyses.PluginDbRepresentation]]
          */
        lazy val stringParametersOfPlugins = oneToManyRelation(plugins, stringParameters).via(
            (p, sp) => p.id === sp.pluginId)

        /**
          * Relation that associates [[cz.payola.data.squeryl.entities.plugins.parameters.BooleanParameterValues]]s
          * to a [[cz.payola.data.squeryl.entities.plugins.parameters.BooleanParameter]]
          */
        lazy val booleanParameterValuesOfPluginInstances = oneToManyRelation(pluginInstances, booleanParameterValues).via(
            (pi, bpv) => pi.id === bpv.pluginInstanceId.getOrElse(EMPTY_ID).toString)

        /**
          * Relation that associates [[cz.payola.data.squeryl.entities.plugins.parameters.FloatParameterValue]]s to a
          * [[cz.payola.data.squeryl.entities.plugins.parameters.FloatParameter]]
          */
        lazy val floatParameterValuesOfPluginInstances = oneToManyRelation(pluginInstances, floatParameterValues).via(
            (pi, fpv) => pi.id === fpv.pluginInstanceId.getOrElse(EMPTY_ID).toString)

        /**
          * Relation that associates [[cz.payola.data.squeryl.entities.plugins.parameters.IntParameterValue]]s to an
          * [[cz.payola.data.squeryl.entities.plugins.parameters.IntParameter]]
          */
        lazy val intParameterValuesOfPluginInstances = oneToManyRelation(pluginInstances, intParameterValues).via(
            (pi, ipv) => pi.id === ipv.pluginInstanceId.getOrElse(EMPTY_ID).toString)

        /**
          * Relation that associates [[cz.payola.data.squeryl.entities.plugins.parameters.StringParameterValues]]s to
          * a [[cz.payola.data.squeryl.entities.plugins.parameters.StringParameter]]
          */
        lazy val stringParameterValuesOfPluginInstances = oneToManyRelation(pluginInstances, stringParameterValues).via(
            (pi, spv) => pi.id === spv.pluginInstanceId.getOrElse(EMPTY_ID).toString)

        /**
          * Relation that associates [[cz.payola.data.squeryl.entities.plugins.parameters.BooleanParameterValues]]s
          * to a [[cz.payola.data.squeryl.entities.analyses.DataSource]]
          */
        lazy val booleanParameterValuesOfDataSources = oneToManyRelation(dataSources, booleanParameterValues).via(
            (ds, bpv) => ds.id === bpv.dataSourceId.getOrElse(EMPTY_ID).toString)

        /**
          * Relation that associates [[cz.payola.data.squeryl.entities.plugins.parameters.FloatParameter]]s values to
          * a [[cz.payola.data.squeryl.entities.analyses.DataSource]]
          */
        lazy val floatParameterValuesOfDataSources = oneToManyRelation(dataSources, floatParameterValues).via(
            (ds, fpv) => ds.id === fpv.dataSourceId.getOrElse(EMPTY_ID).toString)

        /**
          * Relation that associates [[cz.payola.data.squeryl.entities.plugins.parameters.IntParameter]] to a [[cz
          * .payola
          * .data.squeryl.entities.analyses.DataSource]]
          */
        lazy val intParameterValuesOfDataSources = oneToManyRelation(dataSources, intParameterValues).via(
            (ds, ipv) => ds.id === ipv.dataSourceId.getOrElse(EMPTY_ID).toString)

        /**
          * Relation that associates [[cz.payola.data.squeryl.entities.plugins.parameters.StringParameter]]s values
          * to a [[cz.payola.data.squeryl.entities.analyses.DataSource]]
          */
        lazy val stringParameterValuesOfDataSources = oneToManyRelation(dataSources, stringParameterValues).via(
            (ds, spv) => ds.id === spv.dataSourceId.getOrElse(EMPTY_ID).toString)

        /**
          * This value id used in 1:N relations when item on N side of this relation has not filled id of related
          * item (it means the id is None)
          */
        private val EMPTY_ID: String = "00000000-0000-0000-0000-000000000000"

        /**
          * Drops the current schema and recreates it.
          */
        def recreate() {
            declareKeys()
            transaction {
                drop
                create
            }
        }

        /**
          * All the entities have to be created using custom factories in order to inject their dependencies via the
          * implicit constructor parameter.
          */
        override def callbacks = Seq(
            factoryFor(users) is { new User("", "", "", "") },
            factoryFor(groups) is { new Group("", "", null) },
            factoryFor(analyses) is { new Analysis("", "", None) },
            factoryFor(plugins) is { new PluginDbRepresentation("", "", "", 0, None) },
            factoryFor(pluginInstances) is { new PluginInstance("", null, Nil, "") },
            factoryFor(booleanParameters) is { new BooleanParameter("", "", false) },
            factoryFor(booleanParameterValues) is { new BooleanParameterValue("", null, false)  },
            factoryFor(floatParameters) is { new FloatParameter("", "", 0) },
            factoryFor(floatParameterValues) is { new FloatParameterValue("", null, 0) },
            factoryFor(intParameters) is { new IntParameter("", "", 0) },
            factoryFor(intParameterValues) is { new IntParameterValue("", null, 0) },
            factoryFor(stringParameters) is { new StringParameter("", "", "") },
            factoryFor(stringParameterValues) is { new StringParameterValue("", null, "") },
            factoryFor(dataSources) is { new DataSource("", "", None, null, Nil) },
            factoryFor(privileges) is { new PrivilegeDbRepresentation("", "", "", "", "", "", "") }
        )

        /**
          * Declares primary, unique and foreign keys and constrains.
          */
        private def declareKeys() {
            on(users)(user => declare(user.id is (primaryKey), user.name is (unique)))
            on(groups)(group => declare(group.id is (primaryKey), group.name is (unique)))
            on(analyses)(analysis => declare(analysis.id is (primaryKey), analysis.name is (unique)))
            on(plugins)(plugin => declare(plugin.id is (primaryKey), plugin.name is (unique)))
            on(pluginInstances)(instance => declare(instance.id is (primaryKey)))
            on(booleanParameters)(param => declare(param.id is (primaryKey)))
            on(booleanParameterValues)(param => declare(param.id is (primaryKey)))
            on(floatParameters)(param => declare(param.id is (primaryKey)))
            on(floatParameterValues)(param => declare(param.id is (primaryKey)))
            on(intParameters)(param => declare(param.id is (primaryKey)))
            on(intParameterValues)(param => declare(param.id is (primaryKey)))
            on(stringParameters)(param => declare(param.id is (primaryKey)))
            on(stringParameterValues)(param => declare(param.id is (primaryKey)))
            on(dataSources)(ds => declare(ds.id is (primaryKey)))
            on(privileges)(p =>
                declare(
                    p.id is (primaryKey),
                    columns(p.granteeId, p.privilegeClass, p.objectId) are (unique)
                ))

            // When a PluginDbRepresentation is deleted, all of the its instances and data sources will get deleted.
            pluginsPluginInstances.foreignKeyDeclaration.constrainReference(onDelete cascade)
            pluginsDataSources.foreignKeyDeclaration.constrainReference(onDelete cascade)

            // When an Analysis is deleted, all of the its plugin instances will get deleted.
            analysesPluginInstances.foreignKeyDeclaration.constrainReference(onDelete cascade)
            analysesPluginInstancesBindings.foreignKeyDeclaration.constrainReference(onDelete cascade)

            // When a Parameter is deleted, all of the its parameterValues will get deleted.
            valuesOfBooleanParameters.foreignKeyDeclaration.constrainReference(onDelete cascade)
            valuesOfFloatParameters.foreignKeyDeclaration.constrainReference(onDelete cascade)
            valuesOfIntParameters.foreignKeyDeclaration.constrainReference(onDelete cascade)
            valuesOfStringParameters.foreignKeyDeclaration.constrainReference(onDelete cascade)

            // When a PluginDbRepresentation is deleted, all of the its Parameters will get deleted.
            booleanParametersOfPlugins.foreignKeyDeclaration.constrainReference(onDelete cascade)
            floatParametersOfPlugins.foreignKeyDeclaration.constrainReference(onDelete cascade)
            intParametersOfPlugins.foreignKeyDeclaration.constrainReference(onDelete cascade)
            stringParametersOfPlugins.foreignKeyDeclaration.constrainReference(onDelete cascade)

            // When a PluginInstance is deleted, all of the its ParameterInstances will get deleted.
            booleanParameterValuesOfPluginInstances.foreignKeyDeclaration.constrainReference(onDelete cascade)
            floatParameterValuesOfPluginInstances.foreignKeyDeclaration.constrainReference(onDelete cascade)
            intParameterValuesOfPluginInstances.foreignKeyDeclaration.constrainReference(onDelete cascade)
            stringParameterValuesOfPluginInstances.foreignKeyDeclaration.constrainReference(onDelete cascade)

            // When PluginInstance is deleted, delete all its Source/Target bindings.
            bindingsOfSourcePluginInstances.foreignKeyDeclaration.constrainReference(onDelete cascade)
            bindingsOfTargetPluginInstances.foreignKeyDeclaration.constrainReference(onDelete cascade)

            // When DataSource is deleted, delete all associated ParameterValues.
            booleanParameterValuesOfDataSources.foreignKeyDeclaration.constrainReference(onDelete cascade)
            floatParameterValuesOfDataSources.foreignKeyDeclaration.constrainReference(onDelete cascade)
            intParameterValuesOfDataSources.foreignKeyDeclaration.constrainReference(onDelete cascade)
            stringParameterValuesOfDataSources.foreignKeyDeclaration.constrainReference(onDelete cascade)

            // When User/Group is deleted, delete all memberships items.
            groupMembership.leftForeignKeyDeclaration.constrainReference(onDelete cascade)
            groupMembership.rightForeignKeyDeclaration.constrainReference(onDelete cascade)

            // When user is deleted, delete all theirs owned items.
            groupOwnership.foreignKeyDeclaration.constrainReference(onDelete cascade)
            analysisOwnership.foreignKeyDeclaration.constrainReference(onDelete cascade)
            dataSourceOwnership.foreignKeyDeclaration.constrainReference(onDelete cascade)
            pluginOwnership.foreignKeyDeclaration.constrainReference(onDelete cascade)
        }
    }
}
