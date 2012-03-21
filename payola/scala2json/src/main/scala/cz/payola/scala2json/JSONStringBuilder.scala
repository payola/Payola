package cz.payola.scala2json


class JSONStringBuilder(serializer: JSONSerializer, prettyPrint: Boolean, initialValue: String){
    val stringBuilder: StringBuilder = new StringBuilder(initialValue)

    /** Appends an array item to string builder.
      *
      * @param item The item.
      * @param isFirst Whether the field is first - if it is, the comma separator is left out.
      *
      * @return False if the field has been skipped.
      */
    def appendStringArrayItem(item: String, isFirst: Boolean) = {
        if (!isFirst) {
            stringBuilder.append(',')
            if (prettyPrint) {
                stringBuilder.append('\n')
            }
        }

        if (prettyPrint) {
            stringBuilder.append('\t')
        }

        stringBuilder.append(item)
    }

    def appendArrayItem(item: Any, isFirst: Boolean) = {
        var serializedObj: String = serializer.serializeObject(item)
        if (prettyPrint) {
            serializedObj = serializedObj.replaceAllLiterally("\n", "\n\t")
        }
        appendStringArrayItem(serializedObj, isFirst)
    }

    /** Appends "key: value" to @builder, preceded by comma, unless @isFirst is true.
      *
      * @param key The key.
      * @param value The value.
      * @param isFirst If true, comma preceding the field is left out.
      *
      */
    def appendKeyValue(key: String, value: Any, isFirst: Boolean) = {
        var serializedObj: String = serializer.serializeObject(value)
        if (prettyPrint) {
            serializedObj = serializedObj.replaceAllLiterally("\n", "\n\t")
        }
        appendKeySerializedValue(key, serializedObj, isFirst)
    }

    def appendKeySerializedValue(key: String, value: String, isFirst: Boolean) = {
        val separator: String = if (prettyPrint) ": " else ":"
        val field: String = "\"" + key + "\"" + separator + value
        appendStringArrayItem(field, isFirst)
    }

}
