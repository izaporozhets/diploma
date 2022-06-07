package org.example.service;

import java.util.List;
import org.example.entity.Poll;
import org.example.payload.data.ExcelData;
import org.example.payload.request.CreatePollRequest;
import org.example.payload.request.GroupPageRequest;
import org.springframework.ui.Model;

public interface PollService {
	Poll createPoll(CreatePollRequest request);

	Model fillModelPollForm(Model model, String groupId);

	void fillModelStudentsPoll(Model model, Integer pollId, String studentId);

	Poll editPoll(GroupPageRequest request);

	void remindForStudents(Integer pollId);

	String getGroupNameByPollId(Integer pollId);

	List<ExcelData> getExcelDataByPollId(Integer pollId);
}
