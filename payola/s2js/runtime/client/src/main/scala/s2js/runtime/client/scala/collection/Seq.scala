package s2js.runtime.client.scala.collection

import mutable.ArrayBuffer
import s2js.runtime.client.scala.util.control.Breaks._
import s2js.compiler.javascript

object Seq
{
    // Just a hack to make the map function work.
    def canBuildFrom: Boolean = true
}

trait Seq extends Iterable
{
    @javascript("[]")
    var internalJsArray: ArrayBuffer = null

    def getInternalJsArray = internalJsArray

    def setInternalJsArray(value: ArrayBuffer) {
        internalJsArray = value
    }

    @javascript("""
        var u = {}, a = [];
        for(var i = 0, l = self.getInternalJsArray().length; i < l; ++i){
            if(u.hasOwnProperty(self.getInternalJsArray()[i])) {
                continue;
            }
            a.push(self.getInternalJsArray()[i]);
            u[self.getInternalJsArray()[i]] = 1;
        }
        return a;
                """)
    def distinct(): ArrayBuffer = null

    @javascript("""
        return self.getInternalJsArray().sort(function(a,b){
            if (f(a,b)){ return -1; }
            return 1;
        });
                """)
    def sortWith(f: (Double, Double) => Boolean): Iterable = newInstance

    @javascript("""
        for (var i in self.getInternalJsArray()) {
            f(self.getInternalJsArray()[i]);
        }
    """)
    def foreach[U](f: Double => U) {}

    @javascript("self.getInternalJsArray().push(x);")
    def append(x: Any) {}

    def +=(x: Any) {
        append(x)
    }

    def :+(x: Any) {
        append(x)
    }

    // From TraversableLike
    def reversed: Iterable = {
        val elems: Iterable = newInstance
        for (x <- this) {
            elems.prepend(x)
        }
        elems
    }

    @javascript("return self.getInternalJsArray().length;")
    override def size: Int = 0

    @javascript("""
        if (s2js.runtime.client.core.get().isUndefined(self.getInternalJsArray()[n])) {
            throw new scala.NoSuchElementException('An item with index ' + n + ' is not present.');
        }
        return self.getInternalJsArray()[n];
    """)
    def apply(n: Int): Any = null

    @javascript("""
        if (self.size() <= n) {
            throw new scala.NoSuchElementException('An item with index ' + n + ' is not present.');
        }
        self.getInternalJsArray()[n] = newelem;
    """)
    def update(n: Int, newelem: Any) {}

    def length: Int = size

    @javascript("""
        if (index < 0 || self.size() <= index) {
            throw new scala.NoSuchElementException('An item with index ' + n + ' is not present.');
        }
        var removed = self.getInternalJsArray()[index];
        self.getInternalJsArray().splice(index, 1);
        return removed;
    """)
    def remove(index: Int) {}

    @javascript("""self.getInternalJsArray().splice(0, 0, x);""")
    def prepend(x: Any) {}

    @javascript("""self.getInternalJsArray().splice(n, 0, x);""")
    def insert(n: Int, x: Any) {}

    @javascript("""
        var index = self.getInternalJsArray().indexOf(x);
        if (index != -1) {
            self.getInternalJsArray().splice(index, 1);
        }
    """)
    def -=(x: Any) {}

    @javascript("""return self.getInternalJsArray().slice(0);""")
    override def clone: ArrayBuffer = null

    // From SeqLike
    def indexWhere(p: Double => Boolean, from: Int = 0): Int = {
        var i = from
        breakable {() =>
            drop(from).foreach {x =>
                if (p(x)) {
                    break()
                } else {
                    i += 1
                }
            }
            i = -1
        }
        i
    }

    def indexOf(item: Double): Int = {
        indexWhere(_ == item)
    }

    // From SeqLike
    def contains(x: Double): Boolean = {
        exists(_ == x)
    }

    def startsWith(prefix: Seq): Boolean = {
        prefix.length match {
            case prefixLength if prefixLength > length => false
            case 0 => true
            case prefixLength => {
                var result = true
                breakable {() =>
                    var index = 0
                    prefix.foreach {item =>
                        if (item != this(index)) {
                            result = false
                            break()
                        }
                        index += 1
                    }
                }
                result
            }
        }
    }

    def endsWith(suffix: Seq): Boolean = {
        suffix.length match {
            case suffixLength if suffixLength > length => false
            case 0 => true
            case suffixLength => {
                var result = true
                breakable {() =>
                    val startIndex = length - suffixLength
                    var index = 0
                    suffix.foreach {item =>
                        if (item != this(startIndex + index)) {
                            result = false
                            break()
                        }
                        index += 1
                    }
                }
                result
            }
        }
    }

    def toArray: ArrayBuffer = getInternalJsArray

    def toList: ArrayBuffer = getInternalJsArray

    def toBuffer: ArrayBuffer = getInternalJsArray
}


