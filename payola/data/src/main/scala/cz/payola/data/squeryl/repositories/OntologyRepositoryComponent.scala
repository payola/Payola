package cz.payola.data.squeryl.repositories

import cz.payola.data.squeryl._
import cz.payola.data.squeryl.entities.settings._

trait OntologyRepositoryComponent extends TableRepositoryComponent
{
    self: SquerylDataContextComponent =>
    
    private type QueryType = (OntologyCustomization, Option[ClassCustomization], Option[PropertyCustomization])

    lazy val ontologyCustomizationRepository = new LazyTableRepository[OntologyCustomization](
        schema.ontologyCustomizations, OntologyCustomization)
        with OntologyCustomizationRepository
        with NamedEntityTableRepository[OntologyCustomization]
        with OptionallyOwnedEntityTableRepository[OntologyCustomization]
        with ShareableEntityTableRepository[OntologyCustomization]
    {
        override def persist(entity: AnyRef) = {
            val persistedOntologyCustomization = super.persist(entity)
            entity match {
                case o: OntologyCustomization => // The entity is already in the database, so classes are already there.
                case _ => {
                    // Associate and persist the classes.
                    persistedOntologyCustomization.classCustomizations.foreach { classCustomization =>
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

        def persistClassCustomization(classCustomization: AnyRef) {
            persist(ClassCustomization(classCustomization), schema.classCustomizations)
        }

        def persistPropertyCustomization(propertyCustomization: AnyRef) {
            persist(PropertyCustomization(propertyCustomization), schema.propertyCustomizations)
        }
    }
}
