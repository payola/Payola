package s2js.adapters.js.dom

import s2js.adapters.js.browser.Window

abstract class CanvasRenderingContext2D extends CanvasContext
{
    val canvas: Canvas

    var fillStyle: Any

    var font: String = "10px sans-serif"

    var textAlign: String

    var globalAlpha: Double = 1f

    var globalCOmpositeOperation: String = "over"

    var lineCap: String = "square"

    var lineJoin: String = "miter"

    var lineWidth: Double = 1f

    var miterLimit: Double = 1f

    var shadowBlur: Double

    var shadowColor: String

    var shadowOffsetX: Double

    var shadowOffsetY: Double

    var strokeStyle: Any

    val DRAWWINDOW_DRAW_CARET: Long = 0x01

    val DRAWWINDOW_DO_NOT_FLUSH: Long = 0x02

    val DRAWWINDOW_DRAW_VIEW: Long = 0x04

    val DRAWWINDOW_USE_WIDGET_LAYERS = 0x08

    val DRAWWINDOW_ASYNC_DECODE_IMAGE = 0x10

    def arc(x: Double, y: Double, r: Double, startAngle: Double, endAngle: Double)

    def arc(x: Double, y: Double, r: Double, startAngle: Double, endAngle: Double, anticlockwise: Boolean)

    def arcTo(x1: Double, y1: Double, x2: Double, y2: Double, radius: Double)

    def beginPath()

    def bezierCurveTo(cp1x: Double, cp1y: Double, cp2x: Double, cp2y: Double, x: Double, y: Double)

    def clearRect(x: Double, y: Double, w: Double, h: Double)

    def clip()

    def closePath()

    def createPattern(image: Element, repetition: String): CanvasPattern

    def createRadialGradient(x0: Double, y0: Double, r0: Double, x1: Double, y1: Double, r1: Double): CanvasGradient

    def drawImage(image: Element, a1: Double, a2: Double)

    def drawImage(image: Element, a1: Double, a2: Double, a3: Double)

    def drawImage(image: Element, a1: Double, a2: Double, a3: Double, a4: Double)

    def drawImage(image: Element, a1: Double, a2: Double, a3: Double, a4: Double, a5: Double)

    def drawImage(image: Element, a1: Double, a2: Double, a3: Double, a4: Double, a5: Double, a6: Double)

    def drawImage(image: Element, a1: Double, a2: Double, a3: Double, a4: Double, a5: Double, a6: Double,
        a7: Double)

    def drawImage(image: Element, a1: Double, a2: Double, a3: Double, a4: Double, a5: Double, a6: Double, a7: Double,
        a8: Double)

    def drawWindow(window: Window, x: Double, y: Double, w: Double, h: Double, bgColor: String)

    def drawWindow(window: Window, x: Double, y: Double, w: Double, h: Double, bgColor: String, flags: Long)

    def fill()

    def fillRect(x: Double, y: Double, w: Double, h: Double)

    def fillText(text: String, x: Double, y: Double)

    def fillText(text: String, x: Double, y: Double, maxWidth: Double)

    def isPointInPath(x: Double, y: Double): Boolean

    def lineTo(x: Double, y: Double)

    def getImageData(x: Double, y: Double, w: Double, h: Double): ImageData

    def moveTo(x: Double, y: Double)

    def putImageData(imageData: ImageData, dx: Double, dy: Double)

    def quadraticCurveTo(cpx: Double, cpy: Double, x: Double, y: Double)

    def rect(x: Double, y: Double, w: Double, h: Double)

    def restore()

    def rotate(angle: Double)

    def save()

    def scale(x: Double, y: Double)

    def setTransform(m11: Double, m12: Double, m21: Double, m22: Double, dx: Double, dy: Double)

    def stroke()

    def strokeRect(x: Double, y: Double, w: Double, h: Double)

    def strokeText(text: String, x: Double, y: Double)

    def strokeText(text: String, x: Double, y: Double, maxWidth: Double)

    def translate(x: Double, y: Double)

    def measureText(text: String): TextMetrics
}
