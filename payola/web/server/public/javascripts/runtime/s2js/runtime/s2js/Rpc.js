goog.provide('s2js.Rpc');
s2js.Rpc.CallRPC = function(callback, faultCallback, procedureName, params, requestType) {
var self = this;
if (typeof(requestType) === 'undefined') { requestType = 'GET'; }


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
    };
s2js.Rpc.CallRPCSync = function(procedureName, params, requestType) {
var self = this;
if (typeof(requestType) === 'undefined') { requestType = 'GET'; }

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
    };
s2js.Rpc.buildHttpQuery = function(params) {
var self = this;


        var args = '';
        if (Object.prototype.toString.call(params) === '[object Object]') {
                var arr = [];
                for (arg in params) {
                        arr.push(encodeURIComponent(arg) + '=' + encodeURIComponent(params[arg]));
                }
                args = arr.join('&');
        }

        return args;

    };
s2js.Rpc.metaClass_ = new s2js.MetaClass('s2js.Rpc', []);
