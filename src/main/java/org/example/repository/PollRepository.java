package org.example.repository;

import java.util.List;
import org.example.entity.Poll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PollRepository extends JpaRepository<Poll, Integer> {
	@Query("select poll from Poll poll where poll.groupId = :groupId order by poll.addDate desc")
	List<Poll> findDistinctTopByGroupId(@Param("groupId") String groupId);

	List<Poll> findByGroupId(String groupId);

	@Query("select poll from Poll poll where poll.isEditable = true and poll.intervalId = :intervalId")
	List<Poll> findAllEditablePollsByIntervalId(@Param("intervalId") Integer intervalId);
}
