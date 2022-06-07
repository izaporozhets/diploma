package org.example.entity;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "polls")
public class Poll {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "p_id")
	private Integer id;

	@Column(name = "p_group_id")
	private String groupId;

	@Column(name = "p_faculty")
	private String facultyName;

	@Column(name = "p_course")
	private Integer course;

	@Column(name = "p_interval_id")
	private Integer intervalId;

	@Column(name = "p_questions")
	private Integer questions;

	@Column(name = "p_add_date")
	private LocalDateTime addDate;

	@Column(name = "p_is_editable")
	private Boolean isEditable;

}
