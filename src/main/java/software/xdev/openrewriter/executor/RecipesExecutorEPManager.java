package software.xdev.openrewriter.executor;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import com.intellij.openapi.extensions.ExtensionPointName;

import software.xdev.openrewriter.executor.request.target.ExecutionTargetProvider;


public final class RecipesExecutorEPManager
{
	private static final ExtensionPointName<RecipesExecutor> EP_RECIPE_EXECUTORS =
		ExtensionPointName.create("software.xdev.openrewriter.recipeExecutor");
	
	private static final ExtensionPointName<ExecutionTargetProvider<?>> EP_RECIPE_EXECUTION_REQUEST_TARGET_PROVIDERS =
		ExtensionPointName.create("software.xdev.openrewriter.recipeExecutionRequestTargetProvider");
	
	public static List<RecipesExecutor> executors()
	{
		return order(EP_RECIPE_EXECUTORS.getExtensionList());
	}
	
	@SuppressWarnings("java:S1452")
	public static List<ExecutionTargetProvider<?>> executionTargetProviders()
	{
		return order(EP_RECIPE_EXECUTION_REQUEST_TARGET_PROVIDERS.getExtensionList());
	}
	
	private static <T extends Provider> List<T> order(final Collection<T> input)
	{
		return input.stream()
			.sorted(Comparator.comparing(Provider::orderPriority))
			.toList();
	}
	
	private RecipesExecutorEPManager()
	{
	}
}
