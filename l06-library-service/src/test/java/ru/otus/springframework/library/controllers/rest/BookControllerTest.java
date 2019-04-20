package ru.otus.springframework.library.controllers.rest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class)
@ActiveProfiles({"rest", "test-mongodb"})
class BookControllerTest {

    @MockBean
    private UserDetailsService userDetailsService;

    @Autowired
    private MockMvc mvc;

    @Test
    @WithMockUser(username = "test_user", password = "test_password")
    void all() throws Exception {
        mvc.perform(get("/books")).andExpect(status().isOk());
    }

    @Test
    void all302() throws Exception {
        mvc.perform(get("/books"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login.html"));
    }

    @Test
    @WithMockUser(username = "test_user", password = "test_password", authorities = "ROLE_BOOK")
    void book() throws Exception {
        mvc.perform(get("/book/123")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "test_user", password = "test_password", authorities = "ROLE_OTHER")
    void book403() throws Exception {
        mvc.perform(get("/book/123")).andExpect(status().isForbidden());
    }

    @Test
    void book302() throws Exception {
        mvc.perform(get("/book/123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login.html"));
    }
}