package s2js.runtime.client.scala.collection

import s2js.runtime.client.core._
import s2js.runtime.client.scala.util.control.Breaks._
import s2js.runtime.client.scala.NotImplementedException

// Type of the item doesn't need to be specified as it's compiled to JavaScript and erased by the compiler. In order to
// easily support min, max etc. functions, the type Double is used as an item type.
trait Iterable
{
    def newInstance: Iterable

    def foreach[U](f: Double => U)

    def +=(x: Any)

    def -=(x: Any)

    def prepend(x: Any)

    // From TraversableLike
    def reversed: Iterable

    def ++=(coll: Iterable) {
        coll.foreach(this += _)
    }

    def --=(coll: Iterable) {
        coll.foreach(this -= _)
    }

    // From TraversableOnce
    def size: Int = {
        var result = 0
        for (x <- this) {
            result += 1
        }
        result
    }

    // From TraversableOnce
    def nonEmpty: Boolean = !isEmpty

    // From TraversableOnce
    def count(p: Double => Boolean): Int = {
        var cnt = 0
        for (x <- this) {
            if (p(x)) {
                cnt += 1
            }
        }
        cnt
    }

    // From TraversableOnce
    def /:(z: Double)(op: (Double, Double) => Double): Double = foldLeft(z)(op)

    // From TraversableOnce
    def :\(z: Double)(op: (Double, Double) => Double): Double = foldRight(z)(op)

    // From TraversableOnce
    def foldLeft(z: Double)(op: (Double, Double) => Double): Double = {
        var result = z
        this.foreach(x => result = op(result, x))
        result
    }

    // From TraversableOnce
    def foldRight(z: Double)(op: (Double, Double) => Double): Double = {
        reversed.foldLeft(z)((x, y) => op(y, x))
    }

    // From TraversableOnce
    def reduceLeft(op: (Double, Double) => Double): Double = {
        if (isEmpty) {
            throw new s2js.runtime.client.scala.UnsupportedOperationException("empty.reduceLeft")
        }

        var first = true
        var acc: Double = 0

        for (x <- this) {
            if (first) {
                acc = x
                first = false
            } else {
                acc = op(acc, x)
            }
        }
        acc
    }

    // From TraversableOnce
    def reduceRight(op: (Double, Double) => Double): Double = {
        if (isEmpty) {
            throw new s2js.runtime.client.scala.UnsupportedOperationException("empty.reduceRight")
        }

        reversed.reduceLeft((x, y) => op(y, x))
    }

    // From TraversableOnce
    def reduceLeftOption(op: (Double, Double) => Double): Option[Double] =
        if (isEmpty) None else Some(reduceLeft(op))

    // From TraversableOnce
    def reduceRightOption(op: (Double, Double) => Double): Option[Double] =
        if (isEmpty) None else Some(reduceRight(op))

    // From TraversableOnce
    def reduce(op: (Double, Double) => Double): Double = reduceLeft(op)

    // From TraversableOnce
    def reduceOption(op: (Double, Double) => Double): Option[Double] = reduceLeftOption(op)

    // From TraversableOnce
    def fold(z: Double)(op: (Double, Double) => Double): Double = foldLeft(z)(op)

    // From TraversableOnce
    def aggregate(z: Double)(seqop: (Double, Double) => Double, combop: (Double, Double) => Double): Double = foldLeft(
        z)(seqop)

    // From TraversableOnce
    def sum: Double = foldLeft(0)((x, y) => x + y)

    // From TraversableOnce
    def product: Double = foldLeft(1)((x, y) => x * y)

    // From TraversableOnce
    def min: Double = {
        if (isEmpty) {
            throw new s2js.runtime.client.scala.UnsupportedOperationException("empty.min")
        }

        reduceLeft((x, y) => if (x <= y) x else y)
    }

    // From TraversableOnce
    def max: Double = {
        if (isEmpty) {
            throw new s2js.runtime.client.scala.UnsupportedOperationException("empty.max")
        }

        reduceLeft((x, y) => if (x >= y) x else y)
    }

