package s2js.compiler


class PackageSpecs extends CompilerFixtureSpec
{
    describe("Packages") {
        it("can be used") {
            configMap =>
                expect {
                    """
                        package pkg

                        class A
                    """
                } toBe {
                    """
                        goog.provide('pkg.A');

                        pkg.A = function() {
                            var self = this;
                        };
                    """
                }
        }

        it("can be nested using multiple package statements") {
            configMap =>
                expect {
                    """
                        package pkg
                        package sub
                        package nested

                        class A
                    """
                } toBe {
                    """
                        goog.provide('pkg.sub.nested.A');

                        pkg.sub.nested.A = function() {
                            var self = this;
                        };
                    """
                }
        }

        it("can be nested using encapsulation") {
            configMap =>
                expect {
                    """
                        package pkg {
                            package sub {
                                package nested {
                                    class A
                                }
                            }
                        }
                    """
                } toBe {
                    """
                        goog.provide('pkg.sub.nested.A');

                        pkg.sub.nested.A = function() {
                            var self = this;
                        };
                    """
                }
        }

        it("can be nested using package name with '.' separators") {
            configMap =>
                expect {
                    """
                        package pkg.sub.nested {
                            class A
                        }
                    """
                } toBe {
                    """
                        goog.provide('pkg.sub.nested.A');

                        pkg.sub.nested.A = function() {
                            var self = this;
                        };
                    """
                }
        }

        it("can be declared multiple times") {
            configMap =>
                expect {
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
                } toBe {
                    """
                        goog.provide('pkg.A');
                        goog.provide('pkg.B');
                        goog.provide('pkg.sub1.X');
                        goog.provide('pkg.sub2.Y');

                        pkg.A = function() {
                            var self = this;
                        };
                        pkg.B = function() {
                            var self = this;
                        };
                        pkg.sub1.X = function() {
                            var self = this;
                        };
                        pkg.sub2.Y = function() {
                            var self = this;
                        };
                    """
                }
        }
    }
}