package ui;

import java.util.Date;

import exceptions.InvalidCredentialsException;
import exceptions.SignInFailureException;
import ezxcess.ReallyEzxcess;

public class Main implements MessagerToUser {

    private ReallyEzxcess ezxcess;
    private MessagerToUser printer;

    public Main() {
        this.ezxcess = new ReallyEzxcess(ReallyEzxcess.USER_AGENTS.MOZILLA);
    }

    public Main(MessagerToUser messager) {
        this();
        this.setPrinter(messager);
    }

    public void showMessageToUser(String msg) {
        getPrinter().sendMessage(msg);
    }

    public void ensureConnectedToNUS(String username, String password) {
        showMessageToUser("google is accessible <-> connected to internet");

        if (checkGoogleConnection()) {
            showMessageToUser("google is accessible");
            showMessageToUser("\u2234 connected to internet");
        } else {
            showMessageToUser("google is not accessible");
            showMessageToUser("\u2234 not connected to internet");
            try {
                showMessageToUser("ezxcess is accessible <-> connected to NUSNET via ezxcess");
                if (checkEzxcessConnection()) {
                    showMessageToUser("\u2234 connected to NUSNET via ezxcess");
                    showMessageToUser("! Signing in to NUSNET !");
                    showMessageToUser("username: " + username);
                    showMessageToUser("password: " + password.replaceAll(".*", "*"));
                    ezxcess.signInToNusNet(username, password);

                    showMessageToUser("google is accessible -> sign in successful");
                    if (checkGoogleConnection()) {
                        showMessageToUser("\u2234 successful sign in");
                    } else {
                        showMessageToUser("\u2234Unsuccessful sign in");
                    }
                } else {
                    showMessageToUser("\u2234 not connected to NUSNET via ezxcess");
                }
            } catch (InvalidCredentialsException e) {
                showMessageToUser("Invalid username and password");
            } catch (SignInFailureException e) {
                showMessageToUser("Unable to sign in to ezxcess");
            }
        }
    }

    public boolean checkGoogleConnection() {
        return ezxcess.canAccessGoogle();
    }

    public boolean checkEzxcessConnection() {
        showMessageToUser("Accessing ezxcess");
        if (ezxcess.canAccessEzxcess()) {
            showMessageToUser("ezxcess is accessible");
            return true;
        } else {
            showMessageToUser("ezxcess is not accessible");
            return false;
        }
    }

    /**
     * @return the printer
     */
    public MessagerToUser getPrinter() {
        return printer == null ? this : printer;
    }

    /**
     * @param printer the printer to set
     */
    public void setPrinter(MessagerToUser printer) {
        this.printer = printer;
    }

    @Override
    public void sendMessage(String str) {
        System.out.println(new Date() + ":  " + str);
    }

    public static void main(String[] args) {
        test();
    }

    public static void test() {
        Main main = new Main();
        //main.ensureConnectedToNUS(username, password);
        main.ensureConnectedToNUS("E0003921", "");
        System.out.println(main.checkGoogleConnection());
        //main.ezxcess.logoutOfNusNet();
    }
}
