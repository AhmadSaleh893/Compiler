package syntaxanalysis;

import lexicalanalysis.Analyzer;

import java.util.*;

import static syntaxanalysis.SyntaxAnalysis.*;

public class Parsing {
    Stack stack;
    Table.Node[] nodes;
    public Parsing(Table.Node[] nodes)
    {
        this.nodes = nodes;
        stack = new Stack();
        stack.add("$");
        stack.add(0);
    }

    private final static String IDENTIFIER = "ID";
    private final static String STRING = "STRING";
    private final static String INT = "INT";
    private final static String DOUBLE = "DOUBLE";
    private final static String TOKEN = "T";
    public Boolean checkTokens(List<String> tokens)
    {
        tokens.add("$");
        for (int i = 0; i < tokens.size();) {
            String myToken = tokens.get(i);
            String[] both = null;
            String value;

            if (myToken.charAt(0) == '$')
            {
                value = "$";
            }
            else {
                if (myToken.charAt(0) != 'T') {
                    both = myToken.split("\\(");
                    value = both[1].split("\\)")[0];
                } else {
                    both = myToken.split("_");
                    value = both[1];
                }

                if (myToken.charAt(0) == 'I' && myToken.charAt(1) == 'D')
                    value = "ident";
                else if (both[0].equals(INT)) {
                    value = "intConstant";
                } else if (both[0].equals(DOUBLE)) {
                    value = "doubleConstant";
                } else if (both[0].equals(STRING)) {
                    value = "stringConstant";
                }
//                    else
//                    if (both[0].equals())
//                    {
//                        value = "doubleConstant";
//                    }
            }
            Object stackValue = stack.peek();
            int number = 0;
            if (stackValue instanceof Integer) {
                number = (int) stackValue;
                String operation = "";
                if (Analyzer.isDigit(value))
                {
                    value = String.valueOf((char)Integer.parseInt(value));
                }

                operation = nodes[number].getHashMap().get(value);
                if (operation == null)
                {
                    System.out.println();
                    System.out.println("ATTENTION!!! An ERROR:");
                    System.out.println();
                    System.out.println("In row " + number + " of the table has no operation there for " + value + ". \nit's not one of the look ahead.\n  it's not exist.\n it occurred an error.");
                    return false;
                }
                char op = operation.charAt(0);
                if (operation.equals(Table.ACCEPT)) {
                    System.out.println("Program' -> Program");
                    System.out.println("Accepted");
                    return true;
                }
                if (op == 's')
                {

                    stack.add(value);
                    stack.add(Integer.parseInt(operation.split("s")[1]));
                    tokens.remove(myToken);
//                    i--;
                }
                else
                    if (op == 'r')
                    {
                        int n = Integer.parseInt(operation.split("r")[1]);
                        System.out.print("Reduce using rule (" + n + ")" );

                        String[] children = null;

                        Grammar grammar = null;
                        String[][] list = null;
                        int qw;
                        for (qw = n; qw >= 0; qw--)
                        {
                            if (getKey.containsKey(qw)) {
                                String z = getKey.get(qw);
                                int f = grammarSort.get(z);
                                grammar = grammars[f];
                                list = grammar.getChildrenList();
                                break;
                            }
                        }

                        for (int zx = 0; zx <= n - qw; zx++)
                        {
                            children = list[zx];
                        }

                        System.out.print( "  " + grammar.getHead() + " ->");
                        for (String x : children) {
                            if (!x.equals("."))
                                System.out.print(" " + x + " ");
                        }
                        System.out.println();
                        int grandSonCount = children.length - 1;
                        for (int j = 0; j < grandSonCount * 2; j++)
                        {
                            stack.pop();
                        }
                        int reductionNumberHelper = (int)stack.peek();
                        stack.add(grammar.getHead());
                        String nonTerminalNumber = nodes[reductionNumberHelper].getHashMap().get(grammar.getHead());
                        stack.add(Integer.parseInt(nonTerminalNumber));
                    }
            }


        }

        return false;
    }

}
