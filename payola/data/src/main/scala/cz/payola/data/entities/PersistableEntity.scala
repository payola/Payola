package cz.payola.data.entities

import org.squeryl.{Query, KeyedEntity}
import org.squeryl.PrimitiveTypeMode._
import collection.mutable.ArrayBuffer
import org.squeryl.dsl.{ManyToOne, OneToMany, ManyToMany}

trait PersistableEntity extends cz.payola.domain.entities.Entity with KeyedEntity[String]
{ self: cz.payola.domain.entities.Entity =>

    protected final def evaluateCollection[A](col: Query[A]): collection.Seq[A]  = {
        try
        {
            transaction {
                val entities: ArrayBuffer[A] = new ArrayBuffer[A]()

                for (e <- col) {
                    entities += e
                }

                entities.toSeq
            }
        }
        catch {
            case e: Exception => println("Collection evaluating failed: " + e.toString)
            Seq()
        }
    }

    protected final def associate[A <: PersistableEntity](entity: A, relation: ManyToMany[A,_]): Option[A] = {
        try
        {
            transaction {
                if (relation.find(e => e.id == entity.id) == None) {
                    relation.associate(entity)
                }

                Some(entity)
            }
        }
        catch {
            case e: Exception => println("M:N association failed: " + e.toString)

            None
        }
    }

    protected final def assign[A <: PersistableEntity](entity: A, relation: ManyToMany[A,_]): Option[A] = {
        try
        {
            transaction {
                if (relation.find(e => e.id == entity.id) == None) {
                    relation.assign(entity)
                }

                Some(entity)
            }
        }
        catch {
            case e: Exception => println("M:N assign failed: " + e.toString)

            None
        }
    }

    protected final def dissociate[A <: PersistableEntity](entity: A, relation: ManyToMany[A,_]): Option[A] = {
        try
        {
            transaction {
                if (relation.find(e => e.id == entity.id) != None) {
                    relation.dissociate(entity)
                }

                Some(entity)
            }
        }
        catch {
            case e: Exception => println("M:N dissociation failed: " + e.toString)

            None
        }
    }

    protected final def associate[A <: PersistableEntity](entity: A, relation: OneToMany[A]): Option[A] = {
        try
        {
            transaction {
                if (relation.find(e => e.id == entity.id) == None) {
                    relation.associate(entity)
                }

                Some(entity)
            }
        }
        catch {
            case e: Exception => println("1:N association failed: " + e.toString)

            None
        }
    }

    /**
      * Checks nothing, because SQUERYL fills constructors of Entities
      * with null values during DB schema initialization,
      * so all inner checks would fail.
      */
    override protected def checkConstructorPostConditions() {}

    //TODO: just UGLY copy of cz.payola.domain.entities.Entity.equals
    override def equals(other: Any): Boolean = {
        other match {
            case that: cz.payola.domain.entities.Entity => that.canEqual(this) && this.id == that.id
            case _ => false
        }
    }

    /*
    protected final def assign[A <: PersistableEntity](entity: A, relation: OneToMany[A]) {
        transaction {
            if (relation.find(e => e.id == entity.id) == None) {
                relation.assign(entity)
            }
        }
    }
    protected final def assign[A <: PersistableEntity](entity: A, relation: ManyToOne[A]) {
        transaction {
            if (relation.find(e => e.id == entity.id) == None) {
                relation.assign(entity)
            }
        }
    }
    */
}