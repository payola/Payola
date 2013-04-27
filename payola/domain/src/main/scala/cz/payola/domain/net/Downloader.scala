package cz.payola.domain.net

import scala.io.Source
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.client.methods.HttpGet
import org.apache.http.auth.{UsernamePasswordCredentials, AuthScope}
import org.apache.commons.io.IOUtils

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

            if (credentials.isDefined){
                getSecuredResult
            }else{
                getResult
            }
        }
        _content.get
    }

    def getSecuredResult : Option[String] = {
        credentials.map { c =>
            val creds = new UsernamePasswordCredentials(c._1, c._2)
            val httpclient = new DefaultHttpClient()
            val get = new HttpGet(url)
            get.addHeader("X-Requested-Auth", "Digest")
            get.addHeader("Accept", accept)
            try {
                httpclient.getCredentialsProvider().setCredentials(AuthScope.ANY, creds)
                val response = httpclient.execute(get)
                val content = IOUtils.toString(response.getEntity().getContent(), "UTF-8")
                content
            } finally {
                httpclient.getConnectionManager().shutdown()
            }
        }
    }

    def getResult : Option[String] = {
        val connection = new java.net.URL(url).openConnection()
        connection.setRequestProperty("Accept", accept)

        val inputStream = connection.getInputStream
        val result = Some(Source.fromInputStream(inputStream, encoding).mkString)

        inputStream.close()
        result
    }
}
