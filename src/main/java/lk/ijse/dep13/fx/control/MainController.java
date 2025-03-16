package lk.ijse.dep13.fx.control;

import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lk.ijse.dep13.fx.TextSearch;
import lk.ijse.dep13.fx.util.AppRouter;

import java.io.*;
import java.util.List;
import java.util.Optional;

public class MainController {
    public MenuBar mnBar;
    public Menu mnFile;
    public MenuItem mnItemNew;
    public MenuItem mnItemOpen;
    public MenuItem mnItemSave;
    public MenuItem mnItemSaveAs;
    public MenuItem mnItemExit;
    public AnchorPane root;
    public TextArea txtArea;
    public MenuItem mnItemAbout;
    public TextField txtSearch;
    public Spinner spnrSearch;
    public ComboBox cmbBoxFont;
    public ToggleButton tglbtnBold;
    public ToggleButton tglbtnItalic;
    public ToggleButton tglbtnUnderline;
    public CheckMenuItem chkmnItemBold;
    public CheckMenuItem chkmnItemItalic;
    public CheckMenuItem chkmnItemUnderline;
    public ComboBox cmbFontSize;

    private File currentFile;
    private final SimpleBooleanProperty updateValue = new SimpleBooleanProperty(false);

    public void initialize() {
        updateValue.addListener((observable, oldValue, newValue) -> {
           if (newValue) {
               if (!getTitle().endsWith("*")) setTitle(getTitle() + "*");
           } else{
               if (getTitle().endsWith("*")) setTitle(getTitle().substring(0, getTitle().length() - 2));
           }
        });

        txtArea.textProperty().addListener((observable, oldValue, newValue) -> {
           updateValue.set(true);
        });

        chkmnItemBold.selectedProperty().bindBidirectional(tglbtnBold.selectedProperty());
        chkmnItemItalic.selectedProperty().bindBidirectional(tglbtnItalic.selectedProperty());
        chkmnItemUnderline.selectedProperty().bindBidirectional(tglbtnUnderline.selectedProperty());

        ChangeListener subscriber = (observable, oldValue, newValue) -> {fontChanger();};
        tglbtnBold.selectedProperty().addListener(subscriber);
        tglbtnItalic.selectedProperty().addListener(subscriber);
        tglbtnUnderline.selectedProperty().addListener(subscriber);
        cmbFontSize.valueProperty().addListener(subscriber);
        cmbBoxFont.valueProperty().addListener(subscriber);

        cmbBoxFont.setItems(FXCollections.observableArrayList(Font.getFamilies()));
        cmbBoxFont.getSelectionModel().select("System");

        cmbFontSize.setItems(FXCollections.observableArrayList(
                "8", "9", "10", "11", "12", "14", "16", "18", "20", "22", "24", "26", "28", "30", "32", "36", "40", "48", "56", "64", "72"
        ));
        cmbFontSize.getSelectionModel().select("12");

        Platform.runLater(() -> {
            Stage stage = (Stage) root.getScene().getWindow();
            stage.setOnCloseRequest(event -> {
                if (updateValue.get()) {
                    Optional<ButtonType> buttonType = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to exit?\nAny unsaved changes will be discarded", ButtonType.YES, ButtonType.NO).showAndWait();
                    if (buttonType.get() == ButtonType.NO) event.consume();
                }
            });
        });
    }

    private String getTitle(){
        Stage stage = (Stage) root.getScene().getWindow();
        return stage.getTitle();
    }

    private void setTitle(String title) {
        Stage stage = (Stage) root.getScene().getWindow();
        stage.setTitle(title);
    }

    public void mnItemNewOnAction(ActionEvent actionEvent) {
        if (updateValue.get() && !txtArea.getText().isEmpty()){
            if (showConfirmationAlert("Confirmation","Do you want to create a new text file?")){
                txtArea.clear();
                setTitle("SwiftEdit - New Document");
                updateValue.set(false);
            } else txtArea.requestFocus();
        }
    }

