package de.goldendeveloper.twitcher;

public class CustomConfig extends de.goldendeveloper.dcbcore.Config {

    public int getMysqlPort() {
        return Integer.parseInt(dotenv.get("MYSQL_PORT"));
    }

    public String getMysqlHostname() {
        return dotenv.get("MYSQL_HOSTNAME");
    }

    public String getMysqlPassword() {
        return dotenv.get("MYSQL_PASSWORD");
    }

    public String getMysqlUsername() {
        return dotenv.get("MYSQL_USERNAME");
    }

    public String getTwitchClientID() {
        return dotenv.get("TWITCH_CLIENT_ID");
    }

    public String getTwitchClientSecret() {
        return dotenv.get("TWITCH_CLIENT_SECRET");
    }

    public String getTwitchCredential() {
        return dotenv.get("TWITCH_CREDENTIAL");
    }
}