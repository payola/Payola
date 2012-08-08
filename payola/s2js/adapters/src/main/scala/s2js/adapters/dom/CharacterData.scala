package s2js.adapters.dom

trait CharacterData extends Node
{
    var data: String

    val length: Int

    def substringData(offset: Int, count: Int)

    def appendData(data: String)

    def insertData(offset: Int, data: String)

    def deleteData(offset: Int, count: Int)

    def replaceData(offset: Int, count: Int, data: String)
}
