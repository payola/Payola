package s2js.compiler


class LiteralSpecs extends CompilerFixtureSpec
{
    describe("Literals") {
        it("null supported") {
            configMap =>
                expect {
                    """
                        package p
                        object `package`  {
                            def a() {
                                null
                            }
                        }
                    """
                } toBe {
                    """
                        goog.provide('p');

                        p.a = function() {
                            var self = this;
                            null;
                        };
                    """
                }
        }

        it("booleans supported") {
            configMap =>
                expect {
                    """
                        package p
                        object `package` {
                            def a() {
                                true
                                false
                            }
                        }
                    """
                } toBe {
                    """
                        goog.provide('p');

                        p.a = function() {
                            var self = this;
                            true;
                            false;
                        };
                    """
                }
        }

        it("numbers supported") {
            configMap =>
                expect {
                    """
                        package p
                        object `package` {
                            def a() {
                                1234
                                574.432
                                0
                                -5
                                -424.45
                            }
                        }
                    """
                } toBe {
                    """
                        goog.provide('p');

                        p.a = function() {
                            var self = this;
                            1234;
                            574.432;
                            0;
                            -5;
                            -424.45;
                        };
                    """
                }
        }

        it("chars supported") {
            configMap =>
                expect {
                    """
                        package p
                        object `package` {
                            def a() {
                                'x'
                            }
                        }
                    """
                } toBe {
                    """
                        goog.provide('p');

                        p.a = function() {
                            var self = this;
                            'x';
                        };
                    """
                }
        }

        it("strings supported") {
            configMap =>
                expect {
                    """
                        package p
                        object `package` {
                            def a() {
                                "asdfghjkl"
                                "'"
                                "\\"
                                "\\'"
                            }
                        }
                    """
                } toBe {
                    """
                        goog.provide('p');

                        p.a = function() {
                            var self = this;
                            'asdfghjkl';
                            '\'';
                            '\\';
                            '\\\'';
                        };
                    """
                }
        }
    }
}