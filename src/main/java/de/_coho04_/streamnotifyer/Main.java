package de._coho04_.streamnotifyer;

import de._Coho04_.mysql.MYSQL;
import de._coho04_.streamnotifyer.discord.Discord;
import de._coho04_.streamnotifyer.mysql.CreateMysql;
import de._coho04_.streamnotifyer.twitch.Twitch;
import de._coho04_.streamnotifyer.twitter.Twitter;

public class Main {

    private static Discord discord;
    private static Twitch twitch;
    private static Twitter twitter;
    private static MYSQL mysql;

    public static String dbName = "StreamNotifyer";
    public static String tableName = "StreamNotifyer";

    public static void main(String[] args) {
        CreateMysql.createMYSQL("", 3306, "", "");
        discord = new Discord("ODU0NjUyNDgxMjc0OTA0NTc3.YMnDJg.Cu-JLDvFbHNMhDDaWsU_s6uEweo");
        twitch = new Twitch();
        twitter = new Twitter();
    }

    public static void setMysql(MYSQL mysql) {
        Main.mysql = mysql;
    }

    public static Twitter getTwitter() {
        return twitter;
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
