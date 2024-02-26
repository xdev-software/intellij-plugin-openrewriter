package software.xdev.openrewriter.executor.request.recipedata.artifact;

import java.util.Comparator;
import java.util.Objects;

import org.jetbrains.annotations.NotNull;


public class Artifact implements Comparable<Artifact>
{
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
}
