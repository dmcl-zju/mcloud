package com.zju.service;



import java.util.Map;

import com.aliyuncs.dysmsapi.model.v20170525.QuerySendDetailsResponse;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;

public interface PhoneService {
	
	
	public Map<String,String> sendMessage(String mobile);
	
	/**
	   *  ���Ͷ��Žӿ�
     *
     * @param phoneNums     �ֻ�����
     * @param signName      ģ��ǩ��
     * @param templeteCode  ģ�����
     * @param templateParam ģ���滻����
     * @param outId         �ṩ��ҵ����չ�ֶ�
     * @return
     * @throws ClientException
     */
    SendSmsResponse sendSms(String phoneNums, String signName, String templeteCode,
                            String templateParam, String outId) throws ClientException;
 
    /**
             * ��ѯ���ŷ�����ϸ
     *
     * @param phoneNumber
     * @param bizId       ҵ����ˮ��
     * @return
     * @throws ClientException
     */
    QuerySendDetailsResponse querySendDetails(String phoneNumber, String bizId) throws ClientException;
 
   
}
