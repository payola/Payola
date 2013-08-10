package cz.payola.web.client.views.graph.visual.animation

import collection.mutable.ListBuffer
import s2js.adapters.browser._
import cz.payola.web.client.views.graph.visual.graph._
import cz.payola.web.client.views.graph.visual.graph.positioning.PositionHelper
import cz.payola.web.client.views.algebra._

class Animation[T](
    animationFunction: (T, Option[Animation[_]], () => Unit, () => Unit, Option[Int]) => Unit,
    toAnimate: T, var followingAnimation: Option[Animation[_]], quickDraw: () => Unit,
    finalDraw: () => Unit, var animationStepLength: Option[Int])
{
    /**
     * Launches this animation and after its completion the row of followingAnimations is launched.
     */
    def run() {
        animationFunction(toAnimate, followingAnimation, quickDraw, finalDraw, animationStepLength)
    }

    /**
     * Sets to this and all following animations animationStepLength to 0.
     */
    def finishAllAnimations() {
        this.animationStepLength = Some(0)
        if (followingAnimation.isDefined) {
            followingAnimation.get.finishAllAnimations()
        }
    }

    /**
     * Sets the next animation of this animation. If any animation was already set, this will overwrite it.
     * @param newFollowingAnimation aniamtion to set
     */
    def setFollowingAnimation(newFollowingAnimation: Animation[_]) {
        followingAnimation = Some(newFollowingAnimation)
    }

    /**
     * Appends next animation to the last animation of this row of animations.
     * @param nextFollowingAnimation animation to append
     */
    def addFollowingAnimation(nextFollowingAnimation: Animation[_]) {
        if (followingAnimation.isEmpty) {
            setFollowingAnimation(nextFollowingAnimation)
        } else {
            followingAnimation.get.addFollowingAnimation(nextFollowingAnimation)
        }
    }
}

object Animation
{
    private val animationKillConst = 0

    private val animationPrepareConst = -1

    private var animationCurrentNumber = -1

    /**
     * Animation function for performing function after (or during) animation. This animation does not animate anything,
     * it just performs the afterAnimationTool.
     * @param afterAnimationTool this represents the function with its parameters
     * @param nextAnimation following animation to perform
     * @param quickDraw function for redrawing whole drawing space quickly, it is used after every iteration
     * @param finalDraw function for redrawing whole drawing space after all animations have been finished
     * @param animationStepLength parameter for controlling time consumption fo animation steps;
     *                            if it is set to 0: animation is skipped
     *                            otherwise the animation is performed normally
     */
    def emptyAnimation(afterAnimationTool: AfterAnimation, nextAnimation: Option[Animation[_]],
        quickDraw: () => Unit, finalDraw: () => Unit, animationStepLength: Option[Int]) {

        afterAnimationTool.perform()

        if (nextAnimation.isDefined) {
            nextAnimation.get.run()
        } else {
            finalDraw()
        }
    }



    /**
     * Animation function for moving every single vertex of a graph to a new specified position.
     * @param verticesToMove list of vertices with their new positions
     * @param nextAnimation following animation to perform
     * @param quickDraw function for redrawing whole drawing space quickly, it is used after every iteration
     * @param finalDraw function for redrawing whole drawing space after all animations have been finished
     * @param animationStepLength parameter for controlling time consumption fo animation steps;
     *                            if it is set to 0: animation is skipped
     *                            otherwise the animation is performed normally
     */
    def moveVertices(verticesToMove: ListBuffer[(VertexViewElement, Point2D)], nextAnimation: Option[Animation[_]],
        quickDraw: () => Unit, finalDraw: () => Unit, animationStepLength: Option[Int]) {
        val animationVViews = ListBuffer[AnimationVertexView]()
        verticesToMove.foreach { vToMove =>
            val translation = vToMove._1.position.createVector(vToMove._2)
            animationVViews += new AnimationVertexView(vToMove._1, translation, Vector2D.One)
        }
        animationCurrentNumber = animationPrepareConst

        animateTranslation(animationVViews, nextAnimation, quickDraw, finalDraw, animationStepLength)
    }

