package s2js.compiler

class DependencySpecs extends CompilerFixtureSpec
{
    describe("Requires") {
        ignore("avoid requiring deep packages") {
            configMap =>
                scalaCode {
                    """
                        package a.b.c {
                            object d {
                                def m1() {}
                            }
                        }
                    """
                } shouldCompileTo {
                    """
                        s2js.runtime.client.ClassLoader.provide('a.b.c.d');

                        a.b.c.d.m1 = function() {
                            var self = this;
                        };
                        a.b.c.d.__class__ = new s2js.runtime.client.Class('a.b.c.d', []);
                    """
                }
        }

        ignore("add require for used classes") {
            configMap =>
                scalaCode {
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
                } shouldCompileTo {
                    """
                        s2js.runtime.client.ClassLoader.provide('foo.a');

                        s2js.runtime.client.ClassLoader.require('java.util.ArrayList');
                        s2js.runtime.client.ClassLoader.require('java.util.Date');
                        s2js.runtime.client.ClassLoader.require('java.util.Random');

                        foo.a.x = new java.util.Date();

                        foo.a.m1 = function() {
                            var self = this;
                            var y = new java.util.Random();
                            var z = new java.util.ArrayList();
                        };
                        foo.a.__class__ = new s2js.runtime.client.Class('foo.a', []);
                    """
                }
        }

        ignore("ignore implicit browser imports") {
            configMap =>
                scalaCode {
                    """
                        import s2js.adapters.browser._

                        object o1 {
                            val f1 = "aaaa"
                            def m1() {
                                window.alert(f1)
                            }
                        }
                    """
                } shouldCompileTo {
                    """
                        s2js.runtime.client.ClassLoader.provide('o1');

                        o1.f1 = 'aaaa';
                        o1.m1 = function() {
                            var self = this;
                            window.alert(self.f1);
                        };
                        o1.__class__ = new s2js.runtime.client.Class('o1', []);
                    """
                }
        }

        ignore("ignore explicit browser imports") {
            configMap =>
                scalaCode {
                    """
                        import s2js.adapters.browser._

                        object o1 {
                            val f1 = window.location
                        }
                    """
                } shouldCompileTo {
                    """
                        s2js.runtime.client.ClassLoader.provide('o1');

                        o1.f1 = window.location;
                        o1.__class__ = new s2js.runtime.client.Class('o1', []);
                    """
                }
        }
    }
}
