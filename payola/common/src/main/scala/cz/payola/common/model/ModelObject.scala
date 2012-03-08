package cz.payola.common.model

import java.util.UUID

trait ModelObject {
    var objectID: String = UUID.randomUUID.toString
}
