package software.xdev.openrewriter.ui.toolwindow;

import javax.swing.Box;

import org.jetbrains.annotations.NotNull;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;


public class ORSimpleToolWindowPanel extends SimpleToolWindowPanel implements Disposable, DumbAware
{
	private Project project;
	private ActionToolbar toolbar;
	
	public ORSimpleToolWindowPanel(final boolean vertical, final boolean borderless)
	{
		super(vertical, borderless);
	}
	
	protected Project getProject()
	{
		return this.project;
	}
	
	public void setProject(final Project project)
	{
		this.project = project;
	}
	
	protected <T> T getService(@NotNull final Class<T> serviceClass)
	{
		return this.getProject().getService(serviceClass);
	}
	
	protected void setToolbar(final String id, final ActionGroup group)
	{
		if(this.toolbar != null)
		{
			this.toolbar.setTargetComponent(null);
			super.setToolbar(null);
			this.toolbar = null;
		}
		
		this.toolbar = ActionManager.getInstance().createActionToolbar(id, group, false);
		this.toolbar.setTargetComponent(this);
		
		final Box toolBarBox = Box.createHorizontalBox();
		toolBarBox.add(this.toolbar.getComponent());
		super.setToolbar(toolBarBox);
		
		this.toolbar.getComponent().setVisible(true);
	}
	
	protected void refreshToolbar()
	{
		this.toolbar.updateActionsAsync();
	}
	
	@Override
	public void dispose()
	{
		// NO OP
	}
}
