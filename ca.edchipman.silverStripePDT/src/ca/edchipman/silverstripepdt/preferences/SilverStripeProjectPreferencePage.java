package ca.edchipman.silverstripepdt.preferences;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.dltk.internal.ui.dialogs.StatusUtil;
import org.eclipse.dltk.internal.ui.preferences.PropertyAndPreferencePage;
import org.eclipse.dltk.internal.ui.util.CoreUtility;
import org.eclipse.dltk.internal.ui.wizards.dialogfields.DialogField;
import org.eclipse.dltk.internal.ui.wizards.dialogfields.IDialogFieldListener;
import org.eclipse.dltk.internal.ui.wizards.dialogfields.LayoutUtil;
import org.eclipse.dltk.internal.ui.wizards.dialogfields.SelectionButtonDialogField;
import org.eclipse.dltk.internal.ui.wizards.dialogfields.StringDialogField;
import org.eclipse.dltk.ui.dialogs.StatusInfo;
import org.eclipse.dltk.ui.util.IStatusChangeListener;
import org.eclipse.php.internal.core.preferences.CorePreferencesSupport;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;

import ca.edchipman.silverstripepdt.SilverStripePDTPlugin;
import ca.edchipman.silverstripepdt.SilverStripeVersion;
import ca.edchipman.silverstripepdt.editor.SilverStripeTemplateStructuredEditor;

@SuppressWarnings("restriction")
public class SilverStripeProjectPreferencePage extends PropertyAndPreferencePage {
    public static final String PREF_ID = "ca.edchipman.silverstripepdt.preferences.SilverStripeBasePreferencePage"; //$NON-NLS-1$
    public static final String PROP_ID = "ca.edchipman.silverstripepdt.propertyPages.SilverStripeProjectPreferencePage"; //$NON-NLS-1$

    private SilverStripeVersionGroup fConfigurationBlock;
    private SiteBase fSiteBaseBlock;

    public SilverStripeProjectPreferencePage() {
        setPreferenceStore(SilverStripePDTPlugin.getDefault().getPreferenceStore());

        // only used when page is shown programatically
        setTitle("SilverStripe Project Preferences");
    }

    /*
     * @see
     * org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets
     * .Composite)
     */
    public void createControl(Composite parent) {
        setTitle("SilverStripe Project Preferences");
        
        IWorkbenchPreferenceContainer container = (IWorkbenchPreferenceContainer) getContainer();
        IProject project=getProject();
        fConfigurationBlock = new SilverStripeVersionGroup(project, parent);
        
        fSiteBaseBlock = new SiteBase(project, parent, this);

        super.createControl(parent);
        
        
        //PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, IPHPHelpContextIds.PHP_INTERPRETER_PREFERENCES);
    }
    
    protected boolean isProjectPreferencePage() {
    	return false;
    }
    
    protected boolean offerLink() {
    	return false;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @seeorg.eclipse.jdt.internal.ui.preferences.PropertyAndPreferencePage#
     * createPreferenceContent(org.eclipse.swt.widgets.Composite)
     */
    protected Control createPreferenceContent(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.verticalSpacing = 10;
        composite.setLayout(layout);
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));
        
        fConfigurationBlock.createContents(composite);
        fSiteBaseBlock.createContents(composite);
        
