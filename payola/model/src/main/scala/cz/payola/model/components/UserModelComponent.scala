package cz.payola.model.components

import cz.payola.data.DataContextComponent
import cz.payola.domain.entities.User
import cz.payola.domain.RdfStorageComponent
import cz.payola.model.EntityModelComponent

trait UserModelComponent extends EntityModelComponent
{self: DataContextComponent with RdfStorageComponent =>
    lazy val userModel = new EntityModel(userRepository)
    {
        def create(username: String, password: String) {
            val user = new User(username)
            user.password = cryptPassword(password)
            user.email = username
            repository.persist(user)

            // TODO create his private data storage and data source.
        }

        def getByCredentials(username: String, password: String): Option[User] = {
            // TODO repository.getUserByCredentials(username, cryptPassword(password))
            None
        }

        def getByUsername(username: String): Option[User] = {
            // TODO userDAO.getUserByUsername(username)
            None
        }

        // TODO bcrypt
        private def cryptPassword(password: String, method: String = "SHA-1"): String = {
            val md = java.security.MessageDigest.getInstance(method)
            val digest = md.digest(password.toCharArray.map(_.toByte))
            new String(digest.map(_.toChar))
        }
    }
}
