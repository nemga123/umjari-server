package com.umjari.server.global.mail

interface MailSenderInterface {
    val subject: String
    val template: String
    fun sendMail(receiverEmail: String, contextVariables: Map<String, String>)
}
