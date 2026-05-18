package com.innovationCampus.challenger.controllers;

import com.innovationCampus.challenger.AbstractIntegrationTest;
import com.innovationCampus.challenger.security.WithAppUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class FriendControllerTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithAppUser
    void getFriends() throws Exception {
        mockMvc.perform(get("/friends"))
                .andExpect(status().isOk());
    }
}
