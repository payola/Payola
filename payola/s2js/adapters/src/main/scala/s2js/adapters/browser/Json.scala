package s2js.adapters.browser

trait Json
{
    def stringify(x: Any): String

    def parse(x: String): Any
}
