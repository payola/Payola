package s2js.runtime.client.scala.collection.immutable

import s2js.compiler.javascript

object StringOps extends s2js.runtime.client.scala.collection.SeqCompanion[StringOps]
{
    def empty = new StringOps("")

    @javascript("return self.fromJsArray(xs.getInternalJsArray());")
    def apply(xs: Any*): Any = null
}

class StringOps(val str: java.lang.String) extends s2js.runtime.client.scala.collection.Seq
{
    val x = Option(str).getOrElse("")

    initializeInternalJsArray(x)

    def newInstance = StringOps.empty

    @javascript("""self.setInternalJsArray(value.split(''))""")
    def initializeInternalJsArray(value: String) {}

    @javascript("return self.getInternalJsArray().join('');")
    def repr: String = ""

    @javascript("return self.x == 'true';")
    def toBoolean: Boolean = false

    @javascript("return parseInt(self.x);")
    def toByte: Byte = 0

    @javascript("return parseInt(self.x);")
    def toShort: Short = 0

    @javascript("return parseInt(self.x);")
    def toInt: Int = 0

    @javascript("return parseInt(self.x);")
    def toLong: Long = 0

    @javascript("return parseFloat(self.x);")
    def toFloat: Float = 0

    @javascript("return parseFloat(self.x);")
    def toDouble: Double = 0.0

    @javascript("return self.repr().split(pattern);")
    def split(pattern: String): Any = null

    @javascript("return self.x.replace(pattern, replacement);")
    def replaceAllLiterally(pattern: String, replacement: String) = null

    @javascript(
        """
         var copiedArgs = [];
          for(var i=0; i<arguments.length; i++){
              copiedArgs.push(arguments[i]);
          }
         try {
             return vsprintf(self.str, copiedArgs);
         }catch(err){
            console.error("Error evaluating sprinf on '%s'", self.str);
            return self.str;
         }
        """
    )
    def format(args: Array[Any]) = ""

    override def toString = repr
}
