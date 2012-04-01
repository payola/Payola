package cz.payola.web.client.views.plugins.visual.techniques

import collection.mutable.ListBuffer
import cz.payola.web.client.views.plugins.visual.{Vector, Point}
import s2js.adapters.js.browser.window
import cz.payola.web.client.views.plugins.visual.graph.{EdgeView, VertexView}

class Animation(animationFunction: (ListBuffer[VertexView], Option[Animation],
    () => Unit, () => Unit) => Unit,
    verticesToAnimate: ListBuffer[VertexView],
    followingAnimation: Option[Animation], quickDraw: () => Unit,  finalDraw: () => Unit) {

    def run() {
        animationFunction(verticesToAnimate, followingAnimation, quickDraw, finalDraw)
    }

}

object Animation {
    def moveVertices(verticesToMove: ListBuffer[(VertexView, Point)], nextAnimation: Option[Animation],
        quickDraw: () => Unit, finalDraw: () => Unit) {

        val animationVViews = ListBuffer[AnimationVertexView]()
        verticesToMove.foreach{ vToMove =>
            val translation = vToMove._1.position.createVector(vToMove._2)
            animationVViews += new AnimationVertexView(vToMove._1, translation, Vector.One)
        }
        animateTranslation(animationVViews, nextAnimation, quickDraw, finalDraw)
    }

    def moveGraphToUpperLeftCorner(vViews: ListBuffer[VertexView],
        nextAnimation: Option[Animation], quickDraw: () => Unit, finalDraw: () => Unit) {

            var vector = Vector(Double.MaxValue, Double.MaxValue)
            //search for the minimum
            vViews.foreach {v: VertexView =>
                if (v.position.x < vector.x) {
                    vector = Vector(v.position.x, vector.y)
                }
                if (v.position.y < vector.y) {
                    vector = Vector(vector.x, v.position.y)
                }
            }

            //move the graph...actually to the [50,50] coordinate, that no vertices are cut off by the screen edge
            vector = (vector) * (-1) + Vector(50, 50)
            val animationVViews = ListBuffer[AnimationVertexView]()
            vViews.foreach{ vView =>
                animationVViews += new AnimationVertexView(vView, vector, Vector.One)
            }
            animateTranslation(animationVViews, nextAnimation, quickDraw, finalDraw)
        }

    def flipGraph(vViews: ListBuffer[VertexView],
        nextAnimation: Option[Animation], quickDraw: () => Unit, finalDraw: () => Unit) {
        
        var maxX: Double = Double.MinValue
        var minX: Double = Double.MaxValue
        var maxY: Double = Double.MinValue
        var minY: Double = Double.MaxValue
        
        //find out if flipping the graph helps anything...
        vViews.foreach {v: VertexView =>
            if (v.position.x > maxX) {
                maxX = v.position.x
            } else if (v.position.x < minX) {
                minX = v.position.x
            }
            
            if (v.position.y > maxY) {
                maxY = v.position.y
            } else if (v.position.y < minY) {
                minY = v.position.y
            }
        }
        
        if (maxX - minX < maxY - minY) {
        //flip it
            
            val animationVViews = ListBuffer[AnimationVertexView]()
            vViews.foreach{ vView =>
                animationVViews += new AnimationVertexView(vView,
                    vView.position.createVector(Point(vView.position.y, vView.position.x)), Vector.One)
            }
            
            animateTranslation(animationVViews, nextAnimation, quickDraw, finalDraw)
        } else {
            if(nextAnimation.isDefined) {
                nextAnimation.get.run()
            }
        }
    }
    
    /*def timeoutedAnimation(vViews: ListBuffer[VertexView], eViews: ListBuffer[EdgeView],
        nextAnimation: Option[Animation], quickDraw: () => Unit, finalDraw: () => Unit) {

        if(nextAnimation.isDefined) {
            setTimeout(nextAnimation.get.run(), )
        } //TODO remove
    }*/

    private def animateTranslation(animVViews: ListBuffer[AnimationVertexView], nextAnimation: Option[Animation],
        quickDraw: () => Unit, finalDraw: () => Unit) {

        var translationFinished = true

        animVViews.foreach{ vertex =>
            val loweredSpeed = Vector(getBiggestLowerSpeed(vertex.translation.x, vertex.speed.x),
                getBiggestLowerSpeed(vertex.translation.y, vertex.speed.y))

            val tinyTrans = Vector(getNewTranslation(vertex.translation.x, loweredSpeed.x),
                getNewTranslation(vertex.translation.y, loweredSpeed.y))

            vertex.value.position = vertex.value.position + tinyTrans
            vertex.translation = vertex.translation - tinyTrans
            vertex.speed = loweredSpeed + Vector.One

            if(vertex.translation.x != 0 || vertex.translation.y != 0) {
                translationFinished = false
            }
        }

        if(translationFinished) {

            if(nextAnimation.isDefined) {
                nextAnimation.get.run()
            } else {
                finalDraw()
            }
        } else {
            quickDraw()
            setTimeout(() => animateTranslation(animVViews, nextAnimation, quickDraw, finalDraw), 5)
        }
    }

    private def getBiggestLowerSpeed(val1: Double, speed: Double): Double = {

        if(val1 != 0) {
            var _speed = speed
            while(scala.math.abs(scala.math.signum(val1)) * _speed > scala.math.abs(val1)) {
                _speed -= 1
            }

            _speed
        } else {
            0
        }
    }

    private def getNewTranslation(val1: Double, speed: Double): Double = {
        if(speed < 1) {
            val1
        } else {
            scala.math.signum(val1) * speed
        }
    }
    
    def setTimeout(function: () => Unit, timeout: Int) {
        window.setTimeout(function, timeout)
    }
}
