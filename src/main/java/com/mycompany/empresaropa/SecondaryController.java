package com.mycompany.empresaropa;

import com.mycompany.empresaropa.entities.Marca;
import com.mycompany.empresaropa.entities.Prenda;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.text.NumberFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;
import javax.persistence.Query;
import javax.persistence.RollbackException;

public class SecondaryController implements Initializable{
    
    private Prenda prenda;
    private boolean nuevaPrenda;
    private boolean errorFormato;
    private static final String CARPETA_FOTOS = "Fotos";
    @FXML
    private TextField textFieldPrenda;
    @FXML
    private TextField textFieldColor;
    @FXML
    private TextField textFieldPrecio;
    @FXML
    private DatePicker datePickerFecha;
    @FXML
    private CheckBox checkBoxColeccion;
    @FXML
    private ComboBox<Marca> comboBoxMarca;
    @FXML
    private ImageView imgViewFoto;
    @FXML
    private BorderPane rootSecondary;
   
    
    @Override
    public void initialize(URL url, ResourceBundle rb){   
    }
    
    public void setPrenda(Prenda prenda, boolean nuevaPrenda){
        App.em.getTransaction().begin();
        if(!nuevaPrenda){
            this.prenda = App.em.find(Prenda.class, prenda.getId());
        }else{
            this.prenda = prenda;
        }
        this.nuevaPrenda = nuevaPrenda;
        mostrarDatos();
    }
    
    private void mostrarDatos(){
        //textFieldMarca.setText(prenda.getMarca().getNombre());
        
        Query queryMarcaFindAll = App.em.createNamedQuery("Marca.findAll");
        List<Marca> listMarca = queryMarcaFindAll.getResultList();  
        
        comboBoxMarca.setItems(FXCollections.observableList(listMarca));
       System.out.println(prenda.getMarca());
        if (prenda.getMarca()!= null){
            comboBoxMarca.setValue(prenda.getMarca());       
        }
        
        comboBoxMarca.setCellFactory((ListView<Marca> m) -> new ListCell<Marca>() {
        @Override
            protected void updateItem(Marca marca, boolean empty){
                super.updateItem(marca, empty);
                if(marca == null || empty){
                    setText("");
                }else {
                    setText(marca.getNombre());
                }
            }
        });
        comboBoxMarca.setConverter(new StringConverter<Marca>(){
         @Override
            public String toString (Marca marca){
                if(marca == null){
                    return null;
                }else {
                    return marca.getNombre();
                }
            }
            @Override
            public Marca fromString(String userId) {
                return null;
            }
        });
        
        if(prenda.getFoto() != null){
            String imageFileName = prenda.getFoto();
            File file = new File(CARPETA_FOTOS + "/" + imageFileName);
            if (file.exists()){
                Image image = new Image(file.toURI().toString());
                imgViewFoto.setImage(image);
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "No se encuentra la imágen");
                alert.showAndWait();
            }
        }
        
        textFieldPrenda.setText(prenda.getTipoPrenda());
        textFieldColor.setText(prenda.getColor());
        
        if(prenda.getPrecio() != null){            
            textFieldPrecio.setText(String.valueOf(prenda.getPrecio()));
        }
        if(prenda.getNuevaColeccion() != null){
            checkBoxColeccion.setSelected(prenda.getNuevaColeccion());}
        
