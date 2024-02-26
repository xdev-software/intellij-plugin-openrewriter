package software.xdev.openrewriter.executor.request.recipedata.artifact;

import java.util.List;
import java.util.Objects;
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


public class ArtifactRecipesDataProvider implements RecipesDataProvider<ArtifactRecipesData>
{
	@Override
	public String name()
	{
		return "Artifact";
	}
	
	@Override
	public Icon icon()
	{
		return PlatformIcons.LIBRARY_ICON;
	}
	
	@Override
	public ArtifactRecipesData createDefault(final Project project)
	{
		return new ArtifactRecipesData();
	}
	
	@Override
	public Class<ArtifactRecipesData> matchingClass()
	{
		return ArtifactRecipesData.class;
	}
	
	@Override
	public ArtifactRecipesDataConfigPanel createConfigPanel(final Project project)
	{
		return new ArtifactRecipesDataConfigPanel();
	}
	
	public static class ArtifactRecipesDataConfigPanel extends ExecuteRecipeConfigPanel<ArtifactRecipesData>
	{
		private final EditableListPanelManager<Artifact> artifactPanelManager = new EditableListPanelManager<>(
			"Artifacts",
			"No artifacts",
			"Add artifact",
			"Edit artifact",
			"<html><body>"
				+ "<p>An artifact is described like this: <code>groupId:artifactId:version</code></p>"
				+ "<p>The value is derived from <a href=\"https://docs.openrewrite"
				+ ".org/running-recipes/running-rewrite-on-a-maven-project-without-modifying-the"
				+ "-build\"><code>recipeArtifactCoordinates</code></a></p>"
				+ "<p>For more information checkout the "
				+ "<a href=\"https://maven.apache.org/pom.html#maven-coordinates\">Maven docs</a></p>"
				+ "</body></html>",
			"org.openrewrite.recipe:artifactId:LATEST",
			a -> a.getGroupId() + ":" + a.getArtifactId() + ":" + a.getVersion(),
			str -> {
				final List<String> parts = List.of(str.split(":"));
				if(parts.size() != 3)
				{
					return null;
				}
				
				return new Artifact(parts.get(0), parts.get(1), parts.get(2));
			},
			new ArtifactRecipesValidator(input -> input != null && Stream.of(input.split(":"))
				.filter(Objects::nonNull)
				.filter(s -> !s.isBlank())
				.count() == 3)
		);
		
		private final EditableListPanelManager<Recipe> recipePanelManager = new EditableListPanelManager<>(
			"Recipes",
			"No recipes",
			"Add recipe",
			"Edit recipe",
			"<html><body>"
				+ "<p>A recipeID is described like this: "
				+ "<code>org.openrewrite.java.migrate.jakarta.JavaxMigrationToJakarta</code></p>"
				+ "<p>The value is derived from <a href=\"https://docs.openrewrite"
				+ ".org/running-recipes/running-rewrite-on-a-maven-project-without-modifying-the"
				+ "-build\"><code>activeRecipes</code></a></p>"
				+ "</body></html>",
			null,
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
		protected void updateFrom(final ArtifactRecipesData data)
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
