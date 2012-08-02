package s2js.adapters.js.browser

abstract class Json
{
    def stringify(x: Any): String

    def parse(x: String): Any
}
