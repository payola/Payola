package s2js.compiler

class StatementSpecs extends CompilerFixtureSpec
{
    describe("Statements") {
        it("are terminated by semicolons") {
            configMap =>
                scalaCode {
                    """
                        import s2js.adapters.browser._

                        object a {
                            def m1() = {
                                val x = "bar"
                                val y = x + "foo"
                                window.alert(y)
                            }
                        }
                    """
                } shouldCompileTo {
                    """
                        s2js.runtime.client.core.get().classLoader.provide('a');
                        s2js.runtime.client.core.get().mixIn(a, new s2js.runtime.client.core.Lazy(function() {
                            var obj = {};
                            obj.m1 = function() {
                                var self = this;
                                var x = 'bar';
                                var y = (x + 'foo');
                                window.alert(y);
                            };
                            obj.__class__ = new s2js.runtime.client.core.Class('a', []);
                            return obj;
                        }), true);
                    """
                }
        }

        it("assignments are supported") {
            configMap =>
                scalaCode {
                    """
                        object a {
                            var x = "bar"
                        }

                        class B {
                            var x = ""
                        }

                        object c {
                            var x = "foo"
                            def m1(param: String) {
                                val b = new B
                                var local = "bar"

                                a.x = "fooA"
                                b.x = "fooB"
                                x = "fooC"
                                local = "fooLocal"

                                local = param
                                local = x
                                local = a.x
                                local = b.x

                                a.x = param
                                a.x = local
                                a.x = x
                                a.x = b.x

                                b.x = param
                                b.x = local
                                b.x = x
                                b.x = a.x

                                x = param
                                x = local
                                x = a.x
                                x = b.x
                            }
                        }
                    """
                } shouldCompileTo {
                    """
                        s2js.runtime.client.core.get().classLoader.provide('B');
                        s2js.runtime.client.core.get().classLoader.provide('a');
                        s2js.runtime.client.core.get().classLoader.provide('c');

                        B = function() { var self = this; self.x = ''; };
                        B.prototype.__class__ = new s2js.runtime.client.core.Class('B', []);

                        s2js.runtime.client.core.get().mixIn(a, new s2js.runtime.client.core.Lazy(function() {
                            var obj = {};
                            obj.x = 'bar';
                            obj.__class__ = new s2js.runtime.client.core.Class('a', []);
                            return obj;
                        }), true);

                        s2js.runtime.client.core.get().mixIn(c, new s2js.runtime.client.core.Lazy(function() {
                            var obj = {};
                            obj.x = 'foo';
                            obj.m1 = function(param) {
                                var self = this;
                                var b = new B();
                                var local = 'bar';
                                a.get().x = 'fooA';
                                b.x = 'fooB';
                                c.get().x = 'fooC';
                                local = 'fooLocal';
                                local = param;
                                local = c.get().x;
                                local = a.get().x;
                                local = b.x;
                                a.get().x = param;
                                a.get().x = local;
                                a.get().x = c.get().x;
                                a.get().x = b.x;
                                b.x = param;
                                b.x = local;
                                b.x = c.get().x;
                                b.x = a.get().x;
                                c.get().x = param;
                                c.get().x = local;
                                c.get().x = a.get().x;
                                c.get().x = b.x;
                            };
                            obj.__class__ = new s2js.runtime.client.core.Class('c', []);
                            return obj;
                        }), true);
                    """
                }
        }

        it("not operator is supported") {
            configMap =>
                scalaCode {
                    """
                        object o {
                            def m1() = true
                            def m2() = {
                                val v1 = true
                                val v2 = !v1
                                val v3 = !m1
                            }
                        }
                    """
                } shouldCompileTo {
                    """
                        s2js.runtime.client.core.get().classLoader.provide('o');
                        s2js.runtime.client.core.get().mixIn(o, new s2js.runtime.client.core.Lazy(function() {
                            var obj = {};
                            obj.m1 = function() { var self = this; return true; };
                            obj.m2 = function() {
                                var self = this;
                                var v1 = true;
                                var v2 = (! v1);
                                var v3 = (! o.get().m1());
                            };
                            obj.__class__ = new s2js.runtime.client.core.Class('o', []);
                            return obj;
                        }), true);
                    """
                }
        }
    }
}
