package org.example.repository;

import com.microsoft.graph.models.Group;
import com.microsoft.graph.requests.GraphServiceClient;
import okhttp3.Request;
import org.springframework.stereotype.Component;

@Component
public class GroupRepository {

	private final GraphServiceClient<Request> graphServiceClient;

	public GroupRepository(GraphServiceClient<Request> graphServiceClient) {
		this.graphServiceClient = graphServiceClient;
	}

	public Group getGroupById(String groupId) {
		return graphServiceClient.groups("{"+groupId+"}")
			.buildRequest()
			.get();
	}

}
