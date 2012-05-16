package cz.payola.data.entities

import cz.payola.data.entities.dao._
import cz.payola.data.entities.analyses.parameters._
import cz.payola.data.entities.analyses._
import cz.payola.domain.entities.analyses.plugins.data.SparqlEndpoint
import cz.payola.domain.entities.analyses.plugins.query._
import cz.payola.domain.entities.analyses.plugins._
import cz.payola.domain.entities.analyses.evaluation.Success

object TestObject
{
    println("Connecting ...")
    assert (PayolaDB.connect())

    val userDao = new UserDAO()

    val groupDao = new GroupDAO()

    val analysisDao = new AnalysisDAO()

    val plugDao = new PluginDAO

    val plugInstDao = new PluginInstanceDAO()

    val pibDao = new PluginInstanceBindingDAO()

    val bParDao = new BooleanParameterDAO()

    val bParInstDao = new BooleanParameterInstanceDAO()

    val fParDao = new FloatParameterDAO()

    val fParInstDao = new FloatParameterInstanceDAO()

    val iParDao = new IntParameterDAO()

    val iParInstDao = new IntParameterInstanceDAO()

    val sParDao = new StringParameterDAO()

    val sParInstDao = new StringParameterInstanceDAO()

    val user = new User("name", "pwd1", "email1")

    val group1 = new Group("group", user)

    val group2 = new Group("group2", user)

    val analysis = new Analysis("an", Some(user))

    val bPar = new BooleanParameter("bPar", "bPar", true)

    val bParInst = new BooleanParameterValue("bParVal", bPar, false)

    val fPar = new FloatParameter("fPar", "fPar", -1.0f)

    val fParInst = new FloatParameterValue("fParVal", fPar, 1.0f)

    val iPar = new IntParameter("iParVal", "iPar", -1)

    val iParInst = new IntParameterValue("iParVal", iPar, 1)

    val sPar = new StringParameter("sPar", "sPar", "empty")

    val sParInst = new StringParameterValue("sParVal", sPar, "string")

    def main(args: Array[String]) {
        
        println("Creating schema ...")
        PayolaDB.createSchema();                                         
        
        println("Persisting user ...")
        userDao.persist(user)

        // Update test
        user.name += "1"
        userDao.persist(user)

        val u = userDao.getById(user.id)
        assert(u != None)
        assert(u.get.id == user.id)
        assert(u.get.name == user.name)
        assert(u.get.password == user.password)
        assert(u.get.email == user.email)

        // Test userDao
        assert(userDao.findByUsername("n", 0, 1)(0).id == user.id)
        assert(userDao.findByUsername("a", 0, 1)(0).id == user.id)
        assert(userDao.findByUsername("1", 0, 1)(0).id == user.id)
        assert(userDao.findByUsername(user.name, 0, 1)(0).id == user.id)
        assert(userDao.findByUsername("invalid name").size == 0)
        assert(userDao.getUserByCredentials(user.name, user.password).get.id == user.id)
        assert(userDao.getUserByCredentials("invalid", "credientals") == None)                                         
                
        println("Persisting group ...")
        groupDao.persist(group1)
        groupDao.persist(group2)

        val g = groupDao.getById(group1.id)
        assert(g != None)
        assert(g.get.id == group1.id)
        assert(g.get.name == group1.name)
        assert(g.get.ownerId.get == user.id)
        
        group1.addMember(user)
        group2.addMember(user)

        assert(user.memberGroups.size == 2)
        assert(user.ownedGroups.size == 2)

        assert(group1.members(0).name == user.name, "Invalid group owner")
        assert(group2.members(0).name == user.name, "Invalid group2 owner")                                                                            
        
        println("Persisting analysis ...")
        analysisDao.persist(analysis)

        // Update test
        analysis.name += "1"
        analysisDao.persist(analysis)

        assert(user.ownedAnalyses.size == 1)

        val a = analysisDao.getById(analysis.id)
        assert(a != None)
        assert(a.get.id == analysis.id)
        assert(analysisDao.getById("") == None)

        println("   Derived plugins test")
        testDerivedPlugins()

        println("Pagination ...")
        assert(userDao.getAll().size == 1)
        assert(groupDao.getAll().size == 2)
        assert(groupDao.getAll(1, 2).size == 1)
        assert(groupDao.getAll(2, 5).size == 0)                                 
        assert(groupDao.getAll(1, 0).size == 0)
    }
    
