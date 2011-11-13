package s2js

import org.scalatest.fixture.FixtureSpec
import org.scalatest.{ Spec, BeforeAndAfterAll }

class ImportSpecs extends CompilerFixtureSpec {

    it("avoid requiring deep packages") { configMap =>

        expect {"""

        package a.b.c {
            object d {
                def m1() {}
            }
        }

        """} toBe {"""

        goog.provide('a.b.c.d');

        a.b.c.d.m1 = function() {var self = this;};

        """}
    }

    it("add requires for used classes") { configMap =>

        expect {"""

            package foo {
                import java.util.ArrayList
                import java.util.Calendar
                object a {
                    val x = new java.util.Date
                    def m1() {
                        val y = new java.util.Random
                        val z = new ArrayList[String]
                    }
                }
            }

        """} toBe {"""

            goog.provide('foo.a');

            goog.require('java.util.Date');
            goog.require('java.util.Random');
            goog.require('java.util.ArrayList');

            foo.a.x = new java.util.Date();

            foo.a.m1 = function() {var self = this;
                var y = new java.util.Random();
                var z = new java.util.ArrayList();
            };

        """}
    }

    it("add require to a package object's owner") { configMap =>

        expect {"""

            object a {
                val x = goog.dom.getElement("foo")
            }

        """} toBe {"""

            goog.provide('a');
            goog.require('goog.dom');
            a.x = goog.dom.getElement('foo');

        """}
    
    }

    it("ignore implicit browser imports") { configMap =>

        expect {"""
        object o1 {
            val f1 = s2js.Html(<span>foo</span>)
            def m1() {
                println(f1.innerHTML)
            }
        }
        """} toBe {"""
        goog.provide('o1');
        o1.f1 = goog.dom.createDom('span',{},['foo']);
        o1.m1 = function() {var self = this;
            console.log(o1.f1.innerHTML);
        };
        """}
    }

    it("ignore explicit browser imports") { configMap =>

        expect {"""
        import browser._
        object o1 {
            val f1 = window.location
        }
        """} toBe {"""
        goog.provide('o1');
        o1.f1 = window.location;
        """}
    }
    
    it("don't require predef items") { configMap =>

        expect {"""

            object a {
                def m1() {
                    println("foo")
                }
            }

        """} toBe {"""

            goog.provide('a');
            a.m1 = function() {var self = this;
                console.log('foo');
            };

        """}
    }

    it("add requires for used object members") { configMap =>

        expect {"""

            import goog.dom.TagName._

            object a {
                def foo() {
                    println(SPAN)
                }
            }

        """} toBe {"""
        
            goog.provide('a');
            goog.require('goog.dom');
            goog.require('goog.dom.TagName');
            a.foo = function() {var self = this;
                console.log(goog.dom.TagName.SPAN);
            };
        
        """}
    }
}
