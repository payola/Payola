package s2js.compiler

class ClassDefSpecs extends CompilerFixtureSpec
{
    describe("Traits") {
        ignore("can be declared") {
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
                        s2js.runtime.client.ClassLoader.provide('pkg.A');

                        pkg.A = function() {
                            var self = this;
                            self.v1 = 'test';
                        };
                        pkg.A.prototype.m1 = function() {
                            var self = this;
                        };
                        pkg.A.prototype.__class__ = new s2js.runtime.client.Class('pkg.A', []);
                    """
                }
        }
    }

    describe("Classes") {
        ignore("can be declared") {
            configMap =>
                scalaCode {
                    """
                        package pkg

                        class A
                    """
                } shouldCompileTo {
                    """
                        s2js.runtime.client.ClassLoader.provide('pkg.A');

                        pkg.A = function() {
                            var self = this;
                        };
                        pkg.A.prototype.__class__ = new s2js.runtime.client.Class('pkg.A', []);
                    """
                }
        }

        ignore("can have getters and setters") {
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
                        s2js.runtime.client.ClassLoader.provide('pkg.A');
                        s2js.runtime.client.ClassLoader.provide('pkg.t');

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
                        pkg.A.prototype.__class__ = new s2js.runtime.client.Class('pkg.A', []);

                        pkg.t.test = function() {
                            var self = this;
                            var a = new pkg.A();
                            var x = a.x();
                            var y = a.y;
                            a.x_$eq('new x');
                            a.y = 'new y';
                        };
                        pkg.t.__class__ = new s2js.runtime.client.Class('pkg.t', []);
                    """
                }
        }

