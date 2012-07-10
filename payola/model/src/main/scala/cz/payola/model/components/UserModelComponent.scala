package cz.payola.model.components

import cz.payola.data.DataContextComponent
import cz.payola.domain.entities.User
import cz.payola.domain.RdfStorageComponent
import cz.payola.model.EntityModelComponent

trait UserModelComponent extends EntityModelComponent
{
    self: DataContextComponent with RdfStorageComponent =>

    lazy val userModel = new EntityModel(userRepository)
    {
        def create(email: String, password: String) {
            val user = new User(email)
            user.password = cryptPassword(password)
            user.email = email
            repository.persist(user)

            // TODO create his private data storage and data source.
        }

        def getByCredentials(name: String, password: String): Option[User] = {
            userRepository.getByCredentials(name, cryptPassword(password))
        }

        def getByName(name: String): Option[User] = {
            userRepository.getByName(name)
        }

        // TODO bcrypt
        private def cryptPassword(password: String, method: String = "SHA-1"): String = {
            val md = java.security.MessageDigest.getInstance(method)
            val digest = md.digest(password.toCharArray.map(_.toByte))
            new String(digest.map(_.toChar))
        }
    }
}
