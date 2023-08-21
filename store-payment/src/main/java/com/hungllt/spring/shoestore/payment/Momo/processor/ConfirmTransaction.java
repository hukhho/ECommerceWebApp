package com.hungllt.spring.shoestore.payment.Momo.processor;

import com.hungllt.spring.shoestore.payment.Momo.config.Environment;
import com.hungllt.spring.shoestore.payment.Momo.enums.ConfirmRequestType;
import com.hungllt.spring.shoestore.payment.Momo.enums.Language;
import com.hungllt.spring.shoestore.payment.Momo.models.ConfirmRequest;
import com.hungllt.spring.shoestore.payment.Momo.models.ConfirmResponse;
import com.mservice.shared.constants.Parameter;
import com.mservice.shared.exception.MoMoException;
import com.mservice.shared.utils.Encoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConfirmTransaction extends AbstractProcess<ConfirmRequest, ConfirmResponse> {
    public ConfirmTransaction(Environment environment) {
        super(environment);
    }

    public static ConfirmResponse process(Environment env, String orderId, String requestId, String amount, Long transId, String description) throws Exception {
        try {
            ConfirmTransaction m2Processor = new ConfirmTransaction(env);

            ConfirmRequest request = m2Processor.createConfirmTransactionRequest(orderId, requestId, amount, transId, description);
            ConfirmResponse response = m2Processor.execute(request);

            return response;
        } catch (Exception exception) {
            log.error("[ConfirmTransactionRequest] "+ exception);
        }
        return null;
    }

    @Override
    public ConfirmResponse execute(ConfirmRequest request) throws MoMoException {
        return null;
    }

    public ConfirmRequest createConfirmTransactionRequest(String orderId, String requestId, String amount, Long transId, String description) {

        try {
            String requestRawData = new StringBuilder()
                    .append(Parameter.ACCESS_KEY).append("=").append(partnerInfo.getAccessKey()).append("&")
                    .append(Parameter.AMOUNT).append("=").append(amount).append("&")
                    .append("description").append("=").append(description).append("&")
                    .append(Parameter.ORDER_ID).append("=").append(orderId).append("&")
                    .append(Parameter.PARTNER_CODE).append("=").append(partnerInfo.getPartnerCode()).append("&")
                    .append(Parameter.REQUEST_ID).append("=").append(requestId).append("&")
                    .append("transId").append("=").append(transId)
                    .toString();

            String signRequest = Encoder.signHmacSHA256(requestRawData, partnerInfo.getSecretKey());
            log.debug("[RefundRequest] rawData: " + requestRawData + ", [Signature] -> " + signRequest);

            return new ConfirmRequest(partnerInfo.getPartnerCode(), orderId, requestId, Language.EN, Long.valueOf(amount), "", ConfirmRequestType.CAPTURE, signRequest);
        } catch (Exception e) {
            log.error("[RefundResponse] "+ e);
        }

        return null;
    }
}
