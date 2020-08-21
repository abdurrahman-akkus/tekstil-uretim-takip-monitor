/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bantizleme;

import com.mysql.jdbc.Connection;
import java.awt.Dimension;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.border.BevelBorder;

/**
 *
 * @author Filiz
 */
public class BantIzleme extends JFrame {

    //TANIMLAMALAR
    JLabel etiket1, etiket2, etiket3, etiket4, duyuru;

    String sorgu;
    Connection con;
    Timer timer;
    static Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

    public BantIzleme() {

        setLayout(new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS));
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        //Etiketleri oluştur
        etiket1 = new JLabel("BANT NO:", SwingConstants.CENTER);
        etiket2 = new JLabel("-", SwingConstants.CENTER);
        etiket3 = new JLabel("-", SwingConstants.CENTER);
        etiket4 = new JLabel("-", SwingConstants.CENTER);
        duyuru = new JLabel("-", SwingConstants.CENTER);
        //En üsttekine BevelBorder ekledik -maksat şekil:)- etiketler opaklaştırıldı ki arkaplan renkleri görünsün
        etiket1.setBorder(new BevelBorder(1, Color.decode("#4682B4"), Color.decode("#4682B4")));
        etiket1.setOpaque(true);
        etiket2.setOpaque(true);
        etiket3.setOpaque(true);
        etiket4.setOpaque(true);
        duyuru.setOpaque(true);

        setDefault();
        //Container'a etiketleri ekle
        add(etiket1);
        add(etiket2);
        add(etiket3);
        add(etiket4);
        add(duyuru);

