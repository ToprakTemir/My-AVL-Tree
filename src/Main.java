import java.io.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {

        if (args.length < 2) {
            System.out.println("Please provide input and output file names.");
            return;
        }

        String inputFileName = args[0];
        String outputFileName = args[1];

        Node<Double> boss;
        File inputFile = new File(inputFileName);
        Scanner input = new Scanner(inputFile);

        String firstBossName = input.next();
        Double firstBossGMS = Double.parseDouble(input.next());
        boss = new Node<>(firstBossName, firstBossGMS);

        FileWriter fileWriter = new FileWriter(outputFileName, false);
        PrintWriter printWriter = new PrintWriter(fileWriter);


        while(input.hasNext()) {
            String[] curLine = input.nextLine().split(" ");
            switch (curLine[0]) {
                case "MEMBER_IN" -> {
                    boss = boss.addMember(new Node<>(curLine[1], Double.parseDouble(curLine[2])), printWriter);
                    printWriter.flush();
                }
                case "MEMBER_OUT" -> {
                    boss = boss.removeMember(curLine[1], Double.parseDouble(curLine[2]), printWriter);
                    printWriter.flush();
                }
                case "INTEL_TARGET" -> {
                    double GMS1 = Double.parseDouble(curLine[2]);
                    double GMS2 = Double.parseDouble(curLine[4]);
                    Node<Double> lowestCommonSuperior = Node.lowestCommonSuperior(boss, GMS1, GMS2);
                    printWriter.println("Target Analysis Result: " + lowestCommonSuperior.name + " " + String.format("%.3f", lowestCommonSuperior.GMS));
                    printWriter.flush();
                }
                case "INTEL_DIVIDE" -> {
                    boss.divide();
                    printWriter.println("Division Analysis Result: " + Node.divideSelectionCounter);
                    printWriter.flush();
                    Node.divideSelectionCounter = 0;
                }
                case "INTEL_RANK" -> {
                    String nameOfRankTarget = curLine[1];
                    double targetGMS = Double.parseDouble(curLine[2]);
                    // first, find the rank (aka distance to the boss) of the target
                    int rankOfTarget = boss.findDistanceToBoss(targetGMS, 0);
                    printWriter.print("Rank Analysis Result:");
                    boss.printRank(rankOfTarget, printWriter);
                    printWriter.print("\n");
                    printWriter.flush();
                }
            }
        }
    }
}
