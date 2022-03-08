package de._coho04_.streamnotifyer;

import de._coho04_.streamnotifyer.discord.Discord;
import de._coho04_.streamnotifyer.teamspeak.TeamSpeak;
import de._coho04_.streamnotifyer.twitch.Twitch;
import de._coho04_.streamnotifyer.twitter.Twitter;

public class Main {

    private static Discord discord;
    private static Twitch twitch;
    private static TeamSpeak teamSpeak;
    private static Twitter twitter;

    public static void main(String[] args) {
        discord = new Discord("ODU0NjUyNDgxMjc0OTA0NTc3.YMnDJg.Cu-JLDvFbHNMhDDaWsU_s6uEweo");
        teamSpeak = new TeamSpeak("", 9987, "", "", "");
        twitch = new Twitch();
        twitter = new Twitter();
    }

    public static Twitter getTwitter() {
        return twitter;
    }

    public static Discord getDiscord() {
        return discord;
    }

    public static TeamSpeak getTeamSpeak() {
        return teamSpeak;
    }

    public static Twitch getTwitch() {
        return twitch;
    }
}
