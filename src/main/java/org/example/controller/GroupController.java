package org.example.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.payload.data.GroupResponse;
import org.example.payload.dto.SearchDto;
import org.example.payload.request.GroupPageRequest;
import org.example.payload.response.GroupPageResponse;
import org.example.service.GroupService;
import org.example.service.MessageService;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Slf4j
@Controller
public class GroupController {

	private final GroupService groupService;
	private final MessageService messageService;

	public GroupController(GroupService groupService, MessageService messageService) {
		this.groupService = groupService;
		this.messageService = messageService;
	}

	@RequestMapping(value = "/app/v1/groups", method = RequestMethod.GET)
	public String allGroupsPost(@NotNull Model model, @NotNull @ModelAttribute(value = "searchDto") SearchDto searchDto){
		Integer pageNumber = searchDto.getPageNumber();
		String  searchValue = searchDto.getSearchValue();
		log.info("searchValue = " + searchValue + ", " + "pageNumber = " + pageNumber);
		GroupPageResponse groupData = searchValue.isEmpty() ?
			groupService.getAllByPageNumber(pageNumber) :
			groupService.getAllByPageNumberAndSearchValue(pageNumber, searchValue);

		model.addAttribute("groupList", groupData.getList());
		model.addAttribute("range", groupData.getRangePages());
		model.addAttribute("searchDto",  searchDto);
		return "groups/groups";
	}

	@GetMapping("/app/v1/group")
	public String getGroupPage(Model model, @ModelAttribute(value = "groupPageRequest") GroupPageRequest groupPageRequest){
		String  searchValue = groupPageRequest.getSearchValue();

		GroupResponse group = searchValue.isEmpty() ?
			groupService.getGroupDataById(groupPageRequest) :
			groupService.getGroupDataByIdAndSearchValue(groupPageRequest);

		model.addAttribute("group", group);
		model.addAttribute("range", group.getRangePages());
		model.addAttribute("groupPageRequest", groupPageRequest);
		return "groups/group";
	}

}
