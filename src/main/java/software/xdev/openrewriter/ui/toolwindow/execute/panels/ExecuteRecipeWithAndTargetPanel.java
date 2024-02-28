package software.xdev.openrewriter.ui.toolwindow.execute.panels;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import org.jetbrains.annotations.NotNull;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;

import software.xdev.openrewriter.executor.RecipesExecutor;
import software.xdev.openrewriter.executor.request.ExecutionRequest;
import software.xdev.openrewriter.executor.request.target.ExecutionTargetProvider;


public class ExecuteRecipeWithAndTargetPanel extends ExecuteRecipeConfigPanel<ExecutionRequest>
{
	private final ComboBox<RecipesExecutor> cbExecuteWith = new ComboBox<>();
	
	private final ExecutionTargetProviderPanel executionTargetPanel;
	
	public ExecuteRecipeWithAndTargetPanel(final Supplier<Project> projectSupplier)
	{
		this.executionTargetPanel = new ExecutionTargetProviderPanel(
			projectSupplier,
			"Type",
			ExecutionRequest::getTarget,
			ExecutionRequest::setTarget,
			() -> IntStream.range(0, this.cbExecuteWith.getModel().getSize())
				.mapToObj(i -> this.cbExecuteWith.getModel().getElementAt(i))
				.toList(),
			// Automatically select execution type based on selected project
			// Only run when data is available (so we are not loading stuff which might result in invalid state)
			e -> this.ifData(d -> this.cbExecuteWith.setSelectedItem(e)));
		
		this.cbExecuteWith.setRenderer((list, value, index, isSelected, cellHasFocus) ->
			new JLabel(
				value != null ? value.name() : "<No execution provider available>",
				value != null ? value.icon() : AllIcons.General.Warning,
				SwingConstants.LEADING));
		
		this.addWithVerticalLayout(
			new JLabel("Execute with"),
			this.cbExecuteWith);
		
		this.add(new JSeparator());
		
		this.add(header("Apply to"));
		
		this.add(this.executionTargetPanel);
		
		this.cbExecuteWith.addItemListener(e ->
			this.changeValueOnlyOnSelect(e, RecipesExecutor.class::cast, ExecutionRequest::setExecutor));
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