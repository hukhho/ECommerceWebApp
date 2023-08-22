package io.spring.shoestore.app.utils

import io.spring.shoestore.app.exception.AppException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import org.thymeleaf.TemplateEngine


@Service
class EmailService {
    @Autowired
    private val mailSender: JavaMailSender? = null

    @Autowired
    private lateinit var templateEngine: TemplateEngine
    fun sendSimpleEmail(to: String?, subject: String?, text: String?) {
        val message = SimpleMailMessage()
        message.setFrom("hungllt@PreOrderPlatform.store")  // Set the From address
        message.setTo(to)
        message.setSubject(subject)
        message.setText(text)
        mailSender!!.send(message)
    }
    fun sendOrderConfirmationEmail(to: String, orderId: String, orderDate: String, totalAmount: String) {
        // Load the HTML content from a file or another source
        val htmlContent = this::class.java.getResource("/templates/orderEmailConfirmation.html").readText()
            .replace("{{orderId}}", orderId)
            .replace("{{orderDate}}", orderDate)
            .replace("{{totalAmount}}", totalAmount)

        if (mailSender == null) {
            throw AppException("Send mail error!")
        }
        val message = mailSender.createMimeMessage()
        val mimeMessageHelper = MimeMessageHelper(message, true)

        mimeMessageHelper.setFrom("hungllt@PreOrderPlatform.store")
        mimeMessageHelper.setTo(to)
        mimeMessageHelper.setSubject("Order Confirmation")
        mimeMessageHelper.setText(htmlContent, true)  // true = isHtml

        mailSender.send(message)
    }


}

