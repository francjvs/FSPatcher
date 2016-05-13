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
            ARMO armor = null;
            for (Pair<String, ArrayList<ARMO>> p : FSPatcher.outfits) {
                if (p.getBase().contentEquals(setKey)) {
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

    }

    @Override
    public void onOpen(SPMainMenuPanel parent) {
        //initialize();
        for (Pair<String, ArrayList<ARMO>> p : FSPatcher.outfits) {
            String key = p.getBase();
            if (!outfitKeys.contains(key)) {
                outfitKeys.add(key);
                LPanel panel = new LPanel(275, 200);
                panel.setSize(300, 600);
                
                ArrayList<Component> cToDraw = new ArrayList<>(0);

                LLabel name = new LLabel(key, FSPatcher.settingsFont, FSPatcher.settingsColor);
                
                // box.addEnterButton("Set", al);
                // List armors from outfit and add button to remove from outfit
                

                LComboBox banditHeavy = new LComboBox("Bandit Heavy Tier:");
                LComboBox banditBoss = new LComboBox("Bandit Boss Tier:");
                LComboBox banditLight = new LComboBox("Bandit Light Tier:");
                LComboBox thalmor = new LComboBox("Thalmor Tier:");
                LComboBox necromancer = new LComboBox("Necromancer Tier:");
                LComboBox warlock = new LComboBox("Warlock Tier:");

                banditHeavy.addItem("None");
                banditBoss.addItem("None");
                banditLight.addItem("None");
                thalmor.addItem("None");
                necromancer.addItem("None");
                warlock.addItem("None");
                for (int i = 0; i < 30; i++) {
                    banditHeavy.addItem(String.valueOf(i));
                    banditBoss.addItem(String.valueOf(i));
                    banditLight.addItem(String.valueOf(i));
                    thalmor.addItem(String.valueOf(i));
                    necromancer.addItem(String.valueOf(i));
                    warlock.addItem(String.valueOf(i));
                }


                banditHeavy.addEnterButton("set", new TierListener(key, banditHeavy, "BanditHeavy_Tier_"));
                banditHeavy.setSize(100, 25);
                banditBoss.addEnterButton("set", new TierListener(key, banditBoss, "BanditBoss_Tier_"));
                banditBoss.setSize(100, 25);
                banditLight.addEnterButton("set", new TierListener(key, banditLight, "BanditLight_Tier_"));
                banditLight.setSize(100, 25);
                thalmor.addEnterButton("set", new TierListener(key, thalmor, "Thalmor_Tier_"));
                thalmor.setSize(100, 25);
                necromancer.addEnterButton("set", new TierListener(key, necromancer, "Necromancer_Tier_"));
                necromancer.setSize(100, 25);
                warlock.addEnterButton("set", new TierListener(key, warlock, "Warlock_Tier_"));
                warlock.setSize(100, 25);

                LLabel banditHLabel = new LLabel("Bandit Heavy Tier:", FSPatcher.settingsFont, FSPatcher.settingsColor);
                LLabel banditBLabel = new LLabel("Bandit Boss Tier:", FSPatcher.settingsFont, FSPatcher.settingsColor);
                LLabel banditLLabel = new LLabel("Bandit Light Tier:", FSPatcher.settingsFont, FSPatcher.settingsColor);
                LLabel thalmorLabel = new LLabel("Thalmor Tier:", FSPatcher.settingsFont, FSPatcher.settingsColor);
                LLabel necroLabel = new LLabel("Necromancer Tier:", FSPatcher.settingsFont, FSPatcher.settingsColor);
                LLabel lockLabel = new LLabel("Warlock Tier:", FSPatcher.settingsFont, FSPatcher.settingsColor);
                /*            ADD THE REST            */

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
                
                /*Rectangle r;
                panel.add(banditHLabel);
                panel.setPlacement(banditHLabel);
                panel.add(banditHeavy);
                panel.setPlacement(banditHeavy);
                moveObject(banditHeavy,-10);
                panel.add(banditBLabel);
                panel.setPlacement(banditBLabel);
                moveObject(banditBLabel,-20);
                panel.add(banditBoss);
                panel.setPlacement(banditBoss);
                moveObject(banditBoss,-30);
                panel.add(banditLLabel);
                panel.setPlacement(banditLLabel);
                moveObject(banditLLabel,-40);
                panel.add(banditLight);
                panel.setPlacement(banditLight);
                moveObject(banditLight,-50);
                panel.add(thalmorLabel);
                panel.setPlacement(thalmorLabel);
                moveObject(thalmorLabel,-60);
                panel.add(thalmor);
                panel.setPlacement(thalmor);
                moveObject(thalmor,-70);
                panel.add(necroLabel);
                panel.setPlacement(necroLabel);
                moveObject(necroLabel,-80);
                panel.add(necromancer);
                panel.setPlacement(necromancer);
                moveObject(necromancer,-90);
                panel.add(lockLabel);
                panel.setPlacement(lockLabel);
                moveObject(lockLabel,-100);
                panel.add(warlock);
                panel.setPlacement(warlock);
                moveObject(warlock,-110);*/
                
                cToDraw.add(banditHLabel);
                cToDraw.add(banditHeavy);
                cToDraw.add(banditBLabel);
                cToDraw.add(banditBoss);
                cToDraw.add(banditLLabel);
                cToDraw.add(banditLight);
                cToDraw.add(thalmorLabel);
                cToDraw.add(thalmor);
                cToDraw.add(necroLabel);
                cToDraw.add(necromancer);
                cToDraw.add(lockLabel);
                cToDraw.add(warlock);
                
                drawComponents(panel,cToDraw,10);
                

                setPlacement(panel);
                Add(panel);
            }
        }
    }
    
    @Override
    public void onClose(SPMainMenuPanel parent) {
        this.updateUI();
        //this.initialized = false;
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
}
