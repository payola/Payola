package s2js.compiler

class FunctionSpecs extends CompilerFixtureSpec
{
    describe("Functions") {
        it("can be higher-ordered") {
            configMap =>
                scalaCode {
                    """
                        import s2js.adapters.browser._

                        class F {
                            val v1 = "v1"
                            def f1(x: String) = v1 + x.toUpperCase
                        }

                        object o {
                            def f2(f: (String) => String) {
                                window.alert(f("m1"))
                            }

                            def f3(x: String) = "what" + x

                            def start() {
                                val x = new F
                                f2(x.f1)
                                f2(f3)
                                f2 { (x: String) => "no" + x }
                            }
                        }
                    """
                } shouldCompileTo {
                    """
                        s2js.runtime.client.core.get().classLoader.provide('F');
                        s2js.runtime.client.core.get().classLoader.provide('o');

                        F = function() { var self = this; self.v1 = 'v1'; };
                        F.prototype.f1 = function(x) { var self = this; return (self.v1 + x.toUpperCase()); };
                        F.prototype.__class__ = new s2js.runtime.client.core.Class('F', []);

                        s2js.runtime.client.core.get().mixIn(o, new s2js.runtime.client.core.Lazy(function() {
                            var obj = {};
                            obj.f2 = function(f) { var self = this; window.alert(f('m1')); };
                            obj.f3 = function(x) { var self = this; return ('what' + x); };
                            obj.start = function() {
                                var self = this;
                                var x = new F();
                                o.get().f2(function($x) { return x.f1($x); });
                                o.get().f2(function($x) { return o.get().f3($x); });
                                o.get().f2(function(x) { return ('no' + x); });
                            };
                            obj.__class__ = new s2js.runtime.client.core.Class('o', []);
                            return obj;
                        }), true);
                    """
                }
        }

        it("anonymous functions can be assigned to variables") {
            configMap =>
                scalaCode {
                    """
                        import s2js.adapters.browser._

                        object a {
                            val x = (y: String) => { window.alert(y) }
                        }
                    """
                } shouldCompileTo {
                    """
                        s2js.runtime.client.core.get().classLoader.provide('a');
                        s2js.runtime.client.core.get().mixIn(a, new s2js.runtime.client.core.Lazy(function() {
                            var obj = {};
                            obj.x = function(y) { window.alert(y); };
                            obj.__class__ = new s2js.runtime.client.core.Class('a', []);
                            return obj;
                        }), true);
                    """
                }
        }
    }
}
