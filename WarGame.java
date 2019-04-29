package warGame;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.Scanner;

public class WarGame implements Serializable {
	private static final long serialVersionUID = -3434509612974396141L;
	//フィールド
	private Player you;
	private Player cpu;
	private Cards cards;
	private Field field;
	private Card yourCard;
	private Card cpuCard;
	private int round;

	//定数
	final static String YES = "y";
	final static String NO = "n";
	final static String DRAW = "d";
	final static String QUIT = "q";
	final static int WIN = 1;
	final static int DRAW_TO_MATCH = 0;
	final static int LOSE = -1;
	final static int NUM_OF_MATCHES = 0;//対戦回数
	final static int NUM_OF_WINS = 1;//勝利回数
	final static int MAX_NUM_OF_WINNING = 2;//最大獲得枚数

	public WarGame() {
		cards = new Cards();
		you = new Player("あなた");
		cpu = new Player("CPU");
		field = new Field();
		round = 1;
		//プレイヤとCPUにカードを配る
		cards.halve(you, cpu);
	}

	public void start(Scanner scanner) {
		String select;
		do {
			//場に積まれた枚数、お互いの持ち札の枚数を表示
			dispStateOfRound();
			switch (select = youSelect(scanner,"札を切りますか? (d:札を切る, q:中断) >", DRAW, QUIT)) {

			case DRAW:
				//プレイヤとCPUはそれぞれ、持ち札からカードを1枚出す
				System.out.println(String.format("%sが切った札:[%s]", cpu.toString(), cpuCard = cpu.showCard()));
				System.out.println(String.format("%sが切った札:[%s]", you.toString(), yourCard = you.showCard()));

				//カードの大小を比較する
				switch (yourCard.compare(cpuCard)) {
				case WIN:
					cardToWinner(you);
					break;
				case LOSE:
					cardToWinner(cpu);
					break;
				case DRAW_TO_MATCH:
					System.out.println("引き分けです。");
					showCardTo(field);
					break;
				}
				round++;
				System.out.println();
				break;

			case QUIT:
				System.out.println("ゲームを中断します。");
				backup();
				break;
			}
		} while (you.getHandSize() != 0 && cpu.getHandSize() != 0 && !select.equals(QUIT));

		if (you.getHandSize() == 0 || cpu.getHandSize() == 0) {
			//フィールドに札が残っている場合
			if (field.getSize() != 0) {
				field.clear();
			}
			//最終結果を表示
			dispResult();
			//結果をcsvファイルに記録
			record();
			//中断データを削除
			deleteBackup();
		}
	}

	public void dispStateOfRound() {
		System.out.println(String.format("### 第%d回戦 ###", round));
		System.out.println(String.format("場に積まれた札:%d枚", field.getSize()));
		System.out.println(String.format("%sの持ち札:%d枚,獲得した札:%d枚", cpu, cpu.getHandSize(), cpu.getWinsSize()));
		System.out.println(String.format("%sの持ち札:%d枚,獲得した札:%d枚", you, you.getHandSize(), you.getWinsSize()));
	}

	public int yourResult() {
		if(you.getWinsSize() > cpu.getWinsSize()) {
			return WIN;
		}else if(you.getWinsSize() < cpu.getWinsSize()) {
			return LOSE;
		}else {
			return DRAW_TO_MATCH;
		}
	}

	public void dispResult() {
		System.out.println("### 最終結果 ###");
		System.out.println(String.format("%sが獲得した札:%d枚", cpu, cpu.getWinsSize()));
		System.out.println(String.format("%sが獲得した札:%d枚", you, you.getWinsSize()));
		switch(yourResult()){
			case WIN:
				System.out.println(String.format("%sが勝利しました!!", you));
			case LOSE:
				System.out.println(String.format("%sが勝利しました!!", cpu));
			case DRAW_TO_MATCH:
				System.out.println("勝負は引き分けです!!");
		}
	}

