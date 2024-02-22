package software.xdev.openrewriter.ui.group;

import org.jetbrains.annotations.NotNull;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;


public class ToolsMenuGroup extends BaseMenuActionGroup
{
	@Override
	protected boolean isAvailable(@NotNull final AnActionEvent e)
	{
		// Only available if a project exists
		return e.getData(CommonDataKeys.PROJECT) != null;
	}
}
