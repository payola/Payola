package cz.payola.data

import org.squeryl.adapters.H2Adapter
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.dsl.CompositeKey2
import cz.payola.data.entities._
import org.squeryl._
import cz.payola.data.entities.analyses._
import cz.payola.data.entities.analyses.parameters._

object PayolaDB extends Schema
{
    /**
      * This value id used in 1:N relations when item on N side of this relation
      * has not filled id of related item (it means the id is None)
      */
    private val EMPTY_ID: String = "00000000-0000-0000-0000-000000000000"

    /**
      * Table of [[cz.payola.data.entities.User]]s
      */
    val users = table[User]("users")

    /**
      * Table of [[cz.payola.data.entities.Group]]s
      */
    val groups = table[Group]("groups")

    /**
      * Table of [[cz.payola.data.entities.Analysis]] items
      */
    val analyses = table[Analysis]("analyses")

    /**
      * Table of [[cz.payola.data.entities.analyses.PluginDbRepresentation]]s
      */
    val plugins = table[PluginDbRepresentation]("plugins")

    /**
      * Table of [[cz.payola.data.entities.analyses.PluginInstance]]s
      */
    val pluginInstances = table[PluginInstance]("pluginInstances")

    /**
      * Table of  ([[cz.payola.data.entities.User]]s)s
      */
    val booleanParameters = table[BooleanParameter]("booleanParameters")

    /**
      * Table of [[cz.payola.data.entities.analyses.parameters.BooleanParameterValues]]s
      */
    val booleanParameterValues = table[BooleanParameterValue]("booleanParameterValues")

    /**
      * Table of [[cz.payola.data.entities.analyses.parameters.FloatParameter]]s
      */
    val floatParameters = table[FloatParameter]("floatParameters")

    /**
      * Table of [[cz.payola.data.entities.analyses.parameters.FloatParameterValue]]s
      */
    val floatParameterValues = table[FloatParameterValue]("floatParameterValues")

    /**
      * Table of [[cz.payola.data.entities.analyses.parameters.IntParameter]]s
      */
    val intParameters = table[IntParameter]("intParameters")

    /**
      * Table of [[cz.payola.data.entities.analyses.parameters.IntParameterValue]]s
      */
    val intParameterValues = table[IntParameterValue]("intParameterValues")

    /**
      * Table of [[cz.payola.data.entities.analyses.parameters.StringParameter]]s
      */
    val stringParameters = table[StringParameter]("stringParameters")

    /**
      * Table of [[cz.payola.data.entities.analyses.parameters.StringParameterValue]]s
      */
    val stringParameterValues = table[StringParameterValue]("stringParameterValues")

    /**
      * Table of [[cz.payola.data.entities.analyses.PluginInstanceBinding]]s
      */
    val pluginInstanceBindings = table[PluginInstanceBinding]("pluginInstanceBindings")

    /**
      * Table of [[cz.payola.data.entities.analyses.DataSource]]s
      */
    val dataSources = table[DataSource]("dataSources")

    /**
      * Relation that associates members to [[cz.payola.data.entities.Group]]s
      */
    val groupMembership =
        manyToManyRelation(users, groups)
            .via[GroupMembership]((u, g, gm) => (gm.memberId === u.id, g.id === gm.groupId))

    /**
      * Relation that associates [[cz.payola.data.entities.Group]]s to their owners ([[cz.payola.data.entities.User]]s)
      */
    val groupOwnership =
        oneToManyRelation(users, groups)
            .via((u, g) => u.id === g.ownerId.getOrElse(EMPTY_ID).toString)

    /**
      * Relation that associates [[cz.payola.data.entities.Analysis]] to its owner ([[cz.payola.data.entities.User]]s)
      */
    val analysisOwnership =
        oneToManyRelation(users, analyses)
            .via((u, a) => u.id === a.ownerId.getOrElse(EMPTY_ID).toString)

