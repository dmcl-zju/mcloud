package com.zju.utils;

import java.security.MessageDigest;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.zju.model.User;


//������
public class WendaUtil {
	private static final Logger logger = Logger.getLogger(WendaUtil.class);
	//����һЩ���õĳ���
	public static int ENTITY_QUESTION = 1;
	public static int SYSTEM_USERID = 1;
	public static int SYSTEM_USERROLE = 1;
	
	//ϵͳ����ҽ��������
	public static int ACTION_USERID = 2;
	public static int ACTION_ROLE = 1;
	
	
	
	//�û�����ͼƬ
	public static String TOUTIAO_DOMAIN = "http://127.0.0.1/";
	//���ش洢ͼ��ĵ�ַ
	public static String IMAGE_DIR = "D:/upload/";
	//����ͼƬ��׺
	public static String[] IMAGE_FILE_EXTD = {"png", "bmp", "jpg", "jpeg"};
	
	//��֤ͼƬ��׺
	public static boolean isImage(String fileName) {
		for(String ext:IMAGE_FILE_EXTD) {
			if(ext.equals(fileName)) {
				return true;
			}
		}
		return false;
	}
	
	
	//����״̬��
	public static String getJSONString(int code) {
		JSONObject json = new JSONObject();
		json.put("code", code);
		return json.toJSONString();
	}
	//��ͨ�ַ�����Ϣ
	public static String getJSONString(int code,String msg) {
		JSONObject json = new JSONObject();
		json.put("code", code);
		json.put("msg", msg);
		return json.toJSONString();
	}
	//����map
	public static String getJSONString(int code,Map<String,Object> map) {
		JSONObject json = new JSONObject();
		json.put("code", code);
		//����map�������ݼ���json����
		for(Map.Entry<String, Object> entry:map.entrySet()) {
			json.put(entry.getKey(), entry.getValue());
		}
		return json.toJSONString();
	}
	
	
	//����list
	public static String getJSONString(int code,String listname,List<User> list) {
		JSONObject json = new JSONObject();
		json.put("code", code);
		json.put(listname, list);
		return json.toJSONString();
	}
	
	//��ȡ�����֤��
	 public static String getRandom(int count) {
        String num = "";
        for (int i = 0; i < count; i++) {
            num = num + String.valueOf((int) Math.floor(Math.random() * 9 + 1));
        }
        return num;
	}

	
	//MD5����
	 public static String MD5(String key) {
        char hexDigits[] = {
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
        };
        try {
            byte[] btInput = key.getBytes();
            // ���MD5ժҪ�㷨�� MessageDigest ����
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // ʹ��ָ�����ֽڸ���ժҪ
            mdInst.update(btInput);
            // �������
            byte[] md = mdInst.digest();
            // ������ת����ʮ�����Ƶ��ַ�����ʽ
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            logger.error("����MD5ʧ��", e);
            return null;
        }
    }
	
}
