package software.xdev.openrewriter.executor;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import com.intellij.openapi.extensions.ExtensionPointName;

import software.xdev.openrewriter.executor.request.recipedata.RecipesDataProvider;
import software.xdev.openrewriter.executor.request.target.ExecutionTargetProvider;


public final class RecipesExecutorEPManager
{
	private static final ExtensionPointName<RecipesExecutor<?>> EP_RECIPES_EXECUTORS =
		ExtensionPointName.create("software.xdev.openrewriter.recipesExecutor");
	
	private static final ExtensionPointName<ExecutionTargetProvider<?>> EP_EXECUTION_REQUEST_TARGET_PROVIDERS =
		ExtensionPointName.create("software.xdev.openrewriter.executionRequestTargetProvider");
	
	private static final ExtensionPointName<RecipesDataProvider<?>> EP_RECIPES_DATA_PROVIDERS =
		ExtensionPointName.create("software.xdev.openrewriter.recipesDataProvider");
	
	public static List<RecipesExecutor<?>> recipesExecutors()
	{
		return order(EP_RECIPES_EXECUTORS.getExtensionList());
	}
	
	public static List<ExecutionTargetProvider<?>> executionRequestTargetProviders()
	{
		return order(EP_EXECUTION_REQUEST_TARGET_PROVIDERS.getExtensionList());
	}
	
	@SuppressWarnings("java:S1452")
	public static List<RecipesDataProvider<?>> recipesDataProviders()
	{
		return order(EP_RECIPES_DATA_PROVIDERS.getExtensionList());
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
