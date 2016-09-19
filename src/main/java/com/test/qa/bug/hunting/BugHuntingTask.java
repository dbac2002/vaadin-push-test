package com.test.qa.bug.hunting;

import java.util.ArrayList;
import java.util.List;

public class BugHuntingTask {
	private String workTicket;
	private String title;
	private Status status;
	private List<String> defects;

	public BugHuntingTask() {
		setStatus(Status.OPEN);
		setDefects(new ArrayList<>());
	}

	public String getWorkTicket() {
		return workTicket;
	}

	public void setWorkTicket(String workTicket) {
		this.workTicket = workTicket;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public List<String> getDefects() {
		return defects;
	}

	public void setDefects(List<String> defects) {
		this.defects = defects;
	}

	public enum Status {
		OPEN("Available tasks"), IN_PROGRESS("Tasks in progress"), DONE("Finished tasks");

		private final String caption;

		private Status(String caption) {
			this.caption = caption;
		}

		public String getCaption() {
			return caption;
		}

	}
}
