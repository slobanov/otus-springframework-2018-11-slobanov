package ru.otus.springframework.library.controllers.rest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GenreController.class)
@ActiveProfiles("rest")
class GenreControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void all() throws Exception {
        mvc.perform(get("/genres")).andExpect(status().isOk());
    }

    @Test
    void genre() throws Exception {
        mvc.perform(get("/genre/123")).andExpect(status().isOk());
    }
}