package org.amerlan.randomTP.commands;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Random;

public class RTP implements CommandExecutor {
    private int minX, maxX, minZ, maxZ;
    private final int cooldown;
    private long cooldownExpires = System.currentTimeMillis();

    public RTP(int minX, int maxX, int minZ, int maxZ, int cooldown) {
        this.minX = minX + 1;
        this.maxX = maxX - 1;
        this.minZ = minZ + 1;
        this.maxZ = maxZ - 1;
        this.cooldown = cooldown * 1000;

        // make sure bounds are valid
        if (this.minX >= this.maxX) {
            this.maxX = this.minX + 1;
        }
        if (this.minZ >= this.maxZ) {
            this.maxZ = this.minZ + 1;
        }
    }

    // Generate a random location within bounds, that is not in water or lava
    private Location getRandomLocation(World world, Player player, int recursionDepth) {
        // No suitable location was found within the bounds
        if (recursionDepth > 5) {
            player.sendMessage("No suitable location found... teleporting to spawn.");
            return world.getSpawnLocation();
        }

        Random rand = new Random();
        double locX = rand.nextInt(this.minX, this.maxX) + 0.5;
        double locZ = rand.nextInt(this.minZ, this.maxZ) + 0.5;


        double locY = world.getHighestBlockYAt(Location.locToBlock(locX), Location.locToBlock(locZ));
        Location loc = new Location(world, locX, locY, locZ);

        // Check if random location is water or lava
        if (loc.getBlock().getType().equals(Material.WATER) || loc.getBlock().getType().equals(Material.LAVA)) {
            return getRandomLocation(world, player,recursionDepth + 1);
        }

        return loc.add(0,2, 0);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        // check that sender is not the console
        if (!(sender instanceof Player player)) {
            sender.sendMessage("You must be a player to run this command.");
            return false;
        }

        // check that player is in the overworld
        World world = player.getWorld();
        if (world.getEnvironment() != World.Environment.NORMAL) {
            sender.sendMessage("/" + cmd.getName() + " can only be used in the overworld.");
            return false;
        }

        // check that player is off cooldown
        long now = System.currentTimeMillis();
        if (now < this.cooldownExpires) {
            sender.sendMessage("You cannot use /" + cmd.getName() + " for another " + ((this.cooldownExpires - now) / 1000) + " seconds.");
            return false;
        } else {
            this.cooldownExpires = now + (this.cooldown);
        }

        // generate random location and teleport player
        Location loc = getRandomLocation(world, player, 0);
        player.teleport(loc);

        return true;
    }
}
