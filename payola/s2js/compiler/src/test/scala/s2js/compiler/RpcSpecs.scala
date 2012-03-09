package s2js.compiler

class RpcSpecs extends CompilerFixtureSpec
{
    it("remote objects aren't compiled") {
        configMap =>
            scalaCode {
                """
                    package server {
                        @remote object o {
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

    it("synchronous remote method call gets translated into a synchronous rpc call") {
        configMap =>
            scalaCode {
                """
                    package server {
                        @remote object o {
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

    it("asynchronous temote method call gets translated into an asynchronous rpc call") {
        configMap =>
            scalaCode {
                """
                    import s2js.compiler.async

                    package server {
                        @remote object o {
                            @async def foo(bar: String)(successCallback: (Int => Unit))(errorCallback: (Throwable => Unit)) {
                                successCallback(bar.length)
                            }
                        }
                    }

                    object client {
                        def main() {
                            var x = 0
                            server.o.foo("xyz") {i => x = i} {e => x = -1}
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
                        var x = 0;
                        s2js.RPCWrapper.callAsync('server.o.foo', ['xyz'], function(i) { x = i; }, function(e) { x = -1; });
                    };
                    client.__class__ = new s2js.Class('client', []);
                """
            }
    }
}
