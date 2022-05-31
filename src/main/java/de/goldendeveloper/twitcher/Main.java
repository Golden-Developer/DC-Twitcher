package de.goldendeveloper.twitcher;

import de.goldendeveloper.twitcher.discord.Discord;
import de.goldendeveloper.twitcher.mysql.MysqlConnection;
import de.goldendeveloper.twitcher.twitch.Twitch;

public class Main {

    private static Discord discord;
    private static Twitch twitch;
    private static MysqlConnection mysqlConnection;
    private static Config config;

    public static Boolean restart = false;
    public static Boolean production = true;

    public static void main(String[] args) {
        if (args.length >= 1 && args[0].equalsIgnoreCase("restart")) {
            restart = true;
        }
        if (System.getProperty("os.name").split(" ")[0].equalsIgnoreCase("windows")) {
            production = false;
        }
        config = new Config();
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

    public static Config getConfig() {
        return config;
    }
}
