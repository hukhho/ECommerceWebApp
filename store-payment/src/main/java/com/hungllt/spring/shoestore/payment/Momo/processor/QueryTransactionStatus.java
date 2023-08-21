package com.hungllt.spring.shoestore.payment.Momo.processor;

import com.hungllt.spring.shoestore.payment.Momo.config.Environment;
import com.hungllt.spring.shoestore.payment.Momo.enums.Language;
import com.hungllt.spring.shoestore.payment.Momo.models.QueryStatusTransactionRequest;
import com.hungllt.spring.shoestore.payment.Momo.models.QueryStatusTransactionResponse;
import com.mservice.shared.constants.Parameter;
import com.mservice.shared.exception.MoMoException;
import com.mservice.shared.sharedmodels.HttpResponse;
import com.mservice.shared.utils.Encoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class QueryTransactionStatus extends AbstractProcess<QueryStatusTransactionRequest, QueryStatusTransactionResponse> {
    public QueryTransactionStatus(Environment environment) {
        super(environment);
    }

    public static QueryStatusTransactionResponse process(Environment env, String orderId, String requestId) throws Exception {
        try {
            QueryTransactionStatus m2Processor = new QueryTransactionStatus(env);

            QueryStatusTransactionRequest request = m2Processor.createQueryTransactionRequest(orderId, requestId);
            QueryStatusTransactionResponse queryTransResponse = m2Processor.execute(request);

            return queryTransResponse;
        } catch (Exception exception) {
            log.error("[CreateOrderMoMoProcess] "+ exception);
        }
        return null;
    }

    @Override
    public QueryStatusTransactionResponse execute(QueryStatusTransactionRequest request) throws MoMoException {
        try {

            String payload = getGson().toJson(request, QueryStatusTransactionRequest.class);

            HttpResponse response = execute.sendToMoMo(environment.getMomoEndpoint().getQueryUrl(), payload);

            if (response.getStatus() != 200) {
                throw new MoMoException("[PaymentResponse] [" + request.getOrderId() + "] -> Error API");
            }

            System.out.println("uweryei7rye8wyreow8: "+ response.getData());

            QueryStatusTransactionResponse captureMoMoResponse = getGson().fromJson(response.getData(), QueryStatusTransactionResponse.class);
            String responserawData = Parameter.REQUEST_ID + "=" + captureMoMoResponse.getRequestId() +
                    "&" + Parameter.ORDER_ID + "=" + captureMoMoResponse.getOrderId() +
                    "&" + Parameter.MESSAGE + "=" + captureMoMoResponse.getMessage() +
                    "&" + Parameter.ERROR_CODE + "=" + captureMoMoResponse.getResultCode();

            log.info("[PaymentMoMoResponse] rawData: " + responserawData);

            return captureMoMoResponse;

        } catch (Exception exception) {
            log.error("[PaymentMoMoResponse] "+ exception);
            throw new IllegalArgumentException("Invalid params capture MoMo Request");
        }
    }

    public QueryStatusTransactionRequest createQueryTransactionRequest(String orderId, String requestId) {

        try {
            String requestRawData = new StringBuilder()
                    .append(Parameter.ACCESS_KEY).append("=").append(partnerInfo.getAccessKey()).append("&")
                    .append(Parameter.ORDER_ID).append("=").append(orderId).append("&")
                    .append(Parameter.PARTNER_CODE).append("=").append(partnerInfo.getPartnerCode()).append("&")
                    .append(Parameter.REQUEST_ID).append("=").append(requestId)
                    .toString();

            String signRequest = Encoder.signHmacSHA256(requestRawData, partnerInfo.getSecretKey());
            log.debug("[PaymentRequest] rawData: " + requestRawData + ", [Signature] -> " + signRequest);

            return new QueryStatusTransactionRequest(partnerInfo.getPartnerCode(), orderId, requestId, Language.EN, signRequest);
        } catch (Exception e) {
            log.error("[PaymentRequest] "+ e);
        }

        return null;
    }
}
