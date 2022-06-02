package de.goldendeveloper.twitcher.discord;

import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import de.goldendeveloper.twitcher.Main;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

public class Discord {

    private JDA bot;
    private final Color EmbedColor;

    public static String cmdSettings = "settings";
    public static String cmdSettingsSubRole = "twitch-info-role";
    public static String cmdSettingsSubTwitchChannel = "twitch-info-channel";
    public static String cmdSettingsSubTwitchChannelOptionName = "channelname";
    public static String cmdSettingsSubChannel = "discord-info-channel";
    public static String cmdSettingsSubChannelOptionChannel = "channel";
    public static String cmdSettingsSubRoleOptionRole = "role";

    public static String cmdHelp = "help";
    public static String cmdRestart = "restart";
    public static String cmdShutdown = "shutdown";

    public Discord(String Token) {
        try {
            bot = JDABuilder.createDefault(Token)
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
                    .addEventListeners(new Events(), this)
                    .setAutoReconnect(true)
                    .build().awaitReady();
            registerCommands();
            if (Main.getDeployment()) {
                Online();
            }
            bot.getPresence().setActivity(Activity.playing("/help | " + bot.getGuilds().size() + " Servern"));
        } catch (LoginException | InterruptedException e) {
            e.printStackTrace();
        }
        this.EmbedColor = new Color(100, 65, 164);
    }

    public JDA getBot() {
        return bot;
    }

    private void registerCommands() {
        bot.upsertCommand(cmdSettings, "Stellt den " + bot.getSelfUser().getName() + " ein!")
                .addSubcommands(
                        new SubcommandData(cmdSettingsSubChannel, "Setzte den Info Channel für deine Twitch Live Streams").addOption(OptionType.CHANNEL, cmdSettingsSubChannelOptionChannel, "Twitch Benachrichtigung Channel", true),
                        new SubcommandData(cmdSettingsSubTwitchChannel, "Speichert deinen Twitch Channel Namen").addOption(OptionType.STRING, cmdSettingsSubTwitchChannelOptionName, "Twitch Channel Name", true),
                        new SubcommandData(cmdSettingsSubRole, "Setzt die Rolle für die Stream Benachrichtigung").addOption(OptionType.ROLE, cmdSettingsSubRoleOptionRole, "Twitch Info Rolle", true)
                ).queue();
    }

    public Color getEmbedColor() {
        return EmbedColor;
    }

    private void Online() {
        WebhookEmbedBuilder embed = new WebhookEmbedBuilder();
        if (Main.getRestart()) {
            embed.setColor(0x33FFFF);
            embed.addField(new WebhookEmbed.EmbedField(false, "[Status]", "Neustart Erfolgreich"));
        } else {
            embed.setColor(0x00FF00);
            embed.addField(new WebhookEmbed.EmbedField(false, "[Status]", "ONLINE"));
        }
        embed.setAuthor(new WebhookEmbed.EmbedAuthor(getBot().getSelfUser().getName(), getBot().getSelfUser().getAvatarUrl(), "https://Golden-Developer.de"));
        embed.addField(new WebhookEmbed.EmbedField(false, "Gestartet als", getBot().getSelfUser().getName()));
        embed.addField(new WebhookEmbed.EmbedField(false, "Server", Integer.toString(getBot().getGuilds().size())));
        embed.addField(new WebhookEmbed.EmbedField(false, "Status", "\uD83D\uDFE2 Gestartet"));
        embed.addField(new WebhookEmbed.EmbedField(false, "Version", getProjektVersion()));
        embed.setFooter(new WebhookEmbed.EmbedFooter("@Golden-Developer", getBot().getSelfUser().getAvatarUrl()));
        embed.setTimestamp(new Date().toInstant());
        new WebhookClientBuilder(Main.getConfig().getDiscordWebhook()).build().send(embed.build());
    }

    public String getProjektVersion() {
        Properties properties = new Properties();
        try {
            properties.load(this.getClass().getClassLoader().getResourceAsStream("project.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return properties.getProperty("version");
    }

    public String getProjektName() {
        Properties properties = new Properties();
        try {
            properties.load(this.getClass().getClassLoader().getResourceAsStream("project.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return properties.getProperty("name");
    }
}
