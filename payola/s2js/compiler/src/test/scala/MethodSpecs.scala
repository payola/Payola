package s2js.compiler

class MethodSpecs extends CompilerFixtureSpec {
    describe("Method calls") {
        it("default arguments are supported") {
            configMap =>
                expect {
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
                } toBe {
                    """
                        goog.provide('o1');

                        if (typeof(o1) === 'undefined') { o1 = {}; }
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
                        o1.metaClass_ = new s2js.MetaClass('o1', []);
                    """
                }
        }

        it("can have a return value") {
            configMap =>
                expect {
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
                } toBe {
                    """
                        goog.provide('a');

                        if (typeof(a) === 'undefined') { a = {}; }
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
                        a.metaClass_ = new s2js.MetaClass('a', []);
                    """
                }
        }

        it("generic methods can be called") {
            configMap =>
                expect {
                    """
                        object a {
                            def m1[T](t: T) {}
                            def m2() {
                                m1("foo")
                            }
                        }
                    """
                } toBe {
                    """
                        goog.provide('a');

                        if (typeof(a) === 'undefined') { a = {}; }
                        a.m1 = function(t) {
                            var self = this;
                        };
                        a.m2 = function() {
                            var self = this;
                            self.m1('foo');
                        };
                        a.metaClass_ = new s2js.MetaClass('a', []);
                    """
                }
        }

        it("methods from same package should be fully qualified") {
            configMap =>
                expect {
                    """
                        package s2js.adapters.goog.events

                        object a {
                            val x = Event.preventDefault(null)
                        }
                    """
                } toBe {
                    """
                        goog.provide('goog.events.a');
                        goog.require('goog.events.Event');

                        if (typeof(goog.events.a) === 'undefined') { goog.events.a = {}; }
                        goog.events.a.x = goog.events.Event.preventDefault(null);
                        goog.events.a.metaClass_ = new s2js.MetaClass('goog.events.a', []);
                    """
                }
        }

         it("can call a method of returned object") {
            configMap =>
                expect {
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
                } toBe {
                    """
                        goog.provide('A');
                        goog.provide('b');

                        A = function() {
                            var self = this;
                        };
                        A.prototype.go = function(x) {
                            var self = this;
                            return ('foo' + x);
                        };
                        A.prototype.metaClass_ = new s2js.MetaClass('A', []);

                        if (typeof(b) === 'undefined') { b = {}; }
                        b.m1 = function() {
                            var self = this;
                            return new A();
                        };
                        b.m2 = function() {
                            var self = this;
                            var x = self.m1().go('bar').toString();
                        };
                        b.metaClass_ = new s2js.MetaClass('b', []);
                    """
                }
        }

        it("can have multiple parameter lists") {
            configMap =>
                expect {
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
                } toBe {
                """
                    goog.provide('o1');

                    if (typeof(o1) === 'undefined') { o1 = {}; }
                    o1.m1 = function(name, fn) {
                        var self = this;
                        fn(name);
                    };

                    o1.m3 = function() {
                        var self = this;
                        self.m1('foo', function(x) { window.alert(x); });
                    };
                    o1.metaClass_ = new s2js.MetaClass('o1', []);
                """
                }
        }

        it("methods can have other methods as parameters") {
            configMap =>
                expect {
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
                } toBe {
                    """
                        goog.provide('C1');
                        goog.provide('C2');
                        goog.provide('o1');

                        C1 = function() {
                            var self = this;
                            self.f1 = 'c1';
                        };
                        C1.prototype.m1 = function(fn) {
                            var self = this;
                            window.alert(self.f1);
                            fn(self.f1);
                        };
                        C1.prototype.metaClass_ = new s2js.MetaClass('C1', []);

                        C2 = function() {
                            var self = this;
                            self.f1 = 'c2';
                        };
                        C2.prototype.m1 = function(v1) {
                            var self = this;
                            window.alert((v1 + self.f1));
                        };
                        C2.prototype.metaClass_ = new s2js.MetaClass('C2', []);

                        if (typeof(o1) === 'undefined') { o1 = {}; }
                        o1.m1 = function() {
                            var self = this;
                            var c1 = new C1();
                            var c2 = new C2();
                            c1.m1(function(s2js_v1) { c2.m1(s2js_v1); });
                        };
                        o1.metaClass_ = new s2js.MetaClass('o1', []);
                    """
                }
        }

        it("can override base class methods") {
            configMap =>
                expect {
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
                } toBe {
                    """
                        goog.provide('$pkg.a');
                        goog.provide('$pkg.b');

                        $pkg.a = function() {
                            var self = this;
                        };
                        $pkg.a.prototype.m1 = function() {
                            var self = this;
                        };
                        $pkg.a.prototype.m2 = function(x) {
                            var self = this;
                        };
                        $pkg.a.prototype.metaClass_ = new s2js.MetaClass('$pkg.a', []);

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
                        $pkg.b.prototype.metaClass_ = new s2js.MetaClass('$pkg.b', [$pkg.a]);
                    """
                }
        }

        it("variadic methods are supported") {
            configMap =>
                expect {
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
                } toBe {
                    """
                        goog.provide('o');
                        goog.require('scala.Array');

                        if (typeof(o) === 'undefined') { o = {}; }
                        o.m1 = function() {
                            var self = this;
                            var x = scala.Array.fromNative([].splice.call(arguments, 0, arguments.length - 0));
                            x.foreach(function(i) { window.alert(i); });
                        };
                        o.m2 = function(a, b) {
                            var self = this;
                            var x = scala.Array.fromNative([].splice.call(arguments, 2, arguments.length - 2));
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
                        o.metaClass_ = new s2js.MetaClass('o', []);
                    """
                }
        }
    }
}
