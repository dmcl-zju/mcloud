package com.zju.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.zju.model.Message;

public interface MessageMapper {

	String TABLE_NAME = " message ";
	String INSERT_FIELDS = " from_id,from_role,to_role,to_id,content,created_date,has_read,conversation_id ";
	String SELECT_FIELDS = " id,from_id fromId,from_role fromRole,to_role toRole,to_id toId,content,created_date createdDate,has_read hasRead,conversation_id conversationId ";
	String SELECT_FIELDS2 = " from_id fromId,from_role fromRole,to_role toRole,to_id toId,content,created_date createdDate,has_read hasRead,conversation_id conversationId ";
	
	@Insert({"insert into",TABLE_NAME,"(",INSERT_FIELDS,") values(#{fromId},#{fromRole},#{toRole},#{toId},#{content},#{createdDate},#{hasRead},#{conversationId})"})
	public int insMessage(Message message);
	
	@Select({"select",SELECT_FIELDS,"from",TABLE_NAME,"where conversation_id=#{conversationId} order by created_date desc limit #{offset},#{limit}"})
	public List<Message> selByconversationId(@Param("conversationId") String conversationId,
											 @Param("limit") int limit ,
											 @Param("offset") int offset);
	

	//复用了id这个口用来存消息数量
	//select * ,count(id) cous from (select * from message order by created_date desc)message group by conversation_id limit 2,2
	@Select({"select",SELECT_FIELDS2,",count(id) as id from (select * from message where from_id=#{userId} and from_role=#{userRole} or to_id=#{userId} and to_role=#{userRole} order by created_date desc) mm "
			+ "group by conversation_id order by created_date desc limit #{offset},#{limit}"})
	public List<Message> selConversationListByUserid(@Param("userId") int userId,
													 @Param("userRole") int userRole,
													 @Param("offset") int offset,
													 @Param("limit") int limit);
	
	@Select({"select count(id) from",TABLE_NAME,"where to_id=#{userId} and to_role=#{userRole} and has_read=0 and conversation_id=#{conversationId}"})
	public int selCountOfUnread(@Param("userId") int userId,@Param("userRole") int userRole,@Param("conversationId") String conversationId);
	
	@Update({"update",TABLE_NAME,"set has_read=1 where to_id=#{userId} and to_role =#{userRole} and has_read=0 and conversation_id=#{conversationId}"})
	public int updHasread(@Param("userId") int userId,@Param("userRole") int userRole,@Param("conversationId") String conversationId);
	
	
}
