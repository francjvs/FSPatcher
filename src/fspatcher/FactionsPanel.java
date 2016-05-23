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
            
            int panelHeight = 12 + 20 + 2 + (24+2)*faction.getVar().size(); //IF add button + 12 + 22
            LPanel panel = new LPanel(275, 200);
            panel.setSize(300, panelHeight);
            
            // Add Title Label
            LLabel name = new LLabel(faction.getBase(), FSPatcher.settingsFont, FSPatcher.settingsColor);
            panel.Add(name);
            panel.setPlacement(name);
            centerLabel(panel,name);
            
            
            // Add label for weapon with Combo Box for level
            for (Pair<String,Integer> weapon : faction.getVar()) {
                LLabel weapName = new LLabel(weapon.getBase(), FSPatcher.settingsFont, FSPatcher.settingsColor);
                LComboBox weapLevel = new LComboBox(faction.getBase()+"_"+weapon.getBase());
                weapLevel.setSize(100, 24);
                for (int i = 1; i < 30; i++) {
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
            
            setPlacement(panel);
            Add(panel);
            panels.add(panel);
        }
        
    }
    
    @Override
    public void onClose(SPMainMenuPanel parent) {
        // Get levels from the UI
        for(Pair<LLabel,LComboBox> p : factList){
            int index = p.getVar().getName().indexOf("_");
            String factionKey = p.getVar().getName().substring(0, index);
            String weaponEDID = p.getVar().getName().substring(index+1);
            
            for(Pair<String, ArrayList<Pair<String,Integer>>> faction : FSPatcher.factWeapons) {
                if(faction.getBase().equals(factionKey)) {
                    for (Pair<String,Integer> weapon : faction.getVar()) {
                        if(weapon.getBase().equals(weaponEDID)) {
                            weapon.setVar(Integer.parseInt((String) p.getVar().getSelectedItem()));
                            break;
                        }
                    }
                }
            }
            
        }
        
        for (Component panel : panels) {
            panel.setVisible(false);
            remove(panel);
        }
        //panels.clear();
        last.setLocation(first);
    }
    
    private void moveObject(Component l, int Dy) {
        Rectangle r = l.getBounds();
        r.y += Dy;
        l.setBounds(r);
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
            panel.updateLast(cpn);
        }
        
    }
    
    private void centerLabel(LPanel panel, LLabel label) {
        int x = (panel.getWidth()/2) - (label.getWidth()/2);
        
        panel.setPlacement(label, x, label.getY()-label.getHeight());
    }
    
}
