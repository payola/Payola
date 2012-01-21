package cz.payola.data

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers

class QueryResultTest  extends FlatSpec with ShouldMatchers {
    "QueryResult" should "return result passed in constructor." in {
        val rdf = "RDF: Hello, world";
        val ttl = "TTL: Hello, world";

        val queryResult = new QueryResult(rdf, ttl);

        queryResult.getRdf() should equal (rdf);
        queryResult.getTtl() should equal (ttl);
    }

    "QueryResult" should "properly parse result into nodes." in {
        // TODO:
    }
}
