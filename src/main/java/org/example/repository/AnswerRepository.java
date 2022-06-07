package org.example.repository;

import java.util.List;
import java.util.Optional;
import org.example.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Integer> {
	@Query("select answer from Answer answer where answer.poll.id = :pollId")
	List<Answer> findByPollId(@Param(value = "pollId") Integer pollId);

	@Query("select answer from Answer answer where answer.poll.id = :pollId and answer.studentId = :userId")
	Optional<Answer> findByPollIdAndStudentId(@Param(value = "pollId") Integer pollId, @Param(value = "userId") String userId);

	@Query("select answer from Answer answer where answer.poll.id in :pollIds and answer.ciphers.size = 0")
	List<Answer> findAllByPollIds(@Param(value = "pollIds") List<Integer> pollIds);
}
