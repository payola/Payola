package cz.payola.data.squeryl.repositories

import cz.payola.data.squeryl._
import cz.payola.data.squeryl.entities.settings._
import cz.payola.data.squeryl.entities.User
import org.squeryl.PrimitiveTypeMode._

trait OntologyRepositoryComponent extends TableRepositoryComponent
{
    self: SquerylDataContextComponent =>

    lazy val ontologyCustomizationRepository =
        new TableRepository[OntologyCustomization, (OntologyCustomization, Option[User])](
            schema.ontologyCustomizations,OntologyCustomization)
        with OntologyCustomizationRepository
        with NamedEntityTableRepository[OntologyCustomization]
        with OptionallyOwnedEntityTableRepository[OntologyCustomization]
        with ShareableEntityTableRepository[OntologyCustomization]
    {
        override def persist(entity: AnyRef) = {
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
            
        def getClassCustomizations(ontologyCustomizationId: String): Seq[ClassCustomization] = {
            val result = join(schema.classCustomizations, schema.propertyCustomizations.leftOuter)((c, p) =>
                where(c.ontologyCustomizationId === ontologyCustomizationId)
                select(c, p)
                on(p.map(_.classCustomizationId) === Some(c.id))
            ).toList

            result.groupBy(_._1).map { r =>
                val classCustomization = r._1
                classCustomization.propertyCustomizations = r._2.flatMap(_._2)

                classCustomization
            }(collection.breakOut)
        }
    }
}
