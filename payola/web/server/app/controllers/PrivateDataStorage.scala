package controllers

import helpers.Secured
import cz.payola.domain.entities.User
import java.io.File
import cz.payola.web.shared.Payola
import cz.payola.domain.rdf.RdfRepresentation

object PrivateDataStorage extends PayolaController with Secured
{
    /**
     * Show the add graph page.
     */
    def add() = authenticatedWithRequest { (user, request) =>
        Ok(views.html.virtuoso.add(user)(request.flash))
    }

    /**
     * Saves a graph to the user's private data storage.
     */
    def saveFromFile() = authenticatedWithRequest { (user, request) =>
        if (request.body.asMultipartFormData.isDefined) {
            val form = request.body.asMultipartFormData.get
            val fileOption = form.file("graphFile")
            assert(fileOption.isDefined, "No graph XML file!")

            val t = if (fileOption.get.filename.endsWith(".ttl")) {
                RdfRepresentation.Turtle
            } else {
                // We currently support only TTL and RDF/XML. Anything that's not
                // one of those formats can be treated as RDF/XML as the upload's going
                // to fail anyway.
                RdfRepresentation.RdfXml
            }

            saveGraphFromFile(fileOption.get.ref.file, user, t)
        } else {
            Redirect(routes.PrivateDataStorage.add()).flashing("error" -> "Wrong form.")
        }
    }

    def saveFromURL() = authenticatedWithRequest { (user, request) =>
        if (request.body.asFormUrlEncoded.isDefined) {
            val form = request.body.asFormUrlEncoded.get
            val urlOption = form.get("graphURL")
            assert(urlOption.isDefined, "No graph URL!")

            saveGraphAtURL(urlOption.get(0), user)
        } else {
            Redirect(routes.PrivateDataStorage.add()).flashing("error" -> "Wrong form.")
        }
    }

    /**
     * Saves a graph to the user's private data storage.
     * @param graphURL Graph URL.
     * @param user User.
     */
    private def saveGraphAtURL(graphURL: String, user: User) = {
        try {
            Payola.model.payolaStorageModel.addGraphToUser(graphURL, user)
            Redirect(routes.PrivateDataStorage.add()).flashing("success" -> "Successfully saved graph.")
        } catch {
            case t: Throwable => Redirect(routes.PrivateDataStorage.add()).flashing("error" -> t.getMessage)
        }
    }

    /**
     * Saves a graph to the user's private data storage.
     * @param file File containing RDF/XML representation of the graph.
     * @param user User.
     */
    private def saveGraphFromFile(file: File, user: User, rdfType: RdfRepresentation.Type) = {
        try {
            Payola.model.payolaStorageModel.addGraphToUser(file, user, rdfType)
            Redirect(routes.PrivateDataStorage.add()).flashing("success" -> "Successfully saved graph.")
        } catch {
            case t: Throwable => {
                Redirect(routes.PrivateDataStorage.add()).flashing(
                    "error" -> "The uploaded file is not a valid RDF/XML or TTL file (%s).".format(t.getMessage)
                )
            }
        }
    }
}