    /**
     * Animation function for moving graph by provided function packed by PositionHelper
     * @param move _1 packed function for getting a vector to move _2 vertices to move
     * @param nextAnimation following animation to perform
     * @param quickDraw function for redrawing whole drawing space quickly, it is used after every iteration
     * @param finalDraw function for redrawing whole drawing space after all animations have been finished
     * @param animationStepLength parameter for controlling time consumption fo animation steps;
     *                            if it is set to 0: animation is skipped
     *                            otherwise the animation is performed normally
     */
    def moveGraphByFunction(move: (PositionHelper, ListBuffer[VertexViewElement]), nextAnimation: Option[Animation[_]],
        quickDraw: () => Unit, finalDraw: () => Unit, animationStepLength: Option[Int]) {
        moveGraphByVector(((move._1.getPositionCorrection(), move._2)), nextAnimation, quickDraw, finalDraw,
            animationStepLength)
    }

    /**
     * Animation function for moving graph by one vector.
     * @param move _1 vector to move by, _2 vertices to move
     * @param nextAnimation following animation to perform
     * @param quickDraw function for redrawing whole drawing space quickly, it is used after every iteration
     * @param finalDraw function for redrawing whole drawing space after all animations have been finished
     * @param animationStepLength parameter for controlling time consumption fo animation steps;
     *                            if it is set to 0: animation is skipped
     *                            otherwise the animation is performed normally
     */
    def moveGraphByVector(move: (Vector2D, ListBuffer[VertexViewElement]), nextAnimation: Option[Animation[_]],
        quickDraw: () => Unit, finalDraw: () => Unit, animationStepLength: Option[Int]) {
        val animationVViews = ListBuffer[AnimationVertexView]()
        move._2.foreach {
            vView =>
                animationVViews += new AnimationVertexView(vView, move._1, Vector2D.One)
        }
        animationCurrentNumber = animationPrepareConst
        animateTranslation(animationVViews, nextAnimation, quickDraw, finalDraw, animationStepLength)
    }

    /**
     * Animation function for moving graph to upper left corner of the drawing space (actually to Point2D(50, 25)).
     * @param vViews vertices to move
     * @param nextAnimation following animation to perform
     * @param quickDraw function for redrawing whole drawing space quickly, it is used after every iteration
     * @param finalDraw function for redrawing whole drawing space after all animations have been finished
     * @param animationStepLength parameter for controlling time consumption fo animation steps;
     *                            if it is set to 0: animation is skipped
     *                            otherwise the animation is performed normally
     */
    def moveGraphToUpperLeftCorner(vViews: ListBuffer[VertexViewElement], nextAnimation: Option[Animation[_]],
        quickDraw: () => Unit, finalDraw: () => Unit, animationStepLength: Option[Int]) {
        moveGraphByVector((Point2D(50, 25).toVector, vViews), nextAnimation, quickDraw, finalDraw, animationStepLength)
    }

    /**
     * Animation function for rotating vertices around line comming through the center of their group. The line is
     * parallel with line x=y.
     * @param move _1 position correction helper and _2 vertices to move
     * @param nextAnimation animation to perform after this one
     * @param quickDraw function for redrawing whole drawing space quickly, it is used after every iteration
     * @param finalDraw function for redrawing whole drawing space after all animations have been finished
     * @param animationStepLength parameter for controlling time consumption fo animation steps;
     *                            if it is set to 0: animation is skipped
     *                            otherwise the animation is performed normally
     */
    def flipGraph(move: (PositionHelper, ListBuffer[VertexViewElement]), nextAnimation: Option[Animation[_]],
        quickDraw: () => Unit,
        finalDraw: () => Unit, animationStepLength: Option[Int]) {
        var maxX: Double = Double.MinValue
        var minX: Double = Double.MaxValue
        var maxY: Double = Double.MinValue
        var minY: Double = Double.MaxValue

        //find out if flipping the graph helps anything...
        move._2.foreach { v =>
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
            val graphCenter = move._1.getPositionCorrection()
            move._2.foreach { vView =>
                val x = (vView.position.x + vView.position.y + graphCenter.x - graphCenter.y) / 2
                val y = vView.position.x + vView.position.y - x
                val newPosition = vView.position + vView.position.createVector(Point2D(x, y)) * 2

                animationVViews += new AnimationVertexView(vView, vView.position.createVector(newPosition), Vector2D.One)
            }

            animationCurrentNumber = animationPrepareConst
            animateTranslation(animationVViews, nextAnimation, quickDraw, finalDraw, animationStepLength)
        } else {
            if (nextAnimation.isDefined) {
                nextAnimation.get.run()
            }
        }
    }

