package cz.payola.data

import java.sql.SQLException
import cz.payola.common.PayolaException

/**
 * This companion object to DataException class provides a way to wrap the code in try-catch block.
 * When and exception is caught in this block, it is wrapped into DataException which is thrown instead.
 */
object DataException
{
    /**
     * Wraps provided code block in try-catch block.
     * When an exception (different from DataException) is thrown in the code block, is wrapped into new DataExcpetion,
     * which is thrown instead.
     *
     * @param body
     * @tparam A Return type of the code block
     * @return Returns result of the code block or an DataException is thrown
     */
    def wrap[A](body: => A): A = {
        try {
            body
        } catch {
            case dataException: DataException => {
                throw dataException
            }
            case throwable: Throwable => {
                println(throwable.getMessage)
                throwable.printStackTrace()
                throw new DataException("An exception was thrown in the data layer.", throwable)
            }
        }
    }
}

/**
 * When some operation in Data layer results in a exception, DataException should be thrown,
 * with the cause wrapped inside.
 *
 * @param message Optional message to DataException
 * @param cause Optional cause of DataException
 */
class DataException(message: String = "", cause: Throwable = null) extends PayolaException(message, cause)
{
    /**
     * Returns whether the cause is a violation of an unique key in the database.
     * @see ftp://ftp.software.ibm.com/ps/products/db2/info/vr6/htm/db2m0/db2m002.htm#ToC_82
     */
    def isUniqueKeyViolation: Boolean = {
        cause match {
            case r: RuntimeException => r.getCause match {
                case s: SQLException => s.getSQLState == "23505"
                case _ => false
            }
            case _ => false
        }
    }
}
