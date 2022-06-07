package org.example.service;

import com.microsoft.graph.models.Group;
import org.example.payload.data.GroupResponse;
import org.example.payload.request.GroupPageRequest;
import org.example.payload.request.SendMessageRequest;
import org.example.payload.response.GroupPageResponse;

public interface GroupService {
	GroupPageResponse getAllByPageNumber(Integer pageNumber);
	GroupPageResponse getAllByPageNumberAndSearchValue(Integer pageNumber, String searchValue);
	GroupResponse getGroupDataById(GroupPageRequest groupPageRequest);
	String createChat(String userId);
	Group getGroupById(String groupId);
	GroupResponse getGroupDataByIdAndSearchValue(GroupPageRequest groupPageRequest);
	Long getGroupCount();
	Long getMembersCountByGroupId(String groupId);
}
