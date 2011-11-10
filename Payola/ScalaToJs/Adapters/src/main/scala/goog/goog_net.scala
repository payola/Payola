package goog.net 

import goog.events.Event

class XhrIo {
	def getResponseType():XhrIo.ResponseType = null
	def getResponseText():String = ""
	def getResponseXml():browser.Document = null
	def getResponseJson(opt_xssiPrefix:String=""):Map[String,Any] = null
    def getStatus():Int = 200
}

object XhrIo {

	class ResponseType

	def send [T<:Event](url:String, callback:(T)=>Unit = null, method:String="", content:String="", headers:Map[String, String]=null, timeoutInterval:Int=0) {}
}
