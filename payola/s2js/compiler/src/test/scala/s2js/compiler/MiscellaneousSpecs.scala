package s2js.compiler

class MiscellaneousSpecs extends CompilerFixtureSpec
{
    it("conversion methods between numeric types are ignored") {
        configMap =>
            scalaCode {
                """
                    object o {
                        val c: Byte = 1

                        def x6() {
                            val x1: Double = c
                            val x2: Float = c
                            val x3: Long = c
                            val x4: Int = c
                            val x5: Short = c
                            val x6: Byte = c
                        }
                    }
                """
            } shouldCompileTo {
                """
                    s2js.runtime.client.ClassLoader.provide('o');

                    o.c = 1;
                    o.x6 = function() {
                        var self = this;
                        var x1 = self.c;
                        var x2 = self.c;
                        var x3 = self.c;
                        var x4 = self.c;
                        var x5 = self.c;
                        var x6 = self.c;
                    };
                    o.__class__ = new s2js.runtime.client.Class('o', []);
                """
            }
    }

    it("Custom operators are supported and default operators are not overriden.") {
        configMap =>
            scalaCode {
                """
                        class A {
                            def +(x: A): A = new A
                            def -(x: A): A = new A
                            def *(x: A): A = new A
                            def unary_!(): A = new A
                        }

                        object o {
                            def m() {
                                val a = new A() + new A() - new A() * new A()
                                val b = ! new A()
                                val c = a eq b
                                val d = a ne b
                                val e = a == b
                                val f = a != b
                            }
                        }
                    """
            } shouldCompileTo {
                """
                        s2js.runtime.client.ClassLoader.provide('A');
                        s2js.runtime.client.ClassLoader.provide('o');

                        A = function() {
                            var self = this;
                        };
                        A.prototype.$plus = function(x) {
                            var self = this;
                            return new A();
                        };
                        A.prototype.$minus = function(x) {
                            var self = this;
                            return new A();
                        };
                        A.prototype.$times = function(x) {
                            var self = this;
                            return new A();
                        };
                        A.prototype.unary_$bang = function() {
                            var self = this;
                            return new A();
                        };
                        A.prototype.__class__ = new s2js.runtime.client.Class('A', []);

                        o.m = function() {
                            var self = this;
                            var a = new A().$plus(new A()).$minus(new A().$times(new A()));
                            var b = new A().unary_$bang();
                            var c = (a === b);
                            var d = (a !== b);
                            var e = (a == b);
                            var f = (a != b);
                        };
                        o.__class__ = new s2js.runtime.client.Class('o', []);
                    """
            }
    }

    it("field and method names of adapter objects are preserved") {
        configMap =>
            scalaCode {
                """
                    import s2js.adapters.js

                    object o {
                        def foo() {
                            val e = js.browser.document.createElement[js.dom.Element]("div")
                            val l = e.childNodes.length
                        }
                    }
                """
            } shouldCompileTo {
                """
                    s2js.runtime.client.ClassLoader.provide('o');

                    o.foo = function() {
                        var self = this;
                        var e = document.createElement('div');
                        var l = e.childNodes.length;
                    };
                    o.__class__ = new s2js.runtime.client.Class('o', []);
                """
            }
    }
}
