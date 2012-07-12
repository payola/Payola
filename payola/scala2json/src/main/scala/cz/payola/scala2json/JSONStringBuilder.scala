package cz.payola.scala2json

import collection.mutable.ArrayBuffer

/** This class introduces some helper methods for the JSONSerializer in order to append
  * fields to JSON objects/dictionaries or arrays.
  *
  * As the StringBuilder class is defined final, the JSONStringBuilder can't extend it,
  * hence only acts as a wrapper around the actual StringBuilder.
  *
  * @param serializer The serializer that is being used.
  * @param prettyPrint Use the PrettyPrint output format?
  * @param initialValue A value to initialize the inner StringBuilder with. E.g. "{" or "[".
  */
class JSONStringBuilder(serializer: JSONSerializer, prettyPrint: Boolean, initialValue: String){
    val stringBuilder: StringBuilder = new StringBuilder(initialValue)

    /** Appends an array item to string builder.
      *
      * @param item The item.
      * @param isFirst Whether the field is first - if it is, the comma separator is left out.
      */
    def appendStringArrayItem(item: String, isFirst: Boolean) {
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

    /** Appends an array item to string builder.
      *
      * @param item The item.
      * @param isFirst Whether the field is first - if it is, the comma separator is left out.
      * @param processedObjects Already processed objects.
      */
    def appendArrayItem(item: Any, isFirst: Boolean, processedObjects: ArrayBuffer[Any]) {
        var serializedObj: String = serializer.serializeObject(item, processedObjects)
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
      * @param processedObjects Already processed objects.
      */
    def appendKeyValue(key: String, value: Any, isFirst: Boolean, processedObjects: ArrayBuffer[Any]) = {
        var serializedObj: String = serializer.serializeObject(value, processedObjects)
        if (prettyPrint) {
            serializedObj = serializedObj.replaceAllLiterally("\n", "\n\t")
        }
        appendKeySerializedValue(key, serializedObj, isFirst)
    }

    /** Appends "key: value" to @builder, preceded by comma, unless @isFirst is true.
      *
      * @param key The key.
      * @param value The value.
      * @param isFirst If true, comma preceding the field is left out.
      */
    def appendKeySerializedValue(key: String, value: String, isFirst: Boolean) = {
        val separator: String = if (prettyPrint) ": " else ":"
        val field: String = "\"" + key + "\"" + separator + value
        appendStringArrayItem(field, isFirst)
    }

    /** The resulting string padded with tabs.
      *
      * @return Padded string.
      */
    def paddedToString(): String = {
        JSONUtilities.padStringWithTab(this.toString)
    }

}
