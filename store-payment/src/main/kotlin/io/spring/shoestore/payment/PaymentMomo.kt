package io.spring.shoestore.payment

import com.hungllt.spring.shoestore.payment.Momo.config.Environment
import com.hungllt.spring.shoestore.payment.Momo.enums.RequestType
import com.hungllt.spring.shoestore.payment.Momo.processor.CreateOrderMoMo
import com.hungllt.spring.shoestore.payment.Momo.processor.QueryTransactionStatus

class PaymentMomo() {
    fun createPaymentRequest(
        requestId: String,
        orderId: String,
        amount: Long,
        orderInfo: String,
        returnURL: String,
        notifyURL: String,
    ): String {
//        var requestId = System.currentTimeMillis().toString()
        val environment = Environment.selectEnv("dev")
        val captureWalletMoMoResponse = CreateOrderMoMo.process(
            environment,
            orderId,
            requestId,
            java.lang.Long.toString(amount),
            orderInfo,
            returnURL,
            notifyURL,
            "",
            RequestType.CAPTURE_WALLET
        )
        ////        Transaction Query - Kiểm tra trạng thái giao dịch
        //   QueryStatusTransactionResponse queryStatusTransactionResponse = QueryTransactionStatus.process(environment, orderId, requestId);
        ////        Transaction Query - Kiểm tra trạng thái giao dịch
        //   QueryStatusTransactionResponse queryStatusTransactionResponse = QueryTransactionStatus.process(environment, orderId, requestId);
        ////        Transaction Refund - hoàn tiền giao dịch
        val transId = 2L
        //  RefundMoMoResponse refundMoMoResponse = RefundTransaction.process(environment, orderId, requestId, Long.toString(amount), transId, "");
        val payUrl = captureWalletMoMoResponse.payUrl
        return "$payUrl"
    }

    fun queryPaymentRequest(
        orderId: String,
        requestId: String
    ): String {
        val environment = Environment.selectEnv("dev")
        val queryStatusTransactionResponse = QueryTransactionStatus.process(environment, orderId, requestId);
        ////        Transaction Query - Kiểm tra trạng thái giao dịch
        //   QueryStatusTransactionResponse queryStatusTransactionResponse = QueryTransactionStatus.process(environment, orderId, requestId);
        ////        Transaction Query - Kiểm tra trạng thái giao dịch
        //   QueryStatusTransactionResponse queryStatusTransactionResponse = QueryTransactionStatus.process(environment, orderId, requestId);
        ////        Transaction Refund - hoàn tiền giao dịch
        val transId = 2L
        //  RefundMoMoResponse refundMoMoResponse = RefundTransaction.process(environment, orderId, requestId, Long.toString(amount), transId, "");
        val message = queryStatusTransactionResponse.message
        return "$message"
    }
}