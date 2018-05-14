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

    public Mono<Deal> qrCodeRequest(Mono<MerchantConfig> merchantConfig, String amount, String remark ){
        Mono<Message> request = qrCodeClient.createRequest(merchantConfig, amount, remark);
        return request
                .flatMap(req->qrCodeClient.send(req))
                .flatMap((data)->{
                    Deal record = new Deal();
                    if(data.isSuccess()){
                        record.setTranStatus(Deal.WAIT_FOR_PAY);
                    }else{
                        record.setTranStatus(Deal.TRAN_FAILED);
                    }
                    record.setRetCode(data.getRetCode());
                    record.setRetMsg(data.getRetMsg());
                    record.setQrCodeSource(data.getQrCode());
                    return Mono.just(record);
                }).zipWith(request, (deal, req)->{
                    deal.setTranType("0");
                    deal.setMerchantNo(req.getMerchantNo());
                    deal.setReqSn(req.getReqSn());
                    deal.setAmount(req.getTranAmount());
                    deal.setTranDate(new Date());
                    deal.setRandomStr(req.getRandomStr());
                    deal.setOrderTitle(req.getOrderTitle());
                    deal.setRemark(req.getRemark());
//                    deal.setValidTime(req.getValidTime());
                    return deal;
                }).flatMap(deal->repository.save(deal));
    }

}
