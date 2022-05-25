package com.mycompany.empresaropa;

import com.mycompany.empresaropa.entities.Marca;
import com.mycompany.empresaropa.entities.Prenda;
import java.io.IOException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javax.persistence.Query;

public class PrimaryController implements Initializable{

    private Prenda prendaSeleccionada;
    private Marca MarcaSeleccionada;
    NumberFormat formatoimporte;
    @FXML
    private TextField textM;
    @FXML
    private TextField textT;
    @FXML
    private TableView<Prenda> tableRopa;
    @FXML
    private TableColumn<Prenda, String> colMarca;
    @FXML
    private TableColumn<Prenda, String> colPrenda;
    @FXML
    private TableColumn<Prenda, String> colColor;
    @FXML
    private TableColumn<Prenda, String> colPrecio;
    @FXML
    private TextField textFieldBuscar;
    @FXML
    private CheckBox checkBoxCoincide;
    
    

    private void switchToSecondary() throws IOException {
        App.setRoot("secondary");
    }
    
    public void initialize(URL url, ResourceBundle rb) {
        colPrenda.setCellValueFactory(new PropertyValueFactory<>("tipoPrenda"));
        formatoimporte = NumberFormat.getCurrencyInstance();
        //colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));     
        colPrecio.setCellValueFactory(                 
                cellData -> {                     
                    SimpleStringProperty property = new SimpleStringProperty();
                    if (cellData.getValue().getPrecio() != null) {
                        property.setValue(formatoimporte.format(cellData.getValue().getPrecio()));                     
                    }                     
                    return property;
                });
        colColor.setCellValueFactory(new PropertyValueFactory<>("color"));                  
        colMarca.setCellValueFactory(                 
                cellData -> {                     
                    SimpleStringProperty property = new SimpleStringProperty();
                    if (cellData.getValue().getMarca() != null) {
                        property.setValue(cellData.getValue().getMarca().getNombre());                     
                    }                     
                    return property;
                });
        tableRopa.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    prendaSeleccionada = newValue;
                        if (prendaSeleccionada != null){
                            textT.setText(prendaSeleccionada.getTipoPrenda());
                            textM.setText(prendaSeleccionada.getMarca().getNombre());
                        }else{
                            textT.setText("");
                            textM.setText("");
                        }
                });
        
        cargarTodasLasMarcas();
    }
    private void cargarTodasLasMarcas() {     
        Query queryPrendaFindAll = App.em.createNamedQuery("Prenda.findAll");
        List<Prenda> listPrenda = queryPrendaFindAll.getResultList();
        tableRopa.setItems(FXCollections.observableArrayList(listPrenda));
    }

    @FXML
    private void onActionButtonNuevo(ActionEvent event) {
        try{
            App.setRoot("secondary");
            SecondaryController secondaryController = (SecondaryController)App.fxmlLoader.getController();
            prendaSeleccionada = new Prenda();
            secondaryController.setPrenda(prendaSeleccionada, true);
        }catch(IOException ex){
            Logger.getLogger(PrimaryController.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        
    }

    @FXML
    private void onActionEditar(ActionEvent event) {
        if(prendaSeleccionada != null){
            try{
            App.setRoot("secondary");
            SecondaryController secondaryController = (SecondaryController)App.fxmlLoader.getController();
            secondaryController.setPrenda(prendaSeleccionada, false);
            }catch(IOException ex){
            Logger.getLogger(PrimaryController.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            }
        }else{
            Alert alert = new Alert (Alert.AlertType.INFORMATION);
            alert.setTitle("Atencion");
            alert.setHeaderText("Debes seleccionar un dato registro");
            alert.showAndWait();
        }
    }

    @FXML
    private void onActionEliminar(ActionEvent event) {
        if(prendaSeleccionada != null){
            Alert alert = new Alert (Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmar");
            alert.setHeaderText("¿Desea eliminar los datos seleccionados?");
            alert.setContentText(prendaSeleccionada.getMarca().getNombre() +  " " 
                    + prendaSeleccionada.getTipoPrenda());
            Optional<ButtonType> result = alert.showAndWait();
            if(result.get() == ButtonType.OK){
                App.em.getTransaction().begin();
                App.em.remove(prendaSeleccionada);
                App.em.getTransaction().commit();
                tableRopa.getItems().remove(prendaSeleccionada);
                tableRopa.getFocusModel().focus(null);
            }
        }else{
            Alert alert = new Alert (Alert.AlertType.INFORMATION);
            alert.setTitle("Atencion");
            alert.setHeaderText("Debes elegir un dato minimo");}
    }

    @FXML
    private void onActionGuardar(ActionEvent event) {
        if (prendaSeleccionada != null){
            prendaSeleccionada.setTipoPrenda(textT.getText());
            prendaSeleccionada.getMarca().setNombre(textM.getText());
            App.em.getTransaction().begin();
            App.em.merge(prendaSeleccionada);
            App.em.getTransaction().commit();
            
            int numFilaSeleccionada = tableRopa.getSelectionModel().getSelectedIndex();
            tableRopa.getItems().set(numFilaSeleccionada, prendaSeleccionada);
            TablePosition pos = new TablePosition(tableRopa, numFilaSeleccionada, null);
            tableRopa.getFocusModel().focus(pos);
            tableRopa.requestFocus();
        }    
    }

    @FXML
    private void onActionButtonBuscar(ActionEvent event) {
         if (!textFieldBuscar.getText().isEmpty()){
            if(checkBoxCoincide.isSelected()){
                Query queryMarcaFindByNombre = App.em.createNamedQuery("Marca.findByNombre");
                queryMarcaFindByNombre.setParameter("marca", textFieldBuscar.getText());
                List<Prenda> listPrenda = queryMarcaFindByNombre.getResultList();
                tableRopa.setItems(FXCollections.observableArrayList(listPrenda));
            } else {
                String strQuery = "SELECT * FROM Marca WHERE LOWER(nombre) LIKE ";
                strQuery += "\'%" + textFieldBuscar.getText().toLowerCase() + "%\'";
                Query queryMarcaFindLikeNombre = App.em.createNativeQuery(strQuery, Prenda.class);

                List<Prenda> listPrenda = queryMarcaFindLikeNombre.getResultList();
                tableRopa.setItems(FXCollections.observableArrayList(listPrenda));
            }            
        } else {
            cargarTodasLasMarcas();
        }
    }
}
