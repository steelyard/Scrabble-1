package game;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import player.BonusSquarePlayer;
import player.HighScoreWordPlayer;
import player.Player;
import board.Board;
import dictionary.Alphabet;
import dictionary.Trie;

public class GameTests extends Game {

	private static int NR_OF_GAMES = 10;

	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();
		Alphabet.initializeAlphabet(GAME_LANGUAGE);
		Trie.initTrie("dictionary/dsso-1.52_utf8.txt");
		
		List<GameResult> results = new ArrayList<GameResult>();
		Player player1Type = null;
		Player player2Type = null;
		Board boardType;
		
		for (int i = 0; i < NR_OF_GAMES; i++) {
			
			boardType = new Board();
			
			player1Type = new BonusSquarePlayer(boardType);
			player2Type = new HighScoreWordPlayer(boardType);
			
			boolean player1StartsPlaying;
			
			if (i > (NR_OF_GAMES / 2))
				player1StartsPlaying = true;
			else
				player1StartsPlaying = false;
			
			GameResult result = new GameTests(player1StartsPlaying,
					player1Type, player2Type, boardType).play();
			results.add(result);
		}
		String player1Name = player1Type.getClass().getSimpleName();
		String player2Name = player2Type.getClass().getSimpleName();
		Date date = Calendar.getInstance().getTime();
		String currentTime = DateFormat.getTimeInstance().format(date);
		String currentDate = DateFormat.getDateInstance().format(date);
		String dateTime = currentDate + "_" + currentTime;
		String fileName = player1Type.getName() + "_" + player2Type.getName()
		+ "_" + NR_OF_GAMES + "_" + dateTime + ".txt";
		String filePath = player1Name + "_" + player2Name + "/" + NR_OF_GAMES
		+ "/";
		
		long endTime = System.currentTimeMillis();
		int runTimeSeconds = (int) ((endTime - startTime) / 1000);
		GameTests.printResultsToFile(results, fileName, filePath,
				runTimeSeconds);
	}
	/**
	 * Start a new game. Parameter deciding which player should start.
	 * 
	 * @param player1StartsPlaying
	 */
	public GameTests(boolean player1StartsPlaying, Player player1Type,
			Player player2Type, Board boardType) {
		super(player1StartsPlaying, player1Type, player2Type, boardType);
	}

	@Override
	/**
	 * Play the game. Alternate between the two players and print the board's
	 * current state between each move.
	 */
	public GameResult play() {
		Player player1 = getPlayer1();
		Player player2 = getPlayer2();
		while (!gameOver()) {
			Player player = (getTurn() < 0) ? player1 : player2;
			boolean successfulMove = playTurn(player);
			if (!successfulMove)
				incrementPass();
			else
				resetPasses();
			player.resetParameters();
			setTurn(-getTurn());
			player.addScoreToHistory(player.getScore());
		}

		player1.removeLastScoreFromHistory();
		player2.removeLastScoreFromHistory();
		
		for (Character letter : player1.getTilesOnHand()) {
			player1.removePointsFromScore(Alphabet.getLetterPoint(letter));
		}
		for (Character letter : player2.getTilesOnHand()) {
			player2.removePointsFromScore(Alphabet.getLetterPoint(letter));
		}

		player1.addScoreToHistory(player1.getScore());
		player2.addScoreToHistory(player2.getScore());
		GameResult result = new GameResult(player1, player2, isPlayer1StartsPlaying());

		return result;
	}


	public static void printResultsToFile(List<GameResult> results,
			String fileName, String filePath, int runTime) {

		String baseFilePath = "results/";
		String fullPathTofile = baseFilePath + filePath + fileName;

		int player1wins = 0;
		int player2wins = 0;
		int player1winsStartedPlaying = 0;
		int player2winsStartedPlaying = 0;
		String player1Name = results.get(0).getPlayer1Name();
		String player2Name = results.get(0).getPlayer2Name();

		int draws = 0;

		for (GameResult gameResult : results) {
			String winner = gameResult.getWinner();
			boolean player1StartedPlaying = gameResult.isPlayer1Started();
			if (winner.equals(gameResult.getPlayer1Name())) {
				player1wins = player1wins + 1;
				if (player1StartedPlaying)
					player1winsStartedPlaying = player1winsStartedPlaying + 1;
			} else if (winner.equals(gameResult.getPlayer2Name())) {
				player2wins = player2wins + 1;
				if (!player1StartedPlaying)
					player2winsStartedPlaying = player2winsStartedPlaying + 1;
			} else
				draws = draws + 1;
		}
		
		PrintWriter pw = null;
		
		try {
			pw = new PrintWriter(fullPathTofile);
			pw.write("Runtime: " + runTime + " seconds");
			pw.write("Dictionary size: " + Trie.getDictionarySize() + " words.");
			pw.write("\n\n");
			pw.write("Total wins: \n");
			if (player1wins >= player2wins)
				pw.write(player1Name + " " + player1wins + " - " + player2wins
						+ " " + player2Name);
			else if (player2wins > player1wins)
				pw.write(player2Name + " " + player2wins + " - " + player1wins
						+ " " + player1Name);
			pw.write("\n\n");
			pw.write("Draws: " + draws);
			pw.write("\n\n");
			pw.write("Wins when started playing: \n");
			if (player1winsStartedPlaying >= player2winsStartedPlaying)
				pw.write(player1Name + " " + player1winsStartedPlaying + " - "
						+ player2winsStartedPlaying + " " + player2Name);
			else if (player2wins > player1wins)
				pw.write(player2Name + " " + player2winsStartedPlaying + " - "
						+ player1winsStartedPlaying + " " + player1Name);
			pw.write("\n\n");
			for (GameResult gameResult : results) {
				pw.write(gameResult.toString() + "\n");
			}
			
			
			for (GameResult gr : results) {
				pw.write("\n\n" + player1Name + "\n");
				List<Integer> scoreHistory = gr.getPlayer1().getScoreHistory();
				for (int i = 0; i < scoreHistory.size(); i++) {
					pw.write(scoreHistory.get(i) + ", ");
				}
				pw.write("\n" + player2Name + "\n");
				scoreHistory = gr.getPlayer2().getScoreHistory();
				for (int i = 0; i < scoreHistory.size(); i++) {
					pw.write(scoreHistory.get(i) + ", ");
				}
				
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (pw != null)
				pw.close();
		}
	}
}
