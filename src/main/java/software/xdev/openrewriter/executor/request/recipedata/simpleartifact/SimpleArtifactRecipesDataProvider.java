package software.xdev.openrewriter.executor.request.recipedata.simpleartifact;

import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import javax.swing.Icon;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.InputValidator;
import com.intellij.openapi.ui.VerticalFlowLayout;
import com.intellij.ui.OnePixelSplitter;
import com.intellij.util.PlatformIcons;

import software.xdev.openrewriter.executor.request.recipedata.RecipesDataProvider;
import software.xdev.openrewriter.ui.toolwindow.execute.panels.EditableListPanelManager;
import software.xdev.openrewriter.ui.toolwindow.execute.panels.ExecuteRecipeConfigPanel;


public class SimpleArtifactRecipesDataProvider implements RecipesDataProvider<SimpleArtifactRecipesData>
{
	@Override
	public String name()
	{
		return "Simple/Artifact";
	}
	
	@Override
	public Icon icon()
	{
		return PlatformIcons.LIBRARY_ICON;
	}
	
	@Override
	public SimpleArtifactRecipesData createDefault(final Project project)
	{
		return new SimpleArtifactRecipesData();
	}
	
	@Override
	public Class<SimpleArtifactRecipesData> matchingClass()
	{
		return SimpleArtifactRecipesData.class;
	}
	
	@Override
	public ArtifactRecipesDataConfigPanel createConfigPanel(final Project project)
	{
		return new ArtifactRecipesDataConfigPanel();
	}
	
	public static class ArtifactRecipesDataConfigPanel extends ExecuteRecipeConfigPanel<SimpleArtifactRecipesData>
	{
		@SuppressWarnings("java:S1192")
		private final EditableListPanelManager<Artifact> artifactPanelManager = new EditableListPanelManager<>(
			"Artifacts",
			"No artifacts / Only built-in recipes available",
			"Add artifact",
			"Edit artifact",
			// @formatter:off
			"<html><body>"
				+ "<p>An artifact is described like this: <code>[groupId:]artifactId[:version]</code></p>"
				+ "<table>"
				+ "<tr>"
				+ "<th>Attribute</th>"
				+ "<th>Default value</th>"
				+ "</tr>"
				+ "<tr>"
				+ "<td><code>groupId</code></td>"
				+ "<td><code>" + Artifact.DEFAULT_GROUP_ID + "</code></td>"
				+ "</tr>"
				+ "<tr>"
				+ "<td><code>version</code></td>"
				+ "<td><code>" + Artifact.DEFAULT_VERSION + "</code></td>"
				+ "</tr>"
				+ "</table>"
				+ "<p>Examples:</p>"
				+ "<ul>"
				+ "<li><code>rewrite-migrate-java</code></li>"
				+ "<li><code>" + Artifact.DEFAULT_GROUP_ID + ":rewrite-migrate-java</code></li>"
				+ "<li><code>" + Artifact.DEFAULT_GROUP_ID + ":rewrite-migrate-java:" + Artifact.DEFAULT_VERSION
				+ "</code></li>"
				+ "</ul>"
				+ "<p>The value is derived from <a href=\"https://docs.openrewrite"
				+ ".org/running-recipes/running-rewrite-on-a-maven-project-without-modifying-the"
				+ "-build\"><code>recipeArtifactCoordinates</code></a>.</p>"
				+ "<br/>"
				+ "</body></html>",
			// @formatter:on
			"rewrite-migrate-java",
			Artifact::toShortMavenArtifact,
			Artifact::parse,
			new ArtifactRecipesValidator(input -> Artifact.parse(input) != null)
		);
		
		private final EditableListPanelManager<Recipe> recipePanelManager = new EditableListPanelManager<>(
			"Recipes",
			"No recipes",
			"Add recipe",
			"Edit recipe",
			"<html><body>"
				+ "<p>A recipeID is described like this:<br/>"
				+ "<code>org.openrewrite.java.RemoveUnusedImports</code></p>"
				+ "<p>The value is derived from <a href=\"https://docs.openrewrite"
				+ ".org/running-recipes/running-rewrite-on-a-maven-project-without-modifying-the"
				+ "-build\"><code>activeRecipes</code></a>.</p>"
				+ "<br/>"
				+ "</body></html>",
			"org.openrewrite.java.RemoveUnusedImports",
			Recipe::getId,
			Recipe::new,
			new ArtifactRecipesValidator(input -> input != null && !input.isBlank())
		);
		
		public ArtifactRecipesDataConfigPanel()
		{
			final Supplier<VerticalFlowLayout> vlSupplier = () -> new VerticalFlowLayout(5, 0);
			
			final OnePixelSplitter container = new OnePixelSplitter();
			container.setFirstComponent(wrapInVerticalLayout(vlSupplier, this.artifactPanelManager.getPanel()));
			container.setSecondComponent(wrapInVerticalLayout(vlSupplier, this.recipePanelManager.getPanel()));
			container.setHonorComponentsMinimumSize(true);
			
			this.add(container);
			
			Stream.of(this.artifactPanelManager, this.recipePanelManager)
				.forEach(m -> m.addValueChangeListener(() -> this.getValueChangeCallback().run()));
		}
		
		@Override
		protected void updateFrom(final SimpleArtifactRecipesData data)
		{
			this.artifactPanelManager.update(data.getArtifacts());
			this.recipePanelManager.update(data.getRecipes());
		}
		
		static class ArtifactRecipesValidator implements InputValidator
		{
			private final Predicate<String> checkFunc;
			
			public ArtifactRecipesValidator(final Predicate<String> checkFunc)
			{
				this.checkFunc = checkFunc;
			}
			
			@Override
			public boolean checkInput(final String inputString)
			{
				return this.checkFunc.test(inputString);
			}
			
			@Override
			public boolean canClose(final String inputString)
			{
				return this.checkFunc.test(inputString);
			}
		}
	}
}
