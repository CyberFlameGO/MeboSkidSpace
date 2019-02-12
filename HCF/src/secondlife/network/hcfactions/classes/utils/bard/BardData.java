package secondlife.network.hcfactions.classes.utils.bard;

import org.bukkit.scheduler.BukkitTask;

public class BardData {

	public double energy_per_millisecond = 1.0D;
	public double max_energy = 100.0D;
	
	public long energyStart;
	public long buffCooldown;
	public long max_energy_millis = (long) (max_energy * 1000L);

	public BukkitTask heldTask;

	public void setBuffCooldown(long millis) {
		buffCooldown = System.currentTimeMillis() + millis;
	}

	public long getRemainingBuffDelay() {
		return buffCooldown - System.currentTimeMillis();
	}

	public void startEnergyTracking() {
		this.setEnergy(0);
	}

	public long getEnergyMillis() {
		if(energyStart == 0L) {
			return 0L;
		}

		return Math.min(max_energy_millis, (long) (energy_per_millisecond * (System.currentTimeMillis() - energyStart)));
	}

	public double getEnergy() {
		return Math.round(this.getEnergyMillis() / 100.0) / 10.0;
	}

	public void setEnergy(double energy) {
		energyStart = (long) (System.currentTimeMillis() - (1000L * energy));
	}
}
