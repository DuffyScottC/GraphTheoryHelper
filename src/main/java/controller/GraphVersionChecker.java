/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import views.GraphFrame;
import views.NewVersionDialog;

/**
 * Checks to see whether a new update is available for the program on 
 * GitHub.com when the user opens the program.
 * @author Scott
 */
public class GraphVersionChecker {
    
    private NewVersionDialog newVersionDialog;
    
    private final int[] currentVersion = {1, 0, 1};
    private final StringBuilder description = new StringBuilder("<html>");
    
    /**
     * Initializes elements and adds action listeners.
     * @param frame 
     */
    public GraphVersionChecker(GraphFrame frame) {
        newVersionDialog = new NewVersionDialog(frame, true);

        newVersionDialog.getLinkButton().addActionListener((ActionEvent e) -> {
            try {
                Desktop.getDesktop().browse(
                        new URI("https://github.com/DuffyScottC/GraphTheoryHelper/releases/latest"));
            } catch (IOException ex) {
                System.out.println(ex.toString());
            } catch (URISyntaxException ex) {
                System.out.println("Improper URL: " + ex.toString());
            }
        });
        
        newVersionDialog.getCloseButton().addActionListener((ActionEvent e) -> {
            newVersionDialog.setVisible(false);
        });
        
        newVersionDialog.getCopyLinkButton().addActionListener((ActionEvent e) -> {
            String myString = "https://github.com/DuffyScottC/GraphTheoryHelper/releases/latest";
            StringSelection stringSelection = new StringSelection(myString);
            Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
            clpbrd.setContents(stringSelection, null);
        });
    }
    