        return composite;
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.eclipse.jdt.internal.ui.preferences.PropertyAndPreferencePage#
     * hasProjectSpecificOptions(org.eclipse.core.resources.IProject)
     */
    protected boolean hasProjectSpecificOptions(IProject project) {
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.eclipse.jdt.internal.ui.preferences.PropertyAndPreferencePage#
     * getPreferencePageID()
     */
    protected String getPreferencePageID() {
        return PREF_ID;
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.eclipse.jdt.internal.ui.preferences.PropertyAndPreferencePage#
     * getPropertyPageID()
     */
    protected String getPropertyPageID() {
        return PROP_ID;
    }

    /*
     * @see org.eclipse.jface.preference.IPreferencePage#performOk()
     */
    public boolean performOk() {
        if (fConfigurationBlock != null && !fConfigurationBlock.performOk() && fSiteBaseBlock != null && !fSiteBaseBlock.performOk()) {
            return false;
        }
        
        return super.performOk();
    }

    /*
     * @see org.eclipse.jface.preference.IPreferencePage#performApply()
     */
    public void performApply() {
        if (fConfigurationBlock != null) {
            fConfigurationBlock.performApply();
        }
        
        if (fSiteBaseBlock != null) {
            fSiteBaseBlock.performApply();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.dialogs.DialogPage#dispose()
     */
    public void dispose() {
        if (fConfigurationBlock != null) {
            //fConfigurationBlock.dispose();
        }
        super.dispose();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.jdt.internal.ui.preferences.PropertyAndPreferencePage#setElement
     * (org.eclipse.core.runtime.IAdaptable)
     */
    public void setElement(IAdaptable element) {
        super.setElement(element);
        setDescription(null); // no description for property page
    }
    
    public void updateStatus(IStatus status) {
        setValid(!status.matches(IStatus.ERROR));
        StatusUtil.applyToStatusLine(this, status);
    }

    
    /**
     * Request a SilverStripe Version.
     */
    public class SilverStripeVersionGroup implements Observer, SelectionListener, IDialogFieldListener {
        private Group fGroup;
        private IProject fProject;
        private Shell fShell;
        private SelectionButtonDialogField fSS24Radio, fSS23Radio, fSS30Radio, fSS31Radio, fFrameworkModel;
        private IWorkbenchPreferenceContainer fContainer;
        
        public boolean hasChanges = false;
        
        public SilverStripeVersionGroup(IProject project, Composite composite) {
            fProject=project;
        }

        protected void setShell(Shell shell) {
            fShell = shell;
        }
        
        protected Shell getShell() {
            return fShell;
        }
        
        public Control createContents(Composite parent) {
            setShell(parent.getShell());
            Composite composite = new Composite(parent, SWT.NONE);
            GridLayout layout = new GridLayout();
            layout.verticalSpacing = 10;
            
            composite.setLayout(layout);
            composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            
            fSS31Radio=new SelectionButtonDialogField(SWT.RADIO);
            fSS31Radio.setLabelText("SilverStripe 3.1"); //$NON-NLS-1$
            fSS31Radio.setDialogFieldListener(this);
            
            fSS30Radio = new SelectionButtonDialogField(SWT.RADIO);
            fSS30Radio.setLabelText("SilverStripe 3.0"); //$NON-NLS-1$
            fSS30Radio.setDialogFieldListener(this);
            
            fSS24Radio = new SelectionButtonDialogField(SWT.RADIO);
            fSS24Radio.setLabelText("SilverStripe 2.4"); //$NON-NLS-1$
            fSS24Radio.setDialogFieldListener(this);
            
            fSS23Radio = new SelectionButtonDialogField(SWT.RADIO);
            fSS23Radio.setLabelText("SilverStripe 2.3"); //$NON-NLS-1$
            fSS23Radio.setDialogFieldListener(this);
            
            fFrameworkModel = new SelectionButtonDialogField(SWT.CHECK);
            fFrameworkModel.setLabelText("Use SilverStripe Framework Only"); //$NON-NLS-1$
            fFrameworkModel.setDialogFieldListener(this);
            fFrameworkModel.setEnabled(false);
            
            
            // createContent
            fGroup = new Group(composite, SWT.NONE);
            fGroup.setFont(composite.getFont());
            fGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            fGroup.setLayout(layout);
            fGroup.setText("SilverStripe Version"); //$NON-NLS-1$

            fSS31Radio.doFillIntoGrid(fGroup, 2);
            fSS30Radio.doFillIntoGrid(fGroup, 2);
            fSS24Radio.doFillIntoGrid(fGroup, 2);
            fSS23Radio.doFillIntoGrid(fGroup, 2);
            fFrameworkModel.doFillIntoGrid(fGroup, 2);
            
            String ssVersion=CorePreferencesSupport.getInstance().getProjectSpecificPreferencesValue("silverstripe_version", SilverStripeVersion.SS3_1, fProject.getProject());
            String ssFrameworkModel=CorePreferencesSupport.getInstance().getProjectSpecificPreferencesValue("silverstripe_framework_model", SilverStripeVersion.FULL_CMS, fProject.getProject());
            
            if(ssVersion.equals(SilverStripeVersion.SS3_1)) {
                fSS31Radio.setSelection(true);
                fFrameworkModel.setEnabled(true);
                
                if(ssFrameworkModel.equals(SilverStripeVersion.FRAMEWORK_ONLY)) {
                    fFrameworkModel.setSelection(true);
                }
            }else if(ssVersion.equals(SilverStripeVersion.SS3_0)) {
                fSS30Radio.setSelection(true);
                fFrameworkModel.setEnabled(true);
                
                if(ssFrameworkModel.equals(SilverStripeVersion.FRAMEWORK_ONLY)) {
                    fFrameworkModel.setSelection(true);
                }
            }else if(ssVersion.equals(SilverStripeVersion.SS2_4)) {
                fSS24Radio.setSelection(true);
            }else if(ssVersion.equals(SilverStripeVersion.SS2_3)) {
                fSS23Radio.setSelection(true);
            }else {
                fSS31Radio.setSelection(true);                
            }
            
            return composite;
        }
        
        /**
         * Gets the whether the SilverStripe 3.1 radio is selected
         * @return Returns boolean true if the SilverStripe 3.1 radio is selected
         */
        public boolean isSS31() {
            return fSS31Radio.isSelected();
        }
        
        /**
         * Gets the whether the SilverStripe 3.0 radio is selected
         * @return Returns boolean true if the SilverStripe 3.0 radio is selected
         */
        public boolean isSS30() {
            return fSS30Radio.isSelected();
        }
        
        /**
         * Gets the whether the SilverStripe 2.4 radio is selected
         * @return Returns boolean true if the SilverStripe 2.4 radio is selected
         */
        public boolean isSS24() {
            return fSS24Radio.isSelected();
        }
        
        /**
         * Gets the whether the SilverStripe 2.3 radio is selected
         * @return Returns boolean true if the SilverStripe 2.3 radio is selected
         */
        public boolean isSS23() {
            return fSS23Radio.isSelected();
        }
        
        /*
         * (non-Javadoc)
         * 
         * @see
         * org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse
         * .swt.events.SelectionEvent)
         */
        public void widgetSelected(SelectionEvent e) {
            widgetDefaultSelected(e);
        }

        public void widgetDefaultSelected(SelectionEvent e) {
            fSS24Radio.setSelection(true);
        }

        @Override
        public void dialogFieldChanged(DialogField arg0) {
            if (fSS30Radio.isSelected() || fSS31Radio.isSelected()) {
                fFrameworkModel.setEnabled(true); 
            } else {
                fFrameworkModel.setEnabled(false);
            }
        }

        @Override
        public void update(Observable o, Object arg) {
            if (fSS30Radio.isSelected() || fSS31Radio.isSelected()) {
               fFrameworkModel.setEnabled(true); 
            } else {
               fFrameworkModel.setEnabled(false);
            }
        }
        
        public boolean performOk() {
            return processChanges(fContainer);
        }

        public boolean performApply() {
            return processChanges(null); // apply directly
        }

        protected boolean processChanges(IWorkbenchPreferenceContainer container) {
            String ssVersion=SilverStripeVersion.SS3_1;
            String ssFrameworkModel=SilverStripeVersion.FULL_CMS;
            
            if(fSS31Radio.isSelected()) {
                ssVersion=SilverStripeVersion.SS3_1;
            }else if(fSS30Radio.isSelected()) {
                ssVersion=SilverStripeVersion.SS3_0;
            }else if(fSS24Radio.isSelected()) {
                ssVersion=SilverStripeVersion.SS2_4;
            }else if(fSS23Radio.isSelected()) {
                ssVersion=SilverStripeVersion.SS2_3;
            }
            
            if(fFrameworkModel.isSelected()) {
                ssFrameworkModel=SilverStripeVersion.FRAMEWORK_ONLY;
            }
            
            if(ssVersion!=CorePreferencesSupport.getInstance().getProjectSpecificPreferencesValue("silverstripe_version", SilverStripeVersion.SS3_1, fProject.getProject())) {
                hasChanges=true;
            }
            
            if(ssFrameworkModel!=CorePreferencesSupport.getInstance().getProjectSpecificPreferencesValue("silverstripe_framework_model", SilverStripeVersion.FULL_CMS, fProject.getProject())) {
                hasChanges=true;
            }
            
            boolean doBuild = hasChanges;
            if (doBuild) {
                prepareForBuild();
            }
            if (container != null) {
                // no need to apply the changes to the original store: will be done
                // by the page container
                if (doBuild) { // post build
                    container.registerUpdateJob(CoreUtility.getBuildJob(fProject));
                }
            } else {
                // apply changes right away
                CorePreferencesSupport.getInstance().setProjectSpecificPreferencesValue("silverstripe_version", ssVersion, fProject);
                CorePreferencesSupport.getInstance().setProjectSpecificPreferencesValue("silverstripe_framework_model", ssFrameworkModel, fProject);
                
                if (doBuild) {
                    CoreUtility.getBuildJob(fProject).schedule();
                }
            }
            
            if(hasChanges) {
                IWorkbenchWindow win = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
                IWorkbenchPage page = win.getActivePage();
                if (page != null) {
                    IEditorReference[] editors = page.getEditorReferences();
                    for(int i=0;i<editors.length;i++) {
                        IEditorReference editorRef = editors[i];
                        IEditorPart editor = editorRef.getEditor(false);
                        
                        if(editor instanceof SilverStripeTemplateStructuredEditor) {
                            ((SilverStripeTemplateStructuredEditor) editor).setSSVersion(ssVersion);
                        }
                    }
                }
            }
            
            return true;
        }

        protected void prepareForBuild() {
            // implement this method for any actions that need to be taken before
            // running build
        }
    }
    

    
    /**
     * Request a SiteBase Field
     */
    public class SiteBase implements Observer, SelectionListener, IDialogFieldListener {
        private Group fGroup;
        private IProject fProject;
        private Shell fShell;
        private StringDialogField fSiteBase;
        private IWorkbenchPreferenceContainer fContainer;
        
        public boolean hasChanges = false;
        private IStatus fSiteBaseStatus;
        private SilverStripeProjectPreferencePage fParent;
        
        public SiteBase(IProject project, Composite composite, SilverStripeProjectPreferencePage parent) {
            fProject=project;
            fParent=parent;
        }

        protected void setShell(Shell shell) {
            fShell = shell;
        }
        
        protected Shell getShell() {
            return fShell;
        }
        
        public Control createContents(Composite parent) {
            setShell(parent.getShell());
            Composite composite=new Composite(parent, SWT.NONE);
            GridLayout layout=new GridLayout();
            layout.verticalSpacing=10;
            layout.numColumns=2;
            composite.setLayout(layout);
            composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            
            
            fSiteBase=new StringDialogField();
            fSiteBase.setLabelText("Site Base URL: ");
            fSiteBase.setMessage("http://localhost/");
            fSiteBase.setDialogFieldListener(this);
            fSiteBase.doFillIntoGrid(composite, 2);
                        
            String siteBase=CorePreferencesSupport.getInstance().getProjectSpecificPreferencesValue("silverstripe_site_base", "", fProject.getProject());
            fSiteBase.setText(siteBase);
            
            
            LayoutUtil.setHorizontalGrabbing(fSiteBase.getTextControl(null));
            
            return composite;
        }
        
        /**
         * Gets the site base set in the field
         * @return String value of the site base field
         */
        public String getSiteBaseValue() {
            return fSiteBase.getText();
        }
        
        /*
         * (non-Javadoc)
         * 
         * @see
         * org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse
         * .swt.events.SelectionEvent)
         */
        public void widgetSelected(SelectionEvent e) {
            widgetDefaultSelected(e);
        }

        public boolean performOk() {
            return processChanges(fContainer);
        }

        public boolean performApply() {
            return processChanges(null); // apply directly
        }

        protected boolean processChanges(IWorkbenchPreferenceContainer container) {
            String siteBase=fSiteBase.getText();
            
            
            if(siteBase!=CorePreferencesSupport.getInstance().getProjectSpecificPreferencesValue("silverstripe_site_base", null, fProject.getProject())) {
                hasChanges=true;
            }
            
            boolean doBuild = hasChanges;
            if (doBuild) {
                prepareForBuild();
            }
            if (container != null) {
                // no need to apply the changes to the original store: will be done
                // by the page container
                if (doBuild) { // post build
                    container.registerUpdateJob(CoreUtility.getBuildJob(fProject));
                }
            } else {
                // apply changes right away
                CorePreferencesSupport.getInstance().setProjectSpecificPreferencesValue("silverstripe_site_base", siteBase, fProject);
                
                if (doBuild) {
                    CoreUtility.getBuildJob(fProject).schedule();
                }
            }
            
            return true;
        }

        protected void prepareForBuild() {
            // implement this method for any actions that need to be taken before
            // running build
        }

        @Override
        public void dialogFieldChanged(DialogField arg0) {
            fSiteBaseStatus=updateURLStatus();
            fParent.updateStatus(fSiteBaseStatus);
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent e) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void update(Observable arg0, Object arg1) {
            fSiteBaseStatus=updateURLStatus();
            fParent.updateStatus(fSiteBaseStatus);
        }
        
        private IStatus updateURLStatus() {
            StatusInfo status=new StatusInfo();
            try {
                String address=fSiteBase.getText();
                if(address.length()==0) {
                    return status;
                }
                
                URL url=new URL(address);
                if(("http".equals(url.getProtocol())==false && "https".equals(url.getProtocol())==false) || url.getHost().length()==0) { //$NON-NLS-1$
                    status.setError("URL is not a web address");
                    return status;
                }
            }catch (MalformedURLException e) {
                status.setError("Invalid URL");
                return status;
            }

            return status;
        }
    }

    @Override
    protected String getPreferencePageId() {
        return SilverStripeProjectPreferencePage.PREF_ID;
    }

    @Override
    protected String getPropertyPageId() {
        return SilverStripeProjectPreferencePage.PROP_ID;
    }
}