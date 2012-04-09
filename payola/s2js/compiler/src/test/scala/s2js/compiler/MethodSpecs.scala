package s2js.compiler

class MethodSpecs extends CompilerFixtureSpec
{
    describe("Method calls") {
        it("parentheses can be omitted") {
            configMap =>
                scalaCode {
                    """
                        object o1 {
                            def m: String = "foo"
                        }

                        object o2 {
                            def m: String = "bar"
                            def n {}
                            def m3() {
                                n
                                val x = m
                                val y = o1.m
                                val z = o1.m.length
                                val emptyList = scala.collection.immutable.List.empty[String]
                            }
                        }
                    """
                } shouldCompileTo {
                    """
                        s2js.runtime.client.ClassLoader.provide('o1');
                        s2js.runtime.client.ClassLoader.provide('o2');
                        s2js.runtime.client.ClassLoader.require('scala.collection.immutable.List');

                        o1.m = function() {
                            var self = this;
                            return 'foo';
                        };
                        o1.__class__ = new s2js.runtime.client.Class('o1', []);

                        o2.m = function() {
                            var self = this;
                            return 'bar';
                        };
                        o2.n = function() { var self = this; };
                        o2.m3 = function() {
                            var self = this;
                            self.n();
                            var x = self.m();
                            var y = o1.m();
                            var z = o1.m().$length();
                            var emptyList = scala.collection.immutable.List.empty();
                        };
                        o2.__class__ = new s2js.runtime.client.Class('o2', []);
                    """
                }
        }

        it("default parameters are supported") {
            configMap =>
                scalaCode {
                    """
                        object o1 {
                            def m1(v1:String, v2:String = "") {}
                            def m2(v1:String, v2:String = null) {}
                            def m3() {
                                m1("foo")
                                m1("foo", "bar")
                                m2("foo")
                            }
                        }
                    """
                } shouldCompileTo {
                    """
                        s2js.runtime.client.ClassLoader.provide('o1');

                        o1.m1 = function(v1, v2) {
                            var self = this;
                            if (typeof(v2) === 'undefined') { v2 = ''; }
                        };
                        o1.m2 = function(v1, v2) {
                            var self = this;
                            if (typeof(v2) === 'undefined') { v2 = null; }
                        };
                        o1.m3 = function() {
                            var self = this;
                            self.m1('foo');
                            self.m1('foo', 'bar');
                            self.m2('foo');
                        };
                        o1.__class__ = new s2js.runtime.client.Class('o1', []);
                    """
                }
        }

        it("default parameters can reference fields") {
            configMap =>
                scalaCode {
                    """
                        object o1 {
                            val x = "o1"
                        }

                        object o2 {
                            val x = "o2";

                            def m(a: String = x, x: String = x, y: String = o1.x) {}
                        }
                    """
                } shouldCompileTo {
                    """
                        s2js.runtime.client.ClassLoader.provide('o1');
                        s2js.runtime.client.ClassLoader.provide('o2');

                        o1.x = 'o1';
                        o1.__class__ = new s2js.runtime.client.Class('o1', []);

                        o2.x = 'o2';
                        o2.m = function(a, x, y) {
                            var self = this;
                            if (typeof(a) === 'undefined') { a = self.x; }
                            if (typeof(x) === 'undefined') { x = self.x; }
                            if (typeof(y) === 'undefined') { y = o1.x; }
                        };
                        o2.__class__ = new s2js.runtime.client.Class('o2', []);
                    """
                }
        }

        it("can have a return value") {
            configMap =>
                scalaCode {
                    """
                        object a {
                            def m1() = {
                                val x = "foo"
                                x + "bar"
                            }
                            def m2() = {
                                "foo"
                            }
                            def m3() = {
                                "foo" + "bar"
                            }
                            def m4() {
                                "foo" + "bar"
                            }
                        }
                    """
                } shouldCompileTo {
                    """
                        s2js.runtime.client.ClassLoader.provide('a');

                        a.m1 = function() {
                            var self = this;
                            var x = 'foo';
                            return (x + 'bar');
                        };
                        a.m2 = function() {
                            var self = this;
                            return 'foo';
                        };
                        a.m3 = function() {
                            var self = this;
                            return 'foobar';
                        };
                        a.m4 = function() {
                            var self = this;
                            'foobar';
                        };
                        a.__class__ = new s2js.runtime.client.Class('a', []);
                    """
                }
        }

        it("generic methods can be called") {
            configMap =>
                scalaCode {
                    """
                        object a {
                            def m1[T](t: T) {}
                            def m2() {
                                m1("foo")
                            }
                        }
                    """
                } shouldCompileTo {
                    """
                        s2js.runtime.client.ClassLoader.provide('a');

                        a.m1 = function(t) {
                            var self = this;
                        };
                        a.m2 = function() {
                            var self = this;
                            self.m1('foo');
                        };
                        a.__class__ = new s2js.runtime.client.Class('a', []);
                    """
                }
        }

        it("methods from same package should be fully qualified") {
            configMap =>
                scalaCode {
                    """
                        package s2js.adapters.goog.events

                        object a {
                            val x = Event.preventDefault(null)
                        }
                    """
                } shouldCompileTo {
                    """
                        s2js.runtime.client.ClassLoader.provide('goog.events.a');
                        s2js.runtime.client.ClassLoader.require('goog.events.Event');

                        goog.events.a.x = goog.events.Event.preventDefault(null);
                        goog.events.a.__class__ = new s2js.runtime.client.Class('goog.events.a', []);
                    """
                }
        }

        it("can call a method of returned object") {
            configMap =>
                scalaCode {
                    """
                        class A {
                            def go(x:String) = "foo" + x
                        }

                        object b {
                            def m1(): A = new A
                            def m2() {
                                val x = m1().go("bar").toString
                            }
                        }
                    """
                } shouldCompileTo {
                    """
                        s2js.runtime.client.ClassLoader.provide('A');
                        s2js.runtime.client.ClassLoader.provide('b');

                        A = function() {
                            var self = this;
                        };
                        A.prototype.go = function(x) {
                            var self = this;
                            return ('foo' + x);
                        };
                        A.prototype.__class__ = new s2js.runtime.client.Class('A', []);

                        b.m1 = function() {
                            var self = this;
                            return new A();
                        };
                        b.m2 = function() {
                            var self = this;
                            var x = self.m1().go('bar').toString();
                        };
                        b.__class__ = new s2js.runtime.client.Class('b', []);
                    """
                }
        }

        it("can have multiple parameter lists") {
            configMap =>
                scalaCode {
                    """
                        import s2js.adapters.js.browser._

                        object o1 {
                            def m1(name: String)(fn: (String) => Unit) {
                                fn(name)
                            }
                            def m3() {
                                m1("foo") {
                                    x => window.alert(x)
                                }
                            }
                        }
                    """
                } shouldCompileTo {
                    """
                        s2js.runtime.client.ClassLoader.provide('o1');

                        o1.m1 = function(name, fn) {
                            var self = this;
                            fn(name);
                        };

                        o1.m3 = function() {
                            var self = this;
                            self.m1('foo', function(x) { window.alert(x); });
                        };
                        o1.__class__ = new s2js.runtime.client.Class('o1', []);
                    """
                }
        }

        it("methods can have other methods as parameters") {
            configMap =>
                scalaCode {
                    """
                        import s2js.adapters.js.browser._

                        class C1 {
                            val f1 = "c1"
                            def m1(fn: (String) => Unit) {
                                window.alert(f1)
                                fn(f1)
                            }
                        }

                        class C2 {
                            val f1 = "c2"
                            def m1(v1: String) {
                                window.alert(v1 + f1)
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
                } shouldCompileTo {
                    """
                        s2js.runtime.client.ClassLoader.provide('C1');
                        s2js.runtime.client.ClassLoader.provide('C2');
                        s2js.runtime.client.ClassLoader.provide('o1');

                        C1 = function() {
                            var self = this;
                            self.f1 = 'c1';
                        };
                        C1.prototype.m1 = function(fn) {
                            var self = this;
                            window.alert(self.f1);
                            fn(self.f1);
                        };
                        C1.prototype.__class__ = new s2js.runtime.client.Class('C1', []);

                        C2 = function() {
                            var self = this;
                            self.f1 = 'c2';
                        };
                        C2.prototype.m1 = function(v1) {
                            var self = this;
                            window.alert((v1 + self.f1));
                        };
                        C2.prototype.__class__ = new s2js.runtime.client.Class('C2', []);

                        o1.m1 = function() {
                            var self = this;
                            var c1 = new C1();
                            var c2 = new C2();
                            c1.m1(function($v1) { c2.m1($v1); });
                        };
                        o1.__class__ = new s2js.runtime.client.Class('o1', []);
                    """
                }
        }

        it("can override base class methods") {
            configMap =>
                scalaCode {
                    """
                        package $pkg

                        class a {
                            def m1() {}
                            def m2(x:String) {}
                        }
                        class b extends a {
                            override def m1() { super.m1() }
                            override def m2(x:String) { super.m2("foo") }
                        }
                    """
                } shouldCompileTo {
                    """
                        s2js.runtime.client.ClassLoader.provide('$pkg.a');
                        s2js.runtime.client.ClassLoader.provide('$pkg.b');

                        $pkg.a = function() {
                            var self = this;
                        };
                        $pkg.a.prototype.m1 = function() {
                            var self = this;
                        };
                        $pkg.a.prototype.m2 = function(x) {
                            var self = this;
                        };
                        $pkg.a.prototype.__class__ = new s2js.runtime.client.Class('$pkg.a', []);

                        $pkg.b = function() {
                            var self = this;
                            goog.base(self);
                        };
                        goog.inherits($pkg.b, $pkg.a);
                        $pkg.b.prototype.m1 = function() {
                            var self = this;
                            goog.base(self, 'm1');
                        };
                        $pkg.b.prototype.m2 = function(x) {
                            var self = this;
                            goog.base(self, 'm2', 'foo');
                        };
                        $pkg.b.prototype.__class__ = new s2js.runtime.client.Class('$pkg.b', [$pkg.a]);
                    """
                }
        }

        it("variadic methods are supported") {
            configMap =>
                scalaCode {
                    """
                        import s2js.adapters.js.browser._

                        object o {
                            def m1(x: Int*) {
                                for (i <- x) {
                                    window.alert(i)
                                }
                            }

                            def m2(a: String, b: Int, x: Int*) {
                                for (i <- x) {
                                    window.alert(a + (b + i))
                                }
                            }

                            def test() {
                                m1()
                                m1(1)
                                m1(1, 2)
                                m1(1, 2, 3, 4, 5, 6, 7)
                                m2("test", 5)
                                m2("test", 5, 6)
                                m2("test", 5, 6, 7, 8)
                            }
                        }
                    """
                } shouldCompileTo {
                    """
                        s2js.runtime.client.ClassLoader.provide('o');
                        s2js.runtime.client.ClassLoader.require('scala.collection.immutable.List');

                        o.m1 = function() {
                            var self = this;
                            var x = scala.collection.immutable.List.fromJsArray([].splice.call(arguments, 0,
                            arguments.length - 0));
                            x.foreach(function(i) { window.alert(i); });
                        };
                        o.m2 = function(a, b) {
                            var self = this;
                            var x = scala.collection.immutable.List.fromJsArray([].splice.call(arguments, 2,
                            arguments.length - 2));
                            x.foreach(function(i) { window.alert((a + (b + i))); });
                        };
                        o.test = function() {
                            var self = this;
                            self.m1();
                            self.m1(1);
                            self.m1(1, 2);
                            self.m1(1, 2, 3, 4, 5, 6, 7);
                            self.m2('test', 5);
                            self.m2('test', 5, 6);
                            self.m2('test', 5, 6, 7, 8);
                        };
                        o.__class__ = new s2js.runtime.client.Class('o', []);
                    """
                }
        }
    }
}
