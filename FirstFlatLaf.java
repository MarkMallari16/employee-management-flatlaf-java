/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package com.mycompany.firstflatlaf;

import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.SwingUtilities;

/**
 *
 * @author Mallari
 */
public class FirstFlatLaf {

    public static void main(String[] args) {
        FlatLightLaf.setup();

        SwingUtilities.invokeLater(() -> {
            Login log = new Login();
            log.setVisible(true);
        });

    }
}
