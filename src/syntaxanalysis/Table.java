package syntaxanalysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static syntaxanalysis.SyntaxAnalysis.grammarSort;
import static syntaxanalysis.SyntaxAnalysis.grammars;

public class Table {

    List<Production> productions = SyntaxAnalysis.productions;
    HashMap<String, Integer> terminals = SyntaxAnalysis.terminals;
    HashMap<String, Integer> nonTerminals = SyntaxAnalysis.grammarSort;
    public static final String ACCEPT = "accept";

    public class Node {
        HashMap<String, String> hashMap;
        int count;

        public HashMap<String, String> getHashMap() {
            return hashMap;
        }

        public Node() {
            hashMap = new HashMap();
            count = 0;
        }

        void add(String key, String value) {
            hashMap.put(key, value);
        }
    }

    Node[] nodes;

    public Table() {
        nodes = new Node[productions.size()];
    }

    class GrammarToSend {
        List<Grammar> grammarsToSend;
        List<String> moves;

        GrammarToSend(List<Grammar> grammars, List<String> moves) {
            this.moves = moves;
            grammarsToSend = grammars;
        }
    }

    private List<Grammar> reductionExist(Grammar[] myGrammars) {
        List<Grammar> grammarsToSend = new ArrayList<>();
        for (Grammar x : myGrammars) {
            if (x == null)
                break;
            String[] children = x.getChildrenList()[0];
            if (children[children.length - 1].equals(".")) {
                grammarsToSend.add(x);
            }
        }

        return grammarsToSend;
    }

    public Node[] findParseTable() {
        for (int i = 0; i < productions.size(); i++) {
            Production myProduction = productions.get(i);
            if (myProduction == null)
                break;
            Production[] nextList = myProduction.getNext();
            int myProductionNumber = myProduction.getProductionNumber();
            nodes[myProductionNumber] = new Node();
            if (myProduction.getNextCount() != 0) {
                List<Grammar> red = null;
                red = reductionExist(myProduction.getGrammars());

                if (red != null) {
                    for (Grammar x : red) {
                        String reduction = "r";
                        int index = grammarSort.get(x.getHead());
                        Grammar grammar = grammars[index];
                        String[][] childrenList = grammar.getChildrenList();


                        int before = SyntaxAnalysis.beforeGrammarCount.get(grammar.getHead()) - 1;
                        if (grammar.getChildrenCount() == 1) {
                            reduction += before + 1;
                        } else {
                            int count = 0;
                            int cd = 0;

                            for (String[] grandSon : childrenList) {
                                for (String[] v : x.getChildrenList()) {
                                    count++;
                                    if (equals(grandSon, v)) {
                                        cd = 1;
                                        break;
                                    }
                                }
                                if (cd == 1)
                                    break;
                            }
                            reduction += before + count;
                        }
                        for (String myLookAhead : x.getLookAhead())
                            nodes[myProductionNumber].add(myLookAhead, reduction);

                    }
                }
                for (Production myNext : nextList) {
                    if (myNext == null)
                        break;
                    String move = myNext.getMove();
                    String myNextNumber = String.valueOf(myNext.getProductionNumber());
                    if (nonTerminals.containsKey(move)) {
                        nodes[myProductionNumber].add(move, myNextNumber);
                    } else if (terminals.containsKey(move)) {
                        String shiftNumber = "s" + myNextNumber;
                        nodes[myProductionNumber].add(move, shiftNumber);
                    } else if (myProduction.getRepeated().toLowerCase(Locale.ROOT).equals("yes")) {
                        String shiftNumber = "s" + myProductionNumber;
                        nodes[myProductionNumber].add(move, shiftNumber);
                    }



                    /*for (Grammar cc : myNext.getGrammars())
                    {
                        String[][] childrenList = cc.getChildrenList();
                    }*/
                }
            } else {
                Grammar myGrammar = myProduction.getGrammars()[0];
                String[] lookAhead = myGrammar.getLookAhead();

                if (myGrammar.getHead().equals(SyntaxAnalysis.STARTER_GRAMMAR)) {
                    nodes[myProductionNumber].add(lookAhead[0], ACCEPT);
                } else {
                    String reduction = "r";
                    Object f = SyntaxAnalysis.grammarSort.get(myGrammar.getHead());
                    String[][] childrenList;
                    if (f != null) {
                        int index = (int) f;
                        Grammar grammar = grammars[index];

                        childrenList = grammar.getChildrenList();
                        int before = SyntaxAnalysis.beforeGrammarCount.get(grammar.getHead()) - 1;
                        if (grammar.getChildrenCount() == 1) {
                            reduction += before + 1;
                        } else {
                            int count = 0;
                            int cd = 0;
                            for (String[] grandSon : childrenList) {
                                for (String[] v : myGrammar.getChildrenList()) {
                                    count++;
                                    if (equals(grandSon, v)) {
                                        cd = 1;
                                        break;
                                    }
                                }
                                if (cd == 1)
                                    break;
                            }
                            reduction += before + count;
                        }


                    }
                    for (String myLookAhead : lookAhead)
                        nodes[myProductionNumber].add(myLookAhead, reduction);
                }
            }
        }
        return nodes;
    }

    public static boolean equals(String[] a, String[] b) {

        for (String x : a) {
            int z = 0;
            for (String y : b) {
                if (y.equals(x)) {
                    z = 1;
                    break;
                }
            }
            if (z == 0)
                return false;
        }
        return true;
    }
}
