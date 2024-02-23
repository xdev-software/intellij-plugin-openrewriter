package software.xdev.openrewriter.ui.toolwindow;

import java.util.Optional;
import java.util.function.Supplier;

import javax.swing.JComponent;

import org.jetbrains.annotations.NotNull;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.openapi.wm.ex.ToolWindowManagerListener;
import com.intellij.ui.JBSplitter;
import com.intellij.ui.OnePixelSplitter;


public final class ORToolWindowUIUtil
{
	public static JBSplitter createSplitter(
		final Supplier<Project> projectSupplier,
		final JComponent parentComponent,
		final Disposable parentDisposable,
		final JComponent c1,
		final JComponent c2,
		final String proportionProperty,
		final float defaultSplit)
	{
		final OnePixelSplitter splitter = new OnePixelSplitter(
			splitVertically(projectSupplier.get()),
			proportionProperty,
			defaultSplit);
		splitter.setFirstComponent(c1);
		splitter.setSecondComponent(c2);
		splitter.setHonorComponentsMinimumSize(true);
		
		projectSupplier.get().getMessageBus()
			.connect(parentDisposable)
			.subscribe(ToolWindowManagerListener.TOPIC, new ToolWindowManagerListener()
			{
				@Override
				public void stateChanged(@NotNull final ToolWindowManager toolWindowManager)
				{
					splitter.setOrientation(splitVertically(projectSupplier.get()));
					parentComponent.revalidate();
					parentComponent.repaint();
				}
			});
		Disposer.register(parentDisposable, () -> {
			parentComponent.remove(splitter);
			splitter.dispose();
		});
		
		return splitter;
	}
	
	public static boolean splitVertically(final Project project)
	{
		return Optional.ofNullable(ORToolWindow.getToolWindow(project))
			.map(ToolWindow::getAnchor)
			.map(anchor -> anchor == ToolWindowAnchor.LEFT || anchor == ToolWindowAnchor.RIGHT)
			.orElse(false);
	}
	
	private ORToolWindowUIUtil()
	{
	}
}
