package org.example.payload.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.enums.IntervalEnum;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
@NoArgsConstructor
@AllArgsConstructor
public class GroupResponse {
	private String             id;
	private String             name;
	private List<StudentData>  studentList;
	private List<Integer>      rangePages;
	private List<PollData>     pollData;
	private PollData           currentPoll;
	private List<IntervalEnum> intervalData;
 	private Integer            votedStudents;
	private Integer            studentsCount;

	@Override
	public String toString() {
		return "GroupData{" +
			"id='" + id + '\'' +
			", name='" + name + '\'' +
			'}';
	}
}
