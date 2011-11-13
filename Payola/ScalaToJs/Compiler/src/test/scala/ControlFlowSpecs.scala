package s2js


class ControlFlowSpecs extends CompilerFixtureSpec
{

    it("can have while loops") {
        configMap =>

            expect {
                """
                    object a {
                        def m1() {
                            var x = 0
                            while(x < 10) {
                                x = x + 1
                                println(x)
                            }
                        }
                    }
                """
            } toBe {
                """
                    goog.provide('a');
                    a.m1 = function() {
                        var self = this;
                        var x = 0;
                        while((x < 10)) {
                            x = (x + 1);
                            console.log(x);
                        };
                    };
                """
            }
    }

    describe("foreach statement") {
        ignore("delegates to closure foreach support") {
            configMap =>
                expect {
                    """
                        object a {
                            def m1() {
                                val xs = Array("one", "two")
                                xs.foreach {
                                    x => println(x)
                                }
                            }
                        }
                    """
                } toBe {
                    """
                        goog.provide('a');
                        goog.require('goog.array');
                        a.m1 = function() {
                            var self = this;
                            var xs = ['one','two'];
                            goog.array.forEach(xs, function(x) {
                                console.log(x);
                            }, self);
                        };
                    """
                }
        }
    }

    describe("for statements") {

        ignore("can iterate arrays") { configMap =>
            expect {
                """

                    object a {
                        def m1() = {
                            for(x <- 0 to 2) {
                                println("foo"+x)
                            }
                        }
                    }

                """
            } toBe {
                """
                    goog.provide('a');
                    a.m1 = function(x) {
                        var self = this;
                    };
                """
            }
        }
    }

    describe("if statements") {

        it("can have assignments") {
            configMap =>

                expect {
                    """

                    object o1 {
                        def m1() {
                            var x = ""
                            if(x == "") {
                                x = "default"
                            } else {
                                println("what")
                            }
                        }
                    }

                    """
                } toBe {
                    """

                    goog.provide('o1');
                    o1.m1 = function() {
                        var self = this;
                        var x = '';
                        (x == '') ? function() {x = 'default';}() : function() {console.log('what');}();
                    };

                    """
                }
        }

        it("can have return values") {
            configMap =>

                expect {
                    """
                    object o1 {
                        def m1():String = "fooy"
                        def m2(x:String) {
                            val y = if(x == "foo") {
                                println("was foo")
                            } else {
                                println("was not")
                                m1
                            }
                        }
                    }
                    """
                } toBe {
                    """
                    goog.provide('o1');
                    o1.m1 = function() {var self = this;return 'fooy';};
                    o1.m2 = function(x) {
                        var self = this;
                        var y = (x == 'foo') ? function() {return console.log('was foo');}() : function() {console.log('was not');return o1.m1();}();
                    };
                    """
                }
        }

        it("can have else if statements") { configMap =>
            pending
        }
    }

    describe("match statements") {

        it("can be return a value") {
            configMap =>

                expect {
                    """
                    object o1 {
                      def m1(x:String) {
                        x match {
                          case "0" => println("zero")
                          case "1" => println("one")
                          case _ => println("none")
                        }
                      }
                    }
                    """
                } toBe {
                    """
                    goog.provide('o1');
                    o1.m1 = function(x) {
                        var self = this;
                        return function() {var matched;
                          if(x == '0') {
                            return console.log('zero')
                          }else if(x == '1') {
                            return console.log('one')
                          } else {
                            return console.log('none')
                          }
                        }()
                    };
                    """
                }
        }

        it("can be side effecting") {
            configMap =>

                expect {
                    """
                    object o1 {
                        def m1(x:String) {
                            val y = x match {
                                case "0" => println("zero")
                                case "1" => println("one")
                                case _ => println("none")
                            }
                        }
                    }
                    """
                } toBe {
                    """
                    goog.provide('o1');
                    o1.m1 = function(x) {
                      var self = this;
                      var y = function() {var matched;
                        if(x == '0') {
                          return console.log('zero')
                        }else if(x == '1') {
                          return console.log('one')
                        } else {
                          return console.log('none')
                        }
                      }();
                    };
                    """
                }
        }
    }
}

