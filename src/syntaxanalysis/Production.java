package syntaxanalysis;

public class Production {
    private Grammar[] grammars;
    private int count;
    private Production[] next;
    private int nextCount;
    private String move;
    String repeated = "No";

    public String getRepeated() {
        return repeated;
    }

    public int getNextCount() {
        return nextCount;
    }

    public int getProductionNumber() {
        return productionNumber;
    }

    public void setProductionNumber(int productionNumber) {
        this.productionNumber = productionNumber;
    }

    private int productionNumber;

    public String getMove() {
        return move;
    }

    public void setRepeated() {
        this.repeated = "Yes";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Production that = (Production) o;

        if (count != that.count)
            return false;
        int x = 0;
        for (Grammar yy : grammars) {
            if (yy == null)
                break;
            int z = 0;
            for (Grammar gm : that.grammars) {
                if (gm == null)
                    break;
                if (gm.equals(yy)) {
                    x++;
                    z = 1;
                    break;
                }
            }

        }
        if (x == count)
            return true;
        else return false;
    }

    public Production() {
        grammars = new Grammar[60];
        count = 0;
        next = new Production[50];
        nextCount = 0;
        move = "";
    }

    public Production(Grammar[] grammars) {
        this.grammars = grammars;
        int i = 0;
        for (Grammar gm : grammars) {
            if (gm == null)
                break;
            i++;
        }
        count = i;
        next = new Production[50];
        nextCount = 0;
        move = "";
    }

    public boolean checkExist(String head) {
        if (this.count == 0)
            return false;
        for (Grammar gm : grammars) {
            if (gm == null)
                break;
            if (gm.getHead().equals(head))
                return true;
        }
        return false;
    }

    public Grammar[] getGrammars() {
        return grammars;
    }

    public void setMove(String move) {
        this.move = move;
    }

    public Production[] getNext() {
        return next;
    }

    public void setNext(Production next) {

        this.next[nextCount++] = next;
    }

    public void add(Grammar grammar) {
        grammars[count++] = grammar;
    }
}
