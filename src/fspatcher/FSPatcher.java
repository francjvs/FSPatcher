package fspatcher;

import java.awt.Color;
import java.awt.Font;
import java.net.URL;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import lev.gui.LSaveFile;
import skyproc.*;
import skyproc.gui.SPMainMenuPanel;
import skyproc.gui.SUM;
import skyproc.gui.SUMGUI;
import fspatcher.YourSaveFile.Settings;
import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import lev.gui.LPanel;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import skyproc.gui.SPProgressBarPlug;

/**
 *
 * @author Francisco Silva
 */
public class FSPatcher implements SUM {

    /*
     * The important functions to change are:
     * - getStandardMenu(), where you set up the GUI
     * - runChangesToPatch(), where you put all the processing code and add records to the output patch.
     */

    /*
     * The types of records you want your patcher to import. Change this to
     * customize the import to what you need.
     */
    GRUP_TYPE[] importRequests = new GRUP_TYPE[]{
	GRUP_TYPE.LVLI,
        GRUP_TYPE.ARMO,
        GRUP_TYPE.WEAP,
        GRUP_TYPE.FLST,
        GRUP_TYPE.KYWD,
        GRUP_TYPE.OTFT,
        GRUP_TYPE.NPC_
    };
    public static String myPatchName = "FS Patcher";
    public static String authorName = "francjvs";
    public static String version = "0.1";
    public static String welcomeText = "Lootifies Weapons and Armor.  "
	    + "Based HEAVILY on Diene's Lootification";
    public static String descriptionToShowInSUM = "Lootifies Weapons and Armor.";
    public static Color headerColor = new Color(66, 181, 184);  // Teal
    public static Color settingsColor = new Color(72, 179, 58);  // Green
    public static Font settingsFont = new Font("Serif", Font.BOLD, 15);
    public static SkyProcSave save = new YourSaveFile();
    
    public static ArrayList<Mod> activeMods = new ArrayList<>(0);
    public static Mod gearVariants;
    public static Mod global;
    public static ArrayList<Pair<String, ArrayList<ARMO>>> outfits = new ArrayList<>(0);
    public static ArrayList<Pair<String, ArrayList<String>>> tiers = new ArrayList<>(0);
    public static ArrayList<Pair<String, ArrayList<WEAP>>> factWeapons = new ArrayList<>(0);
    public static ArrayList<Pair<Mod, ArrayList<Pair<ARMO, KYWD>>>> modArmors = new ArrayList<>(0);
    public static ArrayList<Pair<Mod, ArrayList<Pair<WEAP, KYWD>>>> modWeapons = new ArrayList<>(0);
    public static boolean listify = false;
    public static ArrayList<Pair<String, Node>> lootifiedMods = new ArrayList<>(0);
    public static ArrayList<ModPanel> modPanels = new ArrayList<>(0);
    public static ArrayList<Pair<String,LPanel>> outfitPanels;

    public static enum lk {
        err;
    };
    
    // Do not write the bulk of your program here
    // Instead, write your patch changes in the "runChangesToPatch" function
    // at the bottom
    public static void main(String[] args) {
	try {
	    SPGlobal.createGlobalLog();
            SPGlobal.newSpecialLog(lk.err, "lli_crash.txt");
	    SUMGUI.open(new FSPatcher(), args);
	} catch (Exception e) {
	    // If a major error happens, print it everywhere and display a message box.
	    System.err.println(e.toString());
	    SPGlobal.logException(e);
	    JOptionPane.showMessageDialog(null, "There was an exception thrown during program execution: '" + e + "'  Check the debug logs or contact the author.");
	    SPGlobal.closeDebug();
	}
    }

    @Override
    public String getName() {
	return myPatchName;
    }

    // This function labels any record types that you "multiply".
    // For example, if you took all the armors in a mod list and made 3 copies,
    // you would put ARMO here.
    // This is to help monitor/prevent issues where multiple SkyProc patchers
    // multiply the same record type to yeild a huge number of records.
    @Override
    public GRUP_TYPE[] dangerousRecordReport() {
	// None
	return new GRUP_TYPE[0];
    }

    @Override
    public GRUP_TYPE[] importRequests() {
	return importRequests;
    }

