package Alex.Frolov.Ivt;

import java.io.File;
import java.io.IOException;
import java.util.prefs.Preferences;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.controlsfx.dialog.Dialogs;
import Alex.Frolov.Ivt.model.FPersonListWrapperA;
import Alex.Frolov.Ivt.model.FPersonA;
import Alex.Frolov.Ivt.view.FBirthdayStatisticsControllerA;
import Alex.Frolov.Ivt.view.FPersonEditDialogControllerA;
import Alex.Frolov.Ivt.view.FPersonOverviewControllerA;
import Alex.Frolov.Ivt.view.FRootLayoutControllerA;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class FMainAppA extends Application {
	 // ... AFTER THE OTHER VARIABLES ...

    /**
     * The data as an observable list of Persons.
     */
    private ObservableList<FPersonA> personData = FXCollections.observableArrayList();

    /**
     * Constructor
     */
    public FMainAppA() {
        // Add some sample data
        personData.add(new FPersonA("Hans", "Muster"));
        personData.add(new FPersonA("Ruth", "Mueller"));
        personData.add(new FPersonA("Heinz", "Kurz"));
        personData.add(new FPersonA("Cornelia", "Meier"));
        personData.add(new FPersonA("Werner", "Meyer"));
        personData.add(new FPersonA("Lydia", "Kunz"));
        personData.add(new FPersonA("Anna", "Best"));
        personData.add(new FPersonA("Stefan", "Meier"));
        personData.add(new FPersonA("Martin", "Mueller"));
    }
  
    /**
     * Returns the data as an observable list of Persons. 
     * @return
     */
    public ObservableList<FPersonA> getPersonData() {
        return personData;
    }
  
    // ... THE REST OF THE CLASS ...

    private Stage primaryStage;
    private BorderPane rootLayout;
    
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("AddressApp");

        // Set the application icon.
        this.primaryStage.getIcons().add(new Image("file:resources/images/address_book_32.png"));

        initRootLayout();

        showPersonOverview();
    }
    
    /**
     * Initializes the root layout.
     */
    /**
     * Initializes the root layout and tries to load the last opened
     * person file.
     */
    public void initRootLayout() {
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(FMainAppA.class
                    .getResource("view/RootLayout.fxml"));
            rootLayout = (BorderPane) loader.load();

            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);

            // Give the controller access to the main app.
            FRootLayoutControllerA controller = loader.getController();
            controller.setMainApp(this);

            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Try to load last opened person file.
        File file = getPersonFilePath();
        if (file != null) {
            loadPersonDataFromFile(file);
        }
    }

    /**
     * Shows the person overview inside the root layout.
     */
    	public void showPersonOverview() {
    	    try {
    	        // Load person overview.
    	        FXMLLoader loader = new FXMLLoader();
    	        loader.setLocation(FMainAppA.class.getResource("view/PersonOverview.fxml"));
    	        AnchorPane personOverview = (AnchorPane) loader.load();

    	        // Set person overview into the center of root layout.
    	        rootLayout.setCenter(personOverview);

    	        // Give the controller access to the main app.
    	        FPersonOverviewControllerA controller = loader.getController();
    	        controller.setKMainAppN(this);

    	    } catch (IOException e) {
    	        e.printStackTrace();
    	    }
    	}

    /**
     * Returns the main stage.
     * @return
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
    /**
     * Opens a dialog to edit details for the specified person. If the user
     * clicks OK, the changes are saved into the provided person object and true
     * is returned.
     * 
     * @param person the person object to be edited
     * @return true if the user clicked OK, false otherwise.
     */
    public boolean showPersonEditDialog(FPersonA person) {
        try {
            // Load the fxml file and create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(FMainAppA.class.getResource("view/PersonEditDialog.fxml"));
            AnchorPane page = (AnchorPane) loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Edit Person");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            // Set the person into the controller.
            FPersonEditDialogControllerA controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setPerson(person);

            // Show the dialog and wait until the user closes it
            dialogStage.showAndWait();

            return controller.isOkClicked();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    /**
     * Returns the person file preference, i.e. the file that was last opened.
     * The preference is read from the OS specific registry. If no such
     * preference can be found, null is returned.
     * 
     * @return
     */
    public File getPersonFilePath() {
        Preferences prefs = Preferences.userNodeForPackage(FMainAppA.class);
        String filePath = prefs.get("filePath", null);
        if (filePath != null) {
            return new File(filePath);
        } else {
            return null;
        }
    }

    /**
     * Sets the file path of the currently loaded file. The path is persisted in
     * the OS specific registry.
     * 
     * @param file the file or null to remove the path
     */
    public void setPersonFilePath(File file) {
        Preferences prefs = Preferences.userNodeForPackage(FMainAppA.class);
        if (file != null) {
            prefs.put("filePath", file.getPath());

            // Update the stage title.
            primaryStage.setTitle("AddressApp - " + file.getName());
        } else {
            prefs.remove("filePath");

            // Update the stage title.
            primaryStage.setTitle("AddressApp");
        }
    }
    /**
     * Loads person data from the specified file. The current person data will
     * be replaced.
     * 
     * @param file
     */
    public void loadPersonDataFromFile(File file) {
        try {
            JAXBContext context = JAXBContext
                    .newInstance(FPersonListWrapperA.class);
            Unmarshaller um = context.createUnmarshaller();

            // Reading XML from the file and unmarshalling.
            FPersonListWrapperA wrapper = (FPersonListWrapperA) um.unmarshal(file);

            personData.clear();
            personData.addAll(wrapper.getPersons());

            // Save the file path to the registry.
            setPersonFilePath(file);

        } catch (Exception e) { // catches ANY exception
            Dialogs.create()
                    .title("Error")
                    .masthead("Could not load data from file:\n" + file.getPath())
                    .showException(e);
        }
    }
        
    /**
     * Saves the current person data to the specified file.
     * 
     * @param file
     */
    public void savePersonDataToFile(File file) {
        try {
            JAXBContext context = JAXBContext
                    .newInstance(FPersonListWrapperA.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            // Wrapping our person data.
            FPersonListWrapperA wrapper = new FPersonListWrapperA();
            wrapper.setPersons(personData);

            // Marshalling and saving XML to the file.
            m.marshal(wrapper, file);

            // Save the file path to the registry.
            setPersonFilePath(file);
        } catch (Exception e) { // catches ANY exception
            Dialogs.create().title("Error")
                    .masthead("Could not save data to file:\n" + file.getPath())
                    .showException(e);
        }
    }
    /**
     * Opens a dialog to show birthday statistics.
     */
    public void showBirthdayStatistics() {
        try {
            // Load the fxml file and create a new stage for the popup.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(FMainAppA.class.getResource("view/BirthdayStatistics.fxml"));
            AnchorPane page = (AnchorPane) loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Birthday Statistics");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            // Set the persons into the controller.
            FBirthdayStatisticsControllerA controller = loader.getController();
            controller.setPersonData(personData);

            dialogStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}