package com.zju.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.zju.model.User;


public interface UserMapper {
	String TABLE_NAME = " user ";
	String INSERT_FIELDS = " name,password,salt,head_url,role,phone_num ";
	
	//这里和实体类名和数据库中不一致可以使用别名来实现 如：head_url headurl，虽然实体类中为headUrl但是这里不会区分大小写
	String SELECT_FIELDS = " id,name,password,salt,head_url headurl,role,phone_num phoneNum ";
	
	@Select({"select",SELECT_FIELDS,"from", TABLE_NAME, "where name=#{name} and password=#{password}"})
	User selByNameAndPassword(User user);
	
	@Select({"select",SELECT_FIELDS,"from", TABLE_NAME, "where name=#{name}"})
	User selByName(String name);
	
	@Select({"select",SELECT_FIELDS,"from", TABLE_NAME, "where phone_num=#{phoneNum}"})
	User selByPhoneNum(String phoneNum);
	

	@Select({"select",SELECT_FIELDS,"from", TABLE_NAME, "where id=#{id}"})
	User selById(int id);
	
	
	
	@Insert({"insert into",TABLE_NAME,"(",INSERT_FIELDS,")","values(#{name},#{password},#{salt},#{headUrl},#{role},#{phoneNum})"})
	int insUser(User user);
	
	@Update({"update ",TABLE_NAME,"set password=#{password} where id = #{id}"})
	int updPassword(User user);
	
	
}
