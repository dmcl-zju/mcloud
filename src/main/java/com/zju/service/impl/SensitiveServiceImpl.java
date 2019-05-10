package com.zju.service.impl;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;


import org.apache.commons.lang.CharUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

/**
 * ���дʹ���
 * @author lin
 *
 */
@Service
public class SensitiveServiceImpl implements InitializingBean{
	
	Logger logger = Logger.getLogger(SensitiveServiceImpl.class);
	
	//����ǰ׺���Ļ����ṹ
	private class TrieNode{
		private boolean end = false;
		private Map<Character,TrieNode> subNode = new HashMap<>();
		
		public void addSubNode(Character key,TrieNode node) {
			subNode.put(key, node);
		}
		
		public TrieNode getSubNode(Character key) {
			return subNode.get(key);
		}
		
		public boolean isEnd() {
			return end;
		}
		
		public void setEnd(boolean end) {
			this.end = end;
		}
		
		public int getSubNodeCount() {
			return subNode.size();
		}
	}
	
	//����ǰ׺�����ڵ㣬����ֵ��ֻ���ӽڵ�
	 private TrieNode rootNode = new TrieNode();
	 
	 //�ж��ǲ��������ַ�
	 private boolean isSymbol(char c) {
        int ic = (int) c;
        // 0x2E80-0x9FFF �������ַ�Χ
        return !CharUtils.isAsciiAlphanumeric(c) && (ic < 0x2E80 || ic > 0x9FFF);
	 }
	 //��ǰ׺�����������
	 private void addWord(String lineTxt) {
		 //tempNode�൱��ָ�룬��ָ���
        TrieNode tempNode = rootNode;
        // ѭ��ÿ���ֽ�
        for (int i = 0; i < lineTxt.length(); ++i) {
            Character c = lineTxt.charAt(i);
            // ���˿ո�
            if (isSymbol(c)) {
                continue;
            }
            TrieNode node = tempNode.getSubNode(c);

            if (node == null) { // û��ʼ��
                node = new TrieNode();
                tempNode.addSubNode(c, node);
            }

            tempNode = node;

            if (i == lineTxt.length() - 1) {
                // �ؼ��ʽ����� ���ý�����־
                tempNode.setEnd(true);
            }
        }
    }
	 //���й���
	 public String filter(String text) {
        if (StringUtils.isBlank(text)) {
            return text;
        }
        String replacement = "***";
        StringBuilder result = new StringBuilder();

        TrieNode tempNode = rootNode;
        int begin = 0; // �ع���
        int position = 0; // ��ǰ�Ƚϵ�λ��

        while (position < text.length()) {
            char c = text.charAt(position);
            // �ո�ֱ������
            if (isSymbol(c)) {
                if (tempNode == rootNode) {
                    result.append(c);
                    ++begin;
                }
                ++position;
                continue;
            }

            tempNode = tempNode.getSubNode(c);

            // ��ǰλ�õ�ƥ�����
            if (tempNode == null) {
                // ��begin��ʼ���ַ������������д�
                result.append(text.charAt(begin));
                // ������һ���ַ���ʼ����
                position = begin + 1;
                begin = position;
                // �ص�����ʼ�ڵ�
                tempNode = rootNode;
            } else if (tempNode.isEnd()) {
                // �������дʣ� ��begin��position��λ����replacement�滻��
                result.append(replacement);
                position = position + 1;
                begin = position;
                tempNode = rootNode;
            } else {
                ++position;
            }
        }
        result.append(text.substring(begin));
        return result.toString();
    }
	@Override
	public void afterPropertiesSet() throws Exception {
		// TODO Auto-generated method stub
		rootNode = new TrieNode();
        try {
            InputStream is = Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream("SensitiveWords.txt");
            InputStreamReader read = new InputStreamReader(is);
            BufferedReader bufferedReader = new BufferedReader(read);
            String lineTxt;
            while ((lineTxt = bufferedReader.readLine()) != null) {
                lineTxt = lineTxt.trim();
                addWord(lineTxt);
            }
            read.close();
        } catch (Exception e) {
            logger.error("��ȡ���д��ļ�ʧ��" + e.getMessage());
        }
	}
}
