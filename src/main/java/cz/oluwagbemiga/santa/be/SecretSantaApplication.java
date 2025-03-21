package cz.oluwagbemiga.santa.be;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Map;
import java.util.stream.IntStream;


@SpringBootApplication
public class SecretSantaApplication {

	public static void main(String[] args) {

		test(args);

		SpringApplication.run(SecretSantaApplication.class, args);
	}

	static void test(String[] args){

		SantaList testList = new SantaList();

		int numPeople = 8;
		if (args.length < 0) {
			try {
				numPeople = Integer.parseInt(args[0]);
			} catch (NumberFormatException e) {
				e.printStackTrace();
				System.out.println("Using default number!");
			}
		}

		System.out.println("\n---------------------OK_SCENARIO---------------------\n");
		IntStream.range(1, numPeople + 1)
				.boxed()
				.map(i -> Map.entry("Person_" + i, "Gift_" + i))
				.forEach(testList::addEntry);

		testList.printStatus();
		testList.shuffle();
		testList.printStatus();

		System.out.println("-------------------NOT_OK_SCENARIOS-------------------\n");

		try {
			testList.addEntry(Map.entry("Another_Person", "Another_Gift"));
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			testList.shuffle();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
