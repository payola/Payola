package cz.payola.domain.entities

trait OptionallyOwnedEntity extends cz.payola.common.entities.OptionallyOwnedEntity
{
    type UserType = User
}