    /**
      * Relation that associates [[cz.payola.data.entities.analyses.DataSource]]s to their owners ([[cz.payola.data.entities.User]]s)
      */
    val dataSourceOwnership =
        oneToManyRelation(users, dataSources)
            .via((u, ds) => u.id === ds.ownerId.getOrElse(EMPTY_ID).toString)

    /**
      * Relation that associates [[cz.payola.data.entities.analyses.PluginDbRepresentation]] to a [[cz.payola.data.entities.analyses.PluginDbRepresentation]]
      */
    val pluginsPluginInstances =
        oneToManyRelation(plugins, pluginInstances)
            .via((p, pi) => p.id === pi.pluginId.getOrElse(EMPTY_ID).toString)

    /**
      * Relation that associates [[cz.payola.data.entities.analyses.PluginDbRepresentation]] to an [[cz.payola.data.entities.Analysis]]
      */
    val analysesPluginInstances =
        oneToManyRelation(analyses, pluginInstances)
            .via((a, pi) => (a.id === pi.analysisId.getOrElse(EMPTY_ID).toString))

    /**
      * Relation that associates [[cz.payola.data.entities.analyses.DataSource]]s to a [[cz.payola.data.entities.analyses.PluginDbRepresentation]]s
      */
    val pluginsDataSources =
        oneToManyRelation(plugins, dataSources)
            .via((p, ds) => p.id === ds.pluginId.getOrElse(EMPTY_ID).toString)

    /**
      * Relation that associates [[cz.payola.data.entities.analyses.PluginInstanceBinding]]s to a [[cz.payola.data.entities.Analysis]]
      */
    val analysesPluginInstancesBindings =
        oneToManyRelation(analyses, pluginInstanceBindings)
            .via((a, pib) => (a.id === pib.analysisId.getOrElse(EMPTY_ID).toString))

    /**
      * Relation that associates [[cz.payola.data.entities.analyses.PluginInstanceBinding]]s to a [[cz.payola.data.entities.analyses.PluginInstance]] as a source
      */
    val bindingsOfSourcePluginInstances =
        oneToManyRelation(pluginInstances, pluginInstanceBindings)
            .via((pi, b) => (pi.id === b.sourcePluginInstanceId.getOrElse(EMPTY_ID).toString))

    /**
      * Relation that associates [[cz.payola.data.entities.analyses.PluginInstanceBinding]]s to a [[cz.payola.data.entities.analyses.PluginInstance]] as a target
      */
    val bindingsOfTargetPluginInstances =
        oneToManyRelation(pluginInstances, pluginInstanceBindings)
            .via((pi, b) => (pi.id === b.targetPluginInstanceId.getOrElse(EMPTY_ID).toString))

    /**
      * Relation that associates [[cz.payola.data.entities.analyses.parameters.BooleanParameterValues]]s to a boolean parameter
      */
    val valuesOfBooleanParameters =
        oneToManyRelation(booleanParameters, booleanParameterValues)
            .via((bp, bpv) => bp.id === bpv.parameterId.getOrElse(EMPTY_ID).toString)

    /**
      * Relation that associates [[cz.payola.data.entities.analyses.parameters.FloatParameterValue]]s to a [[cz.payola.data.entities.analyses.parameters.FloatParameter]]
      */
    val valuesOfFloatParameters =
        oneToManyRelation(floatParameters, floatParameterValues)
            .via((fp, fpv) => fp.id === fpv.parameterId.getOrElse(EMPTY_ID).toString)

    /**
      * Relation that associates [[cz.payola.data.entities.analyses.parameters.IntParameterValue]]s to an [[cz.payola.data.entities.analyses.parameters.IntParameter]]
      */
    val valuesOfIntParameters =
        oneToManyRelation(intParameters, intParameterValues)
            .via((ip, ipv) => ip.id === ipv.parameterId.getOrElse(EMPTY_ID).toString)

    /**
      * Relation that associates [[cz.payola.data.entities.analyses.parameters.StringParameterValues]] to a [[cz.payola.data.entities.analyses.parameters.StringParameter]]
      */
    val valuesOfStringParameters =
        oneToManyRelation(stringParameters, stringParameterValues)
            .via((sp, spv) => sp.id === spv.parameterId.getOrElse(EMPTY_ID).toString)

    /**
      * Relation that associates [[cz.payola.data.entities.analyses.parameters.BooleanParameter]] to a [[cz.payola.data.entities.analyses.PluginDbRepresentation]]
      */
    val booleanParametersOfPlugins =
        oneToManyRelation(plugins, booleanParameters)
            .via((p, bp) => p.id === bp.pluginId.getOrElse(EMPTY_ID).toString)

    /**
      * Relation that associates [[cz.payola.data.entities.analyses.parameters.FloatParameter]]s to a [[cz.payola.data.entities.analyses.PluginDbRepresentation]]
      */
    val floatParametersOfPlugins =
        oneToManyRelation(plugins, floatParameters)
            .via((p, fp) => p.id === fp.pluginId.getOrElse(EMPTY_ID).toString)

    /**
      * Relation that associates [[cz.payola.data.entities.analyses.parameters.IntParameter]]s to a [[cz.payola.data.entities.analyses.PluginDbRepresentation]]
      */
    val intParametersOfPlugins =
        oneToManyRelation(plugins, intParameters)
            .via((p, ip) => p.id === ip.pluginId.getOrElse(EMPTY_ID).toString)

    /**
      * Relation that associates [[cz.payola.data.entities.analyses.parameters.StringParameter]]s to a [[cz.payola.data.entities.analyses.PluginDbRepresentation]]
      */
    val stringParametersOfPlugins =
        oneToManyRelation(plugins, stringParameters)
            .via((p, sp) => p.id === sp.pluginId.getOrElse(EMPTY_ID).toString)

    /**
      * Relation that associates [[cz.payola.data.entities.analyses.parameters.BooleanParameterValues]]s to a [[cz.payola.data.entities.analyses.parameters.BooleanParameter]]
      */
    val booleanParameterValuesOfPluginInstances =
        oneToManyRelation(pluginInstances, booleanParameterValues)
            .via((pi, bpv) => pi.id === bpv.pluginInstanceId.getOrElse(EMPTY_ID).toString)

    /**
      * Relation that associates [[cz.payola.data.entities.analyses.parameters.FloatParameterValue]]s to a [[cz.payola.data.entities.analyses.parameters.FloatParameter]]
      */
    val floatParameterValuesOfPluginInstances =
        oneToManyRelation(pluginInstances, floatParameterValues)
            .via((pi, fpv) => pi.id === fpv.pluginInstanceId.getOrElse(EMPTY_ID).toString)

    /**
      * Relation that associates [[cz.payola.data.entities.analyses.parameters.IntParameterValue]]s to an [[cz.payola.data.entities.analyses.parameters.IntParameter]]
      */
    val intParameterValuesOfPluginInstances =
        oneToManyRelation(pluginInstances, intParameterValues)
            .via((pi, ipv) => pi.id === ipv.pluginInstanceId.getOrElse(EMPTY_ID).toString)

    /**
      * Relation that associates [[cz.payola.data.entities.analyses.parameters.StringParameterValues]]s to a [[cz.payola.data.entities.analyses.parameters.StringParameter]]
      */
    val stringParameterValuesOfPluginInstances =
        oneToManyRelation(pluginInstances, stringParameterValues)
            .via((pi, spv) => pi.id === spv.pluginInstanceId.getOrElse(EMPTY_ID).toString)

    /**
      * Relation that associates [[cz.payola.data.entities.analyses.parameters.BooleanParameterValues]]s to a [[cz.payola.data.entities.analyses.DataSource]]
      */
    val booleanParameterValuesOfDataSources =
        oneToManyRelation(dataSources, booleanParameterValues)
            .via((ds, bpv) => ds.id === bpv.dataSourceId.getOrElse(EMPTY_ID).toString)

