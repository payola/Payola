package cz.payola.web.client.events

class ClassCustomizationModificationEventArgs[A, B](val classURI: String, val value: B, target: A) extends EventArgs[A](target)

class ClassPropertyCustomizationModificationEventArgs[A, B](val classURI: String, val propertyURI: String, val value: B, target: A) extends EventArgs[A](target)
