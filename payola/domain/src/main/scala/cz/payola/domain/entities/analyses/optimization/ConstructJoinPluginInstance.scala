package cz.payola.domain.entities.analyses.optimization

import cz.payola.domain.entities.plugins.PluginInstance
import cz.payola.domain.entities.plugins.concrete.Join
import cz.payola.domain.entities.plugins.concrete.query.Construct

class ConstructJoinPluginInstance(
    val join: PluginWithInstance[Join],
    val subjectConstruct: PluginWithInstance[Construct],
    val objectConstruct: PluginWithInstance[Construct])
    extends PluginInstance(ConstructJoinPlugin, Nil)