    /**
      * Relation that associates [[cz.payola.data.entities.analyses.parameters.FloatParameter]]s values to a [[cz.payola.data.entities.analyses.DataSource]]
      */
    val floatParameterValuesOfDataSources =
        oneToManyRelation(dataSources, floatParameterValues)
            .via((ds, fpv) => ds.id === fpv.dataSourceId.getOrElse(EMPTY_ID).toString)

    /**
      * Relation that associates [[cz.payola.data.entities.analyses.parameters.IntParameter]] to a [[cz.payola.data.entities.analyses.DataSource]]
      */
    val intParameterValuesOfDataSources =
        oneToManyRelation(dataSources, intParameterValues)
            .via((ds, ipv) => ds.id === ipv.dataSourceId.getOrElse(EMPTY_ID).toString)

    /**
      * Relation that associates [[cz.payola.data.entities.analyses.parameters.StringParameter]]s values to a [[cz.payola.data.entities.analyses.DataSource]]
      */
    val stringParameterValuesOfDataSources =
        oneToManyRelation(dataSources, stringParameterValues)
            .via((ds, spv) => ds.id === spv.dataSourceId.getOrElse(EMPTY_ID).toString)

    // The default constraint for all foreign keys in this schema :
    override def applyDefaultForeignKeyPolicy(foreignKeyDeclaration: ForeignKeyDeclaration) {
        foreignKeyDeclaration.constrainReference
    }

    /**
      * Initializes connection to server database. Connection string is read from config file.
      *
      * @return Returns true if connection was established, otherwise and error is thrown
      */
    def connect(test: Boolean = false) {
        // TODO: Read from config file, remove test variable
        val databaseLocation: String = "jdbc:h2:tcp://localhost/~/h2/payola{0}".format(if(test) ".test" else "")
        val userName: String = "sa"
        val password: String = ""

        try {
            startDatabaseSession(databaseLocation, userName, password, "")
        }
        catch {
            case e: Exception => {println("Failed to connect. " + e); throw e}
        }
    }

    private def startDatabaseSession(database: String, userName: String, password: String, databaseType: String = "") {
        // TODO: add database-type specific code
        java.lang.Class.forName("org.h2.Driver");

        SessionFactory.concreteFactory = Some(() =>
            Session.create(
                java.sql.DriverManager.getConnection(database, userName, password),
                new H2Adapter)
        )
    }

    /**
      * Defines and creates all tables and foreign-key contstraints between tables.
      * Before schema is created, existing schema is dropped.
      */
    def createSchema()  {
        on(users)(user =>
            declare(
                user.id is (primaryKey),
                user.name is (unique)
            ))

        on(groups)(group =>
            declare(
                group.id is (primaryKey),
                group.name is (unique)
            ))

        on(analyses)(analysis =>
            declare(
                analysis.id is (primaryKey),
                analysis.name is (unique)
            ))

        on(plugins)(plugin =>
            declare(
                plugin.id is (primaryKey),
                plugin.name is (unique)
            ))

        on(pluginInstances)(instance =>
            declare(
                instance.id is (primaryKey)
            ))

        on(booleanParameters)(param =>
            declare(
                param.id is (primaryKey)
            ))

        on(booleanParameterValues)(param =>
            declare(
                param.id is (primaryKey)
            ))

        on(floatParameters)(param =>
            declare(
                param.id is (primaryKey)
            ))

        on(floatParameterValues)(param =>
            declare(
                param.id is (primaryKey)
            ))

        on(intParameters)(param =>
            declare(
                param.id is (primaryKey)
            ))

        on(intParameterValues)(param =>
            declare(
                param.id is (primaryKey)
            ))

        on(stringParameters)(param =>
            declare(
                param.id is (primaryKey)
            ))

        on(stringParameterValues)(param =>
            declare(
                param.id is (primaryKey)
            ))

        on(dataSources)(ds =>
            declare(
                ds.id is (primaryKey)
            ))

        defineForeignKeyPolicy()

        transaction {
            drop
            create
        }
    }

