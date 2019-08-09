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
	
	
    //��Ʒ����:��ͨ�Ŷ���API��Ʒ,�����������滻
    static final String product = "Dysmsapi";
    //��Ʒ����,�����������滻
    static final String domain = "dysmsapi.aliyuncs.com";
    //�˺ź�����
    private String accessKeyId="LTAIfu9gkzFYNTqK";
    private String accessKeySecret="0q3MfmUIQE303jS4BLR3DmslIVeGr4";
   
    
    /**
     * ���Ͷ��ŷ���
     *
     * @param mobile
     * @return
     */
    @Override
    public Map<String,String> sendMessage(String mobile) {
        Map<String,String> res = new HashMap<>();
        //ģ��
        String signName = "����";
        String templeteCode = "SMS_172205565";
        //��ȡ�����֤�룬��ת����json������ʽ
        String identifyCode = WendaUtil.getRandom(4);
        Map<String, String> codeMap = new HashMap<>();
        codeMap.put("code", identifyCode);
        //��ʼ����
        SendSmsResponse response;
        try {
        	//�ȴ���redis��,������5���ӵ���Чʱ��
        	jedisAdapter.setEX(mobile, identifyCode, 300);
        	//Ȼ�󷢶���
            response = sendSms(mobile, signName, templeteCode, JSON.toJSONString(codeMap), null);
            //���ŷ��ͳɹ������redis
           
            if (response != null && response.getCode().equals("OK")) {
                //System.out.println("���ͳɹ�"+response.getCode()+"---"+response.getMessage());
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
            //�������ʧ�ܻ��߷���redisʧ�ܣ�ȡ��redis��key
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
		
		 //������������ʱʱ��
//        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
//        System.setProperty("sun.net.client.defaultReadTimeout", "10000");
 
        //��ʼ��acsClient,�ݲ�֧��region��
        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
        DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
        IAcsClient acsClient = new DefaultAcsClient(profile);
 
        //��װ�������-��������������̨-�ĵ���������
        SendSmsRequest request = new SendSmsRequest();
        //����:�������ֻ���
        request.setPhoneNumbers(phoneNums);
        //����:����ǩ��-���ڶ��ſ���̨���ҵ�
        request.setSignName(signName);//�����ڲ�
        //����:����ģ��-���ڶ��ſ���̨���ҵ�
        request.setTemplateCode(templeteCode);
        //��ѡ:ģ���еı����滻JSON��,��ģ������Ϊ"�װ���${name},������֤��Ϊ${code}"ʱ,�˴���ֵΪ
        request.setTemplateParam(templateParam);//{"code":"152745"}
 
        //ѡ��-���ж�����չ��(�����������û�����Դ��ֶ�)
        //request.setSmsUpExtendCode("90997");
 
        //��ѡ:outIdΪ�ṩ��ҵ����չ�ֶ�,�����ڶ��Ż�ִ��Ϣ�н���ֵ���ظ�������
        request.setOutId(outId);//zpzc
 
        //hint �˴����ܻ��׳��쳣��ע��catch
 
        SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);
        acsClient.getAcsResponse(request);
        return sendSmsResponse;
	}

	//����ǲ�ѯ�ķ�����ʱû���õ�
	@Override
	public QuerySendDetailsResponse querySendDetails(String phoneNumber, String bizId) throws ClientException {
		 //������������ʱʱ��
//        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
//        System.setProperty("sun.net.client.defaultReadTimeout", "10000");
 
        //��ʼ��acsClient,�ݲ�֧��region��
        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
        DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
        IAcsClient acsClient = new DefaultAcsClient(profile);
 
        //��װ�������
        QuerySendDetailsRequest request = new QuerySendDetailsRequest();
        //����-����
        request.setPhoneNumber(phoneNumber);
        //��ѡ-��ˮ��
        request.setBizId(bizId);
        //����-�������� ֧��30���ڼ�¼��ѯ����ʽyyyyMMdd
        SimpleDateFormat ft = new SimpleDateFormat("yyyyMMdd");
        request.setSendDate(ft.format(new Date()));
        //����-ҳ��С
        request.setPageSize(10L);
        //����-��ǰҳ���1��ʼ����
        request.setCurrentPage(1L);
 
        //hint �˴����ܻ��׳��쳣��ע��catch
        QuerySendDetailsResponse querySendDetailsResponse = acsClient.getAcsResponse(request);
        return querySendDetailsResponse;

	}
}
