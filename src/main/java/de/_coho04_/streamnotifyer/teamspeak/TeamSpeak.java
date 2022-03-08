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

    //TODO: MYSQL: COUNTVIEWERCHANNEL
}
