package com.zju.test;

import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.zju.mapper.QuestionMapper;
import com.zju.mapper.UserMapper;
import com.zju.model.Question;
import com.zju.model.User;



@RunWith(SpringJUnit4ClassRunner.class)
//告诉junit spring配置文件的位子
@ContextConfiguration("classpath:/applicationContext.xml")
@Sql("/init-schema.sql")
public class InitData {
	
	@Resource
	UserMapper userMapper;
	
	@Resource
	QuestionMapper questionMapper;

	@Test
	public void initData() {
		Random random = new Random();
        for (int i = 0; i < 11; ++i) {
            User user = new User();
            user.setHeadUrl(String.format("http://images.nowcoder.com/head/%dt.png", random.nextInt(1000)));
            user.setName(String.format("USER%d", i));
            user.setPassword("");
            user.setSalt("");
            userMapper.insUser(user);
            
            user.setId(i+1);
            user.setPassword("123456");
            userMapper.updPassword(user);
            
            Question question = new Question();
            question.setCommentCount(i);
            Date date = new Date();
            date.setTime(date.getTime() + 1000 * 3600 * 5 * i);
            question.setCreatedDate(date);
            question.setUserId(i + 1);
            question.setTitle(String.format("TITLE{%d}", i));
            question.setContent(String.format("Balaababalalalal Content %d", i));
            questionMapper.insQuestion(question);         
        }
        System.out.println("初始化用户完成");
        
       // List<Question> questions = questionMapper.selAll();
        List<Question> questions = questionMapper.selLastestQuestions(0, 0, 10);
        for(Question q:questions) {
        	System.out.println(q);
        }
        
        
        
           
	}
}
