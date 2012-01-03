package cz.payola.data;

class FakeWebService extends IPayolaWebService {
    def evaluateSparqlQuery(query: String): String = {
        val result = xml.XML.loadFile("data.xml").toString();

        print(result);

        return result;
    }

    def getRelatedItems(id: String, relationType: String): String = {
        return xml.XML.loadFile("data.xml").toString();
    }
}