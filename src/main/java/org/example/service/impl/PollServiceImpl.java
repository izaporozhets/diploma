package org.example.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.Answer;
import org.example.entity.Cipher;
import org.example.entity.Poll;
import org.example.enums.IntervalEnum;
import org.example.exception.ApplicationException;
import org.example.payload.data.ExcelData;
import org.example.payload.data.StudentData;
import org.example.payload.request.CreatePollRequest;
import org.example.payload.request.GroupPageRequest;
import org.example.repository.AnswerRepository;
import org.example.repository.CipherRepository;
import org.example.repository.GroupRepository;
import org.example.repository.PollRepository;
import org.example.repository.StudentsRepository;
import org.example.service.ConverterService;
import org.example.service.MessageService;
import org.example.service.PollService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

@Service
@Slf4j
public class PollServiceImpl implements PollService {

	private final PollRepository     pollRepository;
	private final CipherRepository   cipherRepository;
	private final StudentsRepository studentsRepository;
	private final AnswerRepository   answerRepository;
	private final MessageService     messageService;
	private final GroupRepository    groupRepository;
	private final ConverterService   converterService;

	public PollServiceImpl(PollRepository pollRepository, StudentsRepository studentsRepository, CipherRepository cipherRepository, AnswerRepository answerRepository, MessageService messageService, GroupRepository groupRepository, ConverterService converterService) {
		this.pollRepository = pollRepository;
		this.studentsRepository = studentsRepository;
		this.cipherRepository = cipherRepository;
		this.answerRepository = answerRepository;
		this.messageService = messageService;
		this.groupRepository = groupRepository;
		this.converterService = converterService;
	}

	@Override
	@Transactional
	public Poll createPoll(CreatePollRequest request) {
		Poll poll = new Poll();
		poll.setGroupId(request.getGroupId());
		poll.setAddDate(LocalDateTime.now());
		poll.setIntervalId(request.getIntervalId());
		poll.setCourse(request.getSemester());
		poll.setFacultyName(request.getFacultyName());
		poll.setQuestions(request.getQuestionsNumber());
		poll.setIsEditable(true);
		poll = pollRepository.save(poll);
		List<StudentData> students = studentsRepository.getAllMembersByGroupId(request.getGroupId());
		Poll              finalPoll = poll;
		students.forEach(studentData -> {

			Answer answer = new Answer();
			answer.setCiphers(null);
			answer.setStudentId(studentData.getId());
			answer.setPoll(finalPoll);
			answer.setRemindedLast(LocalDateTime.now());
			answerRepository.save(answer);
			messageService.sentMessageTo(studentData.getId(), "http://localhost:8080/app/v1/polls/" + finalPoll.getId() + "/"+ studentData.getId());
		});
		return poll;
	}

	@Override
	public Model fillModelPollForm(Model model, String groupId) {
		List<Integer> answersAmount = Stream.of(1,2,3,4,5,6).collect(Collectors.toList());
		List<IntervalEnum> intervals = Stream.of(
			IntervalEnum.NONE, IntervalEnum.TWICE_A_DAY, IntervalEnum.EVERY_DAY,
			IntervalEnum.TWICE_A_WEEK, IntervalEnum.THREE_TIMES_A_WEEK
		).collect(Collectors.toList());
		List<Integer> semesters = Stream.of(1,2,3,4).collect(Collectors.toList());
		CreatePollRequest createPollRequest = new CreatePollRequest();
		createPollRequest.setGroupId(groupId);
		model.addAttribute("createRequest",createPollRequest);
		model.addAttribute("answersAmount", answersAmount);
		model.addAttribute("intervals", intervals);
		model.addAttribute("semesters", semesters);
		return model;
	}

	@Override
	public void fillModelStudentsPoll(Model model, Integer pollId, String studentId) {
		Optional<Poll> pollOptional = pollRepository.findById(pollId);
		List<Cipher> ciphers = cipherRepository.findAll();
		Optional<Answer> answerOptional = answerRepository.findByPollIdAndStudentId(pollId, studentId);
		model.addAttribute("ciphers", ciphers);
		pollOptional.ifPresent(poll -> {
			model.addAttribute("questions", poll.getQuestions());
			model.addAttribute("isEditable", poll.getIsEditable());
		});
		answerOptional.ifPresent(answer -> model.addAttribute("chosenAnswers", answer.getCiphers().size() > 0 ? answer.getCiphers() : null));
	}

	@Override
	public Poll editPoll(GroupPageRequest request) {
		Poll poll = pollRepository.findById(request.getPollId()).orElseThrow(() -> new ApplicationException(1, "Poll was not found"));
		if (request.getIsEditEnabled() != null) poll.setIsEditable(!request.getIsEditEnabled());
		if (request.getIntervalName() != null) poll.setIntervalId(IntervalEnum.getIdByDescription(request.getIntervalName()));
		return pollRepository.save(poll);
	}

	@Override
	@Transactional
	public void remindForStudents(Integer pollId) {
		Poll poll = pollRepository.findById(pollId).orElseThrow(() -> new ApplicationException(1, "Poll was not found"));
		List<StudentData> list = studentsRepository.getAllMembersByGroupIdAndPollId(poll.getGroupId(), pollId);
		list.stream().filter(studentData -> !studentData.getHasAnswered())
		.forEach(studentData -> {
			messageService.sentMessageTo(studentData.getId(),"http://localhost:8080/app/v1/polls/" + poll.getId() + "/"+ studentData.getId());
			Optional<Answer> answerOptional = answerRepository.findByPollIdAndStudentId(pollId, studentData.getId());
			answerOptional.ifPresent(answer -> {
				answer.setRemindedLast(LocalDateTime.now());
				answerRepository.save(answer);
			});
		});
	}

	@Override
	public String getGroupNameByPollId(Integer pollId) {
		Optional<Poll> pollOptional = pollRepository.findById(pollId);
		return pollOptional.map(poll -> groupRepository.getGroupById(poll.getGroupId()).displayName).orElse("undefined");
	}

	@Override
	public List<ExcelData> getExcelDataByPollId(Integer pollId) {
		Poll poll = pollRepository.findById(pollId).orElseThrow(() -> new ApplicationException(1, "Poll was not found"));
		List<StudentData> studentData = studentsRepository.getAllMembersByGroupIdAndPollId(poll.getGroupId(), pollId);
		return converterService.toExcelData(poll, studentData);
	}
}
