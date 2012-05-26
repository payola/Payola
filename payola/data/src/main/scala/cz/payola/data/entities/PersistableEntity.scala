package cz.payola.data.entities

import org.squeryl.{Query, KeyedEntity}
import org.squeryl.PrimitiveTypeMode._
import collection.mutable.ArrayBuffer
import org.squeryl.dsl.{ManyToOne, OneToMany, ManyToMany}

trait PersistableEntity extends cz.payola.domain.entities.Entity with KeyedEntity[String]
{
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

    protected final def associate[A <: PersistableEntity](entity: A, relation: ManyToMany[A,_]) {
        try
        {
            transaction {
                if (relation.find(e => e.id == entity.id) == None) {
                    relation.associate(entity)
                }
            }
        }
        catch {
            case e: Exception => println("M:N association failed: " + e.toString)
        }
    }

    protected final def assign[A <: PersistableEntity](entity: A, relation: ManyToMany[A,_]) {
        try
        {
            transaction {
                if (relation.find(e => e.id == entity.id) == None) {
                    relation.assign(entity)
                }
            }
        }
        catch {
            case e: Exception => println("M:N assign failed: " + e.toString)
        }
    }

    protected final def dissociate[A <: PersistableEntity](entity: A, relation: ManyToMany[A,_]) {
        try
        {
            transaction {
                if (relation.find(e => e.id == entity.id) != None) {
                    relation.dissociate(entity)
                }
            }
        }
        catch {
            case e: Exception => println("M:N dissociation failed: " + e.toString)
        }
    }

    protected final def associate[A <: PersistableEntity](entity: A, relation: OneToMany[A]) {
        try
        {
            transaction {
                if (relation.find(e => e.id == entity.id) == None) {
                    relation.associate(entity)
                }
            }
        }
        catch {
            case e: Exception => println("1:N association failed: " + e.toString)
        }
    }

    override protected def checkConstructorPostConditions() {}

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