package software.xdev.openrewriter.executor.request.target.directory;

import static com.intellij.openapi.ui.TextComponentAccessor.TEXT_FIELD_WHOLE_TEXT;

import java.nio.file.Path;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.fileChooser.FileChooserFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;

import software.xdev.openrewriter.executor.request.target.ExecutionTargetProvider;
import software.xdev.openrewriter.ui.toolwindow.execute.panels.ExecuteRecipeConfigPanel;


public class DirectoryExecutionTargetProvider implements ExecutionTargetProvider<DirectoryExecutionTarget>
{
	@Override
	public String name()
	{
		return "Directory";
	}
	
	@Override
	public Icon icon()
	{
		return AllIcons.Nodes.Folder;
	}
	
	@Override
	public DirectoryExecutionTarget createDefault(final Project project)
	{
		return new DirectoryExecutionTarget();
	}
	
	@Override
	public Class<DirectoryExecutionTarget> matchingClass()
	{
		return DirectoryExecutionTarget.class;
	}
	
	@Override
	public ExecuteRecipeConfigPanel<? extends DirectoryExecutionTarget> createConfigPanel(final Project project)
	{
		return new DirectoryExecutionTargetProviderConfigPanel();
	}
	
	public static class DirectoryExecutionTargetProviderConfigPanel
		extends ExecuteRecipeConfigPanel<DirectoryExecutionTarget>
	{
		private final JTextField field;
		
		public DirectoryExecutionTargetProviderConfigPanel()
		{
			this.add(new JLabel("Directory"));
			
			final FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor();
			
			this.field = FileChooserFactory.getInstance().createFileTextField(descriptor, this).getField();
			this.field.setEnabled(false);
			
			final TextFieldWithBrowseButton resultPath = new TextFieldWithBrowseButton(this.field);
			resultPath.addBrowseFolderListener(null, descriptor, TEXT_FIELD_WHOLE_TEXT);
			
			this.add(resultPath);
			
			final Runnable update = () -> this.changeValue(t -> {
				final String strPath = this.field.getText();
				t.setPath(strPath != null && !strPath.isEmpty() ? Path.of(strPath) : null);
			});
			this.field.getDocument().addDocumentListener(new DocumentListener()
			{
				@Override
				public void insertUpdate(final DocumentEvent e)
				{
					update.run();
				}
				
				@Override
				public void removeUpdate(final DocumentEvent e)
				{
					update.run();
				}
				
				@Override
				public void changedUpdate(final DocumentEvent e)
				{
					update.run();
				}
			});
		}
		
		@Override
		protected void updateFrom(final DirectoryExecutionTarget data)
		{
			this.field.setText(data.getPath() != null ? data.getPath().toString() : "");
		}
	}
}
