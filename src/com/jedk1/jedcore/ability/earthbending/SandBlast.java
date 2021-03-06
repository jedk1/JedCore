package com.jedk1.jedcore.ability.earthbending;

import com.jedk1.jedcore.JedCore;
import com.jedk1.jedcore.util.TempFallingBlock;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.SandAbility;
import com.projectkorra.projectkorra.util.BlockSource;
import com.projectkorra.projectkorra.util.ClickType;
import com.projectkorra.projectkorra.util.DamageHandler;
import com.projectkorra.projectkorra.util.TempBlock;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.Random;

public class SandBlast extends SandAbility implements AddonAbility {

	private long cooldown;
	private double sourcerange;
	private int range;
	private int maxBlasts;
	private static double damage;

	private Block source;
	private int blasts;
	private boolean blasting;
	private Vector direction;
	private TempBlock tempblock;

	Random rand = new Random();

	public SandBlast(Player player) {
		super(player);

		if (!bPlayer.canBend(this)) {
			return;
		}

		if (hasAbility(player, SandBlast.class)) {
			SandBlast sb = (SandBlast) getAbility(player, SandBlast.class);
			if (sb.blasting) {
				return;
			}
			sb.remove();
			return;
		}

		setFields();
		if (prepare()) {
			start();
		}
	}

	public void setFields() {
		cooldown = JedCore.plugin.getConfig().getLong("Abilities.Earth.SandBlast.Cooldown");
		sourcerange = JedCore.plugin.getConfig().getDouble("Abilities.Earth.SandBlast.SourceRange");
		range = JedCore.plugin.getConfig().getInt("Abilities.Earth.SandBlast.Range");
		maxBlasts = JedCore.plugin.getConfig().getInt("Abilities.Earth.SandBlast.MaxSandBlocks");
		damage = JedCore.plugin.getConfig().getDouble("Abilities.Earth.SandBlast.Damage");
	}

	private boolean prepare() {
		source = BlockSource.getEarthSourceBlock(player, sourcerange, ClickType.SHIFT_DOWN);
		//source = EarthMethods.getEarthSourceBlock(player, (int) sourcerange, false, true, false);
		if (source != null) {
			if (isSand(source) && source.getRelative(BlockFace.UP).getType().equals(Material.AIR)) {
				tempblock = new TempBlock(source, Material.SANDSTONE, (byte) 0);
				return true;
			}
		}
		return false;
	}

	@Override
	public void progress() {
		if (!hasAbility(player, SandBlast.class)) {
			return;
		}
		if (player.isDead() || !player.isOnline()) {
			if (tempblock != null) {
				tempblock.revertBlock();
			}
			remove();
			return;
		}
		if (player.getWorld() != source.getWorld()) {
			bPlayer.addCooldown(this);
			if (tempblock != null) {
				tempblock.revertBlock();
			}
			remove();
			return;
		}
		if (blasting) {
			if (blasts <= maxBlasts) {
				blastSand();
				blasts++;
			} else {
				if (TempFallingBlock.getFromAbility(this).isEmpty()) {
					bPlayer.addCooldown(this);
					if (tempblock != null) {
						tempblock.revertBlock();
					}
					remove();
					return;
				}
			}
		}
		return;
	}

	public static void blastSand(Player player) {
		if (hasAbility(player, SandBlast.class)) {
			SandBlast sb = (SandBlast) getAbility(player, SandBlast.class);
			if (sb.blasting) {
				return;
			}
			sb.blastSand();
		}
	}

	@SuppressWarnings("deprecation")
	private void blastSand() {
		if (!blasting) {
			blasting = true;
			direction = GeneralMethods.getDirection(source.getLocation().clone().add(0, 1, 0), GeneralMethods.getTargetedLocation(player, range)).multiply(0.07);
		}
		tempblock.revertBlock();

		//FallingBlock fblock = source.getWorld().spawnFallingBlock(source.getLocation().clone().add(0, 1, 0), source.getType(), source.getData());

		if (rand.nextInt(2) == 0) {
			playSandBendingSound(source.getLocation().add(0, 1, 0));
		}

		double x = rand.nextDouble() / 10;
		double z = rand.nextDouble() / 10;

		x = (rand.nextBoolean()) ? -x : x;
		z = (rand.nextBoolean()) ? -z : z;

		//fblock.setVelocity(direction.clone().add(new Vector(x, 0.2, z)));
		//fblock.setDropItem(false);
		//fblocks.put(fblock, player);

		new TempFallingBlock(source.getLocation().add(0, 1, 0), source.getType(), source.getData(), direction.clone().add(new Vector(x, 0.2, z)), this);

	}

	public void affect() {
		for (TempFallingBlock tfb : TempFallingBlock.getFromAbility(this)) {
			FallingBlock fblock = tfb.getFallingBlock();
			if (fblock.isDead()) {
				tfb.remove();
				continue;
			}

			if (GeneralMethods.isRegionProtectedFromBuild(player, "SandBlast", fblock.getLocation())) {
				tfb.remove();
				continue;
			}

			for (Entity entity : GeneralMethods.getEntitiesAroundPoint(fblock.getLocation(), 1.5)) {
				if (entity instanceof LivingEntity && !(entity instanceof ArmorStand)) {
					if (player.isOnline()) {
						if (entity.getEntityId() == player.getEntityId()) {
							break;
						}
					}

					if (!entity.isDead()) {
						DamageHandler.damageEntity(entity, damage, this);

						LivingEntity le = (LivingEntity) entity;
						if (le.hasPotionEffect(PotionEffectType.BLINDNESS)) {
							le.removePotionEffect(PotionEffectType.BLINDNESS);
						}

						le.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 1));
					}
				}
			}
		}
	}
	
	@Override
	public long getCooldown() {
		return cooldown;
	}

	@Override
	public Location getLocation() {
		return null;
	}

	@Override
	public String getName() {
		return "SandBlast";
	}

	@Override
	public boolean isHarmlessAbility() {
		return false;
	}

	@Override
	public boolean isSneakAbility() {
		return true;
	}

	@Override
	public String getAuthor() {
		return JedCore.dev;
	}

	@Override
	public String getVersion() {
		return JedCore.version;
	}

	@Override
	public String getDescription() {
		return "* JedCore Addon *\n" + JedCore.plugin.getConfig().getString("Abilities.Earth.SandBlast.Description");
	}

	@Override
	public void load() {
		return;
	}

	@Override
	public void stop() {
		return;
	}

	@Override
	public boolean isEnabled() {
		return JedCore.plugin.getConfig().getBoolean("Abilities.Earth.SandBlast.Enabled");
	}
}
