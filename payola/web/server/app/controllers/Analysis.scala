package controllers

import helpers.Secured

object Analysis extends PayolaController with Secured
{
    def detail(id: String) = IsAuthenticatedWithFallback ({ loggedUsername => rh =>
        val a = df.getAnalysisById(id)
        a.isDefined match {
            case true => Ok(views.html.analysis.detail(getUser(rh), a.get))
            case false => NotFound(views.html.errors.err404("The analysis does not exist."))
        }
    }, {  _ =>
        val a = df.getAnalysisById(id)
        a.isDefined match {
            case true => Ok(views.html.analysis.detail(None, a.get))
            case false => NotFound(views.html.errors.err404("The analysis does not exist."))
        }
    })
}