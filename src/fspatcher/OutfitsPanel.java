/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fspatcher;

import java.awt.event.*;
import lev.gui.*;
import skyproc.gui.*;
import skyproc.*;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Rectangle;
import java.util.ArrayList;

/**
 *
 * @author David
 */
public class OutfitsPanel extends SPSettingPanel {

    private ArrayList<String> outfitKeys;
    private ArrayList<String> tierNames;
    private ArrayList<Pair<String,LPanel>> outfits;

    private class TierListener implements ActionListener {

        private String set;
        private String newKey;
        private LComboBox box;
        private String tierName;
        

        TierListener(String a, LComboBox b, String name) {
            set = a;
            box = b;
            newKey = null;
            tierName = name;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String pressed = (String) box.getSelectedItem();
            if ((pressed.compareTo("None") == 0) && (newKey != null)) {
                for (Pair<String, ArrayList<String>> p : FSPatcher.tiers) {
                    if (p.getBase().contentEquals(set)) {
                        p.getVar().remove(tierName + newKey);
                    }
                }
                newKey = null;
                box.clearHighlight();
            } else if (pressed.compareTo("None") != 0) {
                if (newKey != null) {
                    for (Pair<String, ArrayList<String>> p : FSPatcher.tiers) {
                        if (p.getBase().contentEquals(set)) {
                            p.getVar().remove(tierName + newKey);
                        }
                    }
                }
                boolean found = false;
                for (Pair<String, ArrayList<String>> p : FSPatcher.tiers) {
                    if (p.getBase().contentEquals(set)) {
                        if (!p.getVar().contains(tierName + pressed)) {
                            p.getVar().add(tierName + pressed);
                        }
                        found = true;
                    }
                }
                if(!found){
                    Pair<String, ArrayList<String>> q = new Pair<>(set, new ArrayList<String>(0));
                    q.getVar().add(tierName + pressed);
                    FSPatcher.tiers.add(q);
                }

                newKey = pressed;
                box.highlightChanged();

            }
        }
    }
    
    private class OutfitRemover implements ActionListener {
        String setKey;
        
        OutfitRemover(String k) {
            setKey = k;
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            for (Pair<String, ArrayList<String>> p : FSPatcher.tiers) {
                if (p.getBase().contentEquals(setKey)) {
                    FSPatcher.tiers.remove(p);
                    break;
                }
            }
            for (Pair<String, ArrayList<ARMO>> p : FSPatcher.outfits) {
                if (p.getBase().contentEquals(setKey)) {
                    FSPatcher.outfits.remove(p);
                    break;
                }
            }
            for (ModPanel mp : FSPatcher.modPanels) {
                mp.FindRemoveOutfit(setKey, e);
            }
            updatePanel();
        }
    }
    
    private class ArmorRemover implements ActionListener {
        String setKey;
        LComboBox box;
        
        ArmorRemover(String k, LComboBox b) {
            setKey = k;
            box = b;
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            String armorEDID = (String) box.getSelectedItem();
            //ARMO armor = null;
            for (Pair<String, ArrayList<ARMO>> p : FSPatcher.outfits) {
                if (p.getBase().contentEquals(setKey)) {
                    ARMO armor = null;
                    for (ARMO a : p.getVar()) {
                        if (a.getEDID().equals(armorEDID)) {
                            armor = a;
                            break;
                        }
                    }
                    if (armor != null) {
                        p.getVar().remove(armor);
                        if (p.getVar().isEmpty()) {
                            for (Pair<String, ArrayList<String>> pt : FSPatcher.tiers) {
                                if (pt.getBase().contentEquals(setKey)) {
                                    FSPatcher.tiers.remove(pt);
                                    break;
                                }
                            }
                            FSPatcher.outfits.remove(p);
                            updatePanel();
                        }
                        for (ModPanel mp : FSPatcher.modPanels) {
                            mp.FindRemoveArmor(setKey, armor, e);
                        }
                        break;
                    }
                }
            }
        }
    }

    public OutfitsPanel(SPMainMenuPanel parent_) {
        super(parent_, "Outfits", FSPatcher.headerColor);
    }

    @Override
    protected void initialize() {
        super.initialize();

        outfitKeys = new ArrayList<>(0);
        outfits = new ArrayList<>(0);
        
        tierNames = new ArrayList<>(0);
            tierNames.add("Bandit Heavy");
            tierNames.add("Bandit Light");
            tierNames.add("Bandit Boss");
            tierNames.add("Thalmor");
            tierNames.add("Necromancer");
            tierNames.add("Warlock");

    }

    @Override
    public void onOpen(SPMainMenuPanel parent) {
        updatePanel();
        //initialize();
        for (Pair<String, ArrayList<ARMO>> p : FSPatcher.outfits) {
            String key = p.getBase();
            if (!outfitKeys.contains(key)) {
                outfitKeys.add(key);
                LPanel panel = new LPanel(275, 200);
                panel.setSize(300, 600);
                
                Pair<String,LPanel> pair = new Pair(key,panel);
                outfits.add(pair);
                
                ArrayList<Component> compList = new ArrayList<>(0);

                LLabel name = new LLabel(key, FSPatcher.settingsFont, FSPatcher.settingsColor);
                
                // box.addEnterButton("Set", al);
                // List armors from outfit and add button to remove from outfit
                
                panel.add(name, BorderLayout.WEST);
                panel.setPlacement(name);
                
                // add button to remove outfit
                LButton remout = new LButton("Remove Outfit");
                remout.addActionListener(new OutfitRemover(key));
                panel.add(remout);
                panel.setPlacement(remout);
                
                // Add Combo box with armors from outfit and button to remove individual pieces
                LComboBox outfitArmor = new LComboBox("Armor:");
                for (ARMO a: p.getVar()) {
                    outfitArmor.addItem(a.getEDID());
                }
                outfitArmor.addEnterButton("Remove", new ArmorRemover(key,outfitArmor));
                outfitArmor.setSize(200, 25);
                panel.add(outfitArmor);
                panel.setPlacement(outfitArmor);
                
                // Add Boxes for each type of tier
                for (String tierName : tierNames) {    
                    String tierKey = tierName.replace(" ", "");
                    LLabel label = new LLabel(tierName + " Tier:", FSPatcher.settingsFont, FSPatcher.settingsColor);
                    compList.add(label);
                    LComboBox box = new LComboBox(tierKey + " Tier:");
                    box.addItem("None");
                    for (int i = 0; i < 30; i++) {
                        box.addItem(String.valueOf(i));
                    }
                    box.addEnterButton("set", new TierListener(key, box, tierKey + "_Tier_"));
                    compList.add(box);
                }
                
                drawComponents(panel, compList, 10);
                
                setPlacement(panel);
                Add(panel);
            }
        }
    }
    
    private void moveObject(Component l, int Dy) {
        Rectangle r = l.getBounds();
        r.y += Dy;
        l.setBounds(r);
    }
    
    private void drawComponents (LPanel panel, ArrayList<Component> list, int dY) {
        
        for (int i = 0;i<list.size();i++){
            Component cpn = list.get(i);
            panel.add(cpn);
            panel.setPlacement(cpn);
            moveObject(cpn,-dY*i);
        }
        
    }
    
    private void updatePanel () {
        
        for (Pair<String, ArrayList<ARMO>> p : FSPatcher.outfits) {
            if(!outfitKeys.contains(p.getBase())) {
                for (Pair<String,LPanel> p1 : outfits) {
                    if (p1.getBase().equals(p.getBase())) {
                        p1.getVar().removeAll();
                        outfits.remove(p1);
                        break;
                    }
                }
            }
        }
        
    }
    
}
