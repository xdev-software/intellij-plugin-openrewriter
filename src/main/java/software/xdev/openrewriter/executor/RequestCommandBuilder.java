package software.xdev.openrewriter.executor;

import java.util.Set;
import java.util.stream.Collectors;

import com.intellij.openapi.extensions.ExtensionPointName;


public interface RequestCommandBuilder
{
	ExtensionPointName<? extends RequestCommandBuilder> EP =
		ExtensionPointName.create("software.xdev.openrewriter.requestCommandBuilder");
	
	static <T extends RequestCommandBuilder> Set<T> getAllFor(final Class<T> rcbClass)
	{
		return EP.getExtensionList()
			.stream()
			.filter(rcbClass::isInstance)
			.map(rcbClass::cast)
			.collect(Collectors.toSet());
	}
}
