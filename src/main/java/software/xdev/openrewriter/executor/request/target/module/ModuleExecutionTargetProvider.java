package software.xdev.openrewriter.executor.request.target.module;

import java.util.Arrays;

import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.JLabel;

import com.intellij.application.options.ModulesComboBox;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;

import software.xdev.openrewriter.executor.request.target.ExecutionTargetProvider;
import software.xdev.openrewriter.ui.toolwindow.execute.ExecuteRecipeConfigPanel;


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
	public ModuleExecutionTarget createDefault()
	{
		return new ModuleExecutionTarget();
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
			
			this.cbModules.setModel(new DefaultComboBoxModel<>(
				Arrays.stream(ModuleManager.getInstance(project).getModules())
					.toArray(Module[]::new)));
			
			this.cbModules.addItemListener(e -> this.changeValue(d -> d.setModule((Module)e.getItem())));
		}
		
		@Override
		protected void updateFrom(final ModuleExecutionTarget data)
		{
			this.cbModules.setSelectedItem(data.getModule());
		}
	}
}
