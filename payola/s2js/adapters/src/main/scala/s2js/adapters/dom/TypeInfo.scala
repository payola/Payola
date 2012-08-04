package s2js.adapters.dom

trait TypeInfo
{
    val typeName: String

    val typeNamespace: String

    def isDerivedFrom(typeNamespaceArg: String, typeNameArg: String, derivationMethod: Int): Boolean
}

object TypeInfo
{
    val DERIVATION_RESTRICTION = 0x01

    val DERIVATION_EXTENSION = 0x02

    val DERIVATION_UNION = 0x04

    val DERIVATION_LIST = 0x08
}
