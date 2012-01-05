package cz.payola.web.client

import s2js.adapters.js.dom.{CanvasRenderingContext2D, Canvas}

class Layer(val canvas: Canvas, val context: CanvasRenderingContext2D) {

    def setWidth(newWidth: Double) {
        canvas.width = newWidth;
    }

    def setHeight(newHeight: Double) {
        canvas.height = newHeight;
    }

    def getWidth: Double = {
        canvas.width
    }

    def getHeight: Double = {
        canvas.height
    }
}
