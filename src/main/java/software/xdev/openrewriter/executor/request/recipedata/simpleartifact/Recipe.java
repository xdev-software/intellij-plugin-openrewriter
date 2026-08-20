package software.xdev.openrewriter.executor.request.recipedata.simpleartifact;

import java.util.Comparator;
import java.util.Objects;

import org.jetbrains.annotations.NotNull;


public class Recipe implements Comparable<Recipe>
{
	private String id;
	
	public Recipe()
	{
	}
	
	public Recipe(final String id)
	{
		this.setId(id);
	}
	
	public String getId()
	{
		return this.id;
	}
	
	public void setId(final String id)
	{
		this.id = Objects.requireNonNull(id);
	}
	
	@Override
	public boolean equals(final Object o)
	{
		if(this == o)
		{
			return true;
		}
		if(!(o instanceof final Recipe recipe1))
		{
			return false;
		}
		return Objects.equals(this.getId(), recipe1.getId());
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(this.getId());
	}
	
	@Override
	public int compareTo(@NotNull final Recipe o)
	{
		return Comparator.comparing(Recipe::getId)
			.compare(this, o);
	}
}
