/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bantizleme;

import com.mysql.jdbc.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import javax.swing.JOptionPane;

/**
 *
 * @author Filiz
 */
public class baglanti {
    java.sql.Connection con;
    
    public Connection ac() throws SQLException{
        try {
            
            Class.forName("com.mysql.jdbc.Connection");
            con = (Connection)DriverManager.getConnection(ayarAl("baglanti"),ayarAl("kullanici"),ayarAl("parola"));
        } 
        catch (ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "HATA", JOptionPane.CANCEL_OPTION);
        }
        return (Connection) con;
    }
    
    public void kapat(Connection con){
        try {
            con.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "HATA", JOptionPane.CANCEL_OPTION);
        }
    }
    
    /**
     *
     * @param ayar
     * @return ayar değerini .properties dosyasından alarak çeker
     */
    public String ayarAl(String ayar) {
        try {String yol = new File("ayarlar.properties").getAbsolutePath();
        //.properties dosyasına ulaşmak için anaktar oluşturuluyor.
        Properties prop = new Properties();
        
        
            InputStream input = new FileInputStream(yol);
            // load a properties file
            prop.load(input);
            ayar = prop.getProperty(ayar);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage()+ "\nLütfen belirtilen adreste doğru dosyanın bulunduğuna emin olunuz ve programı yeniden çalıştırınız!", "HATA", JOptionPane.CANCEL_OPTION);
            System.exit(0);
            
        }
        return ayar;
    }
}
