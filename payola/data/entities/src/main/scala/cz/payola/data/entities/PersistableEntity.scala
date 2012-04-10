package cz.payola.data.entities

import PayolaDB._
import org.squeryl.KeyedEntity

trait PersistableEntity {
    self: KeyedEntity[_] =>
        def persist = {

            println(id)
        }
            /*
            if(isPersisted) {
                //PayolaDB.update(this)
                this.asInstanceOf[ActiveRecord[_]].update(this.asInstanceOf[KeyedEntity[_]])
                PayolaDB.update(this)
            }
            else {
                //PayolaDB.save(this)
                this.asInstanceOf[ActiveRecord[_]].save
                PayolaDB.save
            }
            */
}
