package cz.payola.data.squeryl.repositories

import org.squeryl.PrimitiveTypeMode._
import cz.payola.data.squeryl.entities.User
import cz.payola.data.squeryl.entities.PluginDbRepresentation
import cz.payola.domain.entities.Plugin
import cz.payola.data.squeryl.entities.plugins.Parameter
import cz.payola.data.squeryl._
import org.squeryl.dsl.ast.LogicalBoolean
import cz.payola.data.squeryl.entities.plugins.parameters._
import cz.payola.data.{PaginationInfo, DataException}

/**
 * Provides repository to access persisted plugins
 */
trait PluginRepositoryComponent extends TableRepositoryComponent
{
    self: SquerylDataContextComponent =>
    /**
     * A repository to access persisted plugins
     */
    lazy val pluginRepository = new PluginRepository
    {
        private type QueryType = (PluginDbRepresentation, Option[User], Option[BooleanParameter],
            Option[FloatParameter],
            Option[IntParameter], Option[StringParameter])

        private val representationRepository =
            new TableRepository[PluginDbRepresentation, QueryType](schema.plugins, PluginDbRepresentation)
            {
                protected def getSelectQuery(entityFilter: (PluginDbRepresentation) => LogicalBoolean) =
                    schema.wrapInTransaction {
                        join(schema.plugins, schema.users.leftOuter, schema.booleanParameters.leftOuter,
                            schema.floatParameters.leftOuter, schema.intParameters.leftOuter,
                            schema.stringParameters.leftOuter)((p, o, bPar, fPar, iPar, sPar) =>
                            where(entityFilter(p))
                                select(p, o, bPar, fPar, iPar, sPar)
                                on(o.map(_.id) === p.ownerId,
                                bPar.map(_.pluginId) === Some(p.id),
                                fPar.map(_.pluginId) === Some(p.id),
                                iPar.map(_.pluginId) === Some(p.id),
                                sPar.map(_.pluginId) === Some(p.id))
                        )
                    }

                protected def processSelectResults(results: Seq[QueryType]) = schema.wrapInTransaction {
                    results.groupBy(_._1).map {r =>
                        val plugin = r._1
                        plugin.owner = r._2.head._2
                        plugin.parameters = r._2.flatMap(c => Seq(c._3, c._4, c._5, c._6).flatten).sortBy(_.ordering.getOrElse(9999))

                        plugin
                    }(collection.breakOut)
                }
            }

        def getByIds(ids: Seq[String]): Seq[Plugin] = {
            representationRepository.getByIds(ids).map(_.toPlugin)
        }

        def removeById(id: String): Boolean = {
            representationRepository.removeById(id)
        }

        def getAll(pagination: Option[PaginationInfo] = None): Seq[Plugin] = {
            representationRepository.getAll(pagination).map(_.toPlugin).sortBy(_.name)
        }

        def getAllPublic(forListing: Boolean = false): Seq[Plugin] = {
            representationRepository.selectWhere(_.isPublic === true).map(_.toPlugin).sortBy(_.name)
        }

        def getAllByOwnerId(ownerId: Option[String]): Seq[Plugin] = {
            representationRepository.selectWhere(_.ownerId === ownerId).map(_.toPlugin).sortBy(_.name)
        }

        def getByName(name: String): Option[Plugin] = {
            representationRepository.selectOneWhere(_.name === name).map(_.toPlugin)
        }

        def persist(entity: AnyRef): Plugin = schema.wrapInTransaction {
            entity match {
                case plugin: Plugin => {
                    val representation = representationRepository.persist(entity)
                    plugin.parameters.foreach {parameter =>
                        Parameter(parameter) match {
                            case b: BooleanParameter => {
                                schema.associate(b, schema.booleanParametersOfPlugins.left(representation))
                            }
                            case f: FloatParameter => {
                                schema.associate(f, schema.floatParametersOfPlugins.left(representation))
                            }
                            case i: IntParameter => {
                                schema.associate(i, schema.intParametersOfPlugins.left(representation))
                            }
                            case s: StringParameter => {
                                schema.associate(s, schema.stringParametersOfPlugins.left(representation))
                            }
                        }
                    }

                    plugin
                }
                case _ => throw new DataException("Couldn't convert the entity to a plugin.")
            }
        }

        def getCount: Long = {
            representationRepository.getCount
        }
    }
}
