package main;

import lexicalanalysis.Analyzer;
import syntaxanalysis.Parsing;
import syntaxanalysis.SyntaxAnalysis;
import syntaxanalysis.Table;

import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

public class Compiler {
    public static void main(String args[]) throws IOException {
        Scanner in = new Scanner(System.in);
        System.out.println("Please Enter Sample Code Path (required java code):");
        String sampleCodeName = in.next();//input: enter the desired file path
//"C:\\Users\\Ahmad\\Desktop\\test.txt"
        File sampleCode = new File(sampleCodeName);//sample code has the java code
        File openFile = new File("C:\\Users\\Ahmad\\Desktop\\tokens.txt");
        Desktop desktop = Desktop.getDesktop();
        BufferedWriter bufferedWriter = null;
        Boolean open = false;
        try {
            FileWriter fileWriter = new FileWriter(openFile);//output file
            bufferedWriter = new BufferedWriter(fileWriter);
            Boolean stillComment = false; // to catch multiple comment (/*)
            if (sampleCode.exists()) {
                List<String> lines = Files.readAllLines(Path.of(sampleCode.getPath()));
                open = true;
                Analyzer.init();
                for (String line : lines) {
                    line = line.trim();
                    if (line != null && !line.equals("")) {
                        if (line.charAt(0) == '/' && line.charAt(1) == '*' || stillComment) {
                            stillComment = true;
                            if (line.contains("*/")) {
                                stillComment = false;
                            }
                        } else if (stillComment == false && !(line.charAt(0) == '/' && line.charAt(1) == '/')) {
                            Analyzer.analyzeLine(line, fileWriter);
                        }
                    }
                }
                bufferedWriter.close();
                desktop.open(openFile);

                //  Syntax Analysis Sector *******************************************************

                SyntaxAnalysis.init();
                Table table = new Table();
                Table.Node nodes[] = table.findParseTable();
                Parsing parse = new Parsing(nodes);

                List<String> tokens = Files.readAllLines(Path.of(openFile.getPath()));
                parse.checkTokens(tokens);

            } else // if sample code not exists
            {
                open = false;
                System.out.println("Sorry the path or the file name is incorrect");
            }
        } catch (IOException e) {
//            System.out.println(e.getMessage());
            bufferedWriter.close();
            if (open)
                desktop.open(openFile);
        }
    }
}