    @Override
    public boolean importAtStart() {
	return false;
    }

    @Override
    public boolean hasStandardMenu() {
	return true;
    }

    // This is where you add panels to the main menu.
    // First create custom panel classes (as shown by YourFirstSettingsPanel),
    // Then add them here.
    @Override
    public SPMainMenuPanel getStandardMenu() {
	final SPMainMenuPanel settingsMenu = new SPMainMenuPanel(getHeaderColor());

	settingsMenu.setWelcomePanel(new WelcomePanel(settingsMenu));
	settingsMenu.addMenu(new OtherSettingsPanel(settingsMenu), false, save, Settings.OTHER_SETTINGS);

        Runnable r = new Runnable() {
            @Override
            public void run() {
                theInitFunction();

                for (Mod m : activeMods) {
                    ModPanel panel = new ModPanel(settingsMenu, m, global);
                    modPanels.add(panel);
                    settingsMenu.addMenu(panel);
                }

                settingsMenu.addMenu(new OutfitsPanel(settingsMenu), false, save, Settings.OTHER_SETTINGS);

                settingsMenu.updateUI();
            }
        };
        SUMGUI.startImport(r);
        
	return settingsMenu;
    }

    // Usually false unless you want to make your own GUI
    @Override
    public boolean hasCustomMenu() {
	return false;
    }

