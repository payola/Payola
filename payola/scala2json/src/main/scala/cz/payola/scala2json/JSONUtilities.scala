package cz.payola.scala2json

object JSONUtilities {

    /** Returns a char escaped so that it can
     *  be used in the JSON output right away.
     *
     * @param c Char to be escaped.
     *
     * @return Escaped char.
     */
    def escapedChar(c: Char): String = {
        escapedString(c.toString)
    }

    /** Returns a string escaped and wrapped in quotes so that it can
     *  be used in the JSON output right away.
     *
     * @param str String to be escaped.
     *
     * @return Escaped string.
     */
    def escapedString(str: String): String = {
        val builder: StringBuilder = new StringBuilder(str)

        // Replace all invalid chars, see http://www.json.org/
        builder.replaceAllLiterally("\\", "\\\\")
        builder.replaceAllLiterally("\"", "\\\"")
        builder.replaceAllLiterally("/", "\\/")
        builder.replaceAllLiterally("\b", "\\b")
        builder.replaceAllLiterally("\f", "\\f")
        builder.replaceAllLiterally("\n", "\\n")
        builder.replaceAllLiterally("\r", "\\r")
        builder.replaceAllLiterally("\t", "\\t")

        // Insert quotes around the string
        builder.insert(0, '"')
        builder.append("\"")
        builder.toString
    }
}
