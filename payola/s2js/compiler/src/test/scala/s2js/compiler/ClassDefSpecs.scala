package s2js.compiler

class ClassDefSpecs extends CompilerFixtureSpec
{
    describe("Traits") {
        it("can be declared") {
            configMap =>
                scalaCode {
                    """
                        package pkg

                        trait A {
                            val v1 = "test"
                            def m1() { }
                        }
                    """
                } shouldCompileTo {
                    """
                        s2js.runtime.client.core.get().classLoader.provide('pkg.A');

                        pkg.A = function() {
                            var self = this;
                            self.v1 = 'test';
                        };
                        pkg.A.prototype.m1 = function() {
                            var self = this;
                        };
                        pkg.A.prototype.__class__ = new s2js.runtime.client.core.Class('pkg.A', []);
                    """
                }
        }
    }

    describe("Classes") {
        it("can be declared") {
            configMap =>
                scalaCode {
                    """
                        package pkg

                        class A
                    """
                } shouldCompileTo {
                    """
                        s2js.runtime.client.core.get().classLoader.provide('pkg.A');

                        pkg.A = function() {
                            var self = this;
                        };
                        pkg.A.prototype.__class__ = new s2js.runtime.client.core.Class('pkg.A', []);
                    """
                }
        }

        it("can have getters and setters") {
            configMap =>
                scalaCode {
                    """
                        package pkg

                        class A
                        {
                            protected var _x: String = ""
                            var y = ""

                            def x = _x

                            def x_=(value: String) {
                                _x = value
                            }
                        }

                        object t
                        {
                            def test() {
                                val a = new A
                                val x = a.x
                                val y = a.y
                                a.x = "new x"
                                a.y = "new y"
                            }
                        }
                    """
                } shouldCompileTo {
                    """
                        s2js.runtime.client.core.get().classLoader.provide('pkg.A');
                        s2js.runtime.client.core.get().classLoader.provide('pkg.t');

                        pkg.A = function() {
                            var self = this;
                            self._x = '';
                            self.y = '';
                        };
                        pkg.A.prototype.x = function() {
                            var self = this;
                            return self._x;
                        };
                        pkg.A.prototype.x_$eq = function(value) {
                            var self = this;
                            self._x = value;
                        };
                        pkg.A.prototype.__class__ = new s2js.runtime.client.core.Class('pkg.A', []);

                        s2js.runtime.client.core.get().mixIn(pkg.t, new s2js.runtime.client.core.Lazy(function() {
                            var obj = {};
                            obj.test = function() {
                                var self = this;
                                var a = new pkg.A();
                                var x = a.x();
                                var y = a.y;
                                a.x_$eq('new x');
                                a.y = 'new y';
                            };
                            obj.__class__ = new s2js.runtime.client.core.Class('pkg.t', []); return obj;
                        }), true);
                    """
                }
        }

        it("can have implicit constructor") {
            configMap =>
                scalaCode {
                    """
                        package pkg

                        class A(val v1: Int, val v2: String, val v3: Boolean = true, v4: Double) {
                            val v5 = "test"
                            val v6 = 12345
                            val v7 = v1
                            val v8 = v4
                        }
                    """
                } shouldCompileTo {
                    """
                        s2js.runtime.client.core.get().classLoader.provide('pkg.A');

                        pkg.A = function(v1, v2, v3, v4) {
                            var self = this;
                            if (typeof(v3) === 'undefined') { v3 = true; }
                            self.v1 = v1;
                            self.v2 = v2;
                            self.v3 = v3;
                            self.v4 = v4;
                            self.v5 = 'test';
                            self.v6 = 12345;
                            self.v7 = self.v1;
                            self.v8 = self.v4;
                        };
                        pkg.A.prototype.__class__ = new s2js.runtime.client.core.Class('pkg.A', []);
                    """
                }
        }

        it("constructor can have body") {
            configMap =>
                scalaCode {
                    """
                        package pkg

                        import s2js.adapters.browser._

                        class A(val v1: Int) {
                            window.alert(v1.toString)
                        }
                    """
                } shouldCompileTo {
                    """
                        s2js.runtime.client.core.get().classLoader.provide('pkg.A');

                        pkg.A = function(v1) {
                            var self = this;
                            self.v1 = v1;
                            window.alert(self.v1.toString());
                        };
                        pkg.A.prototype.__class__ = new s2js.runtime.client.core.Class('pkg.A', []);
                    """
                }
        }

        it("can inherit from classes nad traits") {
            configMap =>
                scalaCode {
                    """
                        package pkg

                        class A
                        {
                            val i: String = ""
                        }

                        trait T1 {
                            val v1 = "test1"
                            def m1() { }
                        }

                        trait T2 {
                            val v2 = "test2"
                            def m2() { }
                        }

                        class B extends A with T1 with T2
                        {
                            override def m1() {
                                super.m1()
                                super.m2()
                                val x = "foo"
                            }
                        }

                        class C(val v1: String, val v2: Int) extends A
                        {
                            val d = i + i
                        }
                    """
                } shouldCompileTo {
                    """
                        s2js.runtime.client.core.get().classLoader.provide('pkg.A');
                        s2js.runtime.client.core.get().classLoader.provide('pkg.B');
                        s2js.runtime.client.core.get().classLoader.provide('pkg.C');
                        s2js.runtime.client.core.get().classLoader.provide('pkg.T1');
                        s2js.runtime.client.core.get().classLoader.provide('pkg.T2');

                        pkg.A = function() {
                            var self = this;
                            self.i = '';
                        };
                        pkg.A.prototype.__class__ = new s2js.runtime.client.core.Class('pkg.A', []);

                        pkg.C = function(v1, v2) {
                            var self = this;
                            self.v1 = v1;
                            self.v2 = v2;
                            pkg.A.apply(self, []);
                            self.d = (self.i + self.i);
                        };
                        s2js.runtime.client.core.get().inherit(pkg.C, pkg.A);
                        pkg.C.prototype.__class__ = new s2js.runtime.client.core.Class('pkg.C', [pkg.A]);

                        pkg.T1 = function() {
                            var self = this;
                            self.v1 = 'test1';
                        };
                        pkg.T1.prototype.m1 = function() {
                            var self = this;
                        };
                        pkg.T1.prototype.__class__ = new s2js.runtime.client.core.Class('pkg.T1', []);

                        pkg.T2 = function() {
                            var self = this;
                            self.v2 = 'test2';
                        };
                        pkg.T2.prototype.m2 = function() {
                            var self = this;
                        };
                        pkg.T2.prototype.__class__ = new s2js.runtime.client.core.Class('pkg.T2', []);

                        pkg.B = function() {
                            var self = this;
                            pkg.A.apply(self, []);
                            s2js.runtime.client.core.get().mixInFields(self, new pkg.T2());
                            s2js.runtime.client.core.get().mixInFields(self, new pkg.T1());
                        };
                        s2js.runtime.client.core.get().inherit(pkg.B, pkg.A);

                        s2js.runtime.client.core.get().mixInFunctions(pkg.B.prototype, pkg.T2.prototype);
                        s2js.runtime.client.core.get().mixInFunctions(pkg.B.prototype, pkg.T1.prototype);
                        pkg.B.prototype.m1 = function() {
                            var self = this;
                            pkg.T1.prototype.m1.apply(self, []);
                            pkg.T2.prototype.m2.apply(self, []);
                            var x = 'foo';
                        };
                        pkg.B.prototype.__class__ = new s2js.runtime.client.core.Class('pkg.B', [pkg.A, pkg.T1, pkg.T2]);                    """
                }
        }

        it("parent constructor gets called properly") {
            configMap =>
                scalaCode {
                    """
                        package pkg

                        class A(val v1: String, val v2: Int)

                        class B extends A("test", 123)

                        class C(v1: String, v2: Int) extends A(v1, v2)
                    """
                } shouldCompileTo {
                    """
                        s2js.runtime.client.core.get().classLoader.provide('pkg.A');
                        s2js.runtime.client.core.get().classLoader.provide('pkg.B');
                        s2js.runtime.client.core.get().classLoader.provide('pkg.C');

                        pkg.A = function(v1, v2) {
                            var self = this;
                            self.v1 = v1;
                            self.v2 = v2;
                        };
                        pkg.A.prototype.__class__ = new s2js.runtime.client.core.Class('pkg.A', []);

                        pkg.B = function() {
                            var self = this;
                            pkg.A.apply(self, ['test', 123]);
                        };
                        s2js.runtime.client.core.get().inherit(pkg.B, pkg.A);
                        pkg.B.prototype.__class__ = new s2js.runtime.client.core.Class('pkg.B', [pkg.A]);

                        pkg.C = function(v1, v2) {
                            var self = this;
                            self.v1 = v1;
                            self.v2 = v2;
                            pkg.A.apply(self, [v1, v2]);
                        };
                        s2js.runtime.client.core.get().inherit(pkg.C, pkg.A);
                        pkg.C.prototype.__class__ = new s2js.runtime.client.core.Class('pkg.C', [pkg.A]);
                    """
                }
        }

        it("case classes are supported") {
            configMap =>
                scalaCode {
                    """
                        case class A(x: String, y: Int, z: Boolean)
                    """
                } shouldCompileTo {
                    """
                        s2js.runtime.client.core.get().classLoader.provide('A');
                        s2js.runtime.client.core.get().classLoader.declarationRequire('scala.Product');
                        s2js.runtime.client.core.get().classLoader.require('scala.IndexOutOfBoundsException');
                        s2js.runtime.client.core.get().classLoader.require('scala.None');
                        s2js.runtime.client.core.get().classLoader.require('scala.Some');
                        s2js.runtime.client.core.get().classLoader.require('scala.Tuple3');

                        A = function(x, y, z) {
                            var self = this;
                            self.x = x;
                            self.y = y;
                            self.z = z;
                            scala.Product.apply(self, []);
                        };
                        s2js.runtime.client.core.get().inherit(A, scala.Product);
                        A.prototype.copy = function(x, y, z) {
                            var self = this;
                            if (typeof(x) === 'undefined') { x = self.x; }
                            if (typeof(y) === 'undefined') { y = self.y; }
                            if (typeof(z) === 'undefined') { z = self.z; }
                            return new A(x, y, z);
                        };
                        A.prototype.toString = function() {
                            var self = this;
                            return scala.runtime.ScalaRunTime.get()._toString(self);
                        };
                        A.prototype.productPrefix = function() {
                            var self = this;
                            return 'A';
                        };
                        A.prototype.productArity = function() {
                            var self = this;
                            return 3;
                        };
                        A.prototype.productElement = function($x$1) {
                            var self = this;
                            return (function($selector$1) {
                                if ($selector$1 === 0) {
                                    return self.x;
                                }
                                if ($selector$1 === 1) {
                                    return self.y;
                                }
                                if ($selector$1 === 2) {
                                    return self.z;
                                }
                                if (true) {
                                    return (function() {
                                        throw new scala.IndexOutOfBoundsException($x$1.toString());
                                     })();
                                }
                            })($x$1);
                        };
                        A.prototype.__class__ = new s2js.runtime.client.core.Class('A', [scala.Product]);
                        s2js.runtime.client.core.get().mixIn(A, new s2js.runtime.client.core.Lazy(function() {
                            var obj = {};
                            obj.toString = function() { var self = this; return 'A'; };
                            obj.unapply = function(x$0) {
                                var self = this;
                                return (function() {
                                    if ((x$0 == null)) { return scala.None.get(); }
                                    else { return new scala.Some(new scala.Tuple3(x$0.x, x$0.y, x$0.z)); }
                                })();
                            };
                            obj.$apply = function(x, y, z) { var self = this; return new A(x, y, z); };
                            obj.__class__ = new s2js.runtime.client.core.Class('A', []);
                            return obj;
                        }), true);
                    """
                }
        }
    }

