package controllers

import s2js.runtime.shared.DependencyProvider

class Controller extends play.api.mvc.Controller
{
    // Setup the DependencyProvider.
    DependencyProvider.dependencyFile = new java.io.File("web/server/public/dependencies")
}
