package cz.payola.data.entities.schema

import org.squeryl.adapters.H2Adapter
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.dsl.CompositeKey2
import cz.payola.data.entities._
import org.squeryl._

object PayolaDB extends Schema
{
    private val databaseUsername = "sa"

    private val databasePassword = ""

    private val databaseConnection = "jdbc:h2:tcp://localhost/~/h2/payola"

    val users = table[User]("users")

    val groups = table[Group]("groups")

    val analyses = table[Analysis]("analyses")

    val plugins = table[Plugin]("plugins")

    val pluginInstances = table[PluginInstance]("pluginInstances")

    val booleanParameters = table[BooleanParameter]("booleanParameters")

    val booleanParameterInstances = table[BooleanParameterInstance]("booleanParameterInstances")

    val floatParameters = table[FloatParameter]("floatParameters")

    val floatParameterInstances = table[FloatParameterInstance]("floatParameterInstances")

    val intParameters = table[IntParameter]("intParameters")

    val intParameterInstances = table[IntParameterInstance]("intParameterInstances")

    val stringParameters = table[StringParameter]("stringParameters")

    val stringParameterInstances = table[StringParameterInstance]("stringParameterInstances")

    val groupMembership =
        manyToManyRelation(users, groups)
            .via[GroupMembership]((u, g, gm) => (gm.memberId === u.id, g.id === gm.groupId))

    val groupOwnership =
        oneToManyRelation(users, groups)
            .via((u, g) => u.id === g.ownerId)

    val analysisOwnership =
        oneToManyRelation(users, analyses)
            .via((u, a) => u.id === a.ownerId)

    val pluginsPluginInstances =
        oneToManyRelation(plugins, pluginInstances)
            .via((p, pi) => p.id === pi.pluginId)

    val analysesPluginInstances =
        oneToManyRelation(analyses, pluginInstances)
            .via((a, pi) => (a.id === pi.analysisId))

    val instancesOfBooleanParameters =
        oneToManyRelation(booleanParameters, booleanParameterInstances)
            .via((p, pi) => p.id === pi.parameterId)

    val instancesOfFloatParameters =
        oneToManyRelation(floatParameters, floatParameterInstances)
            .via((p, pi) => p.id === pi.parameterId)

    val instancesOfIntParameters =
        oneToManyRelation(intParameters, intParameterInstances)
            .via((p, pi) => p.id === pi.parameterId)

    val instancesOfStringParameters =
        oneToManyRelation(stringParameters, stringParameterInstances)
            .via((p, pi) => p.id === pi.parameterId)

    val booleanParametersOfPlugins =
        oneToManyRelation(plugins, booleanParameters)
            .via((p, bp) => p.id === bp.pluginId)

    val floatParametersOfPlugins =
        oneToManyRelation(plugins, floatParameters)
            .via((p, fp) => p.id === fp.pluginId)

    val intParametersOfPlugins =
        oneToManyRelation(plugins, intParameters)
            .via((p, ip) => p.id === ip.pluginId)

    val stringParametersOfPlugins =
        oneToManyRelation(plugins, stringParameters)
            .via((p, sp) => p.id === sp.pluginId)        

    val booleanParameterInstancesOfPluginInstances =
        oneToManyRelation(pluginInstances, booleanParameterInstances)
            .via((p, bpi) => p.id === bpi.pluginInstanceId)

    val floatParameterInstancesOfPluginInstances =
        oneToManyRelation(pluginInstances, floatParameterInstances)
            .via((p, fpi) => p.id === fpi.pluginInstanceId)

    val intParameterInstancesOfPluginInstances =
        oneToManyRelation(pluginInstances, intParameterInstances)
            .via((p, ipi) => p.id === ipi.pluginInstanceId)

    val stringParameterInstancesOfPluginInstances =
        oneToManyRelation(pluginInstances, stringParameterInstances)
            .via((p, spi) => p.id === spi.pluginInstanceId)

