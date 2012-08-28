package s2js.compiler

class MiscellaneousSpecs extends CompilerFixtureSpec
{
    it("conversion methods between numeric types are itd") {
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
                    s2js.runtime.client.core.get().classLoader.provide('o');
                    s2js.runtime.client.core.get().mixIn(o, new s2js.runtime.client.core.Lazy(function() {
                        var obj = {};
                        obj.c = 1;
                        obj.x6 = function() {
                            var self = this;
                            var x1 = o.get().c;
                            var x2 = o.get().c;
                            var x3 = o.get().c;
                            var x4 = o.get().c;
                            var x5 = o.get().c;
                            var x6 = o.get().c;
                        };
                        obj.__class__ = new s2js.runtime.client.core.Class('o', []);
                        return obj;
                    }), true);
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
                    s2js.runtime.client.core.get().classLoader.provide('A');
                    s2js.runtime.client.core.get().classLoader.provide('o');

                    A = function() { var self = this; };
                    A.prototype.$plus = function(x) { var self = this; return new A(); };
                    A.prototype.$minus = function(x) { var self = this; return new A(); };
                    A.prototype.$times = function(x) { var self = this; return new A(); };
                    A.prototype.unary_$bang = function() { var self = this; return new A(); };
                    A.prototype.__class__ = new s2js.runtime.client.core.Class('A', []);
                    s2js.runtime.client.core.get().mixIn(o, new s2js.runtime.client.core.Lazy(function() {
                        var obj = {};
                        obj.m = function() {
                            var self = this;
                            var a = new A().$plus(new A()).$minus(new A().$times(new A()));
                            var b = new A().unary_$bang();
                            var c = (a === b);
                            var d = (a !== b);
                            var e = (a == b);
                            var f = (a != b);
                        };
                        obj.__class__ = new s2js.runtime.client.core.Class('o', []);
                       return obj;
                    }), true);
                """
            }
    }

    it("field and method names of adapter objects are preserved") {
        configMap =>
            scalaCode {
                """
                    import s2js.adapters.browser
                    import s2js.adapters.html

                    object o {
                        def foo() {
                            val e = browser.document.createElement[html.Element]("div")
                            val l = e.childNodes.length
                        }
                    }
                """
            } shouldCompileTo {
                """
                    s2js.runtime.client.core.get().classLoader.provide('o');
                    s2js.runtime.client.core.get().mixIn(o, new s2js.runtime.client.core.Lazy(function() {
                        var obj = {};
                        obj.foo = function() {
                            var self = this;
                            var e = document.createElement('div');
                            var l = e.childNodes.length;
                        };
                        obj.__class__ = new s2js.runtime.client.core.Class('o', []);
                        return obj;
                    }), true);
                """
            }
    }
}
