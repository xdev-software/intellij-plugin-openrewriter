package software.xdev.openrewriter.ui.toolwindow;

import java.util.Optional;
import java.util.function.Consumer;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComponentContainer;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.ContentManager;

import software.xdev.openrewriter.ui.toolwindow.execute.ORExecuteRecipeToolWindowPanel;


public final class ORToolWindow
{
	public static final String TOOL_WINDOW_ID = "OpenRewriter";
	
	public static void openExecuteRecipeTab(final Project project, final Module module)
	{
		openTab(
			project,
			ORExecuteRecipeToolWindowPanel.TITLE,
			ORExecuteRecipeToolWindowPanel.class,
			p -> p.requestedWith(project, module));
	}
	
	static <T> void openTab(
		final Project project,
		final String displayName,
		final Class<T> clazz,
		final Consumer<T> tabPanelConsumer)
	{
		Optional.ofNullable(updateTab(project, displayName, clazz, tabPanelConsumer))
			.ifPresent(toolWindow -> toolWindow.show(() -> selectTab(toolWindow.getContentManager(), displayName)));
	}
	
	static <T> ToolWindow updateTab(
		final Project project,
		final String displayName,
		final Class<T> clazz,
		final Consumer<T> tabPanelConsumer)
	{
		ApplicationManager.getApplication().assertIsDispatchThread();
		
		final ToolWindow toolWindow = getToolWindow(project);
		if(tabPanelConsumer != null && toolWindow != null)
		{
			Optional.ofNullable(toolWindow.getContentManager().findContent(displayName))
				.map(ComponentContainer::getComponent)
				.filter(clazz::isInstance)
				.map(clazz::cast)
				.ifPresent(tabPanelConsumer);
		}
		return toolWindow;
	}
	
	static void selectTab(final ContentManager contentManager, final String tabId)
	{
		Optional.ofNullable(contentManager.findContent(tabId))
			.ifPresent(contentManager::setSelectedContent);
	}
	
	public static ToolWindow getToolWindow(final Project project)
	{
		return ToolWindowManager.getInstance(project).getToolWindow(TOOL_WINDOW_ID);
	}
	
	private ORToolWindow()
	{
	}
}