    // The default constraint for all foreign keys in this schema :
    override def applyDefaultForeignKeyPolicy(foreignKeyDeclaration: ForeignKeyDeclaration) =
        foreignKeyDeclaration.constrainReference

    def connect(): Boolean = {
        // TODO: Read from config file
        val databaseLocation: String = "jdbc:h2:tcp://localhost/~/h2/payola"
        val userName: String = "sa"
        val password: String = ""

        try {
            startDatabaseSession(databaseLocation, userName, password, "")

            true
        }
        catch {
            case e: Exception => println("Failed to connect. " + e)

            false
        }
    }

    private def startDatabaseSession(
            database: String = "jdbc:h2:tcp://localhost/~/h2/payola",
            userName: String = "sa",
            password: String = "",
            databaseType: String = "") {
        // TODO: add database specific code
        java.lang.Class.forName("org.h2.Driver");

        SessionFactory.concreteFactory = Some(() =>
            Session.create(
                java.sql.DriverManager.getConnection(database, userName, password),
                new H2Adapter)
        )
    }

    def createSchema() = {
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
                param.id is (primaryKey),
                param.name is (unique)
            ))

        on(booleanParameterInstances)(param =>
            declare(
                param.id is (primaryKey)
            ))

        on(floatParameters)(param =>
            declare(
                param.id is (primaryKey),
                param.name is (unique)
            ))

        on(floatParameterInstances)(param =>
            declare(
                param.id is (primaryKey)
            ))

        on(intParameters)(param =>
            declare(
                param.id is (primaryKey),
                param.name is (unique)
            ))

        on(intParameterInstances)(param =>
            declare(
                param.id is (primaryKey)
            ))

        on(stringParameters)(param =>
            declare(
                param.id is (primaryKey),
                param.name is (unique)
            ))

        on(stringParameterInstances)(param =>
            declare(
                param.id is (primaryKey)
            ))

        defineForeignKeyPolicy()

        transaction {
            drop
            create
            //println(printDdl)
        }
    }

    private def defineForeignKeyPolicy() {
        // When a Plugin is deleted, all of the its instances will get deleted :
        pluginsPluginInstances.foreignKeyDeclaration.constrainReference(onDelete cascade)

        // When an Analysis is deleted, all of the its plugin instances will get deleted :
        analysesPluginInstances.foreignKeyDeclaration.constrainReference(onDelete cascade)

        // When a Parameter is deleted, all of the its instances will get deleted :
        instancesOfBooleanParameters.foreignKeyDeclaration.constrainReference(onDelete cascade)
        instancesOfFloatParameters.foreignKeyDeclaration.constrainReference(onDelete cascade)
        instancesOfIntParameters.foreignKeyDeclaration.constrainReference(onDelete cascade)
        instancesOfStringParameters.foreignKeyDeclaration.constrainReference(onDelete cascade)

        // When a Plugin is deleted, all of the its Parameters will get deleted :
        booleanParametersOfPlugins.foreignKeyDeclaration.constrainReference(onDelete cascade)
        floatParametersOfPlugins.foreignKeyDeclaration.constrainReference(onDelete cascade)
        intParametersOfPlugins.foreignKeyDeclaration.constrainReference(onDelete cascade)
        stringParametersOfPlugins.foreignKeyDeclaration.constrainReference(onDelete cascade)

        // When a PluginInstance is deleted, all of the its ParameterInstances will get deleted :
        booleanParameterInstancesOfPluginInstances.foreignKeyDeclaration.constrainReference(onDelete cascade)
        floatParameterInstancesOfPluginInstances.foreignKeyDeclaration.constrainReference(onDelete cascade)
        intParameterInstancesOfPluginInstances.foreignKeyDeclaration.constrainReference(onDelete cascade)
        stringParameterInstancesOfPluginInstances.foreignKeyDeclaration.constrainReference(onDelete cascade)
    }

    class GroupMembership(val memberId: String, val groupId: String)
        extends KeyedEntity[CompositeKey2[String, String]]
    {
        def id = compositeKey(memberId, groupId)
    }
}
