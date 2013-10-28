package cz.payola.web.client.presenters.entity.settings

import cz.payola.web.client.events.EventArgs
import cz.payola.common.entities.settings.DefinedCustomization

class DefinedCustomizationEventArgs(customization: DefinedCustomization)
    extends EventArgs[DefinedCustomization](customization)
{
}
