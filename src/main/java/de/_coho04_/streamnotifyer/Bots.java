package de._coho04_.streamnotifyer;

import de._coho04_.streamnotifyer.teamspeak.TeamSpeak;

public class Bots {

    private static TeamSpeak teamSpeak;

    public Bots(String TsServerIP, int TsPort, String TsQueryUsername, String TsQueryPassword, String TsBotNickname, String TwitchChannel) {
        teamSpeak = new TeamSpeak(TsServerIP, TsPort, TsQueryUsername, TsQueryPassword, TsBotNickname);
        Main.getTwitch().addChannel(TwitchChannel);
    }

    public static TeamSpeak getTeamSpeak() {
        return teamSpeak;
    }
}
