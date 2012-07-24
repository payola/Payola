package cz.payola.domain.sparql

class VariableGenerator extends Function0[Variable]
{
    private var i = 0

    def apply() = {
        i += 1
        Variable("v" + i)
    }
}
