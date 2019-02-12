package secondlife.network.vituz.punishments;

import lombok.Getter;
import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.utilties.Color;

@Getter
public enum PunishmentType {

    BAN("permanently banned", "unbanned", "&cYour account has been suspended from the " + Vituz.getInstance().getEssentialsManager().getServerName() + " Network.\n\nTo appeal, visit " + Vituz.getInstance().getEssentialsManager().getAppealAt()),
    TEMPBAN("temporarily banned", "unbanned", "&cYour account has been temporarily suspended from the " + Vituz.getInstance().getEssentialsManager().getServerName() + " Network.\n\nTo appeal, " + Vituz.getInstance().getEssentialsManager().getAppealAt()),
    MUTE("muted", "unmuted", "&cYou are currently muted for %DURATION%."),
    IPBAN("ip banned", "unbanned", "&cYour account has been suspended from the " + Vituz.getInstance().getEssentialsManager().getServerName() + " Network.\n\nTo appeal, visit " + Vituz.getInstance().getEssentialsManager().getAppealAt()),
    BLACKLIST("blacklisted", "unblacklisted", "&cYour account has been blacklisted from the " + Vituz.getInstance().getEssentialsManager().getServerName() + " Network.\n\nThis punishment cannot be appealed.");
    
    String context;
    String undoContext;
    String message;
    
    PunishmentType(String context, String undoContext, String message) {
        this.context = context;
        this.undoContext = undoContext;
        this.message = Color.translate(message);
    }
}
