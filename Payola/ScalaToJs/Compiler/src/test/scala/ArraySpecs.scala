package s2js


class ArraySpecs extends CompilerFixtureSpec
{

    describe("arrays") {

        ignore("can be iterated using while statement") {
            configMap =>

                expect {
                    """

                    object o {
                        def m1() = {
                            val xs = scalosure.JsArray("one", "two", "three")
                            var i = 0
                            while(i < xs.length) {
                                println(xs(i))
                                i = i + 1
                            }
                        }
                    }

                    """
                } toBe {
                    """
                    goog.provide('o');
                    o.m1 = function() {
                        var self = this;
                        var xs = ['one','two','three'];
                        var i = 0;
                        while((i < xs.length)) {
                            console.log(xs[i]);
                            i = (i + 1);
                        };
                    };

                    """
                }
        }

        it("can have strings") {
            configMap =>

                expect {
                    """
                        object a {
                            def m1() {
                                val xs = Array("one", "two")
                            }
                        }
                    """
                } toBe {
                    """
                        goog.provide('a');
                        a.m1 = function() {
                            var self = this;
                            var xs = ['one','two'];
                        };
                    """
                }
        }

        ignore("can have numbers") {
            configMap =>

                expect {
                    """
                        object a {
                            def m1() {
                                val xs = Array(1, 2)
                            }
                        }
                    """
                } toBe {
                    """
                        goog.provide('a');
                        a.m1 = function() {
                            var self = this;
                            var xs = [1,2];
                        };
                    """
                }
        }

        it("can have function call elements") {
            configMap =>

                expect {
                    """

                    object o1 {
                        def m1() = "foo"
                        def m2() = "bar"
                        val v1 = Array(m1, m2)
                    }

                    """
                } toBe {
                    """

                    goog.provide('o1');
                    o1.m1 = function() {
                        var self = this;
                        return 'foo';
                    };
                    o1.m2 = function() {
                        var self = this;
                        return 'bar';
                    };
                    o1.v1 = [o1.m1(),o1.m2()];

                    """
                }
        }
    }
}

// vim: set ts=4 sw=4 et:
