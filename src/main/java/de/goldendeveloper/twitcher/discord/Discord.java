package de.goldendeveloper.twitcher.discord;

import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import de.goldendeveloper.twitcher.Main;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import javax.security.auth.login.LoginException;
import java.awt.*;

public class Discord {

    private JDA Bot;
    private final Color EmbedColor;

    public static String cmdSettings = "settings";
    public static String cmdSettingsSubRole = "twitch-info-role";
    public static String cmdSettingsSubTwitchChannel = "twitch-info-channel";
    public static String cmdSettingsSubTwitchChannelOptionName = "channelname";
    public static String cmdSettingsSubChannel = "discord-info-channel";
    public static String cmdSettingsSubChannelOptionChannel = "channel";
    public static String cmdSettingsSubRoleOptionRole = "role";

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
                    .addEventListeners(new Events())
                    .setAutoReconnect(true)
                    .build().awaitReady();
            Bot.upsertCommand(cmdSettings, "Stellt den " + Bot.getSelfUser().getName() + " ein!" )
                    .addSubcommands(
                            new SubcommandData(cmdSettingsSubChannel, "Setzte den Info Channel für deine Twitch Live Streams").addOption(OptionType.CHANNEL, cmdSettingsSubChannelOptionChannel,"Twitch Benachrichtigung Channel", true),
                            new SubcommandData(cmdSettingsSubTwitchChannel, "Speichert deinen Twitch Channel Namen").addOption(OptionType.STRING, cmdSettingsSubTwitchChannelOptionName,"Twitch Channel Name", true),
                            new SubcommandData(cmdSettingsSubRole, "Setzt die Rolle für die Stream Benachrichtigung").addOption(OptionType.ROLE, cmdSettingsSubRoleOptionRole, "Twitch Info Rolle", true)
                    ).queue();
            if (!System.getProperty("os.name").split(" ")[0].equalsIgnoreCase("windows")) {
                Online();
            }
        } catch (LoginException | InterruptedException e) {
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

    private void Online() {
        WebhookEmbedBuilder embed = new WebhookEmbedBuilder();
        embed.setAuthor(new WebhookEmbed.EmbedAuthor(getBot().getSelfUser().getName(), getBot().getSelfUser().getAvatarUrl(), "https://Golden-Developer.de"));
        embed.addField(new WebhookEmbed.EmbedField(false, "[Status]", "ONLINE"));
        embed.setColor(0x00FF00);
        embed.setFooter(new WebhookEmbed.EmbedFooter("@Golden-Developer", getBot().getSelfUser().getAvatarUrl()));
        new WebhookClientBuilder(Main.getConfig().getDiscordWebhook()).build().send(embed.build());
    }
}
