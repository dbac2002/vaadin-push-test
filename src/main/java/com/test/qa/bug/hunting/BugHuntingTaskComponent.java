package com.test.qa.bug.hunting;

import com.test.qa.bug.hunting.BugHuntingTask.Status;
import com.vaadin.server.ExternalResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.DragAndDropWrapper;
import com.vaadin.ui.DragAndDropWrapper.DragStartMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

public class BugHuntingTaskComponent extends Panel {
	private final DragAndDropWrapper dragAndDropWrapper;
	private final BugHuntingTask task;
	private final VerticalLayout mainLayout;
	private HorizontalLayout defectLayout;

	public BugHuntingTaskComponent(BugHuntingTask task) {
		this.task = task;
		mainLayout = new VerticalLayout();
		mainLayout.setMargin(true);

		HorizontalLayout layout = new HorizontalLayout();
		layout.setSizeFull();

		Label title = new Label(task.getTitle());
		layout.addComponent(title);

		Link link = jiraLink(task.getWorkTicket());
		layout.addComponent(link);

		layout.setExpandRatio(title, 8);
		layout.setExpandRatio(link, 2);

		mainLayout.addComponent(layout);

		setContent(mainLayout);

		this.dragAndDropWrapper = new DragAndDropWrapper(this);
		this.dragAndDropWrapper.setDragStartMode(DragStartMode.COMPONENT);
	}

	private Link jiraLink(String ticket) {
		Link link = new Link(ticket, new ExternalResource("https://browse/" + ticket));
		link.setTargetName("_blank");
		return link;
	}

	private void addDefects() {
		if (defectLayout != null) {
			mainLayout.removeComponent(defectLayout);
		}
		if (task.getDefects().size() > 0) {
			defectLayout = new HorizontalLayout();
			defectLayout.addComponent(new Label("Defects: "));
			defectLayout.setSpacing(true);
			task.getDefects().stream().map(this::jiraLink).forEach(defectLayout::addComponent);
			mainLayout.addComponent(defectLayout);
			mainLayout.setComponentAlignment(defectLayout, Alignment.BOTTOM_LEFT);
		}
	}

	public DragAndDropWrapper getDragAndDrop() {
		return dragAndDropWrapper;
	}

	public void changeStatus(Status status) {
		task.setStatus(status);

		if (status == Status.DONE) {
			addDefects();
		}
	}

	public boolean canBeChanged(Status newStatus) {
		switch (task.getStatus()) {
		case OPEN:
			return newStatus != Status.OPEN;
		case IN_PROGRESS:
			return newStatus == Status.DONE;
		case DONE:
			return false;
		}

		return false;
	}

	public Status getStatus() {
		return task.getStatus();
	}
}