    /**
     * Run at the beginning of the program. Checks so see if the current version
     * is out of date (by checking 
     * https://github.com/DuffyScottC/GraphTheoryHelper/releases/latest to see
     * if I have uploaded a new version). If there is a newer version, this
     * presents a dialog to the user telling them that a new version is 
     * available. <>
     * Gives up if we cannot find the right information on the web page,
     * if we can't connect, etc.
     * @param autoOpen If true, this function will open the dialog 
     * automatically if there is a new version.
     */
    public void checkVersion(boolean autoOpen) {
        try {
            //get the latest version web page from github
            URL github = new URL("https://github.com/DuffyScottC/GraphTheoryHelper/releases/latest");
//            URL github = new URL("https://github.com/DuffyScottC/Minecraft_Vanilla_Interdiction_Torch/releases/latest");

            //get a bufferedReader to read the html line by line
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(github.openStream()));
            
            //True if we are storing the description of the latest version
            boolean gettingDescription = false;
            //holds the latest version of the application
            int[] latestVersion = null; //starts out null
            //holds the input line from the html
            String inputLine;
            //cycle through all the lines of the html
            while ((inputLine = in.readLine()) != null) {
                //if we have not yet found a version number
                if (latestVersion == null) {
                    //check if this line has a version number and store it
                    latestVersion = getLatestVersion(currentVersion, inputLine);
                }
                
                //if the line matches "<div class="markdown-body">"
                if (inputLine.matches(".*<div\\sclass=\"markdown-body\">")) {
                    //we can start storing lines of the description
                    gettingDescription = true;
                }
                
                //if we are actively storing lines of the description
                if (gettingDescription) {
                    //add this line to the description
                    gettingDescription = getDescription(inputLine);
                }
            }
            in.close();
            
            //if we get here and latestVersion is still null, then this page 
            //has no version number in it
            if (latestVersion == null) {
                System.out.println("Unable to find latest version on page.");
                clearDialog();
                return;
            }
            
            //add the final html tag to the description
            description.append("</html>");
            
            //if currentVersion is outdated
            if (isCurrentOutdated(currentVersion, latestVersion)) {
                setUpDialog(latestVersion);
                if (autoOpen) { //if we want to open this now
                    openDialog();
                }
            } else {
                clearDialog();
                System.out.println("You're version is up to date!");
            }
            
        } catch (ConnectException e) {
            clearDialog();
            System.out.println("Could not connect to github: " + e.toString());
        } catch (IOException e) {
            clearDialog();
            System.out.println("error finding url or reading in " + e.toString());
        } catch (Exception e) {
            clearDialog();
            System.out.println(e.toString());
        }
    }
    
    /**
     * Checks if the input line contains a version number URL (e.g. "tree/v1.0.2")
     * and outputs the version number as an integer array of size 3.
     * @param currentVersion The current version of the application
     * @param inputLine The line to check if it contains a version number URL
     * @return An integer array of size 3 if there was a version number to be
     * found inside of inputLine; null if there was no version number to be found
     * inside of inputLine.
     */
    private int[] getLatestVersion(int[] currentVersion, String inputLine) {
        //check if this line has a string of the format "tree/v1.0.2"
        //or some other version number.
        String treeVersionRegex = "(tree\\/v)(\\d)\\.(\\d)\\.(\\d)"; //the regex to match
        Pattern treeVersionP = Pattern.compile(treeVersionRegex); //the pattern with the regex
        Matcher treeVersionM = treeVersionP.matcher(inputLine); //the matcher for the line

        //every time we find the string in a line:
        while (treeVersionM.find()) {
            //convert this version number string to an integer array
            int[] latestVersion = new int[3];
            latestVersion[0] = Integer.parseInt(treeVersionM.group(2));
            latestVersion[1] = Integer.parseInt(treeVersionM.group(3));
            latestVersion[2] = Integer.parseInt(treeVersionM.group(4));
            return latestVersion; //we found a version number in inputLine
        }
        return null; //there was no version number in inputLine
    }
    
    private boolean getDescription(String inputLine) {
        if (inputLine.matches(".*<\\/div>")) { //</div> end of description
            return false; //stop adding to the description
        } else {
            description.append(inputLine);
        }
        return true; //keep adding to the description
    }
    
    /**
     *
     * @param current
     * @param latest
     * @return true if current is earlier than latest, false if not (in other
     * words, <b>true if the current program is outdated</b> and false if it is
     * up to date.)
     */
    private boolean isCurrentOutdated(int[] current, int[] latest) {
        if (latest[0] > current[0]) { //v2._._ > v1._._
            return true;
        } else { //v1._._ = v1._._ (or v1._._ < v2._._)
            if (latest[1] > current[1]) { //v1.2._ > v1.1._
                return true;
            } else { //v1.1._ = v1.1._ (or v1.1._ < v1.2._)
                if (latest[2] > current[2]) { //v1.1.2 > v1.1.1
                    return true;
                } //v1.1.1 = v1.1.1 (or v1.1.1 < v1.1.2)
            }
        }
        //if all three numbers are equal, then we are up to date
        return false;
    }
    
    /**
     * Opens the NewVersionDialog object. If checkForNewVersion is true,
     * it runs a check to see if there is a new version. If checkForNewVersion
     * is false, it skips that step and just shows the current version.
     */
    public void openDialog() {
        newVersionDialog.setLocationRelativeTo(null);
        newVersionDialog.setTitle("New Version Available");
        newVersionDialog.setVisible(true);
    }
    
    /**
     * Set up the text in the dialog to show results. 
     * @param latestVersion 
     */
    private void setUpDialog(int[] latestVersion) {
        newVersionDialog.getChangelogTextPane().setText(description.toString());
        newVersionDialog.getInfoLabel().setText("<html>\n" +
                "<p>Current Version: " + versionToString(currentVersion) + "</p>\n" +
                "<p>There is a new update available: " + versionToString(latestVersion) + "</p>\n" +
                "<p>Download it here: </p>\n" +
                "</html>");
    }
    
    private String versionToString(int[] version) {
        return "v" + version[0] + "." + version[1] + "." + version[2];
    }

    private void clearDialog() {
        newVersionDialog.getChangelogTextPane().setText("");
        newVersionDialog.getInfoLabel().setText("<html>\n" +
                "<p>Current Version: v"+currentVersion[0]+"."+currentVersion[1]+"."+currentVersion[2]+"</p>\n" +
                "<p></p>\n" +
                "<p>Check for updates here: </p>\n" +
                "</html>");
    }
    
}
