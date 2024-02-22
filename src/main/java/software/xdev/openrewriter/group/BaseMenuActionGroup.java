package software.xdev.openrewriter.group;

import org.jetbrains.annotations.NotNull;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.DumbAware;


public abstract class BaseMenuActionGroup extends DefaultActionGroup implements DumbAware
{
	@Override
	public void update(@NotNull final AnActionEvent e)
	{
		super.update(e);
		e.getPresentation().setEnabledAndVisible(this.isAvailable(e));
	}
	
	@Override
	public @NotNull ActionUpdateThread getActionUpdateThread()
	{
		return ActionUpdateThread.BGT;
	}
	
	protected abstract boolean isAvailable(@NotNull final AnActionEvent e);
}