    @Override
    public JFrame openCustomMenu() {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean hasLogo() {
	return false;
    }

    @Override
    public URL getLogo() {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean hasSave() {
	return true;
    }

    @Override
    public LSaveFile getSave() {
	return save;
    }

    @Override
    public String getVersion() {
	return version;
    }

    @Override
    public ModListing getListing() {
	return new ModListing(getName(), false);
    }

    @Override
    public Mod getExportPatch() {
	Mod out = new Mod(getListing());
	out.setAuthor(authorName);
	return out;
    }

    @Override
    public Color getHeaderColor() {
	return headerColor;
    }

    // Add any custom checks to determine if a patch is needed.
    // On Automatic Variants, this function would check if any new packages were
    // added or removed.
    @Override
    public boolean needsPatching() {
	return false;
    }

    // This function runs when the program opens to "set things up"
    // It runs right after the save file is loaded, and before the GUI is displayed
    @Override
    public void onStart() throws Exception {
        
        Runnable r = new Runnable() {
            @Override
            public void run() {
            }
        };
        SUMGUI.startImport(r);

        File fXmlFile = new File("Lootification.xml");
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(fXmlFile);
        doc.getDocumentElement().normalize();

        NodeList mList = doc.getElementsByTagName("mod");
        for (int i = 0; i < mList.getLength(); i++) {
            Node nMod = mList.item(i);
            Element mod = (Element) nMod;
            lootifiedMods.add(new Pair<>(mod.getAttribute("modName"), nMod));
        }

        File CustomXmlFile = new File("Custom.xml");
        Document cDoc = dBuilder.parse(CustomXmlFile);
        cDoc.getDocumentElement().normalize();

        mList = cDoc.getElementsByTagName("mod");
        for (int i = 0; i < mList.getLength(); i++) {
            Node nMod = mList.item(i);
            Element mod = (Element) nMod;
            Pair<String, Node> p = new Pair<>(mod.getAttribute("modName"), nMod);
            boolean found = false;
            for (Pair<String, Node> q : lootifiedMods) {
                if (q.getBase().contentEquals(p.getBase())) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                lootifiedMods.add(p);
            }
        }
        
    }

    // This function runs right as the program is about to close.
    @Override
    public void onExit(boolean patchWasGenerated) throws Exception {
    }

    // Add any mods that you REQUIRE to be present in order to patch.
    @Override
    public ArrayList<ModListing> requiredMods() {
	ArrayList<ModListing> req = new ArrayList<>(0);
        ModListing gearVariants = new ModListing("FSConvergence", true);
        req.add(gearVariants);
        return req;
    }

    @Override
    public String description() {
	return descriptionToShowInSUM;
    }

    // This is where you should write the bulk of your code.
    // Write the changes you would like to make to the patch,
    // but DO NOT export it.  Exporting is handled internally.
    @Override
    public void runChangesToPatch() throws Exception {

	Mod patch = SPGlobal.getGlobalPatch();

	Mod merger = new Mod(getName() + "Merger", false);
	merger.addAsOverrides(SPGlobal.getDB());

	// Write your changes to the patch here.
        
        for (ModPanel mPanel : modPanels) {
            boolean found = false;
            if (!mPanel.armorKeys.isEmpty()) {
                for (Pair<Mod, ArrayList<Pair<ARMO, KYWD>>> p : modArmors) {
                    if (p.getBase().equals(mPanel.myMod)) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    Pair<Mod, ArrayList<Pair<ARMO, KYWD>>> p = new Pair<>(mPanel.myMod, mPanel.armorKeys);
                    modArmors.add(p);
                }
            }
            found = false;
            if (!mPanel.weaponKeys.isEmpty()) {
                for (Pair<Mod, ArrayList<Pair<WEAP, KYWD>>> p : modWeapons) {
                    if (p.getBase().equals(mPanel.myMod)) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    Pair<Mod, ArrayList<Pair<WEAP, KYWD>>> p = new Pair<>(mPanel.myMod, mPanel.weaponKeys);
                    modWeapons.add(p);
                }
            }
        }

        SPProgressBarPlug.setStatus("Processing XML");
        XMLTools.addModsToXML(merger);
        XMLTools.processXML(merger, patch);


        FLST baseArmorKeysFLST = (FLST) merger.getMajor("LLI_BASE_ARMOR_KEYS", GRUP_TYPE.FLST);
        FLST variantArmorKeysFLST = (FLST) merger.getMajor("LLI_VAR_ARMOR_KEYS", GRUP_TYPE.FLST);
//        SPGlobal.log("base armor key formlist", baseArmorKeysFLST.getEDID());
//        SPGlobal.log("variant armor keywords", variantArmorKeysFLST.getEDID());
        FLST baseWeaponKeysFLST = (FLST) merger.getMajor("LLI_BASE_WEAPON_KEYS", GRUP_TYPE.FLST);
        FLST variantWeaponKeysFLST = (FLST) merger.getMajor("LLI_VAR_WEAPON_KEYS", GRUP_TYPE.FLST);




        boolean lootify = true; //save.getBool(Settings.LOOTIFY_MOD);
        if (lootify) {
            SPProgressBarPlug.setStatus("Setting up armor matches");
            ArmorTools.setupArmorMatches(baseArmorKeysFLST, variantArmorKeysFLST, merger);
            SPProgressBarPlug.setStatus("Building base armors");
            ArmorTools.buildArmorBases(merger, baseArmorKeysFLST);
            SPProgressBarPlug.setStatus("Setting up armor sets");
            ArmorTools.setupSets(merger, patch);
            SPProgressBarPlug.setStatus("Building armor variants");
            ArmorTools.buildArmorVariants(merger, patch, baseArmorKeysFLST, variantArmorKeysFLST);
            SPProgressBarPlug.setStatus("Setting up armor leveled lists");
            ArmorTools.modLVLIArmors(merger, patch);
            SPProgressBarPlug.setStatus("Processing outfit armors");
            ArmorTools.buildOutfitsArmors(baseArmorKeysFLST, merger, patch);
            SPProgressBarPlug.setStatus("Linking armor leveled lists");
            ArmorTools.linkLVLIArmors(baseArmorKeysFLST, merger, patch);
            SPProgressBarPlug.setStatus("Linking armor in NPC inventory");
            ArmorTools.linkINVArmors(baseArmorKeysFLST, merger, patch);

            WeaponTools.setMergeAndPatch(merger, patch);
            SPProgressBarPlug.setStatus("Setting up weapon matches");
            WeaponTools.setupWeaponMatches(baseWeaponKeysFLST, variantWeaponKeysFLST, merger);
            SPProgressBarPlug.setStatus("Building base weapons");
            WeaponTools.buildWeaponBases(baseWeaponKeysFLST);
            SPProgressBarPlug.setStatus("Building weapon variants");
            WeaponTools.buildWeaponVariants(baseWeaponKeysFLST, variantWeaponKeysFLST);
            SPProgressBarPlug.setStatus("Setting up weapon leveled lists");
            WeaponTools.modLVLIWeapons();
            SPProgressBarPlug.setStatus("Processing outfit weapons");
            WeaponTools.buildOutfitWeapons(baseWeaponKeysFLST);
            SPProgressBarPlug.setStatus("Linking weapon leveled lists");
            WeaponTools.linkLVLIWeapons(baseWeaponKeysFLST);
            SPProgressBarPlug.setStatus("Linking weapon in NPC inventory");
            WeaponTools.linkINVWeapons(baseWeaponKeysFLST);
        }
        
    }

    // OTHER FUNCTIONS
    
    public void theInitFunction() {
        try {
            ArrayList<ModListing> activeModListing = SPImporter.getActiveModList();
            ArrayList<Mod> allMods = new ArrayList<>(0);

            gearVariants = new Mod(getName() + "MergerTemp", false);
            gearVariants.addAsOverrides(SPGlobal.getDB());

            for (ModListing listing : activeModListing) {
                Mod newMod = new Mod(listing);
                allMods.add(newMod);
            }

            for (ARMO armor : gearVariants.getArmors()) {
                allMods.get(activeModListing.indexOf(armor.getFormMaster())).addRecord(armor);
                KeywordSet keys = armor.getKeywordSet();
                for (FormID form : keys.getKeywordRefs()) {
                    KYWD key = (KYWD) gearVariants.getMajor(form, GRUP_TYPE.KYWD);
                    if (key == null) {
                        String error = armor.getEDID() 
                                + " has an invalid keyword reference: "+ form 
                                + " The patch will fail. Clean it in tes5edit and rerun the patcher.";
                        Exception e = new Exception(error);
                        JOptionPane.showMessageDialog(null, e.toString());
                        throw e;
                    }
                }
            }
            for (WEAP weapon : gearVariants.getWeapons()) {
                allMods.get(activeModListing.indexOf(weapon.getFormMaster())).addRecord(weapon);
                KeywordSet keys = weapon.getKeywordSet();
                for (FormID form : keys.getKeywordRefs()) {
                    KYWD key = (KYWD) gearVariants.getMajor(form, GRUP_TYPE.KYWD);
                    if (key == null) {
                        String error = weapon.getEDID() 
                                + " has an invalid keyword reference: "+ form 
                                + " The patch will fail. Clean it in tes5edit and rerun the patcher.";
                        Exception e = new Exception(error);
                        JOptionPane.showMessageDialog(null, e.toString());
                        throw e;
                    }
                }
            }
            for (OTFT o : gearVariants.getOutfits()) {
                ArrayList<FormID> items = o.getInventoryList();
                for (FormID f : items) {
                    LVLI litem = (LVLI) gearVariants.getMajor(f, GRUP_TYPE.LVLI);
                    ARMO arm = (ARMO) gearVariants.getMajor(f, GRUP_TYPE.ARMO);
                    WEAP weapon = (WEAP) gearVariants.getMajor(f, GRUP_TYPE.WEAP);
                    if( (litem == null)&&(arm==null)&&(weapon==null) ){
                        String error = o.getEDID() 
                                + " has an invalid entry: "+ f 
                                + " The patch will fail. Clean it in tes5edit and rerun the patcher.";
                        Exception e = new Exception(error);
                        JOptionPane.showMessageDialog(null, e.toString());
                        throw e;
                    }
                }
            }

            for (Mod m : allMods) {
                String modName = m.getName();

                if (!(modName.contentEquals("Skyrim.esm") || (modName.contentEquals("FSConvergence.esm")) || modName.contentEquals("HearthFires.esm")
                        || modName.contentEquals("Update.esm") || modName.contentEquals("Dragonborn.esm") || modName.contentEquals("Dawnguard.esm"))) {
                    int numArmors = m.getArmors().size();
                    int numWeapons = m.getWeapons().size();

                    if (numArmors > 0 || numWeapons > 0) {
                        activeMods.add(m);
                    }

                }
            }
        } catch (Exception e) {
            throw new RuntimeException (e.getMessage());
        }
    }
    
    
}
