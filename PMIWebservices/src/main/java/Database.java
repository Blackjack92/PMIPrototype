/**
 * Created by rz on 14.06.17.
 */

import org.bouncycastle.cert.X509AttributeCertificateHolder;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.IOException;
import java.math.BigInteger;
import java.sql.*;
import java.util.Base64;

public class Database {
    // JDBC driver name and database URL
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost:3306/AC?autoReconnect=true&useSSL=false";

    //  Database credentials
    static final String USER = "root";
    static final String PASS = "password";


    public void inserting(BigInteger acparam, BigInteger pkcparam, String encoded) throws NamingException {
        InitialContext ctx;
        DataSource ds;
        Connection conn = null;
        Statement stmt = null;
        try {
            ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("jdbc/MeinDatasourceJndiName");
            //ds = (DataSource) ctx.lookup("jdbc/MySQLDataSource");
            conn = ds.getConnection();
            stmt = conn.createStatement();
            String sql = "INSERT INTO ACCredentials " + "VALUES (" + acparam + "," + pkcparam + "," + "'" + encoded + "'" + "," + null +")";
            int result = stmt.executeUpdate(sql);
        } catch (SQLException se) {
            se.printStackTrace();
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }

    public String getSerialNumber(BigInteger serialnumber) throws NamingException, IOException {
        X509AttributeCertificateHolder certificateHolder = null;
        Context ctx = new InitialContext();
        DataSource ds = (DataSource) ctx.lookup("jdbc/MeinDatasourceJndiName");
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = ds.getConnection();
            stmt = conn.createStatement();
            String sql = "SELECT * FROM ACCredentials WHERE AcSerial="+ serialnumber;
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                //Retrieve by column name
                String b_encoded = rs.getString("Certificate");
                if (b_encoded == null){
                    return "Certificate not found";
                }
                // Convert to AC object
                byte[] data = Base64.getUrlDecoder().decode(b_encoded);
                certificateHolder = new X509AttributeCertificateHolder(data);
                return b_encoded;
            }
            rs.close();
        } catch (SQLException se) {
            se.printStackTrace();
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
        return null;
    }

    public String deleteCertificate(BigInteger serialnumber) throws NamingException, IOException {
        X509AttributeCertificateHolder certificateHolder = null;
        Context ctx = new InitialContext();
        DataSource ds = (DataSource) ctx.lookup("jdbc/MeinDatasourceJndiName");
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = ds.getConnection();
            stmt = conn.createStatement();
            String sql = "DELETE FROM ACCredentials WHERE AcSerial="+ serialnumber;
            int result = stmt.executeUpdate(sql);
            if (result == 1 ) {
                insertrevokeserialnumber(serialnumber);
                return "Succesfully deleted";
            }else {
                return "No AttributeCertificate found";
            }
        } catch (SQLException se) {
            se.printStackTrace();
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
        return null;
    }

    public void insertrevokeserialnumber(BigInteger serialnumber) throws NamingException {
        InitialContext ctx;
        DataSource ds;
        Connection conn = null;
        Statement stmt = null;
        try {
            ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("jdbc/MeinDatasourceJndiName");
            //ds = (DataSource) ctx.lookup("jdbc/MySQLDataSource");
            conn = ds.getConnection();
            stmt = conn.createStatement();
            String sql = "INSERT INTO ACRevoked " + "VALUES (" + serialnumber + "," + null +")";
            int result = stmt.executeUpdate(sql);
        } catch (SQLException se) {
            se.printStackTrace();
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }
    public static int GetNextFreeSerialNumber() throws ClassNotFoundException, SQLException {
        Connection conn = null;
        Statement stmt = null;
        String sql = "SELECT COUNT(*) FROM ACCredentials";
        try {
            //STEP 2: Register JDBC driver
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                BigInteger lastUsed = BigInteger.valueOf(rs.getLong("Count(*)"));
                System.out.println(lastUsed);
                return lastUsed.intValue() + 1;
            }
            rs.close();
        } catch (SQLException se) {
            //Handle errors for JDBC
            se.printStackTrace();
        } catch (Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();
        } finally {
            //finally block used to close resources
            try {
                if (stmt != null)
                    conn.close();
            } catch (SQLException se) {
            }
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }

        return -1;
    }

}
