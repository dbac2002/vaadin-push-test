package com.test.qa.bug.hunting;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class BugHuntingTaskContainer {
	private static final BugHuntingTaskContainer INSTANCE = new BugHuntingTaskContainer();

	private final List<BugHuntingTask> tasks = new ArrayList<>();

	private BugHuntingTaskContainer() {
	}

	public void addTasks(BugHuntingTask task) {
		tasks.add(task);
	}

	public Stream<BugHuntingTask> getTasks() {
		return tasks.stream();
	}

	public static BugHuntingTaskContainer get() {
		if (INSTANCE.tasks.isEmpty()) {
			INSTANCE.addTasks(task("Something is wrong 1", "QQ-2341"));
			INSTANCE.addTasks(task("Something is wrong 2", "QQ-2342"));
			INSTANCE.addTasks(task("Something is wrong 3", "QQ-2343"));
			INSTANCE.addTasks(task("Something is wrong 4", "QQ-2344"));
			INSTANCE.addTasks(task("Something is wrong 5", "QQ-2345"));
			INSTANCE.addTasks(task("Something is wrong 6", "QQ-2346"));
			INSTANCE.addTasks(task("Something is wrong 7", "QQ-2347"));
			INSTANCE.addTasks(task("Something is wrong 8", "QQ-2348"));
			INSTANCE.addTasks(task("Something is wrong 9", "QQ-2349"));
		}
		return INSTANCE;
	}

	private static BugHuntingTask task(String title, String ticket) {
		BugHuntingTask bugHuntingTask = new BugHuntingTask();
		bugHuntingTask.setTitle(title);
		bugHuntingTask.setWorkTicket(ticket);
		return bugHuntingTask;
	}
}
