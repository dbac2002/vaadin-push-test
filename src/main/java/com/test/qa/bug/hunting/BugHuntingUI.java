package com.test.qa.bug.hunting;

import java.util.EnumMap;

import javax.servlet.annotation.WebServlet;

import com.test.qa.bug.hunting.Broadcaster.BroadCastEvent;
import com.test.qa.bug.hunting.Broadcaster.BroadcastListener;
import com.test.qa.bug.hunting.BugHuntingTask.Status;
import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.annotations.Widgetset;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.event.dd.acceptcriteria.ServerSideCriterion;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.DragAndDropWrapper;
import com.vaadin.ui.DragAndDropWrapper.WrapperTransferable;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HasComponents;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

/**
 * This UI is the application entry point. A UI may either represent a browser
 * window (or tab) or some part of a html page where a Vaadin application is
 * embedded.
 * <p>
 * The UI is initialized using {@link #init(VaadinRequest)}. This method is
 * intended to be overridden to add component to the user interface and
 * initialize non-component functionality.
 */
@Theme("bughuntingtheme")
@Widgetset("com.test.qa.bug.hunting.BugHuntingWidgetset")
@Push
public class BugHuntingUI extends UI implements BroadcastListener {
	private final EnumMap<Status, Panel> statusPanels = new EnumMap<>(Status.class);

	@Override
	protected void init(VaadinRequest vaadinRequest) {
		VerticalLayout mainLayout = new VerticalLayout();
		mainLayout.setWidth("100%");
		mainLayout.setMargin(true);
		Label headLine = new Label("BugHunting Event");
		headLine.addStyleName(ValoTheme.LABEL_H1);
		mainLayout.addComponent(headLine);

		GridLayout layout = new GridLayout(3, 1);
		layout.setWidth("100%");
		layout.setSpacing(true);

		layout.addComponent(createTaskPanel(Status.OPEN));
		layout.addComponent(createTaskPanel(Status.IN_PROGRESS));
		layout.addComponent(createTaskPanel(Status.DONE));

		mainLayout.addComponent(layout);

		setContent(mainLayout);

		Broadcaster.register(this);
	}

	@Override
	public void detach() {
		Broadcaster.unregister(this);
		super.detach();
	}

	private Panel createTaskPanel(Status status) {
		Panel tasks = new Panel();
		tasks.setHeight("500px");

		VerticalLayout taskLayout = new VerticalLayout();
		taskLayout.setSpacing(true);
		taskLayout.setMargin(true);
		BugHuntingTaskContainer.get().getTasks().filter(task -> task.getStatus() == status).map(BugHuntingTaskComponent::new)
				.map(BugHuntingTaskComponent::getDragAndDrop).forEach(taskLayout::addComponent);

		DragAndDropWrapper wrapper = new DragAndDropWrapper(taskLayout);
		wrapper.setDropHandler(new DropHandlerImplementation(status, taskLayout));
		tasks.setContent(wrapper);
		tasks.setCaption(getStatusCaption(status, taskLayout));

		statusPanels.put(status, tasks);

		return tasks;
	}

	private String getStatusCaption(Status status, ComponentContainer container) {
		return status.getCaption() + " (" + container.getComponentCount() + ")";
	}

	private final class DropHandlerImplementation implements DropHandler {
		private final Status status;
		private final VerticalLayout taskLayout;

		private DropHandlerImplementation(Status status, VerticalLayout taskLayout) {
			this.status = status;
			this.taskLayout = taskLayout;
		}

		@Override
		public AcceptCriterion getAcceptCriterion() {
			return new ServerSideCriterion() {
				@Override
				public boolean accept(DragAndDropEvent dragEvent) {
					BugHuntingTaskComponent sourceComponent = getTaskComponent(dragEvent);
					return sourceComponent != null ? sourceComponent.canBeChanged(status) : false;
				}
			};
		}

		@Override
		public void drop(DragAndDropEvent event) {
			BugHuntingTaskComponent bugHuntingTaskComponent = getTaskComponent(event);
			Status oldStatus = bugHuntingTaskComponent.getStatus();
			bugHuntingTaskComponent.changeStatus(status);
			Layout sourceLayout = (Layout) event.getTransferable().getSourceComponent().getParent();

			BroadCastEvent e = BroadCastEvent.of(bugHuntingTaskComponent, sourceLayout, taskLayout, oldStatus, status);

			Broadcaster.broadcast(e);
		}

		private BugHuntingTaskComponent getTaskComponent(DragAndDropEvent event) {
			WrapperTransferable transferable = (WrapperTransferable) event.getTransferable();
			HasComponents draggedComponent = transferable.getDraggedComponent().getParent();
			if (draggedComponent == null) {
				return null;
			}
			while (draggedComponent.getClass() != BugHuntingTaskComponent.class) {
				draggedComponent = draggedComponent.getParent();
				if (draggedComponent == null) {
					return null;
				}
			}
			return (BugHuntingTaskComponent) draggedComponent;
		}
	}

	@WebServlet(urlPatterns = "/*", name = "BugHuntingUIServlet", asyncSupported = true)
	@VaadinServletConfiguration(ui = BugHuntingUI.class, productionMode = false)
	public static class BugHuntingUIServlet extends VaadinServlet {
	}

	@Override
	public void receiveBroadcast(BroadCastEvent message) {
		access(() -> {
			message.sourceLayout.removeComponent(message.bugHuntingTaskComponent.getDragAndDrop());
			message.taskLayout.addComponent(message.bugHuntingTaskComponent.getDragAndDrop());
			statusPanels.get(message.oldStatus).setCaption(getStatusCaption(message.oldStatus, message.sourceLayout));
			statusPanels.get(message.status).setCaption(getStatusCaption(message.status, message.taskLayout));
		});
	}
}
