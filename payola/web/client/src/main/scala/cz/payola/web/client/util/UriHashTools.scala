package cz.payola.web.client.util

import s2js.compiler.javascript

object UriHashTools
{
    @javascript("""
          if (window.location.hash){
            var params = window.location.hash.substr(1).split('&');
            for(i = 0; i < params.length; ++i) {
                if(params[i].split('=')[0] == name) {
                    return params[i].split('=')[1]
                }
            }
          }
          return '';
                """)
    def getUriParameter(name: String) : String = null

    @javascript("""
          var currentHash = window.location.hash;
          var resultHash = ""

          var params = currentHash.substr(1).split('&');
          for(i = 0; i < params.length; ++i) {
            if(params[i].split('=')[0] != "" && params[i].split('=')[0] != name ) {
                if(resultHash.length != 0) {
                  resultHash = resultHash + '&';
                }
                resultHash = resultHash + params[i]
            }
          }
          $.bbq.pushState((resultHash + '&' + name + "=" + value));

                """)
    def setUriParameter(name: String, value: String) {}

    @javascript("""return encodeURIComponent(uri)""")
    def encodeURIComponent(uri: String) : String = ""

    @javascript("""return decodeURIComponent(uri)""")
    def decodeURIComponent(uri: String) : String = ""


}
