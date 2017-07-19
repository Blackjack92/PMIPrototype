/**
 * Created by rz on 14.06.17.
 */
import org.bouncycastle.cert.X509AttributeCertificateHolder;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
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
        ResultSet rs = null;
        try {
            ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("jdbc/MeinDatasourceJndiName");
            //ds = (DataSource) ctx.lookup("jdbc/MySQLDataSource");
            conn = ds.getConnection();
            stmt = conn.createStatement();
            String sql = "INSERT INTO ACCredentials " + "VALUES (" +acparam +"," +pkcparam +"," +"'"+encoded+"'" +")" + "";
            //rs = stmt.executeQuery(sql);
            stmt.executeUpdate(sql);


            while(rs.next()) {
            }
        }
        catch (SQLException se) {

        }
//        try{
//            if(stmt!=null)
//                conn.close();
//        }catch(SQLException se){
//        }
//        try{
//            if(conn!=null)
//                conn.close();
//        }catch(SQLException se){
//            se.printStackTrace();
//        }
    }

    public X509AttributeCertificateHolder selecting() {
            Connection conn = null;
            Statement stmt = null;
            X509AttributeCertificateHolder certificateHolder = null;
            try{
                //STEP 2: Register JDBC driver
                Class.forName("com.mysql.jdbc.Driver");
                //STEP 3: Open a connection
                conn = DriverManager.getConnection(DB_URL, USER, PASS);
                //STEP 4: Execute a query statement
                stmt = conn.createStatement();

                String sql = "SELECT AcSerial, PKCSerial, Certificate FROM ACCredentials";
                ResultSet rs = stmt.executeQuery(sql);
                //STEP 5: Extract data from result set
                while(rs.next()){
                    //Retrieve by column name
                    BigInteger acSerial  = BigInteger.valueOf(rs.getLong("AcSerial"));
                    BigInteger pkcSerial = BigInteger.valueOf(rs.getLong("PKCSerial"));
                    String b_encoded = rs.getString("Certificate");
                    //Display values
                    System.out.print("AcSerial: " + acSerial+"\n");
                    System.out.print("PKCSerial: " + pkcSerial +"\n");
                    System.out.print("CertificateHolder: " + b_encoded+"\n");
                    // Convert to AC object
                    byte[] data = Base64.getUrlDecoder().decode(b_encoded);
                    certificateHolder = new X509AttributeCertificateHolder(data);
                }
                rs.close();
            }catch(SQLException se){
                //Handle errors for JDBC
                se.printStackTrace();
            }catch(Exception e){
                //Handle errors for Class.forName
                e.printStackTrace();
            }finally{
                //finally block used to close resources
                try{
                    if(stmt!=null)
                        conn.close();
                }catch(SQLException se){
                }
                try{
                    if(conn!=null)
                        conn.close();
                }catch(SQLException se){
                    se.printStackTrace();
                }
            }
            return certificateHolder;
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
                while(rs.next()){
                    BigInteger lastUsed  = BigInteger.valueOf(rs.getLong("Count(*)"));
                    System.out.println(lastUsed);
                    return lastUsed.intValue() + 1;
            }
                rs.close();
            }catch(SQLException se){
                //Handle errors for JDBC
                se.printStackTrace();
            }catch(Exception e){
                //Handle errors for Class.forName
                e.printStackTrace();
            }finally{
                //finally block used to close resources
                try{
                    if(stmt!=null)
                        conn.close();
                }catch(SQLException se){
                }
                try{
                    if(conn!=null)
                        conn.close();
                }catch(SQLException se){
                    se.printStackTrace();
                }
            }

            return -1;
        }

    }
