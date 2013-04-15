package cz.payola.data.squeryl

import org.squeryl._
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.adapters.H2Adapter
import org.squeryl.dsl._
import cz.payola.data._
import cz.payola.data.squeryl.entities._
import cz.payola.data.squeryl.entities.settings._
import cz.payola.data.squeryl.entities.plugins._
import cz.payola.data.squeryl.entities.plugins.parameters._
import cz.payola.data.squeryl.entities.analyses.PluginInstanceBinding
import cz.payola.data.squeryl.entities.privileges.PrivilegeDbRepresentation
import cz.payola.data.squeryl.entities.Group

/**
 * Component that provides DB schema
 */
trait SchemaComponent
{
    self: SquerylDataContextComponent =>
    /**
     * Database schema
     */
    val schema: Schema

    /**
     * Definition of the Payola schema in Squeryl.
     * @see http://www.squeryl.org
     * @param databaseURL A database url of the 'jdbc:subprotocol:subname' format.
     * @param userName The database user on whose behalf the connection is being made.
     * @param password The users password.
     */
    class Schema(val databaseURL: String, val userName: String, val password: String) extends org.squeryl.Schema
    {
        // Initialize the session factory.
        java.lang.Class.forName("org.h2.Driver")
        SessionFactory.concreteFactory = Some(() =>
            DataException.wrap {
                Session.create(java.sql.DriverManager.getConnection(databaseURL, userName, password), new H2Adapter)
            })

        /**Table of [[cz.payola.data.squeryl.entities.User]]s */
        val users = table[User]("users")

        /**Table of [[cz.payola.data.squeryl.entities.Group]]s */
        val groups = table[Group]("groups")

        /**Table of [[cz.payola.data.squeryl.entities.Analysis]] items */
        val analyses = table[Analysis]("analyses")

        /**Table of [[cz.payola.data.squeryl.entities.analyses.PluginDbRepresentation]]s */
        val plugins = table[PluginDbRepresentation]("plugins")

        /**Table of [[cz.payola.data.squeryl.entities.analyses.PluginInstance]]s */
        val pluginInstances = table[PluginInstance]("pluginInstances")

        /**Table of  ([[cz.payola.data.squeryl.entities.User]]s)s */
        val booleanParameters = table[BooleanParameter]("booleanParameters")

        /**Table of [[cz.payola.data.squeryl.entities.plugins.parameters.BooleanParameterValues]]s */
        val booleanParameterValues = table[BooleanParameterValue]("booleanParameterValues")

        /**Table of [[cz.payola.data.squeryl.entities.plugins.parameters.FloatParameter]]s */
        val floatParameters = table[FloatParameter]("floatParameters")

        /**Table of [[cz.payola.data.squeryl.entities.plugins.parameters.FloatParameterValue]]s */
        val floatParameterValues = table[FloatParameterValue]("floatParameterValues")

        /**Table of [[cz.payola.data.squeryl.entities.plugins.parameters.IntParameter]]s */
        val intParameters = table[IntParameter]("intParameters")

        /**Table of [[cz.payola.data.squeryl.entities.plugins.parameters.IntParameterValue]]s */
        val intParameterValues = table[IntParameterValue]("intParameterValues")

        /**Table of [[cz.payola.data.squeryl.entities.plugins.parameters.StringParameter]]s */
        val stringParameters = table[StringParameter]("stringParameters")

        /**Table of [[cz.payola.data.squeryl.entities.plugins.parameters.StringParameterValue]]s */
        val stringParameterValues = table[StringParameterValue]("stringParameterValues")

        /**Table of [[cz.payola.data.squeryl.entities.analyses.PluginInstanceBinding]]s */
        val pluginInstanceBindings = table[PluginInstanceBinding]("pluginInstanceBindings")

        /**Table of [[cz.payola.data.squeryl.entities.analyses.DataSource]]s */
        val dataSources = table[DataSource]("dataSources")

        /**Table of [[cz.payola.data.squeryl.entities.privileges.PrivilegeDbRepresentation]]s */
        val privileges = table[PrivilegeDbRepresentation]("privileges")

        /**Table of [[cz.payola.data.squeryl.entities.settings.OntologyCustomization]]s */
        val ontologyCustomizations = table[OntologyCustomization]("ontologyCustomizations")

        /**Table of [[cz.payola.data.squeryl.entities.settings.ClassCustomization]]s */
        val classCustomizations = table[ClassCustomization]("classCustomizations")

