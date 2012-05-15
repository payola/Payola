package cz.payola.domain

import java.util.UUID

object IDGenerator
{
    def newId: String = UUID.randomUUID.toString
}
