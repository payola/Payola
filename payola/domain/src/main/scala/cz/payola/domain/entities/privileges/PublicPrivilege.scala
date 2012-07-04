package cz.payola.domain.entities.privileges

/**
  * Any privilege that extends this trait is considered public and hence safe to be transferred to the
  * client side - i.e. serialized. The User class will filter the privileges and return only those that
  * are public.
  */
trait PublicPrivilege
