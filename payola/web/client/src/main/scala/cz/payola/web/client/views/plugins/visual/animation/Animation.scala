package cz.payola.web.client.views.plugins.visual.animation

import collection.mutable.ListBuffer
import cz.payola.web.client.views.plugins.visual.{Vector, Point}
import s2js.adapters.js.browser.window
import cz.payola.web.client.views.plugins.visual.graph.{InformationView, VertexView}

class Animation[T](
    animationFunction: (ListBuffer[T], Option[Animation[_]], () => Unit, () => Unit, Option[Int]) => Unit,
    verticesToAnimate: ListBuffer[T], var followingAnimation: Option[Animation[_]], quickDraw: () => Unit,
    finalDraw: () => Unit, animationLength: Option[Int])
{
    def run() {
        animationFunction(verticesToAnimate, followingAnimation, quickDraw, finalDraw, animationLength)
    }

    def setFollowingAnimation(newFollowingAnimation: Animation[_]) {
        followingAnimation = Some(newFollowingAnimation)
    }
}

object Animation
{
    private val animationKillConst = 0

    private val animationPrepareConst = -1

    private var animationCurrentNumber = -1

    def hideText(verticesToAnimate: ListBuffer[InformationView], nextAnimation: Option[Animation[_]],
        quickDraw: () => Unit, finalDraw: () => Unit, animationLength: Option[Int]) {

        animationCurrentNumber = animationPrepareConst
        animateTextVisibility(verticesToAnimate, 1 - 0.1, -0.1, nextAnimation, quickDraw, finalDraw, animationLength)
    }

    def showText(verticesToAnimate: ListBuffer[InformationView], nextAnimation: Option[Animation[_]],
        quickDraw: () => Unit, finalDraw: () => Unit, animationLength: Option[Int]) {

        animationCurrentNumber = animationPrepareConst
        animateTextVisibility(verticesToAnimate, 0 + 0.1, 0.1, nextAnimation, quickDraw, finalDraw, animationLength)
    }

    def moveVertices(verticesToMove: ListBuffer[(VertexView, Point)], nextAnimation: Option[Animation[_]],
        quickDraw: () => Unit, finalDraw: () => Unit, animationLength: Option[Int]) { //TODO chovej se k tehle funkci jako k ostatnim animacnim funkcim

        val animationVViews = ListBuffer[AnimationVertexView]()
        verticesToMove.foreach {
            vToMove =>
                val translation = vToMove._1.position.createVector(vToMove._2)
                animationVViews += new AnimationVertexView(vToMove._1, translation, Vector.One)
        }
        animationCurrentNumber = animationPrepareConst
        animateTranslation(animationVViews, nextAnimation, quickDraw, finalDraw, None)
    }

