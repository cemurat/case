package com.n11.project.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.n11.project.model.Presentation;
import com.n11.project.service.PresentationService;

@RestController
@RequestMapping("/v1/presentations")
public class PresentationController {

	@Autowired
	PresentationService presentationService;

	@PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public Presentation createPresentation(@Valid @RequestBody Presentation presentation) {
		return presentation = presentationService.create(presentation);
	}

	@DeleteMapping("/{id}")
	public void deletePresentation(@PathVariable(name = "id") Long id) {
		presentationService.delete(id);
	}

	@PutMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public Presentation updatePresentation(@Valid @RequestBody Presentation presentation) {
		return presentationService.update(presentation);
	}

	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public Iterable<Presentation> getPresentations() {
		return presentationService.getAllPresentations();
	}

}
