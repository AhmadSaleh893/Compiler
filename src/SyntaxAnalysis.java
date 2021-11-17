

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class SyntaxAnalysis {
    private static Grammar[] grammars;
    static int count;
    private static HashMap<String, Integer> grammarSort;
    public final static String STARTER_GRAMMAR = "Program'";

    static List<Production> productions;
    static String[] firstSet = new String[50];
    static HashMap<String, Integer> doneFromIt = new HashMap<>();
    static int firstSetCount = 0;
    static HashMap<String, Integer> epsilon;

    public static void init() {
        grammars = new Grammar[50];
        grammarSort = new HashMap<>();
        count = 0;
        epsilon = new HashMap<>();
        productions = new ArrayList<>();
        epsilon.put("Formals", 1);
        epsilon.put("VariableDecls", 1);
        epsilon.put("Stmts", 1);
        epsilon.put("E", 1);
        epsilon.put("Actuals", 1);
        epsilon.put("ExprList", 1);

        grammarSort.put("Program'", count);
        grammars[count++] = new Grammar("Program'", new String[][]
                {
                        new String[]{".", "Program"}
                }, null);

        grammarSort.put("Program", count);
        grammars[count++] = new Grammar("Program", new String[][]
                {
                        new String[]{".", "Decls"}
                }, null);

        grammarSort.put("Decls", count);
        grammars[count++] = new Grammar("Decls", new String[][]
                {
                        new String[]{".", "Decl"}
                        , new String[]{".", "Decl", "Decls"}
                }, null);
        grammarSort.put("Decl", count);
        grammars[count++] = new Grammar("Decl", new String[][]
                {
                        new String[]{".", "VariableDecl"}
                        , new String[]{".", "FunctionDecl"}
                }, null);

        grammarSort.put("VariableDecl", count);
        grammars[count++] = new Grammar("VariableDecl", new String[][]
                {
                        new String[]{".", "Variable", ";"}
                }, null);

        grammarSort.put("Variable", count);
        grammars[count++] = new Grammar("Variable", new String[][]
                {
                        new String[]{".", "Type", "ident"}
                }, null);

        grammarSort.put("Type", count);
        grammars[count++] = new Grammar("Type", new String[][]
                {
                        new String[]{".", "int"}
                        , new String[]{".", "double"}
                        , new String[]{".", "bool"}
                        , new String[]{".", "string"}
                        , new String[]{".", "Type", "[]"}
                }, null);

        grammarSort.put("FunctionDecl", count);
        grammars[count++] = new Grammar("FunctionDecl", new String[][]
                {
                        new String[]{".", "Type", "ident", "(", "Formals", ")", "StmtBlock"}
                        , new String[]{".", "void", "ident", "(", "Formals", ")", "StmtBlock"}
                }, null);

        grammarSort.put("Formals", count);
        grammars[count++] = new Grammar("Formals", new String[][]
                {
                        new String[]{".", "Variable"}
                        , new String[]{".", "Variable", ",", "Formals"}
                        , new String[]{".", "epsilon"}
                }, null);

        grammarSort.put("StmtBlock", count);
        grammars[count++] = new Grammar("StmtBlock", new String[][]
                {
                        new String[]{".", "{", "VariableDecls", "Stmts", "}"}
                }, null);

        grammarSort.put("VariableDecls", count);
        grammars[count++] = new Grammar("VariableDecls", new String[][]
                {
                        new String[]{".", "VariableDecl"}
                        , new String[]{".", "VariableDecl", "VariableDecls"}
                        , new String[]{".", "epsilon"}
                }, null);

        grammarSort.put("Stmts", count);
        grammars[count++] = new Grammar("Stmts", new String[][]
                {
                        new String[]{".", "Stmt"}
                        , new String[]{".", "Stmt", "Stmts"}
                        , new String[]{".", "epsilon"}
                }, null);

        grammarSort.put("Stmt", count);
        grammars[count++] = new Grammar("Stmt", new String[][]
                {
                        new String[]{".", "IfStmt"}
                        , new String[]{".", "WhileStmt"}
                        , new String[]{".", "ForStmt"}
                        , new String[]{".", "ReturnStmt"}
                        , new String[]{".", "PrintStmt"}
                        , new String[]{".", "StmtBlock"}
                        , null
                }, null);

        grammarSort.put("IfStmt", count);
        grammars[count++] = new Grammar("IfStmt", new String[][]
                {
                        new String[]{".", "if", "(", "Expr", ")", "Stmt"}
                        , new String[]{".", "if", "(", "Expr", ")", "Stmt", "else", "Stmt"}
                }, null);

        grammarSort.put("WhileStmt", count);
        grammars[count++] = new Grammar("WhileStmt", new String[][]
                {
                        new String[]{".", "while", "(", "Expr", ")", "Stmt"}
                }, null);

        grammarSort.put("ForStmt", count);
        grammars[count++] = new Grammar("ForStmt", new String[][]
                {
                        new String[]{".", "for", "(", "E", ";", "Expr", ";", "E", ")", "Stmt"}
                }, null);

        grammarSort.put("E", count);
        grammars[count++] = new Grammar("E", new String[][]
                {
                        new String[]{".", "Expr"}
                        , new String[]{".", "epsilon"}
                }, null);

        grammarSort.put("ReturnStmt", count);
        grammars[count++] = new Grammar("ReturnStmt", new String[][]
                {
                        new String[]{".", "return", "E", ";"}
                }, null);

        grammarSort.put("PrintStmt", count);
        grammars[count++] = new Grammar("PrintStmt", new String[][]
                {
                        new String[]{".", "print", "(", "ExprList", ")", ";"}
                }, null);

        grammarSort.put("Expr", count);
        grammars[count++] = new Grammar("Expr", new String[][]
                {
                        new String[]{".", "LValue", "=", "Expr"}
                        , new String[]{".", "Constant"}
                        , new String[]{".", "LValue"}
                        , new String[]{".", "(", "Expr", ")"}
                        , new String[]{".", "Expr", "+", "Expr"}
                        , new String[]{".", "Expr", "-", "Expr"}
                        , new String[]{".", "Expr", "*", "Expr"}
                        , new String[]{".", "Expr", "/", "Expr"}
                        , new String[]{".", "-", "Expr"}
                        , new String[]{".", "Expr", "<", "Expr"}
                        , new String[]{".", "Expr", "<=", "Expr"}
                        , new String[]{".", "Expr", ">", "Expr"}
                        , new String[]{".", "Expr", ">=", "Expr"}
                        , new String[]{".", "Expr", "==", "Expr"}
                        , new String[]{".", "Expr", "!=", "Expr"}
                        , new String[]{".", "Expr", "&&", "Expr"}
                        , new String[]{".", "Expr", "||", "Expr"}
                        , new String[]{".", "!", "Expr"}
                        , new String[]{".", "ReadInteger()"}
                        , new String[]{".", "ReadLine()"}
                }, null);

        grammarSort.put("LValue", count);
        grammars[count++] = new Grammar("LValue", new String[][]
                {
                        new String[]{".", "ident"}
                        , new String[]{".", "Expr", "[", "Expr", "]"}
                }, null);

        grammarSort.put("Call", count);
        grammars[count++] = new Grammar("Call", new String[][]
                {
                        new String[]{".", "ident", "(", "Actuals", ")"}
                }, null);

        grammarSort.put("Actuals", count);
        grammars[count++] = new Grammar("Actuals", new String[][]
                {
                        new String[]{".", "ExprList"}
                        , new String[]{".", "epsilon"}
                }, null);

        grammarSort.put("ExprList", count);
        grammars[count++] = new Grammar("ExprList", new String[][]
                {
                        new String[]{".", "Expr"}
                        , new String[]{".", "Expr", ",", "ExprList"}
                        , new String[]{".", "epsilon"}
                }, null);

        grammarSort.put("Constant", count);
        grammars[count++] = new Grammar("Constant", new String[][]
                {
                        new String[]{".", "intConstant"}
                        , new String[]{".", "doubleConstant"}
                        , new String[]{".", "boolConstant"}
                        , new String[]{".", "stringConstant"}
                }, null);
//        S ->aSa
//        S ->bSb
//        S ->c
//        grammarSort.put("S'", count);
//        grammars[count++] = new Grammar("S'",new String[][]
//                {
//                        new String[]{".","S"}
//                },null);
//        grammarSort.put("S", count);
//        grammars[count++] = new Grammar("S",new String[][]
//                {
//                        new String[]{".", "a", "S", "a"}
//                        ,new String[]{".", "b", "S", "b"}
//                        ,new String[]{".", "c"}
//                },null);
    }

    private static List<Production> findDFAs() {
        if (productions.isEmpty()) {
            Production production1 = new Production();
            int starterGrammarIndex = grammarSort.get(STARTER_GRAMMAR);
            Grammar starterGrammar = new Grammar(grammars[starterGrammarIndex]);
            starterGrammar.setLookAhead(new String[]{"$", null});
            production1.add(starterGrammar);
            String[] afterDot = new String[50];
            afterDot[0] = "$";
            production1 = initialProduction(starterGrammar.getChildrenList()[0][1], production1, afterDot);
            productions.add(production1);
            findDFAs();
        } else {
            for (int i = 0; i < productions.size(); i++) {
                findNextProduction(productions.get(i));
            }
        }
        return productions;
    }

    private static void findNextProduction(Production production) {
        HashMap<String, Integer> itemsExist = new HashMap<>();
        Grammar[] myGrammars = production.getGrammars();
        int numberOfChildren = 0;
        int samerHasanMohammad = 0;
        for (int zx = 0; zx < myGrammars.length; ) {
            Production newProduction = null;
            Grammar yy = myGrammars[zx];
            if (yy == null)
                break;
            Grammar[] grammarsToWork = new Grammar[myGrammars.length];
            int grammarsToWorkCount = 0;
            String checker = "";
            int i = zx;
            for (; i < myGrammars.length; i++) {
                Grammar gm = myGrammars[i];
                if (gm == null)
                    break;
                if (gm.equals(yy)) {
                    samerHasanMohammad++;
                }
                String[][] childrenList = gm.getChildrenList();
//                int iq = 0;
                for (String[] children : childrenList) {
                    if (children == null)
                        break;
                    if (!children[children.length - 1].equals(".")) {
                        int j = 0;
                        for (String grandSon : children) {

                            if (grandSon.equals(".") && j + 1 < children.length && !children[j+1].equals("epsilon") && (checker.equals("") || children[j + 1].equals(checker)) && (!itemsExist.containsKey(children[j + 1]))) {
                                checker = children[++j];
                                if (gm.getHead().equals(yy.getHead()))
                                    numberOfChildren++;
                                Grammar newGrammar = new Grammar();
                                newGrammar.setHead(gm.getHead());
                                String[] ch = new String[children.length];
                                int p = 0;
                                for (String child : children) {
                                    if (children == null)
                                        break;
                                    ch[p] = "";
                                    for (int o = 0; o < child.length(); o++) {
                                        ch[p] += child.charAt(o);
                                    }
                                    p++;
                                }
                                ch[j - 1] = checker;
                                ch[j] = ".";
                                newGrammar.setChildrenList(new String[][]{ch});
//                                iq++;
                                newGrammar.setLookAhead(gm.getLookAhead());
                                if (j + 1 < ch.length) {
                                    String vv = ch[j + 1];
                                    String[] cc = new String[50];
                                    if (grammarSort.containsKey(vv)) {
                                        HashMap<String, Integer> hh = new HashMap<>();
                                        int kk = 0;

//                                        for (String x : findFirst(vv))
//                                        {
//                                            cc[kk++] = x;
//                                            /* here add if (!hh.containsKey(x)) {
//                                            cc[kk++] = x;
//                                            hh.put(x, 1);
//                                        }*/
//                                        }
//                                        firstSet = new String[50];
//                                        firstSetCount = 0;
//                                        doneFromIt = new HashMap<>();

                                        if (j + 2 < ch.length) {
                                            for (int qq = j + 2; qq < ch.length; qq++) {
                                                String bb = ch[qq];
                                                if (grammarSort.containsKey(bb)) {
                                                    for (String x : findFirst(bb)) {
                                                        // you have to check if it's not exists before
                                                        if (x == null)
                                                            break;
                                                        if (!hh.containsKey(x)) {
                                                            cc[kk++] = x;
                                                            hh.put(x, 1);
                                                        }
                                                    }

                                                    firstSet = new String[50];
                                                    firstSetCount = 0;
                                                    doneFromIt = new HashMap<>();

                                                    if (!epsilon.containsKey(bb)) {
                                                        break;
                                                    }
                                                } else {
                                                    if (!hh.containsKey(bb)) {
                                                        cc[kk++] = bb;
                                                        hh.put(bb, 1);
                                                        break;
                                                    }
                                                }
                                            }
                                        } else {
                                            cc = gm.getLookAhead();
                                        }
                                        Production zz = new Production();
                                        Production k = findFirstOfGrammar(ch[j + 1], zz, cc);
                                        if (k != null) {
                                            for (Grammar pp : k.getGrammars()) {
                                                if (pp == null)
                                                    break;
                                                grammarsToWork[grammarsToWorkCount++] = pp;
                                            }
                                        }
                                        getDoneFromItFindFirstOfGrammar = new HashMap<>();
                                    }


                                }
                                grammarsToWork[grammarsToWorkCount++] = newGrammar;
//                                if (j + 1 < ch.length) {
//
////                                    String[] afterDOt = new String[50];
////
////                                    if (j + 2 < ch.length) {
////                                        String u = ch[j + 2];
////                                        int df = 0;
////                                        if (!grammarSort.containsKey(u)) {
////                                            afterDOt[df++] = u;
////                                        } else {
////
////                                            String[] lookAhead = gm.getLookAhead();
////                                            for (String uc : lookAhead) {
////                                                if (uc == null)
////                                                    break;
////                                                for (int pq = 0; pq < uc.length(); pq++) {
////                                                    afterDOt[df] = lookAhead[pq];
////                                                }
////                                                df++;
////                                            }
////                                        }
////                                    } else {
////                                        afterDOt = gm.getLookAhead();
////                                    }
//
//
//
//                                }
                                break;
                            } else if (j + 1 < children.length && (children[j + 1].equals(checker) || checker.isEmpty()) && (children[j + 1].equals(yy.getHead()) && itemsExist.containsKey(children[j + 1])) && gm.getHead().equals(yy.getHead())) {
                                numberOfChildren++;
                            }
                            if (grandSon.equals("."))
                                break;
                            j++;

                        }
                    }
                }
            }
            if (!checker.equals(""))
                itemsExist.put(checker, 1);
//            System.out.println(grammarsToWork.length);
            if (grammarsToWorkCount != 0) {
                newProduction = new Production(grammarsToWork);
                newProduction.setMove(checker);
                if (checker.equals("Decl")) {
                    System.out.println();
                }
                Production oldProduction = checkIfRepeated(newProduction);

                if (oldProduction != null) {
                    if (oldProduction.equals(production))
                    {
                        oldProduction.setRepeated();
                        oldProduction.setNext(oldProduction);
                    }
                    else
                        production.setNext(oldProduction);
//                    productions.add(oldProduction);
                } else {
                    production.setNext(newProduction);
                    productions.add(newProduction);
                }
            }
            if (yy.getChildrenCount() == samerHasanMohammad) {
                zx++;
                numberOfChildren = 0;
                samerHasanMohammad = 0;
            }
        }
    }

    private static Production checkIfRepeated(Production repeatedProduction) {
        for (Production production : productions) {
            if (production.equals(repeatedProduction)) {
                return production;
            }
        }
        return null;
    }

    private static String[] findFirst(Grammar grammar1) {
        return insideFindFirst(grammar1);
    }

    private static String[] findFirst(String element) {
        if (!grammarSort.containsKey(element))
            return null;
        int f = grammarSort.get(element);
        Grammar grammar1 = grammars[f];

        return insideFindFirst(grammar1);
    }

    private static String[] insideFindFirst(Grammar grammar1) {
        String[][] childrenList = grammar1.getChildrenList();
        for (String[] child : childrenList) {
            if (child == null)
                break;
            ;
            doneFromIt.put(grammar1.getHead(), 1);
            for (int i = 0; i < child.length; i++) {
                if (child == null)
                    break;
                if (grammar1
                        .getHead().equals("E"))
                    System.out.println();
                String grandSon = child[i];
                if (grandSon.charAt(0) == '.' && i + 1 < child.length) {
                    grandSon = child[++i];
                    if (epsilon.containsKey(grandSon) && !grandSon.equals(grammar1.getHead())) {
                        if (grammarSort.containsKey(grandSon)) {
                            int dd = grammarSort.get(grandSon);
                            findFirst(grammars[dd]);
                        } else if (!doneFromIt.containsKey(grandSon)) {
                            firstSet[firstSetCount++] = grandSon;
                        }
                    }
                    if (!grandSon.equals(grammar1.getHead())) {

                        if (grammarSort.containsKey(grandSon) && !doneFromIt.containsKey(grandSon)) {
                            int checkTerminal = grammarSort.get(grandSon);
                            Grammar newGrammar = grammars[checkTerminal];
                            findFirst(newGrammar);
                        } else if (!doneFromIt.containsKey(grandSon)) {
                            doneFromIt.put(grandSon, 1);

                            firstSet[firstSetCount++] = grandSon;

                        }
                    }
                    break;
                }
            }
        }
        return firstSet;
    }

    private static Production initialProduction(String element, Production production, String[] afterDot) {
        if (!grammarSort.containsKey(element)) {
            return null;
        }
        int grammarIndex = grammarSort.get(element);
        Grammar gm = new Grammar(grammars[grammarIndex]);

        String[] lookAhead = new String[50];
        int[] n = {0};
        int y = gm.getLookAheadCount();
        while (afterDot[y] != null) {
            String lookAheadElement = afterDot[y++];
            if (grammarSort.containsKey(lookAheadElement)) {
                for (String ll : findFirst(lookAheadElement)) {
                    if (ll == null)
                        break;
                    lookAhead[n[0]++] = ll;
                }
                doneFromIt = new HashMap<>();
                firstSet = new String[50];
                firstSetCount = 0;
            } else {
                lookAhead[n[0]++] = lookAheadElement;
            }
        }
        if (lookAhead[0] != null && production.checkExist(gm.getHead()) == false) {
            gm.setLookAhead(lookAhead);
            production.add(gm);
        }
        String[][] childrenList = gm.getChildrenList();
        for (String[] children : childrenList) {
            afterDot = new String[50];
            for (int i = 0; i < children.length; i++) {
                int afterDotCount = 0;
                String grandSon = children[i];
                if (grandSon.equals(".")) {
                    grandSon = children[++i];
                    Object ee = grammarSort.get(grandSon);
                    if (ee != null) {
                        Grammar newGrammar = new Grammar(grammars[(int) ee]);
                        if (grandSon.equals(element)) { // if it finds itself

                            int vv = grammarSort.get(element);
                            Grammar tt = new Grammar(grammars[vv]);
                            String item = children[++i];
                            boolean addIt = true;
                            int c = 0;
                            String[] newLookAhead = new String[50];
                            for (; c < tt.getLookAheadCount(); ) {
                                String cc = tt.getLookAhead()[c];
                                if (item.equals(cc)) {
                                    addIt = false;
                                }
                                c++;
                            }

                            if (addIt == true) {
                                gm.resizeLookAhead(item);
                            }
                            break;
                        } else if (childrenList.length > 1) {
                            for (String ch[] : childrenList) {
                                for (int u = 0; u < ch.length; u++) {
                                    String gDon = ch[u];
                                    if (gDon.equals(".")) {
                                        gDon = ch[++u];
                                        if (gDon.equals(grandSon)) {
                                            if (u + 1 >= ch.length) {
                                                if (gm.getLookAheadCount() == 1)
                                                    afterDot[afterDotCount++] = "$";
                                                else {
                                                    int rr = 0;
                                                    for (; rr < gm.getLookAheadCount(); ) {
                                                        afterDot[afterDotCount++] = gm.getLookAhead()[rr++];
                                                    }
                                                }
                                            } else {
                                                afterDot[afterDotCount++] = ch[u + 1];
                                            }
                                        }
                                        break;
                                    }
                                }
                            }
                        } else {
                            if (i + 1 >= children.length) {
                                afterDot[0] = "$";
                            } else {
                                afterDot[0] = children[i + 1];
                            }
                        }
                        initialProduction(newGrammar.getHead(), production, afterDot);
                    }
                    break;
                }
            }
        }
        return production;
    }

    private static HashMap<String, Integer> getDoneFromItFindFirstOfGrammar = new HashMap<>();

    private static Production findFirstOfGrammar(String element, Production production, String[] afterDot) {
        int grammarIndex = 0;

        System.out.println(element);
        if (!grammarSort.containsKey(element)) {
            return null;
        }


        Object uu = grammarSort.get(element);

        if (uu == null)
            return null;
        if (!getDoneFromItFindFirstOfGrammar.containsKey(element)) {
            getDoneFromItFindFirstOfGrammar.put(element, 1);
        } else
            return null;
        Grammar gm = new Grammar(grammars[(int) uu]);

        String[] lookAhead = new String[50];
        int[] n = {0};
        int y = gm.getLookAheadCount();
        while (afterDot.length > y && afterDot[y] != null) {
            String lookAheadElement = afterDot[y++];
            if (grammarSort.containsKey(lookAheadElement)) {
                for (String ll : findFirst(lookAheadElement)) {
                    if (ll == null)
                        break;
                    lookAhead[n[0]++] = ll;
                }
                doneFromIt = new HashMap<>();
                firstSet = new String[50];
                firstSetCount = 0;
            } else {
                lookAhead[n[0]++] = lookAheadElement;
            }
        }
        if (lookAhead[0] != null && production.checkExist(gm.getHead()) == false) {
            gm.setLookAhead(lookAhead);
            production.add(gm);
        }
        String[][] childrenList = gm.getChildrenList();
        for (String[] children : childrenList) {
            if (children == null)
                break;
            afterDot = new String[50];
            for (int i = 0; i < children.length; i++) {

                int afterDotCount = 0;
                String grandSon = children[i];
                if (grandSon.equals(".") && i + 1 < children.length) {
                    grandSon = children[++i];
                    Object ee = grammarSort.get(grandSon);
                    if (ee != null) {
                        Grammar newGrammar = new Grammar(grammars[(int) ee]);
                        if (grandSon.equals(element)) { // if it finds itself

                            int vv = grammarSort.get(element);
                            Grammar tt = new Grammar(grammars[vv]);
                            String item = "";
                            if (i + 1 < children.length)
                                item = children[++i];
                            boolean addIt = true;
                            int c = 0;
                            String[] newLookAhead = new String[50];
                            for (; c < tt.getLookAheadCount(); ) {
                                String cc = tt.getLookAhead()[c];
                                if (item.equals(cc)) {
                                    addIt = false;
                                }
                                c++;
                            }

                            if (addIt == true) {
                                gm.resizeLookAhead(item);
                            }
                            break;
                        } else if (childrenList.length > 1) {
                            for (String ch[] : childrenList) {
                                if (ch == null)
                                    break;
                                for (int u = 0; u < ch.length; u++) {

                                    String gDon = ch[u];
                                    if (gDon.equals(".")) {
                                        gDon = ch[++u];
                                        if (gDon.equals(grandSon)) {
                                            if (u + 1 >= ch.length) {
                                                int rr = 0;
                                                for (; rr < gm.getLookAheadCount(); ) {
                                                    afterDot[afterDotCount++] = gm.getLookAhead()[rr++];
                                                }
                                            } else {
                                                afterDot[afterDotCount++] = ch[u + 1];
                                            }
                                        }
                                        break;
                                    }
                                }
                            }
                        } else {
                            if (i + 1 >= children.length) {
                                afterDot[0] = gm.getLookAhead()[0];
                            } else {
                                afterDot[0] = children[i + 1];
                            }
                        }

                        findFirstOfGrammar(newGrammar.getHead(), production, afterDot);

                    }
                    break;
                }
            }
        }

        return production;
    }

    public static void main(String[] args) throws IOException {

//        File file = new File("src/text.txt");
//        List<String> lines = Files.readAllLines(file.toPath());
//        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(new File("src/dd")));
//        for (String line : lines)
//        {
//            String both[] = line.split("\uF0E0");
//            bufferedWriter.write(both[0]+" -> " + both[1]+"\n");
//            bufferedWriter.flush();
//        }
//        bufferedWriter.close();
        init();
        List<Production> d = findDFAs();
        List<Production> dfd = new ArrayList<>();
//        for (Production production : d)
//        {
//            if (production.getNext().size() > 0)
//            for (Production pp : production.getNext()) {
//                if (pp == production) {
//                    dfd.add(production);
//                }
//            }
//        }
//
//        String [] oi = findFirst("Program");
//        System.out.print("{");
//        Production pp = new Production();
//         Production vb = findFirstOfGrammar("Expr", pp,new String[]{"$"});
//         String[] jj = findFirst("Decl");
//        for (int i = 0 ; i < count ; i++) {
//            Grammar g = grammars[i];
//            if (g == null)
//                break;
//            System.out.print(g.getHead() + " :   ");
//            for (String gg : findFirst(g.getHead())) {
//                if (gg == null)
//                    break;
//                System.out.print(gg + "  ");
//            }
//            firstSet = new String[50];
//            firstSetCount = 0;
//            doneFromIt = new HashMap<>();
//            System.out.println();
//        }
        int sdfdsf;


        System.out.println();
    }
}