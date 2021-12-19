package syntaxanalysis;

import java.util.*;

public class SyntaxAnalysis {
    static Grammar[] grammars;
    static int count;
    static HashMap<String, Integer> grammarSort;
    public final static String STARTER_GRAMMAR = "Program'";
    public static HashMap<String, Integer> terminals = new HashMap<>();

    static public HashMap<Integer, String> getKey = new HashMap<>();
    static List<Production> productions;
    static String[] firstSet = new String[50];
    static HashMap<String, Integer> doneFromIt = new HashMap<>();
    static int firstSetCount = 0;
    static HashMap<String, Integer> epsilon;
    static HashMap<String, Integer> beforeGrammarCount = new HashMap<>();

    public static void init() {
        grammars = new Grammar[50];
        grammarSort = new HashMap<>();
        count = 0;
        epsilon = new HashMap<>();
        productions = new ArrayList<>();

        getKey.put(0, "Program'");
        beforeGrammarCount.put("Program'", 0);
        grammarSort.put("Program'", count);
        grammars[count++] = new Grammar("Program'", new String[][]
                {
                        new String[]{".", "Program"}
                }, null);

        getKey.put(1, "Program");
        beforeGrammarCount.put("Program", 1);
        grammarSort.put("Program", count);
        grammars[count++] = new Grammar("Program", new String[][]
                {
                        new String[]{".", "Decls"}
                }, null);

        getKey.put(2, "Decls");
        beforeGrammarCount.put("Decls", 2);
        grammarSort.put("Decls", count);
        grammars[count++] = new Grammar("Decls", new String[][]
                {
                        new String[]{".", "Decl"}
                        , new String[]{".", "Decl", "Decls"}
                }, null);

        getKey.put(4, "Decl");
        beforeGrammarCount.put("Decl", 4);
        grammarSort.put("Decl", count);
        grammars[count++] = new Grammar("Decl", new String[][]
                {
                        new String[]{".", "VariableDecl"}
                        , new String[]{".", "FunctionDecl"}
                }, null);

//        getKey.put(6, "VariableDecl");
//        beforeGrammarCount.put("VariableDecl", 6);
//        grammarSort.put("VariableDecl", count);
//        grammars[count++] = new Grammar("VariableDecl", new String[][]
//                {
//                        new String[]{".", "Variable", ";"}
//                }, null);

        getKey.put(6, "VariableDecl");
        beforeGrammarCount.put("VariableDecl", 6);
        grammarSort.put("VariableDecl", count);
        grammars[count++] = new Grammar("VariableDecl", new String[][]
                {
                        new String[]{".", "Type", "ident", ";"}
                }, null);

        getKey.put(7, "Type");
        beforeGrammarCount.put("Type", 7);
        grammarSort.put("Type", count);
        grammars[count++] = new Grammar("Type", new String[][]
                {
                        new String[]{".", "int"}
                        , new String[]{".", "double"}
                        , new String[]{".", "bool"}
                        , new String[]{".", "string"}
                }, null);

        getKey.put(11, "FunctionDecl");
        beforeGrammarCount.put("FunctionDecl", 11);
        grammarSort.put("FunctionDecl", count);
        grammars[count++] = new Grammar("FunctionDecl", new String[][]
                {
                        new String[]{".", "Type", "ident", "(", "Formals", ")", "StmtBlock"}
                }, null);

        getKey.put(12, "Formals");
        beforeGrammarCount.put("Formals", 12);
        grammarSort.put("Formals", count);
        grammars[count++] = new Grammar("Formals", new String[][]
                {
                        new String[]{".", "Type", "ident"}
                        , new String[]{".", "Type", "ident", ",", "Formals"}
                }, null);

        getKey.put(14, "StmtBlock");
        beforeGrammarCount.put("StmtBlock", 14);
        grammarSort.put("StmtBlock", count);
        grammars[count++] = new Grammar("StmtBlock", new String[][]
                {
                        new String[]{".", "{", "VariableDecl", "Stmts", "}"}
                }, null);

        getKey.put(15, "Stmts");
        beforeGrammarCount.put("Stmts", 15);
        grammarSort.put("Stmts", count);
        grammars[count++] = new Grammar("Stmts", new String[][]
                {
                        new String[]{".", "Stmt"}
                        , new String[]{".", "Stmt", ";", "Stmts"}
                }, null);

        getKey.put(17, "Stmt");
        beforeGrammarCount.put("Stmt", 17);
        grammarSort.put("Stmt", count);
        grammars[count++] = new Grammar("Stmt", new String[][]
                {
                        new String[]{".", "IfStmt"}
                        , new String[]{".", "assignStmt"}
                        , new String[]{".", "StmtBlock"}
                }, null);

        getKey.put(20, "IfStmt");
        beforeGrammarCount.put("IfStmt", 20);
        grammarSort.put("IfStmt", count);
        grammars[count++] = new Grammar("IfStmt", new String[][]
                {
                        new String[]{".", "if", "(", "Expr", ")", "Stmt"}
                }, null);

        getKey.put(21, "assignStmt");
        beforeGrammarCount.put("assignStmt", 21);
        grammarSort.put("assignStmt", count);
        grammars[count++] = new Grammar("assignStmt", new String[][]
                {
                        new String[]{".", "ident", "=", "Expr"}
                }, null);

        getKey.put(22, "Expr");
        beforeGrammarCount.put("Expr", 22);
        grammarSort.put("Expr", count);
        grammars[count++] = new Grammar("Expr", new String[][]
                {
                        new String[]{".", "Factor", "+", "Factor"}
                        , new String[]{".", "Factor", "<", "Factor"}
                        , new String[]{".", "Factor", "&&", "Factor"}
                        , new String[]{".", "Factor"}
                }, null);

        getKey.put(26, "Factor");
        beforeGrammarCount.put("Factor", 26);
        grammarSort.put("Factor", count);
        grammars[count++] = new Grammar("Factor", new String[][]
                {
                        new String[]{".", "Constant"}
                        , new String[]{".", "ident"}
                        , new String[]{".", "(", "Expr", ")"}
                }, null);

        getKey.put(28, "Constant");
        beforeGrammarCount.put("Constant", 28);
        grammarSort.put("Constant", count);
        grammars[count++] = new Grammar("Constant", new String[][]
                {
                        new String[]{".", "intConstant"}
                        , new String[]{".", "doubleConstant"}
                        , new String[]{".", "boolConstant"}
                        , new String[]{".", "stringConstant"}
                }, null);

        findTerminals();
        findDFAs();
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
                Production production = productions.get(i);
                production.setProductionNumber(i);
                findNextProduction(production);
            }
        }

        terminals.put("$", 1);
        return productions;
    }

    private static String[] findTerminals() {
        String[] terminals = new String[100];
        for (int i = 0; i < count; i++) {
            Grammar grammar = grammars[i];

            for (String[] children : grammar.getChildrenList()) {
                if (children != null)
                    for (String grandSon : children) {
                        if (grandSon != null)
                            if (!grandSon.equals(".") && !grammarSort.containsKey(grandSon)) {
                                SyntaxAnalysis.terminals.put(grandSon, 1);
                            }
                    }
            }
        }

        return terminals;
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
                    if (!checker.isEmpty() && !terminals.containsKey(checker) && !grammarSort.containsKey(checker)) {
                        terminals.put(checker, 1);
                    }
                    if (children == null)
                        break;
                    if (!children[children.length - 1].equals(".")) {
                        int j = 0;
                        for (String grandSon : children) {

                            if (grandSon.equals(".") && j + 1 < children.length && !children[j + 1].equals("epsilon") && (checker.equals("") || children[j + 1].equals(checker)) && (!itemsExist.containsKey(children[j + 1]))) {
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
                                Grammar[] grammarsToWorkHelper = new Grammar[myGrammars.length];
                                int grammarsToWorkHelperCount = 0;

                                boolean entered = false;
                                if (j + 1 < ch.length) {
                                    String vv = ch[j + 1];
                                    String[] cc = new String[500];
                                    if (grammarSort.containsKey(vv)) {
                                        HashMap<String, Integer> hh = new HashMap<>();
                                        int kk = 0;

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
                                        entered = true;
                                        boolean exist = false;
                                        for (int ccq = 0; ccq < grammarsToWorkCount; ccq++) {
                                            if (grammarsToWork[ccq].equals(newGrammar)) {
                                                exist = true;
                                                break;
                                            }
                                        }

                                        if (!exist)
                                            grammarsToWork[grammarsToWorkCount++] = newGrammar;
                                        exist = false;
                                        Production zz = new Production();
                                        Production k = findFirstOfGrammar(ch[j + 1], zz, cc);
                                        if (k != null) {
                                            for (Grammar pp : k.getGrammars()) {
                                                if (pp == null)
                                                    break;
                                                for (int ccd = 0; ccd < grammarsToWorkCount; ccd++) {
                                                    if (grammarsToWork[ccd].equals(pp)) {
                                                        exist = true;
                                                        break;
                                                    }
                                                }
                                                if (!exist)
                                                    grammarsToWork[grammarsToWorkCount++] = pp;
                                            }
                                        }
                                        getDoneFromItFindFirstOfGrammar = new HashMap<>();
                                    }


                                }

                                if (!entered) {
                                    boolean exist = false;
                                    for (int ccq = 0; ccq < grammarsToWorkCount; ccq++) {
                                        if (grammarsToWork[ccq].equals(newGrammar)) {
                                            exist = true;
                                            break;
                                        }
                                    }

                                    if (!exist)
                                        grammarsToWork[grammarsToWorkCount++] = newGrammar;
                                }

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

            if (grammarsToWorkCount != 0 && !leftRecursive.containsKey(grammarsToWork)) {
                newProduction = new Production(grammarsToWork);
                Production myProduction = new Production();
                newProduction.setMove(checker);

                Production oldProduction = checkIfRepeated(newProduction);

                if (oldProduction != null) {
                    if (oldProduction.equals(production)) {
                        oldProduction.setRepeated();
                        oldProduction.setNext(oldProduction);
                    } else
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

    private static HashMap<Grammar[], Integer> leftRecursive = new HashMap<>();

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

        String[] lookAhead = new String[500];
        int[] n = {0};
        int y = gm.getLookAheadCount();
        while (afterDot.length > y && afterDot[y] != null) {
            String lookAheadElement = afterDot[y++];
            if (grammarSort.containsKey(lookAheadElement)) {
                for (String ll : findFirst(lookAheadElement)) {
                    if (ll == null)
                        break;
                    int ccc = 1;
                    for (int qq = 0; qq < lookAhead.length; qq++) {
                        if (lookAhead[qq] == null)
                            break;
                        if (ll.equals(lookAhead[qq])) {
                            ccc = 0;
                            break;
                        }
                    }
                    if (ccc == 1)
                        lookAhead[n[0]++] = ll;
                }
                doneFromIt = new HashMap<>();
                firstSet = new String[50];
                firstSetCount = 0;
            } else {
                int ccc = 1;
                for (int qq = 0; qq < lookAhead.length; qq++) {
                    if (lookAhead[qq] == null)
                        break;
                    if (lookAheadElement.equals(lookAhead[qq])) {
                        ccc = 0;
                        break;
                    }
                }
                if (ccc == 1)
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
            afterDot = new String[500];
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
}