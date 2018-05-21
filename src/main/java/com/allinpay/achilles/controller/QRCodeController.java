package com.allinpay.achilles.controller;

import com.allinpay.achilles.core.BusinessException;
import com.allinpay.achilles.core.Message;
import com.allinpay.achilles.domian.Deal;
import com.allinpay.achilles.domian.MerchantConfig;
import com.allinpay.achilles.domian.QRCodeNotice;
import com.allinpay.achilles.domian.Request;
import com.allinpay.achilles.service.DealService;
import com.allinpay.achilles.service.MerchantConfigService;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("qrCode")
public class QRCodeController {

    private static final Logger logger = LoggerFactory.getLogger(QRCodeController.class);
    @Autowired
    private DealService service;
    @Autowired
    private MerchantConfigService configService;
    @Autowired
    private DealService dealService;
    private static final int QR_CODE_IMG_SIDE_LENGTH = 160;
    private static final String QR_CODE_FORMAT = "png";

    @RequestMapping("/deal/{id}")
    @ResponseBody
    public Mono<Deal> findById(@PathVariable("id") String id){
        return service.findById(id);
    }

    @RequestMapping("/dealReqSn/{reqSn}")
    @ResponseBody
    public Mono<Deal> findByReqSn(@PathVariable("reqSn") String reqSn){
        return service.findByReqSn(reqSn);
    }

    @RequestMapping("/init")
    public Mono<Rendering> initQRCode(Request request){
        if(StringUtils.isEmpty(request.getMerchantCode())){
            return Mono.just(Rendering.view(Constant.ERROR_PAGE)
                    .modelAttribute(Constant.ERROR_MESSAGE_LABEL, "商户编码不能为空")
                    .build());
        }
        String val = null;
        final Mono<MerchantConfig> config = configService.findByMerchantCode(request.getMerchantCode());
        try {
            Double doubleVal = Double.parseDouble(request.getAmount());
            //金额转换为分
            val = String.valueOf(Double.valueOf(doubleVal * 100).intValue());
        }catch (NumberFormatException ex){
            return config.flatMap(data->Mono.just(Rendering.view(Constant.ORDER_PAGE)
                    .modelAttribute(Constant.ERROR_MESSAGE_LABEL, "金额格式不正确")
                            .modelAttribute("merchantConfig", data)
                            .build()));
        }
        final Mono<Deal> deal = service.qrCodeRequest(config, val, request.getRemark());
        return deal.flatMap(data->{
            if(Message.SUCCESS.equalsIgnoreCase(data.getRetCode())){
                return Mono.just(Rendering.view(Constant.QR_CODE_PAGE)
                        .modelAttribute("merchantConfig", config)
                        .modelAttribute("amount", request.getAmount())
                        .modelAttribute("remark", request.getRemark())
                        .modelAttribute("qrCodeUrl", "/qrCode/img/" + data.getReqSn())
                        .build());
            }else{
                return Mono.just(Rendering.view(Constant.ORDER_PAGE)
                        .modelAttribute("merchantConfig", config)
                        .modelAttribute(Constant.ERROR_MESSAGE_LABEL, data.getRetMsg())
                        .build());
            }
        });
    }

    private ResponseEntity<byte[]> createQRCode(String url, int width, int height, String format) {
        Map<EncodeHintType, Object> hints = new HashMap();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        BitMatrix bitMatrix = null;
        try{
            bitMatrix = new MultiFormatWriter().encode(url,
                    BarcodeFormat.QR_CODE, width, height, hints);// 生成矩阵
        }catch (WriterException ex){
            logger.error("生成二维码失败，错误信息：{}", ex.getMessage());
            throw new BusinessException("生成二维码失败，请再次尝试");
        }
        //将矩阵转为Image
        BufferedImage image = MatrixToImageWriter.toBufferedImage(bitMatrix);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try{
            ImageIO.write(image, format, out);//将BufferedImage转成out输出流
        }catch (IOException ex){
            logger.error("生成二维码失败，系统读写异常,异常信息:{}", ex.getMessage());
            throw new BusinessException("生成二维码失败，请再次尝试");
        }
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(out.toByteArray());
    }

    @RequestMapping("/img/{reqSn}")
    public Mono<ResponseEntity<byte[]>> createQRCode(@PathVariable String reqSn) {
        return service.findByReqSn(reqSn)
                .flatMap((data)->Mono.just(createQRCode(data.getQrCodeSource(), QR_CODE_IMG_SIDE_LENGTH, QR_CODE_IMG_SIDE_LENGTH, QR_CODE_FORMAT)))
                .switchIfEmpty(Mono.just(ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM).body("".getBytes())));
    }

    @RequestMapping(value = "notice")
    @ResponseBody
    public Mono<String> notice(QRCodeNotice notice){
        logger.info("接收到二维码支付通知");
        return dealService.findByReqSnAndTranType(notice.getOuttrxid(), Deal.TRAN_TYPE_WECHAT)
                .defaultIfEmpty(new Deal())
                .flatMap(data->syncDealInfo(data, notice))
                .flatMap(data->Mono.just("success"));
    }

    private Mono<Deal> syncDealInfo(Deal deal, QRCodeNotice notice){
        //todo:验证签名
        if(StringUtils.isEmpty(deal.getReqSn())){
            deal.setReqSn(notice.getTrxid());
        }
        if(StringUtils.isEmpty(deal.getTranType())){
            if("VSP501".equals(notice.getTrxcode())){
                deal.setTranType(Deal.TRAN_TYPE_WECHAT);
            }else{
                deal.setTranType(Deal.TRAN_TYPE_ALIPAY);
            }
        }
        if(!StringUtils.isEmpty(notice.getTrxamt())
                && StringUtils.isEmpty(deal.getAmount())){
            deal.setAmount(notice.getTrxamt());
        }
        if(StringUtils.isEmpty(deal.getTranDate())
                && !StringUtils.isEmpty(notice.getTrxdate())){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            try {
                deal.setTranDate(sdf.parse(notice.getTrxdate()));
            }catch (ParseException ex){
                //do nothing
            }
        }
        if(!StringUtils.isEmpty(notice.getPaytime())){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            try {
                deal.setTranFinishDate(sdf.parse(notice.getPaytime()));
            }catch (ParseException ex){
                //do nothing
            }
        }
        if(!StringUtils.isEmpty(notice.getChnltrxid())
                && StringUtils.isEmpty(deal.getChannelSn())){
            deal.setChannelSn(notice.getChnltrxid());
        }
        if(!StringUtils.isEmpty(notice.getCusorderid())
                && StringUtils.isEmpty(deal.getOrderNo())){
            deal.setOrderNo(notice.getCusorderid());
        }
        deal.setRetCode(notice.getTrxstatus());
        deal.setTranStatus(Deal.SERVER_RESPONSE);
        return dealService.save(deal);
    }

}
