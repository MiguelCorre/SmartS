
package smarts.database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JOptionPane;

/**
 *
 * @author Miguel HPC
 */
public class DatabaseHandler {

    public static DatabaseHandler handler;

    private static String DB_URL = "jdbc:derby:database;create=true";
    private static Connection conn = null;
    private static Statement stmt = null;

    public DatabaseHandler() {
        createConnection();
        setupProdutoTable();

    }

    public static DatabaseHandler getInstance() {
        if (handler == null) {
            handler = new DatabaseHandler();
        }
        return handler;
    }

    void createConnection() {
        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
            conn = DriverManager.getConnection(DB_URL);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ResultSet execQuery(String query) {
        ResultSet result;
        try {
            stmt = conn.createStatement();
            result = stmt.executeQuery(query);
        } catch (SQLException ex) {
            System.out.println("Exception at execQuery:dataHandler" + ex.getLocalizedMessage());
            return null;
        } finally {
        }
        return result;
    }

    public boolean execAction(String qu) {
        try {
            stmt = conn.createStatement();
            stmt.execute(qu);
            return true;
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error:" + ex.getMessage(), "Error Occured", JOptionPane.ERROR_MESSAGE);
            System.out.println("Exception at execQuery:dataHandler" + ex.getLocalizedMessage());
            return false;
        } finally {
        }
    }

    void setupProdutoTable() {
        String TABLE_NAME = "PRODUTO";
        try {
            stmt = conn.createStatement();
            DatabaseMetaData dbm = conn.getMetaData();
            ResultSet tables = dbm.getTables(null, null, TABLE_NAME.toUpperCase(), null);
            if (tables.next()) {
                System.out.println("Table " + TABLE_NAME + " already exists. Ready to go!");
            } else {
                stmt.execute("CREATE TABLE " + TABLE_NAME + "("
                        + "     id varchar(200) primary key, \n"
                        + "     descricao varchar(200), \n"
                        + "     valor varchar(200), \n"
                        + "     quantidade varchar(200)"
                        + "  )");
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage() + " ... setupDatabase");
        } finally {
        }
    }

    public boolean updateQuantidade(int quant, String id) {
        try {
            String update = "UPDATE PRODUTO SET quantidade=? WHERE ID=?";
            PreparedStatement stmt = conn.prepareStatement(update);
            int quant2 = quant - 1;
            String quantString = String.valueOf(quant2);
            stmt.setString(1, (quantString));
            stmt.setString(2, (id));
            int res = stmt.executeUpdate();
            return (res > 0);
        } catch (SQLException ex) {
        }
        return false;
    }

    public boolean updateEncomenda(int defaultStock, String id) {
        try {
            String update = "UPDATE PRODUTO SET quantidade=? WHERE ID=?";
            PreparedStatement stmt = conn.prepareStatement(update);
            String defaultStocks = String.valueOf(defaultStock);
            stmt.setString(1, (defaultStocks));
            stmt.setString(2, (id));
            int res = stmt.executeUpdate();
            return (res > 0);
        } catch (SQLException ex) {
        }

        return false;
    }
    
}
