package cz.payola.common.entities.settings

import scala.collection.immutable
import cz.payola.common.Entity

/**
  * Customization of appearance of a vertex with particular type.
  */
trait ClassCustomization extends Entity
{
    /** Type of the property customizations in the class customization. */
    type PropertyCustomizationType <: PropertyCustomization

    /** URI of the class. */
    val uri: String

    protected var _fillColor: String

    protected var _radius: Int

    protected var _glyph: String

    protected var _labels: String

    protected var _propertyCustomizations: immutable.Seq[PropertyCustomizationType]

    override def classNameText = "ontology class customization"

    /** Fill color of the vertex. */
    def fillColor = _fillColor

    /**
      * Sets fill color of the vertex.
      * @param value New value of the fill color.
      */
    def fillColor_=(value: String) {
        _fillColor = value
    }

    /** Radius of the vertex. */
    def radius = _radius

    /**
      * Sets radius of the vertex.
      * @param value New value of the radius.
      */
    def radius_=(value: Int) {
        _radius = value
    }

    /** Vertex glyph. */
    def glyph = _glyph

    /**
     * Sets the vertex glyph.
     * @param value New value of the vertex glyph.
     */
    def glyph_=(value: String) {
        validate(value.length <= 1, "glyph", "Glyph must be string with maximal lenght 1")
        _glyph = value
    }

    /** Vertex labels splitted. */
    def labelsSplitted: List[LabelItem] = {
        if(_labels == null || _labels == "") {
            List[LabelItem]()
        } else {
            val splitted = _labels.split(';').toList
            splitted.take(splitted.length - 1).map{ value => createLabelItem(value) }
        }
    }

    private def createLabelItem(value: String): LabelItem = {
        val accepted = value.startsWith("T")
        val userDefined = value.startsWith("FU-") || value.startsWith("TU-")
        val text = if(value.startsWith("TU-") || value.startsWith("FU-")) {
            value.substring(3)
        } else { //if(value.startsWith("T-") || value.startsWith("F-"))
            value.substring(2)
        }

        new LabelItem(text, userDefined, accepted)

    }

    /** Vertex labels. */
    def labels = _labels

    /**
     * Sets the vertex glyph.
     * @param value New value of the vertex glyph.
     */
    def labels_=(value: String) {
        _labels = value
    }

    /** Customizations of properties of the class. */
    def propertyCustomizations = _propertyCustomizations
}

class LabelItem(val value: String, val userDefined: Boolean, val accepted: Boolean) { }
