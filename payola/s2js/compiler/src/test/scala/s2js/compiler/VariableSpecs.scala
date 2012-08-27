package s2js.compiler

class VariableSpecs extends CompilerFixtureSpec
{
    describe("Variables") {
        it("can have literal values") {
            configMap =>
                scalaCode {
                    """
                        package foo

                        class A {
                            def m1() {
                                val a = "foo"
                                val b = 1
                                val c = true
                                val d = 1.0
                            }
                        }
                    """
                } shouldCompileTo {
                    """
                        s2js.runtime.client.core.get().classLoader.provide('foo.A');

                        foo.A = function() {
                            var self = this;
                        };

                        foo.A.prototype.m1 = function() {
                            var self = this;
                            var a = 'foo';
                            var b = 1;
                            var c = true;
                            var d = 1.0;
                        };
                        foo.A.prototype.__class__ = new s2js.runtime.client.core.Class('foo.A', []);
                    """
                }
        }

        it("can have instance values") {
            configMap =>
                scalaCode {
                    """
                        package foo {
                            class A {
                                def m1() {
                                    val a = new B
                                }
                            }
                            
                            class B
                        }
                    """
                } shouldCompileTo {
                    """
                        s2js.runtime.client.core.get().classLoader.provide('foo.A');
                        s2js.runtime.client.core.get().classLoader.provide('foo.B');

                        foo.A = function() {
                            var self = this;
                        };
                        foo.A.prototype.m1 = function() {
                            var self = this;
                            var a = new foo.B();
                        };
                        foo.A.prototype.__class__ = new s2js.runtime.client.core.Class('foo.A', []);
                        
                        foo.B = function() {
                            var self = this;
                        };
                        foo.B.prototype.__class__ = new s2js.runtime.client.core.Class('foo.B', []);
                    """
                }
        }

        it("can have parameter values") {
            configMap =>
                scalaCode {
                    """
                        package foo {
                            class A {
                                def m1(y: String) {
                                    val a = y
                                }
                            }
                        }
                    """
                } shouldCompileTo {
                    """
                        s2js.runtime.client.core.get().classLoader.provide('foo.A');

                        foo.A = function() {
                            var self = this;
                        };
                        foo.A.prototype.m1 = function(y) {
                            var self = this;
                            var a = y;
                        };
                        foo.A.prototype.__class__ = new s2js.runtime.client.core.Class('foo.A', []);
                    """
                }
        }

        it("can have function return values") {
            configMap =>
                scalaCode {
                    """
                        package foo {
                            class A {
                                def m1() = "foo"
                                def m2() {
                                    var a = m1();
                                }
                            }
                        }
                    """
                } shouldCompileTo {
                    """
                        s2js.runtime.client.core.get().classLoader.provide('foo.A');

                        foo.A = function() {
                            var self = this;
                        };
                        foo.A.prototype.m1 = function() {
                            var self = this;
                            return 'foo';
                        };
                        foo.A.prototype.m2 = function() {
                            var self = this;
                            var a = self.m1();
                        };
                        foo.A.prototype.__class__ = new s2js.runtime.client.core.Class('foo.A', []);
                    """
                }
        }

        it("can have expression values") {
            configMap =>
                scalaCode {
                    """
                        package foo {
                            class A {
                                def m1(x: Int) {
                                    var a = x + 5
                                    var b = x == 5
                                    var c = ((9 * a) / (2 + a))
                                }
                            }
                        }
                    """
                } shouldCompileTo {
                    """
                        s2js.runtime.client.core.get().classLoader.provide('foo.A');

                        foo.A = function() {
                            var self = this;
                        };
                        foo.A.prototype.m1 = function(x) {
                            var self = this;
                            var a = (x + 5);
                            var b = (x == 5);
                            var c = ((9 * a) / (2 + a));
                        };
                        foo.A.prototype.__class__ = new s2js.runtime.client.core.Class('foo.A', []);
                    """
                }
        }

        it("can have function values") {
            configMap =>
                scalaCode {
                    """
                        package foo {
                            class A {
                                def m1() {
                                    val a = (b: String) => { "foo" + b }
                                }
                            }
                        }
                    """
                } shouldCompileTo {
                    """
                        s2js.runtime.client.core.get().classLoader.provide('foo.A');

                        foo.A = function() {
                            var self = this;
                        };
                        foo.A.prototype.m1 = function() {
                            var self = this;
                            var a = function(b) { return ('foo' + b); };
                        };
                        foo.A.prototype.__class__ = new s2js.runtime.client.core.Class('foo.A', []);
                    """
                }
        }
    }
}

