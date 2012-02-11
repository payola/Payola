package s2js.compiler


class LiteralSpecs extends CompilerFixtureSpec
{
    describe("Literals") {
        it("null supported") {
            configMap =>
                scalaCode {
                    """
                        package p
                        object `package`  {
                            def a() {
                                null
                            }
                        }
                    """
                } shouldCompileTo {
                    """
                        goog.provide('p');

                        p.a = function() {
                            var self = this;
                            null;
                        };
                        p.metaClass_ = new s2js.MetaClass('p', []);
                    """
                }
        }

        it("booleans supported") {
            configMap =>
                scalaCode {
                    """
                        package p
                        object `package` {
                            def a() {
                                true
                                false
                            }
                        }
                    """
                } shouldCompileTo {
                    """
                        goog.provide('p');

                        p.a = function() {
                            var self = this;
                            true;
                            false;
                        };
                        p.metaClass_ = new s2js.MetaClass('p', []);
                    """
                }
        }

        it("numbers supported") {
            configMap =>
                scalaCode {
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
                } shouldCompileTo {
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
                        p.metaClass_ = new s2js.MetaClass('p', []);
                    """
                }
        }

        it("chars supported") {
            configMap =>
                scalaCode {
                    """
                        package p
                        object `package` {
                            def a() {
                                'x'
                            }
                        }
                    """
                } shouldCompileTo {
                    """
                        goog.provide('p');

                        p.a = function() {
                            var self = this;
                            'x';
                        };
                        p.metaClass_ = new s2js.MetaClass('p', []);
                    """
                }
        }

        it("strings supported") {
            configMap =>
                scalaCode {
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
                } shouldCompileTo {
                    """
                        goog.provide('p');

                        p.a = function() {
                            var self = this;
                            'asdfghjkl';
                            '\'';
                            '\\';
                            '\\\'';
                        };
                        p.metaClass_ = new s2js.MetaClass('p', []);
                    """
                }
        }
    }
}
