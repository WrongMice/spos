import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
class MNTEntry {
    int index;
    int mdtIndex;
    MNTEntry(int index, int mdtIndex) {
        this.index = index;
        this.mdtIndex = mdtIndex;
    }
}
class ArgumentEntry {
    int index;
    String value;
    ArgumentEntry(int index, String value) {
        this.index = index;
        this.value = value;
    }
}
class Macro {
    HashMap<String, ArgumentEntry> argumentList = new HashMap<String, ArgumentEntry>();
    HashMap<String, MNTEntry> mntEntryTable = new HashMap<String, MNTEntry>();
    HashMap<Integer, String> mdtEntryTable = new HashMap<Integer, String>();
    public static void main(String[] args) {
/**
 * Some test case may fail accordingly changes required!
 */
        Macro macro1 = new Macro();
        try {
            macro1.passOne();
            macro1.passTwo();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("File exception is occurred!");
        }
    }
    public void passOne() throws IOException {
        int mdtIndex = 1;
        int mntIndex = 1;
        int argumentListIndex = 1;
        FileReader inputFile = new FileReader("C:\\Users\\gades\\Desktop\\Minput.txt");
        BufferedReader inputFileReader = new BufferedReader(inputFile);
        FileWriter mntTable = new FileWriter("mnt.txt");
        FileWriter mdtTable = new FileWriter("mdt.txt");
        FileWriter intermediateFile = new FileWriter("ic.txt");
        String currentLine = "";
        boolean isMacroFound = false;
        while ((currentLine = inputFileReader.readLine()) != null) {
            String[] line = currentLine.split("\\s+");
            if (line[0].equals("MACRO")) {
                isMacroFound = true;
                currentLine = inputFileReader.readLine();
                line = currentLine.split("\\s+");
                boolean isLabelFound = false;
// check for label and put it into argument list
                if (line.length > 1 && line[0].contains("&")) {
                    line[0] = line[0].replaceAll("[&, ]", "");
                    argumentList.put(line[0], new ArgumentEntry(argumentListIndex,
                            ""));
                    argumentListIndex++;
                    isLabelFound = true;

                }
                String macroName;
                if (isLabelFound) {
                    macroName = line[1];
                } else {
                    macroName = line[0];
                }
// entry in the mnt table
                mntEntryTable.put(macroName, new MNTEntry(mntIndex, mdtIndex));
                mntTable.write(mntIndex + "\t" + macroName + "\t" + mdtIndex + "\n");
                mntIndex++;
                if (line.length <= 1)
                    continue;
                int index = 1;
                if (isLabelFound) {
                    index = 2;
                }
                for (int i = index; i < line.length; i++) {
                    line[i] = line[i].replaceAll("[&, ]", "");
                    if (line[i].contains("=")) {
                        String[] temp = line[i].split("=");
                        if (temp.length > 1) {
                            argumentList.put(temp[0], new
                                    ArgumentEntry(argumentListIndex, temp[1]));
                        } else {
                            argumentList.put(temp[0], new
                                    ArgumentEntry(argumentListIndex, ""));
                        }

                        argumentListIndex++;

                    } else {
                        argumentList.put(line[i], new ArgumentEntry(argumentListIndex,
                                ""));
                        argumentListIndex++;
                    }
                }
// enter in macro definition table
                mdtEntryTable.put(mdtIndex, currentLine);
                mdtTable.write(currentLine);
                mdtTable.write("\n");
                mdtIndex++;
            } else if (line[0].equals("MEND")) {
                mdtEntryTable.put(mdtIndex, line[0]);
                mdtTable.write(line[0] + "\n");
                mdtIndex++;
                isMacroFound = false;
            } else if (isMacroFound) {
// check for the argument and replace it with the argument index
                String temp = "";
                for (int i = 0; i < line.length; i++) {
                    if (line[i].contains("&")) {
                        line[i] = line[i].replaceAll("[&, ]", "");
                        line[i] = "#" + argumentList.get(line[i]).index;
                    }

                    mdtTable.write(line[i] + "\t");
                    temp += line[i] + "\t";
                }
                mdtEntryTable.put(mdtIndex, temp);
                mdtTable.write("\n");
                mdtIndex++;
            } else {
                intermediateFile.write(currentLine + "\n");
            }
        }
        FileWriter argumentTable = new FileWriter("argument.txt");
        System.out.println("-------------- Argument List --------------");
        for (Entry<String, ArgumentEntry> table : argumentList.entrySet()) {
            System.out.println(table.getValue().index + "\t" + table.getKey());
            argumentTable.write(table.getValue().index + "\t" + table.getKey() + "\n");
        }
        System.out.println("-------------- MNT Table --------------");
        for (Entry<String, MNTEntry> table : mntEntryTable.entrySet()) {
            System.out.println(table.getValue().index + "\t" + table.getKey() + "\t" +
                    table.getValue().mdtIndex);
        }
        System.out.println("-------------- MDT Table --------------");
        for (Entry<Integer, String> table : mdtEntryTable.entrySet()) {
            System.out.println(table.getKey() + "\t" + table.getValue());
        }
        argumentTable.close();
        inputFileReader.close();
        mntTable.close();
        mdtTable.close();
        intermediateFile.close();
    }
    public void passTwo() throws IOException {
        FileReader icFile = new FileReader("ic.txt");
        BufferedReader icFileReader = new BufferedReader(icFile);
        FileWriter expandedFile = new FileWriter("expanded.txt");
        String currentLine = "";
        while ((currentLine = icFileReader.readLine()) != null) {
            String[] line = currentLine.split("\\s+");
            boolean isMacroFound = false;
            boolean isLabelPresent = false;
            int mdtIndex = 0;
            if (mntEntryTable.containsKey(line[0])) {
                isMacroFound = true;
                mdtIndex = mntEntryTable.get(line[0]).mdtIndex;
            }
            if (line.length > 1 && mntEntryTable.containsKey(line[1])) {
                isMacroFound = true;
                isLabelPresent = true;
                mdtIndex = mntEntryTable.get(line[1]).mdtIndex;
            }
            // if not macro found then continue
            if (!isMacroFound) {
                expandedFile.write(currentLine + "\n");
                continue;
            }
            ArrayList<String> mdtLines = new ArrayList<String>();
            String temp = "";
            FileReader mdtFile = new FileReader("mdt.txt");
            BufferedReader mdtFileReader = new BufferedReader(mdtFile);
            while ((temp = mdtFileReader.readLine()) != null) {
                mdtLines.add(temp);
            }
            HashMap<Integer, String> argumentValues = new HashMap<>();
            boolean isFirstLine = true;
            while ((mdtIndex - 1) < mdtLines.size() && !mdtLines.get(mdtIndex -
                    1).equals("MEND")) {
                String current = mdtLines.get(mdtIndex - 1);
                String[] mdtLine = current.split("\\s+");
                int argumentIndex = 0;
                if (isFirstLine) {
// macro definition line
                    if (isLabelPresent) {
                        argumentIndex = argumentList.get(mdtLine[0].replaceAll("&", "")).index;
                        argumentValues.put(argumentIndex, line[0]);
                        for (int i = 2; i < line.length; i++) {
                            String currentArgument = mdtLine[i].replaceAll("[&, ]",
                                    "");
                            if (currentArgument.contains("=")) {
                                if (line[i].contains("=")) {
                                    currentArgument = line[i].replaceAll("[&, ]", "");
                                }
                                String[] currentArgumentSplit =
                                        currentArgument.split("=");
                                argumentIndex =
                                        argumentList.get(currentArgumentSplit[0].replaceAll("[&]", "")).index;
                                String defaultValue = argumentList
                                        .get(currentArgumentSplit[0].replaceAll("[&]",
                                                "")).value;
                                if (!defaultValue.isBlank() &&
                                        currentArgumentSplit[1].isEmpty()) {
                                    argumentValues.put(argumentIndex, defaultValue);
                                } else {
                                    argumentValues.put(argumentIndex,
                                            currentArgumentSplit[1]);
                                }
                            } else {
                                argumentIndex =
                                        argumentList.get(mdtLine[i].replaceAll("[&, ]",
                                                "")).index;
                                String defaultValue =
                                        argumentList.get(mdtLine[i].replaceAll("[&, ]",
                                                "")).value;
                                if (!defaultValue.isBlank() && line[i].isEmpty()) {
                                    argumentValues.put(argumentIndex, defaultValue);
                                } else {
                                    argumentValues.put(argumentIndex, line[i]);
                                }
                            }
                        }
                    } else {
// no label present
                        for (int i = 1; i < line.length; i++) {
                            String currentArgument = mdtLine[i].replaceAll("[&, ]",
                                    "");
                            if (currentArgument.contains("=")) {
                                if (line[i].contains("=")) {
                                    currentArgument = line[i].replaceAll("[&, ]", "");
                                }

                                String[] currentArgumentSplit =
                                        currentArgument.split("=");
                                argumentIndex =
                                        argumentList.get(currentArgumentSplit[0].replaceAll("[&]", "")).index;
                                String defaultValue = argumentList
                                        .get(currentArgumentSplit[0].replaceAll("[&]",
                                                "")).value;
                                if (!defaultValue.isBlank() &&
                                        currentArgumentSplit[1].isEmpty()) {
                                    argumentValues.put(argumentIndex, defaultValue);
                                } else {
                                    argumentValues.put(argumentIndex,
                                            currentArgumentSplit[1]);
                                }
                            } else {
                                argumentIndex =
                                        argumentList.get(mdtLine[i].replaceAll("[&, ]",
                                                "")).index;
                                String defaultValue =
                                        argumentList.get(mdtLine[i].replaceAll("[&, ]",
                                                "")).value;
                                if (!defaultValue.isBlank() && line[i].isEmpty()) {
                                    argumentValues.put(argumentIndex, defaultValue);
                                } else {
                                    argumentValues.put(argumentIndex, line[i]);
                                }
                            }
                        }
                    }
                } else {
// lines other than macro definition
                    for (int i = 0; i < mdtLine.length; i++) {
                        String variable = mdtLine[i];
                        if (variable.contains("#")) {

                            variable = variable.replaceAll("[#, ]", "");
                            int index = Integer.parseInt(variable);
                            String value = argumentValues.get(index);

                            if (value == null) {

                                for (Entry<String, ArgumentEntry> table :
                                        argumentList.entrySet()) {
                                    if (table.getValue().index == index) {
                                        value = table.getKey();

                                        break;

                                    }
                                }
                            }

                            mdtLine[i] = value;

                        }
                    }
                }
                if (!isFirstLine) {
                    for (int i = 0; i < mdtLine.length; i++) {
                        expandedFile.write(mdtLine[i] + "\t");
                    }

                    expandedFile.write("\n");
                }
                isFirstLine = false;
                mdtIndex++;
            }
            mdtFileReader.close();
        }
        expandedFile.close();
        icFileReader.close();
    }
}