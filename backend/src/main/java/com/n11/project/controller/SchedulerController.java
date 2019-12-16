package com.n11.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.n11.project.model.Presentation;
import com.n11.project.service.SchedulerService;

@RestController
@RequestMapping("/v1/scheduler")
public class SchedulerController {

	@Autowired
	SchedulerService schedulerService;

	@PostMapping
	public Iterable<Presentation> schedulePresentations() {
		return schedulerService.schedulePresentations();
	}

}
