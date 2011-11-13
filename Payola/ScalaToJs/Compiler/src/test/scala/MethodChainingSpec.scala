package s2js

import org.scalatest.fixture.FixtureSpec
import org.scalatest.{ Spec, BeforeAndAfterAll }

class MethodChainingSpec extends CompilerFixtureSpec {

    describe("method chaining") {

        it("calling a method of returned object") { configMap =>

            expect {"""

                class A {
                    def go(x:String) = "foo"+x
                }

                object b {
                    def m1():A = new A
                    def m2() {
                        val x = m1().go("bar").toString
                    }
                }

            """} toBe {"""

                goog.provide('A');
                goog.provide('b');
                /** @constructor*/
                A = function() {var self = this;};
                A.prototype.go = function(x) {var self = this;
                    return ('foo' + x);
                };
                b.m1 = function() {var self = this;return new A();};
                b.m2 = function() {var self = this;
                    var x = b.m1().go('bar').toString();
                };

            """}
        }

        it("call a method from with in a class") { configMap =>
 
            expect {"""

                class A {
                    def m1() = "m1"
                    def m2() = m1()
                }

                object b {
                    def m1() = {
                        val a = new A
                        a.m2
                    }
                }

            """} toBe {"""

                goog.provide('A');
                goog.provide('b');

                /** @constructor*/
                A = function() {var self = this;};
                A.prototype.m1 = function() {
                    var self = this;
                    return 'm1';
                };
                A.prototype.m2 = function() {
                    var self = this;
                    return self.m1();
                };
                b.m1 = function() {
                    var self = this;
                    var a = new A();
                    return a.m2();
                };

            """}

        }
    }
}

// vim: set ts=4 sw=4 foldmethod=syntax et:
