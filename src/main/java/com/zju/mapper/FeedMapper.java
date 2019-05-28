package com.zju.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.zju.model.Feed;

public interface FeedMapper {

	String TABLE_NAME = " feed ";
	String INSERT_FIELDS = " user_id,type,created_date,data ";
	String SELECT_FIELDS = " id,user_id userId,type,created_date createdDate,data ";
	
	//����feed����
	@Insert({"insert into",TABLE_NAME,"(",INSERT_FIELDS,") values(#{userId},#{type},#{createdDate},#{data})"})
	@Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")/*�������ע������¼��᷵����������id*/
	public int insFeed(Feed feed);
	
	//����id��ȡ--����push��ʽ
	@Select({"select",SELECT_FIELDS,"from",TABLE_NAME,"where id=#{id}"})
	public Feed selFeedByid(int id);
	
	//����userId����----����pull��ʽ
	public List<Feed> selFeedByUserIds(@Param("userIds") List<Integer> userIds,@Param("maxId") int maxId,@Param("count") int count);
}
