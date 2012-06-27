package cz.payola.domain

import java.util.UUID

/**
  * The unique ID generator.
  */
object IDGenerator
{
    /**
      * Returns a new unique ID.
      */
    def newId: String = UUID.randomUUID.toString
}
