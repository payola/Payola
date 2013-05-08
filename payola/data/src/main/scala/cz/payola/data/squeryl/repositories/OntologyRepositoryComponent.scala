package cz.payola.data.squeryl.repositories

import cz.payola.data.squeryl._
import cz.payola.data.squeryl.entities.settings._
import cz.payola.data.squeryl.entities.User
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.dsl.ast.LogicalBoolean

/**
 * Provides repository to access persisted ontology customizations
 */
trait OntologyRepositoryComponent extends TableRepositoryComponent
{
    self: SquerylDataContextComponent =>
    private type QueryType = (OntologyCustomization, Option[User], Option[ClassCustomization],
        Option[PropertyCustomization])

    /**
     * A repository to access persisted ontology customizations
     */
    lazy val ontologyCustomizationRepository =
        new TableRepository[OntologyCustomization, QueryType](schema.ontologyCustomizations, OntologyCustomization)
            with OntologyCustomizationRepository
            with NamedEntityTableRepository[OntologyCustomization]
            with OptionallyOwnedEntityTableRepository[OntologyCustomization, QueryType]
            with ShareableEntityTableRepository[OntologyCustomization, QueryType]
        {
            override def persist(entity: AnyRef) = wrapInTransaction {
                val inDbOntologyOpt = entity match {
                    case o: cz.payola.common.entities.settings.OntologyCustomization => {
                        getById(o.id)
                    }
                    case _ => None
                }

                val persistedOntologyCustomization = inDbOntologyOpt.getOrElse( super.persist(entity) )

                entity match {
                    //case o: OntologyCustomization =>
                        // The entity is already in the database, but classes/properties may need an update
                    case o: cz.payola.common.entities.settings.OntologyCustomization => {
                        // Associate and persist the classes.
                        o.classCustomizations.foreach { classCustomization =>

                            val inDbClassCustomization =
                                if(inDbOntologyOpt.isDefined) {
                                    inDbOntologyOpt.get.classCustomizations.find{
                                        inDbClassCust => classCustomization.id == inDbClassCust.id
                                    }
                                } else {
                                    None
                               }
                            val persistedClassCustomization = if(inDbClassCustomization.isDefined) {
                                ClassCustomization.convert(inDbClassCustomization).get
                            } else {
                                schema.associate(ClassCustomization(classCustomization),
                                    schema.classCustomizationsOfOntologies.left(persistedOntologyCustomization))
                            }

                            // Associate and persist the properties
                            classCustomization.propertyCustomizations.foreach { propertyCustomization =>
                                if(inDbClassCustomization.isEmpty
                                    || !inDbClassCustomization.get.propertyCustomizations.exists{inDbProp => inDbProp.id == propertyCustomization.id}) {

                                    schema.associate(PropertyCustomization(propertyCustomization),
                                        schema.propertyCustomizationsOfClasses.left(persistedClassCustomization))
                                }
                            }
                        }
                    }
                }

                persistedOntologyCustomization
            }

            override def removeById(id: String) = {
                // Unset from DefaultCustomizations of Analyses
                analysisRepository.ontologyCustomizationIsRemoved(id)

                super.removeById(id)
            }

            def persistClassCustomization(classCustomization: AnyRef) {
                persist(ClassCustomization(classCustomization), schema.classCustomizations)
            }

            def persistPropertyCustomization(propertyCustomization: AnyRef) {
                persist(PropertyCustomization(propertyCustomization), schema.propertyCustomizations)
            }

            protected def getSelectQuery(entityFilter: (OntologyCustomization) => LogicalBoolean) = {
                join(table, schema.users.leftOuter, schema.classCustomizations.leftOuter,
                    schema.propertyCustomizations.leftOuter)((o, u, c, p) =>
                    where(entityFilter(o))
                        select(o, u, c, p)
                        on(o.ownerId === u.map(_.id),
                        c.map(_.ontologyCustomizationId) === Some(o.id),
                        p.map(_.classCustomizationId) === c.map(_.id))
                )
            }

            protected def processSelectResults(results: Seq[QueryType]) = wrapInTransaction {
                results.groupBy(_._1).map {r =>
                    val ontologyCustomization = r._1
                    ontologyCustomization.owner = r._2.head._2
                    ontologyCustomization.classCustomizations = r._2.groupBy(_._3).flatMap {c =>
                        val classCustomization = c._1
                        if (classCustomization.isDefined) {
                            classCustomization.get.propertyCustomizations = c._2.flatMap(_._4).sortBy(_.uri)
                        }

                        classCustomization
                    }(collection.breakOut).sortBy(_.uri)

                    ontologyCustomization
                }(collection.breakOut)
            }
        }
}
