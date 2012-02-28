package cz.payola.common.rdf

/**
 * Created by IntelliJ IDEA.
 * User: jirihelmich
 * Date: 2/27/12
 * Time: 5:04 PM
 * To change this template use File | Settings | File Templates.
 */

class ListItem (var value: Int) {

    var prev: ListItem = null;
    var next: ListItem = null;

}