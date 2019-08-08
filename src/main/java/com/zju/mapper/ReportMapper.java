package com.zju.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.zju.model.Report;


/**
 * 	诊断报告
 * @author lin
 *
 */
public interface ReportMapper {
	
	
	String TABLE_NAME = " report ";
	String INSERT_FIELDS = " data_id,docter_id,user_id,guidance,created_time ";
	String SELECT_FIELDS = " id,data_id dataId,docter_id docterId,user_id userId,guidance,created_time createdTime ";
	
	
	@Insert({"insert into",TABLE_NAME,"(",INSERT_FIELDS,")","values(#{dataId},#{docterId},#{userId},#{guidance},#{createdTime})"})
	@Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")/*增加这个注解插入记录后会返回自增长的id*/
	int insReport(Report report);
	
	@Select({"select",SELECT_FIELDS,"from", TABLE_NAME, "where id=#{id}"})
	Report selById(int id);
	
	@Select({"select",SELECT_FIELDS,"from", TABLE_NAME, "where user_id=#{userId}"})
	List<Report> selByUserId(int userId);
	
	@Select({"select",SELECT_FIELDS,"from", TABLE_NAME, "where docter_id=#{docterId}"})
	List<Report> selByDocterId(int docterId);
	
	@Select({"select",SELECT_FIELDS,"from", TABLE_NAME, "where user_id=#{userId} and docter_id=#{docterId}"})
	List<Report> selByDocterAndUser(@Param("docterId") int docterId,@Param("userId") int userId);
	
	@Update({"update ",TABLE_NAME,"set guidance=#{guidance},created_time=#{createdTime} where id = #{id}"})
	int updGuidence(@Param("guidance") String guidance,@Param("createdTime") Date createdTime,@Param("id") int id);
	
	
}
