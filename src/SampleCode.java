import java.util.ArrayDeque;
import java.util.Stack;

public class SampleCode {
    public static Boolean balancedParentheses(String line)
    {
        System.out.println(line);
        for (int  i = 0; i < line.length(); i++) {
            char character = line.charAt(i);
            Stack<Object> stack = new Stack<>();
            if (character == '(' || character == '[' || character == '{') {
//                System.out.println(1+"   "+ character);
                stack.push(character);
//                System.out.println(11);
            } else if (character == ')') {
//                System.out.println(2);
                char charFromStack = (char) stack.peek();
                if (charFromStack != '(')
                    return false;
                stack.pop();
            } else if (character == ']') {
                char charFromStack = (char) stack.peek();
                if (charFromStack != '[')
                    return false;
                stack.pop();
            } else if (character == '}') {
                char charFromStack = (char) stack.peek();
                if (charFromStack != '{')
                    return false;
                stack.pop();
            }
        }
        return true;
    }

    public static void main(String[] args) {
        int z = 55;
        double dd = 20.7;
        int ys = 6;
//        (#)
    }

}
