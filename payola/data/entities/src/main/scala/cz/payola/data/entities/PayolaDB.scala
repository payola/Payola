package cz.payola.data.entities

import org.squeryl.adapters.H2Adapter
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.{KeyedEntity, Session, SessionFactory, Schema}
import org.squeryl.dsl.CompositeKey2

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

    private val members = table[GroupMembership]("groups_members")

    val groupMembership =
        manyToManyRelation(users, groups)
            .via[GroupMembership]((u,g,gm) => (gm.memberId === u.id, g.id === gm.groupId))

    val groupOwnership =
        oneToManyRelation(users, groups)
            .via((u, g) => u.id === g.ownerId)

    val analysisOwnership =
            oneToManyRelation(users, analyses)
                .via((u, a) => u.id === a.ownerId)

    def startDatabaseSession():Unit = {
        java.lang.Class.forName("org.h2.Driver");
        SessionFactory.concreteFactory = Some(() => Session.create(
            java.sql.DriverManager.getConnection(databaseConnection, databaseUsername, databasePassword),
            new H2Adapter)
          )
    }

    def createSchema() = {
        on(users)(user => declare(
            user.id is (primaryKey),
            user.name is (unique)
        ))

        on(groups)(group => declare(
            group.id is (primaryKey),
            group.name is (unique)
        ))

        on(analyses)(analysis => declare(
            analysis.id is (primaryKey),
            analysis.name is (unique)
        ))

        on(plugins)(plugin => declare(
            plugin.id is (primaryKey),
            plugin.name is (unique)
        ))

        on(pluginInstances)(instance => declare(
            instance.id is (primaryKey)
        ))

        inTransaction {
            drop
            create
            //println(printDdl)
        }
    }
}

class GroupMembership(val memberId: String, val groupId: String)
    extends KeyedEntity[CompositeKey2[String,String]] {
  def id = compositeKey(memberId, groupId)
}
