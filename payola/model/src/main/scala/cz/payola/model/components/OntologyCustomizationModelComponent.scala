package cz.payola.model.components

import cz.payola.data._
import cz.payola.domain.RdfStorageComponent
import cz.payola.domain.entities.privileges._
import cz.payola.domain.entities.settings.OntologyCustomization
import cz.payola.model.EntityModelComponent
import cz.payola.domain.entities.User

trait OntologyCustomizationModelComponent extends EntityModelComponent
{
    self: DataContextComponent with RdfStorageComponent =>

    lazy val ontologyCustomizationModel = new ShareableEntityModel[OntologyCustomization](
        ontologyCustomizationRepository,
        classOf[UseOntologyCustomizationPrivilege],
        (user: User) => user.ownedOntologyCustomizations)
    {

    }
}
