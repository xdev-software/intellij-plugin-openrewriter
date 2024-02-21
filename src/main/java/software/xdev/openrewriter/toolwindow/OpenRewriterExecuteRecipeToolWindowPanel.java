package software.xdev.openrewriter.toolwindow;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.util.ui.JBUI;


/**
 * The tool window for CheckStyle scans.
 */
public class OpenRewriterExecuteRecipeToolWindowPanel extends JPanel implements DumbAware
{
	
	public static final String ID_TOOLWINDOW = "OpenRewriter";
	
	/**
	 * Logger for this class.
	 */
	private static final Logger LOG = Logger.getInstance(OpenRewriterExecuteRecipeToolWindowPanel.class);
	
	private final Project project;
	private final ToolWindow toolWindow;
	
	/**
	 * Create a tool window for the given project.
	 *
	 * @param project the project.
	 */
	public OpenRewriterExecuteRecipeToolWindowPanel(final ToolWindow toolWindow, final Project project)
	{
		super(new BorderLayout());
		
		this.toolWindow = toolWindow;
		this.project = project;
		
		this.setBorder(JBUI.Borders.empty(1));
		
		this.add(new JLabel("TEST"));
	}
}
