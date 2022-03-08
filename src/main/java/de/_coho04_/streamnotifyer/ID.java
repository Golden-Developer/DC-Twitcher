package de._coho04_.streamnotifyer;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.awt.*;

public class ID {

    //Discord


    // TeamSpeak
    public static final String serverIP = "138.201.202.3";
    public static final int ServerPort = 9987;
    public static final String QueryUsername = "serveradmin";
    public static final String QueryPassword = "fKywpgEvH#8D";
    public static final String BotNickname = "Community Manager";
    public static final String CmdPrefix = "!";
    public static final int StreamInfoServerGruppe = 26;
    public static int SupportChannel = 35;
    public static int SupporterServerGruppe = 14;
    public static int StreamModServerGruppe = 13;
    public static int SupportServerGruppe = 25;
    public static final int AfkChannelID = 43;
    public static final int BotServerGruppe = 28;
    public static final int NoPokeServerGruppe = 27;
    public static final int RuheServerGruppe = 20;
    public static final int StreamRaum = 16;
    public static final String ZuschauerCounterRoom = "[cspacer][Zuschauer]: ";
    public static final String StreamStatusRoom = "[cspacer][Status]: ";
    public static final String GameRoom = "[cspacer][Game]: ";
    public static final String _Coho04_ID = "Mqx5MzCtc16e6OBcjEEp9wFyZaI=";
    public static final String ErrorGiveRang = "Du kannst dir nur alle [10 Sekunden] einen Rang geben!";
    public static final String SuccessGiveRoleOne = "Dir wurde erfolgreich der Rang [";

    public static void noSlashPermissions(SlashCommandInteractionEvent e) {
        e.getInteraction().reply("Dazu hast du keine Rechte! Wende dich bei Fragen an den Support von " + e.getGuild().getName()).queue();
    }

    public static String getError() {
        return "ERROR: Bitte Melden mit /ErrorReport";
    }

    public static void completed(SlashCommandInteractionEvent e, Boolean com, String message) {
        if (com) {
            e.getInteraction().reply(message).queue();
        } else {
            e.getInteraction().reply(getError()).queue();
        }
    }

    public static boolean addRole(String RoleID, Member member, Guild guild) {
        Role Info = Main.getDiscord().getBot().getRoleById(RoleID);
        if (Info != null) {
            guild.addRoleToMember(member, Info).complete();
            Main.getDiscord().getBot().sendPrivateMessage(member.getUser(), "Dir wurde erfolgreich der Rang " + Info.getName() + " gegeben!");
            return true;
        }
        return false;
    }

    public static boolean removeRole(String RoleID, Member member, Guild guild) {
        Role role = Main.getDiscord().getBot().getRoleById(RoleID);
        if (role != null) {
            guild.removeRoleFromMember(member, role).complete();
            Main.getDiscord().getBot().sendPrivateMessage(member.getUser(), "Dir wurde die Role " + role.getName() + " entfernt!");
            return true;
        }
        return false;
    }

    public static boolean hasRole(Member member) {
        boolean Role = false;
        for (int i = 0; i < member.getRoles().size(); i++) {
            if (RoleIDReactionOne.equals(member.getRoles().get(i).getId())) {
                Role = true;
            }
        }
        return Role;
    }

    public static boolean role(Member member) {
        boolean aktiv = true;
        for (Role role : member.getRoles()) {
         //   if (Listen.RoleNoMsgDelete.contains(role.getId())) {
          //     aktiv = false;
           // }
        }
        return aktiv;
    }
}