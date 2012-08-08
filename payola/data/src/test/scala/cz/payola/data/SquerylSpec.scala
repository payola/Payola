package cz.payola.data

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import cz.payola.domain.entities.plugins.concrete.data.SparqlEndpointFetcher
import cz.payola.domain.entities.plugins.concrete.query._
import cz.payola.domain.entities.plugins.concrete._
import cz.payola.domain.entities.privileges._
import cz.payola.domain.entities.plugins.DataSource
import collection.immutable
import cz.payola.domain.entities.settings.OntologyCustomization
import cz.payola.domain.entities.plugins.parameters.StringParameter
import cz.payola.domain.IDGenerator

class SquerylSpec extends TestDataContextComponent("squeryl", false) with FlatSpec with ShouldMatchers
{
    // Users
    val u1 = new cz.payola.domain.entities.User("HS")
    val u2 = new cz.payola.domain.entities.User("ChM")
    val u3 = new cz.payola.domain.entities.User("JH")
    val u4 = new cz.payola.domain.entities.User("OK")
    val u5 = new cz.payola.domain.entities.User("OH")
    val users = List(u1, u2, u3, u4, u5)

    // Groups
    val g1 = new cz.payola.domain.entities.Group("group1", u1)
    val g2 = new cz.payola.domain.entities.Group("group2", u2)
    val g3 = new cz.payola.domain.entities.Group("group3", u3)
    val g4 = new cz.payola.domain.entities.Group("group4", u5) // u5 on purpose
    val g5 = new cz.payola.domain.entities.Group("group5", u5)
    val groups = List(g1, g2, g3, g4, g5)

    // Plugins
    private val params = List(new StringParameter("EndpointURL", "http://ld.opendata.cz:1111", true))
    val sparqlEndpointPlugin = new SparqlEndpointFetcher("SPARQL Endpoint", 0, params, IDGenerator.newId)
    val concreteSparqlQueryPlugin = new ConcreteSparqlQuery
    val projectionPlugin = new Projection
    val selectionPlugin = new Selection
    val typedPlugin = new Typed
    val join = new Join
    val unionPlugin = new Union

    val plugins = List(
        sparqlEndpointPlugin,
        concreteSparqlQueryPlugin,
        projectionPlugin,
        selectionPlugin,
        typedPlugin,
        join,
        unionPlugin
    )

    private val url = "http://opendata.cz/pco/public-contracts.xml"
    val customization = OntologyCustomization.empty(url, "Name1", None)
    val ownedCustomization = OntologyCustomization.empty(url, "Name2", Some(u1))
    val customizations = List(customization, ownedCustomization)

    "Schema" should "be created" in {
        schema.wrapInTransaction { schema.recreate }
    }

    "Users" should "be persited, loaded and managed by UserRepository" in {
        schema.wrapInTransaction { persistUsers }
    }

    private def persistUsers {
        println("Persisting users")
        users.foreach(userRepository.persist(_))

        // Update test
        u1.email = "email"
        u1.password = "password"
        userRepository.persist(u1)

        users.foreach{ user =>
            val persistedUser = userRepository.getById(user.id).get
            assert(persistedUser.email == user.email)
            assert(persistedUser.password == user.password)
            assert(persistedUser.name == user.name)
        }

        val user2_1 = userRepository.getById(u2.id).get
        user2_1.email = "email2"
        user2_1.password = "password2"
        userRepository.persist(user2_1)

        val user2_2 = userRepository.getById(u2.id).get
        assert(user2_2.email == user2_1.email)
        assert(user2_2.password == user2_1.password)

        assert(userRepository.getAllWithNameLike("h")(0).id == u2.id)
        assert(userRepository.getAllWithNameLike("J")(0).id == u3.id)
        assert(userRepository.getAllWithNameLike("K")(0).id == u4.id)
        assert(userRepository.getAllWithNameLike("H").size == 3)
        assert(userRepository.getAllWithNameLike(u1.name)(0).id == u1.id)
        assert(userRepository.getAllWithNameLike("invalid name").size == 0)
        assert(userRepository.getByCredentials(u1.name, u1.password).get.id == u1.id)
        assert(userRepository.getByCredentials("invalid", "credientals") == None)
    }

    "Groups" should "be persisted, loaded and managed by GroupRepository" in {
        schema.wrapInTransaction { persistGroups }
    }

