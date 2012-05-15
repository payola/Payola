package cz.payola.data.entities

import cz.payola.data.entities.dao._
import cz.payola.data.entities.analyses.parameters._
import cz.payola.data.entities.analyses._
import cz.payola.domain.entities.analyses.plugins.data.SparqlEndpoint
import cz.payola.domain.entities.analyses.plugins.query._
import cz.payola.domain.entities.analyses.plugins.Union

object TestObject
{
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

    val bPar = new BooleanParameter("bPar", true)

    val bParInst = new BooleanParameterValue(bPar, false)

    val fPar = new FloatParameter("fPar", -1.0f)

    val fParInst = new FloatParameterValue(fPar, 1.0f)

    val iPar = new IntParameter("iPar", -1)

    val iParInst = new IntParameterValue(iPar, 1)

    val sPar = new StringParameter("sPar", "empty")

    val sParInst = new StringParameterValue(sPar, "string")

    val plug = new Plugin("plugin", 1, List(bPar, fPar, iPar, sPar))

    val plugInst = new PluginInstance(plug, List(bParInst, fParInst, iParInst, sParInst))

    def main(args: Array[String]) {        
        println("Connecting ...")
        assert (PayolaDB.connect())
        
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

        // Update test
        group1.name += "1"
        groupDao.persist(group1)

        val g = groupDao.getById(group1.id)
        assert(g != None)
        assert(g.get.id == group1.id)
        assert(g.get.name == group1.name)
        assert(g.get.ownerId.get == user.id)
        
        user.addToGroup(group1)
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

        println("Persisting plugin ...")
        plugDao.persist(plug)

        // Update test
        plug.name += "1"
        plugDao.persist(plug)

        val pl = plugDao.getById(plug.id)
        assert(pl != None)
        assert(pl.get.name == plug.name)
        assert(pl.get.id == plug.id)
        assert(plugDao.getById("") == None)

        println("Persisting plugin instance ...")
        plugInst.analysisId = Some(analysis.id)
        plugInstDao.persist(plugInst)

        val pi = plugInstDao.getById(plugInst.id)
        assert(pi != None)
        assert(pi.get.id == plugInst.id)
        assert(plugInstDao.getById("") == None)
                
        println("Persisting parameters and values ...")   
        println("   boolean:")
        bParDao.persist(bPar)

        val pB = bParDao.getById(bPar.id)
        assert(pB != None)
        assert(pB.get.id == bPar.id)
        assert(bParDao.getById("") == None)
        
        bParInstDao.persist(bParInst)

        val pvB = bParInstDao.getById(bParInst.id)
        assert(pvB != None)
        assert(pvB.get.id == bParInst.id)
        assert(bParInstDao.getById("") == None)

        assert(bPar.instances.size == 1)

        println("   float:")
        fParDao.persist(fPar)

        val pF = fParDao.getById(fPar.id)
        assert(pF != None)
        assert(pF.get.id == fPar.id)
        assert(fParDao.getById("") == None)
        
        fParInstDao.persist(fParInst)

        val pvF = fParInstDao.getById(fParInst.id)
        assert(pvF != None)
        assert(pvF.get.id == fParInst.id)
        assert(fParInstDao.getById("") == None)

        assert(fPar.instances.size == 1)

        println("   int:")
        iParDao.persist(iPar)

        val pI = iParDao.getById(iPar.id)
        assert(pI != None)
        assert(pI.get.id == iPar.id)
        assert(iParDao.getById("") == None)
        
        iParInstDao.persist(iParInst)

        val pvI = iParInstDao.getById(iParInst.id)
        assert(pvI != None)
        assert(pvI.get.id == iParInst.id)
        assert(iParInstDao.getById("") == None)

        assert(iPar.instances.size == 1)

        println("   string:")
        sParDao.persist(sPar)

        val pS = sParDao.getById(sPar.id)
        assert(pS != None)
        assert(pS.get.id == sPar.id)
        assert(sParDao.getById("") == None)
        
        sParInstDao.persist(sParInst)

        val pvS = sParInstDao.getById(sParInst.id)
        assert(pvS != None)
        assert(pvS.get.id == sParInst.id)
        assert(sParInstDao.getById("") == None)

        assert(sPar.instances.size == 1)
        
        println("Analysis work ... ")
        println("   Parameters check")
        assert(plug.parameters.size == 4)
        assert(plugInst.parameterValues.size == 4)
        assert (analysis.pluginInstances.size == 1)

        assert(plug.parameters.find(par => par.id == bPar.id) != None)
        assert(plug.parameters.find(par => par.id == fPar.id) != None)
        assert(plug.parameters.find(par => par.id == iPar.id) != None)
        assert(plug.parameters.find(par => par.id == sPar.id) != None)

        assert(plugInst.parameterValues.find(par => par.id == bParInst.id) != None)
        assert(plugInst.parameterValues.find(par => par.id == fParInst.id) != None)
        assert(plugInst.parameterValues.find(par => par.id == iParInst.id) != None)
        assert(plugInst.parameterValues.find(par => par.id == sParInst.id) != None)
                
        println("   Derived plugins test")
        testDerivedPlugins()

        println("   Cascade removing")
        // With parameter its instance should be deleted        
        bParDao.removeById(bPar.id)
        assert(bParDao.getById(bPar.id) == None)
        assert(bParInstDao.getById(bParInst.id) == None)
        assert(plug.parameters.size == 3)
        assert(plugInst.parameterValues.size == 3)

        // Parameter instance removing should not remove parameter 
        fParInstDao.removeById(fParInst.id)
        assert(fParDao.getById(fPar.id) != None)
        assert(fParInstDao.getById(fParInst.id) == None)
        assert(plug.parameters.size == 3)
        assert(plugInst.parameterValues.size == 2)

        // Remove plugin -> remove parameters, plugin instances, parameter instances
        plugDao.removeById(plug.id)
        assert(analysis.pluginInstances.size == 0)
        assert(plugInstDao.getById(plugInst.id) == None)
        assert(sParDao.getById(sPar.id) == None)
        assert(sParInstDao.getById(sParInst.id) == None)

        // Prepare for analysis removal test
        plugDao.persist(plug)
        sParDao.persist(sPar)
        plugInstDao.persist(plugInst)
        sParInstDao.persist(sParInst)
        assert (analysis.pluginInstances.size == 1)
        assert (plugInst.parameterValues.find(par => par.id == sParInst.id) != None)

        // Remove analysis -> remove plugin instances
        analysisDao.removeById(analysis.id)
        assert (plugInstDao.getById(plugInst.id) == None)

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
        //TODO: missing LeftJoin class in: val leftJoinPlugin = new LeftJoin
        val unionPlugin = new Union

        val plugins = List(
            sparqlEndpointPlugin,
            concreteSparqlQueryPlugin,
            projectionPlugin,
            selectionPlugin,
            typedPlugin,
            //leftJoinPlugin,
            unionPlugin
        )

        for (p <- plugins) {
            plugDao.persist(new Plugin(p.name, p.inputCount, p.parameters))
            assert(plugDao.getByName(p.name) != None)
        }
        
        val plugInst2 = plug.createInstance().asInstanceOf[PluginInstance]
        analysis.addPluginInstances(plugInst, plugInst2)
        analysis.addBinding(new PluginInstanceBinding(plugInst, plugInst2))

        assert(analysis.pluginInstanceBindings.size == 1)
    }
}
