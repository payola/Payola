package cz.payola.web.client.models

/**
 * Client-side vertex name generator
 * @author Jiri Helmich
 */
class VertexVariableNameGenerator {

    private var counter = 0

    def nextName : String = {
        counter += 1
        "v"+counter.toString
    }

}
