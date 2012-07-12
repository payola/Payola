package cz.payola.scala2json

/** A few helper methods that are used by the JSONSerializer for escaping strings
  * or characters,
  */
object JSONUtilities
{
    /** Returns a char escaped so that it can
      *  be used in the JSON output right away.
      *
      * @param c Char to be escaped.
      *
      * @return Escaped char.
      */
    def escapeChar(c: Char): String = {
        escapeString(c.toString)
    }

    /** Returns a string escaped and wrapped in quotes so that it can
      *  be used in the JSON output right away.
      *
      * @param str String to be escaped.
      *
      * @return Escaped string.
      */
    def escapeString(str: String): String = {
        val builder: StringBuilder = new StringBuilder

        builder.append('"')
        for (i: Int <- 0 until str.length) {
            val c: Char = str(i)
            c match {
                case '\\' => builder.append("\\\\")
                case '"' => builder.append("\\\"")
                case '/' => builder.append("\\/")
                case '\b' => builder.append("\\b")
                case '\f' => builder.append("\\f")
                case '\n' => builder.append("\\n")
                case '\r' => builder.append("\\r")
                case '\t' => builder.append("\\t")
                case c => builder.append(c)
            }
        }

        builder.append('"')

        builder.toString
    }

    /** Pads every line with a tab.
      *
      * @param str String to be padded.
      * @return Padded string
      */
    def padStringWithTab(str: String): String = {
        str.replaceAllLiterally("\n", "\n\t")
    }
}
