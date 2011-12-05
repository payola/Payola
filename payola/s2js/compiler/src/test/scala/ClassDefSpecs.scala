package s2js.compiler


class ClassDefSpecs extends CompilerFixtureSpec
{
    describe("Traits") {
        it("can be declared") {
            configMap =>
                expect {
                    """
                        package pkg

                        trait A {
                            val v1 = "test"
                            def m1() { }
                        }
                    """
                } toBe {
                    """
                        goog.provide('pkg.A');

                        pkg.A = function() {
                            var self = this;
                            self.v1 = 'test';
                        };
                        pkg.A.prototype.m1 = function() {
                            var self = this;
                        };
                        pkg.A.prototype.metaClass_ = new s2js.MetaClass('pkg.A', []);
                    """
                }
        }
    }

    describe("Classes") {
        it("can be declared") {
            configMap =>
                expect {
                    """
                        package pkg

                        class A
                    """
                } toBe {
                    """
                        goog.provide('pkg.A');

                        pkg.A = function() {
                            var self = this;
                        };
                        pkg.A.prototype.metaClass_ = new s2js.MetaClass('pkg.A', []);
                    """
                }
        }

        it("can have implicit constructor") {
            configMap =>
                expect {
                    """
                        package pkg

                        class A(val v1: Int, val v2: String, val v3: Boolean = true, v4: Double) {
                            val v5 = "test"
                            val v6 = 12345
                            val v7 = v1
                            val v8 = v4
                        }
                    """
                } toBe {
                    """
                        goog.provide('pkg.A');

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
                        pkg.A.prototype.metaClass_ = new s2js.MetaClass('pkg.A', []);
                    """
                }
        }

        it("constructor can have body") {
            configMap =>
                expect {
                    """
                        package pkg

                        import s2js.adapters.js.browser._

                        class A(val v1: Int) {
                            window.alert(v1.toString)
                        }
                    """
                } toBe {
                    """
                        goog.provide('pkg.A');

                        pkg.A = function(v1) {
                            var self = this;
                            self.v1 = v1;
                            window.alert(self.v1.toString());
                        };
                        pkg.A.prototype.metaClass_ = new s2js.MetaClass('pkg.A', []);
                    """
                }
        }

        it("can inherit from classes nad traits") {
            configMap =>
                expect {
                    """
                        package pkg

                        class A

                        trait T1 {
                            val v1 = "test1"
                            def m1() { }
                        }

                        trait T2 {
                            val v2 = "test2"
                            def m2() { }
                        }

                        class B extends A with T1 with T2

                        class C(val v1: String, val v2: Int) extends A
                    """
                } toBe {
                    """
                        goog.provide('pkg.A');
                        goog.provide('pkg.B');
                        goog.provide('pkg.C');
                        goog.provide('pkg.T1');
                        goog.provide('pkg.T2');

                        pkg.A = function() {
                            var self = this;
                        };
                        pkg.A.prototype.metaClass_ = new s2js.MetaClass('pkg.A', []);

                        pkg.C = function(v1, v2) {
                            var self = this;
                            self.v1 = v1;
                            self.v2 = v2;
                            goog.base(self);
                        };
                        goog.inherits(pkg.C, pkg.A);
                        pkg.C.prototype.metaClass_ = new s2js.MetaClass('pkg.C', [pkg.A]);

                        pkg.T1 = function() {
                            var self = this;
                            self.v1 = 'test1';
                        };
                        pkg.T1.prototype.m1 = function() {
                            var self = this;
                        };
                        pkg.T1.prototype.metaClass_ = new s2js.MetaClass('pkg.T1', []);

                        pkg.T2 = function() {
                            var self = this;
                            self.v2 = 'test2';
                        };
                        pkg.T2.prototype.m2 = function() {
                            var self = this;
                        };
                        pkg.T2.prototype.metaClass_ = new s2js.MetaClass('pkg.T2', []);

                        pkg.B = function() {
                            var self = this;
                            goog.base(self);
                            goog.object.extend(self, new pkg.T1());
                            goog.object.extend(self, new pkg.T2());
                        };
                        goog.inherits(pkg.B, pkg.A);
                        pkg.B.prototype.metaClass_ = new s2js.MetaClass('pkg.B', [pkg.A, pkg.T1, pkg.T2]);
                    """
                }
        }

        it("parent constructor gets called properly") {
            configMap =>
                expect {
                    """
                        package pkg

                        class A(val v1: String, val v2: Int)

                        class B extends A("test", 123)

                        class C(v1: String, v2: Int) extends A(v1, v2)
                    """
                } toBe {
                    """
                        goog.provide('pkg.A');
                        goog.provide('pkg.B');
                        goog.provide('pkg.C');

                        pkg.A = function(v1, v2) {
                            var self = this;
                            self.v1 = v1;
                            self.v2 = v2;
                        };
                        pkg.A.prototype.metaClass_ = new s2js.MetaClass('pkg.A', []);

                        pkg.B = function() {
                            var self = this;
                            goog.base(self, 'test', 123);
                        };
                        goog.inherits(pkg.B, pkg.A);
                        pkg.B.prototype.metaClass_ = new s2js.MetaClass('pkg.B', [pkg.A]);

                        pkg.C = function(v1, v2) {
                            var self = this;
                            self.v1 = v1;
                            self.v2 = v2;
                            goog.base(self, v1, v2);
                        };
                        goog.inherits(pkg.C, pkg.A);
                        pkg.C.prototype.metaClass_ = new s2js.MetaClass('pkg.C', [pkg.A]);
                    """
                }
        }
    }

