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

                        F = function() {
                            var self = this;
                            self.v1 = 'v1';
                        };
                        F.prototype.f1 = function(x) {
                            var self = this;
                            return (self.v1 + x.toUpperCase());
                        };
                        F.prototype.__class__ = new s2js.runtime.client.core.Class('F', []);

                        o.f2 = function(f) {
                            var self = this;
                             window.alert(f('m1'));
                        };
                        o.f3 = function(x) {
                            var self = this;
                            return ('what' + x);
                        };
                        o.start = function() {
                            var self = this;
                            var x = new F();
                            self.f2(function($x) { return x.f1($x); });
                            self.f2(function($x) { return self.f3($x); });
                            self.f2(function(x) { return ('no' + x); });
                        };
                        o.__class__ = new s2js.runtime.client.core.Class('o', []);
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

                        a.x = function(y) { window.alert(y); };
                        a.__class__ = new s2js.runtime.client.core.Class('a', []);
                    """
                }
        }
    }
}
