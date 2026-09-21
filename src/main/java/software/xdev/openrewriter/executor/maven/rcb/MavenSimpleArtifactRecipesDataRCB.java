package software.xdev.openrewriter.executor.maven.rcb;

import java.util.stream.Collectors;

import org.jetbrains.idea.maven.execution.MavenRunnerParameters;
import org.jetbrains.idea.maven.execution.MavenRunnerSettings;
import org.jetbrains.idea.maven.project.MavenGeneralSettings;

import com.intellij.openapi.project.Project;

import software.xdev.openrewriter.executor.request.ExecutionRequest;
import software.xdev.openrewriter.executor.request.recipedata.simpleartifact.Artifact;
import software.xdev.openrewriter.executor.request.recipedata.simpleartifact.Recipe;
import software.xdev.openrewriter.executor.request.recipedata.simpleartifact.SimpleArtifactRecipesData;


public class MavenSimpleArtifactRecipesDataRCB implements MavenRequestCommandBuilder
{
	@Override
	public void apply(
		final Project project,
		final ExecutionRequest request,
		final MavenRunnerParameters runnerParameters,
		final MavenGeneralSettings generalSettings,
		final MavenRunnerSettings runnerSettings)
	{
		if(request.getRecipesData() instanceof final SimpleArtifactRecipesData r)
		{
			runnerParameters.setCommandLine(runnerParameters.getCommandLine()
				+ " -Drewrite.activeRecipes="
				+ r.getRecipes().stream().map(Recipe::getId).collect(Collectors.joining(","))
				+ (!r.getArtifacts().isEmpty()
				? " -Drewrite.recipeArtifactCoordinates="
				+ r.getArtifacts()
				.stream()
				.map(Artifact::toFullMavenArtifact)
				.collect(Collectors.joining(","))
				: ""));
		}
	}
}
