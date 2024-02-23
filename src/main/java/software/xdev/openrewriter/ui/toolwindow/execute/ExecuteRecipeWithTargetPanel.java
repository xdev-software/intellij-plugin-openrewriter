package software.xdev.openrewriter.ui.toolwindow.execute;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.VerticalFlowLayout;

import software.xdev.openrewriter.executor.RecipesExecutor;
import software.xdev.openrewriter.executor.request.ExecutionRequest;
import software.xdev.openrewriter.executor.request.target.ExecutionTarget;
import software.xdev.openrewriter.executor.request.target.ExecutionTargetProvider;


@SuppressWarnings({"unchecked", "rawtypes"})
class ExecuteRecipeWithTargetPanel extends ExecuteRecipeConfigPanel<ExecutionRequest>
{
	// Components
	private final ComboBox<RecipesExecutor> cbExecuteWith = new ComboBox<>();
	
	private final ComboBox<ExecutionTargetProvider> cbTarget = new ComboBox<>();
	
	private final JPanel targetConfigContainerPanel = new JPanel();
	private ExecuteRecipeConfigPanel<ExecutionTarget> targetConfigPanel;
	
	// State
	private Project project;
	
	private final Map<Class<? extends ExecutionTarget>, ExecutionTargetProvider<?>> executionTargetProviders =
		new HashMap<>();
	
	public ExecuteRecipeWithTargetPanel()
	{
		this.add(new JLabel("Execute with"));
		
		this.cbExecuteWith.setRenderer((list, value, index, isSelected, cellHasFocus) ->
			new JLabel(value.name(), value.icon(), SwingConstants.LEADING));
		this.add(this.cbExecuteWith);
		
		this.add(new JSeparator());
		
		this.add(header("Apply to"));
		
		this.add(new JLabel("Type"));
		
		this.cbTarget.setRenderer((list, value, index, isSelected, cellHasFocus) ->
			new JLabel(value.name(), value.icon(), SwingConstants.LEADING));
		this.add(this.cbTarget);
		
		this.targetConfigContainerPanel.setLayout(new VerticalFlowLayout());
		this.add(this.targetConfigContainerPanel);
		
		this.cbExecuteWith.addItemListener(v -> this.changeValue(r -> r.setExecutor((RecipesExecutor)v.getItem())));
		this.cbTarget.addItemListener(v -> this.changeValue(r -> {
			final ExecutionTargetProvider provider = (ExecutionTargetProvider)v.getItem();
			
			this.createNewTargetConfigPanel(provider);
			
			final ExecutionTarget newExecutionTarget = provider.createDefault();
			r.setTarget(newExecutionTarget);
			this.targetConfigPanel.updateFromAndBind(newExecutionTarget);
		}));
	}
	
	public void setAvailableData(
		final List<RecipesExecutor> recipesExecutors,
		final List<ExecutionTargetProvider<?>> targetProviders)
	{
		this.cbExecuteWith.setModel(new DefaultComboBoxModel<>(recipesExecutors.toArray(RecipesExecutor[]::new)));
		
		this.executionTargetProviders.clear();
		this.executionTargetProviders.putAll(
			targetProviders.stream()
				.collect(Collectors.toMap(ExecutionTargetProvider::matchingClass, Function.identity())));
		
		this.cbTarget.setModel(new DefaultComboBoxModel<>(targetProviders.toArray(ExecutionTargetProvider[]::new)));
	}
	
	protected void createNewTargetConfigPanel(final ExecutionTargetProvider provider)
	{
		this.targetConfigContainerPanel.removeAll();
		
		// Dispose old panel to free up resources if possible
		if(this.targetConfigPanel != null)
		{
			this.targetConfigPanel.dispose();
		}
		
		// Create a new panel
		this.targetConfigPanel = provider.createConfigPanel(this.project);
		this.targetConfigPanel.setValueChangeCallback(this.getValueChangeCallback());
		
		this.targetConfigContainerPanel.add(this.targetConfigPanel);
	}
	
	@Override
	protected void updateFrom(final ExecutionRequest data)
	{
		this.cbExecuteWith.setSelectedItem(data.getExecutor());
		
		final ExecutionTargetProvider<?> executionTargetProvider =
			this.executionTargetProviders.get(data.getTarget().getClass());
		this.cbTarget.setSelectedItem(executionTargetProvider);
		this.createNewTargetConfigPanel(executionTargetProvider);
		this.targetConfigPanel.updateFromAndBind(data.getTarget());
	}
	
	public void setProject(final Project project)
	{
		this.project = project;
	}
	
	public void refreshTargetConfigPanel()
	{
		this.targetConfigPanel.ifData(this.targetConfigPanel::updateFrom);
	}
}