    private def persistGroups {
        println("Persisting groups")

        groups.foreach(groupRepository.persist(_))

        g1.name = "new name"
        groupRepository.persist(g1)

        groups.foreach{ group =>
            val g = groupRepository.getById(group.id).get
                assert(g.id == group.id)
                assert(g.name == group.name)
                assert(g.owner.id == group.owner.id)
        }

        val persistedGroup2_1 = groupRepository.getById(g2.id).get
        persistedGroup2_1.name = "new name 2"
        groupRepository.persist(persistedGroup2_1)
        val persistedGroup2_2 = groupRepository.getById(g2.id).get
        assert(persistedGroup2_1.name == persistedGroup2_2.name)

        assert(userRepository.getById(u1.id).get.ownedGroups.size == 1)
        assert(userRepository.getById(u4.id).get.ownedGroups.size == 0)
        assert(userRepository.getById(u5.id).get.ownedGroups.size == 2)
    }

    "Groups" should "maintain members collection" in {
        schema.wrapInTransaction { persistGroupMemberships }
    }

    private def persistGroupMemberships {
        println("Persisting members")

        val group1 = groupRepository.getById(g1.id).get
        val group2 = groupRepository.getById(g2.id).get
        val group3 = groupRepository.getById(g3.id).get
        val group4 = groupRepository.getById(g4.id).get
        val group5 = groupRepository.getById(g5.id).get

        group2.addMember(userRepository.getById(u1.id).get)
        group1.addMember(userRepository.getById(u2.id).get)
        group1.addMember(userRepository.getById(u3.id).get)
        group2.addMember(userRepository.getById(u4.id).get)
        group2.addMember(userRepository.getById(u5.id).get)

            assert(group1.members.size == 2)
            assert(group2.members.size == 3)
            assert(group3.members.size == 0)
            assert(group4.members.size == 0)
            assert(group5.members.size == 0)
    }

    "Plugins" should "be persisted with their parameters by PluginRepository" in {
        schema.wrapInTransaction { persistPlugins }
    }

    private def persistPlugins {
        println("Persisting plugins")
        unionPlugin.owner = Some(u1)
        unionPlugin.isPublic = true
        
        plugins.foreach { p =>
            
            List(
                pluginRepository.persist(p),
                pluginRepository.getByName(p.name).get,
                pluginRepository.getById(p.id).get
            ).foreach{ plugin =>
                assert(plugin.id == p.id)
                assert(plugin.parameters.size == p.parameters.size)
                assert(plugin.name == p.name)
                assert(plugin.isPublic == p.isPublic)

                // assert all parameters have proper IDs
                for (param <- p.parameters) {
                    val persistedParam = plugin.parameters.find(_.id == param.id).get
                    assert(persistedParam.name == param.name)
                    assert(persistedParam.defaultValue == param.defaultValue)

                    // Check StringParameter.isMultiline property
                    type StringParam = cz.payola.common.entities.plugins.parameters.StringParameter
                    persistedParam match {
                        case s : StringParam => assert(s.isMultiline == param.asInstanceOf[StringParam].isMultiline)
                        case _ => // There is no isMultiline property otherwise
                    }
                }
            }
        }

        // getCount is not used on purpose to test instantiation:
        assert(pluginRepository.getAll().size == plugins.size)
        assert(pluginRepository.getById(unionPlugin.id).get.owner == Some(u1))
        assert(pluginRepository.getById(unionPlugin.id).get.isPublic == unionPlugin.isPublic)
        assert(pluginRepository.getById(unionPlugin.id).get.owner.get.ownedPlugins.size == 1)
    }

    "Analysis" should "be stored/updated/loaded by AnalysisRepository" in {
        schema.wrapInTransaction { persistAnalyses }
    }

