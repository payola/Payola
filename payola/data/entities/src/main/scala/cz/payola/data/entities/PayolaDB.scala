package cz.payola.data.entities

import org.squeryl.adapters.H2Adapter
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.dsl.CompositeKey2
import cz.payola.data.entities._
import org.squeryl._
import cz.payola.data.entities.analyses._
import cz.payola.data.entities.analyses.parameters._

object PayolaDB extends Schema
{
    val EMPTY_ID: String = "00000000-0000-0000-0000-000000000000"

    val users = table[User]("users")

    val groups = table[Group]("groups")

    val analyses = table[Analysis]("analyses")

    val plugins = table[PluginDbRepresentation]("plugins")

    val pluginInstances = table[PluginInstance]("pluginInstances")

    val booleanParameters = table[BooleanParameterDbRepresentation]("booleanParameters")

    val booleanParameterValues = table[BooleanParameterValue]("booleanParameterValues")

    val floatParameters = table[FloatParameterDbRepresentation]("floatParameters")

    val floatParameterValues = table[FloatParameterValue]("floatParameterValues")

    val intParameters = table[IntParameterDbRepresentation]("intParameters")

    val intParameterValues = table[IntParameterValue]("intParameterValues")

    val stringParameters = table[StringParameterDbRepresentation]("stringParameters")

    val stringParameterValues = table[StringParameterValue]("stringParameterValues")

    val pluginInstanceBindings = table[PluginInstanceBinding]("pluginInstanceBindings")

    val groupMembership =
        manyToManyRelation(users, groups)
            .via[GroupMembership]((u, g, gm) => (gm.memberId === u.id, g.id === gm.groupId))

    val groupOwnership =
        oneToManyRelation(users, groups)
            .via((u, g) => u.id === g.ownerId.getOrElse(EMPTY_ID).toString)

    val analysisOwnership =
        oneToManyRelation(users, analyses)
            .via((u, a) => u.id === a.ownerId.getOrElse(EMPTY_ID).toString)

    val pluginsPluginInstances =
        oneToManyRelation(plugins, pluginInstances)
            .via((p, pi) => p.id === pi.pluginId.getOrElse(EMPTY_ID).toString)

    val analysesPluginInstances =
        oneToManyRelation(analyses, pluginInstances)
            .via((a, pi) => (a.id === pi.analysisId.getOrElse(EMPTY_ID).toString))

    val analysesPluginInstancesBindings =
        oneToManyRelation(analyses, pluginInstanceBindings)
            .via((a, pib) => (a.id === pib.analysisId.getOrElse(EMPTY_ID).toString))

    val bindingsOfSourcePluginInstances =
        oneToManyRelation(pluginInstances, pluginInstanceBindings)
            .via((pi, b) => (pi.id === b.sourcePluginInstanceId.getOrElse(EMPTY_ID).toString))

    val bindingsOfTargetPluginInstances =
        oneToManyRelation( pluginInstances, pluginInstanceBindings)
            .via((pi, b) => (pi.id === b.targetPluginInstanceId.getOrElse(EMPTY_ID).toString))

    val valuesOfBooleanParameters =
        oneToManyRelation(booleanParameters, booleanParameterValues)
            .via((bp, bpv) => bp.id === bpv.parameterId.getOrElse(EMPTY_ID).toString)

    val valuesOfFloatParameters =
        oneToManyRelation(floatParameters, floatParameterValues)
            .via((fp, fpv) => fp.id === fpv.parameterId.getOrElse(EMPTY_ID).toString)

    val valuesOfIntParameters =
        oneToManyRelation(intParameters, intParameterValues)
            .via((ip, ipv) => ip.id === ipv.parameterId.getOrElse(EMPTY_ID).toString)

    val valuesOfStringParameters =
        oneToManyRelation(stringParameters, stringParameterValues)
            .via((sp, spv) => sp.id === spv.parameterId.getOrElse(EMPTY_ID).toString)

    val booleanParametersOfPlugins =
        oneToManyRelation(plugins, booleanParameters)
            .via((p, bp) => p.id === bp.pluginId.getOrElse(EMPTY_ID).toString)

    val floatParametersOfPlugins =
        oneToManyRelation(plugins, floatParameters)
            .via((p, fp) => p.id === fp.pluginId.getOrElse(EMPTY_ID).toString)

    val intParametersOfPlugins =
        oneToManyRelation(plugins, intParameters)
            .via((p, ip) => p.id === ip.pluginId.getOrElse(EMPTY_ID).toString)

    val stringParametersOfPlugins =
        oneToManyRelation(plugins, stringParameters)
            .via((p, sp) => p.id === sp.pluginId.getOrElse(EMPTY_ID).toString)

    val booleanParameterValuesOfPluginInstances =
        oneToManyRelation(pluginInstances, booleanParameterValues)
            .via((pi, bpv) => pi.id === bpv.pluginInstanceId.getOrElse(EMPTY_ID).toString)

    val floatParameterValuesOfPluginInstances =
        oneToManyRelation(pluginInstances, floatParameterValues)
            .via((pi, fpv) => pi.id === fpv.pluginInstanceId.getOrElse(EMPTY_ID).toString)

    val intParameterValuesOfPluginInstances =
        oneToManyRelation(pluginInstances, intParameterValues)
            .via((pi, ipv) => pi.id === ipv.pluginInstanceId.getOrElse(EMPTY_ID).toString)

    val stringParameterValuesOfPluginInstances =
        oneToManyRelation(pluginInstances, stringParameterValues)
            .via((pi, spv) => pi.id === spv.pluginInstanceId.getOrElse(EMPTY_ID).toString)

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
                plugin.id is (primaryKey)
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

        defineForeignKeyPolicy()

        transaction {
            drop
            create
            //println(printDdl)
        }
    }

    private def defineForeignKeyPolicy() {
        // When a PluginDbRepresentation is deleted, all of the its instances will get deleted :
        pluginsPluginInstances.foreignKeyDeclaration.constrainReference(onDelete cascade)

        // When an Analysis is deleted, all of the its plugin instances will get deleted :
        analysesPluginInstances.foreignKeyDeclaration.constrainReference(onDelete cascade)
        analysesPluginInstancesBindings.foreignKeyDeclaration.constrainReference(onDelete cascade)

        // When a ParameterDbRepresentation is deleted, all of the its instances will get deleted :
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
    }

    class GroupMembership(val memberId: String, val groupId: String)
        extends KeyedEntity[CompositeKey2[String, String]]
    {
        def id = compositeKey(memberId, groupId)
    }

}
