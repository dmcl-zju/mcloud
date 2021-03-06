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
 * 敏感词过滤
 * @author lin
 *
 */
@Service
public class SensitiveServiceImpl implements InitializingBean{
	
	Logger logger = Logger.getLogger(SensitiveServiceImpl.class);
	
	//定义前缀树的基本结构
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
	
	//定义前缀树根节点，不存值，只放子节点
	 private TrieNode rootNode = new TrieNode();
	 
	 //判断是不是特殊字符
	 private boolean isSymbol(char c) {
        int ic = (int) c;
        // 0x2E80-0x9FFF 东亚文字范围
        return !CharUtils.isAsciiAlphanumeric(c) && (ic < 0x2E80 || ic > 0x9FFF);
	 }
	 //向前缀树中添加内容
	 private void addWord(String lineTxt) {
		 //tempNode相当于指针，先指向根
        TrieNode tempNode = rootNode;
        // 循环每个字节
        for (int i = 0; i < lineTxt.length(); ++i) {
            Character c = lineTxt.charAt(i);
            // 过滤空格
            if (isSymbol(c)) {
                continue;
            }
            TrieNode node = tempNode.getSubNode(c);

            if (node == null) { // 没初始化
                node = new TrieNode();
                tempNode.addSubNode(c, node);
            }

            tempNode = node;

            if (i == lineTxt.length() - 1) {
                // 关键词结束， 设置结束标志
                tempNode.setEnd(true);
            }
        }
    }
	 //进行过滤
	 public String filter(String text) {
        if (StringUtils.isBlank(text)) {
            return text;
        }
        String replacement = "***";
        StringBuilder result = new StringBuilder();

        TrieNode tempNode = rootNode;
        int begin = 0; // 回滚数
        int position = 0; // 当前比较的位置

        while (position < text.length()) {
            char c = text.charAt(position);
            // 空格直接跳过
            if (isSymbol(c)) {
                if (tempNode == rootNode) {
                    result.append(c);
                    ++begin;
                }
                ++position;
                continue;
            }

            tempNode = tempNode.getSubNode(c);

            // 当前位置的匹配结束
            if (tempNode == null) {
                // 以begin开始的字符串不存在敏感词
                result.append(text.charAt(begin));
                // 跳到下一个字符开始测试
                position = begin + 1;
                begin = position;
                // 回到树初始节点
                tempNode = rootNode;
            } else if (tempNode.isEnd()) {
                // 发现敏感词， 从begin到position的位置用replacement替换掉
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
                
               //看文件内容
               // System.out.println(lineTxt);
                addWord(lineTxt);
            }
            read.close();
        } catch (Exception e) {
            logger.error("读取敏感词文件失败" + e.getMessage());
        }
	}
}
