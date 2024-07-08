package software.xdev.openrewriter.executor.gradle.rcb;

import java.util.Objects;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectUtil;

import software.xdev.openrewriter.executor.gradle.GradleRecipesExecutor;
import software.xdev.openrewriter.executor.request.ExecutionRequest;
import software.xdev.openrewriter.executor.request.target.module.ModuleExecutionTarget;


public class GradleModuleExecutionTargetRCB implements GradleRequestCommandBuilder
{
	@Override
	public void apply(
		final Project project,
		final ExecutionRequest request,
		final GradleRecipesExecutor.GradleRunArguments gradleRunArguments)
	{
		if(request.getTarget() instanceof final ModuleExecutionTarget met)
		{
			gradleRunArguments.setWorkDir(
				Objects.requireNonNull(ProjectUtil.guessModuleDir(met.getModule())).getPath());
		}
	}
}
