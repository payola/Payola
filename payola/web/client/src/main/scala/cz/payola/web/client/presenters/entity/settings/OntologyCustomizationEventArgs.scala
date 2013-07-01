package cz.payola.web.client.presenters.entity.settings

import cz.payola.common.entities.settings.OntologyCustomization
import cz.payola.web.client.events.EventArgs

class OntologyCustomizationEventArgs(customization: OntologyCustomization)
    extends EventArgs[OntologyCustomization](customization)
