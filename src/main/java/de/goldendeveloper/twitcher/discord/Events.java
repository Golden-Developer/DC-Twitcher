package de.goldendeveloper.twitcher.discord;

import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import de.goldendeveloper.mysql.entities.Row;
import de.goldendeveloper.mysql.entities.RowBuilder;
import de.goldendeveloper.mysql.entities.SearchResult;
import de.goldendeveloper.mysql.entities.Table;
import de.goldendeveloper.twitcher.Main;
import de.goldendeveloper.twitcher.mysql.MysqlConnection;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.session.ShutdownEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.*;
import java.util.List;

public class Events extends ListenerAdapter {

    @Override
    public void onShutdown(@NotNull ShutdownEvent e) {
        WebhookEmbedBuilder embed = new WebhookEmbedBuilder();
        embed.setAuthor(new WebhookEmbed.EmbedAuthor(Main.getDiscord().getBot().getSelfUser().getName(), Main.getDiscord().getBot().getSelfUser().getAvatarUrl(), "https://Golden-Developer.de"));
        embed.addField(new WebhookEmbed.EmbedField(false, "[Status]", "Offline"));
        embed.addField(new WebhookEmbed.EmbedField(false, "Gestoppt als", Main.getDiscord().getBot().getSelfUser().getName()));
        embed.addField(new WebhookEmbed.EmbedField(false, "Server", Integer.toString(Main.getDiscord().getBot().getGuilds().size())));
        embed.addField(new WebhookEmbed.EmbedField(false, "Status", "\uD83D\uDD34 Offline"));
        embed.addField(new WebhookEmbed.EmbedField(false, "Version", Main.getDiscord().getProjektVersion()));
        embed.setFooter(new WebhookEmbed.EmbedFooter("@Golden-Developer", Main.getDiscord().getBot().getSelfUser().getAvatarUrl()));
        embed.setTimestamp(new Date().toInstant());
        embed.setColor(0xFF0000);
        new WebhookClientBuilder(Main.getConfig().getDiscordWebhook()).build().send(embed.build()).thenRun(() -> {
            System.exit(0);
        });
    }

    @Override
    public void onGuildJoin(GuildJoinEvent e) {
        e.getJDA().getPresence().setActivity(Activity.playing("/help | " + e.getJDA().getGuilds().size() + " Servern"));
    }

