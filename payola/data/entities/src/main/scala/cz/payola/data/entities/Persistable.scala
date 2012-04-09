package cz.payola.data.entities

import PayolaDB._
import org.squeryl.KeyedEntity

trait Persistable extends KeyedEntity[String]{

    def persist
    /* = {
        if(isPersisted) {
            update
        }
        else {
            save
        }
    }
     */
}
