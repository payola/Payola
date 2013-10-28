package cz.payola.common.entities.privileges

import cz.payola.common.entities._
import cz.payola.common.entities.settings._
import cz.payola.common.entities.plugins.DataSource

/**
  * The user may access the specified analysis, so he can see the analysis structure and the plugin instances it
  * consists of, run the analysis and browse the result.
  */
trait AccessAnalysisPrivilege extends Privilege[Analysis]

/**
  * The user may browser the specified data source and use it in his analyses.
  */
trait AccessDataSourcePrivilege extends Privilege[DataSource]

/**
  * The user may use the specified plugin in his analyses.
  */
trait UsePluginPrivilege extends Privilege[Plugin]

/**
 * The user may apply the specified user customization to the results of analyses.
 */
trait UseCustomizationPrivilege extends Privilege[Customization]
