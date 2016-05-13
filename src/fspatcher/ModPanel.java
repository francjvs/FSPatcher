/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fspatcher;

import java.awt.BorderLayout;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Arrays;
import lev.gui.*;
import org.w3c.dom.Node;
import skyproc.*;
import skyproc.gui.*;

/**
 *
 * @author David
 */
public class ModPanel extends SPSettingPanel {

    public Mod myMod;
    public ArrayList<Pair<ARMO, KYWD>> armorKeys = new ArrayList<>(0);
    public ArrayList<Pair<WEAP, KYWD>> weaponKeys = new ArrayList<>(0);
    public ArrayList<Pair<WEAP, KYWD>> weaponFactions = new ArrayList<>(0);
            
    private ArrayList<LComboBox> weaponBoxes;
    private ArrayList<ArmorListener> armorListeners;
    private ArrayList<WeaponListener> weaponListeners;
    private static ArrayList<OutfitListener> outfitListeners;
    
    private final ArrayList<String> FactionKeys = new ArrayList<>(Arrays.asList("Alikr", "OrcStronghold","Thalmor","Imperial","LegateImperial","Guard","Sons","BearSons","Wolf"));
    

    private class ArmorListener implements ActionListener {

        private ARMO armor;
        private KYWD newKey;
        private LComboBox box;
        private LLabel label;

        ArmorListener(ARMO a, LComboBox b, LLabel l) {
            armor = a;
            box = b;
            newKey = null;
            label = l;

        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String pressed = (String) box.getSelectedItem();
            if ((pressed.compareTo("None") == 0) && (newKey != null)) {
                for (Pair<ARMO, KYWD> p : armorKeys) {
                    if (p.getBase().getForm().equals(armor.getForm())) {
                        armorKeys.remove(p);
                        break;
                    }
                }
                newKey = null;
                box.clearHighlight();
                label.setText(armor.getName());
            } else if ((pressed.compareTo("None") != 0) && (newKey == null)) {
                newKey = (KYWD) FSPatcher.gearVariants.getMajor(pressed, GRUP_TYPE.KYWD);
                Pair<ARMO, KYWD> p = new Pair<>(armor, newKey);
                armorKeys.add(p);

                box.highlightChanged();
                label.setText(armor.getName() + " set " + newKey.getEDID());
            } else if ((pressed.compareTo("None") != 0) && (newKey != null)) {
                newKey = (KYWD) FSPatcher.gearVariants.getMajor(pressed, GRUP_TYPE.KYWD);
                for (Pair<ARMO, KYWD> p : armorKeys) {
                    if (p.getBase().getForm().equals(armor.getForm())) {
                        p.setVar(newKey);
                        break;
                    }
                }
                box.highlightChanged();
                label.setText(armor.getName() + " set " + newKey.getEDID());
            }
        }
    }

    private class WeaponListener implements ActionListener {

        private WEAP weapon;
        private KYWD newKey;
        private LComboBox box;
        private LLabel title;

        WeaponListener(WEAP a, LComboBox b, LLabel l) {
            weapon = a;
            box = b;
            newKey = null;
            title = l;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String pressed = (String) box.getSelectedItem();
            if ((pressed.compareTo("None") == 0) && (newKey != null)) {
                for (Pair<WEAP, KYWD> p : weaponKeys) {
                    if (p.getBase().getForm().equals(weapon.getForm())) {
                        weaponKeys.remove(p);
                        break;
                    }
                }
                newKey = null;
                box.clearHighlight();
                title.setText(weapon.getName());
            } else if ((pressed.compareTo("None") != 0) && (newKey == null)) {
                newKey = (KYWD) FSPatcher.gearVariants.getMajor(pressed, GRUP_TYPE.KYWD);
                Pair<WEAP, KYWD> p = new Pair<>(weapon, newKey);
                weaponKeys.add(p);

                box.highlightChanged();
                title.setText(weapon.getName() + " set as " + newKey.getEDID());
            } else if ((pressed.compareTo("None") != 0) && (newKey != null)) {
                newKey = (KYWD) FSPatcher.gearVariants.getMajor(pressed, GRUP_TYPE.KYWD);
                for (Pair<WEAP, KYWD> p : weaponKeys) {
                    if (p.getBase().getForm().equals(weapon.getForm())) {
                        p.setVar(newKey);
                        break;
                    }
                }
                box.highlightChanged();
                title.setText(weapon.getName() + " set as " + newKey.getEDID());
            }
        }
    }

