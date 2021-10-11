package net.redstonecraft.redstoneapi.core;

import java.util.Arrays;
import java.util.Objects;

/**
 * Version class using <a href="https://semver.org/">SemVer version naming convention</a>
 *
 * @author Redstonecrafter0
 * @since 1.1
 * */
@SuppressWarnings("unused")
public class Version implements Comparable<Version> {

    private final int[] version;
    private final String preRelease;
    private final String buildData;

    /**
     * @param major major version
     * @param minor minor version
     * @param patch version patch
     * @param preRelease pre-release string
     * @param buildData buildData like commit hash
     * @throws IllegalArgumentException if the input is invalid
     */
    public Version(int major, int minor, int patch, String preRelease, String buildData) throws IllegalArgumentException {
        if (major < 0 || minor < 0 || patch < 0) {
            throw new IllegalArgumentException("Version numbers are not allowed to be negative");
        }
        if (!preRelease.matches("[0-9A-Za-z-.]*")) {
            throw new IllegalArgumentException("Version preRelease data must only contain ASCII alphanumerics, hyphens and dots as separators");
        }
        if (!buildData.matches("[0-9A-Za-z-.]*")) {
            throw new IllegalArgumentException("Version preRelease data must only contain ASCII alphanumerics, hyphens and dots as separators");
        }
        version = new int[]{major, minor, patch};
        this.preRelease = preRelease;
        this.buildData = buildData;
    }

    /**
     * @param version string to parse from
     * @return version object
     * @throws IllegalArgumentException if the input is invalid
     */
    public static Version fromVersionString(String version) throws IllegalArgumentException {
        if (version.startsWith("v")) {
            version = version.substring(1);
        }
        String main;
        String pre = "";
        String build = "";
        if (version.contains("-")) {
            String[] tmp = version.split("-");
            if (version.contains("+")) {
                String[] tmp1 = tmp[1].split("\\+");
                main = tmp[0];
                pre = tmp1[0];
                build = tmp1[1];
            } else {
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

    /**
     * @return the major version
     */
    public int getMajor() {
        return version[0];
    }

    /**
     * @return the minor version
     */
    public int getMinor() {
        return version[1];
    }

    /**
     * @return the version patch
     */
    public int getPatch() {
        return version[2];
    }

    /**
     * @return if the version is a pre-release
     */
    public boolean isPrerelease() {
        return !preRelease.equals("");
    }

    /**
     * @return if the pre-release is alpha (starts with alpha)
     */
    public boolean isAlpha() {
        return preRelease.startsWith("alpha");
    }

    /**
     * @return if the pre-release is beta (starts with beta)
     */
    public boolean isBeta() {
        return preRelease.startsWith("beta");
    }

    /**
     * @return if the pre-release is a release candidate (starts with rc)
     */
    public boolean isReleaseCandidate() {
        return preRelease.startsWith("rc");
    }

    /**
     * @return if the version has build data
     */
    public boolean hasBuildData() {
        return !buildData.equals("");
    }

    /**
     * @param compare the version to compare with
     * @return true if this version is older than compare
     */
    public boolean isOlderThan(Version compare) {
        for (int i = 0; i < version.length; i++) {
            if (version[i] < compare.version[i]) {
                return true;
            }
        }
        if (isPrerelease() && !compare.isPrerelease()) {
            return true;
        } else if (!isPrerelease() && compare.isPrerelease()) {
            return false;
        }
        for (Pair<String, String> i : IterUtils.zipFillNull(Arrays.asList(preRelease.split("\\.")), Arrays.asList(compare.preRelease.split("\\.")))) {
            Integer a = NumberUtils.toInt(i.first());
            Integer b = NumberUtils.toInt(i.second());
            if (i.first() == null || i.second() == null) {
                return i.first() == null;
            } else if ((a == null || b == null) && i.first().compareTo(i.second()) != 0) {
                return i.first().compareTo(i.second()) < 0;
            } else if (a != null && b != null && (a < b)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param compare the version to compare with
     * @return true if this version is newer than compare
     */
    public boolean isNewerThan(Version compare) {
        for (int i = 0; i < version.length; i++) {
            if (version[i] > compare.version[i]) {
                return true;
            }
        }
        if (isPrerelease() && !compare.isPrerelease()) {
            return false;
        } else if (!isPrerelease() && compare.isPrerelease()) {
            return true;
        }
        for (Pair<String, String> i : IterUtils.zipFillNull(Arrays.asList(preRelease.split("\\.")), Arrays.asList(compare.preRelease.split("\\.")))) {
            Integer a = NumberUtils.toInt(i.first());
            Integer b = NumberUtils.toInt(i.second());
            if (i.first() == null || i.second() == null) {
                return i.second() == null;
            } else if ((a == null || b == null) && i.first().compareTo(i.second()) != 0) {
                return i.first().compareTo(i.second()) > 0;
            } else if (a != null && b != null && (a > b)) {
                return true;
            }
        }
        return false;
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
        int result = Objects.hash(preRelease, buildData);
        result = 31 * result + Arrays.hashCode(version);
        return result;
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
