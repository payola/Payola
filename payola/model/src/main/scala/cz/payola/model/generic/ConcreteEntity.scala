package cz.payola.model.generic

import java.util.UUID
import cz.payola.common.model.Entity

trait ConcreteEntity extends Entity
{
    // Lazy objectID creation TODO why lazy?
    var _id: Option[String] = None

    def id = {
        _id.getOrElse {
            _id = Some(UUID.randomUUID.toString)
            _id.get
        }
    }

    def id_=(objID: String) = _id = Some(objID)
}

