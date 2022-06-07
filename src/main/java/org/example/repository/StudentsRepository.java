package org.example.repository;

import com.microsoft.graph.models.DirectoryObject;
import com.microsoft.graph.requests.DirectoryObjectCollectionWithReferencesPage;
import com.microsoft.graph.requests.GraphServiceClient;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import okhttp3.Request;
import org.example.payload.data.StudentData;
import org.example.service.ConverterService;
import org.springframework.stereotype.Component;

@Component
public class StudentsRepository {

	private final GraphServiceClient<Request> graphServiceClient;
	private final ConverterService converterService;

	public StudentsRepository(GraphServiceClient<Request> graphServiceClient, ConverterService converterService) {
		this.graphServiceClient = graphServiceClient;
		this.converterService = converterService;
	}

	public List<StudentData> getAllMembersByGroupId(String groupId){
		DirectoryObjectCollectionWithReferencesPage members = graphServiceClient.groups("{"+groupId+"}")
			.members().buildRequest().get();
		List<DirectoryObject> studentObjects = new ArrayList<>();

		while(members != null) {
			studentObjects.addAll(members.getCurrentPage());
			if(members.getNextPage() == null) break;
			members = members.getNextPage().buildRequest().get();
		}

		List<StudentData> students = studentObjects.stream()
			.map(converterService::toStudentData)//.filter(studentData -> !studentData.getName().contains("null"))
			.collect(Collectors.toList());
		return students;
	}

	public List<StudentData> getAllMembersByGroupIdAndPollId(String groupId, Integer pollId){
		DirectoryObjectCollectionWithReferencesPage members = graphServiceClient.groups("{"+groupId+"}")
			.members().buildRequest().get();
		List<DirectoryObject> studentObjects = new ArrayList<>();

		while(members != null) {
			studentObjects.addAll(members.getCurrentPage());
			if(members.getNextPage() == null) break;
			members = members.getNextPage().buildRequest().get();
		}

		List<StudentData> students = studentObjects.stream()
			.map(directoryObject -> converterService.toStudentData(directoryObject, pollId))//.filter(studentData -> !studentData.getName().contains("null"))
			.collect(Collectors.toList());
		return students;
	}

}
