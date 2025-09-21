package matanku.kakurembo.api.util;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.World;

@Getter
public class LocationBuilder {

    private World world;
    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;

    public LocationBuilder(Location location) {
        this.world = location.getWorld();
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
        this.yaw = location.getYaw();
        this.pitch = location.getPitch();
    }

    public LocationBuilder setWorld(World world) {
        this.world = world;
        return this;
    }

    public LocationBuilder setX(double x) {
        this.x = x;
        return this;
    }

    public LocationBuilder setY(double y) {
        this.y = y;
        return this;
    }

    public LocationBuilder setZ(double z) {
        this.z = z;
        return this;
    }

    public LocationBuilder setYaw(float yaw) {
        this.yaw = yaw;
        return this;
    }

    public LocationBuilder setPitch(float pitch) {
        this.pitch = pitch;
        return this;
    }

    public Location build() {
        return new Location(world, x, y, z, yaw, pitch);
    }

}
