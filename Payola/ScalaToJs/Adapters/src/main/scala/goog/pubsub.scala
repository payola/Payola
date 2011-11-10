package goog.pubsub

class PubSub extends goog.Disposable {
    def subscribe(topic:String, fn:()=>Any, context:Object=null):Int = 0
    def subscribeOne(topic:String, fn:()=>Any, context:Object=null):Int = 0
    def unsubscribe(topic:String, fn:()=>Any, context:Object=null):Boolean = false
    def unsubscribeByKey(key:Int):Boolean = false
    def publish(topic:String, args:Map[String,Any]):Boolean = false
    def clear(topic:String=null):Boolean = false
    def getCount(topic:String=null):Int = 0
}

