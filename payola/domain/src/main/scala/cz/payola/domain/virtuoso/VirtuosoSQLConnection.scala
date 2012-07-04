package cz.payola.domain.virtuoso

import java.sql._

// i.e. jdbc:virtuoso://<server>:<port>
class VirtuosoSQLConnection(val username: String = "dba", val password: String = "dba", val server: String = "localhost", val port: Int = 1111)
{

    // Registers the driver in the drive manager
    Class.forName("virtuoso.jdbc3.Driver")

    /** Executes SQL query on the Virtuoso server. Is synchronized as by default,
      * only one connection to the server is allowed at a time.
      *
      * @param query Query.
      * @return String representing response. For debug purposes only.
      */
    def executeQuery(query: String): String = {
        // Need to have it synchronized in order not to create multiple connections
        // as by default virtuoso allows just one connection at a time
        this.synchronized {
            reallyExecuteQuery(query)
        }
    }

    /** Creates a new connection to the Virtuoso SQL server. Caller is responsible
      * for closing the connection before someone else can call this method again.
      *
      * @return
      */
    private def getConnection = DriverManager.getConnection("jdbc:virtuoso://" + server + ":" + port, username, password)

    /** Actual method implementing the query execution.
      *
      * @param query Query.
      * @return String representing server response. For debug purposes only.
      */
    private def reallyExecuteQuery(query: String): String = {
        println ("Executing SQL query " + query)

        val connection = getConnection
        val stmt: Statement = connection.createStatement
        val result: ResultSet = stmt.executeQuery(query)
        val meta: ResultSetMetaData = result.getMetaData
        val columnCount = meta.getColumnCount
        val builder = new StringBuilder()


        for (i <- 1 until columnCount) {
            val colName = meta.getColumnName(i)
            if (colName == null) {
                builder.append("NULL\t")
            }else{
                builder.append(colName)
                builder.append('\t')
            }
        }

        builder.append("\n--------------")

        while (result.next()) {
            for (i <- 1 until columnCount) {
                val resultValue = result.getString(i)
                if (resultValue == null) {
                    builder.append("NULL\t")
                }else{
                    builder.append(resultValue)
                    builder.append('\t')
                }
            }
        }

        stmt.close()
        connection.close()

        builder.toString()
    }

}
