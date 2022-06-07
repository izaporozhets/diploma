package org.example.controller;

import java.util.List;
import java.util.Objects;
import org.example.entity.Cipher;
import org.example.payload.request.CreateCipherRequest;
import org.example.repository.CipherRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller()
public class CipherController {

	private final CipherRepository cipherRepository;

	public CipherController(CipherRepository cipherRepository) {
		this.cipherRepository = cipherRepository;
	}

	@GetMapping("/app/v1/ciphers")
	public String getCiphersPage(Model model, @RequestParam(value = "searchValue", required = false) String searchValue){
		List<Cipher> ciphersList = Objects.isNull(searchValue) ? cipherRepository.findAll()
			: cipherRepository.findAllBySearchValue(searchValue);
		model.addAttribute("ciphers", ciphersList);
		return "ciphers/ciphers";
	}

	@PostMapping("/app/v1/ciphers/delete")
	public String deleteCipher(@RequestParam(value = "cipherId") Integer cipherId){
		cipherRepository.deleteById(cipherId);
		return "redirect:/app/v1/ciphers";
	}

	@PostMapping("/app/v1/ciphers/create")
	public String createCipher(@ModelAttribute CreateCipherRequest request){
		Cipher cipher = new Cipher();
		cipher.setName(request.getCipherName());
		cipher.setDescription(request.getCipherDescription());
		cipherRepository.save(cipher);
		return "redirect:/app/v1/ciphers";
	}

	@GetMapping("/app/v1/ciphers/create")
	public String getCreateCipherPage(Model model){
		model.addAttribute("request", new CreateCipherRequest());
		return "ciphers/ciphers-create";
	}

	@PostMapping("/app/v1/ciphers/edit")
	public String editCipher(@RequestParam(value = "cipherId") Integer cipherId){
		return "ciphers";
	}

}
