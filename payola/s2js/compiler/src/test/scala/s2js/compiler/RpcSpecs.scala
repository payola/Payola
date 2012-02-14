package s2js.compiler

class RpcSpecs extends CompilerFixtureSpec {
    it("remote objects aren't compiled") {
        configMap =>
            scalaCode {
                """
                    package server {
                        @scala.remote
                        object o {
                            def foo(bar: Int, baz: String): Int = bar * baz.length
                        }
                    }
                """
            } shouldCompileTo {
                ""
            }
    }

    it("method call gets translated into an rpc call") {
        configMap =>
            scalaCode {
                """
                    package server {
                        @scala.remote
                        object o {
                            def foo(bar: Int, baz: String): Int = bar * baz.length
                        }
                    }

                    object client {
                        def main() {
                            val fooValue = server.o.foo(2, "xyz")
                        }
                    }
                """
            } shouldCompileTo {
                """

                """
            }
    }
}
