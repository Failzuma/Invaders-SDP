package engine;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.*;
import java.util.logging.Logger;
import engine.DrawManager.SpriteType;

import clove.Statistics; //Team Clove

/**
 * Manages files used in the application.
 * 
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 * 
 */
public final class FileManager {

	/** Singleton instance of the class. */
	private static FileManager instance;
	/** Application logger. */
	private static Logger logger;
	/** Max number of high scores. */
	private static final int MAX_SCORES = 7;

	/**
	 * private constructor.
	 */
	private FileManager() {
		logger = Core.getLogger();
	}

	/**
	 * Returns shared instance of FileManager.
	 * 
	 * @return Shared instance of FileManager.
	 */
	protected static FileManager getInstance() {
		if (instance == null)
			instance = new FileManager();
		return instance;
	}

	/**
	 * Loads sprites from disk.
	 * 
	 * @param spriteMap
	 *            Mapping of sprite type and empty boolean matrix that will
	 *            contain the image.
	 * @throws IOException
	 *             In case of loading problems.
	 */
	public void loadSprite(final Map<SpriteType, boolean[][]> spriteMap)
			throws IOException {
		InputStream inputStream = null;

		try {
			inputStream = DrawManager.class.getClassLoader()
					.getResourceAsStream("graphics");
			char c;

			// Sprite loading.
			for (Map.Entry<SpriteType, boolean[][]> sprite : spriteMap
					.entrySet()) {
				for (int i = 0; i < sprite.getValue().length; i++)
					for (int j = 0; j < sprite.getValue()[i].length; j++) {
						do
							c = (char) inputStream.read();
						while (c != '0' && c != '1');

						if (c == '1')
							sprite.getValue()[i][j] = true;
						else
							sprite.getValue()[i][j] = false;
					}
				logger.fine("Sprite " + sprite.getKey() + " loaded.");
			}
			if (inputStream != null)
				inputStream.close();
		} finally {
			if (inputStream != null)
				inputStream.close();
		}
	}

	/**
	 * Loads a font of a given size.
	 * 
	 * @param size
	 *            Point size of the font.
	 * @return New font.
	 * @throws IOException
	 *             In case of loading problems.
	 * @throws FontFormatException
	 *             In case of incorrect font format.
	 */
	public Font loadFont(final float size) throws IOException,
			FontFormatException {
		InputStream inputStream = null;
		Font font;

		try {
			inputStream = FileManager.class.getClassLoader()
					.getResourceAsStream("font.ttf");
			if (inputStream != null) { 
				font = Font.createFont(Font.TRUETYPE_FONT, inputStream).deriveFont(size);
			} else {
				// Set as default font, if inputStream is null
				System.out.println("Custom font not found, applying default font.");
				font = new Font("Serif", Font.PLAIN, (int) size); // Set as "Serif"
			}
		} finally {
			if (inputStream != null)
				inputStream.close();
		}

		return font;
	}

	/**
	 * Returns the application default scores if there is no user high scores
	 * file.
	 * 
	 * @return Default high scores.
	 * @throws IOException
	 *             In case of loading problems.
	 */

	private List<Score> loadDefaultHighScores() throws IOException {
		List<Score> highScores = new ArrayList<Score>();
		InputStream inputStream = null;
		BufferedReader reader = null;

		try {
			inputStream = FileManager.class.getClassLoader()
					.getResourceAsStream("scores");
			reader = new BufferedReader(new InputStreamReader(inputStream));

			Score highScore = null;
			String name = reader.readLine();
			String score = reader.readLine();
			String bulletsShot = reader.readLine(); //Team Clove
			String shipsDestroyed = reader.readLine(); //Team Clove
			String level = reader.readLine(); //Team Clove

			while ((name != null) && (score != null)) {
				highScore = new Score(name, Integer.parseInt(score), Integer.parseInt(bulletsShot),
						Integer.parseInt(shipsDestroyed), Integer.parseInt(level)); //Team Clove
				highScores.add(highScore);
				name = reader.readLine();
				score = reader.readLine();

				for(int i = 0; i < 3; i++){
					reader.readLine();
				}
				/*
				Team Clove
				Repeat the loop for the number of variables you want to skip
				Current Skipped Number of Variables = 3
				*/
			}
		} finally {
			if (inputStream != null)
				inputStream.close();
		}

		return highScores;
	}

