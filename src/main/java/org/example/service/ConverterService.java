package org.example.service;

import com.microsoft.graph.models.DirectoryObject;
import com.microsoft.graph.models.Group;
import java.util.List;
import org.example.entity.Poll;
import org.example.entity.User;
import org.example.payload.data.ExcelData;
import org.example.payload.data.GroupMemberData;
import org.example.payload.data.GroupResponse;
import org.example.payload.data.PollData;
import org.example.payload.data.StudentData;
import org.example.payload.data.UserData;
import org.example.payload.response.GroupShortResponse;

public interface ConverterService {
	GroupResponse toGroupResponse(Group group);
	StudentData toStudentData(DirectoryObject directoryObject);
	StudentData toStudentData(DirectoryObject directoryObject, Integer pollId);
	GroupShortResponse toGroupShortResponse(Group group);
	GroupMemberData toGroupMemberData(DirectoryObject obj);
	PollData toPollData(Poll poll);
	List<ExcelData> toExcelData(Poll poll, List<StudentData> studentData);
	UserData toUserData(User user);
}
