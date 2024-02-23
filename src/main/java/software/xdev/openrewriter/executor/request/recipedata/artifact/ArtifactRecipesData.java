package software.xdev.openrewriter.executor.request.recipedata.artifact;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import software.xdev.openrewriter.executor.request.recipedata.RecipesData;


public class ArtifactRecipesData implements RecipesData
{
	private Set<Artifact> artifacts = new HashSet<>();
	
	private Set<Recipe> recipes = new HashSet<>();
	
	public Set<Artifact> getArtifacts()
	{
		return this.artifacts;
	}
	
	public void setArtifacts(final Set<Artifact> artifacts)
	{
		this.artifacts = Objects.requireNonNull(artifacts);
	}
	
	public Set<Recipe> getRecipes()
	{
		return this.recipes;
	}
	
	public void setRecipes(final Set<Recipe> recipes)
	{
		this.recipes = Objects.requireNonNull(recipes);
	}
	
	@Override
	public boolean canExecute()
	{
		return !this.getArtifacts().isEmpty() && !this.getRecipes().isEmpty();
	}
}
