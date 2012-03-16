package s2js.adapters.js.dom

import collection.mutable.ListBuffer

abstract class ImageData
{
    //TODO: unsigned long
    val width: Long

    val height: Long

    val data: CanvasPixelArray
}
