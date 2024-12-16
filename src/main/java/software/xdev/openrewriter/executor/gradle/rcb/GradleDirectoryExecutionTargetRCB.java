package software.xdev.openrewriter.executor.gradle.rcb;

import com.intellij.openapi.project.Project;

import software.xdev.openrewriter.executor.gradle.GradleRecipesExecutor;
import software.xdev.openrewriter.executor.request.ExecutionRequest;
import software.xdev.openrewriter.executor.request.target.directory.DirectoryExecutionTarget;


public class GradleDirectoryExecutionTargetRCB implements GradleRequestCommandBuilder
{
	@Override
	public void apply(
		final Project project,
		final ExecutionRequest request,
		final GradleRecipesExecutor.GradleRunArguments gradleRunArguments)
	{
		if(request.getTarget() instanceof final DirectoryExecutionTarget det)
		{
			gradleRunArguments.setWorkDir(det.getPath().toString());
		}
	}
}
