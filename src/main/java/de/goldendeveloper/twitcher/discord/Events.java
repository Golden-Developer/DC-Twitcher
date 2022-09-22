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
import net.dv8tion.jda.api.events.ShutdownEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
            if (cmd.equalsIgnoreCase("twitch-channel")) {
                if (e.getSubcommandName().equalsIgnoreCase("add")) {
                    TextChannel DiscordChannel = e.getOption("DiscordChannel").getAsTextChannel();
                    String TwitchChannel = e.getOption("TwitchChannel").getAsString();
                    Role DiscordRole = e.getOption("DiscordRole").getAsRole();

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
                        }
                        Main.getTwitch().addChannel(TwitchChannel);
                    }


                } else if (e.getSubcommandName().equalsIgnoreCase("remove")) {
                    Table table = Main.getMysqlConnection().getMysql().getDatabase(MysqlConnection.dbName).getTable(MysqlConnection.tableName);
                    String twitchChannel = e.getOption("TwitchChannel").getAsString();
                    for (Row row : getRowsWithTwitchChannel(table, twitchChannel)) {
                        table.dropRow(row.get().get("id").getAsInt());
                    }
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

    public void isAvailable(SlashCommandInteractionEvent e, @Nullable String success) {
        if (Main.getMysqlConnection().getMysql().existsDatabase(MysqlConnection.dbName)) {
            if (Main.getMysqlConnection().getMysql().getDatabase(MysqlConnection.dbName).existsTable(MysqlConnection.tableName)) {
                Table table = Main.getMysqlConnection().getMysql().getDatabase(MysqlConnection.dbName).getTable(MysqlConnection.tableName);
                if (table.existsColumn(MysqlConnection.colmDcServer)) {
                    if (table.getColumn(MysqlConnection.colmDcServer).getAll().getAsString().contains(e.getGuild().getId())) {
                        HashMap<String, SearchResult> row = table.getRow(table.getColumn(MysqlConnection.colmDcServer), e.getGuild().getId()).get();
                        if (row.containsKey(MysqlConnection.colmTwitchChannel) && row.containsKey(MysqlConnection.colmDcStreamNotifyRole) && row.containsKey(MysqlConnection.colmDcStreamNotifyChannel) && row.containsKey(MysqlConnection.colmTwitchChannel)) {
                            String TwChannel = row.get(MysqlConnection.colmTwitchChannel).getAsString();
                            String DcChannel = row.get(MysqlConnection.colmDcStreamNotifyChannel).getAsString();
                            String DcRole = row.get(MysqlConnection.colmDcStreamNotifyRole).getAsString();
                            if (!TwChannel.isEmpty()) {
                                if (!DcChannel.isEmpty()) {
                                    if (!DcRole.isEmpty()) {
                                        Main.getTwitch().addChannel(TwChannel);
                                    } else {
                                        if (success != null) {
                                            e.getInteraction().reply(success + "\nDie Stream Info Rolle fehlt bitte setzte eine mit /" + Discord.cmdSettings + " " + Discord.cmdSettingsSubRole + "!").queue();
                                        } else {
                                            e.getInteraction().reply("Die Stream Info Rolle fehlt bitte setzte eine mit /" + Discord.cmdSettings + " " + Discord.cmdSettingsSubRole + "!").queue();
                                        }
                                    }
                                } else {
                                    if (success != null) {
                                        e.getInteraction().reply(success + "\nDer Stream Info Channel fehlt bitte setzte einen mit /" + Discord.cmdSettings + " " + Discord.cmdSettingsSubChannel + "!").queue();
                                    } else {
                                        e.getInteraction().reply("Der Stream Info Channel fehlt bitte setzte einen mit /" + Discord.cmdSettings + " " + Discord.cmdSettingsSubChannel + "!").queue();
                                    }
                                }
                            } else {
                                if (success != null) {
                                    e.getInteraction().reply(success + "\nDer Twitch Channel fehlt bitte setzte einen mit /" + Discord.cmdSettings + " " + Discord.cmdSettingsSubTwitchChannel + "!").queue();
                                } else {
                                    e.getInteraction().reply("Der Twitch Channel fehlt bitte setzte einen mit /" + Discord.cmdSettings + " " + Discord.cmdSettingsSubTwitchChannel + "!").queue();
                                }
                            }
                        }
                    }
                }
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

    public Boolean isInDatabase(TextChannel channel, Role role, String TwitchChannel, Guild guild, Table table) {
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
