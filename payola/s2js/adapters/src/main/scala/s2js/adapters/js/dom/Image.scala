package s2js.adapters.js.dom

abstract class Image extends Element
{
    /**
      * Required. Specifies the URL of an image
      */
    var src: String

    /**
      * Required. Specifies an alternate text for an image
      */
    var alt: String

    /**
      * Specifies the height of an image
      */
    var height: Double

    /**
      * Specifies the width of an image
      */
    var width: Double
}
