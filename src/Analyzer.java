import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class Analyzer {

    public static HashMap<String, String> getHashMap() {
        return hashMap;
    }
    
    private static HashMap<String, String> hashMap;
    private static HashMap<Integer, Character> hash = new HashMap();
    private final static String KEYWORD = "Keyword";
    private final static String IDENTIFIER = "ID(%s)";
    private final static String STRING = "STRING(%s)";
    private final static String INT = "INT(%s)";
    private final static String DOUBLE = "DOUBLE(%s)";
    private final static String TOKEN = "T_";

    public static void init() {
        hashMap = new HashMap<>();
        List<String> keywordsList = List.of("void"
                , "int"
                , "double"
                , "bool"
                , "string"
                , "class"
                , "interface"
                , "null"
                , "this"
                , "extends"
                , "implements"
                , "for"
                , "while"
                , "if"
                , "else"
                , "return"
                , "break"
                , "new"
                , "NewArray"
                , "Print"
                , "ReadInteger"
                , "ReadLine");

        List<String> operatorsList = List.of("+", "-"
                , "*", "/"
                , "%", "<"
                , "<=", ">"
                , ">=", "="
                , "==", "!="
                , "&&", "||", "[]"
                , "!", ";", "[", "]", "(", ")", "{", "}", ".", "&", "|"
        );

        for (String key : keywordsList) {//add keywords
            if (!hashMap.containsKey(key)) {
                hashMap.put(key, KEYWORD);
            }
        }

        for (String key : operatorsList) {//add operators
            if (!hashMap.containsKey(key)) {
                hashMap.put(key, TOKEN);
            }
        }

        for (int i = 48; i < 58; i++)
        {
            hash.put(i, (char)i);
        }
    }

    private static String token_recognizer(String token) {
        token = token.trim();
        if (token.length() == 1) {
            if (token.charAt(0) == '+') {
                return String.valueOf('+');
            } else if (token.charAt(0) == '-') {
                return String.valueOf('-');
            } else if (token.charAt(0) == '*') {
                return String.valueOf('*');
            } else if (token.charAt(0) == '/') {
                return String.valueOf('/');
            } else if (token.charAt(0) == '%') {
                return String.valueOf('%');
            } else if (token.charAt(0) == '<') {
                return String.valueOf('<');
            } else if (token.charAt(0) == '>') {
                return String.valueOf('>');
            } else if (token.charAt(0) == '!') {
                return String.valueOf('!');
            } else if (token.charAt(0) == '=') {
                return String.valueOf('=');
            } else if (token.charAt(0) == '[') {
                return String.valueOf('[');
            } else if (token.charAt(0) == ']') {
                return String.valueOf(']');
            } else if (token.charAt(0) == '{') {
                return String.valueOf('{');
            } else if (token.charAt(0) == '}') {
                return String.valueOf('}');
            } else if (token.charAt(0) == '(') {
                return String.valueOf('(');
            } else if (token.charAt(0) == ')') {
                return String.valueOf(')');
            } else if (token.charAt(0) == '.') {
                return String.valueOf('.');
            } else if (token.charAt(0) == ';')
                return String.valueOf(';');
            else {
                return "Not_Found1 " + token;
            }
        } else if (token.length() == 2) {
            switch (token) {
                case "<=":
                    return "LessOrEqual";
                case ">=":
                    return "MoreOrEqual";
                case "==":
                    return "Equal";
                case "!=":
                    return "NotEqual";
                case "&&":
                    return "And";
                case "||":
                    return "Or";
                case "[]":
                    return "Dims";
                default:
                    return "Not_Found2 " + token;
            }
        } else {
            return "Not_Found " + token;
        }
    }

    private static Boolean isDigit(String element) {
        if (element.length() == 1 && element.charAt(0) == '0')
            return true;
        if (element.charAt(0) == '0' && (element.charAt(1) == 'x' || element.charAt(1) == 'X') )
            return true;

        for (int i = 0; i < element.length(); i++)
        {
            if ( element.charAt(i) != '.' && element.charAt(i) != '+' && element.charAt(i) != '-' && element.charAt(i) != 'E' && element.charAt(i) != 'e' && !hash.containsKey((int)element.charAt(i)))
            {
                return false;
            }
        }
        return true;
    }

    public static void split() {
        String str;
        Scanner in = new Scanner(System.in);
        List<String> list = new ArrayList<>();
        while (true) {
            str = in.next();
            if (str.equals("m"))
                break;
            list.add(str);
        }

        for (int i = 0; i < list.size(); i++) {
            if (i != list.size() - 1)
                System.out.print("\"" + list.get(i) + "\"\n,");
            else
                System.out.println("\"" + list.get(i) + "\"");
        }
    }

    public static void analyzeLine(String line, FileWriter fileWriter) throws IOException {
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

        for (int i = 0; i < line.length(); ) {
            Boolean is_string = false;
            String element = "";
            char curr;
            String bind1 = "";
            curr = line.charAt(i++);
            if (curr == '"') { // this is a String
                is_string = true;
                while (true) {
                    element += curr;
                    curr = line.charAt(i++);
                    if (curr == '"')
                        break;
                    if (i == line.length())
                        break;
                }
                element += '"';
            } else if (curr == '@') { // this is an annotation
                i--;
                while (true) {
                    if (i >= line.length() || curr == ' ' || (hashMap.containsKey(String.valueOf(curr)) && curr != '@'))
                        break;
                    curr = line.charAt(i++);
                    element += curr;
                }
            } else { //anything else
                i--;
                Boolean exceed = false;
                while (true) {
                    if (i == line.length())
                        break;
                    curr = line.charAt(i++);

                    if (hashMap.containsKey(String.valueOf(curr)) || curr == ' ') {

                        if (curr != ' ') {
                            if (i != line.length()) {
                                String x = "";
                                char ch = line.charAt(i);
                                x = hashMap.get(curr + "" + ch);
                                if (curr == '_') {
                                } else if (x != null && x.equals(TOKEN)) {
                                    bind1 = x + token_recognizer(curr + "" + ch);
                                    i++;
                                } else if (x != null && ch == ']') {
                                    bind1 = hashMap.get(String.valueOf(curr)) + token_recognizer(String.valueOf(curr + ch));
                                    i++;
                                } else {
                                    bind1 = hashMap.get(String.valueOf(curr)) + (int) token_recognizer(String.valueOf(curr)).charAt(0);
                                }
                            } else {
                                bind1 = hashMap.get(String.valueOf(curr)) + (int) token_recognizer(String.valueOf(curr)).charAt(0);
                            }
                        }
                        if (line.charAt(i - 1) == '.') {

                            if (element.length()>0 && !isDigit(element))
                            {
                                break;
                            }
                            else
                                bind1 = "";
                        }

                        if (curr != '_' && line.charAt(i - 1) != '.' && exceed == false)
                            break;
                        if (curr == '+' || curr == '-')
                        {
                            exceed = false;
                            bind1 = "";
                        }
                    }
                    if (!element.isEmpty() && isDigit(element) && i != line.length() && curr == 'E' && element.contains("."))
                    {
                        exceed = true;
                    }

                    element += curr;
                }
            }

            if (hashMap.containsKey(element)) {
                String type = hashMap.get(element);
                if (type.equals("Keyword")) {
                    String bind = TOKEN+element;
                    bufferedWriter.append(bind + "\n");
                    bufferedWriter.flush();
                }
            } else if (!element.isEmpty() && !element.isBlank() && !element.equals(" ")) {
                String bind;
                Boolean v = false;
                if (element.contains(".") && !isDigit(element) )
                {
                    String x[] = element.split("\\.");
                    v = isDigit(x[x.length-1]);
                }
                if (isDigit(element))
                    v = true
                            ;
                if ((isDigit(element) || element.charAt(0) == '.') && v == true) {
//                    System.out.println("Is Digigt");
                    double x;
                    int not_valid = 0;
                    Boolean is_double = element.contains(".");
                    if (is_double) {

                        if (element.charAt(0) == '.' ) {
                            bind = "Not_Valid_Double("+element+")";
                            bufferedWriter.append(bind + "\n");
                            bufferedWriter.flush();
                            not_valid = 1;
                        }
                        if (element.charAt(element.length()-1) == '.')
                            element+='0';
                        bind = String.format(DOUBLE, element);
                    } else {
                        bind = String.format(INT, element);
                    }
                    if (not_valid == 0) {
                        bufferedWriter.append(bind + "\n");
                        bufferedWriter.flush();
                    }
                } else if (is_string) {
                    bind = String.format(STRING, element);
                    bufferedWriter.append(bind + "\n");
                    bufferedWriter.flush();
                } else {
                    if (element.contains(".")) {
                        String split[] = element.split("\\.");
                        if (split[0].isEmpty()) {

                            bufferedWriter.append("T_" + (int)'.' + "\n");
                            if (!isDigit(split[1]))
                                bind = String.format(IDENTIFIER, split[1]);
                            else
                                bind = String.format(DOUBLE, split[1]);
                            bufferedWriter.append(bind + "\n");
                            bufferedWriter.flush();
                        } else {
                            if (split[0].contains("e") || !isDigit(split[0]))
                                bind = String.format(IDENTIFIER, split[0]);
                            else
                                bind = String.format(DOUBLE, split[0]);
                            bufferedWriter.append(bind + "\n");
                            bufferedWriter.append("T_" + (int)'.' + "\n");
                            if (!isDigit(split[1]))
                                bind = String.format(IDENTIFIER, split[1]);
                            else
                                bind = String.format(DOUBLE, split[1]);
                            bufferedWriter.append(bind + "\n");
                            bufferedWriter.flush();
                        }
                    }else {
                        bind = String.format(IDENTIFIER, element);
                        bufferedWriter.append(bind + "\n");
                        bufferedWriter.flush();
                    }
                }
            }
            if (!bind1.equals("")) {
                bufferedWriter.append(bind1 + "\n");
                bufferedWriter.flush();
            }
        }
    }
}