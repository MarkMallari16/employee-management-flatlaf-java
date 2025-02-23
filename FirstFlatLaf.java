/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package com.mycompany.firstflatlaf;

import com.formdev.flatlaf.FlatLightLaf;

/**
 *
 * @author Mallari
 */
public class FirstFlatLaf {

    public static void main(String[] args) {
        FlatLightLaf.setup();
        BasicGUI gui = new BasicGUI();
        gui.setVisible(true);
    }
}
