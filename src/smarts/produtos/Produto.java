package smarts.produtos;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import smarts.alert.AlertMaker;
import smarts.main.Cliente;
import smarts.database.DatabaseHandler;
import smarts.storeinterface.InterfaceController;

public class Produto {

    DatabaseHandler databaseHandler;

    Random rand = new Random();
    Random rand2 = new Random();
    Random rand3 = new Random();

    public ArrayList<Double> receitaTotal;
    public final int defaultStock = 20;
    public int n = rand.nextInt(16);

    /////////////////////////////////////////////////////////////////////////
    private final int id[] = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17};
    private final String descricao[] = {"Pizza", "Maca", "Salsicha", "Ketchup", "Cerelac", "Frango", "Robalo", "Nuggets", "Pao", "Cuecas", "Tshirt", "Peru", "Alho", "Banana", "Batata", "Cola", "IceTea"};
    private final double val[] = {2.50, 0.6, 1.2, 2.0, 3.2, 4.4, 5.1, 1.4, 0.2, 6.1, 15.9, 8.7, 1.8, 2.3, 0.9, 1.2, 1.2};
    private final int quantidade[] = {20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20};
    /////////////////////////////////////////////////////////////////////////

    public Produto() {
        receitaTotal = new ArrayList();
    }
    public void ItemList() {
        databaseHandler = DatabaseHandler.getInstance();
        for (int i = 0; i < descricao.length; i++) {
            String str = "INSERT INTO PRODUTO VALUES ("
                    + "'" + id[i] + "',"
                    + "'" + descricao[i] + "',"
                    + "'" + val[i] + "',"
                    + "'" + quantidade[i] + "')";

            if (databaseHandler.execAction(str)) {
                //AlertMaker.showSimpleAlert("Success!", "Database created!");
            } else {
                AlertMaker.showErrorMessage("Error!", "Database already created");
                return;
            }          
        }
        AlertMaker.showSimpleAlert("Success!", "Database created!");
    }

    public void checkData() {
        databaseHandler = DatabaseHandler.getInstance();
        String qu = "SELECT * FROM PRODUTO";
        ResultSet rs = databaseHandler.execQuery(qu);
        try {
            while (rs.next()) {
                String lotexx = rs.getString("id");
                String datainx = rs.getString("descricao");
                String datainx2 = rs.getString("valor");
                String datainx3 = rs.getString("quantidade");
                System.out.println("ID: " + lotexx);
                System.out.println("Nome: " + datainx);
                System.out.println("Valor: " + datainx2);
                System.out.println("Quantidade: " + datainx3);

            }
        } catch (SQLException ex) {
        }
    }

    public void loop(Cliente cliente) {

        int n2 = rand2.nextInt(10) + 1;
        int x = 0;
        String carrinho = "\nCliente : " + cliente.name + "\nID\tDescriçao\t\tValor \n -------------------------------------\n";
        ArrayList<Double> preco = new ArrayList<>();
        databaseHandler = DatabaseHandler.getInstance();

        while (x < n2) {
            int n3 = rand3.nextInt(17) + 1;

            String qu = "SELECT * FROM PRODUTO WHERE id = '" + n3 + "'";
            ResultSet rs = databaseHandler.execQuery(qu);
            try {
                while (rs.next()) {
                    String prodID = rs.getString("id");
                    String prodDesc = rs.getString("descricao");
                    String prodPreco = rs.getString("valor");
                    String prodQuant = rs.getString("quantidade");
                    int Quant = Integer.parseInt(prodQuant);
                    carrinho = carrinho + "\n" + prodID + "\t" + prodDesc + "\t\t\t" + prodPreco + " €.";
                    double foo = Double.parseDouble(prodPreco);
                    preco.add(foo);
                    receitaTotal.add(foo);
                    int n = rand.nextInt(16);
                    String n3String = String.valueOf(n3);
                    if (Quant < 7) {
                        databaseHandler.updateEncomenda(defaultStock, n3String);
                        System.out.println("O SISTEMA ENCOMENDOU: " + prodDesc);
                        Platform.runLater(() -> {
                            InterfaceController.names.add("O SISTEMA ENCOMENDOU: " + prodDesc);
                        });
                        Quant = defaultStock;
                    }
                    databaseHandler.updateQuantidade(Quant, n3String);
                    x += 1;
                }

            } catch (SQLException ex) {
                Logger.getLogger(Produto.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

        
        System.out.println(cliente.getCompra());
        double sum = 0;
        for (double d : preco) {
            sum += d;
        }

        DecimalFormat df = new DecimalFormat("###.##");
        //System.out.println(carrinho);
        //System.out.println("TOTAL = " + sum);

        String foo2 = String.valueOf(df.format(sum));
        try (FileWriter writer2 = new FileWriter("D:\\Eng. Informatica\\Trabalhos Miguel\\Java\\SmartS\\SmartS\\src\\smarts\\database\\factura.txt", true);
                BufferedWriter bw = new BufferedWriter(writer2)) {
            bw.write("=============================================================================");
            bw.write(carrinho + "\n\n\tTotal = " + foo2 + " €.\n");
            cliente.setCompra(carrinho + "\n\n\tTotal = " + foo2 + " €.\n");

        } catch (IOException e) {
            System.err.format("IOException: %s%n", e);
        }
    }

    public String facturaTotal() {
        double sum2 = 0;
        for (Double d : receitaTotal) {
            sum2 += d;
        }
        DecimalFormat df = new DecimalFormat("###.##");
        String foo3 = String.valueOf(df.format(sum2));
        try (FileWriter writer2 = new FileWriter("D:\\Eng. Informatica\\Trabalhos Miguel\\Java\\SmartS\\SmartS\\src\\smarts\\database\\factura.txt", true);
                BufferedWriter bw = new BufferedWriter(writer2)) {
            bw.write("\n\n\tTotal Faturado no dia = " + foo3 + " €.\n");

        } catch (IOException e) {
            System.err.format("IOException: %s%n", e);
        }
        return "Total Faturado no dia = " + foo3 + " €."; 
    }
}
