package ca.edchipman.silverstripepdt;

import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.ui.templates.ScriptTemplateContext;
import org.eclipse.jface.text.IDocument;
import org.eclipse.php.internal.ui.editor.templates.PhpTemplateContextType;

@SuppressWarnings("restriction")
public class SilverStripeTemplateContextType extends PhpTemplateContextType {
	public static final String SS_CONTEXT_TYPE_ID = "ca.edchipman.silverstripepdt.SilverStripeTemplate"; //$NON-NLS-1$
	
	@Override
	public ScriptTemplateContext createContext(IDocument document, int offset, int length, ISourceModule sourceModule) {
		return new SilverStripeTemplateContext(this, document, offset, length, sourceModule);
	}
}
