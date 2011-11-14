package s2js


class FunctionSpecs extends CompilerFixtureSpec
{

    describe("functions") {

        it("can be higher-orderd") {
            configMap =>

                expect {
                    """

                    class F {
                      val v1 = "v1"
                      def f1(x:String) = v1 + x.toUpperCase
                    }

                    object o {

                      def f2(f:(String) => String) {
                        println(f("m1"))
                      }

                      def f3(x:String) = "what"+x

                      def start() {
                        val x = new F
                        f2(x.f1)
                        f2(f3)
                        f2 { (x:String) => "no"+x }
                      }
                    }

                    """
                } toBe {
                    """
                    goog.provide('F');
                    goog.provide('o');
                    /** @constructor*/
                    F = function() {
                      var self = this;
                      self.v1 = 'v1';
                    };
                    F.prototype.f1 = function(x) {
                      var self = this;
                      return (self.v1 + x.toUpperCase());
                    };
                    o.f2 = function(f) {
                      var self = this;
                      console.log(f('m1'));
                    };
                    o.f3 = function(x) {
                      var self = this;
                      return ('what' + x);
                    };
                    o.start = function() {
                      var self = this;
                      var x = new F();
                      o.f2(function(_x_) {return x.f1(_x_)});
                      o.f2(function(_x_) {return o.f3(_x_)});
                      o.f2(function(x) {
                        return ('no' + x);
                      });
                    };
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
                        "foo"+"bar"
                      }
                      def m4() {
                        "foo"+"bar"
                      }
                    }

                    """
                } toBe {
                    """
                    goog.provide('a');
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
                    """
                }
        }

        it("can have arguments") {
            configMap =>

                expect {
                    """
                    object a {
                      def m1(x:String) {}
                      def m2(x:String, y:Int) {}
                    }
                    """
                } toBe {
                    """
                    goog.provide('a');
                    a.m1 = function(x) {var self = this;};
                    a.m2 = function(x,y) {var self = this;};
                    """
                }
        }
    }

    describe("class functions") {

        it("override base class functions") {
            configMap =>

                expect {
                    """
                    package $pkg
                    class a {
                      def m1() {}
                      def m2(x:String) {}
                    }
                    class b extends a {
                      override def m1() {super.m1()}
                      override def m2(x:String) {super.m2("foo")}
                    }
                    """
                } toBe {
                    """
                    goog.provide('$pkg.a');
                    goog.provide('$pkg.b');

                    /** @constructor*/
                    $pkg.a = function() {var self = this;};

                    $pkg.a.prototype.m1 = function() {var self = this;};
                    $pkg.a.prototype.m2 = function(x) {var self = this;};

                    /** @constructor*/
                    $pkg.b = function() {
                      var self = this;
                      $pkg.a.call(self);
                    };
                    goog.inherits($pkg.b, $pkg.a);

                    $pkg.b.prototype.m1 = function() {var self = this;$pkg.b.superClass_.m1.call(self);};
                    $pkg.b.prototype.m2 = function(x) {var self = this;$pkg.b.superClass_.m2.call(self,'foo');};
                    """
                }
        }
    }

    describe("anon functions") {

        it("can be assigned to variables") {
            configMap =>

                expect {
                    """
                    object a {
                      val x = (y:String) => { println(y) }
                    }
                    """
                } toBe {
                    """
                    goog.provide('a');
                    a.x = function(y) {console.log(y);};
                    """
                }
        }

        it("can have multiple statements") {
            configMap =>

                expect {
                    """
                    object a {
                      val x = (y:String) => {
                        println("what")
                        println(y)
                      }
                    }
                    """
                } toBe {
                    """
                    goog.provide('a');
                    a.x = function(y) {
                      console.log('what');
                      console.log(y);
                    };
                    """
                }
        }
    }
}
