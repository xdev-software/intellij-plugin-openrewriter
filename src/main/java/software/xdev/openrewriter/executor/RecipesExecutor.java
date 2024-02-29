package software.xdev.openrewriter.executor;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;

import software.xdev.openrewriter.executor.request.ExecutionRequest;


public interface RecipesExecutor<T extends RecipesExecutorConfig> extends PresentableProvider<T>
{
	default boolean isAsync()
	{
		return true;
	}
	
	boolean isMatchingModule(Project project, Module module);
	
	@SuppressWarnings("unchecked")
	default void execute(final Project project, final ExecutionRequest request)
	{
		this.execute(project, request, (T)request.getExecutorConfig());
	}
	
	void execute(Project project, ExecutionRequest request, T config);
}
