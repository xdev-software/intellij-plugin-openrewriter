package software.xdev.openrewriter.executor;

import javax.swing.Icon;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;

import software.xdev.openrewriter.executor.request.ExecutionRequest;


public interface RecipesExecutor extends Provider
{
	Icon icon();
	
	default boolean isAsync()
	{
		return true;
	}
	
	boolean isMatchingModule(Project project, Module module);
	
	void execute(Project project, ExecutionRequest request);
}
