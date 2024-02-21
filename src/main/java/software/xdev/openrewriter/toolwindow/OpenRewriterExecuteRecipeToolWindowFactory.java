package software.xdev.openrewriter.toolwindow;

import org.jetbrains.annotations.NotNull;

import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.openapi.wm.ToolWindowType;
import com.intellij.ui.content.Content;


public class OpenRewriterExecuteRecipeToolWindowFactory implements ToolWindowFactory, DumbAware
{
	@Override
	public void createToolWindowContent(@NotNull final Project project, @NotNull final ToolWindow toolWindow)
	{
		final Content toolContent = toolWindow.getContentManager().getFactory().createContent(
			new OpenRewriterExecuteRecipeToolWindowPanel(toolWindow, project),
			"Execute Recipe",
			true);
		toolWindow.getContentManager().addContent(toolContent);
		
		toolWindow.setTitle("Execute Recipe");
		toolWindow.setType(ToolWindowType.DOCKED, null);
	}
}
