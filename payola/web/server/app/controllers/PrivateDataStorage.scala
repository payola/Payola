package controllers

import helpers.Secured
import cz.payola.domain.entities.User
import java.io.File
import cz.payola.web.shared.Payola

object PrivateDataStorage extends PayolaController with Secured
{
    /** Show the add graph page.
      *
      */
    def add() = authenticated{ user: User =>
        Ok(views.html.virtuoso.add(user))
    }

    def error(error: String) = authenticated { user: User =>
        Ok(views.html.virtuoso.error(user, error))
    }

    /** Saves a graph to the user's private data storage.
      *
      */
    def save() = authenticatedWithRequest { (user, request) =>
        // First thing to do is to find out if it's a URL-encoded form (i.e. posting just a URL string)
        // or multi-part and we're sending a whole file
        if (request.body.asFormUrlEncoded.isDefined) {
            val form = request.body.asFormUrlEncoded.get
            val urlOption = form.get("graphURL")
            assert(urlOption.isDefined, "No graph URL!")

            saveGraphAtURL(urlOption.get(0), user)
        }else if (request.body.asMultipartFormData.isDefined) {
            val form = request.body.asMultipartFormData.get
            val fileOption = form.file("graphFile")
            assert(fileOption.isDefined, "No graph XML file!")

            saveGraphFromFile(fileOption.get.ref.file, user)
        }else{
            Redirect(routes.PrivateDataStorage.error("Wrong form."))
        }
    }

    /** Saves a graph to the user's private data storage.
      *
      * @param graphURL Graph URL.
      * @param user User.
      */
    private def saveGraphAtURL(graphURL: String, user: User) = {
        try {
            Payola.model.payolaStorageModel.addGraphToUser(graphURL, user)
            Redirect(routes.Profile.index(user.name))
        }catch{
            case t: Throwable => Redirect(routes.PrivateDataStorage.error(t.getMessage))
        }
    }

    /** Saves a graph to the user's private data storage.
      *
      * @param file File containing RDF/XML representation of the graph.
      * @param user User.
      */
    private def saveGraphFromFile(file: File, user: User) = {
        try {
            Payola.model.payolaStorageModel.addGraphToUser(file, user)
            Redirect(routes.Profile.index(user.name))
        }catch{
            case t: Throwable => Redirect(routes.PrivateDataStorage.error(t.getMessage))
        }
    }

}
