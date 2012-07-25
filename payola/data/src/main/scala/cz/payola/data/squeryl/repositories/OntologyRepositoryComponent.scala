package cz.payola.data.squeryl.repositories

import cz.payola.data.squeryl._
import cz.payola.data.squeryl.entities.settings._
import cz.payola.data.squeryl.entities.User
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.dsl.ast.LogicalBoolean

trait OntologyRepositoryComponent extends TableRepositoryComponent
{
    self: SquerylDataContextComponent =>

    private type QueryType = (OntologyCustomization, Option[User], Option[ClassCustomization], Option[PropertyCustomization])

    lazy val ontologyCustomizationRepository =
        new TableRepository[OntologyCustomization, QueryType](schema.ontologyCustomizations, OntologyCustomization)
        with OntologyCustomizationRepository
        with NamedEntityTableRepository[OntologyCustomization]
        with OptionallyOwnedEntityTableRepository[OntologyCustomization, QueryType]
        with ShareableEntityTableRepository[OntologyCustomization, QueryType]
    {
        override def persist(entity: AnyRef) = wrapInTransaction {
            val persistedOntologyCustomization = super.persist(entity)
            entity match {
                case o: OntologyCustomization => // The entity is already in the database, so classes are already there.
                case o: cz.payola.common.entities.settings.OntologyCustomization => {
                    // Associate and persist the classes.
                    o.classCustomizations.foreach { classCustomization =>
                        val persistedClassCustomization = schema.associate(ClassCustomization(classCustomization),
                            schema.classCustomizationsOfOntologies.left(persistedOntologyCustomization))

                        // Associate and persist the properties
                        persistedClassCustomization.propertyCustomizations.foreach { propertyCustomization =>
                            schema.associate(PropertyCustomization(propertyCustomization),
                                schema.propertyCustomizationsOfClasses.left(persistedClassCustomization))
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

        protected def getSelectQuery(entityFilter: (OntologyCustomization) => LogicalBoolean) =
        {
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
            results.groupBy(_._1).map { r =>
                 val ontologyCustomization = r._1
                 ontologyCustomization.owner = r._2.head._2
                 ontologyCustomization.classCustomizations = r._2.groupBy(_._3).flatMap { c =>
                     val classCustomization = c._1
                     if (classCustomization.isDefined) {
                        classCustomization.get.propertyCustomizations = c._2.flatMap(_._4)
                     }

                     classCustomization
                 } (collection.breakOut)

                 ontologyCustomization
            }(collection.breakOut)
        }
    }
}
