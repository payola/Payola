package cz.payola.data.entities

trait Parameter[A] extends cz.payola.domain.entities.parameters.Parameter[A]
{
    def instances: Seq[cz.payola.domain.entities.parameters.ParameterInstance[A]]

    def pluginId: String
}
