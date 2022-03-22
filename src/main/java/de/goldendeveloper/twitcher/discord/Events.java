package de.goldendeveloper.twitcher.discord;

import de.goldendeveloper.mysql.entities.Row;
import de.goldendeveloper.mysql.entities.Table;
import de.goldendeveloper.twitcher.Main;
import de.goldendeveloper.twitcher.mysql.CreateMysql;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.util.HashMap;

public class Events extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent e) {
        if (e.isFromGuild()) {
            String cmd = e.getName();
            if (cmd.equalsIgnoreCase(Discord.cmdSettings)) {
                String subCmd = e.getSubcommandName();
                if (subCmd != null) {
                    if (subCmd.equalsIgnoreCase(Discord.cmdSettingsSubChannel)) {
                        OptionMapping mapping = e.getOption(Discord.cmdSettingsSubRoleOptionRole);
                        if (mapping != null) {
                            Role role = mapping.getAsRole();
                            if (Main.getMysql().existsDatabase(Main.dbName)) {
                                if (Main.getMysql().getDatabase(Main.dbName).existsTable(Main.tableName)) {
                                    Table table = Main.getMysql().getDatabase(Main.dbName).getTable(Main.tableName);
                                    if (table.existsColumn(CreateMysql.colmDcServer)) {
                                        if (!table.getColumn(CreateMysql.colmDcServer).getAll().contains(e.getGuild().getId())) {
                                            table.insert(new Row(table, table.getDatabase())
                                                    .with(CreateMysql.colmDcServer, e.getGuild().getId())
                                                    .with(CreateMysql.colmDcStreamNotifyChannel, "")
                                                    .with(CreateMysql.colmDcStreamNotifyRole, role.getId())
                                                    .with(CreateMysql.colmTwitchChannel, "")
                                            );
                                            e.getInteraction().reply("Die neue Stream Info Rolle ist nun " + role.getAsMention() + "!").queue();
                                        } else {
                                            HashMap<String, Object> row = table.getRow(table.getColumn(CreateMysql.colmDcServer), e.getGuild().getId());
                                            int id = Integer.parseInt(row.get("id").toString());
                                            if (row.containsKey(CreateMysql.colmDcStreamNotifyRole) && !row.get(CreateMysql.colmDcStreamNotifyRole).toString().isEmpty()) {
                                                table.getColumn(CreateMysql.colmDcStreamNotifyRole).set(role.getId(), id);
                                                e.getInteraction().reply("Die neue Stream Info Rolle ist nun " + role.getAsMention() + "!").queue();
                                            } else {
                                                e.getInteraction().reply("Die neue Stream Info Rolle kann nicht die alte Stream Info Rolle sein!").queue();
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } else if (subCmd.equalsIgnoreCase(Discord.cmdSettingsSubRole)) {
                        OptionMapping mapping = e.getOption(Discord.cmdSettingsSubChannelOptionChannel);
                        if (mapping != null) {
                            TextChannel channel = mapping.getAsTextChannel();
                            if (channel != null) {
                                if (Main.getMysql().existsDatabase(Main.dbName)) {
                                    if (Main.getMysql().getDatabase(Main.dbName).existsTable(Main.tableName)) {
                                        Table table = Main.getMysql().getDatabase(Main.dbName).getTable(Main.tableName);
                                        if (table.existsColumn(CreateMysql.colmDcServer)) {
                                            if (!table.getColumn(CreateMysql.colmDcServer).getAll().contains(e.getGuild().getId())) {
                                                table.insert(new Row(table, table.getDatabase())
                                                        .with(CreateMysql.colmDcServer, e.getGuild().getId())
                                                        .with(CreateMysql.colmDcStreamNotifyChannel, channel.getId())
                                                        .with(CreateMysql.colmDcStreamNotifyRole, "")
                                                        .with(CreateMysql.colmTwitchChannel, "")
                                                );
                                                e.getInteraction().reply("Der neue Stream Info Channel ist nun " + channel.getAsMention() + "!").queue();
                                            } else {
                                                HashMap<String, Object> row = table.getRow(table.getColumn(CreateMysql.colmDcServer), e.getGuild().getId());
                                                int id = Integer.parseInt(row.get("id").toString());
                                                if (row.containsKey(CreateMysql.colmDcStreamNotifyChannel) && !row.get(CreateMysql.colmDcStreamNotifyChannel).toString().isEmpty()) {
                                                    table.getColumn(CreateMysql.colmDcStreamNotifyChannel).set(channel.getId(), id);
                                                    e.getInteraction().reply("Der neue Stream Info Channel ist nun " + channel.getAsMention() + "!").queue();
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
                                            table.insert(new Row(table, table.getDatabase())
                                                    .with(CreateMysql.colmDcServer, e.getGuild().getId())
                                                    .with(CreateMysql.colmDcStreamNotifyChannel, "")
                                                    .with(CreateMysql.colmDcStreamNotifyRole, "")
                                                    .with(CreateMysql.colmTwitchChannel, TwChannel)
                                            );
                                            e.getInteraction().reply("Der neue Twitch Channel ist nun " + TwChannel + "!").queue();
                                        } else {
                                            HashMap<String, Object> row = table.getRow(table.getColumn(CreateMysql.colmDcServer), e.getGuild().getId());
                                            int id = Integer.parseInt(row.get("id").toString());
                                            if (row.containsKey(CreateMysql.colmTwitchChannel) && !row.get(CreateMysql.colmTwitchChannel).toString().isEmpty()) {
                                                table.getColumn(CreateMysql.colmTwitchChannel).set(TwChannel, id);
                                                e.getInteraction().reply("Der neue Twitch Channel ist nun " + TwChannel + "!").queue();
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
            } else if (cmd.equalsIgnoreCase(Discord.cmdStart)) {
                if (Main.getMysql().existsDatabase(Main.dbName)) {
                    if (Main.getMysql().getDatabase(Main.dbName).existsTable(Main.tableName)) {
                        Table table = Main.getMysql().getDatabase(Main.dbName).getTable(Main.tableName);
                        if (table.existsColumn(CreateMysql.colmDcServer)) {
                            if (table.getColumn(CreateMysql.colmDcServer).getAll().contains(e.getGuild().getId())) {
                                HashMap<String, Object> row = table.getRow(table.getColumn(CreateMysql.colmDcServer), e.getGuild().getId());
                                if (row.containsKey(CreateMysql.colmTwitchChannel) && row.containsKey(CreateMysql.colmDcStreamNotifyRole) && row.containsKey(CreateMysql.colmDcStreamNotifyChannel) && row.containsKey(CreateMysql.colmTwitchChannel)) {
                                    String TwChannel = row.get(CreateMysql.colmTwitchChannel).toString();
                                    String DcChannel = row.get(CreateMysql.colmDcStreamNotifyChannel).toString();
                                    String DcRole = row.get(CreateMysql.colmDcStreamNotifyRole).toString();
                                    if (!TwChannel.isEmpty()) {
                                        if (!DcChannel.isEmpty()) {
                                            if (!DcRole.isEmpty()) {
                                                Main.getTwitch().addChannel(TwChannel);
                                            } else {
                                                e.getInteraction().reply("Die Stream Info Rolle fehlt bitte setzte eine mit /" + Discord.cmdSettings + " " + Discord.cmdSettingsSubRole + "!").queue();
                                            }
                                        } else {
                                            e.getInteraction().reply("Der Stream Info Channel fehlt bitte setzte einen mit /" + Discord.cmdSettings + " " + Discord.cmdSettingsSubChannel + "!").queue();
                                        }
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
    }
}
