package secondlife.network.hcfactions.factions.utils.struction;

public interface Raidable {

    boolean isRaidable();

    double getDeathsUntilRaidable();

    double getMaximumDeathsUntilRaidable();

    double setDeathsUntilRaidable(double deathsUntilRaidable);

    long getRemainingRegenerationTime();

    void setRemainingRegenerationTime(long millis);

    RegenStatus getRegenStatus();
}
