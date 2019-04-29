package warGame;

import java.io.Serializable;
import java.util.ArrayList;

public class Field implements Serializable{
	private static final long serialVersionUID = -1736651147612173995L;
	private ArrayList<Card> field = new ArrayList<>();

	public int getSize() {
		return field.size();
	}

	public void to(Player p) {
		for(Card card:field) {
			p.addWins(card);
		}
		field.clear();
	}

	public void clear() {
		field.clear();
	}

	public void add(Card card) {
		field.add(card);
	}
}
