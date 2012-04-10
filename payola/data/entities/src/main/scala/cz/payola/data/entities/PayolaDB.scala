package cz.payola.data.entities

import org.squeryl.adapters.H2Adapter
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.{KeyedEntity, Session, SessionFactory, Schema, Table}
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


        transaction {
            drop
            create
            //println(printDdl)
        }
    }

    def persist(persistable: PersistableEntity) {
        if (persistable.isPersisted) {
            persistable.update
        } else {
            persistable.save
        }
    }

    /*def save(entity: cz.payola.common.entities.Entity) = {

        transaction {
            // Get properly typed entity
            // (properly means that can be persisted)
            val e = entity match {
                case x: User => entity.asInstanceOf[User]
                case x: Group => entity.asInstanceOf[Group]
                case _ => throw new Exception("Unpersistable entity type")
            }

            if (e.isPersisted) {
                e.update
                println("updated")
            }
            else {
                // TODO: method is called and finishes, but doesn't save any data in DB
                // result is None
                val result = e.save
                println("saved")
            }
 /*
            val table = entity match {
                case x: User => users
                case x: Group => groups
                case _ => throw new Exception("Unpersistable entity type")
            }


            if (table.where(x => x.id === x.id).size == 0) {
                table.insert(e)
            } */
        }
    }*/

    def getUserById(id: String) : Option[User] = {
        _getByID(users, id)
    }

    def getGroupById(id: String) : Option[Group] = {
        _getByID(groups, id)
    }

    def getAnalysisById(id: String) : Option[Analysis] = {
        _getByID(analyses, id)
    }

    def getPluginById(id: String) : Option[Plugin] = {
        _getByID(plugins, id)
    }

    def getPluginInstanceById(id: String) : Option[PluginInstance] = {
        _getByID(pluginInstances, id)
    }

    private def _getByID[A <: cz.payola.common.entities.Entity](table: Table[A], id: String): Option[A] = {
        try {
            transaction {
                val result = table.where(e => e.id === id)
                if (result.size == 0) {
                    None
                }
                else {
                    Some(result.single)
                }
            }
        }
        catch {
            case _ => None
        }
    }
}

class GroupMembership(val memberId: String, val groupId: String)
    extends KeyedEntity[CompositeKey2[String,String]] {
  def id = compositeKey(memberId, groupId)
}
