package software.xdev.openrewriter.executor.request.target;

import java.util.Collection;
import java.util.Optional;

import com.intellij.openapi.project.Project;

import software.xdev.openrewriter.executor.PresentableProvider;
import software.xdev.openrewriter.executor.RecipesExecutor;


public interface ExecutionTargetProvider<T extends ExecutionTarget> extends PresentableProvider<T>
{
	default boolean isGetPreferredForCurrentStateOverridden()
	{
		try
		{
			// https://stackoverflow.com/a/2315467
			return ExecutionTargetProvider.class.equals(
				this.getClass().getMethod("getPreferredForCurrentState").getDeclaringClass());
		}
		catch(final NoSuchMethodException ex)
		{
			return true;
		}
	}
	
	@SuppressWarnings("unchecked")
	default Optional<RecipesExecutor> getPreferredForCurrentStateUnchecked(
		final Collection<RecipesExecutor> recipesExecutors,
		final ExecutionTarget target,
		final Project project)
	{
		return this.getPreferredForCurrentState(recipesExecutors, (T)target, project);
	}
	
	default Optional<RecipesExecutor> getPreferredForCurrentState(
		final Collection<RecipesExecutor> recipesExecutors,
		final T target,
		final Project project)
	{
		return Optional.empty();
	}
}
