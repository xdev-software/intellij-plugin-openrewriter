package software.xdev.openrewriter.ui;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;


public final class UIThreadAccessor
{
	public static void run(final Project project, final Runnable runnable)
	{
		ApplicationManager.getApplication().invokeLater(() -> {
			if(!project.isDisposed())
			{
				runnable.run();
			}
		});
	}
	
	private UIThreadAccessor()
	{
	}
}
