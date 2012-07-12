package cz.payola.data.squeryl.repositories

import cz.payola.data.squeryl._
import cz.payola.domain.entities.settings
import cz.payola.data.squeryl.entities.settings._
import cz.payola.data.squeryl.entities.User

trait OntologyRepositoryComponent extends TableRepositoryComponent
{
    self: SquerylDataContextComponent =>

    lazy val ontologyCustomizationRepository =
        new TableRepository[OntologyCustomization, (OntologyCustomization, Option[User])](
            schema.ontologyCustomizations,OntologyCustomization)
        with OntologyCustomizationRepository[OntologyCustomization]
        with ShareableEntityTableRepository[OntologyCustomization]
    {
        override def persist(entity: AnyRef) = {
            // First persist ontology customization ...
            val ontologyCustomization = super.persist(entity)

            // ... then persist its class customizations
            entity.asInstanceOf[settings.OntologyCustomization].classCustomizations.map{ cc =>
                    val classCustomization = ontologyCustomization.associateClassCustomization(cc)

                    // ... and its property customizations
                    classCustomization.asInstanceOf[settings.ClassCustomization].propertyCustomizations.map{ pc =>
                            classCustomization.associatePropertyCustomization(pc)
                    }
                }

            ontologyCustomization
        }
    }
}
