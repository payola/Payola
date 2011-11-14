package s2js


class MiscSpecs extends CompilerFixtureSpec
{

    it("ignore browser namespace") {
        configMap =>

            expect {
                """
                import browser._
                object o {
                  def start() {
                    val x = new XMLHttpRequest
                  }
                }
                """
            } toBe {
                """
                goog.provide('o');
                o.start = function() {
                  var self = this;
                  var x = new XMLHttpRequest();
                };
                """
            }
    }

    it("can use arrowassoc for object literals") {
        configMap =>

            expect {
                """

                import s2js.JsObject

                class HashMap[A](u:JsObject[A])

                object HashMap {
                  def apply[A](e:(String,A)*) = new HashMap(JsObject(e:_*))
                }

                object o {
                  def start() {
                    val m1 = new HashMap(JsObject("one"->"foo","two"->"bar"))
                    val m2 = HashMap("one"->"foo","two"->"bar")
                  }
                }
                """
            } toBe {
                """
                goog.provide('HashMap');
                goog.provide('o');
                /** @constructor*/
                HashMap = function(u) {
                  var self = this;
                  self.u = u;
                };
                HashMap.appli = function(e) {
                  var self = this;
                  return new HashMap(e);
                };
                o.start = function() {
                  var self = this;
                  var m1 = new HashMap({'one':'foo','two':'bar'});
                  var m2 = HashMap.appli({'one':'foo','two':'bar'});
                };
                """
            }
    }
    it("supports literal scripts") {
        configMap =>

            expect {
                """
                import scalosure._
                object o {
                  def start() {
                    script.literal("console.log('foo')")
                  }
                }
                """
            } toBe {
                """
                goog.provide('o');
                o.start = function() {
                  var self = this;
                  console.log('foo');
                };
                """
            }
    }

    ignore("can use native javascript arrays") {
        configMap =>

            expect {
                """

                import s2js.JsArray

                object o {
                  def m1() {
                    val xs = JsArray("one", "two")
                    xs.forEach((x, y, z) => {
                      println(x)
                    })
                  }
                }

                """
            } toBe {
                """
                goog.provide('o');
                goog.require('s2js.JsArray');

                o.m1 = function() {
                  var self = this;
                  var xs = ['one','two'];
                  xs.forEach(function(x,y,z) {console.log(x);});
                };

                """
            }
    }

    it("can have multiple arguments lists") {
        configMap =>

            expect {
                """

                object o1 {

                  def m1(name:String)(fn:(String) => Unit) {
                    fn(name)
                  }

                  def m3() {

                    m1("foo") {
                      x => println(x)
                    }
                  }
                }

                """
            } toBe {
                """

                goog.provide('o1');

                o1.m1 = function(name,fn) {
                  var self = this;
                  fn(name);
                };

                o1.m3 = function() {
                  var self = this;
                  o1.m1('foo',function(x) {console.log(x);});
                };

                """
            }
    }

    it("support implicit conversions") {
        configMap =>

            expect {
                """

                class B(name:String) {
                  def doit() {}
                }

                object o {

                  implicit def string2b(a:String):B = new B(a)

                  def m1() {
                    val a = "foo"
                    a.doit()
                  }
                }

                """
            } toBe {
                """

                goog.provide('B');
                goog.provide('o');

                /** @constructor*/
                B = function(name) {
                  var self = this;
                  self.name = name;
                };

                B.prototype.doit = function() {
                  var self = this;
                };

                o.string2b = function(a) {
                  var self = this;
                  return new B(a);
                };

                o.m1 = function() {
                  var self = this;
                  var a = 'foo';
                  o.string2b(a).doit();
                };

                """
            }
    }
}
