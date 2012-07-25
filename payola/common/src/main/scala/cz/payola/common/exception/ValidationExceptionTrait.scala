package cz.payola.common.exception

trait ValidationExceptionTrait
{
    val fieldName: String
    val message: String
    val cause: Throwable

}


