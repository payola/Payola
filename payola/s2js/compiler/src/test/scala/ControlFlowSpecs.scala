package s2js.compiler


class ControlFlowSpecs extends CompilerFixtureSpec
{
    describe("Loop statements") {
        it("while is supported") {
            configMap =>
                expect {
                    """
                        import s2js.adapters.js.browser._

                        object a {
                            def m1() {
                                var x = 0
                                while(x < 10) {
                                    x = x + 1
                                    window.alert(x)
                                }
                            }
                        }
                    """
                } toBe {
                    """
                        goog.provide('a');

                        a = {};
                        a.m1 = function() {
                            var self = this;
                            var x = 0;
                            while((x < 10)) {
                                x = (x + 1);
                                window.alert(x);
                            };
                        };
                    """
                }
        }

        it("for is supported") {
            configMap =>
                expect {
                    """
                        import s2js.adapters.js.browser._

                        object a {
                            def m1() = {
                                for(x <- 0 to 2) {
                                    window.alert("foo" + x)
                                }
                            }
                        }
                    """
                } toBe {
                    """
                        goog.provide('a');

                        a = {};
                        a.m1 = function() {
                            var self = this;
                            scala.Predef.intWrapper(0).to(2).foreach(function(x) { window.alert(('foo' + x)); });
                        };
                    """
                }
        }
    }

    describe("If statements") {
        it("can contain assignments") {
            configMap =>
                expect {
                    """
                        object o1 {
                            def m1() {
                                var x = ""
                                if(x == "") {
                                    x = "default"
                                } else {
                                    x = "non-default"
                                }
                            }
                        }
                    """
                } toBe {
                    """
                        goog.provide('o1');

                        o1 = {};
                        o1.m1 = function() {
                            var self = this;
                            var x = '';
                            (function() {
                                if ((x == '')) {
                                    x = 'default';
                                } else {
                                    x = 'non-default';
                                }
                            })();
                        };
                    """
                }
        }

        it("can return values") {
            configMap =>
                expect {
                    """
                        import s2js.adapters.js.browser._

                        object o1 {
                            def m1(): String = "fooy"
                            def m2(x: String) {
                                val y: String = if(x == "foo") {
                                    window.alert("was foo")
                                    x
                                } else {
                                    window.alert("was not")
                                    m1
                                }
                            }
                        }
                    """
                } toBe {
                    """
                        goog.provide('o1');

                        o1 = {};
                        o1.m1 = function() {
                            var self = this;
                            return 'fooy';
                        };
                        o1.m2 = function(x) {
                            var self = this;
                            var y = (function() {
                                if ((x == 'foo')) {
                                    window.alert('was foo');
                                    return x;
                                } else {
                                    window.alert('was not');
                                    return self.m1();
                                }
                            })();
                        };
                    """
                }
        }

        it("can have else if statements") {
            configMap =>
                expect {
                    """
                        object o1 {
                            def m(x: String): String = {
                                if (x == "foo") {
                                    "it was foo"
                                } else if (x == "bar") {
                                    "it was bar"
                                } else if (x == "baz") {
                                    "it was baz"
                                } else {
                                    "it was something else"
                                }
                            }
                        }
                    """
                } toBe {
                    """
                        goog.provide('o1');

                        o1 = {};
                        o1.m = function(x) {
                            var self = this;
                            return (function() {
                                if ((x == 'foo')) {
                                    return 'it was foo';
                                } else {
                                    return (function() {
                                        if ((x == 'bar')) {
                                            return 'it was bar';
                                        } else {
                                            return (function() {
                                                if ((x == 'baz')) {
                                                    return 'it was baz';
                                                } else {
                                                    return 'it was something else';
                                                }
                                            })();
                                        }
                                    })();
                                }
                            })();
                        };
                    """
                }
        }
    }
}

