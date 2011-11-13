package s2js


class XmlLiteralSpec extends CompilerFixtureSpec
{

    describe("xml literals") {

        it("simple tags") {
            configMap =>

                expect {
                    """
                        object a {
                          val x = s2js.Html(<h1>hello</h1>)
                        }
                    """
                } toBe {
                    """
                        goog.provide('a');
                        a.x = goog.dom.createDom('h1',{},['hello']);
                    """
                }
        }

        it("can have literal tags") {
            configMap =>

                expect {
                    """
                        object a {
                          val x = s2js.Html(<body>
                            <h1 class="foo">testing <strong>xml</strong></h1>
                            <ul><li>one</li><li>two</li></ul>
                          </body>)
                        }
                    """
                } toBe {
                    """
                        goog.provide('a');
                        a.x = goog.dom.createDom('body',{},[
                            goog.dom.createDom('h1',{'class':'foo'},['testing ',goog.dom.createDom('strong',{},['xml'])]),
                            goog.dom.createDom('ul',{},[
                                goog.dom.createDom('li',{},['one']),
                                goog.dom.createDom('li',{},['two'])])]);
                    """
                }
        }

        it("can have variable children") {
            configMap =>

                expect {
                    """
                        object a {
                          val x = "hello"
                          def m1() {
                              val y = "world"
                              val z = s2js.Html(<tr><td>{x}</td><td>{y}</td></tr>)
                          }
                        }
                    """
                } toBe {
                    """
                        goog.provide('a');
                        a.x = 'hello';
                        a.m1 = function() {var self = this;
                            var y = 'world';
                            var z = goog.dom.createDom('tr',{},[goog.dom.createDom('td',{},[a.x]),goog.dom.createDom('td',{},[y])]);
                        };
                    """
                }
        }

        it("can have variable attributes") {
            configMap =>

                expect {
                    """

                    object o1 {
                        val v1 = "foo"
                        val v2 = s2js.Html(<span class={v1}>foo</span>)
                        def m1(url:String) = {
                             s2js.Html(<a href={url}>foo</a>)
                        }
                    }

                    """
                } toBe {
                    """

                    goog.provide('o1');
                    o1.v1 = 'foo';
                    o1.v2 = goog.dom.createDom('span',{'class':o1.v1},['foo']);
                    o1.m1 = function(url) {
                        var self = this;
                        return goog.dom.createDom('a',{'href':url},['foo']);
                    };

                    """
                }
        }

        it("can have expresssions") {
            configMap =>

                expect {
                    """
                        object a {
                          val y = "hello, world"
                          val x = s2js.Html(<h1>{a.y + 5}</h1>)
                        }
                    """
                } toBe {
                    """
                        goog.provide('a');
                        a.y = 'hello, world';
                        a.x = goog.dom.createDom('h1',{},[(a.y + 5)]);
                    """
                }
        }

        it("can have function calls") {
            configMap =>

                expect {
                    """
                        object a {
                          def m1() = "hello, world"
                          val x = s2js.Html(<h1>{m1()}</h1>)
                        }
                    """
                } toBe {
                    """
                        goog.provide('a');
                        a.m1 = function() {var self = this;return 'hello, world';};
                        a.x = goog.dom.createDom('h1',{},[a.m1()]);
                    """
                }
        }
    }
}

// vim: set ts=4 sw=4 et:
