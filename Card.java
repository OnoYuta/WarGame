package warGame;

import java.io.Serializable;

public class Card implements Serializable{
	private static final long serialVersionUID = -4861822853476723114L;
	private Mark mark;
	private Num num;

	public Card(Mark mark, Num num) {
		this.mark = mark;
		this.num = num;
	}

	@Override
	public String toString() {
		return mark.getDisp() + num.getDisp();
	}

	public int compare(Card card) {
		int A = this.num.getStrength();
		int B = card.num.getStrength();
		if (A > B) {
			return 1;
		} else if (A < B) {
			return -1;
		} else {
			return 0;
		}
	}
}