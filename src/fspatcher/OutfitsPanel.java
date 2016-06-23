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
    
    protected int panelHeight = 1000;

    private ArrayList<String> outfitKeys;
    private ArrayList<String> tierNames;
    static private ArrayList<Pair<String,ArrayList<Component>>> outfitComp;

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
            deleteOutfit(setKey,null,null);
            //updateModPanels(setKey,null,e);
            updatePanel(setKey);
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
                            deleteOutfit(setKey,null,p);
                            updatePanel(setKey);
                        } else {
                            updateBox(box,p.getVar());
                        }
                        //updateModPanels(setKey,armor,e);
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
        outfitComp = new ArrayList<>(0);
        
        tierNames = new ArrayList<>(0);
            //tierNames.add("Adventurer");
            tierNames.add("Alikr");
            tierNames.add("Bandit Light");
            tierNames.add("Bandit Heavy");
            tierNames.add("Bandit Boss");
            tierNames.add("Dawnguard");
            //tierNames.add("Guardian");
            tierNames.add("Hunter");
            tierNames.add("Imperial Soldier");
            //tierNames.add("Imperial Mage");
            tierNames.add("Imperial Legate");
            //tierNames.add("Imperial Heavy");
            tierNames.add("Necromancer");
            tierNames.add("Orc Stronghold");
            tierNames.add("Sons Soldier");
            //tierNames.add("Sons Mage");
            tierNames.add("Sons Bear");
            tierNames.add("Thalmor");
            tierNames.add("Thalmor Mage");
            tierNames.add("Vigilants");
            tierNames.add("Warlock");

    }

    @Override
    public void onOpen(SPMainMenuPanel parent) {
        
        for (int i = 0; i<outfitKeys.size();i++) {
            boolean found = false;
            String k = outfitKeys.get(i);
            for (Pair<String, ArrayList<ARMO>> p : FSPatcher.outfits) {
                if (p.getBase().equals(k)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                updatePanel(k);
                outfitKeys.remove(k);
            }
        }
        for (Pair<String, ArrayList<ARMO>> p : FSPatcher.outfits) {
            String key = p.getBase();
            if (!outfitKeys.contains(key)) {
                outfitKeys.add(key);
                LPanel panel = new LPanel(275, 200);
                panel.setSize(300, panelHeight);
                
                //Pair<String,LPanel> pair = new Pair(key,panel);
                //outfits.add(pair);
                
                ArrayList<Component> compList = new ArrayList<>(0);

                // Add Label with Set Keyword
                LLabel name = new LLabel(key, FSPatcher.settingsFont, FSPatcher.settingsColor);
                panel.add(name, BorderLayout.WEST);
                panel.setPlacement(name);
                
                // add button to remove outfit
                LButton remout = new LButton("Remove Outfit");
                remout.addActionListener(new OutfitRemover(key));
                panel.add(remout);
                panel.setPlacement(remout);
                
                // List armors from outfit and add button to remove from outfit
                LComboBox outfitArmor = new LComboBox(key);
                for (ARMO a: p.getVar()) {
                    outfitArmor.addItem(a.getEDID());
                }
                outfitArmor.addEnterButton("Remove", new ArmorRemover(key,outfitArmor));
                outfitArmor.setSize(270, 25);
                panel.add(outfitArmor);
                panel.setPlacement(outfitArmor);
                
                // Check if tier has already been set
                ArrayList<String> setTiers = null;
                for (Pair<String, ArrayList<String>> pt : FSPatcher.tiers) {
                    if (pt.getBase().contentEquals(key)) {
                        setTiers = pt.getVar();
                        break;
                    }
                }
                
                // Add Boxes for each type of tier
                for (String tierName : tierNames) {    
                    String tierKey = tierName.replace(" ", "");
                    LLabel label = new LLabel(tierName + " Tier:", FSPatcher.settingsFont, FSPatcher.settingsColor);
                    compList.add(label);
                    LComboBox box = new LComboBox(key+tierKey);
                    box.setSize(100, 25);
                    box.addItem("None");
                    for (int i = 0; i < 30; i++) {
                        box.addItem(String.valueOf(i));
                    }
                    box.addEnterButton("set", new TierListener(key, box, tierKey + "_Tier_"));
                    compList.add(box);
                    
                    // If tier has already been set and changes UI to reflect that
                    if (setTiers != null) {
                        for (String tier : setTiers) {
                            if(tier.startsWith(tierKey)) {
                                int index = Integer.parseInt(tier.replaceAll("[\\D]", ""));
                                box.setSelectedIndex(index + 1); //To bypass "None" entry
                                box.highlightChanged();
                                break;
                            }
                        }
                    }
                }
                
                drawComponents(panel, compList, 10);
                
                // Store components for UI updates
                //compList.add(name); // Set Label
                //compList.add(remout); // Remove Outfit Button
                //compList.add(outfitArmor); // Remove Armor Button
                compList = new ArrayList<>(0);
                compList.add(panel); // The Panel
                compList.add(outfitArmor); // Remove Armor Button
                Pair<String,ArrayList<Component>> psc = new Pair(key,compList);
                outfitComp.add(psc);
                
                setPlacement(panel);
                Add(panel);
            } else {
                //Update Armor LComboBoxes
                ArrayList<Component> compList = null;
                for (Pair<String,ArrayList<Component>> psc : outfitComp) {
                    if (psc.getBase().contentEquals(key)) {
                        compList = psc.getVar();
                        break;
                    }
                }
                if (compList != null) {
                    LComboBox box = (LComboBox) compList.get(compList.size()-1);
                    updateBox(box,p.getVar());
                }
            }
            
        }
        
    }
    
    @Override
    public void onClose(SPMainMenuPanel parent) {
        //removeAll();
        //revalidate();
        //repaint();
        //initialized = false;
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
    
    private void updatePanel (String setKey) {
        boolean found = false;
        Pair<String,ArrayList<Component>> del = null;
        for (int i = 0; i < outfitComp.size(); i++) {
            Pair<String,ArrayList<Component>> psc = outfitComp.get(i);
            System.out.println(psc.getBase() + " + " + setKey);
            if(psc.getBase().equals(setKey)) {
                del = psc;
                found = true;
                for (Component c : psc.getVar()) {
                    c.setVisible(false);
                }
                //break;
            } else if(found) {
                LPanel panel = (LPanel) psc.getVar().get(0);
                panel.setLocation(panel.getX(), panel.getY()-panelHeight-spacing);
            }
        }
        if (found) {
            last.setLocation(last.getX(), (last.getY()-(double)panelHeight-(double)spacing));
            outfitKeys.remove(setKey);
            outfitComp.remove(del); 
        }
        
        
        
    }
    
    /**
     * Removes the outfit from memory. They can be introduced directly
     * or give null param to make the function find them using the setKey
     * param
     *
     * @param setKey Key from the outfit to be removed
     * @param tier Pair<String, ArrayList<String>> from the tier list
     * @param outfit Pair<String, ArrayList<ARMO>> from the outfit list
     */
    private void deleteOutfit(String setKey, Pair<String, ArrayList<String>> tier, Pair<String, ArrayList<ARMO>> outfit) {
        if (tier == null) {    
            for (Pair<String, ArrayList<String>> p : FSPatcher.tiers) {
                if (p.getBase().contentEquals(setKey)) {
                    FSPatcher.tiers.remove(p);
                    break;
                }
            }
        } else {
            FSPatcher.tiers.remove(tier);
        }
        if (outfit == null) {
            for (Pair<String, ArrayList<ARMO>> p : FSPatcher.outfits) {
                if (p.getBase().contentEquals(setKey)) {
                    FSPatcher.outfits.remove(p);
                    break;
                }
            }
        } else {
            FSPatcher.outfits.remove(outfit);
        }

    }
    
    private void updateModPanels (String setKey, ARMO armor,ActionEvent e) {
        for (ModPanel mp : FSPatcher.modPanels) {
            if (armor == null) {
                mp.FindRemoveOutfit(setKey, e);
            } else {
                mp.FindRemoveArmor(setKey, armor, e);
            }
        }
    }
    
    private void updateBox (LComboBox box, ArrayList<ARMO> list) {
        box.removeAllItems();
        for (ARMO a : list) {
            box.addItem(a.getEDID());
        }
    }
    
}