    @Override
    public void onGuildLeave(GuildLeaveEvent e) {
        e.getJDA().getPresence().setActivity(Activity.playing("/help | " + e.getJDA().getGuilds().size() + " Servern"));
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent e) {
        String cmd = e.getName();
        User _Coho04_ = e.getJDA().getUserById("513306244371447828");
        User zRazzer = e.getJDA().getUserById("428811057700536331");
        if (e.isFromGuild()) {
            if (cmd.equalsIgnoreCase(Discord.cmdTwitchChannel)) {
                e.deferReply(true).queue();
                InteractionHook hook = e.getHook().setEphemeral(false);
                if (e.getSubcommandName().equalsIgnoreCase(Discord.cmdTwitchChannelAdd)) {
                    Channel DiscordChannel = e.getOption(Discord.DiscordChannel).getAsChannel();
                    String TwitchChannel = e.getOption(Discord.TwitchChannel).getAsString();
                    Role DiscordRole = e.getOption(Discord.DiscordRole).getAsRole();
                    if (DiscordChannel != null && DiscordRole != null && !TwitchChannel.isEmpty()) {
                        Table table = Main.getMysqlConnection().getMysql().getDatabase(MysqlConnection.dbName).getTable(MysqlConnection.tableName);
                        if (!isInDatabase(DiscordChannel, DiscordRole, TwitchChannel, e.getGuild(), table)) {
                            table.insert(
                                    new RowBuilder()
                                            .with(table.getColumn(MysqlConnection.colmDcServer), e.getGuild().getId())
                                            .with(table.getColumn(MysqlConnection.colmDcStreamNotifyRole), DiscordRole.getId())
                                            .with(table.getColumn(MysqlConnection.colmTwitchChannel), TwitchChannel)
                                            .with(table.getColumn(MysqlConnection.colmDcStreamNotifyChannel), DiscordChannel.getId())
                                            .build()
                            );
                            hook.sendMessage("Der Twitch Channel wurde erfolgreich hinzugefügt!").queue();
                        } else {
                            hook.sendMessage("Der Twitch Channel existiert bereits!").queue();
                        }
                        Main.getTwitch().addChannel(TwitchChannel);
                    } else {
                        hook.sendMessage("ERROR: Etwas ist schief gelaufen wir konnten deine Angaben nicht erfassen!").queue();
                    }
                } else if (e.getSubcommandName().equalsIgnoreCase(Discord.cmdTwitchChannelRemove)) {
                    Table table = Main.getMysqlConnection().getMysql().getDatabase(MysqlConnection.dbName).getTable(MysqlConnection.tableName);
                    String twitchChannel = e.getOption(Discord.TwitchChannel).getAsString();
                    for (Row row : getRowsWithTwitchChannel(table, twitchChannel)) {
                        table.dropRow(row.get().get("id").getAsInt());
                    }
                    hook.sendMessage("Der Twitch Channel wurde erfolgreich von dem Discord Server entfernt!").queue();
                }
            }
        }
        if (cmd.equalsIgnoreCase(Discord.cmdHelp)) {
            List<Command> commands = Main.getDiscord().getBot().retrieveCommands().complete();
            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle("**Help Commands**");
            embed.setColor(Color.MAGENTA);
            embed.setFooter("@Golden-Developer", e.getJDA().getSelfUser().getAvatarUrl());
            for (Command cm : commands) {
                embed.addField("/" + cm.getName(), cm.getDescription(), true);
            }
            e.getInteraction().replyEmbeds(embed.build()).addActionRow(
                    net.dv8tion.jda.api.interactions.components.buttons.Button.link("https://wiki.Golden-Developer.de/", "Online Übersicht"),
                    Button.link("https://support.Golden-Developer.de", "Support Anfragen")
            ).queue();
        } else if (e.getName().equalsIgnoreCase(Discord.cmdShutdown)) {
            if (e.getUser() == zRazzer || e.getUser() == _Coho04_) {
                e.getInteraction().reply("Der Bot wird nun heruntergefahren").queue();
                e.getJDA().shutdown();
            } else {
                e.getInteraction().reply("Dazu hast du keine Rechte du musst für diesen Befehl der Bot inhaber sein!").queue();
            }
        } else if (e.getName().equalsIgnoreCase(Discord.cmdRestart)) {
            if (e.getUser() == zRazzer || e.getUser() == _Coho04_) {
                try {
                    e.getInteraction().reply("Der Discord Bot wird nun neugestartet!").queue();
                    Process p = Runtime.getRuntime().exec("screen -AmdS " + Main.getDiscord().getProjektName() + " java -Xms1096M -Xmx1096M -jar " + Main.getDiscord().getProjektName() + "-" + Main.getDiscord().getProjektVersion() + ".jar restart");
                    p.waitFor();
                    e.getJDA().shutdown();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                e.getInteraction().reply("Dazu hast du keine Rechte du musst für diesen Befehl der Bot inhaber sein!").queue();
            }
        }
    }

    @Override
    public void onCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent e) {
        if (e.getName().equalsIgnoreCase(Discord.cmdSettings)) {
            if (e.getSubcommandName().equalsIgnoreCase(Discord.cmdSettingsSubTwitchChannel)) {
                if (e.getFocusedOption().getName().equalsIgnoreCase(Discord.cmdSettingsSubTwitchChannelOptionAction)) {
                    e.replyChoices(
                            new Command.Choice("hinzufügen", "add"),
                            new Command.Choice("entfernen", "remove")
                    ).queue();
                } else if (e.getFocusedOption().getName().equalsIgnoreCase(Discord.cmdSettingsSubTwitchChannelOptionAction) && e.getOption(Discord.cmdSettingsSubTwitchChannelOptionAction) != null && e.getOption(Discord.cmdSettingsSubTwitchChannelOptionAction).getAsString().equalsIgnoreCase("remove")) {
                    List<String> channels = getGuildTwitchChannel(e.getGuild());
                    List<Command.Choice> collection = new ArrayList<>();
                    for (String channel : channels) {
                        collection.add(new Command.Choice(channel, channel));
                    }
                    e.replyChoices(collection).queue();
                }
            }
        }
    }

    public List<String> getGuildTwitchChannel(Guild guild) {
        Table table = Main.getMysqlConnection().getMysql().getDatabase(MysqlConnection.dbName).getTable(MysqlConnection.tableName);
        List<String> channels = new ArrayList<>();
        for (Row row : table.getRows()) {
            HashMap<String, SearchResult> sr = row.get();
            if (sr.get(MysqlConnection.colmDcServer).getAsString().equalsIgnoreCase(guild.getId())) {
                String channel = sr.get(MysqlConnection.colmTwitchChannel).getAsString();
                channels.add(channel);
            }
        }
        return channels;
    }

    public List<Row> getRowsWithTwitchChannel(Table table, String channel) {
        List<Row> rows = new ArrayList<>();
        for (Row row : table.getRows()) {
            if (row.get().get(MysqlConnection.colmTwitchChannel).getAsString().equalsIgnoreCase(channel)) {
                rows.add(row);
            }
        }
        return rows;
    }

    public Boolean isInDatabase(Channel channel, Role role, String TwitchChannel, Guild guild, Table table) {
        for (Row r : table.getRows()) {
            HashMap<String, SearchResult> sr = r.get();
            boolean channelExists = sr.get(MysqlConnection.colmDcServer).getAsString().equalsIgnoreCase(guild.getId());
            boolean roleExists = sr.get(MysqlConnection.colmDcStreamNotifyChannel).getAsString().equalsIgnoreCase(channel.getId());
            boolean twitchExists = sr.get(MysqlConnection.colmDcStreamNotifyRole).getAsString().equalsIgnoreCase(role.getId());
            boolean guildExists = sr.get(MysqlConnection.colmTwitchChannel).getAsString().equalsIgnoreCase(TwitchChannel);
            if (channelExists && roleExists && twitchExists && guildExists) {
                return true;
            }
        }
        return false;
    }
}
