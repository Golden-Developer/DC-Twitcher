package de._coho04_.streamnotifyer.discord;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import javax.security.auth.login.LoginException;
import java.awt.*;

public class Discord {

    //Discord
    private JDA Bot;
    private final Color EmbedColor;

    public Discord(String Token) {
        try {
            Bot = JDABuilder.createDefault(Token)
                    .setChunkingFilter(ChunkingFilter.ALL)
                    .setMemberCachePolicy(MemberCachePolicy.ALL)
                    .enableCache(CacheFlag.MEMBER_OVERRIDES, CacheFlag.ROLE_TAGS, CacheFlag.EMOTE, CacheFlag.ACTIVITY, CacheFlag.CLIENT_STATUS, CacheFlag.ONLINE_STATUS)
                    .enableIntents(GatewayIntent.GUILD_MESSAGE_REACTIONS,
                            GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_EMOJIS,
                            GatewayIntent.DIRECT_MESSAGES, GatewayIntent.GUILD_PRESENCES,
                            GatewayIntent.GUILD_BANS, GatewayIntent.DIRECT_MESSAGE_REACTIONS,
                            GatewayIntent.GUILD_INVITES, GatewayIntent.DIRECT_MESSAGE_TYPING,
                            GatewayIntent.GUILD_MESSAGE_TYPING, GatewayIntent.GUILD_VOICE_STATES,
                            GatewayIntent.GUILD_WEBHOOKS, GatewayIntent.GUILD_MEMBERS,
                            GatewayIntent.GUILD_MESSAGE_TYPING)
                    .addEventListeners()
                    .setAutoReconnect(true)
                    .build();
        } catch (LoginException e) {
            e.printStackTrace();
        }
        this.EmbedColor = new Color(100, 65, 164);
    }

    public JDA getBot() {
        return Bot;
    }

    public Color getEmbedColor() {
        return EmbedColor;
    }

    public static String RoleIDReactionOne = "818069147887730709";
    public static String RoleIDReactionTwo = "818069146579107861";
    public static String RoleIDReactionThree = "854738856695955496";
    public static String MessageIDReactionOne = "882705304528236604";
    public static String MessageIDReactionTwo = "882705351730946079";
    public static String MessageIDReactionThree = "882705373583261726";
    public static String StreamNotify = "818069147887730709";
    public static String DcS = "817500165866782770";
    public static long SrStreamMod = 838085319777058826L;
    public static long StreamMod = 817662438802718781L;
    public static long Streamer = 817662233537806367L;
    public static long Member = 817663656937914369L;
    public static String StreamerString = "817662233537806367";
    public static String SrStreamModString = "838085319777058826";
    public static String StreamModString = "817662438802718781";
    public static long _Coho04_ = 513306244371447828L;
    public static String DC_EINGANGSHALLE_TEXTCHANNEL = "817500165866782772";
    public static String DC_MELDUNGEN_TEXTCHANNEL = "870739853539704863";
    public static String DC_SUPPORT_TEXTCHANNEL = "826515837174415360";
    public static long loTS = 817859069880696882L;


    //TODO: MYSQL: GUILD NOTIFYCHANNEL NOTIFYROLE
}
