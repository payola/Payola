package cz.payola.web.shared

import cz.payola.common.rdf.Graph
import cz.payola.model.DataFacade
import s2js.compiler.async
import s2js.shared.RPCException

/**
  *
  * @author jirihelmich
  * @created 3/19/12 5:49 PM
  * @package cz.payola.web.shared
  */

@remote
object RPCTester
{
    def procedure : Int = {
        1
    }
    
    def testString : String = {
        """te"st"""
    }

    def testBoolean : Boolean = {
        true
    }

    def testParamInt (param: Int) : Int = {
        (param*2)
    }

    def testParamString (param: String): String = {
        param.reverse
    }

    def testParamChar (param: Char): Char = {
        param
    }

    def testParamBoolean (param: Boolean): Boolean = {
        !param
    }

    def testParamDouble (param: Double): Double = {
        param
    }

    def testParamFloat (param: Float): Float = {
        param
    }

    def testParamArray (param: List[Int]): Int = {
        param.sum
    }

    @async
    def testParamArrayAsync (param: List[Int])(successCallback: (Int => Unit))(failCallback: (Throwable => Unit)) = {
        successCallback(param.sum)
    }

    def testParamArrayDouble (param: List[Double]): Double = {
        param.sum
    }

    def testParamArrayString (param: List[String]): String = {
        param.mkString("")
    }

    def throwException : Graph = {
        throw new RPCException("Was lazy to do this.")
    }

    def testGraph : Graph = {
        (new DataFacade).getGraph("http://payola.cz")
    }
    
    def testException : Int = {
        7
    }
}
