package software.xdev.openrewriter.executor.request.recipedata.simpleartifact;

import java.util.HashSet;
import java.util.Set;

import software.xdev.openrewriter.executor.request.recipedata.RecipesData;


public class SimpleArtifactRecipesData implements RecipesData
{
	private final Set<Artifact> artifacts = new HashSet<>();
	
	private final Set<Recipe> recipes = new HashSet<>();
	
	public Set<Artifact> getArtifacts()
	{
		return this.artifacts;
	}
	
	public Set<Recipe> getRecipes()
	{
		return this.recipes;
	}
	
	@Override
	public boolean canExecute()
	{
		return !this.getRecipes().isEmpty();
	}
}
