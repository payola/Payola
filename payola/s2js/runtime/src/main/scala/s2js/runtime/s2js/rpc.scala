package s2js.runtime.s2js

import s2js.compiler.NativeJs

object rpc {
       @NativeJs("""

                    var xmlhttp = XMLHttpRequest  ? new XMLHttpRequest : new ActiveXObject('Msxml2.XMLHTTP');

                    xmlhttp.onreadystatechange=function()
                    {
                        if (xmlhttp.readyState==4 && xmlhttp.status==200)
                        {
                            callback(xmlhttp.responseText);
                        }else
                        {
                            faultCallback();
                        }
                    }

                    var url = "/RPC";
                    var encodedData = buildHttpQuery(params);

                    if (requestType.toUpperCase() == "GET")
                    {
                        url += "?"+encodedData;
                    }

                    xmlhttp.open(requestType,url,false);

                    if (requestType.toUpperCase() == "POST" || request)
                    {
                        xmlhttp.setRequestHeader("Content-type","application/x-www-form-urlencoded");
                        xmlhttp.send(encodedData);
                    }else
                    {
                        xmlhttp.send();
                    }
    """)
    def CallRPC(callback: String => Unit, faultCallback: => Unit, procedureName: String, params: Map[String, Object], requestType: String = "GET"): Unit = null

       @NativeJs("""
                    var xmlhttp = XMLHttpRequest  ? new XMLHttpRequest : new ActiveXObject('Msxml2.XMLHTTP');

                    var data = null;

                    xmlhttp.onreadystatechange=function()
                    {
                        if (xmlhttp.readyState==4 && xmlhttp.status==200)
                        {
                            data = xmlhttp.responseText;
                        }
                    }

                    var url = "/RPC";
                    var encodedData = buildHttpQuery(params);

                    if (requestType.toUpperCase() == "GET")
                    {
                        url += "?"+encodedData;
                    }

                    xmlhttp.open(requestType,url,false);

                    if (requestType.toUpperCase() == "POST")
                    {
                        xmlhttp.setRequestHeader("Content-type","application/x-www-form-urlencoded");
                        xmlhttp.send(encodedData);
                    }else
                    {
                        xmlhttp.send();
                    }
                    return data;
    """)
    def CallRPCSync(procedureName: String, params:  Map[String, Object], requestType: String = "GET"): Unit = null

    @NativeJs("""

        var args = '';
        if (Object.prototype.toString.call(params) === '[object Object]') {
                var arr = [];
                for (arg in params) {
                        arr.push(encodeURIComponent(arg) + '=' + encodeURIComponent(params[arg]));
                }
                args = arr.join('&');
        }

        return args;

    """)
    private def buildHttpQuery(params: Map[String, Object]): String = null
}