        /**Table of [[cz.payola.data.squeryl.entities.settings.PropertyCustomization]]s */
        val propertyCustomizations = table[PropertyCustomization]("propertyCustomizations")

        /**Table of [[cz.payola.data.squeryl.entities.Prefix]]es */
        val prefixes = table[Prefix]("prefixes")

        /**
         * Relation that associates members ([[cz.payola.data.squeryl.entities.User]]s)
         * to [[cz.payola.data.squeryl.entities.Group]]s
         */
        lazy val groupMembership = manyToManyRelation(users, groups).via[GroupMembership](
            (u, g, gm) => (gm.memberId === u.id, g.id === gm.groupId))

        /**
         * Relation that associates [[cz.payola.data.squeryl.entities.Group]]s to their owners
         * ([[cz.payola.data.squeryl.entities.User]]s)
         */
        lazy val groupOwnership = oneToManyRelation(users, groups).via(
            (u, g) => u.id === g.ownerId)

        /**
         * Relation that associates [[cz.payola.data.squeryl.entities.Analysis]] to its owner
         * ([[cz.payola.data.squeryl.entities.User]]s)
         */
        lazy val analysisOwnership = oneToManyRelation(users, analyses).via(
            (u, a) => Option(u.id) === a.ownerId)

        /**
         * Relation that associates [[cz.payola.data.squeryl.entities.settings.OntologyCustomization]] to its owner
         * ([[cz.payola.data.squeryl.entities.User]]s)
         */
        lazy val customizationOwnership = oneToManyRelation(users, ontologyCustomizations).via(
            (u, c) => Option(u.id) === c.ownerId)

        /**
         * Relation that associates [[cz.payola.data.squeryl.entities.PluginDbRepresentation]] to its owner
         * ([[cz.payola.data.squeryl.entities.User]]s)
         */
        lazy val pluginOwnership = oneToManyRelation(users, plugins).via(
            (u, p) => Option(u.id) === p.ownerId)

        /**
         * Relation that associates [[cz.payola.data.squeryl.entities.analyses.DataSource]] to its owner
         * ([[cz.payola.data.squeryl.entities.User]]s)
         */
        lazy val dataSourceOwnership = oneToManyRelation(users, dataSources).via(
            (u, ds) => Option(u.id) === ds.ownerId)

        /**
         * Relation that associates [[cz.payola.data.squeryl.entities.Prefix]] to its owner
         * ([[cz.payola.data.squeryl.entities.User]]s)
         */
        lazy val prefixOwnership = oneToManyRelation(users, prefixes).via(
            (u, p) => Option(u.id) === p.ownerId)

        /**
         * Relation that associates [[cz.payola.data.squeryl.entities.analyses.PluginDbRepresentation]] to a
         * [[cz.payola.data.squeryl.entities.PluginDbRepresentation]]
         */
        lazy val pluginsPluginInstances = oneToManyRelation(plugins, pluginInstances).via(
            (p, pi) => p.id === pi.pluginId)

        /**
         * Relation that associates [[cz.payola.data.squeryl.entities.analyses.PluginDbRepresentation]] to an
         * [[cz.payola.data.squeryl.entities.Analysis]]
         */
        lazy val analysesPluginInstances = oneToManyRelation(analyses, pluginInstances).via(
            (a, pi) => a.id === pi.analysisId)

        /**
         * Relation that associates [[cz.payola.data.squeryl.entities.analyses.DataSource]]s to
         * a [[cz.payola.data.squeryl.entities.analyses.PluginDbRepresentation]]s
         */
        lazy val pluginsDataSources = oneToManyRelation(plugins, dataSources).via(
            (p, ds) => p.id === ds.pluginId)

        /**
         * Relation that associates [[cz.payola.data.squeryl.entities.analyses.PluginInstanceBinding]]s to a
         * [[cz.payola.data.squeryl.entities.Analysis]]
         */
        lazy val analysesPluginInstancesBindings = oneToManyRelation(analyses, pluginInstanceBindings).via(
            (a, pib) => a.id === pib.analysisId)

        /**
         * Relation that associates [[cz.payola.data.squeryl.entities.analyses.PluginInstanceBinding]]s to a
         * [[cz.payola.data.squeryl.entities.analyses.PluginInstance]] as a source
         */
        lazy val bindingsOfSourcePluginInstances = oneToManyRelation(pluginInstances, pluginInstanceBindings).via(
            (pi, b) => pi.id === b.sourcePluginInstanceId)

