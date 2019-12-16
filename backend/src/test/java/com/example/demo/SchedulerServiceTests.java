package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import com.n11.project.CaseApplication;
import com.n11.project.model.Presentation;
import com.n11.project.repo.PresentationRepository;
import com.n11.project.service.SchedulerService;

@SpringBootTest(classes = CaseApplication.class)
class SchedulerServiceTests {

	@InjectMocks
	SchedulerService schedulerService;

	@Mock
	PresentationRepository presentationRepo;

	@Test
	void networkMeetingShouldBeCreatedAt16PMIfThereIsNotAnyMeetingAfter13PM() {

		List<Presentation> emptyPresentations = new ArrayList();
		int trackNo = 1;
		schedulerService.createNetworkMeetingIfRequired(emptyPresentations, trackNo);
		Presentation networkMeeting = emptyPresentations.get(0);
		assertEquals(networkMeeting.getSubject(), "Network Meeting " + trackNo);
		LocalDateTime date = LocalDate.now().atTime(16, 0);
		assertEquals(networkMeeting.getStartingTime(), date);
	}

	@Test
	void networkMeetingShouldNotBeCreatedIfMeetingsLastUntil17PM() {

		List<Presentation> presentations = new ArrayList();
		Presentation presentation = new Presentation();
		presentation.setDuration(60);
		LocalDateTime date = LocalDate.now().atTime(16, 0);
		presentation.setStartingTime(date);
		presentations.add(presentation);
		int trackNo = 1;

		schedulerService.createNetworkMeetingIfRequired(presentations, trackNo);
		assertEquals(1, presentations.size());
	}

	@Test
	void networkMeetingShouldBeCreatedAfterPresentationEnded() {

		List<Presentation> presentations = new ArrayList();
		Presentation presentation = new Presentation();
		presentation.setDuration(30);
		LocalDateTime date = LocalDate.now().atTime(16, 0);
		presentation.setStartingTime(date);
		int trackNo = 1;
		presentations.add(presentation);

		schedulerService.createNetworkMeetingIfRequired(presentations, trackNo);

		Presentation networkMeeting = presentations.get(1);
		LocalDateTime networkMeetingStartingTime = LocalDate.now().atTime(16, 30);
		assertEquals(networkMeetingStartingTime, networkMeeting.getStartingTime());
	}

	@Test
	void networkMeetingShouldBeCreatedAt16PMIFPresentationEndedBefore16PM() {

		List<Presentation> presentations = new ArrayList();
		Presentation presentation = new Presentation();
		presentation.setDuration(30);
		LocalDateTime date = LocalDate.now().atTime(15, 0);
		presentation.setStartingTime(date);
		int trackNo = 1;
		presentations.add(presentation);

		schedulerService.createNetworkMeetingIfRequired(presentations, trackNo);

		Presentation networkMeeting = presentations.get(1);
		LocalDateTime networkMeetingStartingTime = LocalDate.now().atTime(16, 00);
		assertEquals(networkMeetingStartingTime, networkMeeting.getStartingTime());
	}

	@Test
	void startingTimesShouldBeIncreasedCorrectly() {
		List<Presentation> presentations = new ArrayList();
		Presentation firstPresentation = new Presentation();
		firstPresentation.setDuration(45);

		Presentation secondPresentation = new Presentation();
		secondPresentation.setDuration(30);

		presentations.add(firstPresentation);
		presentations.add(secondPresentation);

		LocalDateTime localDate = LocalDate.now().atTime(9, 0);
		schedulerService.determineStartingTimeOfScheduledPresentations(presentations, localDate);

		LocalDateTime startingTime = LocalDate.now().atTime(9, 45);
		assertEquals(startingTime, presentations.get(1).getStartingTime());

	}

	@Test
	void numbersMostCloseToTargetShouldBeSelected() {

		List<Presentation> unScheduledPresentations = new ArrayList<>();
		List<Presentation> scheduledPresentations = new ArrayList<>();
		Presentation first = new Presentation();
		first.setDuration(30);
		Presentation second = new Presentation();
		second.setDuration(35);
		Presentation third = new Presentation();
		third.setDuration(55);
		Presentation fourth = new Presentation();
		fourth.setDuration(70);

		unScheduledPresentations.add(first);
		unScheduledPresentations.add(second);
		unScheduledPresentations.add(third);
		unScheduledPresentations.add(fourth);

		scheduledPresentations = schedulerService.schedule(unScheduledPresentations, scheduledPresentations, 60);

		assertEquals(1, scheduledPresentations.size());
		assertEquals(55, scheduledPresentations.get(0).getDuration());
	}

	@Test
	void numbersExactSumOfTargetShouldBeSelected() {

		List<Presentation> unScheduledPresentations = new ArrayList<>();
		List<Presentation> scheduledPresentations = new ArrayList<>();
		Presentation first = new Presentation();
		first.setDuration(20);
		Presentation second = new Presentation();
		second.setDuration(45);
		Presentation third = new Presentation();
		third.setDuration(55);
		Presentation fourth = new Presentation();
		fourth.setDuration(85);

		unScheduledPresentations.add(first);
		unScheduledPresentations.add(second);
		unScheduledPresentations.add(third);
		unScheduledPresentations.add(fourth);

		scheduledPresentations = schedulerService.schedule(unScheduledPresentations, scheduledPresentations, 65);

		assertEquals(2, scheduledPresentations.size());
		assertEquals(20, scheduledPresentations.get(0).getDuration());
		assertEquals(45, scheduledPresentations.get(1).getDuration());

	}

	@Test
	void morningScheduledPresentationsShouldBeRemovedFromUnscheduledList() {

		List<Presentation> unScheduledPresentations = new ArrayList<>();
		List<Presentation> scheduledPresentations = new ArrayList<>();
		Presentation first = new Presentation();
		first.setDuration(100);
		Presentation second = new Presentation();
		second.setDuration(55);
		Presentation third = new Presentation();
		third.setDuration(80);

		unScheduledPresentations.add(first);
		unScheduledPresentations.add(second);
		unScheduledPresentations.add(third);

		schedulerService.scheduleMorningPresentations(unScheduledPresentations, scheduledPresentations, 1);

		assertEquals(second, unScheduledPresentations.get(0));

	}

	@Test
	void afternoonScheduledPresentationsShouldBeRemovedFromUnscheduledList() {

		List<Presentation> unScheduledPresentations = new ArrayList<>();
		List<Presentation> scheduledPresentations = new ArrayList<>();
		Presentation first = new Presentation();
		first.setDuration(100);
		Presentation second = new Presentation();
		second.setDuration(55);
		Presentation third = new Presentation();
		third.setDuration(140);

		unScheduledPresentations.add(first);
		unScheduledPresentations.add(second);
		unScheduledPresentations.add(third);

		schedulerService.scheduleAfternoonPresentations(unScheduledPresentations, scheduledPresentations,1);

		assertEquals(second, unScheduledPresentations.get(0));

	}
}
