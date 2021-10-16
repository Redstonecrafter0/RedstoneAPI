# RedstoneAPI

This is the development branch for version 2.0.
Nothing here will stay the same.

![GitHub release (latest by date)](https://img.shields.io/github/v/release/Redstonecrafter0/RedstoneAPI?label=latest%20release&style=for-the-badge)
![GitHub release (latest by date including pre-releases)](https://img.shields.io/github/v/release/Redstonecrafter0/RedstoneAPI?color=E67233&include_prereleases&label=latest%20pre-release&style=for-the-badge)

![GitHub language count](https://img.shields.io/github/languages/count/Redstonecrafter0/RedstoneAPI?style=for-the-badge)
![GitHub top language](https://img.shields.io/github/languages/top/Redstonecrafter0/RedstoneAPI?style=for-the-badge)
![GitHub repo size](https://img.shields.io/github/repo-size/Redstonecrafter0/RedstoneAPI?style=for-the-badge)
![GitHub issues](https://img.shields.io/github/issues-raw/Redstonecrafter0/RedstoneAPI?style=for-the-badge)
![GitHub closed issues](https://img.shields.io/github/issues-closed-raw/Redstonecrafter0/RedstoneAPI?style=for-the-badge)
![GitHub](https://img.shields.io/github/license/Redstonecrafter0/RedstoneAPI?style=for-the-badge)
![GitHub Repo stars](https://img.shields.io/github/stars/Redstonecrafter0/RedstoneAPI?style=for-the-badge)
![GitHub commit activity](https://img.shields.io/github/commit-activity/m/Redstonecrafter0/RedstoneAPI?style=for-the-badge)
![GitHub contributors](https://img.shields.io/github/contributors/Redstonecrafter0/RedstoneAPI?style=for-the-badge)
![GitHub last commit](https://img.shields.io/github/last-commit/Redstonecrafter0/RedstoneAPI?style=for-the-badge)
![GitHub Release Date](https://img.shields.io/github/release-date/Redstonecrafter0/RedstoneAPI?label=latest%20release&style=for-the-badge)
![GitHub (Pre-)Release Date](https://img.shields.io/github/release-date-pre/Redstonecrafter0/RedstoneAPI?label=latest%20pre-release&style=for-the-badge)

![GitHub all releases](https://img.shields.io/github/downloads/Redstonecrafter0/RedstoneAPI/total?style=for-the-badge)
![Spiget Downloads](https://img.shields.io/spiget/downloads/88273?label=spigot%20downloads&style=for-the-badge)

![Spiget tested server versions](https://img.shields.io/spiget/tested-versions/88273?style=for-the-badge)

![Discord](https://img.shields.io/discord/391551622297157632?color=7289DA&label=discord&style=for-the-badge)

## Support

Support is provided on [Discord](https://discord.gg/aZKuas4).

## Dependency

### Maven
To implement the RedstoneAPI in your projects you can use primarily Maven.
Implement the RedstoneAPI by pasting this code in your pom.xml

Replace {VERSION} with the current version and **keep** the prefix `v`.
```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.github.Redstonecrafter0</groupId>
        <artifactId>RedstoneAPI</artifactId>
        <version>{VERSION}</version>
    </dependency>
</dependencies>
   ```

### Gradle
To implement the RedstoneAPI in your projects you could also use Gradle.
Just paste this in your build.gradle

Replace {VERSION} with the current version but **keep** the prefix `v`.
```
maven {
    url 'https://jitpack.io'
}

dependencies {
    implementation 'com.github.Redstonecrafter0:RedstoneAPI:{VERSION}'
}
```
