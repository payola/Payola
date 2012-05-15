package cz.payola.data.entities

class DataSource(name: String, owner: Option[User])
    extends cz.payola.domain.entities.DataSource(name, owner)
{
}
