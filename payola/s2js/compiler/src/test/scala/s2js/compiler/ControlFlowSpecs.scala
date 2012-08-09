package s2js.compiler

class ControlFlowSpecs extends CompilerFixtureSpec
{
    describe("Loop statements") {
        ignore("while is supported") {
            configMap =>
                scalaCode {
                    """
                        import s2js.adapters.browser._

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
                } shouldCompileTo {
                    """
                        s2js.runtime.client.ClassLoader.provide('a');

                        a.m1 = function() {
                            var self = this;
                            var x = 0;
                            while((x < 10)) {
                                x = (x + 1);
                                window.alert(x);
                            };
                        };
                        a.__class__ = new s2js.runtime.client.Class('a', []);
                    """
                }
        }

        ignore("for is supported") {
            configMap =>
                scalaCode {
                    """
                        import s2js.adapters.browser._

                        object a {
                            def m1() = {
                                for(x <- 0 to 2) {
                                    window.alert("foo" + x)
                                }
                            }
                        }
                    """
                } shouldCompileTo {
                    """
                        s2js.runtime.client.ClassLoader.provide('a');

                        a.m1 = function() {
                            var self = this;
                            scala.Predef.intWrapper(0).to(2).foreach(function(x) { window.alert(('foo' + x)); });
                        };
                        a.__class__ = new s2js.runtime.client.Class('a', []);
                    """
                }
        }
    }

    describe("If statements") {
        ignore("are supported") {
            configMap =>
                scalaCode {
                    """
                        object o1 {
                            def m1() {
                                var x = ""
                                if(x == "") {
                                    x = "default"
                                } else {
                                    x = "non-default"
                                }

                                if(x == "default") {
                                    x = "defaultconfirmed"
                                }
                            }
                        }
                    """
                } shouldCompileTo {
                    """
                        s2js.runtime.client.ClassLoader.provide('o1');

                        o1.m1 = function() {
                            var self = this;
                            var x = '';
                            if ((x == '')) {
                                x = 'default';
                            } else {
                                x = 'non-default';
                            }

                            if ((x == 'default')) {
                                x = 'defaultconfirmed';
                            }
                        };
                       o1.__class__ = new s2js.runtime.client.Class('o1', []);
                    """
                }
        }

        ignore("can return values") {
            configMap =>
                scalaCode {
                    """
                        import s2js.adapters.browser._

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
                } shouldCompileTo {
                    """
                        s2js.runtime.client.ClassLoader.provide('o1');

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
                        o1.__class__ = new s2js.runtime.client.Class('o1', []);
                    """
                }
        }

        ignore("can have else if statements") {
            configMap =>
                scalaCode {
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
                } shouldCompileTo {
                    """
                        s2js.runtime.client.ClassLoader.provide('o1');

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
                        o1.__class__ = new s2js.runtime.client.Class('o1', []);
                    """
                }
        }
    }

    describe("Exceptions") {
        ignore("can be thrown") {
            configMap =>
                scalaCode {
                    """
                        class A {
                            def m() {
                                throw new Exception("something bad happened")
                            }
                        }
                    """
                } shouldCompileTo {
                    """
                        s2js.runtime.client.ClassLoader.provide('A');
                        s2js.runtime.client.ClassLoader.require('scala.Exception');

                        A = function() {
                            var self = this;
                        };
                        A.prototype.m = function() {
                            var self = this;
                            (function() {
                                throw new scala.Exception('something bad happened');
                            })();
                        };
                        A.prototype.__class__ = new s2js.runtime.client.Class('A', []);
                    """
                }
        }

        ignore("try is supported") {
            configMap =>
                scalaCode {
                    """
                        class A {
                            def m() {
                                try {
                                    throw new Exception("something bad happened")
                                }
                            }
                        }
                    """
                } shouldCompileTo {
                    """
                        s2js.runtime.client.ClassLoader.provide('A');
                        s2js.runtime.client.ClassLoader.require('scala.Exception');

                        A = function() { var self = this; };
                        A.prototype.m = function() {
                            var self = this;
                            try {
                                (function() {
                                    throw new scala.Exception('something bad happened');
                                })();
                            } catch ($ex$1) {
                                (function() {
                                    throw $ex$1;
                                })();
                            }
                        };
                        A.prototype.__class__ = new s2js.runtime.client.Class('A', []);
                    """
                }
        }

        ignore("catch is supported") {
            configMap =>
                scalaCode {
                    """
                        class A {
                            def m() {
                                var x = "nothing";
                                try {
                                    throw new Exception("something bad happened")
                                } catch {
                                    case _: RuntimeException => x = "runtime exception"
                                    case _: Exception => x = "exeption"
                                    case _ => x = "unknown exception"
                                }
                            }
                        }
                    """
                } shouldCompileTo {
                    """
                        s2js.runtime.client.ClassLoader.provide('A');
                        s2js.runtime.client.ClassLoader.require('scala.Exception');

                        A = function() {
                            var self = this;
                        };
                        A.prototype.m = function() {
                            var self = this;
                            var x = 'nothing';
                            try {
                                (function() {
                                    throw new scala.Exception('something bad happened');
                                })();
                            } catch ($ex$1) {
                                (function() {
                                    if (s2js.runtime.client.isInstanceOf($ex$1, 'scala.RuntimeException')) {
                                        x = 'runtime exception';
                                        return;
                                    }
                                    if (s2js.runtime.client.isInstanceOf($ex$1, 'scala.Exception')) {
                                        x = 'exeption';
                                        return;
                                    }
                                    if (true) {
                                        x = 'unknown exception';
                                        return;
                                    }
                                    throw $ex$1;
                                })();
                            }
                        };
                        A.prototype.__class__ = new s2js.runtime.client.Class('A', []);
                    """
                }
        }

        ignore("try/catch with return value is supported") {
            configMap =>
                scalaCode {
                    """
                        class A {
                            def m() {
                                val x = try {
                                    "success"
                                } catch {
                                    case _: RuntimeException => "runtime exception"
                                    case _: Exception => "exeption"
                                    case _ => "unknown exception"
                                }
                            }
                        }
                    """
                } shouldCompileTo {
                    """
                        s2js.runtime.client.ClassLoader.provide('A');

                        A = function() { var self = this; };

                        A.prototype.m = function() {
                            var self = this;
                            var x = (function() {
                                var $tryReturnValue$1 = undefined;
                                try {
                                    $tryReturnValue$1 = (function() {
                                        return 'success';
                                    })();
                                } catch ($ex$2) {
                                    $tryReturnValue$1 = (function() {
                                        if (s2js.runtime.client.isInstanceOf($ex$2, 'scala.RuntimeException')) {
                                            return 'runtime exception';
                                        }
                                        if (s2js.runtime.client.isInstanceOf($ex$2, 'scala.Exception')) {
                                            return 'exeption';
                                        } if (true) {
                                            return 'unknown exception';
                                        }
                                        throw $ex$2;
                                    })();
                                }
                                return $tryReturnValue$1;
                            })();
                        };
                        A.prototype.__class__ = new s2js.runtime.client.Class('A', []);
                    """
                }
        }
    }

