package de.image.extractor.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import de.image.extractor.model.MediaItem;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MediaService {
    private static final Logger logger = LoggerFactory.getLogger(MediaService.class);
    private final ElasticsearchClient client;

    @Cacheable("mediaAll")
    public List<MediaItem> getAllMedia(int page, int size) throws IOException {
        logger.info("Retrieving all media items, page: {}, size: {}", page, size);
        try {
            SearchResponse<Object> response = client.search(s -> s
                            .index("imago")
                            .query(q -> q.matchAll(m -> m))
                            .from(page * size)
                            .size(size),
                    Object.class);

            logger.info("Retrieved {} hits", response.hits().total() != null ? response.hits().total().value() : 0);

            return response.hits().hits().stream()
                    .map(Hit::source)
                    .map(this::mapToMediaItem)
                    .collect(Collectors.toList());
        } catch (ElasticsearchException e) {
            logger.error("Failed to retrieve all media: {}", e.getMessage(), e);
            if (e.response() != null) {
                logger.error("Error response: {}", e.response().toString());
            }
            throw new IOException("Failed to retrieve media: " + e.getMessage(), e);
        }
    }
    @Cacheable("mediaSearch")
    public List<MediaItem> filterMedia(String keyword, int page, int size) throws IOException {
        logger.info("Filtering media with query: {}", keyword);
        try {
            SearchResponse<Object> response = client.search(s -> s
                            .index("imago")
                            .query(q -> q
                                    .bool(b -> {
                                        b.should(sh -> sh.multiMatch(m -> m
                                                .fields("suchtext", "fotografen")
                                                .query(keyword)));

                                        try {
                                            long bildnummer = Long.parseLong(keyword);
                                            b.should(sh -> sh.term(t -> t
                                                    .field("bildnummer")
                                                    .value(bildnummer)));
                                        } catch (NumberFormatException e) {
                                            logger.debug("Keyword '{}' is not numeric, skipping bildnummer filter", keyword);
                                        }

                                        return b.minimumShouldMatch("1");
                                    }))
                            .from(page * size)
                            .size(size),
                    Object.class);

            logger.info("Filter executed for keyword: {}, page: {}, size: {}. Hits: {}",
                    keyword, page, size, response.hits().total() != null ? response.hits().total().value() : 0);

            return response.hits().hits().stream()
                    .map(Hit::source)
                    .map(this::mapToMediaItem)
                    .collect(Collectors.toList());
        } catch (ElasticsearchException e) {
            logger.error("Filter search failed: {}", e.getMessage(), e);
            if (e.response() != null) {
                logger.error("Error response: {}", e.response().toString());
            }
            throw new IOException("Filter search failed: " + e.getMessage(), e);
        }
    }

    private MediaItem mapToMediaItem(Object source) {
        var map = (Map<String, Object>) source;
        logger.debug("Processing document: {}", map);

        String datumStr = (String) map.get("datum");
        OffsetDateTime datum = datumStr != null ? OffsetDateTime.parse(datumStr) : null;

        return new MediaItem(
                (String) map.get("bildnummer"),
                (String) map.get("db"),
                (String) map.get("suchtext"),
                (String) map.get("fotografen"),
                datum
        );
    }
}
