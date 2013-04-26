package cz.payola.domain.net

import scala.io.Source

/**
  * Downloader of content on the specified URL.
  * @param url The URL to download the content from.
  * @param accept Accepted type of the response.
  * @param encoding Encoding that is used when reading the response.
  */
class Downloader(val url: String, val accept: String = "", val encoding: String = "UTF-8", val credentials: Option[(String, String)] = None)
{
    private var _content: Option[String] = None

    /**
      * Returns the downloaded content.
      */
    def result: String = {
        _content = _content.orElse {
            val connection = new java.net.URL(url).openConnection()
            connection.setRequestProperty("Accept", accept)

            credentials.map { c =>
                val credentialsString = c._1 + ":" + c._2;
                val basicAuth = "Basic " + javax.xml.bind.DatatypeConverter.printBase64Binary(credentialsString.getBytes());

                connection.setRequestProperty ("Authorization", basicAuth);
            }

            val inputStream = connection.getInputStream
            val result = Some(Source.fromInputStream(inputStream, encoding).mkString)

            inputStream.close()
            result
        }
        _content.get
    }
}