        /**
         * Relation that associates [[cz.payola.data.squeryl.entities.analyses.PluginInstanceBinding]]s to a
         * [[cz.payola.data.squeryl.entities.analyses.PluginInstance]] as a target
         */
        lazy val bindingsOfTargetPluginInstances = oneToManyRelation(pluginInstances, pluginInstanceBindings).via(
            (pi, b) => pi.id === b.targetPluginInstanceId)

        /**
         * Relation that associates [[cz.payola.data.squeryl.entities.plugins.parameters.BooleanParameterValues]]s to a
         * [[cz.payola.data.squeryl.entities.plugins.parameters.BooleanParameter]]
         */
        lazy val valuesOfBooleanParameters = oneToManyRelation(booleanParameters, booleanParameterValues).via(
            (bp, bpv) => bp.id === bpv.parameterId)

        /**
         * Relation that associates [[cz.payola.data.squeryl.entities.plugins.parameters.FloatParameterValue]]s to a
         * [[cz.payola.data.squeryl.entities.plugins.parameters.FloatParameter]]
         */
        lazy val valuesOfFloatParameters = oneToManyRelation(floatParameters, floatParameterValues).via(
            (fp, fpv) => fp.id === fpv.parameterId)

        /**
         * Relation that associates [[cz.payola.data.squeryl.entities.plugins.parameters.IntParameterValue]]s to an
         * [[cz.payola.data.squeryl.entities.plugins.parameters.IntParameter]]
         */
        lazy val valuesOfIntParameters = oneToManyRelation(intParameters, intParameterValues).via(
            (ip, ipv) => ip.id === ipv.parameterId)

        /**
         * Relation that associates [[cz.payola.data.squeryl.entities.plugins.parameters.StringParameterValues]] to
         * a [[cz.payola.data.squeryl.entities.plugins.parameters.StringParameter]]
         */
        lazy val valuesOfStringParameters = oneToManyRelation(stringParameters, stringParameterValues).via(
            (sp, spv) => sp.id === spv.parameterId)

        /**
         * Relation that associates [[cz.payola.data.squeryl.entities.plugins.parameters.BooleanParameter]] to a
         * [[cz.payola.data.squeryl.entities.analyses.PluginDbRepresentation]]
         */
        lazy val booleanParametersOfPlugins = oneToManyRelation(plugins, booleanParameters).via(
            (p, bp) => p.id === bp.pluginId)

        /**
         * Relation that associates [[cz.payola.data.squeryl.entities.plugins.parameters.FloatParameter]]s to a
         * [[cz.payola.data.squeryl.entities.analyses.PluginDbRepresentation]]
         */
        lazy val floatParametersOfPlugins = oneToManyRelation(plugins, floatParameters).via(
            (p, fp) => p.id === fp.pluginId)

        /**
         * Relation that associates [[cz.payola.data.squeryl.entities.plugins.parameters.IntParameter]]s to a
         * [[cz.payola.data.squeryl.entities.analyses.PluginDbRepresentation]]
         */
        lazy val intParametersOfPlugins = oneToManyRelation(plugins, intParameters).via(
            (p, ip) => p.id === ip.pluginId)

        /**
         * Relation that associates [[cz.payola.data.squeryl.entities.plugins.parameters.StringParameter]]s to a
         * [[cz.payola.data.squeryl.entities.analyses.PluginDbRepresentation]]
         */
        lazy val stringParametersOfPlugins = oneToManyRelation(plugins, stringParameters).via(
            (p, sp) => p.id === sp.pluginId)

        /**
         * Relation that associates [[cz.payola.data.squeryl.entities.plugins.parameters.BooleanParameterValues]]s
         * to a [[cz.payola.data.squeryl.entities.plugins.parameters.BooleanParameter]]
         */
        lazy val booleanParameterValuesOfPluginInstances = oneToManyRelation(pluginInstances, booleanParameterValues)
            .via(
            (pi, bpv) => Option(pi.id) === bpv.pluginInstanceId)

        /**
         * Relation that associates [[cz.payola.data.squeryl.entities.plugins.parameters.FloatParameterValue]]s to a
         * [[cz.payola.data.squeryl.entities.plugins.parameters.FloatParameter]]
         */
        lazy val floatParameterValuesOfPluginInstances = oneToManyRelation(pluginInstances, floatParameterValues).via(
            (pi, fpv) => Option(pi.id) === fpv.pluginInstanceId)