    private class SetAllListener implements ActionListener {

        SetAllListener() {
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            for (ArmorListener a : armorListeners) {
                a.actionPerformed(e);
            }
            for (WeaponListener a : weaponListeners) {
                a.actionPerformed(e);
            }
            for (OutfitListener a : outfitListeners) {
                a.actionPerformed(e);
            }
        }
    }

    private class SetNoneListener implements ActionListener {

        FLST armorMatTypes;
        FLST weaponMatTypes;

        SetNoneListener() {
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            for (ArmorListener a : armorListeners) {
                a.box.setSelectedIndex(0);
                a.actionPerformed(e);
            }
            for (WeaponListener a : weaponListeners) {
                a.box.setSelectedIndex(0);
                a.actionPerformed(e);
            }
            for (OutfitListener a : outfitListeners) {
                a.field.setText("");
                a.actionPerformed(e);
            }
        }
    }

    private class OutfitListener implements ActionListener {

        LTextField field;
        ARMO armor;
        String setKey;

        OutfitListener(LTextField ltf, ARMO a) {
            field = ltf;
            armor = a;
            setKey = null;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String key = field.getText();
            if (setKey != null || key.contentEquals("")) {
                for (Pair<String, ArrayList<ARMO>> p : FSPatcher.outfits) {
                    if (p.getBase().contentEquals(setKey)) {
                        p.getVar().remove(armor);
                    }
                    if (p.getVar().isEmpty()) {
                        FSPatcher.outfits.remove(p);
                    }
                }
                field.clearHighlight();
            }

            if (!key.contentEquals("")) {
                boolean found = false;
                if (setKey != null) {
                    for (Pair<String, ArrayList<ARMO>> p : FSPatcher.outfits) {
                        if (p.getBase().contentEquals(setKey)) {
                            if (!p.getVar().contains(armor)) {
                                p.getVar().add(armor);
                            }
                            found = true;
                        }
                    }
                }
                if (!found) {
                    Pair<String, ArrayList<ARMO>> q = new Pair<>(key, new ArrayList<ARMO>(0));
                    q.getVar().add(armor);
                    FSPatcher.outfits.add(q);
                }
                
                field.highlightChanged();
            }
            setKey = key;
        }
    }
    
    private class FactionListener implements ActionListener {

        private LComboBox box;
        private WEAP weapon;
        private ArrayList<String> keys;
        
        FactionListener(WEAP a, LComboBox b) {
            weapon = a;
            box = b;
            keys = FactionKeys;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String pressed = (String) box.getSelectedItem();
            if(!pressed.equals("")) {
                // IF faction option contains "*" - ACTION is remove from faction
                if (pressed.startsWith("*")) {
                    for (Pair<String, ArrayList<WEAP>> p : FSPatcher.factWeapons) {
                        if (pressed.startsWith(p.getBase())) {
                            for (WEAP w : p.getVar()) {
                                if(w.getForm().equals(weapon.getForm())) {
                                    p.getVar().remove(w);
                                    break;
                                }
                            }
                            break;
                        }
                    }
                    // Change option text to reflect change
                    for (String s : keys) {
                        if (s.equals(pressed)) {
                            s = s.replace("*", "");
                        }
                    }
                // IF faction option does not contain "*" - ACTION is add to faction
                } else {
                    for (Pair<String, ArrayList<WEAP>> p : FSPatcher.factWeapons) {
                        if (pressed.startsWith(p.getBase())) {
                            p.getVar().add(weapon);
                            break;
                        }
                    }
                    // Change option text to reflect change
                    for (String s : keys) {
                        if (s.equals(pressed)) {
                            s = "*" + s;
                        }
                    }
                }
                // After ACTION is done and keys list updated box items are replaced with new one
                box.removeAllItems();
                for (int i=0;i<keys.size();i++) {
                    box.addItem(keys.get(i));
                    if (keys.get(i).startsWith(pressed) || pressed.startsWith(keys.get(i))) {
                        box.setSelectedIndex(i);
                    }
                }
            }
        }
    }