    private def persistAnalyses {
        println("Persisting analyses")
        val user = userRepository.getById(u1.id).get
        val count = analysisRepository.getCount
        val a = new cz.payola.domain.entities.Analysis(
            "Cities with more than " + count + "M habitants with countries",
            Some(user)
        )

        // Test storing properties
        a.isPublic = true
        a.description = "description"

        println("      defining analysis")
        val citiesFetcher = sparqlEndpointPlugin.createInstance()
            .setParameter("EndpointURL", "http://dbpedia.org/sparql")
        val citiesTyped = typedPlugin.createInstance().setParameter("TypeURI", "http://dbpedia.org/ontology/City")
        val citiesProjection = projectionPlugin.createInstance().setParameter("PropertyURIs", List(
            "http://dbpedia.org/ontology/populationDensity", "http://dbpedia.org/ontology/populationTotal"
        ).mkString("\n"))
        val citiesSelection = selectionPlugin.createInstance().setParameter(
            "PropertyURI", "http://dbpedia.org/ontology/populationTotal"
        ).setParameter(
            "Operator", ">"
        ).setParameter(
            "Value", count + "000000"
        )

        citiesFetcher.description = "fetch"
        citiesFetcher.isEditable = true

        // Try that defined analysis can be persisted
        a.addPluginInstances(citiesFetcher, citiesTyped, citiesProjection, citiesSelection)
        a.addBinding(citiesFetcher, citiesTyped)
        a.addBinding(citiesTyped, citiesProjection)
        a.addBinding(citiesProjection, citiesSelection)

        // Persist partially defined analysis
        println("      persisting defined analysis")
        analysisRepository.persist(a)

            val analysis = analysisRepository.getById(a.id).get
            assert(analysis.owner.get.id == user.id)
            assert(user.ownedAnalyses.size == count + 1)
            assert(analysis.isPublic == a.isPublic)
            assert(analysis.description == a.description)

            // Asset all is persisted
            assert(analysis.pluginInstances.size == a.pluginInstances.size)
            assert(analysis.pluginInstances.size > 0)
            assert(analysis.pluginInstanceBindings.size == a.pluginInstanceBindings.size)
            assert(analysis.pluginInstanceBindings.size > 0)

        val countriesFetcher = sparqlEndpointPlugin.createInstance()
            .setParameter("EndpointURL", "http://dbpedia.org/sparql")
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

        println("      asserting persisted analysis")

        // Get analysis from DB
        val persistedAnalysis = analysisRepository.getById(analysis.id).get
            assert(persistedAnalysis.pluginInstances.size == analysis.pluginInstances.size)
            assert(persistedAnalysis.pluginInstances.size == 8)
            assert(persistedAnalysis.pluginInstanceBindings.size == analysis.pluginInstanceBindings.size)
            assert(persistedAnalysis.pluginInstanceBindings.size == 7)
            assert(persistedAnalysis.owner.get.id == user.id)

        println("      asserting persisted parameter values")

        // Assert persisted plugins instances
        List(
            citiesFetcher,
            citiesTyped,
            citiesProjection,
            citiesSelection,
            countriesFetcher,
            countriesTyped,
            countriesProjection,
            citiesCountriesJoin
        ).foreach { pi =>
            val pi2 = persistedAnalysis.pluginInstances.find(_.id == pi.id).get
                assert(pi2.id == pi.id)
                assert(pi2.plugin.id == pi.plugin.id)
                assert(pi2.parameterValues.size == pi.parameterValues.size)

            // assert all parameters have proper IDs
            pi.parameterValues.foreach { paramValue =>
                val parameter = paramValue.parameter
                val loadedParameter = pi2.parameterValues.find(_.id == paramValue.id).get.parameter

                assert(parameter.id == loadedParameter.id)
                assert(parameter.name == loadedParameter.name)
                assert(parameter.defaultValue == loadedParameter.defaultValue)
                assert(pi2.parameterValues.find(_.id == paramValue.id).get.value == paramValue.value)

                // Check StringParameter.isMultiline property
                type StringParam = cz.payola.common.entities.plugins.parameters.StringParameter
                loadedParameter match {
                    case s : StringParam => assert(s.isMultiline == parameter.asInstanceOf[StringParam].isMultiline)
                    case _ => // There is no isMultiline property otherwise
                }
            }
        }
    }

    "DataSources" should "be updated/stored by DataSourceRepository" in {
        schema.wrapInTransaction { persistDataSources }
    }

