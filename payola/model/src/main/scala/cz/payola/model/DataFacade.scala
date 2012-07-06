package cz.payola.model

import cz.payola.domain.entities.User
import cz.payola.data.dao._
import cz.payola.domain.entities.plugins.DataSource
import cz.payola.common.rdf.Graph
import cz.payola.domain.entities.Group
import cz.payola.domain.entities.plugins.concrete.data.SparqlEndpoint

class DataFacade
{
    val userDAO = new UserDAO
    val analysisDAO = new AnalysisDAO
    val groupDAO = new GroupDAO
    val dataSourceDAO = new DataSourceDAO
    val pluginDAO = new PluginDAO

    private val GROUPS_COUNT_MAX_COUNT_DEFAULT = 10

    def getPlugins() = {
        pluginDAO.getAll().map(_.createPlugin())
    }

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

        val instance = (new SparqlEndpoint).createInstance().setParameter("EndpointURL", "")

        val dataSource = DataSource("DBPedia", None, instance)

        val query = """
        CONSTRUCT {
            ?person rdf:type <http://xmlns.com/foaf/0.1/Person> .
        } WHERE {
            ?person rdf:type <http://xmlns.com/foaf/0.1/Person> .
        }
        LIMIT 50
        """

        dataSource.executeQuery(query)

    }

    def getPublicDataSources(count: Int, skip: Int = 0) : Seq[DataSource] = {
        dataSourceDAO.getPublicDataSources(Some(new PaginationInfo(skip, count)))
    }

    def getDataSourceById(id: String) : Option[DataSource] = {
        dataSourceDAO.getById(id)
    }

    def getGroupsByOwner(user: Option[User], maxCount: Int = GROUPS_COUNT_MAX_COUNT_DEFAULT) = {
        if (!user.isDefined){
            List()
        }else{
            groupDAO.getByOwnerId(user.get.id, Some(new PaginationInfo(0, maxCount)))
        }
    }

    def createGroup(name: String, owner: User) = {
        val g = new Group(name, owner)
        groupDAO.persist(g)
    }

    def getGroupByOwnerAndId(shouldBeOwner: User, groupId: String) : Option[Group] = {
        val group = groupDAO.getById(groupId)

        if (group.isDefined)
        {
            if (group.get.owner.equals(shouldBeOwner))
            {
                group
            }else{
                None
            }
        }else{
            None
        }
    }

    def getAllUsers() = {
        userDAO.getAll()
    }

    //TODO bcrypt
    private def cryptPassword(password: String, method: String = "SHA-1") : String = {
        val md = java.security.MessageDigest.getInstance(method)
        val digest = md.digest(password.toCharArray.map(_.toByte))
        new String(digest.map(_.toChar))
    }
}
