package cz.payola.data.entities.schema

import org.squeryl.adapters.H2Adapter
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.dsl.CompositeKey2
import cz.payola.data.entities._
import org.squeryl.{Session, SessionFactory, KeyedEntity, Schema}

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

    def startDatabaseSession(): Unit = {
        java.lang.Class.forName("org.h2.Driver");
        SessionFactory.concreteFactory = Some(() =>
            Session.create(
                java.sql.DriverManager.getConnection(databaseConnection, databaseUsername, databasePassword),
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

        transaction {
            drop
            create
            //println(printDdl)
        }
    }

    class GroupMembership(val memberId: String, val groupId: String)
        extends KeyedEntity[CompositeKey2[String, String]]
    {
        def id = compositeKey(memberId, groupId)
    }
}
