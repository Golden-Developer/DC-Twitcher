package de._coho04_.streamnotifyer.teamspeak;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.TS3Config;
import com.github.theholywaffle.teamspeak3.TS3Query;
import com.github.theholywaffle.teamspeak3.api.exception.TS3ConnectionFailedException;

public class TeamSpeak {

    //Teamspeak
    private static TS3Config config = new TS3Config();
    private static TS3Query query = new TS3Query(config);
    private static TS3Api api = query.getApi();

    private int StreamInfoServerGruppe; // = 26
    private int BotServerGruppe; // = 28
    private String ZuschauerCounterChannel; //[cspacer][Zuschauer]:
    private String StreamStatusChannel; //[cspacer][Status]:
    private String GameChannel; //[cspacer][Game]:

    public TeamSpeak(String ServerIP, int ServerPort, String QueryUsername, String QueryPassword, String BotNickname) {
        try {
            config = new TS3Config();
            config.setHost(ServerIP);
            config.setFloodRate(TS3Query.FloodRate.UNLIMITED);
            query = new TS3Query(config);
            query.connect();
            api = query.getApi();
            api.login(QueryUsername, QueryPassword);
            api.selectVirtualServerByPort(ServerPort);
            api.setNickname(BotNickname);
        } catch (TS3ConnectionFailedException e) {
            e.printStackTrace();
        }
    }

    public TS3Api getBot() {
        return api;
    }

    public void setGameChannel(String gameChannel) {
        GameChannel = gameChannel;
    }

    public String getGameChannel() {
        return GameChannel;
    }

    public void setStreamStatusChannel(String streamStatusChannel) {
        StreamStatusChannel = streamStatusChannel;
    }

    public String getStreamStatusChannel() {
        return StreamStatusChannel;
    }

    public void setZuschauerCounterChannel(String zuschauerCounterChannel) {
        ZuschauerCounterChannel = zuschauerCounterChannel;
    }

    public String getZuschauerCounterChannel() {
        return ZuschauerCounterChannel;
    }

    public void setBotServerGruppe(int botServerGruppe) {
        BotServerGruppe = botServerGruppe;
    }

    public int getBotServerGruppe() {
        return BotServerGruppe;
    }

    public void setStreamInfoServerGruppe(int streamInfoServerGruppe) {
        StreamInfoServerGruppe = streamInfoServerGruppe;
    }

    public int getStreamInfoServerGruppe() {
        return StreamInfoServerGruppe;
    }

    //TODO: MYSQL:
    // CountViewerChannelName CountViewerChannelID
    // StreamInfoServerGruppeID BotServerGruppeID
    // ZuschauerCounterChannelName ZuschauerCounterChannelID
    // StreamStatusChannelName ZuschauerCounterChannelId
    // GameChannelName GameChannelID
}
