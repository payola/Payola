package cz.payola.data

import java.sql.SQLException
import cz.payola.common.exception.PayolaException

object DataException
{
    def wrap[A](body: => A): A = {
        try {
            body
        } catch {
            case dataException: DataException => {
                println(dataException.message) // TODO

                throw dataException
            }
            case throwable: Throwable => {
                throwable match {
                    case e: Exception => {
                        // TODO
                        e.printStackTrace()
                    }
                }
                throw new DataException("An exception was thrown in the data layer.", throwable)
            }
        }
    }
}

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
