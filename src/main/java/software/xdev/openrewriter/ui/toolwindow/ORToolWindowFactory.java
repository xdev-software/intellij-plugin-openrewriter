package software.xdev.openrewriter.ui.toolwindow;

import javax.swing.JComponent;

import org.jetbrains.annotations.NotNull;

import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.openapi.wm.ToolWindowType;
import com.intellij.ui.content.ContentManager;

import software.xdev.openrewriter.ui.toolwindow.execute.ORExecuteRecipeToolWindowPanel;


public class ORToolWindowFactory implements ToolWindowFactory, DumbAware
{
	@Override
	public void createToolWindowContent(@NotNull final Project project, @NotNull final ToolWindow toolWindow)
	{
		this.addExecuteRecipeTab(project, toolWindow.getContentManager());
		
		toolWindow.setType(ToolWindowType.DOCKED, null);
	}
	
	protected void addExecuteRecipeTab(final Project project, final ContentManager contentManager)
	{
		this.addTab(
			contentManager,
			new ORExecuteRecipeToolWindowPanel(project),
			ORExecuteRecipeToolWindowPanel.TITLE);
	}
	
	protected void addTab(
		final ContentManager contentManager,
		final JComponent component,
		@NlsContexts.TabTitle final String displayName)
	{
		contentManager.addContent(contentManager.getFactory().createContent(
			component,
			displayName,
			false));
	}
}
