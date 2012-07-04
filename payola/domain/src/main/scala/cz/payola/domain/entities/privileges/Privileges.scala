package cz.payola.domain.entities.privileges

import cz.payola.domain.entities._
import cz.payola.domain.entities.settings.ontology.Customization
import cz.payola.domain.entities.plugins.DataSource

class AccessAnalysisPrivilege(analysis: Analysis)
    extends Privilege[Analysis](analysis)
    with cz.payola.common.entities.privileges.AccessAnalysisPrivilege

class AccessDataSourcePrivilege(dataSource: DataSource)
    extends Privilege[DataSource](dataSource)
    with cz.payola.common.entities.privileges.AccessDataSourcePrivilege

class UsePluginPrivilege(plugin: Plugin)
    extends Privilege[Plugin](plugin)
    with cz.payola.common.entities.privileges.UsePluginPrivilege

class UseOntologyCustomizationPrivilege(customization: Customization)
    extends Privilege[Customization](customization)
    with cz.payola.common.entities.privileges.UseOntologyCustomizationPrivilege
