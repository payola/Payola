package s2js.compiler


class VariableSpecs extends CompilerFixtureSpec
{
    describe("Variables") {
        it("can have literal values") {
            configMap =>
                expect {
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
                } toBe {
                    """
                        goog.provide('foo.A');

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
                    """
                }
        }

        it("can have instance values") {
            configMap =>
                expect {
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
                } toBe {
                    """
                        goog.provide('foo.A');
                        goog.provide('foo.B');

                        foo.A = function() {
                            var self = this;
                        };
                        foo.A.prototype.m1 = function() {
                            var self = this;
                            var a = new foo.B();
                        };
                        
                        foo.B = function() {
                            var self = this;
                        };
                    """
                }
        }

        it("can have parameter values") {
            configMap =>
                expect {
                    """
                        package foo {
                            class A {
                                def m1(y: String) {
                                    val a = y
                                }
                            }
                        }
                    """
                } toBe {
                    """
                        goog.provide('foo.A');

                        foo.A = function() {
                            var self = this;
                        };
                        foo.A.prototype.m1 = function(y) {
                            var self = this;
                            var a = y;
                        };
                    """
                }
        }

        it("can have function return values") {
            configMap =>
                expect {
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
                } toBe {
                    """
                        goog.provide('foo.A');

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
                    """
                }
        }

        it("can have expression values") {
            configMap =>
                expect {
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
                } toBe {
                    """
                        goog.provide('foo.A');

                        foo.A = function() {
                            var self = this;
                        };
                        foo.A.prototype.m1 = function(x) {
                            var self = this;
                            var a = (x + 5);
                            var b = (x == 5);
                            var c = ((9 * a) / (2 + a));
                        };
                    """
                }
        }

        it("can have function values") {
            configMap =>
                expect {
                    """
                        package foo {
                            class A {
                                def m1() {
                                    val a = (b: String) => { "foo" + b }
                                }
                            }
                        }
                    """
                } toBe {
                    """
                        goog.provide('foo.A');

                        foo.A = function() {
                            var self = this;
                        };
                        foo.A.prototype.m1 = function() {
                            var self = this;
                            var a = function(b) { return ('foo' + b); };
                        };
                    """
                }
        }
    }
}

