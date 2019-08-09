package com.zju.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.zju.model.GravidaInfo;
import com.zju.model.User;

/**
 * 	孕妇的详细信息
 * @author lin
 *
 */
public interface GravidaInfoMapper {
	
	String TABLE_NAME = " gravida_info ";
	String INSERT_FIELDS = " user_id,name,age,weight,height,pregnant_times,weeks,expected_date ";
	//这里和实体类名和数据库中不一致可以使用别名来实现 如：head_url headurl，虽然实体类中为headUrl但是这里不会区分大小写
	String SELECT_FIELDS = " id,user_id userId,name,age,weight,height,pregnant_times pregnantTimes,weeks,expected_date expectedDate ";
	
	//插入一条信息
	@Insert({"insert  into",TABLE_NAME,"(",INSERT_FIELDS,")","values(#{userId},#{name},#{age},#{weight},#{height},#{pregnantTimes},#{weeks},#{expectedDate})"})
	int insGravidaInfo(GravidaInfo gravidInfo);
	
	
	//更新信息
	@Update({"update",TABLE_NAME," set name=#{name},age=#{age},weight=#{weight},height=#{height},pregnant_times=#{pregnantTimes},weeks=#{weeks},expected_date=#{expectedDate} where user_id=#{userId}"})
	int updInfo(GravidaInfo gravidInfo);
	
	
	//获取信息
	@Select({"select",SELECT_FIELDS,"from",TABLE_NAME,"where user_id=#{userId}"})
	GravidaInfo selGravidaInfo(int userId);

}