    /**
     * Function performing translation of vertexViews. Each iteration moves all vertices a bit and sets a timeout for
     * next round. If the vertices are all at their new positions the nextAnimation is launched.
     * @param animVViews vertices to move
     * @param nextAnimation animation to run after this animation is completed
     * @param quickDraw function for redrawing whole drawing space quickly, it is used after every iteration
     * @param finalDraw function for redrawing whole drawing space after all animations have been finished
     * @param animationStepLength parameter for controlling time consumption fo animation steps;
     *                            if it is set to 0: animation is skipped
     *                            otherwise the animation is performed normally
     */
    private def animateTranslation(animVViews: ListBuffer[AnimationVertexView], nextAnimation: Option[Animation[_]],
        quickDraw: () => Unit, finalDraw: () => Unit, animationStepLength: Option[Int]) {
        var translationFinished = true

        if (animationCurrentNumber == animationKillConst && nextAnimation.isDefined) {
            nextAnimation.get.finishAllAnimations()
        }

        if (animationCurrentNumber == animationKillConst //if animation forcefully ended set the final positions
            || (animationStepLength.isDefined && animationStepLength.get == 0)) {
            //animation skipped
            animVViews.foreach { vertex =>
                vertex.value.position = vertex.value.position + vertex.translation
            }
        } else {
            animVViews.foreach { vertex =>
                val loweredSpeed = Vector2D(getBiggestLowerSpeed(vertex.translation.x, vertex.speed.x),
                    getBiggestLowerSpeed(vertex.translation.y, vertex.speed.y))

                val tinyTrans = Vector2D(getNewTranslation(vertex.translation.x, loweredSpeed.x),
                    getNewTranslation(vertex.translation.y, loweredSpeed.y))

                vertex.value.position = vertex.value.position + tinyTrans
                vertex.translation = vertex.translation - tinyTrans
                vertex.speed = loweredSpeed + Vector2D.One

                if (vertex.translation.x != 0 || vertex.translation.y != 0) {
                    translationFinished = false
                }
            }
        }

        if (translationFinished
            || animationCurrentNumber == animationKillConst //force end
            || (animationStepLength.isDefined && animationStepLength.get == 0)) {
            //skip animation

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

    /**
     * Calculates the biggest step by which the vertices can be moved in this iteration (of animateTranslation). The
     * step has to be lower than the translation.
     * @param translation how far is the final position of a vertex (whole translation of the animation)
     * @param speed how fast the vertex has to be moved
     * @return the highest speed that by which the vertex can be moved
     */
    private def getBiggestLowerSpeed(translation: Double, speed: Double): Double = {
        if (translation != 0) {
            var _speed = speed
            while ( /*math.abs(math.signum(translation)) * */ _speed > math.abs(translation)) {
                _speed -= 1
            }

            _speed
        } else {
            0
        }
    }

    /**
     * Function for getting translation for vertices for every iteration of the animateTranslation function. Decides
     * if the current iteration performs the final step of the whole animation (returns translationLength) or if
     * the speed is not big enough (speed * signum(translationLength) is returned)
     * @param translationLength how far the vertices have to be moved
     * @param speed length of current iteration's step - how far the vertices can be moved
     * @return
     */
    private def getNewTranslation(translationLength: Double, speed: Double): Double = {
        if (speed < 1) {
            translationLength
        } else {
            math.signum(translationLength) * speed
        }
    }

    /**
     * Method to forcefully end running animations. The current animation and those waiting in stack to be performed
     * will not be skipped, but launched with animationStepLength set to 0. This causes all animations to jump to
     * their final position and run their following animation.
     */
    def clearCurrentTimeout() {
        animationCurrentNumber = animationKillConst
    }

    /**
     * Setter of a next function iteration. The function will be launched after the timeout countdown is completed.
     * @param function what to run after the countdown
     * @param timeout how long to wait before the function will be launched
     */
    private def setTimeout(function: () => Unit, timeout: Int) {
        animationCurrentNumber = window.setTimeout(function, timeout)
    }
}
