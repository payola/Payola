package s2js.adapters.dom

trait Text extends CharacterData
{
    val isElementContentWhitespace: Boolean

    val wholeText: String

    def splitText(offset: Int)

    def replaceWholeText(content: String)
}
