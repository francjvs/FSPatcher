/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fspatcher;

import lev.gui.LTextPane;
import skyproc.gui.SPMainMenuPanel;
import skyproc.gui.SPSettingPanel;

/**
 *
 * @author Justin Swanson
 */
public class WelcomePanel extends SPSettingPanel {

    LTextPane introText;

    public WelcomePanel(SPMainMenuPanel parent_) {
	super(parent_, FSPatcher.myPatchName, FSPatcher.headerColor);
    }

    @Override
    protected void initialize() {
	super.initialize();

	introText = new LTextPane(settingsPanel.getWidth() - 40, 400, FSPatcher.settingsColor);
	introText.setText(FSPatcher.welcomeText);
	introText.setEditable(false);
	introText.setFont(FSPatcher.settingsFont);
	introText.setCentered();
	setPlacement(introText);
	Add(introText);

	alignRight();
    }
}
