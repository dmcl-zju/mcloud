package com.zju.service.impl;






import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.QuerySendDetailsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.QuerySendDetailsResponse;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.zju.controller.CommentController;
import com.zju.service.PhoneService;
import com.zju.utils.JedisAdapter;
import com.zju.utils.WendaUtil;


@Service
public class PhoneServiceImpl implements PhoneService{
	
	Logger logger = Logger.getLogger(CommentController.class);

	@Resource
	JedisAdapter jedisAdapter; 
	
	
    //产品名称:云通信短信API产品,开发者无需替换
    static final String product = "Dysmsapi";
    //产品域名,开发者无需替换
    static final String domain = "dysmsapi.aliyuncs.com";
    //账号和密码
    private String accessKeyId="LTAIfu9gkzFYNTqK";
    private String accessKeySecret="0q3MfmUIQE303jS4BLR3DmslIVeGr4";
   
    
    /**
     * 发送短信服务
     *
     * @param mobile
     * @return
     */
    @Override
    public Map<String,String> sendMessage(String mobile) {
        Map<String,String> res = new HashMap<>();
        //模板
        String signName = "心云";
        String templeteCode = "SMS_172205565";
        //获取随机验证码，并转换成json串的形式
        String identifyCode = WendaUtil.getRandom(4);
        Map<String, String> codeMap = new HashMap<>();
        codeMap.put("code", identifyCode);
        //开始发送
        SendSmsResponse response;
        try {
        	//先存入redis中,先设置5分钟的有效时间
        	jedisAdapter.setEX(mobile, identifyCode, 300);
        	//然后发短信
            response = sendSms(mobile, signName, templeteCode, JSON.toJSONString(codeMap), null);
            //短信发送成功后存入redis
           
            if (response != null && response.getCode().equals("OK")) {
                //System.out.println("发送成功"+response.getCode()+"---"+response.getMessage());
                res.put("code","0");
            }else {
            	 if(jedisAdapter.exists(mobile)) {
                 	jedisAdapter.del(mobile);
                 }
            	 res.put("code","1");
            	 res.put("msg", response.getCode());
            }
        } catch (Exception e) {
            logger.error("sendMessage method invoke error:"+e.getMessage());
            res.put("code","1");
            //如果发送失败或者放入redis失败，取出redis的key
            if(jedisAdapter.exists(mobile)) {
            	jedisAdapter.del(mobile);
            }
            return res;
        }
        
        return res;
    }


	@Override
	public SendSmsResponse sendSms(String phoneNums, String signName, String templeteCode, String templateParam,
			String outId) throws ClientException {
		// TODO Auto-generated method stub
		
		 //可自助调整超时时间
//        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
//        System.setProperty("sun.net.client.defaultReadTimeout", "10000");
 
        //初始化acsClient,暂不支持region化
        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
        DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
        IAcsClient acsClient = new DefaultAcsClient(profile);
 
        //组装请求对象-具体描述见控制台-文档部分内容
        SendSmsRequest request = new SendSmsRequest();
        //必填:待发送手机号
        request.setPhoneNumbers(phoneNums);
        //必填:短信签名-可在短信控制台中找到
        request.setSignName(signName);//众评众测
        //必填:短信模板-可在短信控制台中找到
        request.setTemplateCode(templeteCode);
        //可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
        request.setTemplateParam(templateParam);//{"code":"152745"}
 
        //选填-上行短信扩展码(无特殊需求用户请忽略此字段)
        //request.setSmsUpExtendCode("90997");
 
        //可选:outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者
        request.setOutId(outId);//zpzc
 
        //hint 此处可能会抛出异常，注意catch
 
        SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);
        acsClient.getAcsResponse(request);
        return sendSmsResponse;
	}

	//这个是查询的服务，暂时没有用到
	@Override
	public QuerySendDetailsResponse querySendDetails(String phoneNumber, String bizId) throws ClientException {
		 //可自助调整超时时间
//        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
//        System.setProperty("sun.net.client.defaultReadTimeout", "10000");
 
        //初始化acsClient,暂不支持region化
        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
        DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
        IAcsClient acsClient = new DefaultAcsClient(profile);
 
        //组装请求对象
        QuerySendDetailsRequest request = new QuerySendDetailsRequest();
        //必填-号码
        request.setPhoneNumber(phoneNumber);
        //可选-流水号
        request.setBizId(bizId);
        //必填-发送日期 支持30天内记录查询，格式yyyyMMdd
        SimpleDateFormat ft = new SimpleDateFormat("yyyyMMdd");
        request.setSendDate(ft.format(new Date()));
        //必填-页大小
        request.setPageSize(10L);
        //必填-当前页码从1开始计数
        request.setCurrentPage(1L);
 
        //hint 此处可能会抛出异常，注意catch
        QuerySendDetailsResponse querySendDetailsResponse = acsClient.getAcsResponse(request);
        return querySendDetailsResponse;

	}
}
