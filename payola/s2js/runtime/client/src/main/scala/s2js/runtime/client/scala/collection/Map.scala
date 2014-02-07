package s2js.runtime.client.scala.collection

import mutable.ListBuffer
import s2js.compiler.{dependency, javascript}

@dependency("scala.Tuple2")
@dependency("scala.Option")
trait Map[A, B] extends Iterable
{
    @javascript("{}")
    var internalJsObject = null

    @javascript("""
        for (var key in self.internalJsObject) {
            if (self.internalJsObject.hasOwnProperty(key)) {
                f(new scala.Tuple2(key, self.internalJsObject[key]))
            }
        }
    """)
    def foreach[U](f: Double => U) {}

    @javascript("self.internalJsObject[x._1] = x._2;")
    def +=(x: Any) {}

    def prepend(x: Any) {
        throw new s2js.runtime.client.scala.UnsupportedOperationException("Can't prepend to a Map.")
    }

    // From TraversableLike
    def reversed: Iterable = {
        (ListBuffer.empty ++ this).reversed
    }

    @javascript("delete self.internalJsObject[x];")
    def -=(x: Any) {}

    @javascript("""
        if (s2js.runtime.client.core.get().isUndefined(self.internalJsObject[key])) {
            return scala.None.get();
        } else {
            return new scala.Some(self.internalJsObject[key]);
        }
    """)
    def get(key: A): Option[B] = None

    // From MapLike
    def getOrElse[B1 >: B](key: A, default: => B1): B1 = get(key) match {
        case Some(v) => v
        case None => default
    }

    // From MapLike
    def apply(key: A): B = get(key) match {
        case None => default(key)
        case Some(value) => value
    }

    // From MapLike
    def contains(key: A): Boolean = get(key).isDefined

    // From MapLike
    def isDefinedAt(key: A) = contains(key)

    // From MapLike
    def default(key: A): B = throw new s2js.runtime.client.scala.NoSuchElementException("key not found: " + key)

    // From mutable.MapLike
    def put(key: A, value: B): Option[B] = {
        val r = get(key)
        update(key, value)
        r
    }

    // From mutable.MapLike
    def update(key: A, value: B) {
        this += ((key, value))
    }

    // From mutable.MapLike
    def remove(key: A): Option[B] = {
        val r = get(key)
        this -= key
        r
    }

    // From mutable.MapLike
    def getOrElseUpdate(key: A, op: => B): B = {
        get(key) match {
            case Some(v) => v
            case None => val d = op; this(key) = d; d
        }
    }

    @javascript("""
        var result = scala.collection.mutable.ListBuffer.get().$apply();
        for (var key in self.internalJsObject) {
            if (key === 'length' || !widthRange.hasOwnProperty(key)) {
                continue;
            }
            result.$plus$plus$eq(widthRange[key]);
        }
        return result;
    """)
    def values(): Iterable = null

    @javascript("""
        var result = scala.collection.mutable.ListBuffer.get().$apply();
        for (var key in self.internalJsObject) {
            if (key === 'length' || !self.internalJsObject.hasOwnProperty(key)) {
                continue;
            }
            result.$plus$eq(key);
        }
        return result;
    """)
    def keys(): Iterable = null
}
