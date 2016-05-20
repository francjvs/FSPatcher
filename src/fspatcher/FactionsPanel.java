/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fspatcher;

import java.awt.event.*;
import lev.gui.*;
import skyproc.gui.*;
import skyproc.*;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;

/**
 *
 * @author Francisco
 */
public class FactionsPanel extends SPSettingPanel {
    
    private ArrayList<Pair<LLabel,LComboBox>> factList;
    static private ArrayList<LPanel> panels;
    private Point first = new Point(0,0);
    
    public FactionsPanel(SPMainMenuPanel parent_) {
        super(parent_, "Faction Weapons", FSPatcher.headerColor);
    }
    
    @Override
    protected void initialize() {
        super.initialize();
        
    }
    
    @Override
    public void onOpen(SPMainMenuPanel parent) {
        
        first.setLocation(last);
        
        factList = new ArrayList<>(0);
        panels = new ArrayList<>(0);
        
        for(Pair<String, ArrayList<Pair<String,Integer>>> faction : FSPatcher.factWeapons) {
            
            ArrayList<Pair<LLabel,LComboBox>> compList = new ArrayList<>(0);
            
            int panelHeight = 12 + 20 + 12 + (20+2+25+2)*faction.getVar().size(); //IF add button + 12 + 22
            LPanel panel = new LPanel(275, 200);
            panel.setSize(300, panelHeight);
            
            // Add Title Label
            LLabel name = new LLabel(faction.getBase(), FSPatcher.settingsFont, FSPatcher.settingsColor);
            panel.add(name, BorderLayout.WEST);
            panel.setPlacement(name);
            
            // Add label for weapon with Combo Box for level
            for (Pair<String,Integer> weapon : faction.getVar()) {
                LLabel weapName = new LLabel(weapon.getBase(), FSPatcher.settingsFont, FSPatcher.settingsColor);
                LComboBox weapLevel = new LComboBox(faction.getBase()+"_"+weapon.getBase());
                weapLevel.setSize(100, 24);
                for (int i = 0; i < 30; i++) {
                    weapLevel.addItem(String.valueOf(i));
                    if (weapon.getVar() == i) {
                        weapLevel.setSelectedIndex(i);
                    }
                }
                Pair<LLabel,LComboBox> p = new Pair<>(weapName,weapLevel);
                compList.add(p);
                factList.add(p);
            }
            
            drawComponents(panel,compList,10);
            
            placeAdd(panel);
            panels.add(panel);
        }
        
    }
    
    @Override
    public void onClose(SPMainMenuPanel parent) {
        for (LPanel panel : panels) {
            panel.setVisible(false);
        }
        //panels.clear();
    }
    
    private void moveObject(Component l, int Dy) {
        Rectangle r = l.getBounds();
        r.y += Dy;
        l.setBounds(r);
        updateLast(l);
    }
    
    private void drawComponents (LPanel panel, ArrayList<Pair<LLabel,LComboBox>> compList, int dY) {
        
        for (int i = 0;i<compList.size();i++){
            LLabel cpn = compList.get(i).getBase();
            panel.add(cpn);
            Point p = panel.setPlacement(cpn);
            moveObject(cpn,-dY);
            LComboBox box = compList.get(i).getVar();
            panel.add(box);
            box.setLocation(p.x + cpn.getWidth()+ 5, p.y - cpn.getHeight()-12);
            System.out.println("Label at "+cpn.getBounds());
            System.out.println("Box at "+box.getBounds());
        }
        
    }
    
}
