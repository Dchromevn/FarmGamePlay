package service.save;
import java.io.*;
import model.core.GameState;
public class GameSaveManager {
    private static final String SAVE_FOLDER = "sourcecode/service/save/saves/";
    public static final String DEFAULT_SAVE = "smartfarm_save";  
    private static final String FILE_EXTENSION = ".dat";

    public GameSaveManager() {
    	File directory = new File(SAVE_FOLDER);
    	if(!directory.exists()) {
    		directory.mkdirs();
    	}
    }
    public boolean saveExists(String filename) {
    	File file = new File(SAVE_FOLDER +filename+FILE_EXTENSION);
    	return file.exists();
    }
    public void saveGame(GameState state, String filename) throws IOException {
        String fullPath = SAVE_FOLDER + filename + FILE_EXTENSION;
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fullPath))) {
            oos.writeObject(state);
        }
    }
    public GameState loadGame(String filename) throws IOException, ClassNotFoundException {
        String fullPath = SAVE_FOLDER + filename + FILE_EXTENSION;
        File file = new File(fullPath);
        if (!file.exists()) {
            throw new FileNotFoundException("No saved file found.");
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fullPath))) {
            return (GameState) ois.readObject();
        }
    }
}