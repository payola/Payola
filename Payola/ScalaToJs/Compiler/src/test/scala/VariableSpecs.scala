package s2js


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

                        /** @constructor*/
                        foo.A = function() {var self = this;};

                        foo.A.prototype.m1 = function() {var self = this;
                            var a = 'foo';
                            var b = 1;
                            var c = true;
                            var d = 1.0;
                        };
                    """
                }
        }

        it("can have instantiations") {
            configMap =>
                expect {
                    """
                        package foo {
                            class B

                            class A {
                                def m1() {
                                    val a = new B
                                }
                            }
                        }
                    """
                } toBe {
                    """
                        goog.provide('foo.B');
                        goog.provide('foo.A');

                        /** @constructor*/
                        foo.B = function() {var self = this;};

                        /** @constructor*/
                        foo.A = function() {var self = this;};
                        foo.A.prototype.m1 = function() {
                            var self = this;
                            var a = new foo.B();
                        };
                    """
                }
        }

        it("can be identifiers") {
            configMap =>
                expect {
                    """
                        package foo {
                            class A {
                                def m1(y:String) {
                                    val a = y
                                }
                            }
                        }
                    """
                } toBe {
                    """
                        goog.provide('foo.A');

                        /** @constructor*/
                        foo.A = function() {var self = this;};
                        foo.A.prototype.m1 = function(y) {
                            var self = this;
                            var a = y;
                        };
                    """
                }
        }

        it("can be the return of a function call") {
            configMap =>
                expect {
                    """
                        package foo {
                            class A {
                                def m1() {

                                }
                                def m2() {
                                    var a = m1();
                                }
                            }
                        }
                    """
                } toBe {
                    """
                        goog.provide('foo.A');

                        /** @constructor*/
                        foo.A = function() {var self = this;};
                        foo.A.prototype.m1 = function() {var self = this;};
                        foo.A.prototype.m2 = function() {var self = this;
                            var a = self.m1();
                        };
                    """
                }
        }

        it("can be an expression") {
            configMap =>
                expect {
                    """
                        package foo {
                            class A {
                                def m1(x:Int) {
                                    var a = x + 5
                                    var b = x == 5
                                }
                            }
                        }
                    """
                } toBe {
                    """
                        goog.provide('foo.A');

                        /** @constructor*/
                        foo.A = function() {var self = this;};
                        foo.A.prototype.m1 = function(x) {
                            var self = this;
                            var a = (x + 5);
                            var b = (x == 5);
                        };
                    """
                }
        }

        it("can be function literals") {
            configMap =>
                expect {
                    """
                        package foo {
                            class A {
                                def m1() {
                                    val a = (b:String) => { println("foo") }
                                }
                            }
                        }
                    """
                } toBe {
                    """
                        goog.provide('foo.A');

                        /** @constructor*/
                        foo.A = function() {var self = this;};
                        foo.A.prototype.m1 = function() {
                            var self = this;
                            var a = function(b) {console.log('foo');};
                        };
                    """
                }
        }
    }
}

