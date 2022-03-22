package de.goldendeveloper.twitcher;


import de.goldendeveloper.mysql.MYSQL;
import de.goldendeveloper.twitcher.discord.Discord;
import de.goldendeveloper.twitcher.mysql.CreateMysql;
import de.goldendeveloper.twitcher.twitch.Twitch;

public class Main {

    private static Discord discord;
    private static Twitch twitch;
    private static MYSQL mysql;

    public static String dbName = "Twitcher";
    public static String tableName = "Twitcher";

    public static void main(String[] args) {
        CreateMysql.createMYSQL(ID.MysqlHostname, ID.MysqlPort, ID.MysqlUsername, ID.MysqlPassword);
        discord = new Discord(ID.DiscordBotToken);
        twitch = new Twitch();
    }

    public static void setMysql(MYSQL mysql) {
        Main.mysql = mysql;
    }

    public static Discord getDiscord() {
        return discord;
    }
    public static Twitch getTwitch() {
        return twitch;
    }
    public static MYSQL getMysql() {
        return mysql;
    }
}
