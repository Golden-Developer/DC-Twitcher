package de.goldendeveloper.twitcher;

import de.goldendeveloper.twitcher.discord.Discord;
import de.goldendeveloper.twitcher.mysql.MysqlConnection;
import de.goldendeveloper.twitcher.twitch.Twitch;

public class Main {

    private static Discord discord;
    private static Twitch twitch;
    private static MysqlConnection mysqlConnection;
    private static Config config;
    private static ServerCommunicator serverCommunicator;

    private static Boolean restart = false;
    private static Boolean deployment = true;

    public static void main(String[] args) {
        if (args.length >= 1 && args[0].equalsIgnoreCase("restart")) {
            restart = true;
        }
        String device = System.getProperty("os.name").split(" ")[0];
        if (device.equalsIgnoreCase("windows") || device.equalsIgnoreCase("Mac")) {
            deployment = false;
        }
        config = new Config();
        serverCommunicator = new ServerCommunicator(getConfig().getServerHostname(), getConfig().getServerPort());
        mysqlConnection = new MysqlConnection(config.getMysqlHostname(), config.getMysqlPort(), config.getMysqlUsername(), config.getMysqlPassword());
        discord = new Discord(config.getDiscordToken());
        twitch = new Twitch();
    }

    public static Discord getDiscord() {
        return discord;
    }

    public static Twitch getTwitch() {
        return twitch;
    }

    public static MysqlConnection getMysqlConnection() {
        return mysqlConnection;
    }

    public static Boolean getRestart() {
        return restart;
    }

    public static Boolean getDeployment() {
        return deployment;
    }

    public static Config getConfig() {
        return config;
    }

    public static ServerCommunicator getServerCommunicator() {
        return serverCommunicator;
    }
}
