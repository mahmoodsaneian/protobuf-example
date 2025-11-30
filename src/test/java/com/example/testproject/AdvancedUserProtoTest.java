package com.example.testproject;

import com.example.testproject.proto.advanced.AdvancedUserProto;
import com.google.protobuf.InvalidProtocolBufferException;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AdvancedUserProtoTest {

    private void log(String title, Object value) {
        System.out.println("[" + title + "] " + value);
    }

    /**
     * basic message operations
     * build
     * immutability
     * to builder
     */
    @Test
    public void test01_basicConstructionAndImmutability() {
        AdvancedUserProto.User user1 = AdvancedUserProto.User.newBuilder()
                .setId(1L)
                .setName("Mahmood")
                .addTags("vip")
                .build();

        log("user1", user1);

        AdvancedUserProto.User.Builder builder = user1.toBuilder();
        builder.setName("Mahmood updated");
        AdvancedUserProto.User user2 = builder.build();
        log("user1 name", user1.getName());
        log("user2 name", user2.getName());

        assertEquals("Mahmood", user1.getName());
        assertEquals("Mahmood updated", user2.getName());
        assertNotSame(user1, user2);
    }

    /**
     * Serialization & parsing
     */
    @Test
    public void test02_serializationAndParsing() throws Exception {
        AdvancedUserProto.User user = AdvancedUserProto.User.newBuilder()
                .setId(42L)
                .setName("Test Mahmood")
                .addTags("vip")
                .build();

        byte[] bytes = user.toByteArray();
        log("serialized size", bytes.length);
        log("first bytes", Arrays.toString(Arrays.copyOf(bytes, Math.min(10, bytes.length))));

        AdvancedUserProto.User parsed = AdvancedUserProto.User.parseFrom(bytes);
        log("parsed", parsed);
        assertEquals(user, parsed);

        // write to file
        File file = new File("target/user.bin");
        try (OutputStream outputStream = new FileOutputStream(file)) {
            user.writeTo(outputStream);
        }

        // read from file
        try (InputStream inputStream = new FileInputStream(file)) {
            AdvancedUserProto.User deserialized = AdvancedUserProto.User.parseFrom(inputStream);
            assertEquals(user, deserialized);
        }

        // corrupted bytes (truncate)
        byte[] corrupted = Arrays.copyOf(bytes, bytes.length - 1);
        assertThrows(InvalidProtocolBufferException.class,
                () -> AdvancedUserProto.User.parseFrom(corrupted),
                "Expected InvalidProtocolBufferException for corrupted data");

    }

    /**
     * optional fields
     */
    @Test
    public void test03_optionalFields(){
        // no nickname
        AdvancedUserProto.User u1 = AdvancedUserProto.User.newBuilder()
                .setId(1)
                .setName("Mahmood NoNick")
                .build();

        log("u1.hasNickName", u1.hasNickname());
        assertEquals("", u1.getNickname());

        // nickname explicitly ""
        AdvancedUserProto.User u2 = AdvancedUserProto.User.newBuilder()
                .setId(2)
                .setName("Mahmood empty nick")
                .setNickname("")
                .build();
        log("u2.hasNickName", u2.hasNickname());
        log("u2.nickname", "'" + u2.getNickname() + "'");

        assertTrue(u2.hasNickname());
        assertEquals("",  u2.getNickname());


        // nickname value + clear
        AdvancedUserProto.User u3 = AdvancedUserProto.User.newBuilder()
                .setId(3)
                .setName("Mahmood")
                .setNickname("Mahmood Nickname")
                .setGender(AdvancedUserProto.Gender.MALE)
                .build();

        log("u3.hasNickname", u3.hasNickname());
        log("u3.nickname", u3.getNickname());
        log("u3.hasGender", u3.hasGender());
        log("u3.gender", u3.getGender());

        assertTrue(u3.hasNickname());
        assertEquals("Mahmood Nickname", u3.getNickname());
        assertTrue(u3.hasGender());
        assertEquals(AdvancedUserProto.Gender.MALE, u3.getGender());


        AdvancedUserProto.User u4 = u3.toBuilder().clearNickname().build();
        assertFalse(u4.hasNickname());
        assertEquals("", u4.getNickname());
    }

    /**
     * repeated fields
     */
    @Test
    public void test04_repeatedFields(){
        AdvancedUserProto.User user = AdvancedUserProto.User.newBuilder()
                .setId(10)
                .setName("Mahmood repeated")
                .addTags("vip")
                .addTags("beta")
                .addAllTags(List.of("friend", "tester"))
                .addTags("vip") // duplicate
                .build();

        log("tags", user.getTagsList());
        log("tagsCount", user.getTagsCount());

        assertEquals(List.of("vip", "beta", "friend", "tester", "vip"), user.getTagsList());

        // immutability
        assertThrows(UnsupportedOperationException.class, () -> user.getTagsList().add("vip"));

        // modify via builder
        AdvancedUserProto.User user2 = user.toBuilder()
                .addTags("newTag")
                .build();
        log("user2.tags", user2.getTagsList());
        assertEquals(List.of("vip", "beta", "friend", "tester", "vip", "newTag"),
                user2.getTagsList());
    }
}
