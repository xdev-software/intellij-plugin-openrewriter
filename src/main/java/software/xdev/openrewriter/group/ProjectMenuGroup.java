package software.xdev.openrewriter.group;

import org.jetbrains.annotations.NotNull;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;


public class ProjectMenuGroup extends BaseMenuActionGroup
{
	@Override
	protected boolean isAvailable(@NotNull final AnActionEvent e)
	{
		// Menu is only available when user clicked on a module
		return e.getData(LangDataKeys.MODULE_CONTEXT) != null;
	}
}
