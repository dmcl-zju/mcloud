package com.zju.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.zju.model.Ticket;

public interface TicketMapper {
	String TABLE_NAME = " login_ticket ";
	String INSERT_FIELDS = " user_id,ticket,expired,status,role ";
	//这里和实体类名和数据库中不一致可以使用别名来实现 如：head_url headurl，虽然实体类中为headUrl但是这里不会区分大小写
	String SELECT_FIELDS = " id,user_id userId,ticket,expired,status,role ";
	
	@Insert({"insert into",TABLE_NAME,"(",INSERT_FIELDS,") values "
			+ "(#{userId},#{ticket},#{expired},#{status},#{role})"})
	int insTicket(Ticket ticket);
	
	@Select({"select",SELECT_FIELDS,"from",TABLE_NAME,"where ticket=#{ticket}"})
	Ticket selByticket(String ticket);
	
	@Update({"update",TABLE_NAME,"set status=#{status} where ticket=#{ticket}"})
	int updTicket(@Param("ticket") String ticket ,@Param("status") int status);
}
