package controllers

import helpers.Secured
import cz.payola.data.entities.dao.FakeAnalysisDAO

/**
  *
  * @author jirihelmich
  * @created 5/15/12 1:38 PM
  * @package controllers
  */

object Analysis extends PayolaController with Secured
{
    def detail(id: String) = IsAuthenticatedWithFallback ({ loggedUsername => rh =>
        val a = Some(FakeAnalysisDAO.analysis)
        a.isDefined match {
            case true => Ok(views.html.analysis.detail(getUser(rh), a.get))
            case false => NotFound(views.html.errors.err404("The analysis does not exist."))
        }
    }, {  _ =>
        val a = Some(FakeAnalysisDAO.analysis)
        a.isDefined match {
            case true => Ok(views.html.analysis.detail(None, a.get))
            case false => NotFound(views.html.errors.err404("The analysis does not exist."))
        }
    })
}
