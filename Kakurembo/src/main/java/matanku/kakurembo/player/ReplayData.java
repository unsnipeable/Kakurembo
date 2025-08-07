package matanku.kakurembo.player;

import matanku.kakurembo.game.GameMap;
import org.bukkit.Location;
import org.bukkit.Material;

public record ReplayData(Location loc, Material disguise, GameMap world) {}
