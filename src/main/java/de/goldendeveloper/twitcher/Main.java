package de.goldendeveloper.twitcher;

import de.goldendeveloper.dcbcore.DCBot;
import de.goldendeveloper.dcbcore.DCBotBuilder;
import de.goldendeveloper.twitcher.discord.CustomEvents;
import de.goldendeveloper.twitcher.mysql.MysqlConnection;
import de.goldendeveloper.twitcher.twitch.Twitch;

public class Main {

    private static Twitch twitch;
    private static MysqlConnection mysqlConnection;
    private static CustomConfig customConfig;
    private static DCBot dcBot;

    public static void main(String[] args) {
        customConfig = new CustomConfig();
        DCBotBuilder builder = new DCBotBuilder(args, true);
//        builder.registerCommands();
        builder.registerEvents(new CustomEvents());
        dcBot = builder.build();

        mysqlConnection = new MysqlConnection(customConfig.getMysqlHostname(), customConfig.getMysqlPort(), customConfig.getMysqlUsername(), customConfig.getMysqlPassword());
        twitch = new Twitch();
    }

    public static Twitch getTwitch() {
        return twitch;
    }

    public static DCBot getDcBot() {
        return dcBot;
    }

    public static MysqlConnection getMysqlConnection() {
        return mysqlConnection;
    }

    public static CustomConfig getConfig() {
        return customConfig;
    }
}
