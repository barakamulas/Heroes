import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Test {

    public static void main(String[] args) {

        System.out.println(solution("13 7"));

        //13 7

        //"4 5 6 - 7 +"
        //13 DUP 4 POP 5 DUP + DUP + -13 DUP 4 POP 5 DUP + DUP + -
        //5 6 + -

        //13 DUP 4 POP 5 DUP + DUP + -
    }

    public static int solution(String S){
        try{
            List<Integer> stack = new ArrayList<>();
            List<String> operations;
            operations = Arrays.asList(S.split(" "));
            for (String operation : operations) {
                int largestIndex = stack.size() - 1;
                switch (operation) {
                    case "+": {
                        int num = stack.get(largestIndex) + stack.get(largestIndex - 1);
                        stack.set(largestIndex - 1, (num));
                        stack.remove(largestIndex);
                        break;
                    }
                    case "-": {
                        int num = stack.get(largestIndex) - stack.get(largestIndex - 1);
                        stack.set(largestIndex - 1, (num));
                        stack.remove(largestIndex);
                        break;
                    }
                    case "POP":
                        stack.remove(largestIndex);
                        break;
                    case "DUP":
                        stack.add(stack.get(largestIndex));
                        break;
                    default: {
                        int num = Integer.parseInt(operation);
                        stack.add(num);
                        break;
                    }
                }
            }
            return (stack.get(stack.size()-1));
        }catch(Exception ex){
            return -1;
        }
    }
}
