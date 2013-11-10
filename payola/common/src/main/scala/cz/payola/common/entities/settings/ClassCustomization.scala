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

    protected var _propertyCustomizations: immutable.Seq[PropertyCustomizationType]

    override def classNameText = "ontology class customization"

    /** URI of the property. */
    val uri: String

    protected var _fillColor: String

    protected var _radius: Int

    protected var _glyph: String

    protected var _labels: String

    protected var _conditionalValue: String

    protected var _orderNum: Int

    //following vals should be in an object, but that creates a too deep call resulting a maximum callstack size exceeded JS error
    private val acceptedLabelPrefix = "T"
    private val notAcceptedLabelPrefix = "F"
    private val userDefinedLabelPrefix = "U"
    private val labelValuePrefixEnd = "-"
    private val labelsDelimiter = ";"
    private val userDefinedAcceptedLabelPrefix = acceptedLabelPrefix + userDefinedLabelPrefix + labelValuePrefixEnd
    private val userDefinedIgnoredLabelPrefix = notAcceptedLabelPrefix + userDefinedLabelPrefix + labelValuePrefixEnd


    def isGroupCustomization = uri.startsWith("group_")

    def isConditionalCustomization = uri.startsWith("condition_")

    def getUri = if(isGroupCustomization) { uri.substring(6) }
    else if(isConditionalCustomization) { uri.substring(10) }
    else { uri }

    def hasId(id: String): Boolean = {
        uri == id
    }

    /** Customizations of properties of the class. */
    def propertyCustomizations = _propertyCustomizations

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

    /** Vertex labels. */
    def labels = _labels

    /**
     * Sets the vertex glyph.
     * @param value New value of the vertex glyph.
     */
    def labels_=(value: String) {
        _labels = value
    }

    /** Vertex uri condition. */
    def conditionalValue = _conditionalValue

    /**
     * Sets the vertex uri condition.
     * @param value New value of the vertex uri condition.
     */
    def conditionalValue_=(value: String) {
        _conditionalValue = value
    }

    def orderNumber = _orderNum

    def orderNumber_=(value: Int) {
        _orderNum = value
    }

    /** Vertex labels splitted. */
    def labelsSplitted: List[LabelItem] = {
        if(_labels == null || _labels == "") {
            List[LabelItem]()
        } else {
            val splitted = _labels.split(labelsDelimiter).toList
            splitted.map{ value => createLabelItem(value) }
        }
    }

    private def createLabelItem(value: String): LabelItem = {
        val accepted = isLabelAccepted(value)
        val userDefined = isLabelUserDefined(value)
        val text = if(isLabelUserDefined(value)) {
            value.substring(userDefinedAcceptedLabelPrefix.length)
        } else { //if(value.startsWith("T-") || value.startsWith("F-"))
            value.substring(2)
        }

        new LabelItem(text, userDefined, accepted)

    }

    private def isLabelUserDefined(value: String) =
        value.startsWith(userDefinedAcceptedLabelPrefix) || value.startsWith(userDefinedIgnoredLabelPrefix)

    private def isLabelAccepted(value: String) = value.startsWith(acceptedLabelPrefix)

}

class LabelItem(val value: String, val userDefined: Boolean, val accepted: Boolean) { }