        ignore("can have implicit constructor") {
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
                        s2js.runtime.client.ClassLoader.provide('pkg.A');

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
                        pkg.A.prototype.__class__ = new s2js.runtime.client.Class('pkg.A', []);
                    """
                }
        }

        ignore("constructor can have body") {
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
                        s2js.runtime.client.ClassLoader.provide('pkg.A');

                        pkg.A = function(v1) {
                            var self = this;
                            self.v1 = v1;
                            window.alert(self.v1.toString());
                        };
                        pkg.A.prototype.__class__ = new s2js.runtime.client.Class('pkg.A', []);
                    """
                }
        }

        ignore("can inherit from classes nad traits") {
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
                        s2js.runtime.client.ClassLoader.provide('pkg.A');
                        s2js.runtime.client.ClassLoader.provide('pkg.B');
                        s2js.runtime.client.ClassLoader.provide('pkg.C');
                        s2js.runtime.client.ClassLoader.provide('pkg.T1');
                        s2js.runtime.client.ClassLoader.provide('pkg.T2');

                        pkg.A = function() {
                            var self = this;
                            self.i = '';
                        };
                        pkg.A.prototype.__class__ = new s2js.runtime.client.Class('pkg.A', []);

                        pkg.C = function(v1, v2) {
                            var self = this;
                            self.v1 = v1;
                            self.v2 = v2;
                            pkg.A.apply(self, []);
                            self.d = (self.i + self.i);
                        };
                        goog.inherits(pkg.C, pkg.A);
                        pkg.C.prototype.__class__ = new s2js.runtime.client.Class('pkg.C', [pkg.A]);

                        pkg.T1 = function() {
                            var self = this;
                            self.v1 = 'test1';
                        };
                        pkg.T1.prototype.m1 = function() {
                            var self = this;
                        };
                        pkg.T1.prototype.__class__ = new s2js.runtime.client.Class('pkg.T1', []);

                        pkg.T2 = function() {
                            var self = this;
                            self.v2 = 'test2';
                        };
                        pkg.T2.prototype.m2 = function() {
                            var self = this;
                        };
                        pkg.T2.prototype.__class__ = new s2js.runtime.client.Class('pkg.T2', []);

                        pkg.B = function() {
                            var self = this;
                            pkg.A.apply(self, []);
                            s2js.runtime.client.mixIn(self, new pkg.T2());
                            s2js.runtime.client.mixIn(self, new pkg.T1());
                        };
                        goog.inherits(pkg.B, pkg.A);

                        pkg.B.prototype.m1 = function() {
                            var self = this;
                            pkg.T1.prototype.m1.apply(self, []);
                            pkg.T2.prototype.m2.apply(self, []);
                            var x = 'foo';
                        };
                        pkg.B.prototype.__class__ = new s2js.runtime.client.Class('pkg.B', [pkg.A, pkg.T1, pkg.T2]);
                    """
                }
        }

        ignore("parent constructor gets called properly") {
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
                        s2js.runtime.client.ClassLoader.provide('pkg.A');
                        s2js.runtime.client.ClassLoader.provide('pkg.B');
                        s2js.runtime.client.ClassLoader.provide('pkg.C');

                        pkg.A = function(v1, v2) {
                            var self = this;
                            self.v1 = v1;
                            self.v2 = v2;
                        };
                        pkg.A.prototype.__class__ = new s2js.runtime.client.Class('pkg.A', []);

                        pkg.B = function() {
                            var self = this;
                            pkg.A.apply(self, ['test', 123]);
                        };
                        goog.inherits(pkg.B, pkg.A);
                        pkg.B.prototype.__class__ = new s2js.runtime.client.Class('pkg.B', [pkg.A]);

                        pkg.C = function(v1, v2) {
                            var self = this;
                            self.v1 = v1;
                            self.v2 = v2;
                            pkg.A.apply(self, [v1, v2]);
                        };
                        goog.inherits(pkg.C, pkg.A);
                        pkg.C.prototype.__class__ = new s2js.runtime.client.Class('pkg.C', [pkg.A]);
                    """
                }
        }

        ignore("case classes are supported") {
            configMap =>
                scalaCode {
                    """
                        case class A(x: String, y: Int, z: Boolean)
                    """
                } shouldCompileTo {
                    """
                        s2js.runtime.client.ClassLoader.provide('A');
                        s2js.runtime.client.ClassLoader.declarationRequire('scala.Product');
                        s2js.runtime.client.ClassLoader.require('scala.IndexOutOfBoundsException');
                        s2js.runtime.client.ClassLoader.require('scala.None');
                        s2js.runtime.client.ClassLoader.require('scala.Some');
                        s2js.runtime.client.ClassLoader.require('scala.Tuple3');
                        s2js.runtime.client.ClassLoader.require('scala.runtime.ScalaRunTime');

                        A = function(x, y, z) {
                            var self = this;
                            self.x = x;
                            self.y = y;
                            self.z = z;
                            scala.Product.apply(self, []);
                        };
                        goog.inherits(A, scala.Product);
                        A.prototype.copy = function(x, y, z) {
                            var self = this;
                            if (typeof(x) === 'undefined') { x = self.x; }
                            if (typeof(y) === 'undefined') { y = self.y; }
                            if (typeof(z) === 'undefined') { z = self.z; }
                            return new A(x, y, z);
                        };
                        A.prototype.toString = function() {
                            var self = this;
                            return scala.runtime.ScalaRunTime._toString(self);
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
                        A.prototype.__class__ = new s2js.runtime.client.Class('A', [scala.Product]);
                        A.toString = function() {
                            var self = this;
                            return 'A';
                        };
                        A.unapply = function(x$0) {
                            var self = this;
                            return (function() {
                                if ((x$0 == null)) {
                                    return scala.None;
                                } else {
                                    return new scala.Some(new scala.Tuple3(x$0.x, x$0.y, x$0.z));
                                }
                            })();
                        };
                        A.$apply = function(x, y, z) {
                            var self = this;
                            return new A(x, y, z);
                        };
                        A.__class__ = new s2js.runtime.client.Class('A', []);
                    """
                }
        }
    }

    describe("Objects") {
        ignore("can be declared") {
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
                        s2js.runtime.client.ClassLoader.provide('pkg.o');

                        pkg.o.v1 = 'test';
                        pkg.o.v2 = 12345;
                        pkg.o.m1 = function() {
                            var self = this;
                        };
                        pkg.o.m2 = function() {
                            var self = this;
                        };
                        pkg.o.__class__ = new s2js.runtime.client.Class('pkg.o', []);
                    """
                }
        }

        ignore("can inherit from classes and traits") {
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
                        s2js.runtime.client.ClassLoader.provide('pkg.A');
                        s2js.runtime.client.ClassLoader.provide('pkg.T1');
                        s2js.runtime.client.ClassLoader.provide('pkg.T2');
                        s2js.runtime.client.ClassLoader.provide('pkg.o');
                        pkg.A = function() {
                            var self = this;
                            self.v = 123;
                        };
                        pkg.A.prototype.m = function() {
                            var self = this;
                        };
                        pkg.A.prototype.__class__ = new s2js.runtime.client.Class('pkg.A', []);

                        pkg.T1 = function() {
                            var self = this;
                            self.v1 = 'test1';
                        };
                        pkg.T1.prototype.m1 = function() {
                            var self = this;
                        };
                        pkg.T1.prototype.__class__ = new s2js.runtime.client.Class('pkg.T1', []);

                        pkg.T2 = function() {
                            var self = this;
                            self.v2 = 'test2';
                        };
                        pkg.T2.prototype.m2 = function() {
                            var self = this;
                        };
                        pkg.T2.prototype.__class__ = new s2js.runtime.client.Class('pkg.T2', []);

                        s2js.runtime.client.mixIn(pkg.o, new pkg.A());
                        s2js.runtime.client.mixIn(pkg.o, new pkg.T2());
                        s2js.runtime.client.mixIn(pkg.o, new pkg.T1());
                        pkg.o.__class__ = new s2js.runtime.client.Class('pkg.o', [pkg.A, pkg.T1, pkg.T2]);
                    """
                }
        }

        ignore("companion objects are supported") {
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
                        s2js.runtime.client.ClassLoader.provide('A');

                        A = function(x, y) {
                            var self = this;
                            self.x = x;
                            self.y = y;
                        };
                        A.prototype.clone = function() {
                            var self = this;
                            return A.$apply(self.x, self.y);
                        };
                        A.prototype.__class__ = new s2js.runtime.client.Class('A', []);

                        A.$apply = function(x, y) {
                            var self = this;
                            return new A(x, y);
                        };
                        A.__class__ = new s2js.runtime.client.Class('A', []);
                    """
                }
        }
    }

    describe("Package objects") {
        ignore("can be declared using 'package object'") {
            configMap =>
                scalaCode {
                    """
                        package object po {
                            def m() { }
                        }
                    """
                } shouldCompileTo {
                    """
                        s2js.runtime.client.ClassLoader.provide('po');

                        po.m = function() {
                            var self = this;
                        };
                        po.__class__ = new s2js.runtime.client.Class('po', []);
                    """
                }
        }

        ignore("can be declared using '`package`' object name") {
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
                        s2js.runtime.client.ClassLoader.provide('pkg');

                        pkg.m = function() {
                            var self = this;
                        };
                        pkg.__class__ = new s2js.runtime.client.Class('pkg', []);
                    """
                }
        }

        ignore("don't override the package") {
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
                        s2js.runtime.client.ClassLoader.provide('pkg');
                        s2js.runtime.client.ClassLoader.provide('pkg.A');
                        s2js.runtime.client.ClassLoader.provide('pkg.B');

                        pkg.A = function() {
                            var self = this;
                        };
                        pkg.A.prototype.__class__ = new s2js.runtime.client.Class('pkg.A', []);

                        pkg.B = function() {
                            var self = this;
                        };
                        pkg.B.prototype.__class__ = new s2js.runtime.client.Class('pkg.B', []);

                        s2js.runtime.client.mixIn(pkg, new pkg.A());
                        pkg.__class__ = new s2js.runtime.client.Class('pkg', [pkg.A]);
                    """
                }
        }
    }
}
