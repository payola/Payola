package s2js.runtime.shared

@remote object ClassProvider
{
    var javaScriptsDirectory = ""

    def get(classNames: Seq[String], classNamesToIgnore: Seq[String]): String = {
        """window.alert("Reply from the server with source of following classes: %s");""".format(classNames.mkString(","))
    }
}
