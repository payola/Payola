package s2js.runtime.java.lang

import s2js.compiler.javascript

class String
{
    @javascript("return self.length;")
    def length = 0
}
