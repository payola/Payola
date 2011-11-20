package s2js

class FunctionSpecs extends CompilerFixtureSpec
{

    describe("tests") {
        it("test2") {
            configMap =>
                expect {
                    """
                        package p

                        class A

                        class B(val b1: String, val b2: Int)

                        class C extends A

                        class D(val d1: String, val d2: Int) extends A

                        class E(b1: String, b2: Int) extends B(b1, b2)

                        class F(b1: String, b2: Int, val f3: Boolean) extends B(b1, b2)

                        class G extends B("test", 1234)

                        class H(val h1: String = "default") extends A

                        import js.browser._

                        class I
                        {
                            window.alert("dasd dad as test")
                            window.alert("dasd dad as test 2")
                            window.alert("dasd dad as test 3")
                            if ("str".contains("tr")) {
                                window.alert("dasd dad as test 4")
                            } else {
                                window.alert("dasd dad as test 5")
                            }
                        }

                        class J(val j1: String, j2: String) {
                            val j3: String = "aaaaa"
                        }
                    """
                } toBe {
                    ""
                }
        }


        /*it("test1") {
            configMap =>
                expect {
                    """
                        package a

                        import js.browser._

                        object `package` extends Test("package") {
                            def m1(x:String): Int = { window.alert(3); 5 }
                            val aaa = "ahoj"
                            var bbb = 123
                            val ccc = new Test("labla")
                        }

                        class Test(val v1: String)
                        object test extends Test("ahoj") {
                            def callPackageMethod = {
                                a.m1("calling")
                            }
                        }

                        class Test2 extends C

                        object test2 extends Test2 {
                            class Test2Inner
                        }

                        object test3

                        package b.c.d {
                            class EEEE
                        }

                        package b {
                            class EEEE
                        }

                        package b {
                            trait A
                            object B
                            class C extends sub.B.AAAA with sub.B.BBBB

                            package sub {
                                trait A {
                                    class AAAA extends BBBB
                                    trait BBBB
                                    object CCCC
                                }
                                object B {
                                    class AAAA extends a.Test2 with a.b.A with a.b.sub.B.BBBB with a.c.d.e.A with a.A
                                    trait BBBB
                                    object CCCC
                                }
                                class C {
                                    class AAAA
                                    trait BBBB
                                    object CCCC
                                }
                            }
                        }

                        package c.d.e {
                            trait A
                            object B
                            class C

                            package sub {
                                trait A
                                object B
                                class C
                            }
                        }

                        trait A
                        object B
                        class C
                    """
                } toBe {
                    """
                    
                    """
                }
        }*/
    }
}
