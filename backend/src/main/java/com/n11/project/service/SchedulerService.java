package com.n11.project.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.n11.project.model.Presentation;
import com.n11.project.repo.PresentationRepository;

@Component
public class SchedulerService {

	int MORNING_MINUTES = 180;
	int AFTERNOON_MINUTES = 240;

	@Autowired
	PresentationRepository presentationRepository;

	public Iterable<Presentation> schedulePresentations() {

		List<Presentation> unScheduledPresentations = (List<Presentation>) presentationRepository.findAll();
		List<Presentation> scheduledPresentations = new ArrayList<>();
		int track_no = 1;

		while (unScheduledPresentations.iterator().hasNext()) {
			scheduleMorningPresentations(unScheduledPresentations, scheduledPresentations, track_no);
			scheduleAfternoonPresentations(unScheduledPresentations, scheduledPresentations, track_no);
			track_no += 1;
		}

		return (List<Presentation>) presentationRepository.findAll();
	}

	public void scheduleMorningPresentations(List<Presentation> unScheduledPresentations,
			List<Presentation> scheduledPresentations, int track_no) {

		scheduledPresentations = new ArrayList<>();
		scheduledPresentations = schedule(unScheduledPresentations.stream().collect(Collectors.toList()),
				scheduledPresentations, MORNING_MINUTES);

		LocalDateTime date = LocalDate.now().atTime(9, 0);
		determineStartingTimeOfScheduledPresentations(scheduledPresentations, date);
		scheduledPresentations.forEach((p) -> p.setTrackNo(track_no));
		scheduledPresentations
				.forEach((Presentation p) -> System.out.println("Morning " + p.getId() + " :" + p.getSubject()));
		presentationRepository.saveAll(scheduledPresentations);
		unScheduledPresentations.removeAll(scheduledPresentations);

	}

	public void scheduleAfternoonPresentations(List<Presentation> unScheduledPresentations,
			List<Presentation> scheduledPresentations, int track_no) {

		scheduledPresentations = new ArrayList<>();
		scheduledPresentations = schedule(unScheduledPresentations.stream().collect(Collectors.toList()),
				scheduledPresentations, AFTERNOON_MINUTES);
		LocalDateTime date = LocalDate.now().atTime(13, 0);
		determineStartingTimeOfScheduledPresentations(scheduledPresentations, date);

		determineStartingTimeOfScheduledPresentations(scheduledPresentations, date);
		createNetworkMeetingIfRequired(scheduledPresentations, track_no);
		scheduledPresentations.forEach((Presentation p) -> p.setTrackNo(track_no));
		scheduledPresentations
				.forEach((Presentation p) -> System.out.println("Afternoon " + p.getId() + " :" + p.getSubject()));

		presentationRepository.saveAll(scheduledPresentations);
		unScheduledPresentations.removeAll(scheduledPresentations);

	}

	public void createNetworkMeetingIfRequired(List<Presentation> scheduledPresentations, int track_no) {

		if (scheduledPresentations.size() == 0) {
			LocalDateTime date = LocalDate.now().atTime(16, 0);
			Presentation networkMeeting = new Presentation();
			networkMeeting.setSubject("Network Meeting " + track_no);
			networkMeeting.setStartingTime(date);
			scheduledPresentations.add(networkMeeting);
			return;
		}

		Presentation lastPresentation = scheduledPresentations.get(scheduledPresentations.size() - 1);
		LocalDateTime lastStartingTime = lastPresentation.getStartingTime();
		LocalDateTime endedTime = lastStartingTime.plusMinutes(lastPresentation.getDuration());
		LocalDateTime maximumTime = LocalDate.now().atTime(17, 0);

		if (maximumTime.isAfter(endedTime)) {
			LocalDateTime earliestMeetingTime = LocalDate.now().atTime(16, 0);
			Presentation networkMeeting = new Presentation();
			networkMeeting.setSubject("Network Meeting");

			if (endedTime.isAfter(earliestMeetingTime)) {
				networkMeeting.setStartingTime(endedTime);
			} else {
				networkMeeting.setStartingTime(earliestMeetingTime);
			}
			scheduledPresentations.add(networkMeeting);
		}

	}

	public void determineStartingTimeOfScheduledPresentations(List<Presentation> scheduledPresentations,
			LocalDateTime date) {

		for (Presentation presentation : scheduledPresentations) {
			presentation.setStartingTime(date);
			date = date.plusMinutes(presentation.getDuration());
		}
	}

	public List<Presentation> schedule(List<Presentation> unScheduledPresentations,
			List<Presentation> scheduledPresentations, int remainingTime) {

		if (unScheduledPresentations.size() == 0) {
			return scheduledPresentations;
		}

		if (remainingTime == 0) {
			return scheduledPresentations;
		}

		Presentation selectedPresentation = unScheduledPresentations.remove(0);
		List<Presentation> withSelectedPresentationList = new ArrayList<>();
		if (remainingTime - selectedPresentation.getDuration() >= 0) {
			List<Presentation> copyOfUnScheduled = unScheduledPresentations.stream().collect(Collectors.toList());
			List<Presentation> copyOfScheduled = scheduledPresentations.stream().collect(Collectors.toList());
			copyOfScheduled.add(selectedPresentation);
			withSelectedPresentationList = schedule(copyOfUnScheduled, copyOfScheduled,
					remainingTime - selectedPresentation.getDuration());
		}

		List<Presentation> withoutSelectedPresentationList = schedule(unScheduledPresentations, scheduledPresentations,
				remainingTime);
		int totalTimeUsageWithoutSelected = (int) withoutSelectedPresentationList.stream()
				.map((Presentation p) -> p.getDuration()).reduce(0, (a, b) -> a + b);
		int totalTimeUsageWithSelected = (int) withSelectedPresentationList.stream()
				.map((Presentation p) -> p.getDuration()).reduce(0, (a, b) -> a + b);

		if (totalTimeUsageWithoutSelected >= totalTimeUsageWithSelected) {
			return withoutSelectedPresentationList;
		} else {
			return withSelectedPresentationList;
		}

	}

}
