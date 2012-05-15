package cz.payola.domain.entities.analyses.optimization

import cz.payola.domain.entities.analyses.PluginInstance
import cz.payola.domain.entities.analyses.plugins.Join
import cz.payola.domain.entities.analyses.plugins.query.Construct

class ConstructJoinPluginInstance(
    val join: PluginWithInstance[Join],
    val subjectConstruct: PluginWithInstance[Construct],
    val objectConstruct: PluginWithInstance[Construct])
    extends PluginInstance(ConstructJoinPlugin, Nil)
