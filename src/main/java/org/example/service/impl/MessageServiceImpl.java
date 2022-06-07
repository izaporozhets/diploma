package org.example.service.impl;

import com.google.gson.JsonPrimitive;
import com.microsoft.graph.models.AadUserConversationMember;
import com.microsoft.graph.models.Chat;
import com.microsoft.graph.models.ChatMessage;
import com.microsoft.graph.models.ChatType;
import com.microsoft.graph.models.ConversationMember;
import com.microsoft.graph.models.ItemBody;
import com.microsoft.graph.requests.ChatGetAllMessagesCollectionPage;
import com.microsoft.graph.requests.ConversationMemberCollectionPage;
import com.microsoft.graph.requests.ConversationMemberCollectionResponse;
import com.microsoft.graph.requests.GraphServiceClient;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Request;
import org.example.service.GraphService;
import org.example.service.MessageService;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MessageServiceImpl implements MessageService {

	private final GraphServiceClient<Request> graphServiceClient;
	private final GraphService graphService;

	public MessageServiceImpl(GraphServiceClient<Request> graphService, GraphService graphService1) {
		this.graphServiceClient = graphService;
		this.graphService = graphService1;
	}

	@Override
	public void sentMessageTo(String studentId, String content) {
		String chatId = createChat(studentId);

		if(chatId == null) {log.info("MessageServiceImpl::sentMessageTo::chatId is null"); return;}
		ChatMessage chatMessage = new ChatMessage();
		ItemBody    body        = new ItemBody();
		body.content = content;
		chatMessage.body = body;

		graphServiceClient.chats(chatId).messages()
			.buildRequest()
			.post(chatMessage);

	}

	@Override
	public String createChat(String userId) {

		if(Objects.equals(graphService.getUser().id, userId)) return null;

		Chat chat = new Chat();
		chat.chatType = ChatType.ONE_ON_ONE;
		LinkedList<ConversationMember> membersList = new LinkedList<ConversationMember>();
		AadUserConversationMember members = new AadUserConversationMember();
		LinkedList<String> rolesList = new LinkedList<String>();
		rolesList.add("owner");
		members.roles = rolesList;
		members.additionalDataManager().put("user@odata.bind", new JsonPrimitive("https://graph.microsoft.com/v1.0/users('6601c55f-f40b-4010-b787-e1dc5f59c511')"));
		membersList.add(members);
		members.oDataType = "#microsoft.graph.aadUserConversationMember";
		AadUserConversationMember members1 = new AadUserConversationMember();
		LinkedList<String> rolesList1 = new LinkedList<String>();
		rolesList1.add("guest");
		members1.roles = rolesList1;
		members1.oDataType = "#microsoft.graph.aadUserConversationMember";
		members1.additionalDataManager().put("user@odata.bind", new JsonPrimitive("https://graph.microsoft.com/v1.0/users('" + userId + "')"));
		membersList.add(members1);
		ConversationMemberCollectionResponse conversationMemberCollectionResponse = new ConversationMemberCollectionResponse();
		conversationMemberCollectionResponse.value = membersList;
		ConversationMemberCollectionPage conversationMemberCollectionPage = new ConversationMemberCollectionPage(conversationMemberCollectionResponse, null);
		chat.members = conversationMemberCollectionPage;

		return graphServiceClient.chats()
			.buildRequest()
			.post(chat).id;


		//Student student = studentRepository.findById(userId).orElseThrow(() -> {
		//	throw new ApplicationException(ApplicationError.STUDENT_DOES_NOT_EXIST);
		//});
		//student.setChannelId(chat.id);
		//studentRepository.save(student);
	}

	@Override
	public void addMemberToAChatByIds(String chatId, String userId) {

		AadUserConversationMember conversationMember = new AadUserConversationMember();
		conversationMember.additionalDataManager().put("user@odata.bind", new JsonPrimitive("https://graph.microsoft.com/v1.0/users/"+userId));
		LinkedList<String> rolesList = new LinkedList<String>();
		rolesList.add("owner");
		conversationMember.roles = rolesList;
		conversationMember.oDataType = "#microsoft.graph.aadUserConversationMember";
		ConversationMember res = graphServiceClient.chats(chatId).members()
			.buildRequest()
			.post(conversationMember);

		System.out.println(res);
	}

	@Override
	public List<ChatMessage> getAllMessagesByUserId(String userId) {

		ChatGetAllMessagesCollectionPage getAllMessages = graphServiceClient.users(userId).chats()
			.getAllMessages()
			.buildRequest()
			.get();

		return getAllMessages != null ? getAllMessages.getCurrentPage() : Collections.emptyList();
	}
}