        /**
         * Relation that associates [[cz.payola.data.squeryl.entities.plugins.parameters.IntParameterValue]]s to an
         * [[cz.payola.data.squeryl.entities.plugins.parameters.IntParameter]]
         */
        lazy val intParameterValuesOfPluginInstances = oneToManyRelation(pluginInstances, intParameterValues).via(
            (pi, ipv) => Option(pi.id) === ipv.pluginInstanceId)

        /**
         * Relation that associates [[cz.payola.data.squeryl.entities.plugins.parameters.StringParameterValues]]s to
         * a [[cz.payola.data.squeryl.entities.plugins.parameters.StringParameter]]
         */
        lazy val stringParameterValuesOfPluginInstances = oneToManyRelation(pluginInstances, stringParameterValues).via(
            (pi, spv) => Option(pi.id) === spv.pluginInstanceId)

        /**
         * Relation that associates [[cz.payola.data.squeryl.entities.plugins.parameters.BooleanParameterValues]]s
         * to a [[cz.payola.data.squeryl.entities.analyses.DataSource]]
         */
        lazy val booleanParameterValuesOfDataSources = oneToManyRelation(dataSources, booleanParameterValues).via(
            (ds, bpv) => Option(ds.id) === bpv.dataSourceId)

        /**
         * Relation that associates [[cz.payola.data.squeryl.entities.plugins.parameters.FloatParameterValues]]s to
         * a [[cz.payola.data.squeryl.entities.analyses.DataSource]]
         */
        lazy val floatParameterValuesOfDataSources = oneToManyRelation(dataSources, floatParameterValues).via(
            (ds, fpv) => Option(ds.id) === fpv.dataSourceId)

        /**
         * Relation that associates [[cz.payola.data.squeryl.entities.plugins.parameters.IntParameterValues]] to
         * a [[cz.payola.data.squeryl.entities.analyses.DataSource]]
         */
        lazy val intParameterValuesOfDataSources = oneToManyRelation(dataSources, intParameterValues).via(
            (ds, ipv) => Option(ds.id) === ipv.dataSourceId)

        /**
         * Relation that associates [[cz.payola.data.squeryl.entities.plugins.parameters.StringParameterValues]]s
         * to a [[cz.payola.data.squeryl.entities.analyses.DataSource]]
         */
        lazy val stringParameterValuesOfDataSources = oneToManyRelation(dataSources, stringParameterValues).via(
            (ds, spv) => Option(ds.id) === spv.dataSourceId)

        /**
         * Relation that associates [[cz.payola.data.squeryl.entities.settings.OntologyCustomization]]s
         * to a [[cz.payola.data.squeryl.entities.Analysis]]
         */
        lazy val ontologyCustomizationsOfAnalyses = oneToManyRelation(ontologyCustomizations, analyses).via(
            (o, a) => a.defaultCustomizationId === Some(o.id))

        /**
         * Relation that associates [[cz.payola.data.squeryl.entities.settings.ClassCustomization]]s
         * to a [[cz.payola.data.squeryl.entities.settings.OntologyCustomization]]
         */
        lazy val classCustomizationsOfOntologies = oneToManyRelation(ontologyCustomizations, classCustomizations).via(
            (o, c) => o.id === c.ontologyCustomizationId)

        /**
         * Relation that associates [[cz.payola.data.squeryl.entities.settings.PropertyCustomization]]s
         * to a [[cz.payola.data.squeryl.entities.settings.ClassCustomization]]
         */
        lazy val propertyCustomizationsOfClasses = oneToManyRelation(classCustomizations, propertyCustomizations).via(
            (c, p) => c.id === p.classCustomizationId)

