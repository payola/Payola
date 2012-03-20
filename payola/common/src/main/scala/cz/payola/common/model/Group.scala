package cz.payola.common.model

import scala.collection.mutable
import scala.collection.immutable

trait Group extends NamedEntity with OwnedEntity
{
    /** Type of the analysis shares that are associated with the group. */
    type AnalysisShareType <: AnalysisShare

    protected val _members: mutable.Seq[UserType]

    protected val _sharedAnalyses: mutable.Seq[AnalysisShareType]

    def members: immutable.Seq[UserType] = _members

    def sharedAnalyses: immutable.Seq[AnalysisShareType] = _sharedAnalyses
}
