package software.xdev.openrewriter.ui.toolwindow.execute.panels;

import java.util.List;
import java.util.function.Supplier;

import javax.swing.JSeparator;

import org.jetbrains.annotations.NotNull;

import com.intellij.openapi.project.Project;

import software.xdev.openrewriter.executor.RecipesExecutor;
import software.xdev.openrewriter.executor.request.ExecutionRequest;
import software.xdev.openrewriter.executor.request.target.ExecutionTargetProvider;


public class ExecuteRecipeWithAndTargetPanel extends ExecuteRecipeConfigPanel<ExecutionRequest>
{
	private final RecipesExecutorPanel recipesExecutorPanel;
	
	private final ExecutionTargetProviderPanel executionTargetPanel;
	
	public ExecuteRecipeWithAndTargetPanel(final Supplier<Project> projectSupplier)
	{
		this.recipesExecutorPanel = new RecipesExecutorPanel(
			projectSupplier,
			"Execute with",
			ExecutionRequest::getExecutorConfig,
			ExecutionRequest::setExecutorConfig);
		
		this.executionTargetPanel = new ExecutionTargetProviderPanel(
			projectSupplier,
			"Type",
			ExecutionRequest::getTarget,
			ExecutionRequest::setTarget,
			() -> this.recipesExecutorPanel.getAvailable().stream().toList(),
			// Automatically select execution type based on selected project
			// Only run when data is available (so we are not loading stuff which might result in invalid state)
			e -> this.ifData(d -> this.recipesExecutorPanel.select(e)));
		
		this.add(this.recipesExecutorPanel);
		this.add(new JSeparator());
		this.add(header("Apply to"));
		this.add(this.executionTargetPanel);
	}
	
	@Override
	public void setValueChangeCallback(final @NotNull Runnable valueChangeCallback)
	{
		super.setValueChangeCallback(valueChangeCallback);
		this.recipesExecutorPanel.setValueChangeCallback(valueChangeCallback);
		this.executionTargetPanel.setValueChangeCallback(valueChangeCallback);
	}
	
	public void setAvailableData(
		final List<RecipesExecutor<?>> recipesExecutors,
		final List<ExecutionTargetProvider<?>> targetProviders)
	{
		this.recipesExecutorPanel.setAvailable(recipesExecutors);
		this.executionTargetPanel.setAvailable(targetProviders);
	}
	
	@Override
	protected void updateFrom(final ExecutionRequest data)
	{
		this.recipesExecutorPanel.updateFromAndBind(data);
		this.executionTargetPanel.updateFromAndBind(data);
	}
	
	@Override
	protected void afterUpdateFromAndBind(final ExecutionRequest data)
	{
		this.executionTargetPanel.checkForPreferredRecipesExecutor();
	}
	
	public void refreshTargetConfigPanel()
	{
		this.executionTargetPanel.refreshTargetConfigPanel();
	}
}
