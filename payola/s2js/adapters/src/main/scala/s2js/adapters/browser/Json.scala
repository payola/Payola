package s2js.adapters.browser

abstract class Json
{
    def stringify(x: Any): String

    def parse(x: String): Any
}
