package net.redstonecraft.redstoneapi.tools;

/**
 * Enum containing the most used MIME types and their extensions
 *
 * @author Redstonecrafter0
 * @since 1.2
 * */
public enum MimeType {

    DEFAULT("application/octet-stream", ".bin"),

    AAC("audio/aac", ".aac"),
    ABW("application/x-abiword", ".abw"),
    ARC("application/x-freearc", ".arc"),
    AVI("video/x-msvideo", ".avi"),
    AZW("application/vnd.amazon.ebook", ".azw"),
    BITMAP("image/bmp", ".bmp"),
    BZ("application/x-bzip", ".bz"),
    BZ2("application/x-bzip2", ".bz2"),
    CSH("application/x-csh", ".csh"),
    CSS("text/css", ".css"),
    CSV("text/csv", ".csv"),
    DOC("application/msword", ".doc"),
    DOCX("application/vnd.openxmlformats-officedocument.wordprocessingml.document", ".docx"),
    EOT("application/vnd.ms-fontobject", ".eot"),
    EPUB("application/epub+zip", ".epub"),
    GZIP("application/gzip", ".gz"),
    GIF("image/gif", ".gif"),
    HTML("text/html", ".html", ".htm"),
    ICON("image/x-icon", ".ico"),
    ICS("text/calendar", ".ics"),
    JAR("application/java-archive", ".jar"),
    JPEG("image/jpeg", ".jpg", ".jpeg"),
    JAVASCRIPT("text/javascript", ".js"),
    JSON("application/json", ".json"),
    JSONLD("application/ld+json", ".jsonld"),
    MIDI("audio/midi", ".midi", ".mid"),
    MP3("audio/mpeg", ".mp3"),
    CDA("application/x-cdf", ".cda"),
    MP4("video/mp4", ".mp4"),
    MPEG("video/mpeg", ".mpeg"),
    ODP("application/vnd.oasis.opendocument.presentation", ".odp"),
    ODS("application/vnd.oasis.opendocument.spreadsheet", ".ods"),
    ODT("application/vnd.oasis.opendocument.text", ".odt"),
    OGA("audio/ogg", ".oga"),
    OGV("video/ogg", ".ogv"),
    OGX("application/ogg", ".ogx"),
    OPUS("audio/opus", ".opus"),
    OTF("font/otf", ".otf"),
    PNG("image/png", ".png"),
    PDF("application/pdf", ".pdf"),
    PHP("application/x-httpd-php", ".php"),
    PPT("application/vnd.ms-powerpoint", ".ppt"),
    PPTX("application/vnd.openxmlformats-officedocument.presentationml.presentation", ".pptx"),
    RAR("application/vnd.rar", ".rar"),
    RTF("application/rtf", ".rtf"),
    SH("application/x-sh", ".sh"),
    SVG("image/svg+xml", ".svg"),
    SWF("application/x-shockwave-flash", ".swf"),
    TAR("application/x-tar", ".tar"),
    TIFF("image/tiff", ".tif", ".tiff"),
    TS("video/mp2t", ".ts"),
    TTF("font/ttf", ".ttf"),
    TXT("text/plain", ".txt"),
    VSD("application/vnd.visio", ".vsd"),
    WAV("audio/wav", ".wav"),
    WEBA("audio/webm", ".weba"),
    WEBM("video/webm", ".webm"),
    WEBP("image/webp", ".webp"),
    WOFF("font/woff", ".woff"),
    WOFF2("font/woff2", ".woff2"),
    XHTML("application/xhtml+xml", ".xhtml"),
    XLS("application/vnd.ms-excel", ".xls"),
    XLSX("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", ".xls"),
    XML("text/xml", ".xml"),
    XUL("application/vnd.mozilla.xul+xml", ".xul"),
    ZIP("application/zip", ".zip"),
    SEVENZIP("application/x-7z-compressed", ".7z");

    private final String mimetype;
    private final String[] extensions;

    MimeType(String mimetype, String... extensions) {
        this.mimetype = mimetype;
        this.extensions = extensions;
    }

    public String[] getExtensions() {
        return extensions;
    }

    public String getPrimaryExtension() {
        return extensions[0];
    }

    public String getMimetype() {
        return mimetype;
    }

    public static MimeType getByFilename(String filename) {
        for (MimeType i : MimeType.values()) {
            for (String j : i.getExtensions()) {
                if (filename.endsWith(j)) {
                    return i;
                }
            }
        }
        return DEFAULT;
    }

    public static MimeType getByExtension(String ext) {
        for (MimeType i : MimeType.values()) {
            for (String j : i.getExtensions()) {
                if (ext.equals(j)) {
                    return i;
                }
            }
        }
        return DEFAULT;
    }

    public static MimeType getByMimeType(String mimetype) {
        for (MimeType i : MimeType.values()) {
            if (i.getMimetype().equals(mimetype)) {
                return i;
            }
        }
        return DEFAULT;
    }
}
