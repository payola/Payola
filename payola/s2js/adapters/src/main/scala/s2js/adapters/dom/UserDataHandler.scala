package s2js.adapters.dom

trait UserDataHandler
{
    def handle(operation: Int, key: String, data: DOMUserData, src: Node, dst: Node)
}

object UserDataHandler
{
    val NODE_CLONED = 1

    val NODE_IMPORTED = 2

    val NODE_DELETED = 3

    val NODE_RENAMED = 4

    val NODE_ADOPTED = 5
}