        /**
         * All the entities have to be created using custom factories in order to inject their dependencies via the
         * implicit constructor parameter.
         */
        override def callbacks = Seq(
            factoryFor(users) is {
                new User("", "", "", "")
            },
            factoryFor(groups) is {
                new Group("", "", null)
            },
            factoryFor(analyses) is {
                new Analysis("", "", None, false, "", None)
            },
            factoryFor(plugins) is {
                new PluginDbRepresentation("", "", "", 0, None, false)
            },
            factoryFor(pluginInstances) is {
                new PluginInstance("", null, Nil, "", false)
            },
            factoryFor(pluginInstanceBindings) is {
                new PluginInstanceBinding("", null, null, 0)
            },
            factoryFor(booleanParameters) is {
                new BooleanParameter("", "", false)
            },
            factoryFor(booleanParameterValues) is {
                new BooleanParameterValue("", null, false)
            },
            factoryFor(floatParameters) is {
                new FloatParameter("", "", 0)
            },
            factoryFor(floatParameterValues) is {
                new FloatParameterValue("", null, 0)
            },
            factoryFor(intParameters) is {
                new IntParameter("", "", 0)
            },
            factoryFor(intParameterValues) is {
                new IntParameterValue("", null, 0)
            },
            factoryFor(stringParameters) is {
                new StringParameter("", "", "", false, false)
            },
            factoryFor(stringParameterValues) is {
                new StringParameterValue("", null, "")
            },
            factoryFor(dataSources) is {
                new DataSource("", "", None, null, Nil, false, "", true)
            },
            factoryFor(privileges) is {
                new PrivilegeDbRepresentation("", "", "", "", "", "", "")
            },
            factoryFor(ontologyCustomizations) is {
                new OntologyCustomization("", "", "", None, Nil, false)
            },
            factoryFor(classCustomizations) is {
                new ClassCustomization("", "", "", 0, "", Nil)
            },
            factoryFor(propertyCustomizations) is {
                new PropertyCustomization("", "", "", 0)
            },
            factoryFor(prefixes) is {
                new Prefix("", "", "", "", None, false)
            }
        )

