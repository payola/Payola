package cz.payola.web.client.views.bootstrap

import cz.payola.web.client.views.elements.Div

class ProgressBar extends Div(Nil, "progress")
{
    private val progressValueBar = new Div(List(), "progress-bar")
    progressValueBar.render(this.htmlElement)
    progressValueBar.setAttribute("role","progressbar")
    progressValueBar.setAttribute("aria-valuenow","0")
    progressValueBar.setAttribute("aria-valuemin","0")
    progressValueBar.setAttribute("aria-valuemax","100")

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
        progressValueBar.setAttribute("aria-valuenow",(value * 100).toString)
    }

    def setStriped(striped: Boolean) {
        if (striped) {
            this.addCssClass("progress-striped")
        }else{
            this.removeCssClass("progress-striped")
        }
    }

    def setStyleToFailure(){
        progressValueBar.removeCssClass("progress-bar-success")
        progressValueBar.addCssClass("progress-bar-danger")
    }

    def setStyleToSuccess(){
        progressValueBar.removeCssClass("progress-bar-danger")
        progressValueBar.addCssClass("progress-bar-success")
    }
}