    // From TraversableOnce
    def maxBy(f: Double => Double): Double = {
        if (isEmpty) {
            throw new s2js.runtime.client.scala.UnsupportedOperationException("empty.maxBy")
        }

        reduceLeft((x, y) => if (f(x) >= f(y)) x else y)
    }

    // From TraversableOnce
    def minBy(f: Double => Double): Double = {
        if (isEmpty) {
            throw new s2js.runtime.client.scala.UnsupportedOperationException("empty.minBy")
        }

        reduceLeft((x, y) => if (f(x) <= f(y)) x else y)
    }

    // From TraversableOnce
    def mkString(start: String, sep: String, end: String): String = {
        var result = ""
        var separator = ""
        var suffix = ""
        if (isDefined(end)) {
            result = start
            separator = sep
            suffix = end
        } else if (isDefined(sep)) {
            result = start
            separator = start
        } else if (isDefined(start)) {
            separator = start
        }

        var first = true
        for (x <- this) {
            if (!first) {
                result += separator
            }
            result += (x.toString)
            first = false
        }
        result += suffix
        result
    }

    // From TraversableLike
    def isEmpty: Boolean = {
        var result = true
        breakable {() =>
            for (x <- this) {
                result = false
                break
            }
        }
        result
    }

    // From TraversableLike
    def hasDefiniteSize = true

    // From TraversableLike
    def ++(that: Iterable): Iterable = {
        val b = newInstance
        b ++= this
        b ++= that
        b
    }

    // From TraversableLike
    def --(that: Iterable): Iterable = {
        val b = newInstance
        b ++= this
        b --= that
        b
    }

    // From TraversableLike
    def map(f: Double => Double): Iterable = {
        val b = newInstance
        for (x <- this) {
            b += f(x)
        }
        b
    }

    // From TraversableLike
    def flatMap(f: Double => Iterable): Iterable = {
        val b = newInstance
        for (x <- this) {
            b ++= f(x)
        }
        b
    }

    // From TraversableLike
    def filter(p: Double => Boolean): Iterable = {
        val b = newInstance
        for (x <- this) {
            if (p(x)) {
                b += x
            }
        }
        b
    }

    // From TraversableLike
    def filterNot(p: Double => Boolean): Iterable = filter(!p(_))

    // From TraversableLike
    def collect(pf: PartialFunction[Double, Double]): Iterable = {
        throw new NotImplementedException("Function collect is not implemented.")
    }

    // From TraversableLike
    def partition(p: Double => Boolean): (Iterable, Iterable) = {
        val l, r = newInstance
        for (x <- this) {
            (if (p(x)) l else r) += x
        }
        (l, r)
    }

    // From TraversableLike
    def forall(p: Double => Boolean): Boolean = {
        var result = true
        breakable {() =>
            for (x <- this) {
                if (!p(x)) {
                    result = false;
                    break
                }
            }
        }
        result
    }

    // From TraversableLike
    def exists(p: Double => Boolean): Boolean = !forall(!p(_))

    // From TraversableLike
    def find(p: Double => Boolean): Option[Double] = {
        var result: Option[Double] = None
        breakable {() =>
            for (x <- this) {
                if (p(x)) {
                    result = Some(x);
                    break
                }
            }
        }
        result
    }

    // From TraversableLike
    def scan(z: Double)(op: (Double, Double) => Double): Iterable = scanLeft(z)(op)

    // From TraversableLike
    def scanLeft(z: Double)(op: (Double, Double) => Double): Iterable = {
        val b = newInstance
        var acc = z
        b += acc
        for (x <- this) {
            acc = op(acc, x);
            b += acc
        }
        b
    }

    // From TraversableLike
    def scanRight(z: Double)(op: (Double, Double) => Double): Iterable = {
        val b = newInstance
        b += z
        var acc = z
        for (x <- reversed) {
            acc = op(x, acc)
            b += acc
        }
        b
    }

    // From TraversableLike
    def head: Double = {
        var result: () => Double = () => throw new s2js.runtime.client.scala.NoSuchElementException("empty.head")
        breakable {() =>
            for (x <- this) {
                result = () => x
                break
            }
        }
        result()
    }