    private def defineForeignKeyPolicy() {
        // When a PluginDbRepresentation is deleted, all of the its instances will get deleted :
        pluginsPluginInstances.foreignKeyDeclaration.constrainReference(onDelete cascade)

        // When an Analysis is deleted, all of the its plugin instances will get deleted :
        analysesPluginInstances.foreignKeyDeclaration.constrainReference(onDelete cascade)
        analysesPluginInstancesBindings.foreignKeyDeclaration.constrainReference(onDelete cascade)

        // When a Parameter is deleted, all of the its parameterValues will get deleted :
        valuesOfBooleanParameters.foreignKeyDeclaration.constrainReference(onDelete cascade)
        valuesOfFloatParameters.foreignKeyDeclaration.constrainReference(onDelete cascade)
        valuesOfIntParameters.foreignKeyDeclaration.constrainReference(onDelete cascade)
        valuesOfStringParameters.foreignKeyDeclaration.constrainReference(onDelete cascade)

        // When a PluginDbRepresentation is deleted, all of the its Parameters will get deleted :
        booleanParametersOfPlugins.foreignKeyDeclaration.constrainReference(onDelete cascade)
        floatParametersOfPlugins.foreignKeyDeclaration.constrainReference(onDelete cascade)
        intParametersOfPlugins.foreignKeyDeclaration.constrainReference(onDelete cascade)
        stringParametersOfPlugins.foreignKeyDeclaration.constrainReference(onDelete cascade)

        // When a PluginInstance is deleted, all of the its ParameterInstances will get deleted :
        booleanParameterValuesOfPluginInstances.foreignKeyDeclaration.constrainReference(onDelete cascade)
        floatParameterValuesOfPluginInstances.foreignKeyDeclaration.constrainReference(onDelete cascade)
        intParameterValuesOfPluginInstances.foreignKeyDeclaration.constrainReference(onDelete cascade)
        stringParameterValuesOfPluginInstances.foreignKeyDeclaration.constrainReference(onDelete cascade)

        // When PluginInstance is deleted, delete all its Source/Target bindings
        bindingsOfSourcePluginInstances.foreignKeyDeclaration.constrainReference(onDelete cascade)
        bindingsOfTargetPluginInstances.foreignKeyDeclaration.constrainReference(onDelete cascade)

        // When DataSource is deleted, delete all associated ParameterValues
        booleanParameterValuesOfDataSources.foreignKeyDeclaration.constrainReference(onDelete cascade)
        floatParameterValuesOfDataSources.foreignKeyDeclaration.constrainReference(onDelete cascade)
        intParameterValuesOfDataSources.foreignKeyDeclaration.constrainReference(onDelete cascade)
        stringParameterValuesOfDataSources.foreignKeyDeclaration.constrainReference(onDelete cascade)

        // When User/Group is deleted, delete all memberships items
        groupMembership.leftForeignKeyDeclaration.constrainReference(onDelete cascade)
        groupMembership.rightForeignKeyDeclaration.constrainReference(onDelete cascade)

        // When user is deleted, delete all theirs owned items
        groupOwnership.foreignKeyDeclaration.constrainReference(onDelete cascade)
        analysisOwnership.foreignKeyDeclaration.constrainReference(onDelete cascade)
        dataSourceOwnership.foreignKeyDeclaration.constrainReference(onDelete cascade)
    }

    /**
      * Defines member-relation between user and group
      * @param memberId - id of the user (member)
      * @param groupId  - id of the group user is member or
      */
    class GroupMembership(val memberId: String, val groupId: String)
        extends KeyedEntity[CompositeKey2[String, String]]
    {
        def id = compositeKey(memberId, groupId)
    }

}
