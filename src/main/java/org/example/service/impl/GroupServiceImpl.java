package org.example.service.impl;

import com.google.common.collect.Lists;
import com.microsoft.graph.models.DirectoryObject;
import com.microsoft.graph.models.Group;
import com.microsoft.graph.options.HeaderOption;
import com.microsoft.graph.options.Option;
import com.microsoft.graph.requests.DirectoryObjectCollectionWithReferencesPage;
import com.microsoft.graph.requests.GraphServiceClient;
import com.microsoft.graph.requests.GroupCollectionPage;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Request;
import org.example.entity.Answer;
import org.example.entity.Cipher;
import org.example.entity.Poll;
import org.example.enums.IntervalEnum;
import org.example.payload.data.AnswerData;
import org.example.payload.data.GroupResponse;
import org.example.payload.data.PollData;
import org.example.payload.data.StudentData;
import org.example.payload.request.GroupPageRequest;
import org.example.payload.request.SendMessageRequest;
import org.example.payload.response.GroupPageResponse;
import org.example.payload.response.GroupShortResponse;
import org.example.repository.AnswerRepository;
import org.example.repository.PollRepository;
import org.example.service.ConverterService;
import org.example.service.GroupService;
import org.example.service.MessageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class GroupServiceImpl implements GroupService {

	private final GraphServiceClient<Request> graphServiceClient;
	private final MessageService              messageService;
	private final ConverterService            converterService;
	private final PollRepository              pollRepository;
	private final AnswerRepository            answerRepository;

	public GroupServiceImpl(GraphServiceClient<Request> graphServiceClient, MessageService messageService, ConverterService converterService, PollRepository pollRepository, AnswerRepository answerRepository) {
		this.graphServiceClient = graphServiceClient;
		this.messageService = messageService;
		this.converterService = converterService;
		this.pollRepository = pollRepository;
		this.answerRepository = answerRepository;
	}

	@Override
	public GroupPageResponse getAllByPageNumber(Integer pageNumber) {
		GroupPageResponse response = new GroupPageResponse();
		LinkedList<Option> requestOptions = new LinkedList<Option>();
		requestOptions.add(new HeaderOption("ConsistencyLevel", "eventual"));

		GroupCollectionPage groupPage = graphServiceClient.groups()
			.buildRequest(requestOptions)
			.orderBy("displayName")
			.top(10)
			.get();

		List<Group> resultGroups = new ArrayList<>();
		AtomicInteger atomicInteger = new AtomicInteger(1);
		while(groupPage != null && pageNumber >= atomicInteger.get()) {
			if(atomicInteger.get() == pageNumber){
				resultGroups.addAll(groupPage.getCurrentPage());
				break;
			}
			if(groupPage.getNextPage() == null) break;
			groupPage = groupPage.getNextPage().buildRequest().get();
			atomicInteger.getAndIncrement();
		}

		Long count = getGroupCount();
		List<GroupShortResponse> responseList = resultGroups.stream()
			.map(converterService::toGroupShortResponse)
			.collect(Collectors.toList());
		response.setList(responseList);
		response.setRangePages(getPagesRange(count));
		response.setCount(count);
		return response;
	}

	@Override
	public GroupPageResponse getAllByPageNumberAndSearchValue(Integer pageNumber, String searchValue) {
		GroupPageResponse groupPageResponse = new GroupPageResponse();
		LinkedList<Option> requestOptions = new LinkedList<Option>();
		requestOptions.add(new HeaderOption("ConsistencyLevel", "eventual"));

		GroupCollectionPage groupPage = graphServiceClient.groups()
			.buildRequest(requestOptions)
			.filter("startswith(displayName, '" + searchValue + "')")
			.get();

		List<Group> resultGroups = groupPage != null ? groupPage.getCurrentPage() : new ArrayList<>();

		List<List<Group>> lists = Lists.partition(resultGroups, 10);

		Long count = (long)resultGroups.size() == 0 ? 1 : (long)resultGroups.size();
		List<GroupShortResponse> resultList = lists.size() > 0 ? lists.get(pageNumber-1).stream().map(converterService::toGroupShortResponse)
			.sorted(Comparator.comparing(GroupShortResponse::getName)).collect(Collectors.toList()) : null;

		groupPageResponse.setList(resultList);
		groupPageResponse.setRangePages(getPagesRange(count));
		groupPageResponse.setCount(count);
		return groupPageResponse;
	}

	@Override
	public GroupResponse getGroupDataById(GroupPageRequest groupPageRequest) {
		String  groupId = groupPageRequest.getGroupId();
		Integer pageNumber = groupPageRequest.getPageNumber();
		Integer pollId = groupPageRequest.getPollId();

		Group group = graphServiceClient.groups("{"+groupId+"}")
			.buildRequest()
			.get();

		DirectoryObjectCollectionWithReferencesPage members = graphServiceClient.groups("{"+groupId+"}")
			.members().buildRequest().get();


		List<DirectoryObject> studentObjects = new ArrayList<>();
		AtomicInteger atomicInteger = new AtomicInteger(1);


		while(members != null && pageNumber >= atomicInteger.get()) {

			if(atomicInteger.get() == pageNumber){
				studentObjects.addAll(members.getCurrentPage());
				break;
			}

			if(members.getNextPage() == null) break;

			members = members.getNextPage().buildRequest().get();
			atomicInteger.getAndIncrement();
		}

		Optional<Poll> lastPoll = pollId == null ? pollRepository.findDistinctTopByGroupId(groupId).stream().findFirst()
			: pollRepository.findById(pollId);

		List<StudentData> students = lastPoll.map(value -> studentObjects.stream()
			.map(obj -> converterService.toStudentData(obj, value.getId()))
			.collect(Collectors.toList())).orElseGet(() -> studentObjects.stream().map(converterService::toStudentData)
			.collect(Collectors.toList()));

		List<PollData> polls = pollRepository.findByGroupId(groupId)
			.stream().filter(poll -> poll.getIntervalId() != null).map(converterService::toPollData)
			.collect(Collectors.toList());

		Integer votedStudentsCount = (int)students.stream().filter(studentData -> studentData.getAnswerData() != null)
			.filter(studentData -> studentData.getAnswerData().getListCiphers().size() != 0).count();

		GroupResponse response = converterService.toGroupResponse(group);
		response.setPollData(polls);
		response.setVotedStudents(votedStudentsCount);
		response.setStudentsCount(students.size());
		response.setStudentList(students);
		response.setRangePages(getPagesRange((long)students.size()));
		lastPoll.ifPresent(poll -> response.setCurrentPoll(converterService.toPollData(poll)));
		response.setIntervalData(List.of(IntervalEnum.NONE, IntervalEnum.EVERY_DAY,IntervalEnum.TWICE_A_DAY, IntervalEnum.TWICE_A_WEEK, IntervalEnum.THREE_TIMES_A_WEEK));
		return response;
	}

	@Override
	@Transactional
	public String createChat(String userId) {
		return messageService.createChat(userId);
	}

	@Override
	public Group getGroupById(String groupId) {
		return graphServiceClient.groups("{"+groupId+"}")
			.buildRequest()
			.get();
	}

	@Override
	public GroupResponse getGroupDataByIdAndSearchValue(GroupPageRequest groupPageRequest) {
		String groupId = groupPageRequest.getGroupId();
		String searchValue = groupPageRequest.getSearchValue();
		Integer pageNumber = groupPageRequest.getPageNumber();
		Integer pollId = groupPageRequest.getPollId();

		LinkedList<Option> requestOptions = new LinkedList<Option>();
		requestOptions.add(new HeaderOption("ConsistencyLevel", "eventual"));

		Group group = graphServiceClient.groups("{"+groupId+"}")
			.buildRequest()
			.get();

		DirectoryObjectCollectionWithReferencesPage members = graphServiceClient.groups("{"+groupId+"}")
			.members().buildRequest(requestOptions)
			.filter("startswith(displayName, '" + searchValue + "')")
			.orderBy("displayName")
			.get();


		List<DirectoryObject> studentObjects = new ArrayList<>();
		AtomicInteger atomicInteger = new AtomicInteger(1);


		while(members != null && pageNumber >= atomicInteger.get()) {

			if(atomicInteger.get() == pageNumber){
				studentObjects.addAll(members.getCurrentPage());
				break;
			}
			if(members.getNextPage() == null) break;

			members = members.getNextPage().buildRequest().get();
			atomicInteger.getAndIncrement();
		}

		List<StudentData> students = studentObjects.stream().map(converterService::toStudentData).collect(Collectors.toList());

		Optional<Poll> lastPoll = pollId == null ? pollRepository.findDistinctTopByGroupId(groupId).stream().findFirst()
			: pollRepository.findById(pollId);

		lastPoll.ifPresent(poll -> {

			students.forEach(studentData -> {
				Optional<Answer> studentAnswer = answerRepository.findByPollIdAndStudentId(poll.getId(), studentData.getId());
				studentAnswer.ifPresent(answer -> {
					List<String> ciphers = answer.getCiphers().stream().map(Cipher::getName).collect(Collectors.toList());
					studentData.setHasAnswered(!answer.getCiphers().isEmpty());
					studentData.setAnswerData(new AnswerData(answer.getCiphers(), String.join(",", ciphers), answer.getUpdateDate()));
				});
			});

		});

		List<PollData> polls = pollRepository.findByGroupId(groupId)
			.stream().map(converterService::toPollData)
			.collect(Collectors.toList());


		GroupResponse response = converterService.toGroupResponse(group);
		response.setStudentList(students);
		response.setPollData(polls);
		return response;
	}

	@Override
	public Long getGroupCount() {
		LinkedList<Option> requestOptions = new LinkedList<Option>();
		requestOptions.add(new HeaderOption("ConsistencyLevel", "eventual"));
		return graphServiceClient.groups().count().buildRequest(requestOptions).get();
	}

	@Override
	public Long getMembersCountByGroupId(String groupId) {
		LinkedList<Option> requestOptions = new LinkedList<Option>();
		requestOptions.add(new HeaderOption("ConsistencyLevel", "eventual"));
		DirectoryObjectCollectionWithReferencesPage count = graphServiceClient.groups("{"+groupId+"}")
			.members().buildRequest(requestOptions).count().get();
		return count.getCount();
	}

	private List<Integer> getPagesRange(Long count){
		int pagesCount = (int)Math.ceil(count / 10.0);

		return IntStream.rangeClosed(1, pagesCount + 2) // + 2 for chevrons
			.boxed().collect(Collectors.toList());
	}

}
