package s2js.runtime.s2js

import s2js.compiler.NativeJs

object rpc {
       @NativeJs("""xmlhttp.onreadystatechange=function()
                    {
                        if (xmlhttp.readyState==4 && xmlhttp.status==200)
                        {
                            callback(xmlhttp.responseText);
                        }else
                        {
                            faultCallback();
                        }
                    }
                    xmlhttp.open(requestType,"/RPC",async);
                    xmlhttp.send();
    """)
    def CallRPC(callback: String => Unit, faultCallback: => Unit, procedureName: String, requestType: String = "GET", async: Boolean = true): Unit = null
}