    public void mnItemOpenOnAction(ActionEvent actionEvent) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Text File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text File", "*.txt"));
        File file = fileChooser.showOpenDialog(mnBar.getScene().getWindow());
        if (file != null) {
            FileInputStream fis = new FileInputStream(file);
            byte[] bytes = new byte[fis.available()];
            fis.read(bytes);
            String text = new String(bytes);
                if (!(txtArea.getText().isEmpty())) {
                    if (showConfirmationAlert("Confirmation","Do you want to replace textArea with this file?")){
                        txtArea.clear();
                        txtArea.setText(txtArea.getText() + text);
                        setTitle("SwiftEdit - " + file.getName());
                    } else return;
                } else txtArea.setText(txtArea.getText() + text);
            fis.close();
            }
    }

    public void mnItemSaveOnAction(ActionEvent actionEvent) throws IOException {
        if (currentFile == null) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Text File");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text File (*.txt)", "*.txt"));
            currentFile = fileChooser.showSaveDialog(root.getScene().getWindow());

            if (currentFile != null) {
                if (!currentFile.getName().endsWith(".txt")) {
                    currentFile = new File(currentFile.getAbsolutePath() + ".txt");
                }
                setTitle("SwiftEdit - " + currentFile.getName());
            } else {
                return;
            }
        }
        saveFile(currentFile);
        updateValue.set(false);
    }

    private void saveFile(File file) {
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(txtArea.getText().getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to save the file, try again").show();
        }
    }

    public void mnItemSaveAsOnAction(ActionEvent actionEvent) throws IOException {
        if (!txtArea.getText().isEmpty()) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Text File");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
            File file = fileChooser.showSaveDialog(root.getScene().getWindow());
            if (!file.getName().endsWith(".txt")) file = new File(file.getAbsolutePath() + ".txt");
            saveFile(file);
            if (currentFile != null) {
                currentFile = file;
                updateValue.set(false);
                setTitle("SwiftEdit - " + currentFile.getName());
            }
        }
    }

    public void mnItemExitOnAction(ActionEvent actionEvent) {
        if (!txtArea.getText().isEmpty()){
            if (showConfirmationAlert("Confirmation","Do you want close editor without saving this file?")){
                Platform.exit();
            } else txtArea.requestFocus();
        } else Platform.exit();
    }

    // Drag Events
    public void txtAreaOnDragOver(DragEvent dragEvent) {
        if (dragEvent.getDragboard().hasFiles()) {
            dragEvent.acceptTransferModes(TransferMode.ANY);
        }
    }

    public void txtAreaOnDragDropped(DragEvent dragEvent) throws IOException {
        List<File> files = dragEvent.getDragboard().getFiles();
        if (files.size() > 0) {
            FileInputStream fis = new FileInputStream(files.get(0));
            byte[] bytes = new byte[fis.available()];
            fis.read(bytes);
            String text = new String(bytes);
            if ((txtArea.getText().isEmpty())) {
                txtArea.setText(text);
            } else {
                if (showConfirmationAlert("Confirmation","Do you want to replace textArea with this file?")){
                    txtArea.clear();
                    txtArea.setText(txtArea.getText() + text);
                } else {
                    txtArea.requestFocus();
                }
            }
            fis.close();
        }
    }
    private boolean showConfirmationAlert(String title, String headerText) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);
        return alert.showAndWait().get() == ButtonType.OK;
    }

    public void mnItemAboutOnAction(ActionEvent actionEvent) throws IOException {
        Stage stage = new Stage(StageStyle.UTILITY);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.centerOnScreen();
        stage.resizableProperty().setValue(false);
        Scene scene = new Scene(AppRouter.getContainer(AppRouter.Routes.ABOUT));
        stage.setScene(scene);
        stage.show();
    }

    public void txtSearchOnAction(ActionEvent actionEvent) {
        String searchText = txtSearch.getText().trim();
        String textAreaContent = txtArea.getText();

        if (textAreaContent.isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Text area is empty, nothing to search", ButtonType.OK).show();
            return;
        }
        if (searchText.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Please enter a search term", ButtonType.OK).show();
            return;
        }
        TextSearch textSearch = new TextSearch(txtArea, spnrSearch);

        textSearch.search(txtSearch.getText());

        int count = textSearch.getOccurrenceCount();

        new Alert(Alert.AlertType.INFORMATION,"found " + count + " words", ButtonType.OK).show();
    }

    public void fontChanger() {
        String selectedFont = (String) cmbBoxFont.getSelectionModel().getSelectedItem();
        String selectedSize = (String) cmbFontSize.getSelectionModel().getSelectedItem();

        if (selectedFont == null) selectedFont = "System"; // Default font
        if (selectedSize == null) selectedSize = "12"; // Default size

        double fontSize = Double.parseDouble(selectedSize);

        // Apply font and styles to selected text
        txtArea.setStyle("%s; %s; %s; -fx-font-family: '%s'; -fx-font-size: %dpx;".formatted(
                chkmnItemBold.isSelected() ? "-fx-font-weight: bold" : "-fx-font-weight: normal",
                chkmnItemItalic.isSelected() ? "-fx-font-style: italic" : "-fx-font-style: normal",
                chkmnItemUnderline.isSelected() ? "-fx-underline: true" : "-fx-underline: false",
                selectedFont, (int) fontSize
        ));
    }
}
