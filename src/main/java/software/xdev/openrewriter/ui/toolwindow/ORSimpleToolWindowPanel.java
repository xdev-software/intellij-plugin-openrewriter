package software.xdev.openrewriter.ui.toolwindow;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javax.swing.Box;

import org.jetbrains.annotations.NotNull;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;


public class ORSimpleToolWindowPanel extends SimpleToolWindowPanel implements Disposable, DumbAware
{
	private Project project;
	
	private ActionToolbar toolbar;
	
	public ORSimpleToolWindowPanel(final boolean vertical, final boolean borderless)
	{
		super(vertical, borderless);
	}
	
	protected Project getProject()
	{
		return this.project;
	}
	
	public void setProject(final Project project)
	{
		this.project = project;
	}
	
	protected <T> T getService(@NotNull final Class<T> serviceClass)
	{
		return this.getProject().getService(serviceClass);
	}
	
	protected void setToolbar(final String id, final ActionGroup group)
	{
		if(this.toolbar != null)
		{
			this.toolbar.setTargetComponent(null);
			super.setToolbar(null);
			this.toolbar = null;
		}
		
		this.toolbar = ActionManager.getInstance().createActionToolbar(id, group, false);
		this.toolbar.setTargetComponent(this);
		
		final Box toolBarBox = Box.createHorizontalBox();
		toolBarBox.add(this.toolbar.getComponent());
		super.setToolbar(toolBarBox);
		
		this.toolbar.getComponent().setVisible(true);
	}
	
	private static final Consumer<ActionToolbar> REFRESH_TOOLBAR_RUNNABLE = buildRefreshToolbarRunnable();
	
	static Consumer<ActionToolbar> buildRefreshToolbarRunnable()
	{
		Optional<BiConsumer<ActionToolbar, Exception>> fallback;
		try
		{
			// Deprecated since 241
			final Method mUpdateActionsImmediately =
				ActionToolbar.class.getDeclaredMethod("updateActionsImmediately");
			
			fallback = Optional.of((tb, cause) -> {
				try
				{
					mUpdateActionsImmediately.invoke(tb);
				}
				catch(final IllegalAccessException | InvocationTargetException e)
				{
					final RuntimeException ex = new RuntimeException("Failed to invoke updateActionsImmediately", e);
					if(cause != null)
					{
						ex.addSuppressed(cause);
					}
					throw ex;
				}
			});
		}
		catch(final NoSuchMethodException e)
		{
			fallback = Optional.empty();
		}
		
		final Optional<BiConsumer<ActionToolbar, Exception>> finalFallback = fallback;
		
		try
		{
			// Only available since 241
			final Method mUpdateActionsAsync = ActionToolbar.class.getDeclaredMethod("updateActionsAsync");
			
			return tb -> {
				try
				{
					mUpdateActionsAsync.invoke(tb);
				}
				catch(final IllegalAccessException | InvocationTargetException e)
				{
					final RuntimeException ex = new RuntimeException("Failed to invoke updateActionsAsync", e);
					finalFallback.ifPresentOrElse(
						f -> f.accept(tb, ex),
						() -> {
							throw ex;
						});
				}
			};
		}
		catch(final NoSuchMethodException e)
		{
			return finalFallback
				.map(f -> (Consumer<ActionToolbar>)tb -> f.accept(tb, null))
				.orElseThrow();
		}
	}
	
	protected void refreshToolbar()
	{
		REFRESH_TOOLBAR_RUNNABLE.accept(this.toolbar);
	}
	
	@Override
	public void dispose()
	{
		// NO OP
	}
}