        if(prenda.getFechaCreacion() != null){
            Date date = prenda.getFechaCreacion();
            Instant instant = date.toInstant();
            ZonedDateTime zdt = instant.atZone(ZoneId.systemDefault());
            LocalDate localDate = zdt.toLocalDate();
            datePickerFecha.setValue(localDate);
        }
    }

    private void switchToPrimary() throws IOException {
        App.setRoot("primary");
    }

    @FXML
    private void onActionEliminarF(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar supresion en imagen");
        alert.setHeaderText("¿Desea SUPRIMIR el archivo asociado a la imagen, \n"
                + "quitar la foto pero MANTENER el archivo, \no CANCELAR la operación?");
        alert.setContentText("Elija a opción deseada");
        
        ButtonType buttonTypeEiminar = new ButtonType("Suprimir");
        ButtonType buttonTypeMantener = new ButtonType("Mantener");
        ButtonType buttonTypeCancel = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        
        alert.getButtonTypes().setAll(buttonTypeEiminar, buttonTypeMantener, buttonTypeCancel);
        
        Optional<ButtonType> result = alert.showAndWait();
        
        if(result.get() == buttonTypeEiminar) {
            String imageFileName = prenda.getFoto();
            File file = new File(CARPETA_FOTOS + "/" + imageFileName);
            if(file.exists()){
                file.delete();
            }
            prenda.setFoto(null);
            imgViewFoto.setImage(null);
        } else if (result.get() == buttonTypeMantener){            
            prenda.setFoto(null);
            imgViewFoto.setImage(null);
        }
    }

    @FXML
    private void onActionEx(ActionEvent event) throws IOException {
        File carpertaFotos = new File(CARPETA_FOTOS);
        if(!carpertaFotos.exists()){
            carpertaFotos.mkdir();
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar imagen");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Imágenes (jpg, png)", "*.png"),
                 new FileChooser.ExtensionFilter("Todos los archivos", ".")
        );
        File file = fileChooser.showOpenDialog(rootSecondary.getScene().getWindow());
        if (file != null){
            try{
                Files.copy(file.toPath(), new File(CARPETA_FOTOS + "/" + file.getName()).toPath());
                prenda.setFoto(file.getName());
                Image image = new Image(file.toURI().toString());
                imgViewFoto.setImage(image);
            } catch (FileAlreadyExistsException ex){
                Alert alert = new Alert(Alert.AlertType.WARNING,"Nombre de archivo duplicado");
                alert.showAndWait();
            }
        }
    }
    

    @FXML
    private void onActionC(ActionEvent event) {
        App.em.getTransaction().rollback();
        
        try{
            App.setRoot("primary");
        }catch (IOException ex){
            Logger.getLogger(SecondaryController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void onActionGuardar2(ActionEvent event) {
        errorFormato = false;
        
        prenda.setMarca(comboBoxMarca.getValue());     
        prenda.setTipoPrenda(textFieldPrenda.getText());
        prenda.setColor(textFieldColor.getText());
        
        if(!textFieldPrecio.getText().isEmpty()){
            try{
                prenda.setPrecio(BigDecimal.valueOf(Double.valueOf(textFieldPrecio.getText()).doubleValue()));
            }catch(NumberFormatException ex){
                errorFormato = true;
                Alert alert = new Alert(Alert.AlertType.INFORMATION,"Salario No valido");
                alert.showAndWait();
                textFieldPrecio.requestFocus();
            }
        }
        prenda.setNuevaColeccion(checkBoxColeccion.isSelected());
        if(datePickerFecha.getValue() != null){
            LocalDate localDate = datePickerFecha.getValue();
            ZonedDateTime zoneDateTime = localDate.atStartOfDay(ZoneId.systemDefault());
            Instant instant = zoneDateTime.toInstant();
            Date date = Date.from(instant);
            prenda.setFechaCreacion(date);
        }else{
            prenda.setFechaCreacion(null);
        }
        if (!errorFormato){
            try{
                if(prenda.getId()== null){
                    System.out.println("Guardada las nuevas prenda en BD");
                    App.em.persist(prenda);
                }else{
                    System.out.println("Actualizada la prenda en BD");
                    App.em.merge(prenda);
                }
                App.em.getTransaction().commit();
                
                App.setRoot("primary");
            }catch(RollbackException ex){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("No se ha `podido guardar los datos. "        
                        + "Compruebe que los datos cumplen los requisitos");
                alert.setContentText(ex.getLocalizedMessage());
                alert.showAndWait();
            }catch(IOException ex){
                Logger.getLogger(SecondaryController.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
    }
}