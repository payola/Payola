package cz.payola.model.components

import cz.payola.data.DataContextComponent
import cz.payola.domain.entities._
import cz.payola.domain._
import cz.payola.model.EntityModelComponent

trait UserModelComponent extends EntityModelComponent
{
    self: DataContextComponent with PayolaStorageModelComponent with PrivilegeModelComponent =>

    lazy val userModel = new EntityModel(userRepository)
    {
        override def persist(entity: Entity) {
            uniqueKeyCheckedPersist(entity, "name")
        }

        def create(email: String, password: String): User = {
            val user = new User(email)
            user.password = cryptPassword(password)
            user.email = email
            persist(user)

            payolaStorageModel.createUsersPrivateStorage(user)

            user
        }

        def getByCredentials(name: String, password: String): Option[User] = {
            userRepository.getByCredentials(name, cryptPassword(password))
        }

        def getByName(name: String): Option[User] = {
            userRepository.getByName(name)
        }

        def getByEmail(email: String): Option[User] = {
            userRepository.getByEmail(email)
        }

        def getByNameLike(name: String): Seq[User] = {
            userRepository.getAllWithNameLike(name)
        }

        def changePasswordForUser(user: User, newPassword: String) {
            user.password = cryptPassword(newPassword)
        }

        def cryptPassword(password: String, method: String = "SHA-1"): String = {
            // TODO use bcrypt?
            val md = java.security.MessageDigest.getInstance(method)
            val digest = md.digest(password.toCharArray.map(_.toByte))
            new String(digest.map(_.toChar))
        }
    }
}