    def moveGraphToUpperLeftCorner(vViews: ListBuffer[VertexView], nextAnimation: Option[Animation[_]],
        quickDraw: () => Unit, finalDraw: () => Unit, animationLength: Option[Int]) {
        var vector = Vector(Double.MaxValue, Double.MaxValue)
        //search for the minimum
        vViews.foreach {
            v: VertexView =>
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
        vViews.foreach {
            vView =>
                animationVViews += new AnimationVertexView(vView, vector, Vector.One)
        }
        animationCurrentNumber = animationPrepareConst
        animateTranslation(animationVViews, nextAnimation, quickDraw, finalDraw, None)
    }

    def flipGraph(vViews: ListBuffer[VertexView], nextAnimation: Option[Animation[_]], quickDraw: () => Unit,
        finalDraw: () => Unit, animationLength: Option[Int]) {
        var maxX: Double = Double.MinValue
        var minX: Double = Double.MaxValue
        var maxY: Double = Double.MinValue
        var minY: Double = Double.MaxValue

        //find out if flipping the graph helps anything...
        vViews.foreach {
            v: VertexView =>
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
            vViews.foreach {
                vView =>
                    animationVViews += new AnimationVertexView(vView,
                        vView.position.createVector(Point(vView.position.y, vView.position.x)), Vector.One)
            }

            animationCurrentNumber = animationPrepareConst
            animateTranslation(animationVViews, nextAnimation, quickDraw, finalDraw, None)
        } else {
            if (nextAnimation.isDefined) {
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

    private def animateTextVisibility(animVViews: ListBuffer[InformationView], visibilityCurrent: Double,
        visibilityStep: Double, nextAnimation: Option[Animation[_]], quickRedraw: () => Unit, finalRedraw: () => Unit,
        animationLength: Option[Int]) {

        if(animationCurrentNumber == animationKillConst) { //animation forcefully ended, set final visibility
            val visibilityFinal = if(math.signum(visibilityStep) < 0) { 0.0 } else { 1.0 }
            animVViews.foreach { info =>
                info.setTextVisibility(visibilityFinal)
            }
        } else {
            animVViews.foreach { info =>
                info.setTextVisibility(visibilityCurrent)
            }
        }

        if((visibilityCurrent == 0 && visibilityCurrent + visibilityStep < 0) ||
            (visibilityCurrent == 1 && 1 < visibilityCurrent + visibilityStep) || // animation is finished
            animationCurrentNumber == animationKillConst) {


            if(nextAnimation.isDefined) {
                nextAnimation.get.run()
            } else {
                finalRedraw()
            }
        } else {
            val visibilityNext =
                if(visibilityCurrent + visibilityStep < 0) {
                    0
                } else if(1 < visibilityCurrent + visibilityStep) {
                    1
                } else {
                    visibilityCurrent + visibilityStep
                }
            quickRedraw()
            setTimeout(() => animateTextVisibility(animVViews, visibilityNext, visibilityStep, nextAnimation,
                quickRedraw, finalRedraw, animationLength), 1)
        }
    }

    private def animateTranslation(animVViews: ListBuffer[AnimationVertexView], nextAnimation: Option[Animation[_]],
        quickDraw: () => Unit, finalDraw: () => Unit, animationLength: Option[Int]) {
        var translationFinished = true

        if(animationCurrentNumber == animationKillConst) { //if animation forcefully ended set the final positions
            animVViews.foreach{ vertex =>
                vertex.value.position = vertex.value.position + vertex.translation

            }
        } else {
            animVViews.foreach { vertex =>
                val loweredSpeed = Vector(getBiggestLowerSpeed(vertex.translation.x, vertex.speed.x),
                    getBiggestLowerSpeed(vertex.translation.y, vertex.speed.y))

                val tinyTrans = Vector(getNewTranslation(vertex.translation.x, loweredSpeed.x),
                    getNewTranslation(vertex.translation.y, loweredSpeed.y))

                vertex.value.position = vertex.value.position + tinyTrans
                vertex.translation = vertex.translation - tinyTrans
                vertex.speed = loweredSpeed + Vector.One

                if (vertex.translation.x != 0 || vertex.translation.y != 0) {
                    translationFinished = false
                }
            }
        }

        if (translationFinished || animationCurrentNumber == animationKillConst) {

            if (nextAnimation.isDefined) {
                nextAnimation.get.run()
            } else {
                finalDraw()
            }
        } else {
            quickDraw()
            setTimeout(() => animateTranslation(animVViews, nextAnimation, quickDraw, finalDraw, None), 5)
        }
    }

    private def getBiggestLowerSpeed(val1: Double, speed: Double): Double = {
        if (val1 != 0) {
            var _speed = speed
            while (scala.math.abs(scala.math.signum(val1)) * _speed > scala.math.abs(val1)) {
                _speed -= 1
            }

            _speed
        } else {
            0
        }
    }

    private def getNewTranslation(val1: Double, speed: Double): Double = {
        if (speed < 1) {
            val1
        } else {
            scala.math.signum(val1) * speed
        }
    }

    def clearCurrentTimeout() {
        animationCurrentNumber = animationKillConst
    }

    def setTimeout(function: () => Unit, timeout: Int) {
        animationCurrentNumber = window.setTimeout(function, timeout)
    }
}
