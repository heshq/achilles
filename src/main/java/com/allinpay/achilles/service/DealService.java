package com.allinpay.achilles.service;

import com.allinpay.achilles.core.BusinessException;
import com.allinpay.achilles.core.Message;
import com.allinpay.achilles.core.QRCodeClient;
import com.allinpay.achilles.domian.Deal;
import com.allinpay.achilles.domian.MerchantConfig;
import com.allinpay.achilles.repository.DealRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

import java.util.Date;


@Service
public class DealService {

    @Autowired
    private DealRepository repository;
    @Autowired
    private QRCodeClient qrCodeClient;

    public Mono<Deal> findById(String id){
        return repository.findById(id);
    }

    public Mono<Deal> findByReqSn(String reqSn){
        if(StringUtils.isEmpty(reqSn)){
            throw new BusinessException("查询的交易流失号不能为空");
        }
        return repository.findByReqSn(reqSn);
    }

    public Mono<Deal> findByReqSnAndTranType(String reqSn, String tranType){
        if(StringUtils.isEmpty(reqSn)){
            throw new BusinessException("交易流水号不能为空");
        }
        return repository.findByReqSnAndTranType(reqSn, tranType);
    }

    public Mono<Deal> save(Deal deal){
        return repository.save(deal);
    }

    public Mono<Deal> qrCodeRequest(Mono<MerchantConfig> merchantConfig, String amount, String remark ){
        Mono<Message> request = qrCodeClient.createRequest(merchantConfig, amount, remark);
        return request
                .flatMap(req->qrCodeClient.send(req))
                .zipWith(request)
                .flatMap(data->{
                    Message res = data.getT1();
                    Message req = data.getT2();
                    Deal record = new Deal();
                    if(res.isSuccess()){
                        record.setTranStatus(Deal.WAIT_FOR_PAY);
                    }else{
                        record.setTranStatus(Deal.TRAN_FAILED);
                    }
                    record.setReqSn(res.get("trxid"));//通联流水号
                    record.setChannelSn(res.get("chnltrxid"));//渠道流水号
                    record.setQrCodeSource(res.getQrCode());
                    record.setOrderNo(req.getReqSn());//商户订单号
                    record.setTranType(Deal.TRAN_TYPE_WECHAT);
                    record.setMerchantNo(req.getMerchantNo());
                    record.setAmount(req.getTranAmount());
                    record.setTranDate(new Date());
                    record.setRandomStr(req.getRandomStr());
                    record.setOrderTitle(req.getOrderTitle());
                    record.setRemark(req.getRemark());
                    record.setRetCode(res.getRetCode());
                    record.setRetMsg(res.getRetMsg());
                    return Mono.just(record);
                }).flatMap(deal->repository.save(deal));
    }

}
