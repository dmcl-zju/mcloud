package com.zju.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.stereotype.Service;

import com.zju.model.Question;

@Service
public class SearchServiceImpl {
	
	//private final String urlString = "http://localhost:8983/solr/mywenda";
	private final String urlString = "http://localhost/solr/";
	private SolrClient solr = new HttpSolrClient.Builder(urlString).build();
	
	private static final String QUESTION_TITLE_FIELD = "question_title";
	private static final String QUESTION_CONTENT_FIELD = "question_content";
	
	public List<Question> searchQuestion(String keyword,int offset,int count,String pre,String pos) throws Exception{
		List<Question> questions = new ArrayList<>();
		//�½���ѯ���󲢴���ؼ���
		SolrQuery query = new SolrQuery();
		query.setQuery(keyword);
		//���÷�ҳ��ѯ
		query.setRows(count);
		query.setStart(offset);
		//���ø������ܲ�����ǰ��׺
		query.setHighlight(true);
		query.setHighlightSimplePre(pre);
		query.setHighlightSimplePost(pos);
		//���ò�ѯ�ֶ�
		query.setFields(QUESTION_CONTENT_FIELD,QUESTION_TITLE_FIELD);
		//query.set("df", QUESTION_CONTENT_FIELD,QUESTION_TITLE_FIELD);
		//���ò�ѯ�͸������ֶ���Ϣ
		query.set("hl.fl", QUESTION_TITLE_FIELD+","+QUESTION_CONTENT_FIELD);
		QueryResponse response = solr.query(query);
		
		//��ȡ���Ľ����һ��map��ʽ---�����ڵ�����ҳ�п�����response
		Map<String,Map<String,List<String>>> result= response.getHighlighting();
		for(String result_key:result.keySet()) {
			Question q = new Question();
			q.setId(Integer.parseInt(result_key));
			if(result.get(result_key).containsKey(QUESTION_TITLE_FIELD)) {
				List<String> list = result.get(result_key).get(QUESTION_TITLE_FIELD);
				if(list.size()>0) {
					q.setTitle(list.get(0));
				}
			}
			if(result.get(result_key).containsKey(QUESTION_CONTENT_FIELD)) {
				List<String> list = result.get(result_key).get(QUESTION_CONTENT_FIELD);
				if(list.size()>0) {
					q.setContent(list.get(0));
				}
			}
			questions.add(q);
		}
		return questions;
	}
	
	//�·����������������
	public boolean indexQuestion(int qid,String title,String content) throws Exception {
		
		SolrInputDocument document = new SolrInputDocument();
		document.addField("id", qid);
		document.addField(QUESTION_TITLE_FIELD, title);
		document.addField(QUESTION_CONTENT_FIELD, content);
		UpdateResponse response = solr.add(document);
		solr.commit();
		return response != null && response.getStatus() == 0;
	}
	
}
