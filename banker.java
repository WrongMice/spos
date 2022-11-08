import java.util.Scanner;

public class banker{
    private int need[][], allocate[][], max[][], avail[][], np, nr;

    private void input() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter no. of processes: ");
        np = sc.nextInt();
        System.out.print("Enter no. of Resources : ");
        nr = sc.nextInt();

        need = new int[np][nr];
        max = new int[np][nr];
        allocate = new int[np][nr];
        avail = new int[1][nr];

        System.out.println("Enter Allocation matrix:");
        for (int i = 0; i < np; i++)
            for (int j = 0; j < nr; j++)
                allocate[i][j] = sc.nextInt();

        System.out.println("Enter Max-Need matrix:");
        for (int i = 0; i < np; i++)
            for (int j = 0; j < nr; j++)
                max[i][j] = sc.nextInt();

        System.out.println("Enter Available matrix:");
        for (int j = 0; j < nr; j++)
            avail[0][j] = sc.nextInt();
        sc.close();
    }

    private int[][] calc_need() {
        for (int i = 0; i < np; i++)
            for (int j = 0; j < nr; j++) {
                need[i][j] = max[i][j] - allocate[i][j];
            }
        return need;
    }

    private boolean check(int i) {

        for (int j = 0; j < nr; j++)
            if (avail[0][j] < need[i][j])
                return false;
        return true;
    }

    public void isSafe() {
        input();
        calc_need();
        boolean done[] = new boolean[np];
        int j = 0;

        while (j < np) {
            boolean allocated = false;

            for (int i = 0; i < np; i++)
                if (!done[i] && check(i)) {
                    for (int k = 0; k < nr; k++)
                        avail[0][k] = avail[0][k] + allocate[i][k];
                    System.out.println("Allocated process : " + (i + 1));
                    allocated = done[i] = true;
                    j++;
                }
            if (!allocated) break;
        }
        if (j == np) {
            System.out.println("\nSafely allocated");
        } else {
            System.out.println("All proceess cant be allocated safely");
        }
    }

    public static void main(String[] args) {
        new banker().isSafe();
    }
}