    describe("Match statements") {
        ignore("are supported") {
            configMap =>
                scalaCode {
                    """
                        object o {
                            def m() {
                                "abc" match {
                                    case "a" => 1
                                    case "b" => 2
                                    case _ => 3
                                }
                            }
                        }
                    """
                } shouldCompileTo {
                    """
                        s2js.runtime.client.ClassLoader.provide('o');

                        o.m = function() {
                            var self = this;
                            (function($selector$1) {
                                if ($selector$1 === 'a') {
                                    1;
                                    return;
                                }
                                if ($selector$1 === 'b') {
                                    2;
                                    return;
                                }
                                if (true) {
                                    3;
                                    return;
                                }
                            })('abc');
                        };
                        o.__class__ = new s2js.runtime.client.Class('o', []);
                    """
                }
        }

        ignore("can return value") {
            configMap =>
                scalaCode {
                    """
                        object o {
                            def m(): Int = {
                                "abc" match {
                                    case "a" => 123
                                    case _ => 0
                                }
                            }
                        }
                    """
                } shouldCompileTo {
                    """
                        s2js.runtime.client.ClassLoader.provide('o');

                        o.m = function() {
                            var self = this;
                            return (function($selector$1) {
                                if ($selector$1 === 'a') {
                                    return 123;
                                }
                                if (true) {
                                    return 0;
                                }
                            })('abc');
                        };
                        o.__class__ = new s2js.runtime.client.Class('o', []);
                    """
                }
        }

        ignore("can have alternative patterns") {
            configMap =>
                scalaCode {
                    """
                        object o {
                            def m(): Int = {
                                "abc" match {
                                    case "a" | "b" | "c" => 123
                                    case _ => 0
                                }
                            }
                        }
                    """
                } shouldCompileTo {
                    """
                        s2js.runtime.client.ClassLoader.provide('o');

                        o.m = function() {
                            var self = this;
                            return (function($selector$1) {
                                if (($selector$1 === 'a') || ($selector$1 === 'b') || ($selector$1 === 'c')) {
                                    return 123;
                                }
                                if (true) {
                                    return 0;
                                }
                            })('abc');
                        };
                        o.__class__ = new s2js.runtime.client.Class('o', []);
                    """
                }
        }

        ignore("can have guards") {
            configMap =>
                scalaCode {
                    """
                        object o {
                            def m(): Int = {
                                val x = false
                                "abc" match {
                                    case "a" if x == true => 123
                                    case _ => 0
                                }
                            }
                        }
                    """
                } shouldCompileTo {
                    """
                        s2js.runtime.client.ClassLoader.provide('o');

                        o.m = function() {
                            var self = this;
                            var x = false;
                            return (function($selector$1) {
                                if ($selector$1 === 'a') {
                                    if ((x == true)) {
                                        return 123;
                                    }
                                }
                                if (true) {
                                    return 0;
                                }
                            })('abc');
                        };
                        o.__class__ = new s2js.runtime.client.Class('o', []);
                    """
                }
        }

        ignore("typed patterns are supported") {
            configMap =>
                scalaCode {
                    """
                        object o {
                            def m(p: Any): Int = {
                                p match {
                                    case _: String => 123
                                    case _: Int => 456
                                    case _ => 0
                                }
                            }
                        }
                    """
                } shouldCompileTo {
                    """
                        s2js.runtime.client.ClassLoader.provide('o');

                        o.m = function(p) {
                            var self = this;
                            return (function($selector$1) {
                                if (s2js.runtime.client.isInstanceOf($selector$1, 'scala.String')) {
                                    return 123;
                                }
                                if (s2js.runtime.client.isInstanceOf($selector$1, 'scala.Int')) {
                                    return 456;
                                }
                                if (true) {
                                    return 0;
                                }
                            })(p);
                        };
                        o.__class__ = new s2js.runtime.client.Class('o', []);
                    """
                }
        }

        ignore("basic binding is supported") {
            configMap =>
                scalaCode {
                    """
                        object o {
                            def m(p: Any): Int = {
                                p match {
                                    case x: String => 123
                                    case y @ (_: Int) => 456
                                    case _ => 0
                                }
                            }
                        }
                    """
                } shouldCompileTo {
                    """
                        s2js.runtime.client.ClassLoader.provide('o');

                        o.m = function(p) {
                            var self = this;
                            return (function($selector$1) {
                                if (s2js.runtime.client.isInstanceOf($selector$1, 'scala.String')) {
                                    var x = $selector$1;
                                    return 123;
                                }
                                if (s2js.runtime.client.isInstanceOf($selector$1, 'scala.Int')) {
                                    var y = $selector$1;
                                    return 456;
                                }
                                if (true) {
                                    return 0;
                                }
                            })(p);
                        };
                        o.__class__ = new s2js.runtime.client.Class('o', []);
                    """
                }
        }

        ignore("case class binding is supported") {
            configMap =>
                scalaCode {
                    """
                        object o {
                            def m(p: Any): Int = {
                                p match {
                                    case (_, _, (_, (bound1: Int, bound2@ _))) => 123
                                    case Some((_, Some((_, _, q, _, _)))) => 456
                                    case _ => 0
                                }
                            }
                        }
                    """
                } shouldCompileTo {
                    """
                        s2js.runtime.client.ClassLoader.provide('o');

                        o.m = function(p) {
                            var self = this;
                            return (function($selector$1) {
                                if (s2js.runtime.client.isInstanceOf($selector$1, 'scala.Tuple3') && (true) && (true) &&
                                (s2js.runtime.client.isInstanceOf($selector$1.productElement(2), 'scala.Tuple2') && (true) &&
                                (s2js.runtime.client.isInstanceOf($selector$1.productElement(2).productElement(1), 'scala.Tuple2') &&
                                (s2js.runtime.client.isInstanceOf($selector$1.productElement(2).productElement(1).productElement(0),
                                'scala.Int')) && (true)))) {
                                    var bound1 = $selector$1.productElement(2).productElement(1).productElement(0);
                                    var bound2 = $selector$1.productElement(2).productElement(1).productElement(1);
                                    return 123;
                                }
                                if (s2js.runtime.client.isInstanceOf($selector$1, 'scala.Some') &&
                                (s2js.runtime.client.isInstanceOf($selector$1.productElement(0), 'scala.Tuple2') && (true) &&
                                (s2js.runtime.client.isInstanceOf($selector$1.productElement(0).productElement(1), 'scala.Some') &&
                                (s2js.runtime.client.isInstanceOf($selector$1.productElement(0).productElement(1).productElement(0),
                                'scala.Tuple5') && (true) && (true) && (true) && (true) && (true))))) {

                            var q = $selector$1.productElement(0).productElement(1).productElement(0).productElement(2);
                                    return 456;
                                }
                                if (true) {
                                    return 0;
                                }
                            })(p);
                        };
                        o.__class__ = new s2js.runtime.client.Class('o', []);
                    """
                }
        }
    }
}

