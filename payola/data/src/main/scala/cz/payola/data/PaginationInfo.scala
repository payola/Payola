package cz.payola.data

/**
 * Provides a way to paginate database query results.
 * @param skip How many item in result should be skipped (offset).
 * @param limit How many item should be returned at most (count).
 */
case class PaginationInfo(skip: Int = 0, limit: Int = 0)
{
    require(skip >= 0, "The skip parameter can't be lower than zero.")
    require(limit >= 0, "The limit parameter can't be lower than zero.")
}
