package cz.payola.model

import cz.payola.data._

trait EntityModelComponent
{
    self: DataContextComponent =>

    class EntityModel[+A](val repository: Repository[A])
    {
        def getById(id: String): Option[A] = repository.getById(id)

        def getAll: Seq[A] = repository.getAll()
    }
}