    private def persistDataSources {
        println("Persisting datasources")

        val ds1 = new DataSource("Cities", None, sparqlEndpointPlugin, immutable.Seq(
            sparqlEndpointPlugin.parameters(0).asInstanceOf[cz.payola.domain.entities.plugins.parameters.StringParameter]
                .createValue("http://dbpedia.org/ontology/Country")))
        val ds2 = new DataSource("Countries", Some(u2), sparqlEndpointPlugin, immutable.Seq(
            sparqlEndpointPlugin.parameters(0).asInstanceOf[cz.payola.domain.entities.plugins.parameters.StringParameter]
                .createValue("http://dbpedia.org/ontology/City")))

        ds1.description = "desc"
        ds1.isEditable = true

        List(ds1, ds2).foreach{ ds =>
            ds.isPublic = true
            dataSourceRepository.persist(ds)

            val ds_db = dataSourceRepository.getById(ds.id).get

                assert(ds.parameterValues.size == ds_db.parameterValues.size)
                assert(ds_db.isPublic == ds.isPublic)
                assert(ds_db.description == ds.description)
                assert(ds_db.isEditable == ds.isEditable)

            val parameter = sparqlEndpointPlugin.parameters(0)
            val loadedParameter = ds_db.parameterValues(0).parameter

                assert(parameter.id == loadedParameter.id)
                assert(parameter.name == loadedParameter.name)
                assert(parameter.defaultValue == loadedParameter.defaultValue)

            // assert all parameters have proper IDs
            ds_db.parameterValues.foreach { paramValue =>
                assert(ds.parameterValues.find(_.id == paramValue.id).get.parameter.id == paramValue.parameter.id)
                assert(ds.parameterValues.find(_.id == paramValue.id).get.value == paramValue.value)
            }
        }

        assert(dataSourceRepository.getById(ds2.id).get.owner.get.ownedDataSources.size == 1)
        assert(dataSourceRepository.getAllPublic.size == 2)
        assert(dataSourceRepository.getCount == 2)
    }

    "Privileges" should "be granted and persisted properly" in {
        schema.wrapInTransaction { persistPrivileges }
    }

    private def persistPrivileges {
        println("Persisting privileges")

        val a1 = analysisRepository.getAll()(0)
        val ds1 = dataSourceRepository.getAll()(0)
        val ds2 = dataSourceRepository.getAll()(1)
        val user1 = userRepository.getById(u1.id).get
        val user2 = userRepository.getById(u2.id).get
        val group1 = groupRepository.getById(g1.id).get
        val p = pluginRepository.getById(sparqlEndpointPlugin.id).get

        val p1 = new AccessAnalysisPrivilege(user1, user2, a1)
        val p2 = new AccessDataSourcePrivilege(user2, user1, ds2)
        val p3 = new AccessDataSourcePrivilege(user1, group1, ds1)
        val p4 = new AccessAnalysisPrivilege(user2, group1, a1)
        val p5 = new UsePluginPrivilege(user2, group1, p)
        val p6 = new UsePluginPrivilege(user2, user1, p)

        user2.grantPrivilege(p1)
        user1.grantPrivilege(p2)
        group1.grantPrivilege(p3)
        group1.grantPrivilege(p4)
        group1.grantPrivilege(p5)
        user1.grantPrivilege(p6)

        val p1_db = privilegeRepository.getById(p1.id).get

        assert(p1_db.id == p1.id)
        assert(p1_db.granter == p1.granter)
        assert(p1_db.grantee == p1.grantee)
        assert(p1_db.obj == p1.obj)
        
        assert(privilegeRepository.getById(p2.id).get.granter == p2.granter)
        assert(privilegeRepository.getById(p3.id).get.grantee == p3.grantee)
        assert(privilegeRepository.getById(p5.id).get.obj == p5.obj)
        assert(privilegeRepository.getByIds(List(p4.id, p6.id)).size == 2)

        assert(privilegeRepository.getCount == 6)
        assert(user1.grantedDataSources.size == 1)
        assert(user2.grantedAnalyses.size == 1)
        assert(user1.grantedPlugins.size == 1)
        assert(group1.grantedDataSources.size == 1)
        assert(group1.grantedAnalyses.size == 1)
        assert(group1.grantedPlugins.size == 1)

        assert(privilegeRepository.getAllByGranteeIds(List(group1.id), classOf[UsePluginPrivilege]).size == 1)
        assert(privilegeRepository.getAllByGranteeId(user1.id).size == 2)
    }

    "Customizations" should "be persisted" in {
        schema.wrapInTransaction { persistCustomizations }
    }

