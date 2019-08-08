package com.zju.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alibaba.fastjson.JSONObject;
import com.zju.async.EventType;
import com.zju.mapper.FeedMapper;
import com.zju.model.EntityType;
import com.zju.model.Feed;
import com.zju.model.HostHolder;
import com.zju.service.FeedService;
import com.zju.service.FollowService;
import com.zju.utils.JedisAdapter;
import com.zju.utils.Rediskeyutil;

@RunWith(SpringJUnit4ClassRunner.class)
//告诉junit spring配置文件的位子
@ContextConfiguration("classpath:/applicationContext.xml")
public class FeedTest {

	@Resource
	FeedMapper feedMapper;
	
	@Resource
	HostHolder hostHolder;
	
	@Resource
	FollowService followServiceImpl;
	
	@Resource
	FeedService feedServiceImpl;
	
	@Resource
	JedisAdapter jedisAdapter;
	
	@Test
	public void feddTest() {
		/*Map<String,String> map = new HashMap<>();
		//数据库中插入10条数据
		for(int i=0;i<10;i++) {
			Feed feed = new Feed();
			feed.setCreatedDate(new Date());
			map.put("key", ""+i);
			feed.setData(JSONObject.toJSONString(map));
			feed.setUserId(20);
			feed.setType(EventType.COMMENT.getValue());
			//feedMapper.insFeed(feed);
		}
		
		Feed feed = feedMapper.selFeedByid(1);
		System.out.println(feed);
		System.out.println("--------------------------------------------");
		List<Integer> userIds = new ArrayList<>();
		//userIds.add(10);
		List<Feed> list = feedMapper.selFeedByUserIds(userIds, 30, 5);
		for(Feed feeds:list) {
			System.out.println(feeds);
		}*/
		
		
		//pull测试
		/*int hostUserId = 0;
		List<Integer> followees = new ArrayList<>();
		//找到自己关注的用户--先拿10个用户的
		if(hostUserId!=0) {
			followees = followServiceImpl.getFollowees(EntityType.ENTITY_USER, hostUserId, 0, 10);
		}
		System.out.println("我的关注：");
		for(int i:followees) {
			System.out.println(i);
		}
		
		List<Feed> feeds = feedServiceImpl.getFeedsByUsers(followees,Integer.MAX_VALUE,10);
		for(Feed feed:feeds) {
			System.out.println(feed);
		}*/
		
		//push测试
		int hostUserId = 14;
		//把自己的timeline从redis中取出来
		String timelineKey = Rediskeyutil.getTimelineKey(hostUserId);
		System.out.println(timelineKey);
		List<String> timelines = jedisAdapter.lrange(timelineKey,0,10);
		timelines.add(""+5);
		System.out.println("给我的推送："+timelines);
		System.out.println("--------------------------");
		
		List<Feed> feeds = new ArrayList<>();
		for(String timeline:timelines) {
			Feed feed = feedServiceImpl.getFeedById(Integer.valueOf(timeline));
			if(feed!=null) {
				feeds.add(feed);
				System.out.println(feed);
			}
		}
	}
}
