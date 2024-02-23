package software.xdev.openrewriter.executor.request.target;

import javax.swing.Icon;

import com.intellij.openapi.project.Project;

import software.xdev.openrewriter.executor.Provider;
import software.xdev.openrewriter.ui.toolwindow.execute.ExecuteRecipeConfigPanel;


public interface ExecutionTargetProvider<T extends ExecutionTarget> extends Provider
{
	Icon icon();
	
	T createDefault();
	
	Class<T> matchingClass();
	
	ExecuteRecipeConfigPanel<? extends ExecutionTarget> createConfigPanel(final Project project);
}
