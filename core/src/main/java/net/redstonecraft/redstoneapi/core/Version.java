package net.redstonecraft.redstoneapi.core;

import java.util.Arrays;

/**
 * Version class using <a href="https://semver.org/">SemVer version naming convention</a>
 *
 * @author Redstonecrafter0
 * @since 1.1
 * */
public class Version implements Comparable<Version> {

    private final int[] version;
    private final String preRelease;
    private final String buildData;

    public Version(int major, int minor, int patch, String preRelease, String buildData) throws IllegalArgumentException {
        if (major < 0 || minor < 0 || patch < 0) {
            throw new IllegalArgumentException("Version numbers are not allowed to be negative");
        }
        if (!preRelease.matches("[0-9A-Za-z-.]")) {
            throw new IllegalArgumentException("Version preRelease data must only contain ASCII alphanumerics, hyphens and dots as separators");
        }
        if (!buildData.matches("[0-9A-Za-z-.]")) {
            throw new IllegalArgumentException("Version preRelease data must only contain ASCII alphanumerics, hyphens and dots as separators");
        }
        version = new int[]{major, minor, patch};
        this.preRelease = preRelease;
        this.buildData = buildData;
    }

    public static Version fromVersionString(String version) throws IllegalArgumentException {
        if (version.startsWith("v")) {
            version = version.substring(1);
        }
        String main;
        String pre = "";
        String build = "";
        if (version.contains("-")) {
            if (version.contains("+")) {
                String[] tmp = version.split("-");
                String[] tmp1 = tmp[1].split("\\+");
                main = tmp[0];
                pre = tmp1[0];
                build = tmp1[1];
            } else {
                String[] tmp = version.split("-");
                main = tmp[0];
                pre = tmp[1];
            }
        } else if (version.contains("+")) {
            String[] tmp = version.split("\\+");
            main = tmp[0];
            build = tmp[1];
        } else {
            main = version;
        }
        if (!main.matches("[0-9]+(\\.[0-9]+){0,2}")) {
            throw new IllegalArgumentException("Invalid Version");
        }
        String[] nums = main.split("\\.");
        int[] n = ArrayUtils.arrayOf(0, 3);
        for (int i = 0; i < nums.length; i++) {
            n[i] = Integer.parseInt(nums[i]);
        }
        return new Version(n[0], n[1], n[2], pre, build);
    }

    public int getMajor() {
        return version[0];
    }

    public int getMinor() {
        return version[1];
    }

    public int getPatch() {
        return version[2];
    }

    public boolean isPrerelease() {
        return !preRelease.equals("");
    }

    public boolean isAlpha() {
        return preRelease.contains("alpha");
    }

    public boolean isBeta() {
        return preRelease.contains("beta");
    }

    public boolean isReleaseCandidate() {
        return preRelease.contains("rc");
    }

    public boolean hasBuildData() {
        return !buildData.equals("");
    }

    public boolean isOlderThan(Version compare) {
        for (int i = 0; i < version.length; i++) {
            if (version[i] < compare.version[i]) {
                return true;
            }
        }
        if (!isPrerelease() && compare.isPrerelease()) {
            return false;
        }
        // TODO: https://semver.org/
        return false;
    }

    public boolean isNewerThan(Version compare) {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        return compareTo((Version) o) == 0;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(version);
    }

    @Override
    public String toString() {
        return version[0] + "." + version[1] + "." + version[2] + (isPrerelease() ? "-" + preRelease : "") + (hasBuildData() ? "+" + buildData : "");
    }

    @Override
    public int compareTo(Version o) {
        if (isOlderThan(o)) {
            return -1;
        } else if (isNewerThan(o)) {
            return 1;
        } else {
            return 0;
        }
    }

}
