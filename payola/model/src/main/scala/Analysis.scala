
package cz.payola.model

/**
 * Created by IntelliJ IDEA.
 * User: Krystof Vasa
 * Date: 21.11.11
 * Time: 11:02
 * To change this template use File | Settings | File Templates.
 */

import scala.collection.mutable._

class Analysis (var owner: User) {
  def isOwnedByUser(u: User): Boolean = owner == u

}
