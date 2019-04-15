package ru.otus.springframework.library.controllers.rest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthorController.class)
@ActiveProfiles("rest")
class AuthorControllerTest {

    @Autowired
    private MockMvc mvc;

    @WithMockUser(username = "test_user", password = "test_password")
    @Test
    void all() throws Exception {
        mvc.perform(get("/authors")).andExpect(status().isOk());
    }

    @WithMockUser(username = "test_user", password = "test_password")
    @Test
    void author() throws Exception {
        mvc.perform(get("/author/123")).andExpect(status().isOk());
    }
}