package cz.payola.data.squeryl.repositories

import cz.payola.data.squeryl._
import cz.payola.data.squeryl.entities.plugins._
import cz.payola.data.squeryl.entities.plugins.parameters._
import org.squeryl.dsl.ast.LogicalBoolean
import org.squeryl.PrimitiveTypeMode._
import org.squeryl._
import org.squeryl.dsl._
import cz.payola.data.squeryl.entities._

/**
 * Defines operation of repositories to access persisted
 * [[cz.payola.data.squeryl.entities.plugins.PluginInstanceLike]] entities
 */
trait PluginInstanceRepositoryComponent extends TableRepositoryComponent
{
    self: SquerylDataContextComponent =>

    /**
     * A repository to access persisted [[cz.payola.data.squeryl.entities.plugins.PluginInstanceLike]] entities
     */
    trait PluginInstanceTableRepository[A <: Entity with PluginInstanceLike]
    {
        protected val pluginInstanceLikeTable: Table[A]

        protected val pluginInstanceLikeEntityConverter: EntityConverter[A]

        protected val booleanParameterValuesRelation: OneToManyRelationImpl[A, BooleanParameterValue]

        protected val floatParameterValuesRelation: OneToManyRelationImpl[A, FloatParameterValue]

        protected val intParameterValuesRelation: OneToManyRelationImpl[A, IntParameterValue]

        protected val stringParameterValuesRelation: OneToManyRelationImpl[A, StringParameterValue]

        protected def getPluginInstanceLikeId(parameterValue: Option[ParameterValue[_]]): Option[String]

        def persistPluginInstance(pluginInstance: cz.payola.domain.entities.plugins.PluginInstance) {
            schema.wrapInTransaction {
                // First persist ParameterInstance then associate all parameter values
                val persistedInstance = pluginInstanceLikeEntityConverter(pluginInstance)
                schema.persist(persistedInstance, pluginInstanceLikeTable)

                persistedInstance.parameterValues.foreach {
                    case paramValue: BooleanParameterValue => schema.associate(
                        paramValue, booleanParameterValuesRelation.left(persistedInstance))
                    case paramValue: FloatParameterValue => schema.associate(
                        paramValue, floatParameterValuesRelation.left(persistedInstance))
                    case paramValue: IntParameterValue => schema.associate(
                        paramValue, intParameterValuesRelation.left(persistedInstance))
                    case paramValue: StringParameterValue => schema.associate(
                        paramValue, stringParameterValuesRelation.left(persistedInstance))
                }

                persistedInstance
            }
        }

        def persistParameterValue(parameterValue: cz.payola.domain.entities.plugins.ParameterValue[_]) {
            ParameterValue(parameterValue) match {
                case b: BooleanParameterValue => schema.persist(b, schema.booleanParameterValues)
                case f: FloatParameterValue => schema.persist(f, schema.floatParameterValues)
                case i: IntParameterValue => schema.persist(i, schema.intParameterValues)
                case s: StringParameterValue => schema.persist(s, schema.stringParameterValues)
            }
        }

        private def _getLoadQuery(entityFilter: (PluginInstanceLike) => LogicalBoolean) = {
            join(pluginInstanceLikeTable, schema.booleanParameterValues.leftOuter,
                schema.floatParameterValues.leftOuter, schema.intParameterValues.leftOuter,
                schema.stringParameterValues.leftOuter)((instance, bPar, fPar, iPar, sPar) =>
                where(entityFilter(instance))
                    select(instance, bPar, fPar, iPar, sPar)
                    on(getPluginInstanceLikeId(bPar) === Some(instance.id),
                    getPluginInstanceLikeId(fPar) === Some(instance.id),
                    getPluginInstanceLikeId(iPar) === Some(instance.id),
                    getPluginInstanceLikeId(sPar) === Some(instance.id))
            )
        }

        protected def loadPluginInstancesByFilter(entityFilter: (PluginInstanceLike) => LogicalBoolean):
        Seq[PluginInstanceLike] = schema.wrapInTransaction {

            val pluginInstances = _getLoadQuery(entityFilter).groupBy(_._1).map {r =>
                val instance = r._1
                instance.parameterValues = r._2.flatMap(c => Seq(c._2, c._3, c._4, c._5).flatten).toList//.sortBy(_.parameter.ordering.getOrElse(9999))

                instance
            }(collection.breakOut)

            val pluginIds = pluginInstances.map(_.pluginId).toSeq
            val pluginsByIds = pluginRepository.getByIds(pluginIds).map(p => (p.id, p)).toMap

            // Set plugins by id to parameter instances
            pluginInstances.foreach {p =>
                p.plugin = pluginsByIds(p.pluginId)

                _mapParameterValuesToParameters(p)
            }

            pluginInstances
        }

        private def _mapParameterValuesToParameters(pluginInstanceLike: PluginInstanceLike) {
            // Map parameter to parameter value
            pluginInstanceLike.parameterValues.foreach {v =>
                val value = v.asInstanceOf[ParameterValue[_]]
                value.parameter = Parameter(pluginInstanceLike.plugin.parameters.find(_.id == value.parameterId).get)
            }
        }
    }

}
