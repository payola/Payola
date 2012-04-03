package cz.payola.data.rdf

abstract class ProviderConfiguration[A <: DataProvider]
{
    def createProvider: A
}
