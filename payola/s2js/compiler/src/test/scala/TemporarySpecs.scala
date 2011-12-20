package s2js.compiler

class TemporarySpecs extends CompilerFixtureSpec {
    describe("Temporary") {
        it("fdsfs") {
            configMap =>
                scalaCode {
                    """
                        class Exception(val message: String = "", val cause: Exception = null) extends Throwable
                    """
                } shouldCompileTo {
                    """

                    """
                }
        }

    }
}
