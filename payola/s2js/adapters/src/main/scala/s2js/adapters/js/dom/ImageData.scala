package s2js.adapters.js.dom

abstract class ImageData {
    //TODO: unsigned long
    val width: Long

    val height: Long

    val data: CanvasPixelArray
}
