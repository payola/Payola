package cz.payola.web.client.presenters.entity.settings

import cz.payola.common.entities.settings.UserCustomization
import cz.payola.web.client.events.EventArgs

class UserCustomizationEventArgs(customization: UserCustomization)
    extends EventArgs[UserCustomization](customization)
{
}
