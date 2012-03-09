package cz.payola.common.model

trait Analysis extends NamedModelObject with OwnedObject {
    // Analysis consists of chained PluginInstances
    // TODO

    def isOwnedByUser(u: User): Boolean = owner == u
}
