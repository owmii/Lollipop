package owmii.lib.util;

import net.minecraft.nbt.CompoundNBT;

public class Ticker {

    private double max;
    private double ticks;

    public Ticker(double max) {
        this.max = max;
    }

    public static Ticker empty() {
        return new Ticker(0);
    }

    public boolean isEmpty() {
        return this.ticks <= 0;
    }

    public boolean ended() {
        return this.ticks >= this.max;
    }

    public void add(double ticks) {
        this.ticks = Math.min(Math.max(0, this.ticks + ticks), this.max);
    }

    public void onward() {
        if (this.ticks < this.max) {
            this.ticks++;
        }
    }

    public void back() {
        if (this.ticks > 0) {
            this.ticks--;
        }
    }

    public void back(double value) {
        if (this.ticks > 0) {
            this.ticks -= Math.min(this.ticks, value);
        }
    }

    public void reset() {
        this.ticks = 0;
    }

    public static boolean delayed(double delay) {
        return System.currentTimeMillis() % (delay * 5) == 0;
    }

    public void read(CompoundNBT compound, String key) {
        this.ticks = compound.getDouble(key + "Ticks");
        this.max = compound.getDouble(key + "Max");
    }

    public void write(CompoundNBT compound, String key) {
        compound.putDouble(key + "Ticks", this.ticks);
        compound.putDouble(key + "Max", this.max);
    }

    public double getMax() {
        return this.max;
    }

    public void setMax(double max) {
        this.max = max;
    }

    public double getTicks() {
        return this.ticks;
    }

    public void setTicks(double ticks) {
        this.ticks = ticks;
    }

    public void setAll(double ticks) {
        this.max = ticks;
        this.ticks = ticks;
    }
}
