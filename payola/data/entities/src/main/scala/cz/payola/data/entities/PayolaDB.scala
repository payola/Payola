package cz.payola.data.entities

import org.squeryl.adapters.H2Adapter
import org.squeryl.PrimitiveTypeMode._
import java.lang.Class
import org.squeryl.{KeyedEntity, Session, SessionFactory, Schema}
import org.squeryl.dsl.CompositeKey2

object PayolaDB extends Schema
{
    val databaseUsername = "sa"
    val databasePassword = ""
    val databaseConnection = "jdbc:h2:tcp://localhost/~/h2/payola"

    val users = table[User]("users")
    val groups = table[Group]("groups")
    private val members = table[GroupMembership]

    on(users)(user => declare(
        user.id is (primaryKey),
        user.name is (unique)
    ))

    on(groups)(group => declare(
        group.id is (primaryKey),
        group.name is (unique)
    ))

    lazy val groupMembership =
        manyToManyRelation(users, groups)
            .via[GroupMembership]((u,g,gm) => (gm.memberId === u.id, g.id === gm.groupId))

    lazy val groupOwnership =
        oneToManyRelation(users, groups)
            .via((u, g) => u.id === g.ownerId)

    def startDatabaseSession():Unit = {
        Class.forName("org.h2.Driver");
        SessionFactory.concreteFactory = Some(() => Session.create(
            java.sql.DriverManager.getConnection(databaseConnection, databaseUsername, databasePassword),
            new H2Adapter)
          )
    }

    def createSchema() = {

        transaction {
            drop
            create
            //println(printDdl)
        }
    }

    def save(user: User) = {

        transaction {
            if (users.where(u => u.id === user.id).size == 0) {
                users.insert(user)
            }
            else {
                update(users)(u =>
                  where(u.id === user.id)
                  set(u.name := user.name,
                      //TODO: u.password := user.password,
                      u.email := user.email)
                )
            }
        }
    }

    def save(group: Group) = {

        transaction {
            if (groups.where(g => g.id === group.id).size == 0) {
                groups.insert(group)
            }
            else {
                update(groups)(g =>
                  where(g.id === group.id)
                  set(g.name := group.name)
                )
            }
        }
    }

    def getUserById(id: String) : Option[User] = {

        transaction {
            val result = users.where(u => u.id === id)
            if (result.size == 0) {
                None
            }
            else {
                Some(result.single)
            }
        }
    }

    def getGroupById(id: String) : Option[Group] = {

        transaction {
            val result = groups.where(g => g.id === id)
            if (result.size == 0) {
                None
            }
            else {
                Some(result.single)
            }
        }
    }
}

class GroupMembership(val memberId: String, val groupId: String)
    extends KeyedEntity[CompositeKey2[String,String]] {
  def id = compositeKey(memberId, groupId)
}
