package cz.payola.domain.entities.privileges

import cz.payola.domain.IDGenerator
import cz.payola.domain.Entity
import cz.payola.domain.entities._
import cz.payola.domain.entities.settings._
import cz.payola.domain.entities.plugins.DataSource

class AccessAnalysisPrivilege(granter: User, grantee: Entity with PrivilegeableEntity, analysis: Analysis,
    id: String = IDGenerator.newId)
    extends Privilege[Analysis](granter, grantee, analysis, id)
    with cz.payola.common.entities.privileges.AccessAnalysisPrivilege

class AccessDataSourcePrivilege(granter: User, grantee: Entity with PrivilegeableEntity, dataSource: DataSource,
    id: String = IDGenerator.newId)
    extends Privilege[DataSource](granter, grantee, dataSource, id)
    with cz.payola.common.entities.privileges.AccessDataSourcePrivilege

class UsePluginPrivilege(granter: User, grantee: Entity with PrivilegeableEntity, plugin: Plugin,
    id: String = IDGenerator.newId)
    extends Privilege[Plugin](granter, grantee, plugin, id)
    with cz.payola.common.entities.privileges.UsePluginPrivilege

class UseCustomizationPrivilege(granter: User, grantee: Entity with PrivilegeableEntity,
    customization: Customization, id: String = IDGenerator.newId)
    extends Privilege[Customization](granter, grantee, customization, id)
    with cz.payola.common.entities.privileges.UseCustomizationPrivilege
