package cz.payola.data.squeryl

import org.squeryl.KeyedEntity
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.dsl.CompositeKey2

/**
 * Defines member-relation between user and group
 * @param memberId Id of the user (member)
 * @param groupId  Id of the group the user is member of
 */
class GroupMembership(val memberId: String, val groupId: String)
    extends KeyedEntity[CompositeKey2[String, String]]
{
    def id = compositeKey(memberId, groupId)
}

