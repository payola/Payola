package cz.payola.web.client.views.graph.visual.animation

class AfterAnimationParamLess(functionToPerform: () => Unit) extends AfterAnimation
{
    def perform() {
        functionToPerform()
    }
}
