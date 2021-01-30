package net.redstonecraft.redstoneapi.tools;

import java.util.Arrays;

/**
 * Version class
 *
 * @author Redstonecrafter0
 * @since 1.1
 * */
public class Version {

    private final int[] version;
    private final boolean includeV;
    private final boolean preRelease;

    public Version(String version) {
        if (version.startsWith("v")) {
            version = version.substring(1);
            includeV = true;
            preRelease = false;
        } else if (version.startsWith("pre-")) {
            version = version.substring(4);
            includeV = false;
            preRelease = true;
        } else {
            includeV = false;
            preRelease = false;
        }
        if (!version.matches("[0-9]+(\\.[0-9]+)*")) {
            throw new IllegalArgumentException("Invalid Version");
        }
        String[] nums = version.split("\\.");
        this.version = new int[nums.length];
        for (int i = 0; i < nums.length; i++) {
            this.version[i] = Integer.parseInt(nums[i]);
        }
    }

    public Version(int[] numbers) {
        version = numbers;
        includeV = false;
        preRelease = false;
    }

    public Version(int[] numbers, boolean includeV, boolean preRelease) {
        version = numbers;
        this.includeV = includeV;
        this.preRelease = preRelease;
    }

    public int getMajor() {
        return getOnPos(0) == null ? 0 : getOnPos(0);
    }

    public int getMinor() {
        return getOnPos(1) == null ? 0 : getOnPos(1);
    }

    public int getPatch() {
        return getOnPos(2) == null ? 0 : getOnPos(2);
    }

    public int getBuild() {
        return getOnPos(3) == null ? 0 : getOnPos(3);
    }

    public Integer getOnPos(int pos) {
        try {
            return version[pos];
        } catch (ArrayIndexOutOfBoundsException ignored) {
            return null;
        }
    }

    public int getDepth() {
        return version.length;
    }

    public boolean isOlderThan(Version compare) {
        for (int i = 0; i < Math.max(getDepth(), compare.getDepth()); i++) {
            if (getOnPos(i) == null || compare.getOnPos(i) == null) {
                return getOnPos(i) == null && compare.getOnPos(i) != null;
            }
            if (getOnPos(i) > compare.getOnPos(i)) {
                return false;
            } else if (getOnPos(i) < compare.getOnPos(i)) {
                return true;
            }
        }
        return false;
    }

    public boolean isNewerThan(Version compare) {
        return !isOlderThan(compare) && !equals(compare);
    }

    public boolean isPreRelease() {
        return preRelease;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Version version1 = (Version) o;
        return Arrays.equals(version, version1.version);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(version);
    }

    @Override
    public String toString() {
        String[] nums = new String[version.length];
        for (int i = 0; i < version.length; i++) {
            nums[i] = String.valueOf(version[i]);
        }
        return includeV ? "v" + String.join(".", nums) : (preRelease ? "pre-" + String.join(".", nums) : String.join(".", nums));
    }
}
