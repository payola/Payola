package cz.payola.common.entities.settings

import cz.payola.common.Entity

/**
  * Customization of appearance of an corresponding to particular class property.
  */
trait PropertyCustomization extends Entity
{
    /** URI of the property. */
    val uri: String

    protected var _strokeColor: String

    protected var _strokeWidth: Int

    override def classNameText = "ontology property customization"

    /** Stroke color of the edge. */
    def strokeColor = _strokeColor

    /**
      * Sets stroke color of the edge.
      * @param value New value of the stroke color.
      */
    def strokeColor_=(value: String) {
        _strokeColor = value
    }

    /** Stroke width of the edge. */
    def strokeWidth = _strokeWidth

    /**
      * Sets stroke width of the edge.
      * @param value New value of the stroke width.
      */
    def strokeWidth_=(value: Int) {
        _strokeWidth = value
    }
}
