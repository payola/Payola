package controllers

import cz.payola.domain.entities.User

case class InvocationInfo(
    methodToRun: java.lang.reflect.Method,
    clazz: java.lang.Class[_],
    runnableObj: Any,
    methodIsSecured: Boolean,
    authorizationRequired: Boolean,
    user: Option[User])
