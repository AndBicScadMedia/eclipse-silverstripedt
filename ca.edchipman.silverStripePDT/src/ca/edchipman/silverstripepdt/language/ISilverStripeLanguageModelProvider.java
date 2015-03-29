package ca.edchipman.silverstripepdt.language;

import org.eclipse.dltk.core.IScriptProject;

public interface ISilverStripeLanguageModelProvider {
    public String getLanguageLibraryPath(IScriptProject project, String ssFrameworkModel);
}