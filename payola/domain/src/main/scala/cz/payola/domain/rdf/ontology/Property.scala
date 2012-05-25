package cz.payola.domain.rdf.ontology

/**
  * Created with IntelliJ IDEA.
  * User: charliemonroe
  * Date: 5/22/12
  * Time: 5:41 PM
  * To change this template use File | Settings | File Templates.
  */

class Property(val URI: String)
{
    override def toString = super.toString + " => " + URI
}