	/**
	 * Loads high scores from file, and returns a sorted list of pairs score -
	 * value.
	 * 
	 * @return Sorted list of scores - players.
	 * @throws IOException
	 *             In case of loading problems.
	 */

	public List<Score> loadHighScores() throws IOException {

		List<Score> highScores = new ArrayList<Score>();
		InputStream inputStream = null;
		BufferedReader bufferedReader = null;

		try {
			String jarPath = FileManager.class.getProtectionDomain()
					.getCodeSource().getLocation().getPath();
			jarPath = URLDecoder.decode(jarPath, "UTF-8");

			String scoresPath = new File(jarPath).getParent();
			scoresPath += File.separator;
			scoresPath += "scores";

			File scoresFile = new File(scoresPath);
			inputStream = new FileInputStream(scoresFile);
			bufferedReader = new BufferedReader(new InputStreamReader(
					inputStream, Charset.forName("UTF-8")));

			logger.info("Loading user high scores.");

			Score highScore = null;
			String name = bufferedReader.readLine();
			String score = bufferedReader.readLine();
			String bulletsShot = bufferedReader.readLine();
			String shipsDestroyed = bufferedReader.readLine();
			String level = bufferedReader.readLine();

			while ((name != null) && (score != null)) {
				highScore = new Score(name, Integer.parseInt(score), Integer.parseInt(bulletsShot),
						Integer.parseInt(shipsDestroyed), Integer.parseInt(level)); //Team Clove
				highScores.add(highScore);
				name = bufferedReader.readLine();
				score = bufferedReader.readLine();

				for(int i = 0; i < 3; i++){
					bufferedReader.readLine();
				}
				/*
				Team Clove
				Repeat the loop for the number of variables you want to skip
				Current Skipped Number of Variables = 3
				 */
			}

		} catch (FileNotFoundException e) {
			// loads default if there's no user scores.
			logger.info("Loading default high scores.");
			highScores = loadDefaultHighScores();
		} finally {
			if (bufferedReader != null)
				bufferedReader.close();
		}

		Collections.sort(highScores);
		return highScores;
	}

	/**
	 * Saves user high scores to disk.
	 * 
	 * @param highScores
	 *            High scores to save.
	 * @throws IOException
	 *             In case of loading problems.
	 */

	public void saveHighScores(final List<Score> highScores) throws IOException {
		OutputStream outputStream = null;
		BufferedWriter bufferedWriter = null;

		try {
			String jarPath = FileManager.class.getProtectionDomain()
					.getCodeSource().getLocation().getPath();
			jarPath = URLDecoder.decode(jarPath, "UTF-8");

			String scoresPath = new File(jarPath).getParent();
			scoresPath += File.separator;
			scoresPath += "scores";

			File scoresFile = new File(scoresPath);

			if (!scoresFile.exists())
				scoresFile.createNewFile();

			outputStream = new FileOutputStream(scoresFile);
			bufferedWriter = new BufferedWriter(new OutputStreamWriter(
					outputStream, Charset.forName("UTF-8")));

			logger.info("Saving user high scores.");

			// Saves 7 or less scores.
			int savedCount = 0;
			for (Score score : highScores) {
				if (savedCount >= MAX_SCORES)
					break;
				bufferedWriter.write(score.getName());
				bufferedWriter.newLine();
				bufferedWriter.write(Integer.toString(score.getScore()));
				bufferedWriter.newLine();
				bufferedWriter.write(Integer.toString(score.getBulletsShot())); //Team Clove
				bufferedWriter.newLine(); //Team Clove
				bufferedWriter.write(Integer.toString(score.getShipsDestroyed())); //Team Clove
				bufferedWriter.newLine(); //Team Clove
				bufferedWriter.write(Integer.toString(score.getLevel())); //Team Clove
				bufferedWriter.newLine(); //Team Clove
				savedCount++;
			}

		} finally {
			if (bufferedWriter != null)
				bufferedWriter.close();
		}
	}

