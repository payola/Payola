package cz.payola.model

import cz.payola.domain.entities.User
import cz.payola.data.dao._
import cz.payola.data.entities.dao._
import cz.payola.domain.entities.analyses.PluginInstance
import cz.payola.common.rdf.Graph

class DataFacade
{
    val userDAO = new UserDAO
    val analysisDAO = new AnalysisDAO
    val groupDAO = new GroupDAO

    def getUserByCredentials(username: String, password: String) : Option[User] = {
        userDAO.getUserByCredentials(username, cryptPassword(password))
    }

    def getUserByUsername(username: String) : Option[User] = {
        userDAO.getUserByUsername(username)
    }

    def register(username: String, password: String): Unit = {
        val u = new cz.payola.domain.entities.User(username)
        u.password = cryptPassword(password)
        u.email = username

        userDAO.persist(u)
    }

    def getAnalysisById(id: String) = {
        analysisDAO.getById(id)
    }

    def topAnalyses = {
        analysisDAO.getTopAnalyses()
    }

    def getPublicAnalysesByOwner(o: User) = {
        analysisDAO.getTopAnalysesByUser(o.id)
    }

    def getGraph(uri: String) : Graph = {
        null
    }

    def getPublicDataSources(count: Int, skip: Int = 0) : Seq[PluginInstance] = {
        //TODO this should return unique endpoints by EndpointURL
        FakeAnalysisDAO.analysis.pluginInstances.filter(i => i.plugin.name == "SPARQL Endpoint")
    }

    def getDataSourceById(id: String) : Option[PluginInstance] = {
        FakeAnalysisDAO.analysis.pluginInstances.filter(i => i.plugin.name == "SPARQL Endpoint").headOption
    }

    def getGroupsByOwner(user: Option[User]) = {
        if (!user.isDefined){
            List()
        }else{
            groupDAO.getByOwnerId(user.get.id)
        }
    }

    //TODO bcrypt
    private def cryptPassword(password: String, method: String = "SHA-1") : String = {
        val md = java.security.MessageDigest.getInstance(method)
        val digest = md.digest(password.toCharArray.map(_.toByte))
        new String(digest.map(_.toChar))
    }
}
