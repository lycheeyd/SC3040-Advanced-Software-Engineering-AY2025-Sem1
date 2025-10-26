package com.Services;


import com.controller.UserInfoController;
import com.models.UserInfo;
import com.service.UserInfoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserInfoController.class)
class UserInfoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserInfoService userInfoService;

    private UserInfo createUser(String id, String name) {
        UserInfo user = new UserInfo();
        user.setUserId(id);
        user.setName(name);
        return user;
    }

    @Test
    void searchUsers_ShouldReturnUserList() throws Exception {
        // ARRANGE
        UserInfo userA = createUser("userA", "Alice");
        UserInfo userB = createUser("userB", "Bob");
        when(userInfoService.searchByUserIdOrName("searchterm", "currentuser"))
                .thenReturn(List.of(userA, userB));

        // ACT & ASSERT
        mockMvc.perform(get("/api/users/search")
                        .param("searchTerm", "searchterm")
                        .param("currentUserId", "currentuser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Alice"))
                .andExpect(jsonPath("$[1].name").value("Bob"));
    }
}