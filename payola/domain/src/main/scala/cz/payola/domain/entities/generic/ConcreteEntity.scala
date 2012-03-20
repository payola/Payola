package cz.payola.domain.entities.generic

import java.util.UUID
import cz.payola.common.entities.Entity

trait ConcreteEntity extends Entity
{
    // Lazy objectID creation
    val _id: String = UUID.randomUUID.toString

    // TODO move to data?
    /* override def id = {
        _id.getOrElse {
            _id = Some(UUID.randomUUID.toString)
            _id.get
        }
    }*/
}

