package s2js

import org.scalatest.fixture.FixtureSpec
import org.scalatest.{ Spec, BeforeAndAfterAll }

class PackageSpecs extends CompilerFixtureSpec {

  describe("classes") {

    it("can be case classes") { configMap =>

      expect {"""
      case class A(name:String)
      """} toBe {"""
      goog.provide('A');
      goog.require('scalosure');
      goog.require('scalosure.Some');

      /** @constructor*/
      A = function(name) {
        var self = this;
        self.name = name;
      };
      A.prototype.copy$default$1 = function() {
        var self = this;
        return self.name;
      };
      A.unapply = function(x$0) {
        var self = this;
        return (x$0 == null) ? function() {
          return scalosure.None;
        }() : function() {
          return scalosure.Some.$apply(x$0.name);
        }();
      };
      A.$apply = function(name) {
        var self = this;
        return new A(name);
      };
      """}
    }

    it("can mixin traits") { configMap =>

      expect {"""

      package pkg

      trait T {
        val f1 = "foo"
        def m1():Unit
      }

      class A

      class B extends A with T

      """} toBe {"""

      goog.provide('pkg.T');
      goog.provide('pkg.A');
      goog.provide('pkg.B');

      /** @constructor*/
      pkg.T = function() {};
      pkg.T.prototype.f1 = 'foo';
      pkg.T.prototype.m1 = function() {
        var self = this;
        null;
      };

      /** @constructor*/
      pkg.A = function() {var self = this;};

      /** @constructor*/
      pkg.B = function() {
        var self = this;
        pkg.A.call(self);
        self.f1 = pkg.T.prototype.f1;
      };
      goog.inherits(pkg.B, pkg.A);
      pkg.B.prototype.m1 = pkg.T.prototype.m1;

      """}
    }

    it("can have constructors arguments") { configMap =>

      expect {"""

      package $pkg {
        class A()
        class B(x:String)
        class C(x:String, y:String)
      }

      """} toBe {"""

      goog.provide('$pkg.A');
      goog.provide('$pkg.B');
      goog.provide('$pkg.C');

      /** @constructor*/
      $pkg.A = function() {
        var self = this;
      };

      /** @constructor*/
      $pkg.B = function(x) {
        var self = this;
        self.x = x;
      };

      /** @constructor*/
      $pkg.C = function(x,y) {
        var self = this;
        self.x = x;
        self.y = y;
      };
      """}
    }

    it("can have methods") { configMap =>

      expect {"""

      package $pkg {
        class A {
          def m1() {}
          def m2(x:String) {}
          def m3(x:String, y:String) {}
        }
      }

      """} toBe {"""

      goog.provide('$pkg.A');

      /** @constructor*/
      $pkg.A = function() {
        var self = this;
      };

      $pkg.A.prototype.m1 = function() {var self = this;};
      $pkg.A.prototype.m2 = function(x) {var self = this;};
      $pkg.A.prototype.m3 = function(x,y) {var self = this;};

      """}
    }

    it("can have fields") { configMap =>

      expect {"""

      package $pkg {
        class A {
          val f1 = "f1"
          var f2 = null
        }
      }

      """} toBe {"""

      goog.provide('$pkg.A');

      /** @constructor*/
      $pkg.A = function() {var self = this;
        self.f1 = 'f1';
        self.f2 = null;
      };

      """}
    }

    it("can inherit from another class") { configMap =>

      expect {"""

      package $pkg {
        class A
        class B(var x:String) extends A
        class C(x:String, y:String) extends B(x)
      }

      """} toBe {"""

      goog.provide('$pkg.A');
      goog.provide('$pkg.B');
      goog.provide('$pkg.C');

      /** @constructor*/
      $pkg.A = function() {var self = this;};

      /** @constructor*/
      $pkg.B = function(x) {
        var self = this;
        $pkg.A.call(self);
        self.x = x;
      };
      goog.inherits($pkg.B, $pkg.A);

      /** @constructor*/
      $pkg.C = function(x,y) {
        var self = this;
        $pkg.B.call(self,x);
        self.y = y;
      };
      goog.inherits($pkg.C, $pkg.B);

      """}
    }

    it("can have default arguments for constructors") { configMap =>

      expect {"""
      package $pkg {
        class A(x:String = "")
        class B extends A
      }
      """} toBe {"""

      goog.provide('$pkg.A');
      goog.provide('$pkg.B');

      /** @constructor*/
      $pkg.A = function(x) {
        var self = this;
        if (typeof(x) === 'undefined') { x = ''; };
        self.x = x;
      };

      /** @constructor*/
      $pkg.B = function() {
        var self = this;
        $pkg.A.call(self);
      };
      goog.inherits($pkg.B, $pkg.A);
      """}

    }

    it("can have a constructor body") { configMap =>

      expect {"""

      class A(x:String) {
        val y:String = ""
        var z:String = ""
        z = "what"
        println("foo"+z)
      }

      """} toBe {"""

      goog.provide('A');

      /** @constructor*/
      A = function(x) {
        var self = this;
        self.x = x;
        self.y = '';
        self.z = '';
        self.z = 'what';
        console.log(('foo' + self.z));
      };
      """}

    }
  }

  describe("objects") {

    it("can have methods") { configMap =>

      expect {"""
      package $pkg {
        object a {
          def m1() {}
        }
      }
      """} toBe {"""
      goog.provide('$pkg.a');
      $pkg.a.m1 = function() {var self = this;};
      """}
    }

    it("can have variables") { configMap =>

      expect {"""
      package $pkg {
        object a {
          val x = "foo"
        }
      }
      """} toBe {"""
      goog.provide('$pkg.a');
      $pkg.a.x = 'foo';
      """}
    }

    it("can be package objects") { configMap =>

      expect {"""

      package p1

      package object o1 {
        val f1 = ""
        def m1() {
          println(f1) 
        }
      }

      """} toBe {"""

      goog.provide('p1.o1');
      p1.o1.f1 = '';
      p1.o1.m1 = function() {var self = this;console.log(p1.o1.f1);};

      """}
    }
  }

  describe("misc") {

    it("println should convert to console log") { configMap =>

      expect {"""
      object a {
        def m1() {
          println("f")
        }
      }
      """} toBe {"""
      goog.provide('a');
      a.m1 = function() {var self = this;
      console.log('f');
    };
    """}
  }

  it("export functions that have been annotated") { configMap =>

    expect {"""
    package $pkg

    object a {
      @s2js.ExportSymbol
      def m1() {}
    }
    """} toBe {"""
    goog.provide('$pkg.a');
    $pkg.a.m1 = function() {var self = this;};
    goog.exportSymbol('$pkg.a.m1', $pkg.a.m1);
    """}


  }
}
}
