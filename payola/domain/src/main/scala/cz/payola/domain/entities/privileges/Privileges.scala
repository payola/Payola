package cz.payola.domain.entities.privileges

import cz.payola.domain.IDGenerator
import cz.payola.domain.Entity
import cz.payola.domain.entities._
import cz.payola.domain.entities.settings.OntologyCustomization
import cz.payola.domain.entities.plugins.DataSource

class AccessAnalysisPrivilege(granter: User, grantee: Entity with PrivilegableEntity, analysis: Analysis,
    id: String = IDGenerator.newId)
    extends Privilege[Analysis](granter, grantee, analysis, id)
    with cz.payola.common.entities.privileges.AccessAnalysisPrivilege

class AccessDataSourcePrivilege(granter: User, grantee: Entity with PrivilegableEntity, dataSource: DataSource,
    id: String = IDGenerator.newId)
    extends Privilege[DataSource](granter, grantee, dataSource, id)
    with cz.payola.common.entities.privileges.AccessDataSourcePrivilege

class UsePluginPrivilege(granter: User, grantee: Entity with PrivilegableEntity, plugin: Plugin,
    id: String = IDGenerator.newId)
    extends Privilege[Plugin](granter, grantee, plugin, id)
    with cz.payola.common.entities.privileges.UsePluginPrivilege

class UseOntologyCustomizationPrivilege(granter: User, grantee: Entity with PrivilegableEntity,
    customization: OntologyCustomization, id: String = IDGenerator.newId)
    extends Privilege[OntologyCustomization](granter, grantee, customization, id)
    with cz.payola.common.entities.privileges.UseOntologyCustomizationPrivilege
