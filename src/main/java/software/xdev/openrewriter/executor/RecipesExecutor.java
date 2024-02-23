package software.xdev.openrewriter.executor;

import javax.swing.Icon;

import com.intellij.openapi.project.Project;

import software.xdev.openrewriter.executor.request.ExecutionRequest;


public interface RecipesExecutor extends Provider
{
	Icon icon();
	
	default boolean isAsync()
	{
		return true;
	}
	
	void execute(Project project, ExecutionRequest request);
}
