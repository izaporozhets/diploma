package org.example.entity;

import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "answers")
public class Answer {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "a_id")
	private Integer id;

	@ManyToMany
	@JoinTable(
		name = "answers_cipers",
		joinColumns = {@JoinColumn(name = "ac_answer_id", referencedColumnName = "a_id")},
		inverseJoinColumns = {@JoinColumn(name = "ac_ciper_id", referencedColumnName = "c_id")}
	)
	private List<Cipher> ciphers;

	@Column(name = "a_upd_date")
	private LocalDateTime updateDate;

	@Column(name = "a_std_id")
	private String studentId;

	@Column(name = "a_reminded_last")
	private LocalDateTime remindedLast;

	@ManyToOne()
	@JoinColumn(name = "a_p_id")
	private Poll poll;
}
