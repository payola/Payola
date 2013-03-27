package cz.payola.web.client.models

class VertexVariableNameGenerator {

    private var counter = 0

    def nextName : String = {
        counter += 1
        "v"+counter.toString
    }

}
