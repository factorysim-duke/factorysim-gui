package edu.duke.ece651.factorysim.screen.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.ui.util.dialog.Dialogs;
import com.kotcrab.vis.ui.widget.*;
import edu.duke.ece651.factorysim.client.ServerConnectionManager;
import edu.duke.ece651.factorysim.Tuple;
import edu.duke.ece651.factorysim.screen.SettingsScreen;

/**
 * Dialog for user login.
 */
public class LoginDialog extends VisWindow {
    private VisTextField usernameField;
    private VisTextField passwordField;
    private VisTextButton loginButton;
    private VisTextButton signUpButton;
    private VisTextButton cancelButton;
    private LoginCallback callback;

    /**
     * Interface for login callback.
     */
    public interface LoginCallback {
        void onLoginSuccess(String username);
        void onLoginCancel();
    }

    /**
     * Constructor for LoginDialog.
     *
     * @param callback callback to handle login events
     */
    public LoginDialog(LoginCallback callback) {
        super("Login");
        this.callback = callback;

        // Configure window properties
        setModal(true);
        setMovable(true);
        setResizable(false);

        // Add content to the window
        setupUI();

        // Set size and position
        setSize(300, 200);
        centerWindow();

        // Add to stage
        pack();
    }

    /**
     * Set up the UI components.
     */
    private void setupUI() {
        // Create form table
        VisTable contentTable = new VisTable();
        contentTable.pad(10);

        // Username field
        contentTable.add(new VisLabel("Username:")).left().padRight(10);
        usernameField = new VisTextField();
        contentTable.add(usernameField).expandX().fillX().row();

        // Password field
        contentTable.add(new VisLabel("Password:")).left().padRight(10).padTop(10);
        passwordField = new VisTextField();
        passwordField.setPasswordMode(true);
        passwordField.setPasswordCharacter('*');
        contentTable.add(passwordField).expandX().fillX().padTop(10).row();

        // Buttons
        VisTable buttonTable = new VisTable();
        buttonTable.pad(10, 0, 0, 0);

        // Login button
        loginButton = new VisTextButton("Login", "blue");
        loginButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                attemptLogin();
            }
        });

        // Sign-up button
        signUpButton = new VisTextButton("Sign Up", "blue");
        signUpButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                attemptSignUp();
            }
        });

        // Cancel button
        cancelButton = new VisTextButton("Cancel");
        cancelButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                LoginDialog.this.cancel();
            }
        });


        buttonTable.add(cancelButton).padRight(10);
        buttonTable.add(loginButton).padRight(10);
        buttonTable.add(signUpButton);

        contentTable.add(buttonTable).colspan(2).right().padTop(20);

        add(contentTable).expand().fill();
    }

    /**
     * Attempt to log in with the provided credentials.
     */
    private void attemptLogin() {
        // Get and check username and password
        String username = usernameField.getText();
        String password = passwordField.getText();
        if (username.isEmpty() || password.isEmpty()) {
            Dialogs.showErrorDialog(getStage(), "Username and password cannot be empty.");
            return;
        }

        // Try to connect (log into) the server
        Tuple<String, Integer> hostAndPort = SettingsScreen.getStoredHostAndPort();
        String host = hostAndPort.first();
        int port = hostAndPort.second();
        try {
            ServerConnectionManager.getInstance().connect(host, port, username, password);
        } catch (Exception e) {
            Dialogs.showErrorDialog(getStage(), e.getMessage());
            return;
        }

        // Success
        callback.onLoginSuccess(username);
        close();
    }

    private void attemptSignUp() {
        // Get and check username and password
        String username = usernameField.getText();
        String password = passwordField.getText();
        if (username.isEmpty() || password.isEmpty()) {
            Dialogs.showErrorDialog(getStage(), "Username and password cannot be empty.");
            return;
        }

        // Try to sign up
        Tuple<String, Integer> hostAndPort = SettingsScreen.getStoredHostAndPort();
        String host = hostAndPort.first();
        int port = hostAndPort.second();
        try {
            ServerConnectionManager.getInstance().signup(host, port, username, password);
        } catch (Exception e) {
            Dialogs.showErrorDialog(getStage(), e.getMessage());
            return;
        }

        // Display a dialog to the user indicating success
        Dialogs.showOKDialog(getStage(), "Success", "Successfully signed up");
    }

    /**
     * Cancel the login attempt.
     */
    private void cancel() {
        callback.onLoginCancel();
        close();
    }

    /**
     * Show the login dialog on the given stage.
     *
     * @param stage the stage to show the dialog on
     * @return the dialog instance
     */
    public static LoginDialog show(Stage stage, LoginCallback callback) {
        LoginDialog dialog = new LoginDialog(callback);
        stage.addActor(dialog);
        dialog.centerWindow();
        return dialog;
    }
}
