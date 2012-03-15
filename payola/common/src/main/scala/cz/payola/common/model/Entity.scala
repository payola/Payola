package cz.payola.common.model

trait Entity
{
    protected val _id: String
    
    def id = _id
}
