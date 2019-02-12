package secondlife.network.practice.handlers;

import org.bukkit.GameMode;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import secondlife.network.practice.Practice;
import secondlife.network.practice.events.PracticeEvent;
import secondlife.network.practice.events.oitc.OITCEvent;
import secondlife.network.practice.events.oitc.OITCPlayer;
import secondlife.network.practice.events.parkour.ParkourEvent;
import secondlife.network.practice.events.sumo.SumoEvent;
import secondlife.network.practice.events.sumo.SumoPlayer;
import secondlife.network.practice.match.Match;
import secondlife.network.practice.match.MatchState;
import secondlife.network.practice.player.PlayerState;
import secondlife.network.practice.player.PracticeData;
import secondlife.network.practice.utilties.CC;
import secondlife.network.vituz.utilties.Color;
import secondlife.network.vituz.utilties.Msg;

public class EntityHandler implements Listener {
	private final Practice plugin = Practice.getInstance();

	@EventHandler
	public void onEntityDamage(EntityDamageEvent e) {
		if (e.getEntity() instanceof Player) {
			Player player = (Player) e.getEntity();
			PracticeData playerData = PracticeData.getByName(player.getName());

			switch (playerData.getPlayerState()) {
				case FIGHTING:
					Match match = this.plugin.getMatchManager().getMatch(playerData);
					if (match.getMatchState() != MatchState.FIGHTING) {
						e.setCancelled(true);
					}
					if (e.getCause() == EntityDamageEvent.DamageCause.VOID) {
						if(match.getKit().isBedWars()) {
							return;
						}

						this.plugin.getMatchManager().removeFighter(player, playerData, true);
					}

					if(match.getKit().isParkour()) {
						e.setCancelled(true);
					}

					break;
				case EVENT:
					PracticeEvent event = this.plugin.getEventManager().getEventPlaying(player);

					if(event != null) {

						if(event instanceof SumoEvent) {
							SumoEvent sumoEvent = (SumoEvent) event;
							SumoPlayer sumoPlayer = sumoEvent.getPlayer(player);

							if (sumoPlayer != null && sumoPlayer.getState() == SumoPlayer.SumoState.FIGHTING) {
								e.setCancelled(false);
							}
						} else if(event instanceof OITCEvent) {
							OITCEvent oitcEvent = (OITCEvent) event;
							OITCPlayer oitcPlayer = oitcEvent.getPlayer(player);

							if (oitcPlayer != null && oitcPlayer.getState() == OITCPlayer.OITCState.FIGHTING && e.getCause() != EntityDamageEvent.DamageCause.FALL) {
								e.setCancelled(false);
							} else {
								e.setCancelled(true);
							}
						} else if(event instanceof ParkourEvent) {
							e.setCancelled(true);
						}
					}
					break;
				default:
					if (e.getCause() == EntityDamageEvent.DamageCause.VOID) {
						e.getEntity().teleport(this.plugin.getSpawnManager().getSpawnLocation().toBukkitLocation());
					}
					e.setCancelled(true);
					break;
			}
		}
	}

	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {

		if(!(e.getEntity() instanceof Player)) {
			e.setCancelled(true);
			return;
		}

		Player entity = (Player) e.getEntity();

		Player damager;

		if (e.getDamager() instanceof Player) {
			damager = (Player) e.getDamager();
		} else if (e.getDamager() instanceof Projectile) {
			damager = (Player) ((Projectile) e.getDamager()).getShooter();
		} else {
			return;
		}

		PracticeData entityData = PracticeData.getByName(entity.getName());
		PracticeData damagerData = PracticeData.getByName(damager.getName());

		if(entityData == null || damagerData == null) {
			e.setCancelled(true);
			return;
		}

		boolean isEventEntity = this.plugin.getEventManager().getEventPlaying(entity) != null;
		boolean isEventDamager = this.plugin.getEventManager().getEventPlaying(damager) != null;

		PracticeEvent eventDamager = this.plugin.getEventManager().getEventPlaying(damager);
		PracticeEvent eventEntity = this.plugin.getEventManager().getEventPlaying(entity);

		if(damagerData.getPlayerState() == PlayerState.SPECTATING || this.plugin.getEventManager().getSpectators().containsKey(damager.getUniqueId())) {
			e.setCancelled(true);
			return;
		}

		if((!entity.canSee(damager) && damager.canSee(entity)) || damager.getGameMode() == GameMode.SPECTATOR) {
			e.setCancelled(true);
			return;
		}

		if (isEventDamager && eventDamager instanceof ParkourEvent || isEventEntity &&  eventEntity instanceof ParkourEvent) {
			e.setCancelled(true);
			return;
		}

		/*if (isEventDamager && eventDamager instanceof RedroverEvent && ((RedroverEvent) eventDamager).getPlayer(damager).getState() != RedroverPlayer.RedroverState.FIGHTING || isEventEntity &&  eventDamager instanceof RedroverEvent && ((RedroverEvent) eventEntity).getPlayer(entity).getState() != RedroverPlayer.RedroverState.FIGHTING  || !isEventDamager && damagerData.getPlayerState() != PlayerState.FIGHTING || !isEventEntity && entityData.getPlayerState() != PlayerState.FIGHTING) {
			e.setCancelled(true);
			return;
		}*/

		if (isEventDamager && eventDamager instanceof SumoEvent && ((SumoEvent) eventDamager).getPlayer(damager).getState() != SumoPlayer.SumoState.FIGHTING || isEventEntity &&  eventDamager instanceof SumoEvent && ((SumoEvent) eventEntity).getPlayer(entity).getState() != SumoPlayer.SumoState.FIGHTING  || !isEventDamager && damagerData.getPlayerState() != PlayerState.FIGHTING || !isEventEntity && entityData.getPlayerState() != PlayerState.FIGHTING) {
			e.setCancelled(true);
			return;
		}

		if (isEventDamager && eventDamager instanceof OITCEvent || isEventEntity &&  eventEntity instanceof OITCEvent || !isEventDamager && damagerData.getPlayerState() != PlayerState.FIGHTING || !isEventEntity && entityData.getPlayerState() != PlayerState.FIGHTING) {

			if(isEventEntity && isEventDamager && eventEntity instanceof OITCEvent && eventDamager instanceof OITCEvent) {

				OITCEvent oitcEvent = (OITCEvent) eventDamager;
				OITCPlayer oitcKiller = oitcEvent.getPlayer(damager);
				OITCPlayer oitcPlayer = oitcEvent.getPlayer(entity);

				if(oitcKiller.getState() != OITCPlayer.OITCState.FIGHTING || oitcPlayer.getState() != OITCPlayer.OITCState.FIGHTING) {
					e.setCancelled(true);
					return;
				}

				if (e.getDamager() instanceof Arrow) {
					Arrow arrow = (Arrow) e.getDamager();

					if (arrow.getShooter() instanceof Player) {

						if(damager != entity) {
							oitcPlayer.setLastKiller(oitcKiller);
							e.setDamage(0.0D);
							eventEntity.onDeath().accept(entity);
						}
					}
				}
			}


			return;
		}

		if(entityData.getPlayerState() == PlayerState.EVENT && eventEntity instanceof SumoEvent || damagerData.getPlayerState() == PlayerState.EVENT && eventDamager instanceof SumoEvent) {
			e.setDamage(0.0D);
			return;
		}

		/*if(entityData.getPlayerState() == PlayerState.EVENT && eventEntity instanceof RedroverEvent || damagerData.getPlayerState() == PlayerState.EVENT && eventDamager instanceof RedroverEvent) {
			return;
		}*/

		Match match = this.plugin.getMatchManager().getMatch(entityData);

		if(match == null) {
			e.setDamage(0.0D);
			return;
		}

		if (damagerData.getTeamID() == entityData.getTeamID() && !match.isFFA()) {
			e.setCancelled(true);
			return;
		}

		if(match.getKit().isParkour()) {
			e.setCancelled(true);
			return;
		}

		if(match.getKit().isBedWars() && entityData.getBedwarsRespawn() > 0) {
			e.setCancelled(true);
			damager.sendMessage(Color.translate("&cThat player has respawn protection!"));
			return;
		}

		if (match.getKit().isSpleef() || match.getKit().isSumo()) {
			e.setDamage(0.0D);
		}

		if(match != null) {
			if(e.getDamager() instanceof Player) {
				damagerData.setCombo(damagerData.getCombo() + 1);
				damagerData.setHits(damagerData.getHits() + 1);

				if(damagerData.getCombo() > damagerData.getLongestCombo()) {
					damagerData.setLongestCombo(damagerData.getCombo());
				}

				entityData.setCombo(0);

				if(match.getKit().isSpleef()) {
					e.setCancelled(true);
				}
			} else if(e.getDamager() instanceof Arrow) {
				Arrow arrow = (Arrow) e.getDamager();

				if(arrow.getShooter() instanceof Player) {
					Player shooter = (Player) arrow.getShooter();

					if(!entity.getName().equals(shooter.getName())) {
						double health = Math.ceil(entity.getHealth() - e.getFinalDamage()) / 2.0D;

						if(health > 0.0D) {
							shooter.sendMessage(CC.SECONDARY + entity.getName() + CC.PRIMARY + " is now at " + CC.SECONDARY + health + Msg.HEART + CC.PRIMARY + ".");
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void onPotionSplash(PotionSplashEvent e) {
		if (!(e.getEntity().getShooter() instanceof Player)) {
			return;
		}
		for (PotionEffect effect : e.getEntity().getEffects()) {
			if (effect.getType().equals(PotionEffectType.HEAL)) {
				Player shooter = (Player) e.getEntity().getShooter();

				if (e.getIntensity(shooter) <= 0.5D) {
					PracticeData shooterData = PracticeData.getByName(shooter.getName());

					if (shooterData != null) {
						shooterData.setMissedPots(shooterData.getMissedPots() + 1);
					}
				}
				break;
			}
		}
	}
}
