package software.xdev.openrewriter.ui;

import java.util.Optional;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;


@Service(Service.Level.PROJECT)
public final class NotificationService
{
	private final Project project;
	
	public NotificationService(final Project project)
	{
		this.project = project;
	}
	
	public Builder builder()
	{
		return new Builder(this);
	}
	
	void show(
		final GroupId groupId,
		final String title,
		final String content,
		final NotificationType type
	)
	{
		Notifications.Bus.notify(
			new Notification(groupId.groupId(), title, content, type),
			this.project);
	}
	
	public enum GroupId
	{
		REWRITE_SUCCESS("Rewrite successful"),
		REWRITE_FAILED("Rewrite failed"),
		
		DEFAULT(null);
		
		public static final String BASE = "OpenRewriter";
		
		private final String additionalGroupIdContent;
		
		GroupId(final String additionalGroupIdContent)
		{
			this.additionalGroupIdContent = additionalGroupIdContent;
		}
		
		public String groupId()
		{
			return Optional.ofNullable(this.additionalGroupIdContent)
				.map(c -> BASE + ": " + c)
				.orElse(BASE);
		}
	}
	
	
	public static class Builder
	{
		private final NotificationService notificationService;
		
		private GroupId groupId = GroupId.DEFAULT;
		private String title = "";
		private String content = "";
		private NotificationType type = NotificationType.INFORMATION;
		
		public Builder(final NotificationService notificationService)
		{
			this.notificationService = notificationService;
		}
		
		public Builder withGroupId(final GroupId groupId)
		{
			this.groupId = groupId;
			return this;
		}
		
		public Builder withTitle(final String title)
		{
			this.title = title;
			return this;
		}
		
		public Builder withContent(final String content)
		{
			this.content = content;
			return this;
		}
		
		public Builder withType(final NotificationType type)
		{
			this.type = type;
			return this;
		}
		
		public void show()
		{
			this.notificationService.show(this.groupId, this.title, this.content, this.type);
		}
	}
}
