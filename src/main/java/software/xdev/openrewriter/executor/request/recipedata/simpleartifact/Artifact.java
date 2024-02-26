package software.xdev.openrewriter.executor.request.recipedata.simpleartifact;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class Artifact implements Comparable<Artifact>
{
	public static final String DEFAULT_GROUP_ID = "org.openrewrite.recipe";
	public static final String DEFAULT_VERSION = "LATEST";
	
	private String groupId;
	private String artifactId;
	private String version;
	
	public Artifact()
	{
	}
	
	public Artifact(final String groupId, final String artifactId, final String version)
	{
		this.setGroupId(groupId);
		this.setArtifactId(artifactId);
		this.setVersion(version);
	}
	
	public String getGroupId()
	{
		return this.groupId;
	}
	
	public void setGroupId(final String groupId)
	{
		this.groupId = Objects.requireNonNull(groupId);
	}
	
	public String getArtifactId()
	{
		return this.artifactId;
	}
	
	public void setArtifactId(final String artifactId)
	{
		this.artifactId = Objects.requireNonNull(artifactId);
	}
	
	public String getVersion()
	{
		return this.version;
	}
	
	public void setVersion(final String version)
	{
		this.version = Objects.requireNonNull(version);
	}
	
	@Override
	public boolean equals(final Object o)
	{
		if(this == o)
		{
			return true;
		}
		if(!(o instanceof final Artifact artifact))
		{
			return false;
		}
		return Objects.equals(this.getGroupId(), artifact.getGroupId()) && Objects.equals(
			this.getArtifactId(),
			artifact.getArtifactId()) && Objects.equals(this.getVersion(), artifact.getVersion());
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(this.getGroupId(), this.getArtifactId(), this.getVersion());
	}
	
	@Override
	public int compareTo(@NotNull final Artifact o)
	{
		return Comparator.comparing(Artifact::getGroupId)
			.thenComparing(Artifact::getArtifactId)
			.thenComparing(Artifact::getVersion)
			.compare(this, o);
	}
	
	public String toShortMavenArtifact()
	{
		final boolean customGroupId = !DEFAULT_GROUP_ID.equals(this.getGroupId());
		final boolean customVersion = !DEFAULT_VERSION.equals(this.getVersion());
		
		final List<String> parts = new ArrayList<>();
		if(customGroupId || customVersion)
		{
			parts.add(this.getGroupId());
		}
		parts.add(this.getArtifactId());
		if(customVersion)
		{
			parts.add(this.getVersion());
		}
		
		return String.join(":", parts);
	}
	
	public String toFullMavenArtifact()
	{
		return String.join(":", this.getGroupId(), this.getArtifactId(), this.getVersion());
	}
	
	@Nullable
	public static Artifact parse(final String str)
	{
		if(str == null || str.isEmpty())
		{
			return null;
		}
		
		final List<String> parts = Stream.of(str.split(":"))
			.map(String::trim)
			.filter(s -> !s.isEmpty())
			.toList();
		if(parts.isEmpty() || parts.size() > 3)
		{
			return null;
		}
		
		return new Artifact(
			parts.size() >= 2 ? parts.get(0) : DEFAULT_GROUP_ID,
			parts.size() == 1 ? parts.get(0) : parts.get(1),
			parts.size() == 3 ? parts.get(2) : DEFAULT_VERSION);
	}
}
