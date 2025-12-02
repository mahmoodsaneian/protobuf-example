package com.example.testproject;

import com.example.testproject.proto.advanced.AdvancedUserProto.*;
import com.google.protobuf.InvalidProtocolBufferException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/advanced/users")
public class AdvancedUserController {

    // Returning protobuf
    @GetMapping(
            value = "/{id}",
            produces = "application/x-protobuf")
    public byte[] getUserAsProtobuf(@PathVariable long id) {
        User user = User.newBuilder()
                .setId(id)
                .setName("User " + id)
                .setGender(Gender.MALE)
                .addTags("vip")
                .build();
        return user.toByteArray();
    }

    // Receiving protobuf
    @PostMapping(
            value = "/parse",
            consumes = "application/x-protobuf")
    public String parseUser(@RequestBody byte[] body) throws InvalidProtocolBufferException {
        User user = User.parseFrom(body);
        return "OK:" + user.getId() + ":" + user.getName();
    }

    // Mixed mode
    record UserJsonDto(long id, String name, String email) {
    }

    @GetMapping(
            value = "/{id}/json",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public UserJsonDto getUserAsJson(@PathVariable long id) {
        User internal = User.newBuilder()
                .setId(id)
                .setName("JsonUser " + id)
                .setEmailLogin("user" + id + "@example.com")
                .build();
        return new UserJsonDto(
                internal.getId(),
                internal.getName(),
                internal.getEmailLogin()
        );
    }

    @PostMapping(
            value = "/parse-safe",
            consumes = "application/x-protobuf")
    public String parseUserSafe(@RequestBody byte[] body) {
        try {
            User user = User.parseFrom(body);
            return "OK : " + user.getId();
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid protobuf", e);
        }
    }
}
