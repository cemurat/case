package com.n11.project.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Entity
@Table(name = "presentation")
public class Presentation {

	@Id
	@GeneratedValue
	private Long id;

	@Column(unique = true)
	private String subject;

	@Max(240)
	@Min(5)
	private int duration;

	private int track_no;

	private LocalDateTime starting_time;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public int getTrackNo() {
		return track_no;
	}

	public void setTrackNo(int track_no) {
		this.track_no = track_no;
	}

	public LocalDateTime getStartingTime() {
		return starting_time;
	}

	public void setStartingTime(LocalDateTime starting_time) {
		this.starting_time = starting_time;
	}

	@Override
	public String toString() {
		return "Presentation {id:" + id + ", subject:" + subject + ", duration:" + duration + ", trackNo:" + track_no
				+ ", startingTime:" + starting_time + "}";
	}

}
