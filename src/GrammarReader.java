import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class GrammarReader {

    private Grammar[] grammars;
    int count;
    private HashMap<String, Numbers> grammarSort;

    public Grammar[] getGrammars() {
        return grammars;
    }

    public int getCount() {
        return count;
    }

    public HashMap<String, Numbers> getGrammarSort() {
        return grammarSort;
    }

    public GrammarReader(String STARTER_GRAMMAR) throws IOException {
        File file = new File("src/text.txt");
        grammars = new Grammar[71];
        grammarSort = new HashMap<>();
        count = 0;
        List<String> lines = Files.readAllLines(file.toPath());
        grammarSort.put(STARTER_GRAMMAR, new Numbers(count));
        grammars[count++] = new Grammar(STARTER_GRAMMAR, new String[][]
                {
                        new String[]{".", "Program"}
                }, null);
        int o = 0;
        for (String line : lines) {
            if (!line.isEmpty()) {
                System.out.println(o++);
                String[] both = line.split("->");
                String header = both[0].trim();
                String[] secondSide = both[1].trim().split(" ");
                String[][] childrenList = new String[1][secondSide.length+1];
                int i = 1;
                childrenList[0][0] = ".";
                for (String element : secondSide)
                {
                    if (!element.isEmpty()) {
                        element = element.trim();
                        childrenList[0][i++] = element;
                    }
                }
                if (!grammarSort.containsKey(header)) {
                    grammarSort.put(header, new Numbers(count));
                }
                else
                {
                    Numbers numbers = grammarSort.get(header);
                    numbers.add(count);
                }
                    grammars[count++] = new Grammar(header,childrenList,null);

            }
        }
    }
}
