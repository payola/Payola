package cz.payola.web.client.events

import cz.payola.web.client.views.bootstrap.InputControl

class ClassCustomizationModificationEventArgs[A](val input: InputControl, val classURI: String, val value: String, target: A) extends EventArgs[A](target)

class PropertyCustomizationModificationEventArgs[A](val input: InputControl, val classURI: String, val propertyURI: String, val value: String, target: A) extends EventArgs[A](target)
