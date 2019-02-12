package secondlife.network.hcfactions.factions.utils.struction;

import lombok.Getter;

@Getter
public enum Role {

	LEADER("Leader", "***"), COLEADER("Co-Leader", "**"), CAPTAIN("Captain", "*"), MEMBER("Member", "");

    private String name;
    private String astrix;

    Role(String name, String astrix) {
        this.name = name;
        this.astrix = astrix;
    }
}
