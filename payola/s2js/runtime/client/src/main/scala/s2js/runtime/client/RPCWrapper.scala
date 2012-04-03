package s2js.runtime.client

import s2js.compiler.{javascript, dependency}
import collection.mutable.ArrayBuffer

@dependency("s2js.runtime.client.RPCException")
object RPCWrapper
{
    //     vyjimky - pokud se neco nezdari, tak by to melo vyhazovat vyjimku. Zadefinuj si nejakou podobne
    //              jako je definovana s2js.runtime.client.scala.RuntimeExeption.
    //             - pokud invokovana metoda na serveru vyhodi vyjimku, tak by ji rpc controller mel nejak zabalit
    //              a callSync v idealnim pripade vyhodit stejnou (asi otestovat, jestli typ vyjimky je v js definovan
    //              a pokud ne tak holt vyhodit generickou).
    //
    @javascript("""
        var url = "/RPC";

        var request = XMLHttpRequest  ? new XMLHttpRequest : new ActiveXObject('Msxml2.XMLHTTP');
        request.open("POST", url, false);

        //Send the proper header information along with the request
        request.setRequestHeader("Content-type", "application/x-www-form-urlencoded");

        if (parameters.length > 0)
        {
            var params = this.buildHttpQuery(parameters, parameterTypes);
            params += "&paramTypes="+this.serializeParamTypes(parameterTypes);
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
            throw new s2js.runtime.client.RPCException("RPC call exited with status code "+request.status);
        }
    """)
    def callSync(procedureName: String, parameters: Any, parameterTypes: Any): Any = ()

    @javascript("""
        var url = "/RPC/async";

        var request = XMLHttpRequest  ? new XMLHttpRequest : new ActiveXObject('Msxml2.XMLHTTP');
        request.open("POST", url, true); //ASYNC!

        //Send the proper header information along with the request
        request.setRequestHeader("Content-type", "application/x-www-form-urlencoded");

        var serializer = this;

        request.onreadystatechange = function(){
            if (request.readyState==4 && request.status==200)
            {
                var refQueue = [];
                var objectRegistry = {};
                var instance = serializer.deserialize(eval("("+request.responseText+")"), objectRegistry, refQueue);

                for (var k in refQueue)
                {
                    refQueue[k].obj[refQueue[k].key] = objectRegistry[refQueue[k].refID];
                }

                successCallback(instance);
            }else if (request.readyState==4){
                failCallback(new s2js.runtime.client.RPCException("RPC call exited with status code "+request.status));
            }
        }

        if (parameters.length > 0)
        {
            var params = this.buildHttpQuery(parameters, parameterTypes);
            params += "&paramTypes="+this.serializeParamTypes(parameterTypes);
            request.send("method="+procedureName+"&"+params);
        }else{
            request.send("method="+procedureName);
        }
    """)
    def callAsync(procedureName: String, parameters: Any, parameterTypes: Any, successCallback: Function1[Any, Unit], failCallback: Function1[Throwable, Unit]) {}
    
    def serializeParamTypes(parameterTypes: Seq[String]) : String = {
        parameterTypes.mkString("[\"","\",\"","\"]")
    }

    @javascript("return encodeURIComponent(s);")
    def encodeURIComponent(s: String): String = ""
    
    def buildHttpQuery(params: ArrayBuffer[Any], paramTypes: ArrayBuffer[String]): String = {
        var index = -1
        val paramStrings = params.map {param =>
            val paramString = param match {
                case p: String => p
                case p: Seq[_] => {
                    if (isNumericCollection(paramTypes.apply(index+1)))
                    {
                        p.mkString("[",",","]")
                    }else
                    {
                        p.mkString("[\"","\",\"","\"]")
                    }
                }
                case p => p.toString
            }
            index += 1
            index + "=" + encodeURIComponent(paramString)
        }
        paramStrings.mkString("&")
    }

    def isNumericCollection(paramType: String) : Boolean = {
        if(
            paramType.endsWith("[scala.Int]")
            || paramType.endsWith("[scala.Double]")
            || paramType.endsWith("[scala.Float]")
            ||  paramType.endsWith("[scala.Short]")
        ){
            true
        }else{
            false
        }
    }

    @javascript("""
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
        if (typeof(clazz) === 'undefined') {
            // registrer based on objectID into the registry and return
            objectRegistry[obj.__objectID__] = obj;
            return obj;
        } else if (clazz === 'scala.None') {
            return scala.None;
        } else if (clazz === 'scala.Some') {
            return new scala.Some(obj.__value__);
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

    @javascript("""
        // deserialize via recursion, but be aware of references, which are probably not yet created,
        // so add the "set reference" request into a queue to make it later
        for (var key in obj)
        {
            // is it a reference?
            if ((Object.prototype.toString.call(obj[key]) === '[object Object]') && (typeof(obj[key].__ref__) !==
            "undefined"))
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

    @javascript("""
        // create an instance of the collection class
        var instance = this.checkDefinedAndMakeInstance(obj.__arrayClass__);
        if (instance == null)
        {
            return obj;
        }

        // deserialize members of the collection and add them to the instance
        for (var i = 0; i < obj.__value__.length; i++) {
            instance.$plus$eq(this.deserialize(obj.__value__[i], objectRegistry, refQueue));
        }

        // register the object into the registry
        objectRegistry[obj.__objectID__] = instance;

        // done
        return instance;
    """)
    def deserializeArrayClass(obj: Object, objectRegistry: Object = null, refQueue: Object = null): Object = null

    @javascript("""
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
    def checkDefinedAndMakeInstance(className: String): Object = null
}
