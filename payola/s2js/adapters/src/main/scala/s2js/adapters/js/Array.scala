package s2js.adapters.js

class Array[A <: Any]
{
    @native def concat(a: Array[A])

    @native def indexOf(item: A): Int

    @native def join(separator: String): String

    @native def lastIndexOf(item: A): Int

    @native def pop(): A

    @native def push(item: A): Int

    @native def reverse()

    @native def shift(): A

    @native def slice(start: Int, end: Int): Array[A]

    @native def sort()

    @native def splice(index: Int, howmany: Int): Array[A]

    @native def unshift(item: A): Int
}
