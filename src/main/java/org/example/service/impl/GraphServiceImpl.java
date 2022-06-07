package org.example.service.impl;

import com.microsoft.graph.models.User;
import com.microsoft.graph.requests.GraphServiceClient;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Request;
import org.example.service.GraphService;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class GraphServiceImpl implements GraphService {

	private final GraphServiceClient<Request> graphServiceClient;

	public GraphServiceImpl(GraphServiceClient<Request> graphServiceClient) {
		this.graphServiceClient = graphServiceClient;
	}

	@Override
	public User getUser() {
		if (graphServiceClient == null) throw new NullPointerException(
			"Graph client has not been initialized. Call initializeGraphAuth before calling this method"
		);

		User me = graphServiceClient.me()
			.buildRequest()
			.get();

		return me;
	}

}
