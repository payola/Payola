package cz.payola.web.client.views.graph.visual.animation

class AfterAnimationWithParams[T](functionToPerform: (T) => Unit, parameters: T) extends AfterAnimation
{
    def perform() {
        functionToPerform(parameters)
    }
}