package warGame;

import java.io.Serializable;

public enum Mark implements Serializable{
	SPADE("スペード"),
	DIAMOND("ダイヤ");

	private String disp;
	private Mark(String disp) {
		this.disp = disp;
	}

	public String getDisp() {
		return disp;
	}
}
