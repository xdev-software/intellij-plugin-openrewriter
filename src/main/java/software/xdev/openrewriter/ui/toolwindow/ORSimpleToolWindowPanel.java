package software.xdev.openrewriter.ui.toolwindow;

import javax.swing.Box;
import javax.swing.JComponent;

import org.jetbrains.annotations.NotNull;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.ui.JBSplitter;


public class ORSimpleToolWindowPanel extends SimpleToolWindowPanel implements Disposable, DumbAware
{
	private Project project;
	
	private ActionToolbar toolbar;
	
	public ORSimpleToolWindowPanel(final boolean vertical)
	{
		super(vertical);
	}
	
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
	
	@SuppressWarnings("checkstyle:MagicNumber")
	protected JBSplitter createSplitter(
		final JComponent c1,
		final JComponent c2,
		final String proportionProperty)
	{
		return this.createSplitter(
			c1,
			c2,
			proportionProperty,
			0.5f);
	}
	
	protected JBSplitter createSplitter(
		final JComponent c1,
		final JComponent c2,
		final String proportionProperty,
		final float defaultSplit)
	{
		return this.createSplitter(
			this,
			this,
			c1,
			c2,
			proportionProperty,
			defaultSplit);
	}
	
	protected JBSplitter createSplitter(
		final JComponent parentComponent,
		final Disposable parentDisposable,
		final JComponent c1,
		final JComponent c2,
		final String proportionProperty,
		final float defaultSplit)
	{
		return ORToolWindowUIUtil.createSplitter(
			this::getProject,
			parentComponent,
			parentDisposable,
			c1,
			c2,
			proportionProperty,
			defaultSplit);
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
		this.toolbar.updateActionsImmediately();
	}
	
	@Override
	public void dispose()
	{
		// NO OP
	}
}
