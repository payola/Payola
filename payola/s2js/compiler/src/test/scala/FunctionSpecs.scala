package s2js.compiler


class FunctionSpecs extends CompilerFixtureSpec
{
    describe("Functions") {
        it("can be higher-orderd") {
            configMap =>
                scalaCode {
                    """
                        import s2js.adapters.js.browser._

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
                        goog.provide('F');
                        goog.provide('o');

                        F = function() {
                            var self = this;
                            self.v1 = 'v1';
                        };
                        F.prototype.f1 = function(x) {
                            var self = this;
                            return (self.v1 + x.toUpperCase());
                        };
                        F.prototype.metaClass_ = new s2js.MetaClass('F', []);

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
                        o.metaClass_ = new s2js.MetaClass('o', []);
                    """
                }
        }

        it("anonymous functions can be assigned to variables") {
            configMap =>
                scalaCode {
                    """
                        import s2js.adapters.js.browser._

                        object a {
                            val x = (y: String) => { window.alert(y) }
                        }
                    """
                } shouldCompileTo {
                    """
                        goog.provide('a');

                        a.x = function(y) { window.alert(y); };
                        a.metaClass_ = new s2js.MetaClass('a', []);
                    """
                }
        }
    }
}
