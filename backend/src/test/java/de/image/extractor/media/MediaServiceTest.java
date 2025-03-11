package de.image.extractor.media;

import de.image.extractor.controller.MediaController;
import de.image.extractor.model.MediaItem;
import de.image.extractor.service.MediaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MediaController.class)
public class MediaServiceTest {
    @Autowired
    private MockMvc mockMvc;
    @Mock
    private MediaService mediaService;

    private MediaItem sampleItem = new MediaItem(
            "258999085",
            "stock",
            "Pal Sarkozy?s Funeral",
            "ABACAPRESS",
            OffsetDateTime.parse("2023-03-09T00:00:00.000Z")
    );

    @Test
    public void testGetAllMedia() throws Exception {
        List<MediaItem> mockItems = Collections.singletonList(sampleItem);
        when(mediaService.getAllMedia(anyInt(), anyInt())).thenReturn(mockItems);

        mockMvc.perform(get("/all")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].db").exists())
                .andExpect(jsonPath("$[0].photoGraphers").exists())
                .andExpect(jsonPath("$[0].datum").exists());
    }

    @Test
    public void testFilterMedia() throws Exception {
        List<MediaItem> mockItems = Collections.singletonList(sampleItem);
        when(mediaService.filterMedia(anyString(), anyInt(), anyInt())).thenReturn(mockItems);

        mockMvc.perform(get("/filter")
                        .param("keyword", "Pal Sarkozy")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("258999339"))
                .andExpect(jsonPath("$[0].db").value("stock"));
    }
}
