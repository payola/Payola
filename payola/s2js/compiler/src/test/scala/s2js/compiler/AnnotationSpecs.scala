package s2js.compiler

class AnnotationSpecs extends CompilerFixtureSpec
{
    describe("Annotations") {
        it("native class implementation is supported") {
            configMap =>
                scalaCode {
                    """
                        @s2js.compiler.javascript(""" + "\"\"\"" + """
                            A = function() {
                                this.x = 'foo';
                                window.alert('a created');
                            }
                        """ + "\"\"\"" + """)
                        class A
                    """
                } shouldCompileTo {
                    """
                        s2js.runtime.client.core.get().classLoader.provide('A');

                        A = function() {
                            this.x = 'foo';
                            window.alert('a created');
                        }
                    """
                }
        }

        it("native method implementation is supported") {
            configMap =>
                scalaCode {
                    """
                        class A {
                            val x = "foo"
                            val y = 123

                            @s2js.compiler.javascript(""" + "\"\"\"" + """
                                console.log(self.x + self.y.toString + x);
                            """ + "\"\"\"" + """)
                            def m(x: String) {}
                        }
                    """
                } shouldCompileTo {
                    """
                        s2js.runtime.client.core.get().classLoader.provide('A');

                        A = function() {
                            var self = this;
                            self.x = 'foo';
                            self.y = 123;
                        };
                        A.prototype.m = function(x) {
                            var self = this;
                            console.log(self.x + self.y.toString + x);
                        };
                        A.prototype.__class__ = new s2js.runtime.client.core.Class('A', []);
                    """
                }
        }

        it("native val value is supported") {
            configMap =>
                scalaCode {
                    """
                        class A {
                            @s2js.compiler.javascript("[1, 2, 3]")
                            val x = ""
                        }
                    """
                } shouldCompileTo {
                    """
                        s2js.runtime.client.core.get().classLoader.provide('A');

                        A = function() {
                            var self = this;
                            self.x = [1, 2, 3];
                        };
                        A.prototype.__class__ = new s2js.runtime.client.core.Class('A', []);
                    """
                }
        }
    }
}
