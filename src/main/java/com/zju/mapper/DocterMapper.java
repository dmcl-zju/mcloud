package com.zju.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.zju.model.User;


/**
 * 	̥������
 * @author lin
 *
 */
public interface DocterMapper {
	
	//ҽ���ı�
	String TABLE_NAME = " docter ";
	String INSERT_FIELDS = " name,password,salt,head_url,role ";
	
	//�����ʵ�����������ݿ��в�һ�¿���ʹ�ñ�����ʵ�� �磺head_url headurl����Ȼʵ������ΪheadUrl�������ﲻ�����ִ�Сд
	String SELECT_FIELDS = " id,name,password,salt,head_url headurl,role ";
	
	
	
	@Select({"select id,name,background from", TABLE_NAME}) 
	List<Map<String,Object>> selAllBackground();
	
	@Select({"select id,name,background from", TABLE_NAME,"where id=#{id}"}) 
	Map<String,Object> selBackgroundById(int id);
	
	@Select({"select",SELECT_FIELDS,"from", TABLE_NAME}) 
	List<User> selAll();

	@Select({"select",SELECT_FIELDS,"from", TABLE_NAME, "where name=#{name} and password=#{password}"})
	User selByNameAndPassword(User user);
	
	@Select({"select",SELECT_FIELDS,"from", TABLE_NAME, "where name=#{name}"})
	User selByName(String name);
	
	@Select({"select",SELECT_FIELDS,"from", TABLE_NAME, "where id=#{id}"})
	User selById(int id);
	
	@Insert({"insert into",TABLE_NAME,"(",INSERT_FIELDS,")","values(#{name},#{password},#{salt},#{headUrl},#{role})"})
	int insUser(User user);
	
	@Update({"update ",TABLE_NAME,"set password=#{password} where id = #{id}"})
	int updPassword(User user);
	
	
}
