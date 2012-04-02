package cz.payola.common.entities

trait Entity
{
    protected val _id: String
    
    def id = _id
}
