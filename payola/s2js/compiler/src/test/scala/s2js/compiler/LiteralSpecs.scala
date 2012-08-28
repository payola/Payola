package s2js.compiler

class LiteralSpecs extends CompilerFixtureSpec
{
    describe("Literals") {
        it("null is supported") {
            configMap =>
                scalaCode {
                    """
                        package p
                        object `package`  {
                            def a() {
                                null
                            }
                        }
                    """
                } shouldCompileTo {
                    """
                        s2js.runtime.client.core.get().classLoader.provide('p');
                        s2js.runtime.client.core.get().mixIn(p, new s2js.runtime.client.core.Lazy(function() {
                            var obj = {};
                            obj.a = function() { var self = this; null; };
                            obj.__class__ = new s2js.runtime.client.core.Class('p', []);
                            return obj;
                        }), true);
                    """
                }
        }

        it("booleans are supported") {
            configMap =>
                scalaCode {
                    """
                        package p
                        object `package` {
                            def a() {
                                true
                                false
                            }
                        }
                    """
                } shouldCompileTo {
                    """
                        s2js.runtime.client.core.get().classLoader.provide('p');
                        s2js.runtime.client.core.get().mixIn(p, new s2js.runtime.client.core.Lazy(function() {
                            var obj = {};
                            obj.a = function() { var self = this; true; false; };
                            obj.__class__ = new s2js.runtime.client.core.Class('p', []);
                            return obj;
                        }), true);
                    """
                }
        }

        it("numbers are supported") {
            configMap =>
                scalaCode {
                    """
                        package p
                        object `package` {
                            def a() {
                                1234
                                574.432
                                0
                                -5
                                -424.45
                            }
                        }
                    """
                } shouldCompileTo {
                    """
                        s2js.runtime.client.core.get().classLoader.provide('p');
                        s2js.runtime.client.core.get().mixIn(p, new s2js.runtime.client.core.Lazy(function() {
                            var obj = {};
                            obj.a = function() { var self = this; 1234; 574.432; 0; -5; -424.45; };
                            obj.__class__ = new s2js.runtime.client.core.Class('p', []);
                            return obj;
                        }), true);
                    """
                }
        }

        it("chars are supported") {
            configMap =>
                scalaCode {
                    """
                    object a {
                        val a = 'a'
                        val b = 'b'
                        val c = '\b'
                        val d = '\f'
                        val e = '\n'
                        val f = '\r'
                        val g = '\t'
                        val h = '\''
                        val i = '\"'
                        val j = '\\'
                    }
                    """
                } shouldExactlyCompileTo {
                    """s2js.runtime.client.core.get().classLoader.provide('a');""" + "\n" +
                    """s2js.runtime.client.core.get().mixIn(a, new s2js.runtime.client.core.Lazy(function() {""" + "\n" +
                    """var obj = {};""" + "\n" +
                    """obj.a = 'a';""" + "\n" +
                    """obj.b = 'b';""" + "\n" +
                    """obj.c = '\b';""" + "\n" +
                    """obj.d = '\f';""" + "\n" +
                    """obj.e = '\n';""" + "\n" +
                    """obj.f = '\r';""" + "\n" +
                    """obj.g = '\t';""" + "\n" +
                    """obj.h = '\'';""" + "\n" +
                    """obj.i = '\"';""" + "\n" +
                    """obj.j = '\\';""" + "\n" +
                    """obj.__class__ = new s2js.runtime.client.core.Class('a', []);""" + "\n" +
                    """return obj;""" + "\n" +
                    """}), true);""" + "\n"
                }
        }

        it("strings are supported") {
            configMap =>
                scalaCode {
                    """
                        object a {
                            val a = "asdfghjkl"
                            val b = "12345"
                            val c = ""
                            val d = "\b"
                            val f = "\f"
                            val g = "\n"
                            val h = "\r"
                            val i = "\t"
                            val j = "\'"
                            val k = "\""
                            val l = "\\"
                            val m = """ + "\"\"\"" + """multiline""" + "\n" +
                                """string""" + "\n" +
                            "\"\"\"" + """
                        }
                    """
                } shouldExactlyCompileTo {
                    """s2js.runtime.client.core.get().classLoader.provide('a');""" + "\n" +
                    """s2js.runtime.client.core.get().mixIn(a, new s2js.runtime.client.core.Lazy(function() {""" + "\n" +
                    """var obj = {};""" + "\n" +
                    """obj.a = 'asdfghjkl';""" + "\n" +
                    """obj.b = '12345';""" + "\n" +
                    """obj.c = '';""" + "\n" +
                    """obj.d = '\b';""" + "\n" +
                    """obj.f = '\f';""" + "\n" +
                    """obj.g = '\n';""" + "\n" +
                    """obj.h = '\r';""" + "\n" +
                    """obj.i = '\t';""" + "\n" +
                    """obj.j = '\'';""" + "\n" +
                    """obj.k = '\"';""" + "\n" +
                    """obj.l = '\\';""" + "\n" +
                    """obj.m = 'multiline\nstring\n';""" + "\n" +
                    """obj.__class__ = new s2js.runtime.client.core.Class('a', []);""" + "\n" +
                    """return obj;""" + "\n" +
                    """}), true);""" + "\n" +
                    """"""
                }
        }

        it("classOf is supported") {
            configMap =>
                scalaCode {
                    """
                        package p

                        class A

                        object a {
                            val a = classOf[A]
                        }
                    """
                } shouldCompileTo  {
                    """
                        s2js.runtime.client.core.get().classLoader.provide('p.A');
                        s2js.runtime.client.core.get().classLoader.provide('p.a');

                        p.A = function() { var self = this; };
                        p.A.prototype.__class__ = new s2js.runtime.client.core.Class('p.A', []);

                        s2js.runtime.client.core.get().mixIn(p.a, new s2js.runtime.client.core.Lazy(function() {
                            var obj = {};
                            obj.a = p.A.prototype.__class__;
                            obj.__class__ = new s2js.runtime.client.core.Class('p.a', []);
                            return obj;
                        }), true);
                    """
                }
        }
    }
}
