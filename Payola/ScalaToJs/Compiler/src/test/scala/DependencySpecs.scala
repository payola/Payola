package s2js


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

                        a.b.c.d = {};
                        a.b.c.d.m1 = function() {
                            var self = this;
                        };
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

                        foo.a = {};
                        foo.a.x = new java.util.Date();

                        foo.a.m1 = function() {var self = this;
                            var y = new java.util.Random();
                            var z = new java.util.ArrayList();
                        };
                    """
                }
        }

        it("add require for a package object's owner") {
            configMap =>
                expect {
                    """
                        object a {
                            val x = goog.dom.getElement("foo")
                        }
                    """
                } toBe {
                    """
                        goog.provide('a');
                        goog.require('goog.dom');

                        a = {};
                        a.x = goog.dom.getElement('foo');
                    """
                }
        }

        it("ignore implicit browser imports") {
            configMap =>
                expect {
                    """
                        import js.browser._

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

                        o1 = {};
                        o1.f1 = 'aaaa';
                        o1.m1 = function() {
                            var self = this;
                            window.alert(self.f1);
                        };
                    """
                }
        }

        it("ignore explicit browser imports") {
            configMap =>
                expect {
                    """
                        import js.browser._

                        object o1 {
                            val f1 = window.location
                        }
                    """
                } toBe {
                    """
                        goog.provide('o1');

                        o1 = {};
                        o1.f1 = window.location;
                    """
                }
        }

        it("add require for used object members") {
            configMap =>
                expect {
                    """
                        import goog.dom.TagName._

                        object a {
                            val x = SPAN
                        }

                    """
                } toBe {
                    """
                        goog.provide('a');

                        goog.require('goog.dom.TagName');

                        a = {};
                        a.x = goog.dom.TagName.SPAN;
                    """
                }
        }
    }
}
