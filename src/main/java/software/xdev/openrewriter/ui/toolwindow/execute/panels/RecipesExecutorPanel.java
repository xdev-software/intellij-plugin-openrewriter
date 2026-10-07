package software.xdev.openrewriter.ui.toolwindow.execute.panels;

import java.awt.Component;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.SwingConstants;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;

import software.xdev.openrewriter.executor.RecipesExecutor;
import software.xdev.openrewriter.executor.RecipesExecutorConfig;
import software.xdev.openrewriter.executor.request.ExecutionRequest;


public class RecipesExecutorPanel
	extends PresentableProviderPanel<RecipesExecutor<?>, RecipesExecutorConfig, ExecutionRequest>
{
	public RecipesExecutorPanel(
		final Supplier<Project> getProjectSupplier,
		final String lblText,
		final Function<ExecutionRequest, RecipesExecutorConfig> getFromRootFunc,
		final BiConsumer<ExecutionRequest, RecipesExecutorConfig> setIntoRootFunc)
	{
		super(getProjectSupplier, lblText, getFromRootFunc, setIntoRootFunc);
	}
	
	@Override
	protected Component cbProviderRenderer(
		final JList<? extends RecipesExecutor<?>> list,
		final RecipesExecutor<?> value,
		final int index,
		final boolean isSelected,
		final boolean cellHasFocus)
	{
		return new JLabel(
			value != null ? value.name() : "<No execution provider available>",
			value != null ? value.icon() : AllIcons.General.Warning,
			SwingConstants.LEADING);
	}
}
