package software.xdev.openrewriter.executor.request.target.module;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.JLabel;

import com.intellij.application.options.ModulesComboBox;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;

import software.xdev.openrewriter.executor.RecipesExecutor;
import software.xdev.openrewriter.executor.request.target.ExecutionTargetProvider;
import software.xdev.openrewriter.ui.toolwindow.execute.panels.ExecuteRecipeConfigPanel;


public class ModuleExecutionTargetProvider implements ExecutionTargetProvider<ModuleExecutionTarget>
{
	@Override
	public String name()
	{
		return "Module";
	}
	
	@Override
	public Icon icon()
	{
		return AllIcons.Nodes.Module;
	}
	
	@Override
	public int orderPriority()
	{
		return 5;
	}
	
	@Override
	public Class<ModuleExecutionTarget> matchingClass()
	{
		return ModuleExecutionTarget.class;
	}
	
	@Override
	public ModuleExecutionTarget createDefault(final Project project)
	{
		return new ModuleExecutionTarget(getModules(project).findFirst().orElse(null));
	}
	
	@Override
	public Optional<RecipesExecutor> getPreferredForCurrentState(
		final Collection<RecipesExecutor> recipesExecutors,
		final ModuleExecutionTarget target,
		final Project project)
	{
		return recipesExecutors.stream()
			.filter(r -> r.isMatchingModule(project, target.getModule()))
			.findFirst();
	}
	
	@Override
	public ModuleExecutionTargetProviderConfigPanel createConfigPanel(final Project project)
	{
		return new ModuleExecutionTargetProviderConfigPanel(project);
	}
	
	public static class ModuleExecutionTargetProviderConfigPanel extends ExecuteRecipeConfigPanel<ModuleExecutionTarget>
	{
		final ModulesComboBox cbModules = new ModulesComboBox();
		
		public ModuleExecutionTargetProviderConfigPanel(final Project project)
		{
			this.add(new JLabel("Module"));
			
			this.cbModules.allowEmptySelection("<null>");
			this.add(this.cbModules);
			
			this.cbModules.setModel(new DefaultComboBoxModel<>(getModules(project).toArray(Module[]::new)));
			
			this.cbModules.addItemListener(e ->
				this.changeValueOnlyOnSelect(e, Module.class::cast, ModuleExecutionTarget::setModule));
		}
		
		@Override
		protected void updateFrom(final ModuleExecutionTarget data)
		{
			this.cbModules.setSelectedItem(data.getModule());
		}
	}
	
	private static Stream<Module> getModules(final Project project)
	{
		return Arrays.stream(ModuleManager.getInstance(project).getModules());
	}
}
