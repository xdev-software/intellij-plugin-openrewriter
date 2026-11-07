package software.xdev.openrewriter.ui.toolwindow.execute.panels;

import java.util.Collection;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.jetbrains.annotations.NotNull;

import com.intellij.openapi.project.Project;

import software.xdev.openrewriter.executor.RecipesExecutor;
import software.xdev.openrewriter.executor.request.ExecutionRequest;
import software.xdev.openrewriter.executor.request.target.ExecutionTarget;
import software.xdev.openrewriter.executor.request.target.ExecutionTargetProvider;


public class ExecutionTargetProviderPanel
	extends PresentableProviderPanel<ExecutionTargetProvider<?>, ExecutionTarget, ExecutionRequest>
{
	protected final Map<ExecutionTargetProvider<?>, Boolean> preferredForCurrentStateOverridden = new WeakHashMap<>();
	protected final Supplier<Collection<RecipesExecutor<?>>> recipeExecutorsSupplier;
	protected final Consumer<RecipesExecutor<?>> onPreferredRecipesExecutor;
	
	public ExecutionTargetProviderPanel(
		final Supplier<Project> getProjectSupplier,
		final String lblText,
		final Function<ExecutionRequest, ExecutionTarget> getFromRootFunc,
		final BiConsumer<ExecutionRequest, ExecutionTarget> setIntoRootFunc,
		final Supplier<Collection<RecipesExecutor<?>>> recipeExecutorsSupplier,
		final Consumer<RecipesExecutor<?>> onPreferredRecipesExecutor)
	{
		super(getProjectSupplier, lblText, getFromRootFunc, setIntoRootFunc);
		this.recipeExecutorsSupplier = recipeExecutorsSupplier;
		this.onPreferredRecipesExecutor = onPreferredRecipesExecutor;
	}
	
	@Override
	public void setValueChangeCallback(final @NotNull Runnable valueChangeCallback)
	{
		super.setValueChangeCallback(() -> {
			this.checkForPreferredRecipesExecutor();
			valueChangeCallback.run();
		});
	}
	
	public void checkForPreferredRecipesExecutor()
	{
		// Performance: Check if the method was overridden
		// default is: do nothing -> No execution required
		if(Boolean.TRUE.equals(
			this.preferredForCurrentStateOverridden.computeIfAbsent(
				this.getCurrentSelected(),
				ExecutionTargetProvider::isGetPreferredForCurrentStateOverridden)))
		{
			this.ifData(d ->
				this.getCurrentSelected().getPreferredForCurrentStateUnchecked(
						this.recipeExecutorsSupplier.get(),
						this.getData(d),
						this.getProjectSupplier.get())
					.ifPresent(this.onPreferredRecipesExecutor));
		}
	}
}
