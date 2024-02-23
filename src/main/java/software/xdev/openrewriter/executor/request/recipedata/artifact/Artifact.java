package software.xdev.openrewriter.executor.request.recipedata.artifact;

public class Artifact
{
	private String groupId;
	private String artifactId;
	private String version;
	
	public String getGroupId()
	{
		return this.groupId;
	}
	
	public void setGroupId(final String groupId)
	{
		this.groupId = groupId;
	}
	
	public String getArtifactId()
	{
		return this.artifactId;
	}
	
	public void setArtifactId(final String artifactId)
	{
		this.artifactId = artifactId;
	}
	
	public String getVersion()
	{
		return this.version;
	}
	
	public void setVersion(final String version)
	{
		this.version = version;
	}
}
