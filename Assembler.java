import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Assembler {


    public HashMap<String,String> _imperativeStatements = new HashMap<>();
    public HashMap<String,String> _declarativeStatements = new HashMap<>();


    Assembler(){
        _imperativeStatements.put("STOP","00");
        _imperativeStatements.put("ADD","01");
        _imperativeStatements.put("SUB","02");
        _imperativeStatements.put("MULT","03");
        _imperativeStatements.put("MOVER","04");
        _imperativeStatements.put("MOVEM","05");
        _imperativeStatements.put("COMP","06");
        _imperativeStatements.put("BC","07");
        _imperativeStatements.put("DIV","08");
        _imperativeStatements.put("READ","09");
        _imperativeStatements.put("PRINT","10");
        _declarativeStatements.put("DS","01");
        _declarativeStatements.put("DC","02");

    }

    public static void main(String[] args) {
        Assembler assem = new Assembler();
        assem.PassOne();
    }

    void PassOne(){
        try{



            FileReader fileReader = new FileReader("input.txt");

            BufferedReader bufferReader = new BufferedReader(fileReader);

            FileWriter fileWriter = new FileWriter("IC11.txt");

            BufferedWriter bufferWriter = new BufferedWriter(fileWriter);

            HashMap<String,String> symbolTable = new HashMap<>();
            HashMap<String,String> literalTable = new HashMap<>();
            ArrayList<Integer> poolTable = new ArrayList<>();


            int locptr=0;
            int symboltableptr=1;
            int literaltableptr=1;
            int pooltableptr=1;

            String currentLine = bufferReader.readLine();
            String [] Line = currentLine.split(" ");

            if(Line[1].equals("START")){
                locptr= Integer.parseInt(Line[2]);
                bufferWriter.write("AD\t01\t" + locptr);
            }


            while((currentLine=bufferReader.readLine())!=null){
                Line = currentLine.split("[ ,]");

                boolean isLocationPtrSet = false;

                if(!Line[0].isEmpty()){
                    symbolTable.put(Line[0],String.valueOf(locptr));
                    bufferWriter.write("S\t" + locptr);
                }

                if(Line[1].equals("ORIGIN")){
                    int address = Integer.parseInt(symbolTable.get(Line[2]));

                    if(Line[3].equals("+")){
                        address+=Integer.parseInt(Line[4]);
                    }else{
                        address-=Integer.parseInt(Line[4]);
                    }

                    locptr = address;
                    bufferWriter.write("AD\t03\t" + "C\t" + locptr);
                    isLocationPtrSet=true;
                }


                if(Line[1].equals("EQU")){
                    int address = Integer.parseInt(symbolTable.get(Line[2]));

                    if(Line[3].equals("+")){
                        address+=Integer.parseInt(Line[4]);
                    }else{
                        address-=Integer.parseInt(Line[4]);
                    }

                    symbolTable.put(Line[0], String.valueOf(address));
                    bufferWriter.write("AD\t04\t" + "C\t" + locptr);
                    isLocationPtrSet=true;
                }

                // if
                if(Line[1].equals("LTORG")){
                    poolTable.add(pooltableptr);

                    for(Map.Entry<String,String> table : literalTable.entrySet()){
                        if(table.getValue().isEmpty()){
                            table.setValue(String.valueOf(locptr));
                            locptr++;
                            pooltableptr++;
                            isLocationPtrSet=true;
                        }
                    }
                }

                // if declarative statements;

                for(Map.Entry<String,String> table : _declarativeStatements.entrySet()){
                    if(table.getKey().equals(Line[1])){
                        if(table.getKey().equals("DC")){

                            symbolTable.put(Line[0] , String.valueOf(locptr));
                            bufferWriter.write("DL\t02\t" + "C\t"+ Line[2] + String.valueOf(locptr ));
                        }else{

                            int address = locptr + Integer.parseInt(symbolTable.get(Line[2]));

                            symbolTable.put(Line[0],String.valueOf(locptr));
                            bufferWriter.write("DL\t01\t" + "C\t"+ Line[2] + String.valueOf(Line[2]));
                            locptr=address;
                            isLocationPtrSet=true;
                        }
                    }
                }

                //imperativeStates;

                for (Map.Entry<String,String> table : _imperativeStatements.entrySet()){
                    if(table.getKey().equals(Line[1])){
                        bufferWriter.write("IS\t" + table.getValue() + "\t");

                        if(Line.length > 2){
                            switch (Line[2]){
                                case "AREG" : bufferWriter.write("01\t");
                                case "BREG" : bufferWriter.write("02\t");
                                case "CREG" : bufferWriter.write("03\t");
                                case "DREG" : bufferWriter.write("04\t");
                            }
                        }

                        if(Line.length > 3){
                            if(Line[3].contains("=")){
                                literalTable.put(Line[3],"");
                                bufferWriter.write("L\t" + literaltableptr + "\t"+ locptr);
                                literaltableptr++;
                            }else{
                                if (symbolTable.get(Line[3]) == null) {
                                    symbolTable.put(Line[3], "");
//                                    symbolTablePointerMapper.put(line[3], symbolTablePointer);
                                    bufferWriter.write("S\t" + symboltableptr + "\t");
                                    bufferWriter.write(String.valueOf(locptr));
                                    symboltableptr++;
                                } else {
//                                    bufferWriter.write("S\t" + symbolTablePointerMapper.get(line[3]) + "\t");
                                    bufferWriter.write(String.valueOf(locptr));
                                }
                            }
                        }
                    }
                }



                //end statement



                if(Line[1].equals("END")){
                    poolTable.add(pooltableptr);

                    for(Map.Entry<String,String> table : literalTable.entrySet()){
                        if(table.getValue().isEmpty()){
                            table.setValue(String.valueOf(locptr));
                            locptr++;
                            pooltableptr++;
                            isLocationPtrSet=true;
                        }
                    }

                    bufferWriter.write("AD\t05\t" + String.valueOf(locptr));
                }


                bufferWriter.newLine();

                if(isLocationPtrSet == false){
                    locptr++;
                }



            }

            bufferReader.close();
            bufferWriter.close();

            FileWriter symbolTableFile = new FileWriter("symbolTable.txt");
            BufferedWriter bufferSymbolTableWriter = new BufferedWriter(symbolTableFile);

            System.out.println("------------- Symbol Table --------------");
            for (Map.Entry<String, String> table : symbolTable.entrySet()) {
                bufferSymbolTableWriter.write(table.getKey() + "\t" + table.getValue() + "\n");
                System.out.println(table.getKey() + "\t" + table.getValue());
            }

            bufferSymbolTableWriter.close();

            /*
             * Write the literal table into the file
             */
            FileWriter literalTableFile = new FileWriter("literalTable.txt");
            BufferedWriter bufferLiteralTableWriter = new BufferedWriter(literalTableFile);

            System.out.println("------------- Literal Table --------------");
            for (Map.Entry<String, String> table : literalTable.entrySet()) {
                bufferLiteralTableWriter.write(table.getKey() + "\t" + table.getValue() + "\n");
                System.out.println(table.getKey() + "\t" + table.getValue());
            }

            bufferLiteralTableWriter.close();

            /*
             * Write the pool table into the file
             */
            FileWriter poolTableFile = new FileWriter("poolTable.txt");
            BufferedWriter bufferPoolTableWriter = new BufferedWriter(poolTableFile);

            System.out.println("------------- Pool Table --------------");
            for (Integer integer : poolTable) {
                bufferPoolTableWriter.write(integer + "\n");
                System.out.println(integer);
            }

            bufferPoolTableWriter.close();




        } catch(Exception e){

        }
    }
}