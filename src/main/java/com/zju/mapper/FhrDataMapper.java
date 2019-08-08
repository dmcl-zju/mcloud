package com.zju.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.zju.model.FhrData;
import com.zju.model.User;


public interface FhrDataMapper {
	
	
	String TABLE_NAME = " fhr_data ";
	
	String INSERT_FIELDS = " user_id,m_time,up_time,data,fhr_base,fhr_stv,uc_base,fhr_up,fhr_down,score ";
	String INSERT_FIELDS1 = " user_id,m_time,up_time,data ";
	String INSERT_FIELDS2 = " fhr_base,fhr_stv,uc_base,fhr_up,fhr_down,sore ";
	
	String SELECT_FIELDS = " id,user_id userId,m_time mTime,up_time upTime,data,fhr_base fhrBase,fhr_stv fhrStv,uc_base ucBase,fhr_up fhrUp,fhr_down fhrDown,score ";
	
	
	//����������
	@Insert({"insert into",TABLE_NAME,"(",INSERT_FIELDS,")","values(#{userId},#{mTime},#{upTime},#{data},#{fhrBase},#{fhrStv},#{ucBase},#{fhrUp},#{fhrDown},#{score})"})                          
	@Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")/*�������ע������¼��᷵����������id*/
	int insFhrDataAll(FhrData fhrData);
	
	//����id������
	@Select({"select",SELECT_FIELDS,"from", TABLE_NAME, "where id=#{id}"})
	FhrData selById(int id);
	
	//�����û�������
	@Select({"select",SELECT_FIELDS,"from", TABLE_NAME, "where user_id=#{userId}"})
	List<FhrData> selByUserId(int userId);
	
	//���¼����������ֵ
	@Update({"update ",TABLE_NAME,"set fhr_base=#{fhrBase},fhr_stv=#{fhrStv},uc_base=#{ucBase},fhr_up=#{fhrUp},fhr_down=#{fhrDown},score=#{score} where id = #{id}"})
	int updValue(FhrData fhrData);
	
	
}
