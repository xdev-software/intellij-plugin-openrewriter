package software.xdev.openrewriter.ui.toolwindow.execute;

import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.swing.Icon;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.util.NlsActions;


class SimpleExecuteRecipeAction extends AnAction implements DumbAware
{
	private final Supplier<Boolean> isEnabled;
	private final Consumer<AnActionEvent> onClick;
	
	public SimpleExecuteRecipeAction(
		@Nullable @NlsActions.ActionText final String text,
		final @Nullable Icon icon,
		final Supplier<Boolean> isEnabled,
		final Consumer<AnActionEvent> onClick)
	{
		super(() -> text, icon);
		this.isEnabled = isEnabled;
		this.onClick = onClick;
	}
	
	@Override
	public @NotNull ActionUpdateThread getActionUpdateThread()
	{
		return ActionUpdateThread.BGT;
	}
	
	@Override
	public void update(@NotNull final AnActionEvent e)
	{
		e.getPresentation().setEnabled(this.isEnabled.get());
	}
	
	@Override
	public void actionPerformed(@NotNull final AnActionEvent e)
	{
		this.onClick.accept(e);
	}
}
