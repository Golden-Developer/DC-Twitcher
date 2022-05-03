package de.goldendeveloper.twitcher.discord;

import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import de.goldendeveloper.mysql.entities.Row;
import de.goldendeveloper.mysql.entities.RowBuilder;
import de.goldendeveloper.mysql.entities.Table;
import de.goldendeveloper.twitcher.Main;
import de.goldendeveloper.twitcher.mysql.CreateMysql;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.ShutdownEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class Events extends ListenerAdapter {

    @Override
    public void onShutdown(@NotNull ShutdownEvent e) {
        WebhookEmbedBuilder embed = new WebhookEmbedBuilder();
        embed.setAuthor(new WebhookEmbed.EmbedAuthor(Main.getDiscord().getBot().getSelfUser().getName(), Main.getDiscord().getBot().getSelfUser().getAvatarUrl(), "https://Golden-Developer.de"));
        embed.addField(new WebhookEmbed.EmbedField(false, "[Status]", "OFFLINE"));
        embed.setColor(0xFF0000);
        embed.setFooter(new WebhookEmbed.EmbedFooter("@Golden-Developer", Main.getDiscord().getBot().getSelfUser().getAvatarUrl()));
        new WebhookClientBuilder(Main.getConfig().getDiscordWebhook()).build().send(embed.build());
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent e) {
        if (e.isFromGuild()) {
            String cmd = e.getName();
            if (cmd.equalsIgnoreCase(Discord.cmdSettings)) {
                String subCmd = e.getSubcommandName();
                if (subCmd != null) {
                    if (subCmd.equalsIgnoreCase(Discord.cmdSettingsSubRole)) {
                        OptionMapping mapping = e.getOption(Discord.cmdSettingsSubRoleOptionRole);
                        if (mapping != null) {
                            Role role = mapping.getAsRole();
                            if (Main.getMysql().existsDatabase(Main.dbName)) {
                                if (Main.getMysql().getDatabase(Main.dbName).existsTable(Main.tableName)) {
                                    Table table = Main.getMysql().getDatabase(Main.dbName).getTable(Main.tableName);
                                    if (table.existsColumn(CreateMysql.colmDcServer)) {
                                        if (!table.getColumn(CreateMysql.colmDcServer).getAll().contains(e.getGuild().getId())) {
                                            table.insert(new RowBuilder()
                                                    .with(table.getColumn(CreateMysql.colmDcServer), e.getGuild().getId())
                                                    .with(table.getColumn(CreateMysql.colmDcStreamNotifyChannel), "")
                                                    .with(table.getColumn(CreateMysql.colmDcStreamNotifyRole), role.getId())
                                                    .with(table.getColumn(CreateMysql.colmTwitchChannel), "")
                                                    .build()
                                            );
                                            isAvailable(e, "Die neue Stream Info Rolle ist nun " + role.getAsMention() + "!");
                                        } else {
                                            HashMap<String, Object> row = table.getRow(table.getColumn(CreateMysql.colmDcServer), e.getGuild().getId()).get();
                                            if (row.containsKey(CreateMysql.colmDcStreamNotifyRole) && !row.get(CreateMysql.colmDcStreamNotifyRole).toString().isEmpty()) {
                                                table.getRow(table.getColumn(CreateMysql.colmDcServer), e.getGuild().getId()).set(table.getColumn(CreateMysql.colmDcStreamNotifyRole), role.getId());
                                                isAvailable(e, "Die neue Stream Info Rolle ist nun " + role.getAsMention() + "!");
                                            } else {
                                                e.getInteraction().reply("Die neue Stream Info Rolle kann nicht die alte Stream Info Rolle sein!").queue();
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } else if (subCmd.equalsIgnoreCase(Discord.cmdSettingsSubChannel)) {
                        OptionMapping mapping = e.getOption(Discord.cmdSettingsSubChannelOptionChannel);
                        if (mapping != null) {
                            TextChannel channel = mapping.getAsTextChannel();
                            if (channel != null) {
                                if (Main.getMysql().existsDatabase(Main.dbName)) {
                                    if (Main.getMysql().getDatabase(Main.dbName).existsTable(Main.tableName)) {
                                        Table table = Main.getMysql().getDatabase(Main.dbName).getTable(Main.tableName);
                                        if (table.existsColumn(CreateMysql.colmDcServer)) {
                                            if (!table.getColumn(CreateMysql.colmDcServer).getAll().contains(e.getGuild().getId())) {
                                                table.insert(new RowBuilder()
                                                        .with(table.getColumn(CreateMysql.colmDcServer), e.getGuild().getId())
                                                        .with(table.getColumn(CreateMysql.colmDcStreamNotifyChannel), channel.getId())
                                                        .with(table.getColumn(CreateMysql.colmDcStreamNotifyRole), "")
                                                        .with(table.getColumn(CreateMysql.colmTwitchChannel), "")
                                                        .build()
                                                );
                                                isAvailable(e, "Der neue Stream Info Channel ist nun " + channel.getAsMention() + "!");
                                            } else {
                                                HashMap<String, Object> row = table.getRow(table.getColumn(CreateMysql.colmDcServer), e.getGuild().getId()).get();
                                                if (row.containsKey(CreateMysql.colmDcStreamNotifyChannel) && !row.get(CreateMysql.colmDcStreamNotifyChannel).toString().isEmpty()) {
                                                    table.getRow(table.getColumn(CreateMysql.colmDcServer), e.getGuild().getId()).set(table.getColumn(CreateMysql.colmDcStreamNotifyChannel), channel.getId());
                                                    isAvailable(e, "Der neue Stream Info Channel ist nun " + channel.getAsMention() + "!");
                                                } else {
                                                    e.getInteraction().reply("Der neue Stream Info Channel kann nicht der alte Stream Info Channel sein!").queue();
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } else if (subCmd.equalsIgnoreCase(Discord.cmdSettingsSubTwitchChannel)) {
                        OptionMapping mapping = e.getOption(Discord.cmdSettingsSubTwitchChannel);
                        if (mapping != null) {
                            String TwChannel = mapping.getAsString();
                            if (Main.getMysql().existsDatabase(Main.dbName)) {
                                if (Main.getMysql().getDatabase(Main.dbName).existsTable(Main.tableName)) {
                                    Table table = Main.getMysql().getDatabase(Main.dbName).getTable(Main.tableName);
                                    if (table.existsColumn(CreateMysql.colmDcServer)) {
                                        if (!table.getColumn(CreateMysql.colmDcServer).getAll().contains(e.getGuild().getId())) {
                                            table.insert(new RowBuilder()
                                                    .with(table.getColumn(CreateMysql.colmDcServer), e.getGuild().getId())
                                                    .with(table.getColumn(CreateMysql.colmDcStreamNotifyChannel), "")
                                                    .with(table.getColumn(CreateMysql.colmDcStreamNotifyRole), "")
                                                    .with(table.getColumn(CreateMysql.colmTwitchChannel), TwChannel)
                                                    .build()
                                            );
                                            isAvailable(e, "Der neue Twitch Channel ist nun " + TwChannel + "!");
                                        } else {
                                            HashMap<String, Object> row = table.getRow(table.getColumn(CreateMysql.colmDcServer), e.getGuild().getId()).get();
                                            if (row.containsKey(CreateMysql.colmTwitchChannel) && !row.get(CreateMysql.colmTwitchChannel).toString().isEmpty()) {
                                                table.getRow(table.getColumn(CreateMysql.colmDcServer), e.getGuild().getId()).set(table.getColumn(CreateMysql.colmTwitchChannel), TwChannel);
                                                isAvailable(e, "Der neue Twitch Channel ist nun " + TwChannel + "!");
                                            } else {
                                                e.getInteraction().reply("Der neue Twitch Channel kann nicht der alte Twitch Channel sein!").queue();
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void isAvailable(SlashCommandInteractionEvent e, @Nullable String success) {
        if (Main.getMysql().existsDatabase(Main.dbName)) {
            if (Main.getMysql().getDatabase(Main.dbName).existsTable(Main.tableName)) {
                Table table = Main.getMysql().getDatabase(Main.dbName).getTable(Main.tableName);
                if (table.existsColumn(CreateMysql.colmDcServer)) {
                    if (table.getColumn(CreateMysql.colmDcServer).getAll().contains(e.getGuild().getId())) {
                        HashMap<String, Object> row = table.getRow(table.getColumn(CreateMysql.colmDcServer), e.getGuild().getId()).get();
                        if (row.containsKey(CreateMysql.colmTwitchChannel) && row.containsKey(CreateMysql.colmDcStreamNotifyRole) && row.containsKey(CreateMysql.colmDcStreamNotifyChannel) && row.containsKey(CreateMysql.colmTwitchChannel)) {
                            String TwChannel = row.get(CreateMysql.colmTwitchChannel).toString();
                            String DcChannel = row.get(CreateMysql.colmDcStreamNotifyChannel).toString();
                            String DcRole = row.get(CreateMysql.colmDcStreamNotifyRole).toString();
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


    /*
     *  /settings discord-info-channel <TextChannel/NewsChannel>
     *  /settings twitch-info-channel <String>
     *  /settings twitch-info-role <Role>
     *
     * onTwitch
     * Twitch Channel Namens
     *
     * */
}
