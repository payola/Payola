package cz.payola.domain.entities.privileges

import cz.payola.domain.entities._
import cz.payola.domain.entities.settings.ontology.Customization
import cz.payola.domain.entities.plugins.DataSource
import cz.payola.domain.IDGenerator

class AccessAnalysisPrivilege(analysis: Analysis, id: String = IDGenerator.newId)
    extends Privilege[Analysis](analysis, id)
    with cz.payola.common.entities.privileges.AccessAnalysisPrivilege

class AccessDataSourcePrivilege(dataSource: DataSource, id: String = IDGenerator.newId)
    extends Privilege[DataSource](dataSource, id)
    with cz.payola.common.entities.privileges.AccessDataSourcePrivilege

class UsePluginPrivilege(plugin: Plugin, id: String = IDGenerator.newId)
    extends Privilege[Plugin](plugin, id)
    with cz.payola.common.entities.privileges.UsePluginPrivilege

class UseOntologyCustomizationPrivilege(customization: Customization, id: String = IDGenerator.newId)
    extends Privilege[Customization](customization, id)
    with cz.payola.common.entities.privileges.UseOntologyCustomizationPrivilege
