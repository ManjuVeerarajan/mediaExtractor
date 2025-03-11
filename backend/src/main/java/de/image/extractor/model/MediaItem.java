package de.image.extractor.model;

import lombok.Getter;

import java.time.OffsetDateTime;

@Getter
public class MediaItem {
    private String id;
    private String db;
    private String text;
    private String photoGraphers;
    private String thumbnailUrl;
    private OffsetDateTime datum;

    public MediaItem(String id, String db, String text, String photoGraphers, OffsetDateTime datum) {
        this.id = id;
        this.db = db;
        this.text = text != null ? text : "";
        this.photoGraphers = photoGraphers != null ? photoGraphers : "";
        this.thumbnailUrl = buildUrl(this.db, this.id);
        this.datum = datum;
    }

    public String buildUrl(String db, String id) {
        String paddedId = String.format("%010d", Integer.parseInt(id));
        return "https://www.imago-images.de/bild/" + (db != null ? db : "st") + "/" + paddedId + "/s.jpg";
    }

}
