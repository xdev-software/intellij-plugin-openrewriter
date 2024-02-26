package software.xdev.openrewriter.ui.toolwindow.execute.panels;

import java.util.List;
import java.util.function.Supplier;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import org.jetbrains.annotations.NotNull;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;

import software.xdev.openrewriter.executor.RecipesExecutor;
import software.xdev.openrewriter.executor.request.ExecutionRequest;
import software.xdev.openrewriter.executor.request.target.ExecutionTarget;
import software.xdev.openrewriter.executor.request.target.ExecutionTargetProvider;


public class ExecuteRecipeWithAndTargetPanel extends ExecuteRecipeConfigPanel<ExecutionRequest>
{
	private final ComboBox<RecipesExecutor> cbExecuteWith = new ComboBox<>();
	
	private final PresentableProviderPanel<ExecutionTargetProvider<?>, ExecutionTarget, ExecutionRequest>
		executionTargetPanel;
	
	public ExecuteRecipeWithAndTargetPanel(final Supplier<Project> projectSupplier)
	{
		this.executionTargetPanel = new PresentableProviderPanel<>(
			projectSupplier,
			"Type",
			ExecutionRequest::getTarget,
			ExecutionRequest::setTarget);
		this.cbExecuteWith.setRenderer((list, value, index, isSelected, cellHasFocus) ->
			new JLabel(value.name(), value.icon(), SwingConstants.LEADING));
		
		this.addWithVerticalLayout(
			new JLabel("Execute with"),
			this.cbExecuteWith);
		
		this.add(new JSeparator());
		
		this.add(header("Apply to"));
		
		this.add(this.executionTargetPanel);
		
		this.cbExecuteWith.addItemListener(v -> this.changeValue(r -> r.setExecutor((RecipesExecutor)v.getItem())));
	}
	
	@Override
	public void setValueChangeCallback(final @NotNull Runnable valueChangeCallback)
	{
		super.setValueChangeCallback(valueChangeCallback);
		this.executionTargetPanel.setValueChangeCallback(valueChangeCallback);
	}
	
	public void setAvailableData(
		final List<RecipesExecutor> recipesExecutors,
		final List<ExecutionTargetProvider<?>> targetProviders)
	{
		this.cbExecuteWith.setModel(new DefaultComboBoxModel<>(recipesExecutors.toArray(RecipesExecutor[]::new)));
		
		this.executionTargetPanel.setAvailable(targetProviders);
	}
	
	@Override
	protected void updateFrom(final ExecutionRequest data)
	{
		this.cbExecuteWith.setSelectedItem(data.getExecutor());
		
		this.executionTargetPanel.updateFrom(data);
	}
	
	public void refreshTargetConfigPanel()
	{
		this.executionTargetPanel.refreshTargetConfigPanel();
	}
}
