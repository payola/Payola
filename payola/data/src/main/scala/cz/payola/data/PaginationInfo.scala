package cz.payola.data

/**
  * This class provides a way to paginate database query results.
  *
  * @param skip - how many item in result should be skipped (offset)
  * @param limit - how many item should be returned at most (count)
  */
case class PaginationInfo(val skip: Int = 0, val limit: Int = 0)
{
    require(skip >= 0, "'skip' parameter cann't be lower than zero")
    require(limit >= 0, "'limit' parameter cann't be lower than zero")
}
