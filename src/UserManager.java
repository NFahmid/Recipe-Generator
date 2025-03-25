import java.io.*;

public class UserManager {
    private static volatile UserManager instance;
    private UserDataManager userDataManager;
    private UserAuthenticator userAuthenticator;

    private UserManager() {
        userDataManager = UserDataManager.getInstance();
        userAuthenticator = UserAuthenticator.getInstance();
    }

    public static UserManager getInstance() {
        if (instance == null) {
            synchronized (UserManager.class) {
                if (instance == null) {
                    instance = new UserManager();
                }
            }
        }
        return instance;
    }

    public boolean registerUser(String username, String password) {
        if (!userAuthenticator.validateRegistration(username, password)) {
            return false;
        }

        if (userDataManager.getUser(username) != null) {
            return false;
        }

        User newUser = new User(username, password);
        userDataManager.addUser(newUser);
        userDataManager.createUserIngredientFile(username);
        userDataManager.saveUsers();
        return true;
    }

    public boolean login(String username, String password) {
        if (userAuthenticator.authenticateUser(username, password, userDataManager.getUsers())) {
            User user = userDataManager.getUser(username);
            userDataManager.setCurrentUser(user);
            userDataManager.loadUserIngredients();
            return true;
        }
        return false;
    }

    public void logout() {
        if (userDataManager.getCurrentUser() != null) {
            userDataManager.saveUserIngredients();
            userDataManager.setCurrentUser(null);
        }
    }

    public User getCurrentUser() {
        return userDataManager.getCurrentUser();
    }
}