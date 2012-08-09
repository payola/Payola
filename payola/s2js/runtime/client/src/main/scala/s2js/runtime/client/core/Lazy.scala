package s2js.runtime.client.core

class Lazy[A](initializer: () => A)
{
    private var value: A = _

    private var isInitialized = false

    def get: A = {
        if (!isInitialized) {
            value = initializer()
            isInitialized = true
        }
        value
    }
}
