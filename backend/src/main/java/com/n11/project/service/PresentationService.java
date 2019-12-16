package com.n11.project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.n11.project.model.Presentation;
import com.n11.project.repo.PresentationRepository;

@Component
public class PresentationService {
	
	@Autowired
	PresentationRepository presentationRepo;
	
	public Presentation create(Presentation entity) {
		return presentationRepo.save(entity);
	}

	public void delete(Long id) {
		presentationRepo.deleteById(id);		
	}

	public Presentation update(Presentation presentation) {
		return presentationRepo.save(presentation);
	} 
	
	public Iterable<Presentation> getAllPresentations() {
		return presentationRepo.findAll();		
	}

}
