package software.xdev.openrewriter.executor.request;

import java.util.Objects;

import software.xdev.openrewriter.executor.RecipesExecutor;
import software.xdev.openrewriter.executor.request.recipedata.RecipesData;
import software.xdev.openrewriter.executor.request.target.ExecutionTarget;


public class ExecutionRequest implements CanExecute
{
	private RecipesData recipesData;
	
	private RecipesExecutor executor;
	
	private ExecutionTarget target;
	
	public ExecutionRequest(
		final RecipesData recipesData,
		final RecipesExecutor executor,
		final ExecutionTarget target)
	{
		this.setRecipesData(recipesData);
		this.setExecutor(executor);
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
	
	public RecipesExecutor getExecutor()
	{
		return this.executor;
	}
	
	public void setExecutor(final RecipesExecutor executor)
	{
		this.executor = executor;
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
		return this.getRecipesData().canExecute() && this.getExecutor() != null && this.getTarget().canExecute();
	}
}
