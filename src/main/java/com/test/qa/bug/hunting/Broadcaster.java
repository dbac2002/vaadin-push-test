package com.test.qa.bug.hunting;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.test.qa.bug.hunting.BugHuntingTask.Status;
import com.vaadin.ui.Layout;
import com.vaadin.ui.VerticalLayout;

public class Broadcaster implements Serializable {

	private static final long serialVersionUID = 3540459607283346649L;

	static ExecutorService executorService = Executors.newSingleThreadExecutor();

	private static LinkedList<BroadcastListener> listeners = new LinkedList<BroadcastListener>();

	public interface BroadcastListener {
		void receiveBroadcast(BroadCastEvent message);
	}

	public static synchronized void register(BroadcastListener listener) {
		listeners.add(listener);
	}

	public static synchronized void unregister(BroadcastListener listener) {
		listeners.remove(listener);
	}

	public static synchronized void broadcast(BroadCastEvent broadCoastEvent) {
		for (final BroadcastListener listener : listeners) {
			executorService.execute(() -> listener.receiveBroadcast(broadCoastEvent));
		}
	}

	public static final class BroadCastEvent {
		final BugHuntingTaskComponent bugHuntingTaskComponent;
		final Layout sourceLayout;
		final VerticalLayout taskLayout;
		final Status oldStatus;
		final Status status;

		public BroadCastEvent(BugHuntingTaskComponent bugHuntingTaskComponent, Layout sourceLayout, VerticalLayout taskLayout, Status oldStatus,
				Status status) {
			this.bugHuntingTaskComponent = bugHuntingTaskComponent;
			this.sourceLayout = sourceLayout;
			this.taskLayout = taskLayout;
			this.oldStatus = oldStatus;
			this.status = status;
		}

		public static BroadCastEvent of(BugHuntingTaskComponent bugHuntingTaskComponent, Layout sourceLayout, VerticalLayout taskLayout,
				Status oldStatus, Status status) {
			return new BroadCastEvent(bugHuntingTaskComponent, sourceLayout, taskLayout, oldStatus, status);
		}

	}
}
