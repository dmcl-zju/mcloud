package com.zju.service;

import java.util.List;

import com.zju.model.Comment;

public interface CommentService {
	
	//��������
	public int addComment(Comment comment);
	//��ȡ��������
	public List<Comment> getCommentByEntity(int entityId,int entityType);
	//��ȡĳ���û�������
	public List<Comment> getCommentByUserId(int userId); 
	//ɾ���û�����
	public int deleteComment(int entityId,int entityType);
	//�鿴������
	public int getCommentCount(int entityId,int entityType);
	
}
