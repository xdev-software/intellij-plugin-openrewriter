package software.xdev.openrewriter.ui.toolwindow;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


class TestORSimpleToolWindowPanel
{
	@Test
	@DisplayName("buildRefreshToolbarRunnable/Reflection works")
	void buildRefreshToolbarRunnableWorks()
	{
		Assertions.assertDoesNotThrow(ORSimpleToolWindowPanel::buildRefreshToolbarRunnable);
	}
}
