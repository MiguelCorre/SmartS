package smarts.storeinterface;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import smarts.alert.AlertMaker;
import smarts.main.Cliente;
import smarts.database.DatabaseHandler;
import smarts.main.NaiveStoreAlgorithm;
import smarts.main.StoreController;
import smarts.produtos.Produto;

public class InterfaceController implements Initializable {

    public static final ObservableList names = FXCollections.observableArrayList();

    @FXML
    private JFXListView<?> listView;
    @FXML
    public JFXComboBox<String> comboBox;
    @FXML
    public JFXTextField nClientes;
    @FXML
    private JFXButton clienteButton;
    @FXML
    private ListView<?> listView2;
    @FXML
    private JFXButton clienteButton1;
    @FXML
    private JFXListView<?> listView3;

    Produto prod;
    NaiveStoreAlgorithm algo;
    StoreController controller;
    DatabaseHandler databaseHandler;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    @FXML
    private void handleStockButton(ActionEvent event) {
        databaseHandler = DatabaseHandler.getInstance();
        ObservableList names3 = FXCollections.observableArrayList();
        listView3.setItems(names3);
        String qu = "SELECT * FROM PRODUTO";
        ResultSet rs = databaseHandler.execQuery(qu);
        try {
            while (rs.next()) {
                String datainx1 = rs.getString("id");
                String datainx2 = rs.getString("descricao");
                String datainx3 = rs.getString("valor");
                String datainx4 = rs.getString("quantidade");
                names3.add("ID: " + datainx1 + "\nNome: " + datainx2 + "\nValor: " + datainx3 + "\nQuantidade: " + datainx4);
                names3.add("================================================");

            }
        } catch (SQLException ex) {
        }
    }

    @FXML
    private void handleCreateButton(ActionEvent event) {
        prod = new Produto();
        prod.ItemList();
    }

    @FXML
    private void handleClienteButton(ActionEvent event) {

        String nClientesString = nClientes.getText();
        if (nClientesString.isEmpty()) {
            AlertMaker.showErrorMessage("Nº de clientes inválido", "Por favor insira um número de clientes válido");
            return;
        }
        int clientCount = Integer.parseInt(nClientesString);

        algo = new NaiveStoreAlgorithm();
        controller = new StoreController(clientCount, algo);

        listView.setItems(names);
        names.add("\t\t\t\t\t\t\t\tA LOJA ABRIU");

        int i = 0;

        ExecutorService exec = Executors.newFixedThreadPool(clientCount);
        new Thread(() -> {
            DatabaseHandler.getInstance();
        }).start();

        while (i <= clientCount) {
            exec.execute(controller);
            i++;
        }
        exec.shutdown();
    }

    @FXML
    private void handleTesteButton(ActionEvent event) {
        comboBox.getItems().clear();

        if (names.isEmpty()) {
            AlertMaker.showErrorMessage("Erro a ler os dados!", "Algo correu mal!");
            return;
        } else {
            AlertMaker.showSimpleAlert("Sucesso!", "Dados foram lidos com sucesso!");
            comboBox.getItems().add("Todos");
            for (Iterator it = controller.allPeopleList.iterator(); it.hasNext();) {
                Cliente next = (Cliente) it.next();
                comboBox.getItems().add(next.name);
            }
            return;
        }
    }

    @FXML
    private void handleTesteButton2(ActionEvent event) {
        if (comboBox.getItems().isEmpty()) {
            AlertMaker.showErrorMessage("ComboBox vazia!", "Verifique se existem clientes!");
            return;
        }
        ObservableList names2 = FXCollections.observableArrayList();
        listView2.setItems(names2);
        String nomeClienteBox = comboBox.getEditor().getText();
        for (Iterator it = controller.allPeopleList.iterator(); it.hasNext();) {
            Cliente next = (Cliente) it.next();
            if (next.name.equals(nomeClienteBox)) {
                names2.add("================================================");
                names2.add(next.getCompra());
                names2.add("================================================");
            }
            if (nomeClienteBox.equals("Todos")) {
                names2.add("================================================");
                names2.add(next.getCompra());
            }
        }
    }

}