    describe("Objects") {
        it("can be declared") {
            configMap =>
                scalaCode {
                    """
                        package pkg

                        object o {
                            val v1 = "test"
                            val v2 = 12345
                            def m1() { }
                            def m2() { }
                        }
                    """
                } shouldCompileTo {
                    """
                        s2js.runtime.client.core.get().classLoader.provide('pkg.o');
                        s2js.runtime.client.core.get().mixIn(pkg.o, new s2js.runtime.client.core.Lazy(function() {
                            var obj = {};
                            obj.v1 = 'test';
                            obj.v2 = 12345;
                            obj.m1 = function() { var self = this; };
                            obj.m2 = function() { var self = this; };
                            obj.__class__ = new s2js.runtime.client.core.Class('pkg.o', []);
                            return obj;
                        }), true);
                    """
                }
        }

        it("can inherit from classes and traits") {
            configMap =>
                scalaCode {
                    """
                        package pkg

                        class A {
                            val v = 123
                            def m() { }
                        }

                        trait T1 {
                            val v1 = "test1"
                            def m1() { }
                        }

                        trait T2 {
                            val v2 = "test2"
                            def m2() { }
                        }

                        object o extends A with T1 with T2
                    """
                } shouldCompileTo {
                    """
                        s2js.runtime.client.core.get().classLoader.provide('pkg.A');
                        s2js.runtime.client.core.get().classLoader.provide('pkg.T1');
                        s2js.runtime.client.core.get().classLoader.provide('pkg.T2');
                        s2js.runtime.client.core.get().classLoader.provide('pkg.o');

                        pkg.A = function() { var self = this; self.v = 123; };
                        pkg.A.prototype.m = function() { var self = this; };
                        pkg.A.prototype.__class__ = new s2js.runtime.client.core.Class('pkg.A', []);

                        pkg.T1 = function() { var self = this; self.v1 = 'test1'; };
                        pkg.T1.prototype.m1 = function() { var self = this; };
                        pkg.T1.prototype.__class__ = new s2js.runtime.client.core.Class('pkg.T1', []);

                        pkg.T2 = function() { var self = this; self.v2 = 'test2'; };
                        pkg.T2.prototype.m2 = function() { var self = this; };
                        pkg.T2.prototype.__class__ = new s2js.runtime.client.core.Class('pkg.T2', []);

                        s2js.runtime.client.core.get().mixIn(pkg.o, new s2js.runtime.client.core.Lazy(function() {
                            var obj = {};
                            obj = new pkg.A();
                            s2js.runtime.client.core.get().mixIn(obj, new pkg.T2());
                            s2js.runtime.client.core.get().mixIn(obj, new pkg.T1());
                            obj.__class__ = new s2js.runtime.client.core.Class('pkg.o', [pkg.A, pkg.T1, pkg.T2]);
                            return obj;
                        }), true);
                    """
                }
        }

        it("companion objects are supported") {
            configMap =>
                scalaCode {
                    """
                        class A(val x: String, val y: Int) {
                            override def clone: A = {
                                A(x, y)
                            }
                        }

                        object A {
                            def apply(x: String, y: Int): A = new A(x, y)
                        }
                    """
                } shouldCompileTo {
                    """
                        s2js.runtime.client.core.get().classLoader.provide('A');

                        A = function(x, y) { var self = this; self.x = x; self.y = y; };
                        A.prototype.clone = function() { var self = this; return A.get().$apply(self.x, self.y); };
                        A.prototype.__class__ = new s2js.runtime.client.core.Class('A', []);

                        s2js.runtime.client.core.get().mixIn(A, new s2js.runtime.client.core.Lazy(function() {
                            var obj = {};
                            obj.$apply = function(x, y) { var self = this; return new A(x, y); };
                            obj.__class__ = new s2js.runtime.client.core.Class('A', []);
                            return obj;
                        }), true);
                    """
                }
        }
    }