        //Threadi oluştur ve başlat.
        zamanlayici z = new zamanlayici();
        Thread thz = new Thread(z);
        thz.start();
    }

    public void setDefault() {

        //Font ayarla
        etiket1.setFont(new Font("Calibri", Font.BOLD, (int) (dim.width / 17)));
        etiket2.setFont(new Font("Calibri", Font.BOLD, (int) (dim.width / 13)));
        etiket3.setFont(new Font("Calibri", Font.BOLD, (int) (dim.width / 13)));
        etiket4.setFont(new Font("Calibri", Font.BOLD, (int) (dim.width / 13)));
        duyuru.setFont(new Font("Calibri", Font.BOLD, (int) (dim.width / 13)));
        //Max. ebat ayarla
        etiket1.setMaximumSize(dim);
        etiket2.setMaximumSize(dim);
        etiket3.setMaximumSize(dim);
        etiket4.setMaximumSize(dim);
        duyuru.setMaximumSize(dim);
        //Renk ayarla
        etiket1.setForeground(Color.RED);
        etiket2.setForeground(Color.WHITE);
        etiket3.setForeground(Color.WHITE);
        etiket4.setForeground(Color.WHITE);
        duyuru.setForeground(Color.WHITE);
        etiket1.setBackground(Color.decode("#666699"));
        etiket2.setBackground(Color.decode("#666699"));
        etiket3.setBackground(Color.decode("#666699"));
        etiket4.setBackground(Color.decode("#666699"));
        duyuru.setBackground(Color.decode("#666699"));

//        etiket1.setText("-");
//        etiket2.setText("-");
//        etiket3.setText("-");
//        etiket4.setText("-");
//        duyuru.setText("-");
        etiket1.setVisible(true);
        etiket2.setVisible(true);
        etiket3.setVisible(true);
        etiket4.setVisible(true);
        duyuru.setVisible(true);

        etiket2.setIcon(null);
        etiket3.setIcon(null);

    }

    /**
     *
     * @param ayar
     * @return
     */
    public String varsayilanAyarAl(String ayar) {
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
            //System.exit(0);
        } 
        return ayar;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        BantIzleme anahtar = new BantIzleme();
        //EKRAN BOYUTU AYARI

        anahtar.setLocation(0, 0);
        anahtar.setSize(dim.width - 70, dim.height - 70);
        anahtar.setVisible(true);

    }

    public String zamanAl() {
        SimpleDateFormat bicim = new SimpleDateFormat("yyyy-MM-dd");
        bicim.setTimeZone(TimeZone.getTimeZone("GMT+3:00"));
        java.util.Date tr = new java.util.Date();

        return bicim.format(tr);
    }

    public String donemAl() throws ParseException, SQLException {
        String donem = null;
        String donemBas, donemSon, donemBaslaTampon, donemBitTampon;

        SimpleDateFormat bicim = new SimpleDateFormat("HH:mm");
        bicim.setTimeZone(TimeZone.getTimeZone("GMT+3:00"));
        java.util.Date tr = new java.util.Date();

        baglanti bagAnahtar = new baglanti();
        con = bagAnahtar.ac();
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM donem WHERE aktif='1' ORDER BY sira ASC");

        java.util.Date donemBasla = null;
        java.util.Date donemBit = null;
        while (rs.next()) {
            donemBaslaTampon = rs.getString("donemBasla");
            donemBitTampon = rs.getString("donemBit");
            donemBasla = bicim.parse(donemBaslaTampon);
            donemBit = bicim.parse(donemBitTampon);

            java.util.Date simdi = bicim.parse(bicim.format(tr));

            Calendar cal = Calendar.getInstance();
            //Ajanda zamanını şimdi ayarla
            cal.setTime(simdi);
            //Şimdiye -10 dk ekle
            cal.add(Calendar.MINUTE, -10);
            String gecSimdiString = bicim.format(cal.getTime());

            java.util.Date gecSimdiSaat = bicim.parse(gecSimdiString);

            if (gecSimdiSaat.after(donemBit)) {
                donemBas = rs.getString("donemBasla");
                donemSon = rs.getString("donemBit");

                //Bir sonraki dönemin bitimi gerek.
                rs.next();
                donemBitTampon = rs.getString("donemBit");
                donemBit = bicim.parse(donemBitTampon);

                if (gecSimdiSaat.before(donemBit)) {
                    donem = donemBas + "-" + donemSon;

                    break;
                } else {
                    rs.previous();
                }

            }
        }
        bagAnahtar.kapat(con);
        return donem;
    }

    public String toplamaCek(String toplam, String bant) {
        String veriCekme, dikimHedefToplam = "0", koliHedefToplam = "0", koliAdetToplam = "0", dikimAdetToplam = "0";
        baglanti bagAnahtar = new baglanti();
        try {
            veriCekme = "SELECT bantAd, SUM(dikimHedef) AS dH, SUM(koliHedef) AS kH, SUM(koliAdet) AS kA, SUM(dikimAdet) AS dA"
                    + " FROM saatliktakip WHERE tarih='" + zamanAl() + "' AND duyuru='boş' AND bantAd='" + bant + "' GROUP BY bantAd";

            con = bagAnahtar.ac();
            Statement st = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet rs = st.executeQuery(veriCekme);

            while (rs.next()) {

                dikimHedefToplam = rs.getString("dH");
                dikimAdetToplam = rs.getString("dA");
                koliAdetToplam = rs.getString("kA");
                koliHedefToplam = rs.getString("kH");

            }

            rs.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(rootPane, ex.getMessage(), "HATA", JOptionPane.CANCEL_OPTION);
        }
        switch (toplam) {
            case "dA":
                toplam = dikimAdetToplam;
                break;
            case "dH":
                toplam = dikimHedefToplam;
                break;
            case "kA":
                toplam = koliAdetToplam;
                break;
            case "kH":
                toplam = koliHedefToplam;
                break;

        }
        bagAnahtar.kapat(con);
        return toplam;
    }

    public int saatlikAdetBilgisi(String toplam, String bant, String donem) {
        int toplamGonder = 0;
        String veriCekme;
        baglanti bagAnahtar = new baglanti();
        try {
            veriCekme = "SELECT bantAd, SUM(dikimHedef) AS dH, SUM(koliHedef) AS kH, SUM(koliAdet) AS kA, SUM(dikimAdet) AS dA"
                    + " FROM saatliktakip WHERE tarih='" + zamanAl() + "' AND duyuru='boş' AND donem='" + donem + "' AND bantAd='" + bant + "' GROUP BY bantAd";

            con = bagAnahtar.ac();
            Statement st = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet rs = st.executeQuery(veriCekme);
            switch (toplam) {
                case "sDA":
                    while (rs.next()) {
                        toplamGonder = Integer.parseInt(rs.getString("dA"));
                    }
                    break;
                case "sDH":
                    while (rs.next()) {
                        toplamGonder = Integer.parseInt(rs.getString("dH"));
                    }
                    break;
            }

            

        } catch (SQLException ex) {

            Logger.getLogger(BantIzleme.class.getName()).log(Level.SEVERE, null, ex);
        }
        bagAnahtar.kapat(con);
        return toplamGonder;
    }

    class zamanlayici implements Runnable {

        @Override
        public void run() {
            int dikimToplamEksik = 0, koliToplamEksik, dikimSaatlikEksik, koliSaatlikEksik;
            int dikimSaatlikAdet, dikimSaatlikHedef, dikimToplam = 0, dikimToplamHedef = 0;
            float dikimSaatlikOran, dikimToplamOran;
            
            try {
                //Hesaplama
                while (true) {
                    baglanti bagAnahtar = new baglanti();
                    String donem = donemAl();
                    //SAATİ GÖSTER
                    saatiGoster(donem);

                    sorgu = "SELECT * FROM saatliktakip WHERE tarih='" + zamanAl() + "' "
                            + "AND donem='" + donem + "' GROUP BY bantAd ORDER BY bantAd ASC";

                    con = bagAnahtar.ac();
                    Statement st = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                    Point p = duyuru.getLocation();

                    ResultSet rs = st.executeQuery(sorgu);

                    while (rs.next()) {

                        //Üretilen ve hedef adetler belirlendi
                        dikimSaatlikAdet = saatlikAdetBilgisi("sDA", rs.getString("bantAd"), donem);
                        dikimSaatlikHedef = saatlikAdetBilgisi("sDH", rs.getString("bantAd"), donem);
                        if ((rs.getString("duyuru")).equals("boş")) {
                            dikimToplam = Integer.parseInt(toplamaCek("dA", rs.getString("bantAd")));
                            dikimToplamHedef = Integer.parseInt(toplamaCek("dH", rs.getString("bantAd")));
                            //Toplam eksikleri belirlendi
                            dikimToplamEksik = dikimToplamHedef - dikimToplam;
                            koliToplamEksik = Integer.parseInt(toplamaCek("kH", rs.getString("bantAd"))) - Integer.parseInt(toplamaCek("kA", rs.getString("bantAd")));
                            //ORANLAMA
                            if (dikimSaatlikHedef != 0 && dikimToplamHedef != 0) {
                                dikimSaatlikOran = dikimSaatlikAdet * 100 / dikimSaatlikHedef;
                                dikimToplamOran = dikimToplam * 100 / dikimToplamHedef;

                                //Orana göre renklendirme-
                                if (dikimSaatlikOran > 100) {
                                    etiket4.setBackground(Color.decode("#1a30d8"));
                                } else if (dikimSaatlikOran > 85) {
                                    etiket4.setBackground(Color.decode("#006600"));
                                } else if (dikimSaatlikOran > 80) {
                                    etiket4.setBackground(Color.decode("#CC6600"));
                                } else {
                                    etiket4.setBackground(Color.decode("#990000"));
                                }

                                if (dikimToplamOran > 100) {
                                    duyuru.setBackground(Color.decode("#1a30d8"));
                                } else if (dikimToplamOran > 85) {
                                    duyuru.setBackground(Color.decode("#006600"));
                                } else if (dikimToplamOran > 80) {
                                    duyuru.setBackground(Color.decode("#CC6600"));
                                } else {
                                    duyuru.setBackground(Color.decode("#990000"));
                                }
                            }                            

                            etiket1.setText(rs.getString("bantAd") + ">" + rs.getString("model"));
                            etiket2.setText("KOLİ HEDEF: " + rs.getString("koliHedef"));
                            etiket3.setText("DİKİM HEDEF: " + rs.getString("dikimHedef"));
                            if ((dikimSaatlikHedef - dikimSaatlikAdet) >= 0) {
                                etiket4.setText("DÖNEMLİK KALAN: " + String.valueOf(dikimSaatlikHedef - dikimSaatlikAdet));
                            } else {
                                etiket4.setText("DÖNEMLİK FAZLA: " + String.valueOf(dikimSaatlikAdet - dikimSaatlikHedef));
                            }
                            if (dikimToplamEksik >= 0) {
                                duyuru.setText("TOPLAM KALAN: " + dikimToplamEksik);
                            } else {
                                duyuru.setText("TOPLAM FAZLA: " + (-1 * dikimToplamEksik));
                            }

                            //bekleme ayarı ile Thread'in uyku saniyesi ayarlanıyor.
                            Thread.sleep(Long.valueOf(varsayilanAyarAl("bekleme")));

                        } else if (rs.getString("duyuru").substring(0, 3).equals("bp=")) {

                            etiket1.setBackground(Color.decode("#666699"));
                            etiket2.setBackground(Color.decode("#666699"));
                            etiket3.setBackground(Color.decode("#666699"));
                            etiket4.setBackground(Color.decode("#666699"));
                            duyuru.setBackground(Color.decode("#666699"));

                            float bpDegeri = Float.parseFloat(rs.getString("duyuru").substring(3));
                            duyuru.setText("");
                            if (bpDegeri == 0) {
                                etiket1.setText((int) Float.parseFloat(rs.getString("bantAd")) + ". BANT");
                                etiket2.setText(" ");
                                etiket3.setText(" ");
                                etiket4.setForeground(Color.WHITE);
                                etiket4.setText("BP DEĞERİ:" + bpDegeri);
                                Thread.sleep(Long.valueOf(varsayilanAyarAl("bekleme")));
                            } else if (bpDegeri > 0 && bpDegeri <= 0.25) {
                                etiket1.setText((int) Float.parseFloat(rs.getString("bantAd")) + ". BANT");
                                etiket2.setText(" ");
                                etiket3.setText("");
                                etiket2.setIconTextGap(0);
                                etiket2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resimler/yildizlar/kirmizi025.png")));
                                etiket4.setForeground(Color.WHITE);
                                etiket4.setText("BP DEĞERİ:" + bpDegeri);
                                Thread.sleep(Long.valueOf(varsayilanAyarAl("bekleme")));
                            } else if (bpDegeri > 0.25 && bpDegeri <= 0.5) {
                                etiket1.setText((int) Float.parseFloat(rs.getString("bantAd")) + ". BANT");
                                etiket2.setText(" ");
                                etiket3.setText("");
                                etiket2.setIconTextGap(0);
                                etiket2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resimler/yildizlar/kirmizi05.png")));
                                etiket4.setForeground(Color.WHITE);
                                etiket4.setText("BP DEĞERİ:" + bpDegeri);
                                Thread.sleep(Long.valueOf(varsayilanAyarAl("bekleme")));
                            } else if (bpDegeri > 0.5 && bpDegeri <= 0.75) {
                                etiket1.setText((int) Float.parseFloat(rs.getString("bantAd")) + ". BANT");
                                etiket2.setText(" ");
                                etiket3.setText("");
                                etiket2.setIconTextGap(0);
                                etiket2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resimler/yildizlar/kirmizi075.png")));
                                etiket4.setForeground(Color.WHITE);
                                etiket4.setText("BP DEĞERİ:" + bpDegeri);
                                Thread.sleep(Long.valueOf(varsayilanAyarAl("bekleme")));
                            } else if (bpDegeri > 0.75 && bpDegeri <= 1) {
                                etiket1.setText((int) Float.parseFloat(rs.getString("bantAd")) + ". BANT");
                                etiket2.setText(" ");
                                etiket3.setText("");
                                etiket2.setIconTextGap(0);
                                etiket2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resimler/yildizlar/kirmizi1.png")));
                                etiket4.setForeground(Color.WHITE);
                                etiket4.setText("BP DEĞERİ:" + bpDegeri);
                                Thread.sleep(Long.valueOf(varsayilanAyarAl("bekleme")));
                            } else if (bpDegeri > 1 && bpDegeri <= 1.25) {
                                etiket1.setText((int) Float.parseFloat(rs.getString("bantAd")) + ". BANT");
                                etiket2.setText(" ");
                                etiket3.setText("");
                                etiket2.setIconTextGap(0);
                                etiket2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resimler/yildizlar/kirmizi125.png")));
                                etiket4.setForeground(Color.WHITE);
                                etiket4.setText("BP DEĞERİ:" + bpDegeri);
                                Thread.sleep(Long.valueOf(varsayilanAyarAl("bekleme")));
                            } else if (bpDegeri > 1.25 && bpDegeri <= 1.5) {
                                etiket1.setText((int) Float.parseFloat(rs.getString("bantAd")) + ". BANT");
                                etiket2.setText(" ");
                                etiket3.setText("");
                                etiket2.setIconTextGap(0);
                                etiket2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resimler/yildizlar/sari15.png")));
                                etiket4.setForeground(Color.WHITE);
                                etiket4.setText("BP DEĞERİ:" + bpDegeri);
                                Thread.sleep(Long.valueOf(varsayilanAyarAl("bekleme")));
                            } else if (bpDegeri > 1.5 && bpDegeri <= 1.75) {
                                etiket1.setText((int) Float.parseFloat(rs.getString("bantAd")) + ". BANT");
                                etiket2.setText(" ");
                                etiket3.setText("");
                                etiket2.setIconTextGap(0);
                                etiket2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resimler/yildizlar/sari175.png")));
                                etiket4.setForeground(Color.WHITE);
                                etiket4.setText("BP DEĞERİ:" + bpDegeri);
                                Thread.sleep(Long.valueOf(varsayilanAyarAl("bekleme")));
                            } else if (bpDegeri > 1.75 && bpDegeri <= 2) {
                                etiket1.setText((int) Float.parseFloat(rs.getString("bantAd")) + ". BANT");
                                etiket2.setText(" ");
                                etiket3.setText("");
                                etiket2.setIconTextGap(0);
                                etiket2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resimler/yildizlar/sari2.png")));
                                etiket4.setForeground(Color.WHITE);
                                etiket4.setText("BP DEĞERİ:" + bpDegeri);
                                Thread.sleep(Long.valueOf(varsayilanAyarAl("bekleme")));
                            } else if (bpDegeri > 2 && bpDegeri <= 2.25) {
                                etiket1.setText((int) Float.parseFloat(rs.getString("bantAd")) + ". BANT");
                                etiket2.setText("");
                                etiket3.setText("");
                                etiket2.setIconTextGap(20);
                                etiket2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resimler/yildizlar/sari2.png")));
                                etiket3.setIconTextGap(20);
                                etiket3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resimler/yildizlar/sari025.png")));
                                etiket4.setForeground(Color.WHITE);
                                etiket4.setText("BP DEĞERİ:" + bpDegeri);
                                Thread.sleep(Long.valueOf(varsayilanAyarAl("bekleme")));
                            } else if (bpDegeri > 2.25 && bpDegeri <= 2.5) {
                                etiket1.setText((int) Float.parseFloat(rs.getString("bantAd")) + ". BANT");
                                etiket2.setText("");
                                etiket3.setText("");
                                etiket2.setIconTextGap(20);
                                etiket2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resimler/yildizlar/sari2.png")));
                                etiket3.setIconTextGap(20);
                                etiket3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resimler/yildizlar/sari05.png")));
                                etiket4.setForeground(Color.WHITE);
                                etiket4.setText("BP DEĞERİ:" + bpDegeri);
                                Thread.sleep(Long.valueOf(varsayilanAyarAl("bekleme")));
                            } else if (bpDegeri > 2.25 && bpDegeri <= 2.75) {
                                etiket1.setText((int) Float.parseFloat(rs.getString("bantAd")) + ". BANT");
                                etiket2.setText("");
                                etiket3.setText("");
                                etiket2.setIconTextGap(0);
                                etiket2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resimler/yildizlar/yesil2.png")));
                                etiket3.setIconTextGap(0);
                                etiket3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resimler/yildizlar/yesil075.png")));
                                etiket4.setForeground(Color.WHITE);
                                etiket4.setText("BP DEĞERİ:" + bpDegeri);
                                Thread.sleep(Long.valueOf(varsayilanAyarAl("bekleme")));
                            } else if (bpDegeri > 2.75 && bpDegeri <= 3) {
                                etiket1.setText((int) Float.parseFloat(rs.getString("bantAd")) + ". BANT");
                                etiket2.setText("");
                                etiket3.setText("");
                                etiket2.setIconTextGap(0);
                                etiket2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resimler/yildizlar/yesil2.png")));
                                etiket3.setIconTextGap(0);
                                etiket3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resimler/yildizlar/yesil1.png")));
                                etiket4.setForeground(Color.WHITE);
                                etiket4.setText("BP DEĞERİ:" + bpDegeri);
                                Thread.sleep(Long.valueOf(varsayilanAyarAl("bekleme")));
                            } else if (bpDegeri > 3 && bpDegeri <= 3.25) {
                                etiket1.setText((int) Float.parseFloat(rs.getString("bantAd")) + ". BANT");
                                etiket2.setText("");
                                etiket3.setText("");
                                etiket2.setIconTextGap(0);
                                etiket2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resimler/yildizlar/yesil2.png")));
                                etiket3.setIconTextGap(0);
                                etiket3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resimler/yildizlar/yesil125.png")));
                                etiket4.setForeground(Color.WHITE);
                                etiket4.setText("BP DEĞERİ:" + bpDegeri);
                                Thread.sleep(Long.valueOf(varsayilanAyarAl("bekleme")));
                            } else if (bpDegeri > 3.25 && bpDegeri <= 3.5) {
                                etiket1.setText((int) Float.parseFloat(rs.getString("bantAd")) + ". BANT");
                                etiket2.setText("");
                                etiket3.setText("");
                                etiket2.setIconTextGap(0);
                                etiket2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resimler/yildizlar/yesil2.png")));
                                etiket3.setIconTextGap(0);
                                etiket3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resimler/yildizlar/yesil15.png")));
                                etiket4.setForeground(Color.WHITE);
                                etiket4.setText("BP DEĞERİ:" + bpDegeri);
                                Thread.sleep(Long.valueOf(varsayilanAyarAl("bekleme")));
                            } else if (bpDegeri > 3.5 && bpDegeri <= 3.75) {
                                etiket1.setText((int) Float.parseFloat(rs.getString("bantAd")) + ". BANT");
                                etiket2.setText("");
                                etiket3.setText("");
                                etiket2.setIconTextGap(0);
                                etiket2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resimler/yildizlar/yesil2.png")));
                                etiket3.setIconTextGap(0);
                                etiket3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resimler/yildizlar/yesil175.png")));
                                etiket4.setForeground(Color.WHITE);
                                etiket4.setText("BP DEĞERİ:" + bpDegeri);
                                Thread.sleep(Long.valueOf(varsayilanAyarAl("bekleme")));
                            } else if (bpDegeri > 3.75 && bpDegeri <= 4) {
                                etiket1.setText((int) Float.parseFloat(rs.getString("bantAd")) + ". BANT");
                                etiket2.setText("");
                                etiket3.setText("");
                                etiket2.setIconTextGap(0);
                                etiket2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resimler/yildizlar/yesil2.png")));
                                etiket3.setIconTextGap(0);
                                etiket3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resimler/yildizlar/yesil2.png")));
                                etiket4.setForeground(Color.WHITE);
                                etiket4.setText("BP DEĞERİ:" + bpDegeri);
                                Thread.sleep(Long.valueOf(varsayilanAyarAl("bekleme")));
                            }
                        } else {

                            etiket1.setVisible(false);
                            etiket2.setVisible(false);
                            etiket3.setVisible(false);
                            etiket4.setVisible(false);
                            duyuru.setLocation(0, 0);
                            duyuru.setSize(dim.width, dim.height);
                            duyuru.setBackground(Color.decode("#990000"));
                            duyuru.setFont(new Font("Calibri", Font.BOLD, (int) (dim.width / 13)));
                            //JLabel içerisindeki yazı için "metni kaydır"
                            duyuru.setText("<html><center> " + rs.getString("duyuru") + " </center></html>");
                            //bekleme ayarı ile Thread'in uyku saniyesi ayarlanıyor.
                            Thread.sleep(Long.valueOf(varsayilanAyarAl("bekleme")));
                        }
                        setDefault();
                    }
                    st.clearBatch();
                    rs.close();

                    duyuru.setLocation(p);

                    bagAnahtar.kapat(con);
                }
            } catch (SQLException | InterruptedException | ParseException ex) {
                JOptionPane.showMessageDialog(rootPane, ex.getMessage(), "HATA", JOptionPane.CANCEL_OPTION);
            }

        }

        private void saatiGoster(String donemBilgisi) {
            try {
                etiket1.setVisible(false);
                etiket2.setVisible(false);
                etiket3.setVisible(false);
                etiket4.setVisible(false);
                duyuru.setLocation(0, 0);
                duyuru.setSize(dim.width, dim.height);
                duyuru.setBackground(Color.decode("#0066FF"));
                duyuru.setFont(new Font("Calibri", Font.BOLD, (int) (dim.width / 13)));
                java.util.Date tr = new java.util.Date();
                SimpleDateFormat bicim = new SimpleDateFormat("HH:mm");
                bicim.setTimeZone(TimeZone.getTimeZone("GMT+3:00"));
                java.util.Date simdi = bicim.parse(bicim.format(tr));
                duyuru.setText("<html><center>GÖSTERİLEN DÖNEM:<br>" + donemBilgisi + "<br>SAAT:<br>"
                        + String.valueOf(bicim.format(simdi)) + "</center></html>");
                System.out.println(String.valueOf(simdi));
                Thread.sleep(Long.valueOf(varsayilanAyarAl("bekleme")));
                setDefault();
            } catch (ParseException | InterruptedException ex) {
                Logger.getLogger(BantIzleme.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
