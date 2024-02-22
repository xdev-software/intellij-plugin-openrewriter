package software.xdev.openrewriter.ui.action;

import java.util.Optional;

import org.jetbrains.annotations.NotNull;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAwareAction;

import software.xdev.openrewriter.ui.toolwindow.ORToolWindow;


public class OpenExecuteRecipeAction extends DumbAwareAction
{
	@Override
	public void actionPerformed(@NotNull final AnActionEvent e)
	{
		Optional.ofNullable(e.getProject())
			.ifPresent(ORToolWindow::openExecuteRecipeTab);
	}
}
