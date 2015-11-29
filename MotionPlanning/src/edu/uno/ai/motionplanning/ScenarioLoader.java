/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uno.ai.motionplanning;

import java.io.*;
import java.util.*;

/**
 *
 * @author jgrimm
 */
public class ScenarioLoader {

    File baseDirectory;
    File scenarioDirectory;

    public ScenarioLoader(File baseDirectory, File scenarioDirectory) {
        this.baseDirectory = baseDirectory;
        this.scenarioDirectory = scenarioDirectory;
    }

    public List<Scenario> loadAllScenarios(String filename) {
        ArrayList<Scenario> scenarios = new ArrayList<Scenario>();
        try {

            File path = new File(filename);
            if (!path.exists()) {
                path = new File(scenarioDirectory, filename);
            }
            FileInputStream fs = new FileInputStream(path);
            InputStreamReader isr = new InputStreamReader(fs);
            BufferedReader br = new BufferedReader(isr);
            int line = 0;
            String text = null;
            while ((text = br.readLine()) != null) {
                line++;
                if (line == 1 && text.contains("version")) {
                    continue;
                }
                String[] tokens = text.split("\\s");
                if (tokens.length<9) break; 
                String groupStr = tokens[0];
                String map = tokens[1];
                String widthStr = tokens[2];
                String heightStr = tokens[3];
                String startxStr = tokens[4];
                String startyStr = tokens[5];
                String endxStr = tokens[6];
                String endyStr = tokens[7];
                String optimalLengthStr = tokens[8];
                int group = Integer.parseInt(groupStr);
                int width = Integer.parseInt(widthStr);
                int height = Integer.parseInt(heightStr);
                int startx = Integer.parseInt(startxStr);
                int starty = Integer.parseInt(startyStr);
                int endx = Integer.parseInt(endxStr);
                int endy = Integer.parseInt(endyStr);
                float optimalLength = Float.parseFloat(optimalLengthStr);
                GridMap scenMap = GridMap.load(new File(baseDirectory, map).toString());
                if (scenMap == null) {
                    System.out.println("Unable to load map file " + filename + " line " + line);
                    
                    continue;
                }
                if (scenMap.getWidth() != width || scenMap.getHeight() != height) {
                    //System.out.println("Map scaling not supported " + filename + " line " + line);
                    continue;
                }
                scenarios.add(new Scenario(scenMap, startx, starty, endx, endy, optimalLength, group));
            }
            br.close();
            isr.close();
            fs.close();
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        return scenarios;
    }

    public List<Scenario> loadAllScenarios() {
        ArrayList<Scenario> scenarios = new ArrayList<>();
        ArrayList<File> scenarioFiles = new ArrayList<>();
        File[] scenarioFilesArray = scenarioDirectory.listFiles(new ScenarioFilter());
        scenarioFiles.addAll(Arrays.asList(scenarioFilesArray));
        int count = 0;
        for (int i = 0; i < scenarioFiles.size(); i++) {
            File scenarioFile = scenarioFiles.get(i);
            //System.out.println(scenarioFile.toString());
            if (scenarioFile.isDirectory()) {
                scenarioFilesArray = scenarioFile.listFiles(new ScenarioFilter());
                if (scenarioFilesArray != null) {
                    scenarioFiles.addAll(Arrays.asList(scenarioFilesArray));
                }
                scenarioFiles.remove(i);
                i--;
            } else {
                List<Scenario> fileScenarios = loadAllScenarios(scenarioFile.toString());
                //System.out.println(scenarioFile.toString() + ": " + fileScenarios.size());
                count += fileScenarios.size();
                scenarios.addAll(fileScenarios);
            }
        }
        System.out.println("Total loadable scenarios: " + count);
        return scenarios;
    }

    class ScenarioFilter implements FileFilter {

        @Override
        public boolean accept(File pathname) {
            if (pathname.toString().endsWith(".scen")) {
                return true;
            } else if (pathname.isDirectory()) {
                return true;
            }
            return false;

        }

    }
}