    private def persistCustomizations {
        println("Persisting customizations")

        val cc1 = ownedCustomization.classCustomizations(0)
        cc1.radius = 1
        cc1.fillColor = "rgb(100, 100, 100)"
        cc1.glyph = "g"

        val pp1 = cc1.propertyCustomizations(0)
        pp1.strokeColor = "rgb(0, 0, 200)"
        pp1.strokeWidth = 2

        customization.isPublic = true

        ontologyCustomizationRepository.persist(customization)
        ontologyCustomizationRepository.persist(ownedCustomization)

        val c1 = ontologyCustomizationRepository.getById(customization.id).get
        val c2 = ontologyCustomizationRepository.getById(ownedCustomization.id).get

        assert(c1.id == customization.id)
        assert(c1.isPublic == customization.isPublic)
        assert(c2.id == ownedCustomization.id)
        assert(c2.owner.get.id == u1.id)
        assert(c2.owner.get.ownedOntologyCustomizations.size == 1)

        // Assert eager-loading
        for(ontologyCustomization <- customizations) {
            val persistedOc = ontologyCustomizationRepository.getById(ontologyCustomization.id).get
                assert(persistedOc.owner == ontologyCustomization.owner)
                assert(persistedOc.name == ontologyCustomization.name)
                assert(persistedOc.ontologyURL == ontologyCustomization.ontologyURL)
                assert(persistedOc.classCustomizations.size == ontologyCustomization.classCustomizations.size)

            for (classCustomization <- ontologyCustomization.classCustomizations){
                val persistedCc = persistedOc.classCustomizations.find(_.id == classCustomization.id).get
                    assert(persistedCc.uri == classCustomization.uri)
                    assert(persistedCc.fillColor == classCustomization.fillColor)
                    assert(persistedCc.radius == classCustomization.radius)
                    assert(persistedCc.glyph == classCustomization.glyph)
                    assert(persistedCc.propertyCustomizations.size == classCustomization.propertyCustomizations.size)

                for (propertyCustomization <- classCustomization.propertyCustomizations){
                    val persistedPc = persistedCc.propertyCustomizations.find(_.id == propertyCustomization.id).get
                        assert(persistedPc.uri == propertyCustomization.uri)
                        assert(persistedPc.strokeWidth == propertyCustomization.strokeWidth)
                        assert(persistedPc.strokeColor == propertyCustomization.strokeColor)
                }
            }
        }

        // Test assigned customization
        val a1 = analysisRepository.getAll()(0)

        a1.defaultOntologyCustomization = Some(ownedCustomization)
        assert(analysisRepository.getById(a1.id).get.defaultOntologyCustomization == Some(ownedCustomization))

        a1.defaultOntologyCustomization = None
        assert(analysisRepository.getById(a1.id).get.defaultOntologyCustomization == None)

        a1.defaultOntologyCustomization = Some(customization)
        assert(analysisRepository.getById(a1.id).get.defaultOntologyCustomization == Some(customization))
    }

    "Pagionation" should "work" in {
        schema.wrapInTransaction { testPagination }
    }

    private def testPagination {
        println("Paginating")

        assert(userRepository.getAll(Some(new PaginationInfo(2,1))).size == 1)
        assert(userRepository.getAll(Some(new PaginationInfo(2,4))).size == 3)
        assert(userRepository.getAll(Some(new PaginationInfo(5,1))).size == 0)
        assert(userRepository.getAll(Some(new PaginationInfo(4,0))).size == 0)

        assert(groupRepository.getAll().size == 5)
        assert(groupRepository.getAll(Some(new PaginationInfo(1, 2))).size == 2)
        assert(groupRepository.getAll(Some(new PaginationInfo(2, 5))).size == 3)
        assert(groupRepository.getAll(Some(new PaginationInfo(5, 1))).size == 0)
        assert(groupRepository.getAll(Some(new PaginationInfo(4, 0))).size == 0)
    }

    "Entities" should "be removed with their related entities" in {
        schema.wrapInTransaction { testCascadeDeletes }
    }

    private def testCascadeDeletes {
        println("Removing")

        val analysisCount = analysisRepository.getAll().size
        val pluginsCount = pluginRepository.getAll().size

        // Create another analysis in DB
        persistAnalyses

        assert(analysisRepository.getCount == analysisCount + 1)
        assert(pluginRepository.getCount == pluginsCount)

        // Test "undefault" customization from analysis
        //assert(ontologyCustomizationRepository.removeById(customization.id))

        // Remove one analysis
        assert(analysisRepository.removeById(analysisRepository.getAll()(0).id) == true)

        // One analysis and half of plugin instances are gone
        assert(analysisRepository.getCount == analysisCount)
        assert(pluginRepository.getCount == pluginsCount)

        val analysis = analysisRepository.getAll()(0)

        // Remove user and all his entities
        users.foreach(u => assert(userRepository.removeById(u.id)))

        // 1 owned plugin removed
        assert(pluginRepository.getCount == pluginsCount - 1)

        // Remove all plugins (except removed UnionPlugin)
        plugins.foreach(p => assert(pluginRepository.removeById(p.id) == (p.id != unionPlugin.id)))

        // Only (empty) analysis is left
        assert(analysisRepository.getCount == 0)
        assert(pluginRepository.getCount == 0)
    }
}
