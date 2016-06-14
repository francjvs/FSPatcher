/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fspatcher;

import lev.gui.LCheckBox;
import lev.gui.LComboBox;
import skyproc.SPGlobal;
import skyproc.gui.SPMainMenuPanel;
import skyproc.gui.SPSettingPanel;
import skyproc.gui.SUMGUI;

/**
 *
 * @author Justin Swanson
 */
public class OtherSettingsPanel extends SPSettingPanel {

    LCheckBox importOnStartup;
    LCheckBox LootifyDragonborn;
    LCheckBox SkipInactiveMods;
    LCheckBox UseMatchingOutfits;
    LCheckBox processAMMO;

    public OtherSettingsPanel(SPMainMenuPanel parent_) {
	super(parent_, "Other Settings", FSPatcher.headerColor);
    }

    @Override
    protected void initialize() {
	super.initialize();

	importOnStartup = new LCheckBox("Import Mods on Startup", FSPatcher.settingsFont, FSPatcher.settingsColor);
	importOnStartup.tie(YourSaveFile.Settings.IMPORT_AT_START, FSPatcher.save, SUMGUI.helpPanel, true);
	importOnStartup.setOffset(2);
	importOnStartup.addShadow();
	setPlacement(importOnStartup);
	AddSetting(importOnStartup);
        
        LootifyDragonborn = new LCheckBox("Lootify the Dragonborn DLC items", FSPatcher.settingsFont, FSPatcher.settingsColor);
        LootifyDragonborn.tie(YourSaveFile.Settings.LOOTIFY_DRAGONBORN, FSPatcher.save, SUMGUI.helpPanel, true);
        setPlacement(LootifyDragonborn);
        AddSetting(LootifyDragonborn);
        
        SkipInactiveMods = new LCheckBox("Don't process inactive mods", FSPatcher.settingsFont, FSPatcher.settingsColor);
        SkipInactiveMods.tie(YourSaveFile.Settings.SKIP_INACTIVE_MODS, FSPatcher.save, SUMGUI.helpPanel, true);
        setPlacement(SkipInactiveMods);
        AddSetting(SkipInactiveMods);
        
        UseMatchingOutfits = new LCheckBox("Match outfits when possible", FSPatcher.settingsFont, FSPatcher.settingsColor);
        UseMatchingOutfits.tie(YourSaveFile.Settings.USE_MATCHING_OUTFITS, FSPatcher.save, SUMGUI.helpPanel, true);
        setPlacement(UseMatchingOutfits);
        AddSetting(UseMatchingOutfits);
        
        processAMMO = new LCheckBox("Process AMMO", FSPatcher.settingsFont, FSPatcher.settingsColor);
        processAMMO.tie(YourSaveFile.Settings.PROCESS_AMMO, FSPatcher.save, SUMGUI.helpPanel, true);
        setPlacement(processAMMO);
        AddSetting(processAMMO);

	alignRight();

    }
}
