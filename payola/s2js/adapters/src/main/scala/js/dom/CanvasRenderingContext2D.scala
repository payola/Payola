package s2js.adapters.js.dom

import s2js.adapters.js.browser.Window

abstract class CanvasRenderingContext2D extends CanvasContext {
    val canvas: Canvas = null

    var fillStyle: Object = null

    var font: String = "10px sans-serif"

    var globalAlpha: Float = 1f

    var globalCOmpositeOperation: String = "over"

    //TODO string enum?
    var lineCap: String = "square"

    var lineJoin: String = "miter"

    var lineWidth: Float = 1f

    var miterLimit: Float = 1f

    var shadowBlur: Float

    var shadowColor: String

    var shadowOffsetX: Float

    var shadowOffsetY: Float

    var strokeStyle: Object

    val DRAWWINDOW_DRAW_CARET: Long = 0x01

    val DRAWWINDOW_DO_NOT_FLUSH: Long = 0x02

    val DRAWWINDOW_DRAW_VIEW: Long = 0x04

    val DRAWWINDOW_USE_WIDGET_LAYERS = 0x08

    val DRAWWINDOW_ASYNC_DECODE_IMAGE = 0x10

    def arc(x: Float, y: Float, r: Float, startAngle: Float, endAngle: Float) {}

    def arc(x: Float, y: Float, r: Float, startAngle: Float, endAngle: Float, anticlockwise: Boolean) {}

    def arcTo(x1: Float, y1: Float, x2: Float, y2: Float, radius: Float) {}

    def beginPath() {}

    def bezierCurveTo(cp1x: Float, cp1y: Float, cp2x: Float, cp2y: Float, x: Float, y: Float) {}

    def clearRect(x: Float, y: Float, w: Float, h: Float) {}

    def clip() {}

    def closePath() {}

    def createPattern(image: Element, repetition: String): CanvasPattern {}

    def createRadialGradient(x0: Float, y0: Float, r0: Float, x1: Float, y1: Float, r1: Float): CanvasGradient {}

    def drawImage(image: Element, a1: Float, a2: Float) {}

    def drawImage(image: Element, a1: Float, a2: Float, a3: Float) {}

    def drawImage(image: Element, a1: Float, a2: Float, a3: Float, a4: Float) {}

    def drawImage(image: Element, a1: Float, a2: Float, a3: Float, a4: Float, a5: Float) {}

    def drawImage(image: Element, a1: Float, a2: Float, a3: Float, a4: Float, a5: Float, a6: Float) {}

    def drawImage(image: Element, a1: Float, a2: Float, a3: Float, a4: Float, a5: Float, a6: Float,
        a7: Float) {}

    def drawImage(image: Element, a1: Float, a2: Float, a3: Float, a4: Float, a5: Float, a6: Float, a7: Float,
        a8: Float) {}

    def drawWindow(window: Window, x: Float, y: Float, w: Float, h: Float, bgColor: String) {}

    def drawWindow(window: Window, x: Float, y: Float, w: Float, h: Float, bgColor: String, flags: Long) {}

    def fill() {}

    def fillRect(x: Float, y: Float, w: Float, h: Float) {}

    def fillText(text: String, x: Float, y: Float) {}

    def fillText(text: String, x: Float, y: Float, maxWidth: Float) {}

    def isPointInPath(x: Float, y: Float): Boolean {}

    def lineTo(x: Float, y: Float) {}

    def getImageData(x: Double, y: Double, w: Double, h: Double): ImageData

    def moveTo(x: Float, y: Float) {}

    def putImageData() {}

    def quadraticCurveTo(cpx: Float, cpy: Float, x: Float, y: Float) {}

    def rect(x: Float, y: Float, w: Float, h: Float) {}

    def restore() {}

    def rotate(angle: Float) {}

    def save() {}

    def scale(x: Float, y: Float) {}

    def setTransform(m11: Float, m12: Float, m21: Float, m22: Float, dx: Float, dy: Float) {}

    def stroke() {}

    def strokeRect(x: Float, y: Float, w: Float, h: Float) {}

    def strokeText(text: String, x: Float, y: Float) {}

    def strokeText(text: String, x: Float, y: Float, maxWidth: Float) {}

    def translate(x: Float, y: Float) {}
}