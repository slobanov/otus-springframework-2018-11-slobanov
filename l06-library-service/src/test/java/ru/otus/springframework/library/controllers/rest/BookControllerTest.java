package ru.otus.springframework.library.controllers.rest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class)
@ActiveProfiles("rest")
class BookControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    @WithMockUser(username = "test_user", password = "test_password")
    void all() throws Exception {
        mvc.perform(get("/books")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "test_user", password = "test_password")
    void book() throws Exception {
        mvc.perform(get("/book/123")).andExpect(status().isOk());
    }
}