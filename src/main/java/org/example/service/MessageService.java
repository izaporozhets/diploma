package org.example.service;

import com.microsoft.graph.models.ChatMessage;
import java.util.List;

public interface MessageService {
	void sentMessageTo(String studentId, String content);
	String createChat(String userId);
	void addMemberToAChatByIds(String chatId, String userId);
	List<ChatMessage> getAllMessagesByUserId(String userId);
}
