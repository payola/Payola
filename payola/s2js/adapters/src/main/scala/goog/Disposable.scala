package s2js.adapters.goog

class Disposable {
    def dispose() {}

    def isDisposed: Boolean = false

    def registerDisposable(disposable: Disposable) {}
}