	public String youSelect(Scanner scanner,String msg, String A, String B) {
		System.out.println(msg);
		while (true) {
			String select = scanner.next();
			if (select.equals(A) || select.equals(B)) {
				return select;
			} else {
				System.out.println(String.format("半角英字%sまたは%sのみ入力できます。", A, B));
				continue;
			}
		}
	}

	public void showCardTo(Field f) {
		field.add(cpuCard);
		field.add(yourCard);
		cpuCard = null;
		yourCard = null;
	}

	public void cardToWinner(Player p) {
		System.out.println(String.format("%sが札を獲得しました。", p));
		p.addWins(cpuCard);
		p.addWins(yourCard);
		field.to(p);
		cpuCard = null;
		yourCard = null;
	}

	public void backup() {
		String path = "./WarGameBackup.dat";
		try (ObjectOutputStream out = new ObjectOutputStream(
				new BufferedOutputStream(new FileOutputStream(path)));) {
			out.writeObject(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void deleteBackup() {
		File f = new File("./WarGameBackup.dat");
		if(f.exists()) {
			f.delete();
		}
	}

	public WarGame askUserToContinue(Scanner scanner) {
		File f = new File("./WarGameBackup.dat");
		//中断データの存在を確認する
		if (f.exists()) {
			String select = youSelect(scanner,"中断したゲームを再開しますか? (y:続きから始める, n:最初から始める) >", YES, NO);
			switch (select) {
			case YES://続きから始める
				WarGame backup = getWarGameFromBackup();
				return backup;
			case NO://最初から始める
				return this;
			}
		} else {
			return this;
		}
		return null;
	}

	public WarGame getWarGameFromBackup() {
		String path = "./WarGameBackup.dat";
		try (ObjectInputStream in = new ObjectInputStream(
				new BufferedInputStream(new FileInputStream(path)));) {
			WarGame backup = (WarGame) in.readObject();
			return backup;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void record() {
		File f = new File("./WarGameRecord.csv");
		int[] record = new int[3];
		if (f.exists()) {
			//CSVデータの読み込み
			try {
			BufferedReader br = new BufferedReader(new FileReader(f));
			String line;
			int lineNum = 0;
			while ((line = br.readLine()) != null) {
				//1行目はヘッダーなので無視
				if (lineNum == 0) {
					lineNum++;
					continue;
				} else {
					String[] lastRecord = line.split(",");
					record[NUM_OF_MATCHES] = Integer.valueOf(lastRecord[NUM_OF_MATCHES]);
					record[NUM_OF_WINS] = Integer.valueOf(lastRecord[NUM_OF_WINS]);
					record[MAX_NUM_OF_WINNING] = Integer.valueOf(lastRecord[MAX_NUM_OF_WINNING]);
				}
			}
			br.close();
			}catch(IOException e) {
				e.printStackTrace();
			}
		}else {
			record[NUM_OF_MATCHES] = 0;
			record[NUM_OF_WINS] = 0;
			record[MAX_NUM_OF_WINNING] = 0;
		}
		//対戦回数にプラス1
		record[NUM_OF_MATCHES]++;
		//勝利していたら、勝利回数にプラス1
		if(yourResult() == WIN) {
			record[NUM_OF_WINS]++;
		}
		//今回の獲得枚数がレコードの最大獲得枚数を上回っていたら、最大獲得枚数を上書き
		if(you.getWinsSize() > record[MAX_NUM_OF_WINNING]) {
			record[MAX_NUM_OF_WINNING] = you.getWinsSize();
		}
		//CSVデータの書き込み
		try {
			FileWriter fw = new FileWriter("./WarGameRecord.csv");
			PrintWriter pw = new PrintWriter(new BufferedWriter(fw));
			pw.println("対戦回数, 勝利回数, 最大獲得枚数");
			pw.println(String.format("%d,%d,%d", record[NUM_OF_MATCHES],record[NUM_OF_WINS],record[MAX_NUM_OF_WINNING]));
			pw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//更新されたレコードを表示
		System.out.println(String.format("対戦回数:%d 勝利回数:%d 最大獲得枚数:%d", record[NUM_OF_MATCHES],record[NUM_OF_WINS],record[MAX_NUM_OF_WINNING]));
	}

}
