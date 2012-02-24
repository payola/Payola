package s2js.runtime.s2js

import s2js.compiler.{NativeJs,NativeJsDependency}

object Rpc
{
    // Do kompilatoru budu pridavat podporu pro takovyhle rozhrani:
    //     procedureName jak jsme se bavili - plne kvalifikovane jmeno metody (napr. 'pkg.subpkg.o.foo')
    //     parameters - Javasript object, napr. { foo: 1234, bar: "fsdsdfsd" }
    //                  (spravne by typ parametru 'parameters' mel bejt neco jako JsObject, ale ten ani neplanuju
    //                  implementovat, takze na miste, kde metodam predavam raw javascriptovej object nebo pole
    //                  pouzivam Any. Map[String, Object] se nehodi, protoze na javascriptovym objektu nemuzes a ani
    //                  bys nemel volat napr. filter(), kterej na Map zavolat jde.
    //     navratova hodnota - navratova hodnota funkce deserializovana z jsonu.
    //                       pro primitivni typy vratit primo je, pro objekty se musi konstruovat objektovej graf, coz
    //                       bude hodne zalezet na charliem, jak bude resit reference apod. To jeste budeme muset
    //                       probrat.
    //                       - typ je opet Any, protoze to muze bejt cokoli. Kdyz nevraci nic, tak vracet undefined.
    //     vyjimky - pokud se neco nezdari, tak by to melo vyhazovat vyjimku. Zadefinuj si nejakou podobne
    //              jako je definovana s2js.runtime.scala.RuntimeExeption.
    //             - pokud invokovana metoda na serveru vyhodi vyjimku, tak by ji rpc controller mel nejak zabalit
    //              a callSync v idealnim pripade vyhodit stejnou (asi otestovat, jestli typ vyjimky je v js definovan
    //              a pokud ne tak holt vyhodit generickou).
    //
    //@NativeJsDependency("s2js.runtime.s2js.RPCException")
    @NativeJs("""

        var url = "/RPC";
        var params = this.buildHttpQuery(parameters);

        var request = XMLHttpRequest  ? new XMLHttpRequest : new ActiveXObject('Msxml2.XMLHTTP');
        request.open("POST", url, false);

        //Send the proper header information along with the request
        request.setRequestHeader("Content-type", "application/x-www-form-urlencoded");

        request.send("method="+procedureName+"&"+params);
        if (request.readyState==4 && request.status==200)
        {
            return this.deserialize(eval("("+request.responseText+")"));
        }else{
            //throw new s2js.runtime.s2js.RPCException("RPC call exited with status code "+request.status);
        }
    """)
    def callSync(procedureName: String, parameters: Any): Any = ()

    @NativeJs("""
        var args = '';
        if (Object.prototype.toString.call(params) === '[object Array]') {
                var arr = [];
                for (arg in params) {
                        arr.push(encodeURIComponent(arg) + '=' + encodeURIComponent(params[arg]));
                }
                args = arr.join('&');
        }

        return args;
    """)
    def buildHttpQuery(params: Map[String, Object]): String = null

    @NativeJs("""
        if (Object.prototype.toString.call(obj) !== '[object Object]')
        {
            return obj;
        }

        var class = obj.__class__;
        if (typeof(class) === 'undefined')
        {
            return obj;
        }

        if (eval("typeof(class)") === 'undefined')
        {
            goog.load(class);
        }

        var result = eval("new "+class+"()");

        for (var key in obj)
        {
            result[key] = this.deserialize(obj[key]);
        }

        return result;

    """)
    def deserialize(obj: Object): Object = null
}
