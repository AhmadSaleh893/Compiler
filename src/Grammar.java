import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Grammar {
    private String head;
    private String[][] childrenList;
    private String[] lookAhead;
    private int childrenCount;
    private int lookAheadCount;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Grammar grammar = (Grammar) o;

        if (childrenCount != grammar.childrenCount)
            return false;

        if (!head.equals(grammar.head))
        {
            return false;
        }

        for (int i = 0; i < childrenCount; i++)
        {
            String[] child1 = childrenList[i];
            String[] child2 = grammar.childrenList[i];
            if (child1 == child2 && child1 == null)
                break;
            if (child1.length != child2.length)
                return false;
                for (int j = 0; j < child1.length; j++)
                {
                    if (!child1[j].equals(child2[j]))
                    {
                        return false;
                    }
                }
        }

        if (lookAheadCount != grammar.getLookAheadCount())
            return false;

        for (int i = 0; i < lookAheadCount; i++)
        {
            String a = lookAhead[i];
            String b = grammar.lookAhead[i];
            if (!a.equals(b))
                return false;

        }

        return true;
    }

    public Grammar() {
        head = null;
        childrenList = null;
        lookAhead = null;
        childrenCount = 0;
        lookAheadCount = 0;
    }

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public String[][] getChildrenList() {
        return childrenList;
    }

    public void setChildrenList(String[][] childrenList) {
        this.childrenList = childrenList;
        if (childrenList == null)
            childrenCount = 0;
        else
            this.childrenCount = childrenList.length;
    }

    public String[] getLookAhead() {
        return lookAhead;
    }
    public void resizeLookAhead(String newItem)
    {
        String[] newStrings = new String[lookAheadCount+1];
        int i = 0;
        if (lookAheadCount == 0)
        {
            lookAhead = new String[1];
            lookAhead[0] = newItem;
            return;
        }
        for (String v : lookAhead)
        {
            if (v == null)
                break;
            newStrings[i++] = v;
        }
        newStrings[i] = newItem;
        this.lookAheadCount++;
        lookAhead = newStrings;
    }
    public void setLookAhead(String[] lookAhead) {
        if (lookAhead == null)
        {
            this.lookAhead = null;
            return;
        }
        int c = 0;

        for (String cc : lookAhead)
        {
            if (cc == null)
                break;
            c++;
        }
        this.lookAhead = new String[c];
        int i = 0;
        for (;i<c;)
        {
           this.lookAhead[i] = lookAhead[i];
        i++;
        }
        this.lookAheadCount = this.lookAhead.length;
    }

    public int getChildrenCount() {
        return childrenCount;
    }

    public void setChildrenCount(int childrenCount) {
        this.childrenCount = childrenCount;
    }

    public int getLookAheadCount() {
        return lookAheadCount;
    }

    public void setLookAheadCount(int lookAheadCount) {
        this.lookAheadCount = lookAheadCount;
    }

    public Grammar(String head, String[][] childrenList,  String[] lookAhead) {
        this.head = head;
        int length = childrenList.length;
        this.childrenList = new String[length][30];
        lookAhead = new String[50];
        childrenCount = 0;
        lookAheadCount = 0;
//        Arrays.stream(childrenList).map(child -> this.childrenList[childrenCount++] = child );
        for (int i = 0; i < childrenList.length;i++)
        {
            String[] child = childrenList[i];
            this.childrenList[childrenCount++] = child;
        }
        Arrays.stream(lookAhead).map(element -> this.lookAhead[lookAheadCount++] = element );
    }

    public Grammar(Grammar grammar)
    {
        this.head = new String();
        for (int i = 0 ; i< grammar.head.length();i++)
        {
            this.head += grammar.head.charAt(i);
        }
        int i = 0;
        this.childrenList = new String[grammar.childrenCount][];
        for (String[] a : grammar.childrenList)
        {
            if (a == null)
                break;
            this.childrenList[i] = new String[a.length];
            int j = 0;
            for (String b : a)
            {
                if (b == null)
                    break;
                this.childrenList[i][j] = b;
                j++;
            }
            i++;
        }
        this.childrenCount = grammar.childrenCount;
        i=0;
        this.lookAhead = new String[grammar.lookAheadCount];
        if (grammar.lookAhead != null) {
            for (String a : grammar.lookAhead) {
                if (a == null)
                    break;
                this.lookAhead[i++] = a;
            }
        }
        else
            lookAhead = null;
        this.lookAheadCount = grammar.lookAheadCount;
    }
}