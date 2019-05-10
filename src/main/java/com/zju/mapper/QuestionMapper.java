package com.zju.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.zju.model.Question;

public interface QuestionMapper {
	String TABLE_NAME = " question ";
	String INSERT_FIELDS = " title,content,created_date,user_id,comment_count ";
	//�����ʵ�����������ݿ��в�һ�¿���ʹ�ñ�����ʵ�� �磺head_url headurl����Ȼʵ������ΪheadUrl�������ﲻ�����ִ�Сд
	String SELECT_FIELDS = " id,title,content,created_date createddate,user_id userid,comment_count commentcount";
	
	@Insert({"insert into",TABLE_NAME,"(",INSERT_FIELDS,
				") values(#{title},#{content},#{createdDate},#{userId},#{commentCount})"})
	int insQuestion(Question question);
	
	@Select({"select",SELECT_FIELDS,"from",TABLE_NAME})
	List<Question> selAll();
	
	@Select({"select",SELECT_FIELDS,"from",TABLE_NAME,"where id=#{id}"})
	Question selByid(int id);
	
	//ʹ��xml��ʽ�ӿڰ󶨷���----���ڸ���sql���Ĵ���ʱ����ʹ��
	List<Question> selLastestQuestions(@Param("userId") int userId,
									   @Param("offset") int offset,
									   @Param("limit") int limit);
}
