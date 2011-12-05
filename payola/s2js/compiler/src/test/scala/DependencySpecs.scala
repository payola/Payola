package s2js.compiler


class DependencySpecs extends CompilerFixtureSpec
{
    describe("Requires") {
        it("avoid requiring deep packages") {
            configMap =>
                expect {
                    """
                        package a.b.c {
                            object d {
                                def m1() {}
                            }
                        }
                    """
                } toBe {
                    """
                        goog.provide('a.b.c.d');

                        if (typeof(a.b.c.d) === 'undefined') { a.b.c.d = {}; }
                        a.b.c.d.m1 = function() {
                            var self = this;
                        };
                        a.b.c.d.metaClass_ = new s2js.MetaClass('a.b.c.d', []);
                    """
                }
        }

        it("add require for used classes") {
            configMap =>
                expect {
                    """
                        package foo {
                            import java.util.ArrayList
                            import java.util.Calendar

                            object a {
                                val x = new java.util.Date
                                def m1() {
                                    val y = new java.util.Random
                                    val z = new ArrayList[String]
                                }
                            }
                        }
                    """
                } toBe {
                    """
                        goog.provide('foo.a');

                        goog.require('java.util.ArrayList');
                        goog.require('java.util.Date');
                        goog.require('java.util.Random');

                        if (typeof(foo.a) === 'undefined') { foo.a = {}; }
                        foo.a.x = new java.util.Date();

                        foo.a.m1 = function() {var self = this;
                            var y = new java.util.Random();
                            var z = new java.util.ArrayList();
                        };
                        foo.a.metaClass_ = new s2js.MetaClass('foo.a', []);
                    """
                }
        }

        it("ignore implicit browser imports") {
            configMap =>
                expect {
                    """
                        import s2js.adapters.js.browser._

                        object o1 {
                            val f1 = "aaaa"
                            def m1() {
                                window.alert(f1)
                            }
                        }
                    """
                } toBe {
                    """
                        goog.provide('o1');

                        if (typeof(o1) === 'undefined') { o1 = {}; }
                        o1.f1 = 'aaaa';
                        o1.m1 = function() {
                            var self = this;
                            window.alert(self.f1);
                        };
                        o1.metaClass_ = new s2js.MetaClass('o1', []);
                    """
                }
        }

        it("ignore explicit browser imports") {
            configMap =>
                expect {
                    """
                        import s2js.adapters.js.browser._

                        object o1 {
                            val f1 = window.location
                        }
                    """
                } toBe {
                    """
                        goog.provide('o1');

                        if (typeof(o1) === 'undefined') { o1 = {}; }
                        o1.f1 = window.location;
                        o1.metaClass_ = new s2js.MetaClass('o1', []);
                    """
                }
        }

        it("add require for used object members") {
            configMap =>
                expect {
                    """
                        import s2js.adapters.goog.events.EventType._

                        object a {
                            val x = CLICK
                        }

                    """
                } toBe {
                    """
                        goog.provide('a');

                        goog.require('goog.events.EventType');

                        if (typeof(a) === 'undefined') { a = {}; }
                        a.x = goog.events.EventType.CLICK;
                        a.metaClass_ = new s2js.MetaClass('a', []);
                    """
                }
        }
    }
}
