package software.xdev.openrewriter.group;

import org.jetbrains.annotations.NotNull;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;


public class OpenRewriterToolsMenuGroup extends OpenRewriterBaseMenuActionGroup
{
	@Override
	protected boolean isAvailable(@NotNull final AnActionEvent e)
	{
		// Only available if a project exists
		return e.getData(CommonDataKeys.PROJECT) != null;
	}
}
