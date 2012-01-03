package cz.payola.data

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers

/**
 * User: Ondřej Heřmánek
 * Date: 3.1.12, 14:44
 */

class QueryResultTest  extends FlatSpec with ShouldMatchers {
    "QueryResult" should "return result passed in constructor." in {
        val result = "Hello, world";

        val queryResult = new QueryResult(result);

        queryResult.getResult() should equal (result);
    }

    "QueryResult" should "properly parse result into nodes." in {
        // TODO:
    }
}