package cz.payola.web.shared

import org.apache.commons.mail.SimpleEmail
import s2js.compiler.remote

@remote private[shared] case class Email(
    subject: String,
    content: String = "",
    from: String,
    to: Seq[String] = Seq.empty)
{
    def send() {
        val email = new SimpleEmail
        email.setHostName(Payola.settings.smtpServer)
        email.setSmtpPort(Payola.settings.smtpPort)
        email.setAuthentication(Payola.settings.smtpUsername, Payola.settings.smtpPassword)
        email.setSSL(true)
        email.setFrom(from)
        to.foreach(email.addTo(_))
        email.setSubject(subject)
        email.setMsg(content)

        email.send()
    }
}
