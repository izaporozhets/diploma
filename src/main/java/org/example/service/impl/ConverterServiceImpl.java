package org.example.service.impl;

import com.microsoft.graph.models.DirectoryObject;
import com.microsoft.graph.models.Group;
import com.microsoft.graph.models.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.Answer;
import org.example.entity.Cipher;
import org.example.entity.Poll;
import org.example.enums.IntervalEnum;
import org.example.enums.RoleEnum;
import org.example.payload.data.AnswerData;
import org.example.payload.data.ExcelData;
import org.example.payload.data.GroupMemberData;
import org.example.payload.data.GroupResponse;
import org.example.payload.data.PollData;
import org.example.payload.data.StudentData;
import org.example.payload.data.UserData;
import org.example.payload.response.GroupShortResponse;
import org.example.repository.AnswerRepository;
import org.example.repository.GroupRepository;
import org.example.repository.PollRepository;
import org.example.service.ConverterService;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ConverterServiceImpl implements ConverterService {

	private final AnswerRepository   answerRepository;
	private final PollRepository     pollRepository;
	private final GroupRepository    groupRepository;

	public ConverterServiceImpl(AnswerRepository answerRepository, PollRepository pollRepository, GroupRepository groupRepository) {
		this.answerRepository = answerRepository;
		this.pollRepository = pollRepository;
		this.groupRepository = groupRepository;
	}

	@Override
	public GroupResponse toGroupResponse(Group group) {
		GroupResponse groupResponse = new GroupResponse();
		groupResponse.setId(group.id);
		groupResponse.setName(group.displayName);
		return groupResponse;
	}

	@Override
	public StudentData toStudentData(DirectoryObject directoryObject) {
		User user = (User)directoryObject;
		StudentData studentData = new StudentData();
		studentData.setId(user.id);
		studentData.setName(user.givenName != null ? user.givenName + " " + user.surname : user.displayName);
		return studentData;
	}

	@Override
	public StudentData toStudentData(DirectoryObject directoryObject, Integer pollId) {
		User user = (User)directoryObject;
		StudentData studentData = new StudentData();
		studentData.setId(user.id);
		studentData.setName(user.givenName != null ? user.givenName + " " + user.surname : user.displayName);
		studentData.setEmail(user.mail);
		Optional<Answer> answerOptional = answerRepository.findByPollIdAndStudentId(pollId, user.id);
		answerOptional.ifPresent(answer -> {
			List<String> ciphers = answer.getCiphers().stream().map(Cipher::getName).collect(Collectors.toList());
			studentData.setAnswerData(new AnswerData(answer.getCiphers(), String.join(", ", ciphers), answer.getUpdateDate()));
			studentData.setHasAnswered(!answer.getCiphers().isEmpty());
			studentData.setRemindedAt(answer.getRemindedLast());
		});
		return studentData;
	}

	@Override
	public GroupShortResponse toGroupShortResponse(Group group) {
		return new GroupShortResponse(group.id, group.displayName);
	}

	@Override
	public GroupMemberData toGroupMemberData(DirectoryObject obj) {
		User user = (User)obj;
		return new GroupMemberData(user.id, user.displayName);
	}

	@Override
	public PollData toPollData(Poll poll) {
		return new PollData()
			.setId(poll.getId())
			.setName(poll.getAddDate().toLocalDate().toString() + " : " + poll.getCourse() + " семестр")
			.setFacultyName(poll.getFacultyName())
			.setSemester(poll.getCourse())
			.setCreationDate(poll.getAddDate())
			.setIntervalName(IntervalEnum.getDescriptionById(poll.getIntervalId()))
			.setIsEditable(poll.getIsEditable());
	}

	@Override
	public List<ExcelData> toExcelData(Poll poll, List<StudentData> studentData) {
		String groupName = groupRepository.getGroupById(poll.getGroupId()).displayName;
		List<ExcelData> result = new ArrayList<>();
		studentData.forEach(stdData -> {
			stdData.getAnswerData().getListCiphers().forEach(cipher -> {
				ExcelData excelData = new ExcelData();
				excelData.setNo(poll.getFacultyName());
				excelData.setEmail(stdData.getEmail());
				excelData.setName(stdData.getName());
				excelData.setCourse(poll.getCourse());
				excelData.setGroupName(groupName);
				excelData.setAnswersAmount(poll.getQuestions());
				excelData.setCipherName(cipher.getName());
				excelData.setCipherDescription(cipher.getDescription());
				result.add(excelData);
			});
		});
		return result;
	}

	@Override
	public UserData toUserData(org.example.entity.User user) {
		return new UserData().setId(user.getId())
			.setName(user.getName())
			.setSurname(user.getSurname())
			.setPatronymic(user.getPatronymic())
			.setLastLogin(user.getLastLogin())
			.setUsername(user.getUsername()).setIsPasswordConfirmed(user.getIsPasswordConfirmed())
			.setRoleName(RoleEnum.USER.equals(user.getRoleEnum()) ? "Користувач" : "Адміністратор");
	}

}