    public ModPanel(SPMainMenuPanel parent_, Mod m, Mod g) {
        super(parent_, m.toString(), FSPatcher.headerColor);
        myMod = m;
    }

    @Override
    protected void initialize() {
        super.initialize();

        armorKeys = new ArrayList<>(0);
        weaponKeys = new ArrayList<>(0);
        weaponFactions = new ArrayList<>(0);
        
        boolean found = false;
        for (Pair<String, Node> p : FSPatcher.lootifiedMods) {
            if (p.getBase().contentEquals(myMod.getName())) {
                found = true;
                break;
            }
        }
        if (found) {
            LPanel donePanel = new LPanel(100, 100);
            donePanel.setSize(300, 200);
            LLabel allDone = new LLabel(myMod.getNameNoSuffix() + " is already Lootified.", FSPatcher.settingsFont, FSPatcher.settingsColor);
            donePanel.Add(allDone);
            scroll.add(donePanel);
        } else {
            FLST variantArmorKeysFLST = (FLST) FSPatcher.gearVariants.getMajor("LLI_VAR_ARMOR_KEYS", GRUP_TYPE.FLST);
            FLST variantWeaponKeysFLST = (FLST) FSPatcher.gearVariants.getMajor("LLI_VAR_WEAPON_KEYS", GRUP_TYPE.FLST);
            FLST armorMatTypes = (FLST) FSPatcher.gearVariants.getMajor("LLI_ARMOR_MAT_TYPES", GRUP_TYPE.FLST);
            ArrayList<FormID> armorMaterialTypes = armorMatTypes.getFormIDEntries();
            FLST weaponMatTypes = (FLST) FSPatcher.gearVariants.getMajor("LLI_WEAPON_MAT_TYPES", GRUP_TYPE.FLST);
            ArrayList<FormID> weaponMaterialTypes = weaponMatTypes.getFormIDEntries();

            //setupIni();
            ArrayList<FormID> variantArmorKeys = variantArmorKeysFLST.getFormIDEntries();
            ArrayList<String> armorVariantNames = new ArrayList<>(0);
            armorVariantNames.add("None");

            FSPatcher.gearVariants.addAsOverrides(myMod, GRUP_TYPE.FLST, GRUP_TYPE.KYWD, GRUP_TYPE.ARMO, GRUP_TYPE.WEAP);


            weaponBoxes = new ArrayList<>(0);
            armorListeners = new ArrayList<>(0);
            weaponListeners = new ArrayList<>(0);
            outfitListeners = new ArrayList<>(0);

            for (FormID f : variantArmorKeys) {
                MajorRecord maj = FSPatcher.gearVariants.getMajor(f, GRUP_TYPE.KYWD);
                armorVariantNames.add(maj.getEDID());
            }

            LPanel setReset = new LPanel(300, 60);
            setReset.setSize(300, 50);

            LButton setAll = new LButton("Set All");
            setAll.addActionListener(new SetAllListener());
            LButton setNone = new LButton("set none");
            setNone.addActionListener(new SetNoneListener());

            setReset.add(setAll, BorderLayout.WEST);
            setReset.add(setNone);
            setReset.setPlacement(setNone, 150, 0);
            setPlacement(setReset);
            Add(setReset);

            for (ARMO armor : myMod.getArmors()) {
                boolean non_playable = armor.getBodyTemplate().get(BodyTemplate.GeneralFlags.NonPlayable);
                FormID enchant = armor.getEnchantment();
                boolean newItem = armor.getFormMaster().print().contentEquals(myMod.getName());
                if (!non_playable && (enchant.isNull()) && newItem) {
                    LPanel panel = new LPanel(275, 200);
                    panel.setSize(300, 80);
                    LLabel armorName = new LLabel(armor.getName(), FSPatcher.settingsFont, FSPatcher.settingsColor);

                    LComboBox box = new LComboBox("", FSPatcher.settingsFont, FSPatcher.settingsColor);
                    for (String s : armorVariantNames) {
                        box.addItem(s);
                    }

                    KYWD k = ArmorTools.armorHasAnyKeyword(armor, armorMatTypes, FSPatcher.gearVariants);
                    if (k != null) {
                        int index = armorMaterialTypes.indexOf(k.getForm()) + 1; //offset None entry
                        box.setSelectedIndex(index);
                    }
                    ArmorListener al = new ArmorListener(armor, box, armorName);
                    armorListeners.add(al);
                    box.addEnterButton("Set", al);

                    box.setSize(250, 30);
                    panel.add(armorName, BorderLayout.WEST);
                    panel.add(box);
                    panel.setPlacement(box);

                    LTextField outfitField = new LTextField("Outfit name");
                    OutfitListener ol = new OutfitListener(outfitField, armor);
                    outfitListeners.add(ol);
                    
                    outfitField.addEnterButton("set", ol);
                    outfitField.setSize(250, 30);
                    outfitField.setText("");

                    panel.Add(outfitField);
                    panel.setPlacement(outfitField);

                    setPlacement(panel);
                    Add(panel);
                }
            }

            ArrayList<FormID> variantWeaponKeys = variantWeaponKeysFLST.getFormIDEntries();
            ArrayList<String> weaponVariantNames = new ArrayList<>(0);
            weaponVariantNames.add("None");
            for (FormID f : variantWeaponKeys) {
                MajorRecord maj = FSPatcher.gearVariants.getMajor(f, GRUP_TYPE.KYWD);
                weaponVariantNames.add(maj.getEDID());
            }

            for (WEAP weapon : myMod.getWeapons()) {
                boolean non_playable = weapon.get(WEAP.WeaponFlag.NonPlayable);
                boolean bound = weapon.get(WEAP.WeaponFlag.BoundWeapon);
                FormID enchant = weapon.getEnchantment();
                boolean newItem = weapon.getFormMaster().print().contentEquals(myMod.getName());
                if (!non_playable && !bound && (enchant.isNull()) && newItem) {
                    LPanel panel = new LPanel(275, 200);
                    panel.setSize(300, 80);
                    LLabel weaponName = new LLabel(weapon.getName(), FSPatcher.settingsFont, FSPatcher.settingsColor);


                    LComboBox box = new LComboBox("", FSPatcher.settingsFont, FSPatcher.settingsColor);
                    for (String s : weaponVariantNames) {
                        box.addItem(s);
                    }
                    KYWD k = WeaponTools.weaponHasAnyKeyword(weapon, weaponMatTypes, FSPatcher.gearVariants);
                    if (k != null) {
                        int index = weaponMaterialTypes.indexOf(k.getForm()) + 1; //offset None entry
                        box.setSelectedIndex(index);
                    }

                    String set = "set";
                    WeaponListener wl = new WeaponListener(weapon, box, weaponName);
                    weaponListeners.add(wl);
                    box.addEnterButton(set, wl);
                    box.setSize(250, 30);
                    panel.add(weaponName, BorderLayout.WEST);
                    panel.add(box);
                    panel.setPlacement(box);

                    weaponBoxes.add(box);
                    
                    LComboBox factbox = new LComboBox("", FSPatcher.settingsFont, FSPatcher.settingsColor);
                    factbox.addItem("");
                    for (String s : FactionKeys) {
                        factbox.addItem(s);
                    }
                    box.setSelectedIndex(0);
                    factbox.addEnterButton("Set/Unset", new FactionListener(weapon,factbox));
                    factbox.setSize(250, 30);
                    panel.add(factbox);
                    panel.setPlacement(factbox);

                    setPlacement(panel);
                    Add(panel);
                }
            }
        }

    }
    
    public void FindRemoveOutfit(String setKey, ActionEvent e) {
        for (OutfitListener ol : outfitListeners) {
            if(ol.setKey != null) {
                if(ol.setKey.equals(setKey)) {
                    ol.field.setText("");
                    ol.actionPerformed(e);
                }
            }
        }
    }
    
    public void FindRemoveArmor(String setKey, ARMO a, ActionEvent e) {
        for (OutfitListener ol : outfitListeners) {
            if(ol.setKey != null) {
                if (ol.setKey.equals(setKey) && ol.armor.equals(a)) {
                    ol.field.setText("");
                    ol.actionPerformed(e);
                }
            }
                
        }
    }
}
