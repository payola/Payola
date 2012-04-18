package cz.payola.domain.entities

class DataSource(protected var _name: String, protected val _owner: Option[User])
    extends Entity with OptionallyOwnedEntity with NamedEntity with ShareableEntity
    with cz.payola.common.entities.DataSource
{
    protected var _isPublic = false
}
