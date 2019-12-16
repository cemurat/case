package com.n11.project.repo;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.n11.project.model.Presentation;

public interface PresentationRepository extends CrudRepository<Presentation, Long> {

	@Query("select p from Presentation p where p.subject = :subject")
	Presentation findByName(String subject);
	
}
