package warGame;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

public class Cards implements Serializable{
	private static final long serialVersionUID = -4885647418395139329L;
	private ArrayList<Card> cards = new ArrayList<>();
	public Cards() {
		cards.clear();
		for(Mark m:Mark.values()) {
			for(Num n:Num.values()) {
				cards.add(new Card(m,n));
			}
		}
		Collections.shuffle(cards);
	}

	public Card takeOut() {
		Card takeOut = cards.get(0);
		cards.remove(0);
		return takeOut;
	}

	public void halve(Player A,Player B) {
		int timesToDraw = cards.size()/2;
		for(int i = 0;i < timesToDraw;i++) {
			A.draw(this);
			B.draw(this);
		}
	}

	public int size() {
		return cards.size();
	}

}