    describe("Package objects") {
        it("can be declared using 'package object'") {
            configMap =>
                scalaCode {
                    """
                        package object po {
                            def m() { }
                        }
                    """
                } shouldCompileTo {
                    """
                        s2js.runtime.client.core.get().classLoader.provide('po');
                        s2js.runtime.client.core.get().mixIn(po, new s2js.runtime.client.core.Lazy(function() {
                            var obj = {};
                            obj.m = function() { var self = this; };
                            obj.__class__ = new s2js.runtime.client.core.Class('po', []); return obj;
                        }), true);
                    """
                }
        }

        it("can be declared using '`package`' object name") {
            configMap =>
                scalaCode {
                    """
                        package pkg

                        object `package` {
                            def m() { }
                        }
                    """
                } shouldCompileTo {
                    """
                        s2js.runtime.client.core.get().classLoader.provide('pkg');

                        s2js.runtime.client.core.get().mixIn(pkg, new s2js.runtime.client.core.Lazy(function() {
                            var obj = {};
                            obj.m = function() { var self = this; };
                            obj.__class__ = new s2js.runtime.client.core.Class('pkg', []);
                            return obj;
                        }), true);
                    """
                }
        }

        it("don't override the package") {
            configMap =>
                scalaCode {
                    """
                        package pkg

                        class A
                        class B

                        object `package` extends A
                    """
                } shouldCompileTo {
                    """
                        s2js.runtime.client.core.get().classLoader.provide('pkg');
                        s2js.runtime.client.core.get().classLoader.provide('pkg.A');
                        s2js.runtime.client.core.get().classLoader.provide('pkg.B');

                        pkg.A = function() { var self = this; };
                        pkg.A.prototype.__class__ = new s2js.runtime.client.core.Class('pkg.A', []);

                        pkg.B = function() { var self = this; };
                        pkg.B.prototype.__class__ = new s2js.runtime.client.core.Class('pkg.B', []);

                        s2js.runtime.client.core.get().mixIn(pkg, new s2js.runtime.client.core.Lazy(function() {
                            var obj = {};
                            obj = new pkg.A();
                            obj.__class__ = new s2js.runtime.client.core.Class('pkg', [pkg.A]);
                            return obj;
                        }), true);
                    """
                }
        }
    }
}
