package cz.payola.web.shared.managers

import scala.collection.mutable
import cz.payola.common.entities.User
import cz.payola.web.shared._
import cz.payola.common.PayolaException
import scala.actors._
import cz.payola.web.shared.Email
import s2js.compiler.remote

@remote
object PasswordManager
{

    // UUID -> (email, new password)
    lazy private val recoveryHashMap: mutable.HashMap[String, (String, String)] = new mutable.HashMap[String, (String, String)]()

    class HashMapCleaner(val timeout: Long, val uuid: String) extends Actor {
        def act() {
            reactWithin(timeout) {
                case TIMEOUT => {
                    recoveryHashMap.remove(uuid)
                }
            }
        }
    }

    @remote def sendRecoveryEmailToUser(uuid: String, user: User, newPassword: String) {
        recoveryHashMap.put(uuid, (user.id, newPassword))

        // Remove the UUID from the hash map after 2 hours
        val t = new HashMapCleaner(7200000, uuid)
        t.start()

        val content =
            """
              |Hello,
              |
              |please, follow this link to confirm your password reset:
              |
              |%s/reset/%s
            """.stripMargin.format(Payola.settings.websiteURL, uuid)

        val email = new Email("Reset Your Payola Password", content, Payola.settings.websiteNoReplyEmail, List(user.email))
        email.send()
    }

    @remote def confirmPasswordReset(uuid: String): Boolean = {
        if (recoveryHashMap.contains(uuid)){
            val tup = recoveryHashMap.get(uuid).get

            val userOpt = Payola.model.userModel.getById(tup._1)
            if (userOpt.isEmpty){
                throw new PayolaException("User disappeared.")
            }

            Payola.model.userModel.changePasswordForUser(userOpt.get, tup._2)
            Payola.model.userModel.persist(userOpt.get)

            recoveryHashMap.remove(uuid)
            true
        }else{
            false
        }
    }

}
