package cz.payola.data.entities

import org.squeryl.KeyedEntity

trait PersistableEntity extends KeyedEntity[String]
{
    def persist() {
        PayolaDB.persist(this)
    }
}
