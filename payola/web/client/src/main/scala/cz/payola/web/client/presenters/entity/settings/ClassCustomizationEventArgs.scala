package cz.payola.web.client.presenters.entity.settings

import cz.payola.web.client.events.EventArgs
import cz.payola.common.entities.settings.ClassCustomization

class ClassCustomizationEventArgs[A](target: A, val classCustomization: ClassCustomization, val newValue: String)
    extends EventArgs[A](target)
