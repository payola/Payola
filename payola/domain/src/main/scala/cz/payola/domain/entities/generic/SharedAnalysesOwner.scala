package cz.payola.domain.entities.generic

trait SharedAnalysesOwner
{
    // Shared analysis. Initially only IDs are loaded, actual shares are loaded from the
    // data layer as needed
    /*protected val _sharedAnalyses: ArrayBuffer[AnalysisShare] = new ArrayBuffer[AnalysisShare]()
    private val _sharedAnalysesIDs: ArrayBuffer[String] = new ArrayBuffer[String]()


    /** Adds an analysis share to the group.
      *
      * @param a The share.
      *
      * @throws IllegalArgumentException if the analysis share is null.
      */
    def addSharedAnalysis(a: AnalysisShare) = {
        require(a != null, "Cannot share null analysis share")
        if (!_sharedAnalysesIDs.contains(a.id)){
            _sharedAnalysesIDs += a.id
            _sharedAnalyses += a
        }
    }

    /** Returns true if this particular share has been shared with this group.
      *
      * @param share The share.
      *
      * @return Returns true if this particular share has been shared with this group.
      */
    def containsSharedAnalysis(share: AnalysisShare): Boolean = _sharedAnalysesIDs.contains(share.id)

    /** Results in true if this group has the analysis shared.
      *
      * @param a Analysis.
      *
      * @return True or false.
      */
    def hasAccessToSharedAnalysis(a: Analysis): Boolean = _sharedAnalyses.exists(_.analysis == a)

    /** Removes the passed analysis share from the group's analysis shares.
      *
      * @param a Analysis share to be removed.
      *
      * @throws IllegalArgumentException if the analysis is null.
      */
    def removeSharedAnalysis(a: AnalysisShare) = {
        require(a != null, "Cannot remove null analysis!")

        _sharedAnalysesIDs -= a.id
        _sharedAnalyses -= a
    }

    /** Returns an immutable array of analysis shared with this group.
      *
      * @return An immutable array of analysis shared with this group.
      */
    /*def sharedAnalyses = {
        val analyses = List[AnalysisShare]()
        _sharedAnalysesIDs foreach { shareID: String =>
            val a: Option[AnalysisShare] = _cachedAnalysisShares.get(shareID)
            if (a.isEmpty){
                // TODO loading from DB
            }else{
                a.get :: analyses
            }
        }
        analyses.reverse
    }*/

    /** Returns an analysis share at index. Will raise an exception if the index is out of bounds.
      * The analysis share will be loaded from DB if necessary.
      *
      * @param index Index of the analysis share (according to the AnalysesIDs).
      * @return The analysis share.
      */
    def sharedAnalysisAtIndex(index: Int): AnalysisShare = {
        require(index >= 0 && index < sharedAnalysisCount, "Shared analysis index out of bounds - " + index)
        _sharedAnalyses(index)
    }

    /** Number of shared analyses.
      *
      * @return Number of shared analyses.
      */
    def sharedAnalysisCount: Int = _sharedAnalysesIDs.size*/
}
