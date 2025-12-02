package com.example.testproject;

import com.example.testproject.proto.advanced.AdvancedUserProto.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.assertj.core.api.Assertions.assertThat;

@WebMvcTest(controllers = {AdvancedUserController.class})
public class AdvancedUserHttpTest {

    private MockMvc mockMvc;

    @Autowired
    public void setMockMvc(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    /**
     * Returning protobuf from controllers
     */
    @Test
    public void controllerReturnsProtobuf() throws Exception {
        var result = mockMvc.perform(
                        get("/advanced/users/123").accept("application/x-protobuf"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/x-protobuf"))
                .andReturn();

        byte[] body = result.getResponse().getContentAsByteArray();
        User user = User.parseFrom(body);

        assertThat(user.getId()).isEqualTo(123);
        assertThat(user.getName()).isEqualTo("User 123");
    }

    /**
     * Receiving protobuf in requests
     */
    @Test
    public void controllerReceiveProtobuf() throws Exception {
        User user = User.newBuilder()
                .setId(555)
                .setName("Mahmood")
                .build();

        mockMvc.perform(
                        post("/advanced/users/parse")
                                .contentType("application/x-protobuf")
                                .content(user.toByteArray()))
                .andExpect(status().isOk())
                .andExpect(content().string("OK:555:Mahmood"));
    }

    /**
     * Mixed mode
     */
    @Test
    public void mixedModeInternalProtoExternalJson() throws Exception {
        var result = mockMvc.perform(
                        get("/advanced/users/10/json")
                                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        System.out.println("[JSON] " + json);
        assertThat(json).contains("JsonUser 10").contains("user10@example.com");
    }

    /**
     * Error Handling in HTTP layer
     */
    @Test
    public void invalidProtobufReturns400() throws Exception {
        byte[] garbage = new byte[]{1, 2, 3, 4};
        mockMvc.perform(
                        post("/advanced/users/parse-safe")
                                .contentType("application/x-protobuf")
                                .content(garbage))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(org.hamcrest.CoreMatchers.containsString("Invalid protobuf")));
    }
}
