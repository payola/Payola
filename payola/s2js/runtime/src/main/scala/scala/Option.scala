package s2js.runtime.scala

object Option {
    def apply[A](x: A): Option[A] = if (x == null) None else Some(x)

    def empty[A]: Option[A] = None
}

abstract class Option[+A] {
    def isEmpty: Boolean

    def isDefined: Boolean = !isEmpty

    def get: A

    def getOrElse[B >: A](default: => B): B = if (isEmpty) default else this.get

    def orNull[A1 >: A](implicit ev: Null <:< A1): A1 = this getOrElse null

    def map[B](f: A => B): Option[B] = if (isEmpty) None else Some(f(this.get))

    def flatMap[B](f: A => Option[B]): Option[B] = if (isEmpty) None else f(this.get)

    def flatten[B](implicit ev: A <:< Option[B]): Option[B] = if (isEmpty) None else ev(this.get)

    def filter(p: A => Boolean): Option[A] = if (isEmpty || p(this.get)) this else None

    def filterNot(p: A => Boolean): Option[A] = if (isEmpty || !p(this.get)) this else None

    def exists(p: A => Boolean): Boolean = !isEmpty && p(this.get)

    def foreach[U](f: A => U) {
        if (!isEmpty) f(this.get)
    }

    def orElse[B >: A](alternative: => Option[B]): Option[B] = if (isEmpty) alternative else this

    def iterator: Iterator[A] = if (isEmpty) collection.Iterator.empty else collection.Iterator.single(this.get)

    def toList: List[A] = if (isEmpty) List() else List(this.get)
}

object Some {
    def apply[A](x: A): Option[A] = new Some[A](x)
}

class Some[+A](x: A) extends Option[A] {
    def isEmpty = false

    def get = x
}

object None extends Option[Nothing] {
    def isEmpty = true

    def get = throw new NoSuchElementException("None.get")
}
