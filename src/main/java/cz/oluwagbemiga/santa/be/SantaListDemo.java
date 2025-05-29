package cz.oluwagbemiga.santa.be;

import java.util.*;
import java.util.stream.IntStream;

public class SantaListDemo {

    private boolean isLocked = false;
    private List<Map.Entry<String, String>> in = new ArrayList<>();
    private Map<String, Map.Entry<String, String>> out = new HashMap<>();

    public void addEntry(Map.Entry<String, String> input) {
        String entryString = input.getKey() + " : " + input.getValue();
        if (isLocked)
            throw new RuntimeException("The list has been shuffled. Unable to add entry: " + entryString);
        in.add(input);
        System.out.println(entryString + " was added to Santa's list.");
    }

    public void shuffle() {
        if (isLocked) throw new RuntimeException("The list has been shuffled. Unable to shuffle again.");

        System.out.println("Shuffling...");
        Collections.shuffle(in);
        isLocked = true;
        IntStream.range(0, in.size())
                .forEach(i -> {
                    if (i == in.size() - 1) {
                        System.out.println("DEBUG:Index " + in.size() + " out of bounds for length " + in.size() + ". Getting index 0 instead.");
                        out.put(in.get(i).getKey(), in.get(0));
                    } else {
                        out.put(in.get(i).getKey(), in.get(i + 1));
                    }
                });
        System.out.println("Shuffled.");

    }

    public void printStatus() {
        String inStatus = "IN LIST: \n";
        String outStatus;
        if (!isLocked) outStatus = "\nOUT LIST: EMPTY (Has not been shuffled)\n";
        else outStatus = "\nOUT LIST: \n";

        for (Map.Entry<String, String> entry : in) {
            String toSave = entry.getKey() + " wants " + entry.getValue() + "\n";
            inStatus = inStatus.concat(toSave);
        }

        if (isLocked) {
            inStatus = inStatus.replace("IN LIST: \n", "SHUFFLED LIST:\n");
            for (Map.Entry<String, Map.Entry<String, String>> i : out.entrySet()) {
                outStatus = outStatus.concat(i.getKey() + " is getting " + i.getValue().getValue() + " for " + i.getValue().getKey() + "\n");
            }
        }
        System.out.println("\n-------STATUS-------");
        System.out.println(inStatus + outStatus + "--------------------\n");
    }
}
