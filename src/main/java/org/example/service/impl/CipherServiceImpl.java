package org.example.service.impl;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.Cipher;
import org.example.service.CipherService;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CipherServiceImpl implements CipherService {

	@Override
	public List<Cipher> createInitialCiphers(Integer answersNumber) {
		for(int i = 0; i < answersNumber; i++){

		}
		return null;
	}
}
