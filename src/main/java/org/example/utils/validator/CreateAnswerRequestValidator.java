package org.example.utils.validator;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.example.payload.request.CreateAnswerRequest;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class CreateAnswerRequestValidator implements Validator {
	@Override
	public boolean supports(Class<?> clazz) {
		return false;
	}

	@Override
	public void validate(Object o, Errors errors) {
		CreateAnswerRequest request =(CreateAnswerRequest)o;
		List<String> ciphers = request.getCipherIds();
		Set<String> ciphersUnique = new HashSet<>(ciphers);
		if(ciphers.size() > ciphersUnique.size()){
			errors.rejectValue("cipherAnswers", "Відповіді не можуть бути однаковими");
		}
	}
}
