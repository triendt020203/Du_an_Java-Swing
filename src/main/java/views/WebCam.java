package views;

import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamResolution;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import domainmodel.PhieuDatLich;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import modelview.QLAcount;
import modelview.QLPhieuDatLich;
import net.bytebuddy.asm.Advice;
import service.IPhieuDatLichService;
import service.Impl.PhieuDatLichServiceImpl;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
/**
 *
 * @author DANG VAN SY
 */
public class WebCam extends javax.swing.JFrame implements Runnable, ThreadFactory {

    private WebcamPanel webcamPanel = null;
    private Webcam webCam = null;
    private Executor executor = Executors.newSingleThreadExecutor(this);
    private Map<String,PhieuDatLich> map = new HashMap<>();
    private IPhieuDatLichService phieuDatLichService = new PhieuDatLichServiceImpl();
    private QLAcount qLAcount;
    private JPanel pnTong;
    private JLabel lbHome;

    public WebCam(QLAcount qLAcount ,JPanel pnTong, JLabel labelHome) {
        initComponents();
        HienThi();
        this.qLAcount =qLAcount;
        this.pnTong= pnTong;
        this.lbHome= labelHome;
        for (PhieuDatLich phieuDatLich : phieuDatLichService.getPhieuDatLichByTT()) {
            map.put(phieuDatLich.getMaQR(), phieuDatLich);
        }
    }

    public void HienThi() {
        itnitWebCam();
        this.setLocationRelativeTo(null);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        paneWebCam = new javax.swing.JPanel();
        lbExit = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);

        jPanel1.setName(""); // NOI18N
        jPanel1.setPreferredSize(new java.awt.Dimension(270, 180));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        paneWebCam.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        paneWebCam.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        jPanel1.add(paneWebCam, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 80, 270, 180));

        lbExit.setText(" X");
        lbExit.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        lbExit.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lbExit.setOpaque(true);
        lbExit.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lbExitMouseClicked(evt);
            }
        });
        jPanel1.add(lbExit, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 0, 20, 20));

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel1.setText("WEBCAM");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 37, 80, 20));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 401, Short.MAX_VALUE)
                .addGap(1, 1, 1))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void lbExitMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbExitMouseClicked
        webCam.close();
        this.dispose();
    }//GEN-LAST:event_lbExitMouseClicked
    private void itnitWebCam() {
        Dimension size = WebcamResolution.QVGA.getSize();
        webCam = Webcam.getWebcams().get(0);
        webCam.setViewSize(size);

        webcamPanel = new WebcamPanel(webCam);
        webcamPanel.setPreferredSize(size);
//        paneWebCam.se
        paneWebCam.add(webcamPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 270, 180));
        executor.execute(this);
    }
          
    @Override
    public void run() {
        do {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                Logger.getLogger(WebCam.class.getName()).log(Level.SEVERE, null, ex);
            }

            Result result = null;
            BufferedImage image = null;
            if (webCam.isOpen()) {
                if ((image = webCam.getImage()) == null) {
                    continue;
                }
            }
            LuminanceSource source = new BufferedImageLuminanceSource(image);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
            try {
                result = new MultiFormatReader().decode(bitmap);
            } catch (NotFoundException ex) {
                Logger.getLogger(WebCam.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (result != null) {
                if(!map.containsKey(result.getText())){
                    JOptionPane.showMessageDialog(webcamPanel, "Không tìm thấy lịch đặt");
                    webCam.close();
                    this.dispose();
                    return;
                }
                PhieuDatLich phieuDatLich = map.get(result.getText()); 
                webCam.close();
                this.dispose();
                new FrmPhieuDatSan(phieuDatLich,qLAcount, pnTong, lbHome).setVisible(true);
                return;
            }
            
        } while (true);
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(r, " ");
        t.setDaemon(true);
        return t;
    }

    /**
     * @param args the command line arguments
     */
//    public static void main(String args[]) {
//        /* Set the Nimbus look and feel */
//        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
//        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
//         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
//         */
//        try {
//            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
//                if ("Nimbus".equals(info.getName())) {
//                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
//                    break;
//                }
//            }
//        } catch (ClassNotFoundException ex) {
//            java.util.logging.Logger.getLogger(WebCam.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (InstantiationException ex) {
//            java.util.logging.Logger.getLogger(WebCam.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (IllegalAccessException ex) {
//            java.util.logging.Logger.getLogger(WebCam.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
//            java.util.logging.Logger.getLogger(WebCam.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        }
//        //</editor-fold>
//
//        /* Create and display the form */
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                new WebCam().setVisible(true);
//            }
//        });
//    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lbExit;
    private javax.swing.JPanel paneWebCam;
    // End of variables declaration//GEN-END:variables

}
