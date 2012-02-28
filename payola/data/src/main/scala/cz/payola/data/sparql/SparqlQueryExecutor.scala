package cz.payola.data.sparql

import actors.Actor
import collection.mutable.ListBuffer
import messages.{ErrorMessage, ResultMessage}

class SparqlQueryExecutor(val query: String, val dataProvider: RdfDataProvider) extends Actor
{
    def act() {
        val expectedResultCount = dataProvider.executeSparqlQuery(query, this)
        val results = new ListBuffer[String]
        var errors = new ListBuffer[Throwable]

        loop {
            react {
                case ResultMessage(result) => {
                    results += result
                    checkResult()
                }
                case ErrorMessage(e) => {
                    errors += e
                    checkResult()
                }
            }
        }

        def checkResult() {
            if (results.length + errors.length == expectedResultCount) {
                println("Done with %s results and %s errors.".format(results.length, errors.length))
                exit()
            }
        }
    }
}
