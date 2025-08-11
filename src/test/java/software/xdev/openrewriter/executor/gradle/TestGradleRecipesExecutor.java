package software.xdev.openrewriter.executor.gradle;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


class TestGradleRecipesExecutor
{
	@Test
	@DisplayName("Reflection works")
	void reflectionWorks()
	{
		Assertions.assertDoesNotThrow(GradleRecipesExecutor::getMBuildTaskInfo);
	}
}
