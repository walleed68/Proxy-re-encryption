/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package myserverjava;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.DriverManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.sql.ResultSet;


import net.sf.ntru.encrypt.EncryptionKeyPair;
import net.sf.ntru.encrypt.EncryptionParameters;
import net.sf.ntru.encrypt.NtruEncrypt;





/**
 *
 * @author walle
 */
public class MyServerJava {
    static Socket s;
    static ServerSocket ss;
    static InputStreamReader isr;
    static BufferedReader br;
    static String Username;
    

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws SQLException {
        // TODO code application logic here
    
        
        
        try{
           //socket connection input 
            ss = new ServerSocket(6000);
            s = ss.accept();
            isr = new InputStreamReader(s.getInputStream());
            br = new BufferedReader(isr);
            Username = br.readLine();
            PrintWriter writer; 
            if(Username.substring(Username.length() - 1).equals("1")){
                
                
                  long start = System.currentTimeMillis();
                
                // create an instance of NtruEncrypt with a standard parameter set
        NtruEncrypt ntru = new NtruEncrypt(EncryptionParameters.APR2011_439_FAST);

        // create an encryption key pair
        EncryptionKeyPair kp = ntru.generateKeyPair();
        
             // database connection and new keypair store against username
               Connection conn = null;

            try{
            conn = DriverManager.getConnection("jdbc:derby://localhost:1527/msproject", "root", "root");
                Username = Username.substring(0,Username.length()-1);
         
                PreparedStatement pstmt = conn.prepareStatement("INSERT INTO Userdata (Username, Pubkey, Prikey, Keypair) VALUES (? , ? , ? , ?)");
                pstmt.setString (1,Username);
                pstmt.setString (2,kp.getPublic().toString());
                pstmt.setString (3, kp.getPrivate().toString());
                pstmt.setString (4, kp.toString());
                pstmt.executeUpdate();
            }
            catch(SQLException ex){
                ex.printStackTrace();
            }
            
             long end = System.currentTimeMillis();
            long elapsed = end - start;
                System.out.println(elapsed);
             
            } 
           else if (Username.substring(Username.length() - 1).equals("2")){
               Username = Username.substring(0,Username.length()-1);
               
                   Connection conn = null;
                   

                try{
                
              
                s = new Socket("IP",6001);
                
                
                conn = DriverManager.getConnection("jdbc:derby://localhost:1527/msproject", "root", "root");
                PreparedStatement pstmt = conn.prepareStatement("select * from Userdata where Username='" + Username +"'" );
                ResultSet rs = (ResultSet) pstmt.executeQuery();
                 if (rs.next()){    
            writer = new PrintWriter(s.getOutputStream());

            writer.write("ok");
            writer.flush();
            writer.close();
            } else {
                     writer = new PrintWriter(s.getOutputStream());
                     writer.write("No");
            writer.flush();
            writer.close();
                 }
                
                }catch (IOException e){
                    e.printStackTrace();
                }
                
                
           
                
           }
            
            else if (Username.substring(Username.length() - 1).equals("3")){
               Username = Username.substring(0,Username.length()-1);
               
                   Connection conn = null;
                   
                
                   conn = DriverManager.getConnection("jdbc:derby://localhost:1527/msproject", "root", "root");
                PreparedStatement pstmt = conn.prepareStatement("select * from Filesuploaded where Username='" + Username +"'" );
                ResultSet rs = (ResultSet) pstmt.executeQuery();
                rs.next();
                int count = rs.getInt(1);
                pstmt = conn.prepareStatement("INSERT INTO Filesuploaded (Username, Files) VALUES (? , ?)");
                pstmt.setString (1,Username);
                pstmt.setString (2,Username +count+1);
              
            }
       
                
        }
        catch(IOException e){
        }
    
    }
    
}
