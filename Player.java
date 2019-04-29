package warGame;

import java.io.Serializable;
import java.util.ArrayList;

public class Player implements Serializable{
	private static final long serialVersionUID = 3698405703966815730L;
	private ArrayList<Card> hand = new ArrayList<>();
	private ArrayList<Card> wins = new ArrayList<>();
	private String name;

	public Player(String name) {
		this.name = name;
	}

	public void draw(Cards cards) {
		hand.add(cards.takeOut());
	}

	public int getHandSize() {
		return hand.size();
	}

	public int getWinsSize() {
		return wins.size();
	}

	public Card showCard() {
		Card card = hand.get(0);
		hand.remove(0);
		return card;
	}

	public void addWins(Card card) {
		wins.add(card);
	}

	@Override
	public String toString() {
		return this.name;
	}

}
