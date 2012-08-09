package s2js.compiler

class PackageSpecs extends CompilerFixtureSpec
{
    describe("Packages") {
        ignore("can be used") {
            configMap =>
                scalaCode {
                    """
                        package pkg

                        class A
                    """
                } shouldCompileTo {
                    """
                        s2js.runtime.client.ClassLoader.provide('pkg.A');

                        pkg.A = function() {
                            var self = this;
                        };
                        pkg.A.prototype.__class__ = new s2js.runtime.client.Class('pkg.A', []);
                    """
                }
        }

        ignore("can be nested using multiple package statements") {
            configMap =>
                scalaCode {
                    """
                        package pkg
                        package sub
                        package nested

                        class A
                    """
                } shouldCompileTo {
                    """
                        s2js.runtime.client.ClassLoader.provide('pkg.sub.nested.A');

                        pkg.sub.nested.A = function() {
                            var self = this;
                        };
                        pkg.sub.nested.A.prototype.__class__ = new s2js.runtime.client.Class('pkg.sub.nested.A', []);
                    """
                }
        }

        ignore("can be nested using encapsulation") {
            configMap =>
                scalaCode {
                    """
                        package pkg {
                            package sub {
                                package nested {
                                    class A
                                }
                            }
                        }
                    """
                } shouldCompileTo {
                    """
                        s2js.runtime.client.ClassLoader.provide('pkg.sub.nested.A');

                        pkg.sub.nested.A = function() {
                            var self = this;
                        };
                        pkg.sub.nested.A.prototype.__class__ = new s2js.runtime.client.Class('pkg.sub.nested.A', []);
                    """
                }
        }

        ignore("can be nested using package name with '.' separators") {
            configMap =>
                scalaCode {
                    """
                        package pkg.sub.nested {
                            class A
                        }
                    """
                } shouldCompileTo {
                    """
                        s2js.runtime.client.ClassLoader.provide('pkg.sub.nested.A');

                        pkg.sub.nested.A = function() {
                            var self = this;
                        };
                        pkg.sub.nested.A.prototype.__class__ = new s2js.runtime.client.Class('pkg.sub.nested.A', []);
                    """
                }
        }

        ignore("can be declared multiple times") {
            configMap =>
                scalaCode {
                    """
                        package pkg.sub1 {
                            class X
                        }
                        package pkg {
                            class A
                        }
                        package pkg {
                            class B
                        }
                        package pkg.sub2 {
                            class Y
                        }
                    """
                } shouldCompileTo {
                    """
                        s2js.runtime.client.ClassLoader.provide('pkg.A');
                        s2js.runtime.client.ClassLoader.provide('pkg.B');
                        s2js.runtime.client.ClassLoader.provide('pkg.sub1.X');
                        s2js.runtime.client.ClassLoader.provide('pkg.sub2.Y');

                        pkg.A = function() {
                            var self = this;
                        };
                        pkg.A.prototype.__class__ = new s2js.runtime.client.Class('pkg.A', []);
                        pkg.B = function() {
                            var self = this;
                        };
                        pkg.B.prototype.__class__ = new s2js.runtime.client.Class('pkg.B', []);
                        pkg.sub1.X = function() {
                            var self = this;
                        };
                        pkg.sub1.X.prototype.__class__ = new s2js.runtime.client.Class('pkg.sub1.X', []);
                        pkg.sub2.Y = function() {
                            var self = this;
                        };
                        pkg.sub2.Y.prototype.__class__ = new s2js.runtime.client.Class('pkg.sub2.Y', []);
                    """
                }
        }

        it("package objects accessed from within the package are properly qualified") {
            configMap =>
                scalaCode {
                    """
                        package a.b.c

                        object `package` {
                            def foo() {}
                        }

                        object bar {
                            def bar() {
                                foo()
                            }
                        }
                    """
                } shouldCompileTo {
                    """
                        s2js.runtime.client.core.classLoader.provide('a.b.c');
                        s2js.runtime.client.core.classLoader.provide('a.b.c.bar');

                        a.b.c.bar.bar = function() {
                            var self = this;
                            a.b.c.foo();
                        };
                        a.b.c.bar.__class__ = new s2js.runtime.client.core.Class('a.b.c.bar', []);

                        a.b.c.foo = function() { var self = this; };
                        a.b.c.__class__ = new s2js.runtime.client.core.Class('a.b.c', []);
                    """
                }
        }
    }
}
