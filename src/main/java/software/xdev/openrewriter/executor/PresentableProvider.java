package software.xdev.openrewriter.executor;

import javax.swing.Icon;

import com.intellij.openapi.project.Project;

import software.xdev.openrewriter.ui.toolwindow.execute.panels.ExecuteRecipeConfigPanel;


public interface PresentableProvider<T> extends Provider
{
	Icon icon();
	
	T createDefault(Project project);
	
	Class<T> matchingClass();
	
	ExecuteRecipeConfigPanel<? extends T> createConfigPanel(final Project project);
}
