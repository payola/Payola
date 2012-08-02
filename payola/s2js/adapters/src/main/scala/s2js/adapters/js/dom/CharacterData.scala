package s2js.adapters.js.dom

abstract class CharacterData extends Node
{
    var data: String

    def appendData(data: String)

    def deleteData(offset: Int, count: Int)

    def insertData(offset: Int, data: String)

    def length: Int

    def replaceData(offset: Int, count: Int, data: String)

    def substringData(offset: Int, count: Int)
}
