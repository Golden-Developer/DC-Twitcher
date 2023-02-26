package de.goldendeveloper.twitcher.discord;

import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import de.goldendeveloper.twitcher.Main;
import io.sentry.Sentry;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import java.awt.*;
import java.util.Date;

public class Discord {

    private JDA bot;
    private final Color EmbedColor;

    public static String cmdSettings = "settings";
    public static String cmdSettingsSubTwitchChannel = "twitch-info-channel";
    public static String cmdSettingsSubTwitchChannelOptionAction = "action";
    public static String DiscordChannel = "discord-channel";
    public static String DiscordRole = "discord-role";
    public static String TwitchChannel = "twitch-channel";
    public static String cmdTwitchChannel = "twitch-channel";
    public static String cmdTwitchChannelRemove = "remove";
    public static String cmdTwitchChannelAdd = "add";

    public static String cmdHelp = "help";
    public static String cmdRestart = "restart";
    public static String cmdShutdown = "shutdown";

    public Discord(String Token) {
        try {
            bot = JDABuilder.createDefault(Token)
                    .setChunkingFilter(ChunkingFilter.ALL)
                    .setMemberCachePolicy(MemberCachePolicy.ALL)
                    .enableCache(CacheFlag.MEMBER_OVERRIDES, CacheFlag.ROLE_TAGS, CacheFlag.STICKER, CacheFlag.ACTIVITY, CacheFlag.CLIENT_STATUS, CacheFlag.ONLINE_STATUS)
                    .enableIntents(GatewayIntent.GUILD_MESSAGE_REACTIONS,
                            GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_EMOJIS_AND_STICKERS,
                            GatewayIntent.DIRECT_MESSAGES, GatewayIntent.GUILD_PRESENCES,
                            GatewayIntent.GUILD_MODERATION, GatewayIntent.DIRECT_MESSAGE_REACTIONS,
                            GatewayIntent.GUILD_INVITES, GatewayIntent.DIRECT_MESSAGE_TYPING,
                            GatewayIntent.GUILD_MESSAGE_TYPING, GatewayIntent.GUILD_VOICE_STATES,
                            GatewayIntent.GUILD_WEBHOOKS, GatewayIntent.GUILD_MEMBERS,
                            GatewayIntent.GUILD_MESSAGE_TYPING)
                    .addEventListeners(new Events())
                    .setContextEnabled(true)
                    .setAutoReconnect(true)
                    .build().awaitReady();
            registerCommands();
            if (Main.getDeployment()) {
                Main.getServerCommunicator().startBot(bot);
                Online();
            }
            bot.getPresence().setActivity(Activity.playing("/help | " + bot.getGuilds().size() + " Servern"));
        } catch (Exception e) {
            Sentry.captureException(e);
            e.printStackTrace();
        }
        this.EmbedColor = new Color(100, 65, 164);
        System.out.println("[" + Main.getConfig().getProjektName() + "]: Discord gestartet!");
    }

    public void registerCommands() {
        bot.upsertCommand(cmdTwitchChannel, "Füge die Benachrichtigung eines Twitch Kanals dem Discord Server hinzu!")
                .addSubcommands(
                        new SubcommandData(cmdTwitchChannelAdd, "Setzte den Info Channel für deine Twitch Live Streams")
                                .addOption(OptionType.CHANNEL, DiscordChannel, "Hier bitte den Discord Benachrichtigung´s Channel angeben!", true)
                                .addOption(OptionType.ROLE, DiscordRole, "Hier bitte die Discord Benachrichtigung´s Rolle angeben!", true)
                                .addOption(OptionType.STRING, TwitchChannel, "Hier bitte den Twitch Benachrichtigung´s Channel angeben!", true),

                        new SubcommandData(cmdTwitchChannelRemove, "Entferne einen Twitch Channel von deinem Discord Server!")
                                .addOption(OptionType.STRING, TwitchChannel, "Hier bitte den Twitch Benachrichtigung´s Channel angeben!", true)
                ).setGuildOnly(true).queue();
        bot.upsertCommand(cmdShutdown, "Fährt den Discord Bot herunter!").queue();
        bot.upsertCommand(cmdRestart, "Startet den Discord Bot neu!").queue();
        bot.upsertCommand(cmdHelp, "Zeigt dir eine Liste möglicher Befehle an!").queue();
    }

    public JDA getBot() {
        return bot;
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
        embed.addField(new WebhookEmbed.EmbedField(false, "Version", Main.getConfig().getProjektVersion()));
        embed.setFooter(new WebhookEmbed.EmbedFooter("@Golden-Developer", getBot().getSelfUser().getAvatarUrl()));
        embed.setTimestamp(new Date().toInstant());
        new WebhookClientBuilder(Main.getConfig().getDiscordWebhook()).build().send(embed.build());
    }
}
