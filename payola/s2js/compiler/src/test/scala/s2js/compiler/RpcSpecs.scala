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
                        var fooValue = s2js.RPCWrapper.callSync('server.o.foo', [2, 'xyz'],
                            ['scala.Int', 'java.lang.String']);
                    };
                    client.__class__ = new s2js.Class('client', []);
                """
            }
    }

    it("parameters of collection types are supported") {
        configMap =>
            scalaCode {
                """
                    package server {
                        @remote object o {
                            def foo(bar: List[Int], baz: List[String], bat: List[Double]): Int = {
                                bar.length + baz.length + bat.length
                            }
                        }
                    }

                    object client {
                        def main() {
                            val fooValue = server.o.foo(List(1, 2, 3), List("aaa", "bbb", "ccc"), List(1.1, 2.2, 3.0))
                        }
                    }
                """
            } shouldCompileTo {
                """
                    goog.provide('client');
                    goog.provide('server.o');
                    goog.require('s2js.RPCWrapper');
                    goog.require('scala.collection.immutable.List');

                    client.main = function() {
                        var self = this;
                        var fooValue = s2js.RPCWrapper.callSync('server.o.foo',
                            [scala.collection.immutable.List.$apply(1, 2, 3),
                            scala.collection.immutable.List.$apply('aaa', 'bbb', 'ccc'),
                            scala.collection.immutable.List.$apply(1.1, 2.2, 3.0)],
                            ['scala.collection.immutable.List[scala.Int]',
                            'scala.collection.immutable.List[java.lang.String]',
                            'scala.collection.immutable.List[scala.Double]']);
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
                        s2js.RPCWrapper.callAsync('server.o.foo', ['xyz'], ['java.lang.String'],
                            function(i) { x = i; }, function(e) { x = -1; });
                    };
                    client.__class__ = new s2js.Class('client', []);
                """
            }
    }
}
