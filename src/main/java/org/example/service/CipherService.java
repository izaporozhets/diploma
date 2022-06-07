package org.example.service;

import java.util.List;
import org.example.entity.Cipher;

public interface CipherService {
	List<Cipher> createInitialCiphers(Integer answersNumber);
}
