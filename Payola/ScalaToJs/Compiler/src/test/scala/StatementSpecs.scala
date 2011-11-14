package s2js


class StatementSpecs extends CompilerFixtureSpec
{

    it("support not operator") {
        configMap =>

            expect {
                """

                object o {
                    def m1() = true
                    def m2() = {
                        val v1 = true
                        val v2 = !v1
                        if(!m1) {
                            println("not")
                        }
                    }
                }

                """
            } toBe {
                """

                goog.provide('o');
                o.m1 = function() {
                    var self = this;
                    return true;
                };
                o.m2 = function() {
                    var self = this;
                    var v1 = true;
                    var v2 = !v1;
                    !o.m1() ? function() {console.log('not');}() : function() {;}();
                };
                """
            }
    }

    it("ignore asinstanceof calls") {
        configMap =>
            expect {
                """
                    object a {
                        def m1(x:String) = {
                            val y = x.asInstanceOf[String]
                        }
                    }
                """
            } toBe {
                """
                    goog.provide('a');
                    a.m1 = function(x) {
                        var self = this;
                        var y = x;
                    };
                """
            }
    }

    describe("statements") {

        it("should have a semicolons") {
            configMap =>

                expect {
                    """
                        object a {
                            def m1() = {
                                val x = "bar"
                                val y = x + "foo"
                                println(y)
                            }
                        }
                    """
                } toBe {
                    """
                        goog.provide('a');
                        a.m1 = function() {
                            var self = this;
                            var x = 'bar';
                            var y = (x + 'foo');
                            console.log(y);
                        };
                    """
                }
        }

        it("can access class and module variables") {
            configMap =>

                expect {
                    """
                        class a {
                            var x = ""

                            def m1() {
                                x = "foo"
                            }
                        }
                        object b {
                            var x = ""
                            def m1() {
                                x = "foo"
                            }
                        }
                    """
                } toBe {
                    """
                        goog.provide('a');
                        goog.provide('b');
                        /** @constructor*/
                        a = function() {var self = this;
                            self.x = '';
                        };
                        a.prototype.m1 = function() {
                            var self = this;
                            self.x = 'foo';
                        };
                        b.x = '';
                        b.m1 = function() {
                            var self = this;
                            b.x = 'foo';
                        };
                    """
                }
        }
    }

    describe("assignments") {

        it("calling a method of a returned object") {
            configMap =>

                expect {
                    """

                        class A {
                            var x = ""
                        }

                        object b {
                            def m1() {
                                val a = new A
                                a.x = "foo"
                            }
                        }

                    """
                } toBe {
                    """

                        goog.provide('A');
                        goog.provide('b');
                        /** @constructor*/
                        A = function() {var self = this;
                            self.x = '';
                        };
                        b.m1 = function() {
                            var self = this;
                            var a = new A();
                            a.x = 'foo';
                        };

                    """
                }
        }
    }

    describe("function calls") {

        it("can handle function reference passing") {
            configMap =>

                expect {
                    """

                    class C1 {
                        val f1 = "c1"
                        def m1(fn:(String)=>Unit) {
                            println(f1)
                            fn(f1)
                        }
                    }

                    class C2 {
                        val f1 = "c2"
                        def m1(v1:String) {
                            println(v1+f1)
                        }
                    }

                    object o1 {
                        def m1() {
                            val c1 = new C1
                            val c2 = new C2
                            c1.m1(c2.m1)
                        }
                    }

                    """
                } toBe {
                    """

                    goog.provide('C1');
                    goog.provide('C2');
                    goog.provide('o1');

                    /** @constructor*/
                    C1 = function() {var self = this;
                        self.f1 = 'c1';
                    };

                    C1.prototype.m1 = function(fn) {var self = this;
                        console.log(self.f1);
                        fn(self.f1);
                    };

                    /** @constructor*/
                    C2 = function() {var self = this;
                        self.f1 = 'c2';
                    };

                    C2.prototype.m1 = function(v1) {var self = this;
                        console.log((v1 + self.f1));
                    };

                    o1.m1 = function() {
                        var self = this;
                        var c1 = new C1();
                        var c2 = new C2();
                        c1.m1(function(_v1_) {return c2.m1(_v1_)});
                    };

                    """
                }
        }

        it("can handle default arguments") {
            configMap =>

                expect {
                    """

                    object o1 {
                        def m1[T](v1:String, v2:String = "") {}
                        def m2(v1:String, v2:String = "") {}
                        def m3(v1:String, v2:String = null) {}
                        def m4() {
                            m1("foo")
                            m2("foo")
                            m1("foo", "bar")
                            m3("foo")
                        }
                    }

                    """
                } toBe {
                    """

                    goog.provide('o1');
                    o1.m1 = function(v1,v2) {
                        var self = this;
                        if (typeof(v2) === 'undefined') { v2 = ''; };
                    };
                    o1.m2 = function(v1,v2) {
                        var self = this;
                        if (typeof(v2) === 'undefined') { v2 = ''; };
                    };
                    o1.m3 = function(v1,v2) {
                        var self = this;
                        if (typeof(v2) === 'undefined') { v2 = null; };
                    };
                    o1.m4 = function() {
                        var self = this;
                        o1.m1('foo');
                        o1.m2('foo');
                        o1.m1('foo','bar');
                        o1.m3('foo');
                    };

                    """
                }
        }


        it("should handle package objects") {
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
                        a.x = goog.dom.getElement('foo');
                    """
                }
        }

        it("can call parameterized functions") {
            configMap =>

                expect {
                    """
                        object a {
                            def m1[T](t:T) {}
                            def m2() {
                                m1("foo")
                            }
                        }
                    """
                } toBe {
                    """
                        goog.provide('a');
                        a.m1 = function(t) {var self = this;};
                        a.m2 = function() {var self = this;
                            a.m1('foo');
                        };
                    """
                }
        }

        it("should use fully qualified function names") {
            configMap =>

                expect {
                    """
                        package foo
                        object a {
                            def m1() {}
                            def m2() {
                                m1()
                            }
                        }
                    """
                } toBe {
                    """
                        goog.provide('foo.a');
                        foo.a.m1 = function() {var self = this;};
                        foo.a.m2 = function() {var self = this;
                            foo.a.m1();
                        };
                    """
                }
        }
    }

    describe("when call a function from same package") {

        it("should be fully qualified") {
            configMap =>

                expect {
                    """
                        package goog
                        object a {
                            val x = css.getCssName("foo")
                        }
                    """
                } toBe {
                    """
                        goog.provide('goog.a');
                        goog.require('goog.css');
                        goog.a.x = goog.css.getCssName('foo');
                    """
                }
        }
    }
}