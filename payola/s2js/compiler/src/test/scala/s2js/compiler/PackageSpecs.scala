package s2js.compiler

class PackageSpecs extends CompilerFixtureSpec
{
    describe("Packages") {
        it("can be used") {
            configMap =>
                scalaCode {
                    """
                        package pkg

                        class A
                    """
                } shouldCompileTo {
                    """
                        s2js.ClassLoader.provide('pkg.A');

                        pkg.A = function() {
                            var self = this;
                        };
                        pkg.A.prototype.__class__ = new s2js.Class('pkg.A', []);
                    """
                }
        }

        it("can be nested using multiple package statements") {
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
                        s2js.ClassLoader.provide('pkg.sub.nested.A');

                        pkg.sub.nested.A = function() {
                            var self = this;
                        };
                        pkg.sub.nested.A.prototype.__class__ = new s2js.Class('pkg.sub.nested.A', []);
                    """
                }
        }

        it("can be nested using encapsulation") {
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
                        s2js.ClassLoader.provide('pkg.sub.nested.A');

                        pkg.sub.nested.A = function() {
                            var self = this;
                        };
                        pkg.sub.nested.A.prototype.__class__ = new s2js.Class('pkg.sub.nested.A', []);
                    """
                }
        }

        it("can be nested using package name with '.' separators") {
            configMap =>
                scalaCode {
                    """
                        package pkg.sub.nested {
                            class A
                        }
                    """
                } shouldCompileTo {
                    """
                        s2js.ClassLoader.provide('pkg.sub.nested.A');

                        pkg.sub.nested.A = function() {
                            var self = this;
                        };
                        pkg.sub.nested.A.prototype.__class__ = new s2js.Class('pkg.sub.nested.A', []);
                    """
                }
        }

        it("can be declared multiple times") {
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
                        s2js.ClassLoader.provide('pkg.A');
                        s2js.ClassLoader.provide('pkg.B');
                        s2js.ClassLoader.provide('pkg.sub1.X');
                        s2js.ClassLoader.provide('pkg.sub2.Y');

                        pkg.A = function() {
                            var self = this;
                        };
                        pkg.A.prototype.__class__ = new s2js.Class('pkg.A', []);
                        pkg.B = function() {
                            var self = this;
                        };
                        pkg.B.prototype.__class__ = new s2js.Class('pkg.B', []);
                        pkg.sub1.X = function() {
                            var self = this;
                        };
                        pkg.sub1.X.prototype.__class__ = new s2js.Class('pkg.sub1.X', []);
                        pkg.sub2.Y = function() {
                            var self = this;
                        };
                        pkg.sub2.Y.prototype.__class__ = new s2js.Class('pkg.sub2.Y', []);
                    """
                }
        }
    }
}
