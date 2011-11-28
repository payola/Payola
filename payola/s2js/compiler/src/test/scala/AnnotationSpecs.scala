package s2js.compiler

class AnnotationSpecs extends CompilerFixtureSpec {
    describe("Annotations") {
        it("native class implementation is supported") {
            configMap =>
                expect {
                    """
                        @s2js.compiler.Native(""" + "\"\"\"" + """
                            A = function() {
                                this.x = 'foo';
                                window.alert('a created');
                            }
                        """ + "\"\"\"" + """)
                        class A
                    """
                } toBe {
                    """
                        goog.provide('A');

                        A = function() {
                            this.x = 'foo';
                            window.alert('a created');
                        }
                    """
                }
        }

        it("native method implementation is supported") {
            configMap =>
                expect {
                    """
                        class A {
                            val x = "foo"
                            val y = 123

                            @s2js.compiler.Native(""" + "\"\"\"" + """
                                console.log(self.x + self.y.toString + x);
                            """ + "\"\"\"" + """)
                            def m(x: String) {}
                        }
                    """
                } toBe {
                    """
                        goog.provide('A');

                        A = function() {
                            var self = this;
                            self.x = 'foo';
                            self.y = 123;
                        };
                        A.prototype.m = function(x) {
                            var self = this;
                            console.log(self.x + self.y.toString + x);
                        };
                    """
                }
        }

        it("native val value is supported") {
            configMap =>
                expect {
                    """
                        class A {
                            @s2js.compiler.Native("[1, 2, 3]")
                            val x = ""
                        }
                    """
                } toBe {
                    """
                        goog.provide('A');

                        A = function() {
                            var self = this;
                            self.x = [1, 2, 3];
                        };
                    """
                }
        }
    }
}
