package cz.payola.model.generic

import java.util.UUID
import cz.payola.common.model.ModelObject

class ConcreteModelObject extends ModelObject {
    var objectID: String = UUID.randomUUID.toString
}

