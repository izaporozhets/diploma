package org.example.payload.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PollDto {
	private String  groupId;
	private Integer semesterNumber;
	private Integer intervalId;
	private String  facultyName;
	private Integer answersNumber;
}
