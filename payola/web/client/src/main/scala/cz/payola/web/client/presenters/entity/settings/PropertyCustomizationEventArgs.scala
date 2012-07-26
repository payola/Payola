package cz.payola.web.client.presenters.entity.settings

import cz.payola.web.client.events.EventArgs
import cz.payola.common.entities.settings._

class PropertyCustomizationEventArgs[A](
    target: A,
    val classCustomization: ClassCustomization,
    val propertyCustomization: PropertyCustomization,
    val newValue: String)
    extends EventArgs[A](target)
