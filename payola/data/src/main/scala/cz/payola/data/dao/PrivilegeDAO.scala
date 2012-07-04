package cz.payola.data.dao

import cz.payola.data.entities.privileges.PrivilegeDbRepresentation
import cz.payola.data.PayolaDB

class PrivilegeDAO extends EntityDAO[PrivilegeDbRepresentation](PayolaDB.privileges)
{

}
