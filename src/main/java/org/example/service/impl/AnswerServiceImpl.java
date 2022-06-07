package org.example.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.Answer;
import org.example.entity.Cipher;
import org.example.entity.Poll;
import org.example.payload.request.CreateAnswerRequest;
import org.example.repository.AnswerRepository;
import org.example.repository.CipherRepository;
import org.example.repository.PollRepository;
import org.example.service.AnswerService;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AnswerServiceImpl implements AnswerService {

	private final PollRepository   pollRepository;
 	private final AnswerRepository answerRepository;
	private final CipherRepository cipherRepository;

	public AnswerServiceImpl(PollRepository pollRepository, AnswerRepository answerRepository, CipherRepository cipherRepository) {
		this.pollRepository = pollRepository;
		this.answerRepository = answerRepository;
		this.cipherRepository = cipherRepository;
	}

	@Override
	public void createAnswer(CreateAnswerRequest request) {
		Optional<Poll> pollOptional = pollRepository.findById(request.getPollId());
		pollOptional.ifPresent(poll -> {
			List<Cipher> providedCiphers = cipherRepository.findAllByIds(request.getCipherIds().stream().map(Integer::valueOf).collect(Collectors.toList()));
			Optional<Answer> answerOptional = answerRepository.findByPollIdAndStudentId(request.getPollId(), request.getStudentId());
			answerOptional.ifPresent(answer -> {
				answer.setCiphers(providedCiphers);
				answer.setUpdateDate(LocalDateTime.now());
				answerRepository.save(answer);
			});
		});
	}
}