	/**
	 *  save userData(Statistics) to disk
	 *
	 * @param playerStatistics
	 * 				Player's Statistics list to save.
	 * @throws IOException
	 * 				In case of saving problems.
	 *
	 */

    public void saveUserData(final List<Statistics> playerStatistics) throws IOException {
		Properties properties = new Properties();
		OutputStream outputStream = null;

        try {
            String jarPath = FileManager.class.getProtectionDomain()
                    .getCodeSource().getLocation().getPath();
            jarPath = URLDecoder.decode(jarPath, "UTF-8");

            String staticsPath = new File(jarPath).getParent();
            staticsPath += File.separator;
			staticsPath += "Statistic.properties";

            File staticsFile = new File(staticsPath);

            if (!staticsFile.exists())
				staticsFile.createNewFile();

			if(!playerStatistics.isEmpty()){
				Statistics stat = playerStatistics.get(0);
				properties.setProperty("shipsDestructionStreak", String.valueOf(stat.getShipsDestructionStreak()));
				properties.setProperty("playedGameNumber", String.valueOf(stat.getPlayedGameNumber()));
				properties.setProperty("clearAchievementNumber", String.valueOf(stat.getClearAchievementNumber()));
			}
			outputStream = new FileOutputStream(staticsFile);
			properties.store(new OutputStreamWriter(outputStream, Charset.forName("UTF-8")),
					"PlayerGameStatistics");


        } finally {
            if (outputStream != null)
                outputStream.close();
        }
    }

	/**
	 *
	 * load userData(Statistics) from file, and return userData(Statistics)
	 *
	 * @return Player's Statistics
	 * @throws IOException
	 * 				In case of loading problems.
	 */

	public Statistics loadUserData() throws IOException {
		Properties properties = new Properties();
		InputStream inputStream = null;

		Statistics stat;

		try{
			String jarPath = FileManager.class.getProtectionDomain()
					.getCodeSource().getLocation().getPath();
			jarPath = URLDecoder.decode(jarPath, "UTF-8");

			String staticsPath = new File(jarPath).getParent();
			staticsPath += File.separator;
			staticsPath += "Statistic.properties";

			File staticsFile = new File(staticsPath);

			inputStream = new FileInputStream(staticsFile);
			properties.load(new InputStreamReader(inputStream, Charset.forName("UTF-8")));

			int shipsDestructionStreak = Integer.parseInt(properties.getProperty("shipsDestructionStreak"));
			int playedGameNumber = Integer.parseInt(properties.getProperty("playedGameNumber"));
			int clearAchievementNumber = Integer.parseInt(properties.getProperty("clearAchievementNumber"));

			stat = new Statistics(shipsDestructionStreak, playedGameNumber, clearAchievementNumber);

		} catch (FileNotFoundException e){
			logger.info("Loading default user statistics.");
			stat = loadDefaultUserData();
		} finally {
			if(inputStream != null){
				inputStream.close();
			}

		}

		return stat;
	}

	/**
	 * Returns the application default userData(Statistics)
	 * if there is no Statistic.properties file.
	 *
	 *
	 * @return Default Player's Statistics
	 * @throws IOException
	 * 				In case of loading problems.
	 */

