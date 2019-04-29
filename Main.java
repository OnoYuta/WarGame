package warGame;

import java.util.Scanner;

public class Main {

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		WarGame warGame = new WarGame();
		warGame = warGame.askUserToContinue(scanner);
		warGame.start(scanner);
		scanner.close();
	}

}