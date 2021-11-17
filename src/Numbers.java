public class Numbers {
    private Integer[] numbers;
    private int count;

    public Integer[] getNumbers() {
        return numbers;
    }

    public int getCount() {
        return count;
    }

    public Numbers(int number)
    {
        count = 0;
        numbers = new Integer[20];
        numbers[count++] = number;
    }
    public Numbers()
    {
        numbers = new Integer[20];
        count = 0;
    }

    public void add(int number)
    {
        numbers[count++] = number;
    }
}