    def testDerivedPlugins() {         
        val sparqlEndpointPlugin = new SparqlEndpoint
        val concreteSparqlQueryPlugin = new ConcreteSparqlQuery
        val projectionPlugin = new Projection
        val selectionPlugin = new Selection
        val typedPlugin = new Typed
        val join = new Join
        val unionPlugin = new Union

        val analysisDao = new AnalysisDAO

        val plugins = List(
            sparqlEndpointPlugin,
            concreteSparqlQueryPlugin,
            projectionPlugin,
            selectionPlugin,
            typedPlugin,
            join,
            unionPlugin
        )

        println("       persisting analysis")
        // persist analysis
        val analysis = new Analysis("Cities with more than 2 million habitants with countries", None)
        analysisDao.persist(analysis)
        assert(analysisDao.getById(analysis.id).isDefined)

        println("       persisting plugins")
        // Persist  plugins
        for (p <- plugins) {
            plugDao.persist(p)

            val p2 = plugDao.getByName(p.name)
            assert(p2.isDefined)
            assert(p2.get.id == p.id)
            assert(p2.get.parameters.size == p.parameters.size)

            // assert all parameters have proper IDs
            for(param <- p2.get.parameters){
                assert(p.parameters.find(_.id == param.id).get.name == param.name)
                //assert(p.parameters.find(_.id == param.id).get.defaultValue == param.defaultValue)
            }
        }

        println("       declaring analysis")
        val citiesFetcher = sparqlEndpointPlugin.createInstance().setParameter("EndpointURL", "http://dbpedia.org/sparql")
        val citiesTyped = typedPlugin.createInstance().setParameter("TypeURI", "http://dbpedia.org/ontology/City")
        val citiesProjection = projectionPlugin.createInstance().setParameter("PropertyURIs", List(
            "http://dbpedia.org/ontology/populationDensity", "http://dbpedia.org/ontology/populationTotal"
        ).mkString("\n"))
        val citiesSelection = selectionPlugin.createInstance().setParameter(
            "PropertyURI", "http://dbpedia.org/ontology/populationTotal"
        ).setParameter(
            "Operator", ">"
        ).setParameter(
            "Value", "2000000"
        )
        analysis.addPluginInstances(citiesFetcher, citiesTyped, citiesProjection, citiesSelection)
        analysis.addBinding(citiesFetcher, citiesTyped)
        analysis.addBinding(citiesTyped, citiesProjection)
        analysis.addBinding(citiesProjection, citiesSelection)

        val countriesFetcher = sparqlEndpointPlugin.createInstance().setParameter("EndpointURL", "http://dbpedia.org/sparql")
        val countriesTyped = typedPlugin.createInstance().setParameter("TypeURI", "http://dbpedia.org/ontology/Country")
        val countriesProjection = projectionPlugin.createInstance().setParameter("PropertyURIs", List(
            "http://dbpedia.org/ontology/areaTotal"
        ).mkString("\n"))
        analysis.addPluginInstances(countriesFetcher, countriesTyped, countriesProjection)
        analysis.addBinding(countriesFetcher, countriesTyped)
        analysis.addBinding(countriesTyped, countriesProjection)

        val citiesCountriesJoin = join.createInstance().setParameter(
            "JoinPropertyURI", "http://dbpedia.org/ontology/country"
        ).setParameter(
            "IsInner", false
        )
        analysis.addPluginInstances(citiesCountriesJoin)
        analysis.addBinding(citiesSelection, citiesCountriesJoin, 0)
        analysis.addBinding(countriesProjection, citiesCountriesJoin, 1)

        /*
        val evaluation = analysis.evaluate()
        while (!evaluation.isFinished) {
            println("Not finished, current progress: " + evaluation.progress.value)
            Thread.sleep(1000)
        }
        val result = evaluation.result
        */

        println("       persisting analysis again")
        analysisDao.persist(analysis)

        println("       asserting persisted analysis")
        // Get analysis from DB
        val persistedAnalysis = analysisDao.getById(analysis.id)
        assert(persistedAnalysis.isDefined)
        assert(persistedAnalysis.get.pluginInstances.size > 0)
        assert(persistedAnalysis.get.pluginInstanceBindings.size > 0)
        assert(persistedAnalysis.get.pluginInstanceInputBindings.size > 0)
        assert(persistedAnalysis.get.pluginInstanceOutputBindings.size > 0)
                  
        // Persist  plugins
        for (p <- plugins) {
            val p2 = plugDao.getByName(p.name)
            assert(p2.isDefined)
            assert(p2.get.id == p.id)
            assert(p2.get.parameters.size == p.parameters.size)

            // assert all parameters have proper IDs
            for(param <- p2.get.parameters){
                assert(p.parameters.find(_.id == param.id).get.name == param.name)
                assert(p.parameters.find(_.id == param.id).get.defaultValue == param.defaultValue)
                println("Parameter " + param.name + " of plugin " + p.name + " has " + param.asInstanceOf[Parameter[_]].parameterValues.size + " values")
            }
        }
        
        val pluginInstances = List(
            citiesFetcher,
            citiesTyped,
            citiesProjection,
            citiesSelection,
            countriesFetcher,
            countriesTyped,
            countriesProjection,
            citiesCountriesJoin
        )
        
        for(pi <- pluginInstances) {
            val pi2 = plugInstDao.getById(pi.id)
            assert(pi2.isDefined)
            assert(pi2.get.id == pi.id)
            assert(pi2.get.parameterValues.size == pi.parameterValues.size)

            // assert all parameters have proper IDs
            for(paramValue <- pi2.get.parameterValues){
                assert(pi.parameterValues.find(_.id == paramValue.id).get.parameter.id == paramValue.parameter.id)
                assert(pi.parameterValues.find(_.id == paramValue.id).get.value == paramValue.value)

                println("ID: " + pi.parameterValues.find(_.id == paramValue.id).get.parameter.id + " vs " + paramValue.parameter.id)
                println("Value: " + pi.parameterValues.find(_.id == paramValue.id).get.value + " vs " + paramValue.value)
            }
        }
        /*
        // .. and test it
        println("       evaluating persisted analysis")
        val eval = persistedAnalysis.get.evaluate()
        while (!eval.isFinished) {
            println("Not finished, current progress: " + eval.progress.value)
            Thread.sleep(1000)
        }
        val res = eval.result

        println("Done with result: " + res.toString)
        assert(res.map(_.isInstanceOf[Success]).getOrElse(false))
        */
    }
}