    // From TraversableLike
    def headOption: Option[Double] = if (isEmpty) None else Some(head)

    // From TraversableLike
    def tail: Iterable = {
        if (isEmpty) {
            throw new s2js.runtime.client.scala.UnsupportedOperationException("empty.tail")
        }
        drop(1)
    }

    // From TraversableLike
    def last: Double = {
        var lst = head
        for (x <- this) {
            lst = x
        }
        lst
    }

    // From TraversableLike
    def lastOption: Option[Double] = if (isEmpty) None else Some(last)

    // From TraversableLike
    def init: Iterable = {
        if (isEmpty) {
            throw new s2js.runtime.client.scala.UnsupportedOperationException("empty.init")
        }
        var lst = head
        var follow = false
        val b = newInstance
        for (x <- this) {
            if (follow) {
                b += lst
            } else {
                follow = true
            }
            lst = x
        }
        b
    }

    // From TraversableLike
    def take(n: Int): Iterable = slice(0, n)

    def takeRight(n: Int): Iterable = slice(this.size - n, this.size)

    // From TraversableLike
    def drop(n: Int): Iterable = {
        if (n <= 0) {
            val b = newInstance
            b ++= this
            b
        } else {
            sliceWithKnownDelta(n, Int.MaxValue, -n)
        }
    }

    def dropRight(n: Int): Iterable = {
        if (n <= 0) {
            val b = newInstance
            b
        } else {
            sliceWithKnownDelta(0, n, -n)
        }
    }

    // From TraversableLike
    def slice(from: Int, until: Int): Iterable = {
        sliceWithKnownBound(math.max(from, 0), until)
    }

    // From TraversableLike
    // Precondition: from >= 0, until > 0, builder already configured for building.
    private[this] def sliceInternal(from: Int, until: Int, b: Iterable): Iterable = {
        var i = 0
        breakable {() =>
            for (x <- this) {
                if (i >= from) b += x
                i += 1
                if (i >= until) break
            }
        }
        b
    }

    // From TraversableLike
    // Precondition: from >= 0
    private[scala] def sliceWithKnownDelta(from: Int, until: Int, delta: Int): Iterable = {
        val b = newInstance
        if (until <= from) {
            b
        } else {
            sliceInternal(from, until, b)
        }
    }

    // From TraversableLike
    // Precondition: from >= 0
    private[scala] def sliceWithKnownBound(from: Int, until: Int): Iterable = {
        val b = newInstance
        if (until <= from) {
            b
        } else {
            sliceInternal(from, until, b)
        }
    }

    // From TraversableLike
    def takeWhile(p: Double => Boolean): Iterable = {
        val b = newInstance
        breakable {() =>
            for (x <- this) {
                if (!p(x)) {
                    break
                }
                b += x
            }
        }
        b
    }

    // From TraversableLike
    def dropWhile(p: Double => Boolean): Iterable = {
        val b = newInstance
        var go = false
        for (x <- this) {
            if (!p(x)) {
                go = true
            }
            if (go) {
                b += x
            }
        }
        b
    }

    // From TraversableLike
    def span(p: Double => Boolean): (Iterable, Iterable) = {
        val l, r = newInstance
        var toLeft = true
        for (x <- this) {
            toLeft = toLeft && p(x)
            (if (toLeft) l else r) += x
        }
        (l, r)
    }

    // From TraversableLike
    def splitAt(n: Int): (Iterable, Iterable) = {
        val l, r = newInstance
        var i = 0
        for (x <- this) {
            (if (i < n) l else r) += x
            i += 1
        }
        (l, r)
    }

    // From TraversableLike
    override def toString = mkString(stringPrefix + "(", ", ", ")")

    // From TraversableLike
    def stringPrefix: String = {
        val clazz = classOf(this)
        if (clazz != null){
            var str = clazz.fullName
            val idx1 = str.lastIndexOf(".")
            if (idx1 != -1) {
                str = str.substring(idx1 + 1)
            }
            val idx2 = str.indexOf("$")
            if (idx2 != -1) {
                str = str.substring(0, idx2)
            }
            str
        } else {
            "anonymous"
        }
    }
}
