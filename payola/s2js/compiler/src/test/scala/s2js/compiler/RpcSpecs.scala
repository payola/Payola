package s2js.compiler

class RpcSpecs extends CompilerFixtureSpec
{
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
                """
                    goog.provide('server.o');
                """
            }
    }

    it("method call gets translated into a rpc call") {
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
                    goog.provide('client');
                    goog.provide('server.o');
                    goog.require('s2js.RPCWrapper');

                    client.main = function() {
                        var self = this;
                        var fooValue = s2js.RPCWrapper.callSync('server.o.foo', [2, 'xyz']);
                    };
                    client.__class__ = new s2js.Class('client', []);
                """
            }
    }
}
