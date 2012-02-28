package s2js.runtime.s2js

import s2js.compiler.{NativeJsDependency, NativeJs}

object Rpc
{
    //     vyjimky - pokud se neco nezdari, tak by to melo vyhazovat vyjimku. Zadefinuj si nejakou podobne
    //              jako je definovana s2js.runtime.scala.RuntimeExeption.
    //             - pokud invokovana metoda na serveru vyhodi vyjimku, tak by ji rpc controller mel nejak zabalit
    //              a callSync v idealnim pripade vyhodit stejnou (asi otestovat, jestli typ vyjimky je v js definovan
    //              a pokud ne tak holt vyhodit generickou).
    //

    @NativeJsDependency("s2js.RPCException")
    @NativeJs("""

        var url = "/RPC";

        var request = XMLHttpRequest  ? new XMLHttpRequest : new ActiveXObject('Msxml2.XMLHTTP');
        request.open("POST", url, false);

        //Send the proper header information along with the request
        request.setRequestHeader("Content-type", "application/x-www-form-urlencoded");

        if (parameters.length > 0)
        {
            var params = this.buildHttpQuery(parameters);
            request.send("method="+procedureName+"&"+params);
        }else{
            request.send("method="+procedureName);
        }

        if (request.readyState==4 && request.status==200)
        {
            var refQueue = [];
            var objectRegistry = {};
            var instance = this.deserialize(eval("("+request.responseText+")"), objectRegistry, refQueue);

            for (var k in refQueue)
            {
                refQueue[k].obj[refQueue[k].key] = objectRegistry[refQueue[k].refID];
            }

            return instance;
        }else{
            throw new s2js.RPCException("RPC call exited with status code "+request.status);
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

        // check if the deserialized object is of type Object
        if (Object.prototype.toString.call(obj) !== '[object Object]')
        {
            // if not, it is a scalar or an array, so return it
            return obj;
        }

        // init object reference registry (reuse or create)
        objectRegistry = objectRegistry || {};

        // handle collections carefully
        if (typeof(obj.__arrayClass__) !== "undefined")
        {
            return this.deserializeArrayClass(obj, objectRegistry, refQueue);
        }

        // a "typical" object, get its className
        var clazz = obj.__class__;

        // if it doesn't have a className set, it is a anonymous object (or bug in serialization)
        if (typeof(clazz) === 'undefined')
        {
            // registrer based on objectID into the registry and return
            objectRegistry[obj.__objectID__] = obj;
            return obj;
        }

        var result = this.checkDefinedAndMakeInstance(clazz);
        if (result == null)
        {
            return obj;
        }

        this.deserializeProperties(obj, result, objectRegistry, refQueue);

        // assign into the registry
        objectRegistry[obj.__objectID__] = result;
        return result;

    """)
    def deserialize(obj: Object, objectRegistry: Object = null, refQueue: Object = null): Object = null

    @NativeJs("""
        // deserialize via recursion, but be aware of references, which are probably not yet created,
        // so add the "set reference" request into a queue to make it later
        for (var key in obj)
        {
            // is it a reference?
            if ((Object.prototype.toString.call(obj[key]) === '[object Object]') && (typeof(obj[key].__ref__) !== "undefined"))
            {
                // push the setRef task into the queue
                refQueue.push({
                    "obj": result,
                    "key": key,
                    "refID": obj[key].__ref__
                });
                continue;
            }

            // skip properties beginning with "__"
            if (key.match(/^__/)) continue;

            // deserialize the object right now
            result[key] = this.deserialize(obj[key], objectRegistry, refQueue);
        }
    """)
    def deserializeProperties(obj: Object, result: Object, objectRegistry: Object, refQueue: Object) = null

    @NativeJs("""
        // create an instance of the collection class
        var instance = this.checkDefinedAndMakeInstance(obj.__arrayClass__);
        if (instance == null)
        {
            return obj;
        }

        // set the contents of the collection properly to the desired property
        instance.internalJsArray = obj.__value__;

        // deserialize members of the collection
        for(var k in instance.internalJsArray)
        {
            instance.internalJsArray[k] = this.deserialize(instance.internalJsArray[k], objectRegistry, refQueue);
        }

        // register the object into the registry
        objectRegistry[obj.__objectID__] = instance;

        // done
        return instance;
    """)
    def deserializeArrayClass(obj: Object, objectRegistry: Object = null, refQueue: Object = null): Object = null

    @NativeJs("""

        var namespaces = className.split(".");
        var fqdn = "";

        for (var k in namespaces)
        {
            if (k > 0){
                fqdn += ".";
            }

            fqdn += namespaces[k];

            // check if the type is already loaded
            if (eval("typeof("+fqdn+")") === 'undefined')
            {
                // if not, load it
                //TODO
                window.alert("Should load "+className+" (undefined "+ fqdn+")");
                return null;
            }
        }

        // make an instance of the desired type
        var result = eval("new "+className+"()");
        return result;
    """)
    def checkDefinedAndMakeInstance(className: String) : Object = null
}