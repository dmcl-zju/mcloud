package com.zju.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.zju.model.GravidaInfo;
import com.zju.model.User;

/**
 * 	�и�����ϸ��Ϣ
 * @author lin
 *
 */
public interface GravidaInfoMapper {
	
	String TABLE_NAME = " gravida_info ";
	String INSERT_FIELDS = " user_id,name,age,weight,height,pregnant_times,weeks,expected_date ";
	//�����ʵ�����������ݿ��в�һ�¿���ʹ�ñ�����ʵ�� �磺head_url headurl����Ȼʵ������ΪheadUrl�������ﲻ�����ִ�Сд
	String SELECT_FIELDS = " id,user_id userId,name,age,weight,height,pregnant_times pregnantTimes,weeks,expected_date expectedDate ";
	
	//����һ����Ϣ
	@Insert({"insert  into",TABLE_NAME,"(",INSERT_FIELDS,")","values(#{userId},#{name},#{age},#{weight},#{height},#{pregnantTimes},#{weeks},#{expectedDate})"})
	int insGravidaInfo(GravidaInfo gravidInfo);
	
	
	//������Ϣ
	@Update({"update",TABLE_NAME," set name=#{name},age=#{age},weight=#{weight},height=#{height},pregnant_times=#{pregnantTimes},weeks=#{weeks},expected_date=#{expectedDate} where user_id=#{userId}"})
	int updInfo(GravidaInfo gravidInfo);
	
	
	//��ȡ��Ϣ
	@Select({"select",SELECT_FIELDS,"from",TABLE_NAME,"where user_id=#{userId}"})
	GravidaInfo selGravidaInfo(int userId);

}
