package cz.payola.web.client.views.bootstrap

import cz.payola.web.client.views.elements.Div

class ProgressBar extends Div(Nil, "progress span5")
{
    private val progressValueBar = new Div(List(), "bar")
    progressValueBar.render(this.htmlElement)

    this.setActive(true)
    this.setStriped(true)
    this.setStyleToSuccess()

    setProgress(0.0)

    def setActive(active: Boolean) {
        if (active) {
            this.addCssClass("active")
        }else{
            this.removeCssClass("active")
        }
    }

    def setProgress(value: Double) {
        progressValueBar.setAttribute("style", "width: %d%%; height: 40px".format(value * 100))
    }

    def setStriped(striped: Boolean) {
        if (striped) {
            this.addCssClass("progress-striped")
        }else{
            this.removeCssClass("progress-striped")
        }
    }

    def setStyleToFailure(){
        this.removeCssClass("progress-success")
        this.addCssClass("progress-danger")
    }

    def setStyleToSuccess(){
        this.removeCssClass("progress-danger")
        this.addCssClass("progress-success")
    }
}