    describe("Objects") {
        it("can be declared") {
            configMap =>
                expect {
                    """
                        package pkg

                        object o {
                            val v1 = "test"
                            val v2 = 12345
                            def m1() { }
                            def m2() { }
                        }
                    """
                } toBe {
                    """
                        goog.provide('pkg.o');

                        if (typeof(pkg.o) === 'undefined') { pkg.o = {}; }
                        pkg.o.v1 = 'test';
                        pkg.o.v2 = 12345;
                        pkg.o.m1 = function() {
                            var self = this;
                        };
                        pkg.o.m2 = function() {
                            var self = this;
                        };
                        pkg.o.metaClass_ = new s2js.MetaClass('pkg.o', []);
                    """
                }
        }

        it("can inherit from classes and traits") {
            configMap =>
                expect {
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
                } toBe {
                    """
                        goog.provide('pkg.A');
                        goog.provide('pkg.T1');
                        goog.provide('pkg.T2');
                        goog.provide('pkg.o');
                        pkg.A = function() {
                            var self = this;
                            self.v = 123;
                        };
                        pkg.A.prototype.m = function() {
                            var self = this;
                        };
                        pkg.A.prototype.metaClass_ = new s2js.MetaClass('pkg.A', []);

                        pkg.T1 = function() {
                            var self = this;
                            self.v1 = 'test1';
                        };
                        pkg.T1.prototype.m1 = function() {
                            var self = this;
                        };
                        pkg.T1.prototype.metaClass_ = new s2js.MetaClass('pkg.T1', []);

                        pkg.T2 = function() {
                            var self = this;
                            self.v2 = 'test2';
                        };
                        pkg.T2.prototype.m2 = function() {
                            var self = this;
                        };
                        pkg.T2.prototype.metaClass_ = new s2js.MetaClass('pkg.T2', []);

                        if (typeof(pkg.o) === 'undefined') { pkg.o = {}; }
                        goog.object.extend(pkg.o, new pkg.A());
                        goog.object.extend(pkg.o, new pkg.T1());
                        goog.object.extend(pkg.o, new pkg.T2());
                        pkg.o.metaClass_ = new s2js.MetaClass('pkg.o', [pkg.A, pkg.T1, pkg.T2]);
                    """
                }
        }

        it("companion objects are supported") {
            configMap =>
                expect {
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
                } toBe {
                    """
                        goog.provide('A');

                        A = function(x, y) {
                            var self = this;
                            self.x = x;
                            self.y = y;
                        };
                        A.prototype.clone = function() {
                            var self = this;
                            return A.s2js_apply(self.x, self.y);
                        };
                        A.prototype.metaClass_ = new s2js.MetaClass('A', []);

                        if (typeof(A) === 'undefined') { A = {}; }
                        A.s2js_apply = function(x, y) {
                            var self = this;
                            return new A(x, y);
                        };
                        A.metaClass_ = new s2js.MetaClass('A', []);
                    """
                }
        }
    }

    describe("Package objects") {
        it("can be declared using 'package object'") {
            configMap =>
                expect {
                    """
                        package object po {
                            def m() { }
                        }
                    """
                } toBe {
                    """
                        goog.provide('po');

                        if (typeof(po) === 'undefined') { po = {}; }
                        po.m = function() {
                            var self = this;
                        };
                        po.metaClass_ = new s2js.MetaClass('po', []);
                    """
                }
        }

        it("can be declared using '`package`' object name") {
            configMap =>
                expect {
                    """
                        package pkg

                        object `package` {
                            def m() { }
                        }
                    """
                } toBe {
                    """
                        goog.provide('pkg');

                        if (typeof(pkg) === 'undefined') { pkg = {}; }
                        pkg.m = function() {
                            var self = this;
                        };
                        pkg.metaClass_ = new s2js.MetaClass('pkg', []);
                    """
                }
        }

        it("don't override the package") {
            configMap =>
                expect {
                    """
                        package pkg

                        class A
                        class B

                        object `package` extends A
                    """
                } toBe {
                    """
                        goog.provide('pkg');
                        goog.provide('pkg.A');
                        goog.provide('pkg.B');

                        pkg.A = function() {
                            var self = this;
                        };
                        pkg.A.prototype.metaClass_ = new s2js.MetaClass('pkg.A', []);

                        pkg.B = function() {
                            var self = this;
                        };
                        pkg.B.prototype.metaClass_ = new s2js.MetaClass('pkg.B', []);

                        if (typeof(pkg) === 'undefined') { pkg = {}; }
                        goog.object.extend(pkg, new pkg.A());
                        pkg.metaClass_ = new s2js.MetaClass('pkg', [pkg.A]);
                    """
                }
        }
    }
}
