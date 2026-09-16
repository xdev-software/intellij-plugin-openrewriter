package software.xdev.openrewriter.executor.request;

import java.util.Objects;

import software.xdev.openrewriter.executor.RecipesExecutorConfig;
import software.xdev.openrewriter.executor.request.recipedata.RecipesData;
import software.xdev.openrewriter.executor.request.target.ExecutionTarget;


public class ExecutionRequest implements CanExecute
{
	private RecipesData recipesData;
	
	private RecipesExecutorConfig executorConfig;
	
	private ExecutionTarget target;
	
	public ExecutionRequest(
		final RecipesData recipesData,
		final RecipesExecutorConfig executorConfig,
		final ExecutionTarget target)
	{
		this.setRecipesData(recipesData);
		this.setExecutorConfig(executorConfig);
		this.setTarget(target);
	}
	
	public RecipesData getRecipesData()
	{
		return this.recipesData;
	}
	
	public void setRecipesData(final RecipesData recipesData)
	{
		this.recipesData = Objects.requireNonNull(recipesData);
	}
	
	public RecipesExecutorConfig getExecutorConfig()
	{
		return this.executorConfig;
	}
	
	public void setExecutorConfig(final RecipesExecutorConfig executorConfig)
	{
		this.executorConfig = executorConfig;
	}
	
	public ExecutionTarget getTarget()
	{
		return this.target;
	}
	
	public void setTarget(final ExecutionTarget target)
	{
		this.target = Objects.requireNonNull(target);
	}
	
	@Override
	public boolean canExecute()
	{
		return this.getRecipesData().canExecute()
			&& this.getExecutorConfig() != null
			&& this.getTarget().canExecute();
	}
}
