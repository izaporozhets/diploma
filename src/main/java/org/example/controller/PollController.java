package org.example.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.compress.utils.IOUtils;
import org.example.payload.data.ExcelData;
import org.example.payload.request.CreateAnswerRequest;
import org.example.payload.request.CreatePollRequest;
import org.example.payload.request.GroupPageRequest;
import org.example.service.AnswerService;
import org.example.service.MessageService;
import org.example.service.PollService;
import org.example.utils.excel.ExcelFileExporter;
import org.example.utils.validator.CreateAnswerRequestValidator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller()
public class PollController {

	private final PollService pollService;
	private final MessageService messageService;
	private final AnswerService answerService;
	private final CreateAnswerRequestValidator validator;

	public PollController(PollService pollService, MessageService messageService, AnswerService answerService, CreateAnswerRequestValidator validator) {
		this.pollService = pollService;
		this.messageService = messageService;
		this.answerService = answerService;
		this.validator = validator;
	}


	@GetMapping("/app/v1/polls/{pollId}/export")
	public void exportPoll(HttpServletResponse response, @PathVariable(value = "pollId") Integer pollId) throws IOException {
		String name = pollService.getGroupNameByPollId(pollId);
		response.setContentType("application/octet-stream");
		response.setHeader("Content-Disposition", "attachment; filename="+name+".xlsx");
		List<ExcelData> excelData = pollService.getExcelDataByPollId(pollId);
		ByteArrayInputStream stream = ExcelFileExporter.excelDataToExcelFile(excelData);
		IOUtils.copy(stream, response.getOutputStream());
	}

	@PostMapping("/app/v1/polls")
	public String createPoll(Model model, @ModelAttribute CreatePollRequest request) {
		pollService.createPoll(request);
		model.addAttribute("groupId", request.getGroupId());
		return "redirect:group";
	}

	@PostMapping("/app/v1/polls/edit")
	public String editPoll(Model model, @ModelAttribute GroupPageRequest request){
		pollService.editPoll(request);
		model.addAttribute("groupId", request.getGroupId());
		model.addAttribute("pollId", request.getPollId());
		return "redirect:/app/v1/group";
	}

	@PostMapping("/app/v1/polls/remind")
	public String remindForStudents(Model model, @ModelAttribute GroupPageRequest request){
		pollService.remindForStudents(request.getPollId());
		model.addAttribute("groupId", request.getGroupId());
		model.addAttribute("pollId", request.getPollId());
		return "redirect:/app/v1/group";
	}

	@GetMapping("/app/v1/polls/{groupId}")
	public String openPollForm(Model model, @PathVariable(value = "groupId") String groupId) {
		pollService.fillModelPollForm(model, groupId);
		return "polls/poll_create";
	}

	@GetMapping("/app/v1/polls/{pollId}/{studentId}")
	public String getStudentsPoll(Model model,@PathVariable(value = "pollId") Integer pollId ,@PathVariable(value = "studentId") String studentId) {
		pollService.fillModelStudentsPoll(model, pollId, studentId);
		CreateAnswerRequest request =  new CreateAnswerRequest();
		request.setPollId(pollId);
		request.setStudentId(studentId);
		model.addAttribute("createAnswer", request);
		return "polls/poll";
	}

	@GetMapping("/app/v1/polls/finish")
	public String createStudentAnswer(Model model, @ModelAttribute CreateAnswerRequest request) {
		if(request.getCipherIds().stream().anyMatch(String::isEmpty)){
			pollService.fillModelStudentsPoll(model, request.getPollId(), request.getStudentId());
			model.addAttribute("noneAnswersError", true);
			model.addAttribute("createAnswer", request);
			return "polls/poll";
		}
		if(request.getCipherIds().size() > new HashSet<>(request.getCipherIds()).size()){
			pollService.fillModelStudentsPoll(model, request.getPollId(), request.getStudentId());
			model.addAttribute("sizeError", true);
			model.addAttribute("createAnswer", request);
			return "polls/poll";
		}
		answerService.createAnswer(request);
		return "redirect:/app/v1/polls/" + request.getPollId() + "/" + request.getStudentId();
	}
}