        /**
         * Declares primary, unique and foreign keys and constrains.
         */
        private def declareKeys() {
            val COLUMN_TYPE_ID = "varchar(36)"
            val COLUMN_TYPE_TOKEN = "varchar(36)"
            val COLUMN_TYPE_PREFIX = "varchar(10)"
            val COLUMN_TYPE_NAME = "varchar(128)"
            val COLUMN_TYPE_DESCRIPTION = "text"
            val COLUMN_TYPE_URI = "text"
            val COLUMN_TYPE_VALUE = "text"
            val COLUMN_TYPE_COLOR = "varchar(128)"
            val COLUMN_TYPE_CLASSNAME = "varchar(64)"
            val COLUMN_TYPE_FULL_CLASSNAME = "varchar(256)"

            on(users)(user =>
                declare(
                    user.id is(primaryKey, dbType(COLUMN_TYPE_ID)),
                    user.name is(unique, dbType(COLUMN_TYPE_ID)),
                    user.password is (dbType("varchar(32)")),
                    user.email is (dbType("varchar(128)"))
                ))

            on(plugins)(plugin =>
                declare(
                    plugin.id is(primaryKey, (dbType(COLUMN_TYPE_ID))),
                    plugin.name is(unique, dbType(COLUMN_TYPE_NAME)),
                    plugin.pluginClassName is (dbType(COLUMN_TYPE_FULL_CLASSNAME)),
                    plugin.ownerId is (dbType(COLUMN_TYPE_ID))
                ))

            on(pluginInstances)(instance =>
                declare(
                    instance.id is(primaryKey, (dbType(COLUMN_TYPE_ID))),
                    instance._desc is (dbType(COLUMN_TYPE_DESCRIPTION)),
                    instance.description is (dbType(COLUMN_TYPE_DESCRIPTION)),
                    instance.analysisId is (dbType(COLUMN_TYPE_ID)),
                    instance.pluginId is (dbType(COLUMN_TYPE_ID))
                ))

            on(pluginInstanceBindings)(binding =>
                declare(
                    binding.id is(primaryKey, (dbType(COLUMN_TYPE_ID))),
                    binding.targetPluginInstanceId is (dbType(COLUMN_TYPE_ID)),
                    binding.sourcePluginInstanceId is (dbType(COLUMN_TYPE_ID)),
                    binding.analysisId is (dbType(COLUMN_TYPE_ID)),
                    columns(binding.targetPluginInstanceId, binding.inputIndex) are (unique),
                    columns(binding.sourcePluginInstanceId, binding.analysisId) are (unique)
                ))

            on(booleanParameterValues)(param =>
                declare(
                    param.id is(primaryKey, (dbType(COLUMN_TYPE_ID))),
                    param.pluginInstanceId is (dbType(COLUMN_TYPE_ID)),
                    param.dataSourceId is (dbType(COLUMN_TYPE_ID)),
                    param.parameterId is (dbType(COLUMN_TYPE_ID)),
                    param.value is (dbType(COLUMN_TYPE_VALUE))
                ))

            on(floatParameterValues)(param =>
                declare(
                    param.id is(primaryKey, (dbType(COLUMN_TYPE_ID))),
                    param.pluginInstanceId is (dbType(COLUMN_TYPE_ID)),
                    param.dataSourceId is (dbType(COLUMN_TYPE_ID)),
                    param.parameterId is (dbType(COLUMN_TYPE_ID)),
                    param.value is (dbType(COLUMN_TYPE_VALUE))
                ))

            on(intParameterValues)(param =>
                declare(
                    param.id is(primaryKey, (dbType(COLUMN_TYPE_ID))),
                    param.pluginInstanceId is (dbType(COLUMN_TYPE_ID)),
                    param.dataSourceId is (dbType(COLUMN_TYPE_ID)),
                    param.parameterId is (dbType(COLUMN_TYPE_ID)),
                    param.value is (dbType(COLUMN_TYPE_VALUE))
                ))

            on(stringParameterValues)(param =>
                declare(
                    param.id is(primaryKey, (dbType(COLUMN_TYPE_ID))),
                    param.pluginInstanceId is (dbType(COLUMN_TYPE_ID)),
                    param.dataSourceId is (dbType(COLUMN_TYPE_ID)),
                    param.parameterId is (dbType(COLUMN_TYPE_ID)),
                    param.value is (dbType(COLUMN_TYPE_VALUE))
                ))

            on(booleanParameters)(param =>
                declare(
                    param.id is(primaryKey, (dbType(COLUMN_TYPE_ID))),
                    param.pluginId is (dbType(COLUMN_TYPE_ID)),
                    param._defaultValueDb is (dbType(COLUMN_TYPE_VALUE)),
                    param.name is (dbType(COLUMN_TYPE_NAME)),
                    columns(param.pluginId, param.name) are (unique)
                ))

            on(floatParameters)(param =>
                declare(
                    param.id is(primaryKey, (dbType(COLUMN_TYPE_ID))),
                    param.pluginId is (dbType(COLUMN_TYPE_ID)),
                    param._defaultValueDb is (dbType(COLUMN_TYPE_VALUE)),
                    param.name is (dbType(COLUMN_TYPE_NAME)),
                    columns(param.pluginId, param.name) are (unique)
                ))

            on(intParameters)(param =>
                declare(
                    param.id is(primaryKey, (dbType(COLUMN_TYPE_ID))),
                    param.pluginId is (dbType(COLUMN_TYPE_ID)),
                    param._defaultValueDb is (dbType(COLUMN_TYPE_VALUE)),
                    param.name is (dbType(COLUMN_TYPE_NAME)),
                    columns(param.pluginId, param.name) are (unique)
                ))

            on(stringParameters)(param =>
                declare(
                    param.id is(primaryKey, (dbType(COLUMN_TYPE_ID))),
                    param.pluginId is (dbType(COLUMN_TYPE_ID)),
                    param._defaultValueDb is (dbType(COLUMN_TYPE_VALUE)),
                    param.name is (dbType(COLUMN_TYPE_NAME)),
                    columns(param.pluginId, param.name) are (unique)
                ))

            on(dataSources)(ds =>
                declare(
                    ds.id is(primaryKey, (dbType(COLUMN_TYPE_ID))),
                    ds.name is (dbType(COLUMN_TYPE_NAME)),
                    ds.pluginId is (dbType(COLUMN_TYPE_ID)),
                    ds.ownerId is (dbType(COLUMN_TYPE_ID)),
                    ds._desc is (dbType(COLUMN_TYPE_DESCRIPTION)),
                    ds.description is (dbType(COLUMN_TYPE_DESCRIPTION)),
                    columns(ds.name, ds.ownerId) are (unique)
                ))

            on(groups)(group =>
                declare(
                    group.id is(primaryKey, dbType(COLUMN_TYPE_ID)),
                    group.name is (dbType(COLUMN_TYPE_NAME)),
                    group.ownerId is (dbType(COLUMN_TYPE_ID)),
                    columns(group.name, group.ownerId) are (unique)
                ))

            on(analyses)(analysis =>
                declare(
                    analysis.id is(primaryKey, dbType(COLUMN_TYPE_ID)),
                    analysis.name is (dbType(COLUMN_TYPE_NAME)),
                    analysis.ownerId is (dbType(COLUMN_TYPE_ID)),
                    analysis.defaultCustomizationId is (dbType(COLUMN_TYPE_ID)),
                    analysis._desc is (dbType(COLUMN_TYPE_DESCRIPTION)),
                    analysis.description is (dbType(COLUMN_TYPE_DESCRIPTION)),
                    analysis.token is (dbType(COLUMN_TYPE_TOKEN)),
                    columns(analysis.name, analysis.ownerId) are (unique)
                ))

            on(privileges)(p =>
                declare(
                    p.id is(primaryKey, (dbType(COLUMN_TYPE_ID))),
                    p.granteeId is (dbType(COLUMN_TYPE_ID)),
                    p.granterId is (dbType(COLUMN_TYPE_ID)),
                    p.objectId is (dbType(COLUMN_TYPE_ID)),
                    p.granteeClassName is (dbType(COLUMN_TYPE_CLASSNAME)),
                    p.objectClassName is (dbType(COLUMN_TYPE_CLASSNAME)),
                    p.privilegeClass is (dbType(COLUMN_TYPE_FULL_CLASSNAME)),
                    columns(p.granteeId, p.privilegeClass, p.objectId) are (unique)
                ))

            on(ontologyCustomizations)(c =>
                declare(
                    c.id is(primaryKey, (dbType(COLUMN_TYPE_ID))),
                    c.name is (dbType(COLUMN_TYPE_NAME)),
                    c.ownerId is (dbType(COLUMN_TYPE_ID)),
                    c.ontologyURLs is (dbType(COLUMN_TYPE_URI)),
                    columns(c.name, c.ownerId) are (unique)
                ))

            on(classCustomizations)(c =>
                declare(
                    c.id is(primaryKey, (dbType(COLUMN_TYPE_ID))),
                    c.uri is (dbType(COLUMN_TYPE_URI)),
                    c.glyph is (dbType("varchar(1)")),
                    c.fillColor is (dbType(COLUMN_TYPE_COLOR)),
                    c.ontologyCustomizationId is (dbType(COLUMN_TYPE_ID))
                ))

            on(propertyCustomizations)(c =>
                declare(
                    c.id is(primaryKey, (dbType(COLUMN_TYPE_ID))),
                    c.strokeColor is (dbType(COLUMN_TYPE_COLOR)),
                    c.classCustomizationId is (dbType(COLUMN_TYPE_ID)),
                    c.uri is (dbType(COLUMN_TYPE_URI))
                ))

            on(prefixes)(p =>
                declare(
                    p.id is(primaryKey, (dbType(COLUMN_TYPE_ID))),
                    p.name is (dbType(COLUMN_TYPE_NAME)),
                    p.prefix is (dbType(COLUMN_TYPE_PREFIX)),
                    p.url is (dbType(COLUMN_TYPE_URI)),
                    p.ownerId is (dbType(COLUMN_TYPE_ID)),
                    columns(p.name, p.ownerId) are (unique),
                    columns(p.prefix, p.ownerId) are (unique)
                ))

            // When a PluginDbRepresentation is deleted, all of the its instances and data sources will get deleted.
            pluginsPluginInstances.foreignKeyDeclaration.constrainReference(onDelete cascade)
            pluginsDataSources.foreignKeyDeclaration.constrainReference(onDelete cascade)

            // When an Analysis is deleted, all of the its plugin instances will get deleted.
            analysesPluginInstances.foreignKeyDeclaration.constrainReference(onDelete cascade)
            analysesPluginInstancesBindings.foreignKeyDeclaration.constrainReference(onDelete cascade)

            // When a Parameter is deleted, all of the its parameterValues will get deleted.
            valuesOfBooleanParameters.foreignKeyDeclaration.constrainReference(onDelete cascade)
            valuesOfFloatParameters.foreignKeyDeclaration.constrainReference(onDelete cascade)
            valuesOfIntParameters.foreignKeyDeclaration.constrainReference(onDelete cascade)
            valuesOfStringParameters.foreignKeyDeclaration.constrainReference(onDelete cascade)

            // When a PluginDbRepresentation is deleted, all of the its Parameters will get deleted.
            booleanParametersOfPlugins.foreignKeyDeclaration.constrainReference(onDelete cascade)
            floatParametersOfPlugins.foreignKeyDeclaration.constrainReference(onDelete cascade)
            intParametersOfPlugins.foreignKeyDeclaration.constrainReference(onDelete cascade)
            stringParametersOfPlugins.foreignKeyDeclaration.constrainReference(onDelete cascade)

            // When a PluginInstance is deleted, all of the its ParameterInstances will get deleted.
            booleanParameterValuesOfPluginInstances.foreignKeyDeclaration.constrainReference(onDelete cascade)
            floatParameterValuesOfPluginInstances.foreignKeyDeclaration.constrainReference(onDelete cascade)
            intParameterValuesOfPluginInstances.foreignKeyDeclaration.constrainReference(onDelete cascade)
            stringParameterValuesOfPluginInstances.foreignKeyDeclaration.constrainReference(onDelete cascade)

            // When PluginInstance is deleted, delete all its Source/Target bindings.
            bindingsOfSourcePluginInstances.foreignKeyDeclaration.constrainReference(onDelete cascade)
            bindingsOfTargetPluginInstances.foreignKeyDeclaration.constrainReference(onDelete cascade)

            // When DataSource is deleted, delete all associated ParameterValues.
            booleanParameterValuesOfDataSources.foreignKeyDeclaration.constrainReference(onDelete cascade)
            floatParameterValuesOfDataSources.foreignKeyDeclaration.constrainReference(onDelete cascade)
            intParameterValuesOfDataSources.foreignKeyDeclaration.constrainReference(onDelete cascade)
            stringParameterValuesOfDataSources.foreignKeyDeclaration.constrainReference(onDelete cascade)

            // When User/Group is deleted, delete all memberships items.
            groupMembership.leftForeignKeyDeclaration.constrainReference(onDelete cascade)
            groupMembership.rightForeignKeyDeclaration.constrainReference(onDelete cascade)

            // When user is deleted, delete all theirs owned items.
            groupOwnership.foreignKeyDeclaration.constrainReference(onDelete cascade)
            customizationOwnership.foreignKeyDeclaration.constrainReference(onDelete cascade)
            analysisOwnership.foreignKeyDeclaration.constrainReference(onDelete cascade)
            dataSourceOwnership.foreignKeyDeclaration.constrainReference(onDelete cascade)
            pluginOwnership.foreignKeyDeclaration.constrainReference(onDelete cascade)

            // When ontology customization is removed, remove all sub-customizations
            classCustomizationsOfOntologies.foreignKeyDeclaration.constrainReference(onDelete cascade)
            propertyCustomizationsOfClasses.foreignKeyDeclaration.constrainReference(onDelete cascade)
        }

