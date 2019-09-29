package objects;

public class FirmTypeA extends Firm{
    ResourceBundle[] resourceBundles;


    protected void selectBestResourceBundle(){
        // loop through all firms in the landscape
        // get the maximum fitness resource bundle
        // apply it to their own resource bundle and add dependencies
    }

    public ResourceBundle[] getResourceBundles() {
        return resourceBundles;
    }

    public void setResourceBundles(ResourceBundle[] resourceBundles) {
        this.resourceBundles = resourceBundles;
    }
}