	public Statistics loadDefaultUserData() throws IOException {
		Properties properties = new Properties();
		InputStream inputStream = null;

		Statistics stat;

		try{
			inputStream = FileManager.class.getClassLoader()
					.getResourceAsStream("Statistic.properties");

			properties.load(new InputStreamReader(inputStream, Charset.forName("UTF-8")));

			int shipsDestructionStreak = Integer.parseInt(properties.getProperty("shipsDestructionStreak"));
			int playedGameNumber = Integer.parseInt(properties.getProperty("playedGameNumber"));
			int clearAchievementNumber = Integer.parseInt(properties.getProperty("clearAchievementNumber"));

			stat = new Statistics(shipsDestructionStreak, playedGameNumber, clearAchievementNumber);

		} finally {
			if(inputStream != null){
				inputStream.close();
			}
		}
		return stat;
	}

	/**
	 * Saves user currency to disk.
	 *
	 * @param currency
	 *            amount of user currency to save.
	 * @throws IOException
	 *             In case of saving problems.
	 */
	// Team-Ctrl-S(Currency)
	public void saveCurrency(final int currency) throws IOException {
		OutputStream outputStream = null;
		BufferedWriter bufferedWriter = null;

		try {
			String jarPath = FileManager.class.getProtectionDomain()
					.getCodeSource().getLocation().getPath();
			jarPath = URLDecoder.decode(jarPath, "UTF-8");

			//Choose File root
			String currencyPath = new File(jarPath).getParent();
			currencyPath += File.separator;
			currencyPath += "currency";

			File currencyFile = new File(currencyPath);
			//create File If there is no currencyFile
			if (!currencyFile.exists())
				currencyFile.createNewFile();

			outputStream = new FileOutputStream(currencyFile);
			bufferedWriter = new BufferedWriter(new OutputStreamWriter(
					outputStream, Charset.forName("UTF-8")));

			logger.info("Saving user's currency.");

			// Saves user's currency
			bufferedWriter.write(Integer.toString(currency));
			bufferedWriter.newLine();

		} finally {
			if (bufferedWriter != null)
				bufferedWriter.close();
		}
	}

	/**
	 * Loads user currency from file, and returns current currency.
	 *
	 * @return amount of current currency.
	 * @throws IOException
	 *             In case of loading problems.
	 */
	// Team-Ctrl-S(Currency)
	public int loadCurrency() throws IOException {
        int currency;
		InputStream inputStream = null;
        BufferedReader bufferedReader = null;

        try {
            String jarPath = FileManager.class.getProtectionDomain()
                    .getCodeSource().getLocation().getPath();
            jarPath = URLDecoder.decode(jarPath, "UTF-8");

            String currencyPath = new File(jarPath).getParent();
            currencyPath += File.separator;
            currencyPath += "currency";

            File currencyFile = new File(currencyPath);
            inputStream = new FileInputStream(currencyFile);
            bufferedReader = new BufferedReader(new InputStreamReader(
                    inputStream, Charset.forName("UTF-8")));

            logger.info("Loading user's currency.");

            String amount = bufferedReader.readLine();
            currency = Integer.parseInt(amount);
        }
		catch (FileNotFoundException e) {
			// loads default if there's no user currency.
			logger.info("Loading default currency.");
			currency = loadDefaultCurrency();
		}
        finally {
            if (bufferedReader != null)
                bufferedReader.close();
        }

        return currency;
    }

	/**
	 * Returns the application default currency if there is no user currency files.
	 *
	 * @return Default currency.
	 * @throws IOException
	 *             In case of loading problems.
	 */
	// Team-Ctrl-S(Currency)
	private int loadDefaultCurrency() throws IOException {
		int currency;
		InputStream inputStream = null;
		BufferedReader reader = null;

		try {
			inputStream = FileManager.class.getClassLoader()
					.getResourceAsStream("currency");
			reader = new BufferedReader(new InputStreamReader(inputStream));

			String amount = reader.readLine();
			currency = Integer.parseInt(amount);
		} finally {
			if (inputStream != null)
				inputStream.close();
		}

		return currency;
	}
}