        /**
         * Drops the current schema and recreates it.
         */
        def recreate() {
            declareKeys()
            inTransaction {
                drop
                create
            }
        }

        /**
         * Runs given code block wrapped in inTransaction statement (Squeryl)
         * @param body
         */
        def wrapInTransaction[C](body: => C) = {
            DataException.wrap {
                inTransaction {
                    body
                }
            }
        }

        /**
         * Persists the specified entity to the specified table.
         * @param entity The entity to persist.
         * @param table Tha table to persist the entity int.
         * @tparam C Type of the entity.
         */
        def persist[C <: Entity](entity: C, table: Table[C]) {
            wrapInTransaction {
                if (table.where(_.id === entity.id).isEmpty) {
                    table.insert(entity)
                } else {
                    table.update(entity)
                }
            }
        }

        /**
         * Creates 1:N relation between this entity (on '1' side of relation) and specified entity (on 'N' side of
         * relation).
         * Specified entity wil be persisted
         *
         * @param entity - specified entity to be related with this entity
         * @param relation  - definition of 1:N relation between this and specified entity
         * @tparam A - type of specified entity
         * @return Returns persisted specified entity
         */
        def associate[A <: Entity](entity: A, relation: OneToMany[A]): A = {
            wrapInTransaction {
                if (relation.find(e => e.id == entity.id).isEmpty) {
                    relation.associate(entity)
                }
                entity
            }
        }

        /**
         * Creates M:N relation of this entity and specified entity.
         * @param entity - specified entity that will be related with this entity
         * @param relation - definition of M:N relation
         * @tparam A - type of specified entity
         * @return Returns persisted specified entity
         */
        def associate[A <: Entity](entity: A, relation: ManyToMany[A, _]): A = {
            wrapInTransaction {
                if (relation.find(_.id == entity.id).isEmpty) {
                    relation.associate(entity)
                }
                entity
            }
        }

        /**
         * Removes M:N relation between this entity and specified entity.
         * No entity will be removed.
         *
         * @param entity - specified entity whose relation with this item should be removed
         * @param relation - definition on M:N relation
         * @tparam A - type of specified entity
         * @return Returns specified entity
         */
        def dissociate[A <: Entity](entity: A, relation: ManyToMany[A, _]): A = {
            wrapInTransaction {
                if (relation.find(_.id == entity.id).isDefined) {
                    relation.dissociate(entity)
                }
                entity
            }
        }
    }

}
