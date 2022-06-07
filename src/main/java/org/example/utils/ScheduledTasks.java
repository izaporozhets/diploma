package org.example.utils;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.example.entity.Answer;
import org.example.entity.Poll;
import org.example.enums.IntervalEnum;
import org.example.repository.AnswerRepository;
import org.example.repository.PollRepository;
import org.example.service.MessageService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasks {

	private final PollRepository pollRepository;
	private final AnswerRepository answerRepository;
	private final MessageService messageService;

	public ScheduledTasks(PollRepository pollRepository, AnswerRepository answerRepository, MessageService messageService) {
		this.pollRepository = pollRepository;
		this.answerRepository = answerRepository;
		this.messageService = messageService;
	}

	@Scheduled(cron = "0 0 10 * * 1-5")
	public void at10EveryDay() {
		List<Poll> polls = pollRepository.findAllEditablePollsByIntervalId(IntervalEnum.EVERY_DAY.getId());
		List<Poll> pollsTwice = pollRepository.findAllEditablePollsByIntervalId(IntervalEnum.TWICE_A_DAY.getId());
		List<Integer> pollIds = Stream.of(polls, pollsTwice)
			.flatMap(Collection::stream).map(Poll::getId).collect(Collectors.toList());
		sendNotifications(pollIds);
	}

	@Scheduled(cron = "0 0 15 * * 1-5")
	public void at15EveryDay() {
		List<Poll> polls = pollRepository.findAllEditablePollsByIntervalId(IntervalEnum.TWICE_A_DAY.getId());
		List<Integer> pollIds = polls.stream().map(Poll::getId).collect(Collectors.toList());
		sendNotifications(pollIds);
	}

	@Scheduled(cron = "0 0 15 * * 2,4")
	public void at15EveryTuesdayAndThursday(){
		List<Poll> polls = pollRepository.findAllEditablePollsByIntervalId(IntervalEnum.TWICE_A_WEEK.getId());
		List<Integer> pollIds = polls.stream().map(Poll::getId).collect(Collectors.toList());
		sendNotifications(pollIds);
	}

	@Scheduled(cron = "0 0 15 * * 1,3,5")
	public void at15EveryMondayAndWednesdayAndFriday(){
		List<Poll> polls = pollRepository.findAllEditablePollsByIntervalId(IntervalEnum.THREE_TIMES_A_WEEK.getId());
		List<Integer> pollIds = polls.stream().map(Poll::getId).collect(Collectors.toList());
		sendNotifications(pollIds);
	}

	private void sendNotifications(List<Integer> pollIds) {
		List<Answer> answers = answerRepository.findAllByPollIds(pollIds);
		answers.forEach(answer -> {
			messageService.sentMessageTo(answer.getStudentId(), "http://localhost:8080/app/v1/polls/" + answer.getPoll().getId() + "/"+ answer.getStudentId());
			answer.setRemindedLast(LocalDateTime.now());
			answerRepository.save(answer);
		});
	}
}
