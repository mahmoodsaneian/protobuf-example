package com.example.testproject;

import com.example.testproject.proto.UserApiProto;
import com.example.testproject.proto.advanced.AdvancedUserProto;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
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
    public void test03_optionalFields() {
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
        assertEquals("", u2.getNickname());


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
    public void test04_repeatedFields() {
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

    /**
     * enums
     * unknown enum value
     */
    @Test
    public void test05_enums() {
        AdvancedUserProto.User user = AdvancedUserProto.User.newBuilder()
                .setId(1)
                .setName("Mahmood enum")
                .setGender(AdvancedUserProto.Gender.OTHER)
                .build();

        log("u.gender", user.getGender());
        log("u.genderValue", user.getGenderValue());

        assertEquals(AdvancedUserProto.Gender.OTHER, user.getGender());
        assertEquals(AdvancedUserProto.Gender.OTHER.getNumber(), user.getGenderValue());

        // unknown enum
        AdvancedUserProto.User unknown = user.toBuilder().setGenderValue(99).build();
        log("uUnknown.genderValue", unknown.getGenderValue());
        log("uUnknown.gender", unknown.getGender());

        assertEquals(99, unknown.getGenderValue());
        assertEquals(AdvancedUserProto.Gender.UNRECOGNIZED, unknown.getGender());

        switch (unknown.getGender()) {
            case MALE, FEMALE, OTHER, GENDER_UNSPECIFIED -> fail("Should be UNRECOGNIZED");
            case UNRECOGNIZED -> log("enum handling", "Unknown gender value at runtime");
        }
    }

    /**
     * one of login method
     */
    @Test
    public void test06_oneOfLoginMethod() {
        AdvancedUserProto.User user1 = AdvancedUserProto.User.newBuilder()
                .setId(1)
                .setName("Mahmood Email")
                .setEmailLogin("mahmoodsaneian1@gmail.com")
                .build();

        log("user1.loginMethodCase", user1.getLoginMethodCase());
        log("user1.emailLogin", user1.getEmailLogin());

        assertEquals(AdvancedUserProto.User.LoginMethodCase.EMAIL_LOGIN, user1.getLoginMethodCase());
        assertEquals("mahmoodsaneian1@gmail.com", user1.getEmailLogin());

        AdvancedUserProto.User user2 = AdvancedUserProto.User.newBuilder()
                .setId(2)
                .setName("Mahmood Phone")
                .setPhoneLogin(AdvancedUserProto.PhoneNumber.newBuilder()
                        .setCountry("Iran")
                        .setNumber("+989188688513").build())
                .build();
        log("user2.loginMethodCase", user2.getLoginMethodCase());
        log("user2.phoneLogin.number", user2.getPhoneLogin().getNumber());

        assertEquals(AdvancedUserProto.User.LoginMethodCase.PHONE_LOGIN, user2.getLoginMethodCase());
        assertEquals("+989188688513", user2.getPhoneLogin().getNumber());

        // set email then phone

        AdvancedUserProto.User.Builder builder = AdvancedUserProto.User.newBuilder()
                .setId(3)
                .setName("Mahmood mixed")
                .setEmailLogin("before@example.com");
        assertEquals(AdvancedUserProto.User.LoginMethodCase.EMAIL_LOGIN, builder.getLoginMethodCase());

        builder.setPhoneLogin(AdvancedUserProto.PhoneNumber.newBuilder()
                .setCountry("IR")
                .setNumber("+989188688513").build());

        AdvancedUserProto.User user3 = builder.build();
        log("user3.loginMethodCase", user3.getLoginMethodCase());
        log("user3.emailLogin_afterPhone", "'" + user3.getEmailLogin() + "'");

        assertEquals(AdvancedUserProto.User.LoginMethodCase.PHONE_LOGIN, user3.getLoginMethodCase());
        assertEquals("", user3.getEmailLogin());

        // no login info
        AdvancedUserProto.User user4 = AdvancedUserProto.User.newBuilder()
                .setId(4)
                .setName("Mahmood no login")
                .build();

        assertEquals(AdvancedUserProto.User.LoginMethodCase.LOGINMETHOD_NOT_SET, user4.getLoginMethodCase());
    }

    /**
     * nested messages
     */
    @Test
    public void test07_nestedMessages() {
        AdvancedUserProto.ContactInfo contact = AdvancedUserProto.ContactInfo.newBuilder()
                .setAddress("Jahrom")
                .setZip("74188")
                .build();

        AdvancedUserProto.User u1 = AdvancedUserProto.User.newBuilder()
                .setId(1)
                .setName("Mahmood with contact")
                .setContact(contact)
                .build();

        log("u1.contact.address", u1.getContact().getAddress());
        assertEquals("Jahrom", u1.getContact().getAddress());

        AdvancedUserProto.User u2 = AdvancedUserProto.User.newBuilder()
                .setId(2)
                .setName("Mahmood without contact")
                .build();

        AdvancedUserProto.ContactInfo c2 = u2.getContact();
        log("u2.contact", c2);
        log("u2.contact.address", "'" + c2.getAddress() + "'");
        assertEquals("", c2.getAddress());

        // modify
        AdvancedUserProto.User.Builder builder = AdvancedUserProto.User.newBuilder()
                .setId(3)
                .setName("Mahmood builder");

        builder.getContactBuilder()
                .setAddress("Jahrom-2")
                .setZip("46893");

        AdvancedUserProto.User u3 = builder.build();

        log("u3.contact", u3.getContact());
        assertEquals("Jahrom-2", u3.getContact().getAddress());
    }

    /**
     * unknown enum value + reserialization
     */
    @Test
    public void test08_unknownEnumValueRoundTrip() throws Exception {
        AdvancedUserProto.User u = AdvancedUserProto.User.newBuilder()
                .setId(1)
                .setName("Unknown enum user")
                .setGenderValue(123)
                .build();

        log("u.gender", u.getGender());
        log("u.genderValue", u.getGenderValue());

        assertEquals(AdvancedUserProto.Gender.UNRECOGNIZED, u.getGender());
        byte[] bytes = u.toByteArray();
        AdvancedUserProto.User parsed  = AdvancedUserProto.User.parseFrom(bytes);

        log("parsed.gender", parsed.getGender());
        log("parsed.genderValue", parsed.getGenderValue());

        assertEquals(123, parsed.getGenderValue());
        assertEquals(AdvancedUserProto.Gender.UNRECOGNIZED, parsed.getGender());
    }

    /**
     * JSON <-> protobuf using json format
     */
    @Test
    public void test09_jsonFormatRoundTrip() throws Exception {
        AdvancedUserProto.User user = AdvancedUserProto.User.newBuilder()
                .setId(100)
                .setName("Mahmood Json")
                .setNickname("Mj")
                .setGender(AdvancedUserProto.Gender.FEMALE)
                .addTags("vip")
                .build();

        String json = JsonFormat.printer()
                .includingDefaultValueFields()
                .print(user);

        log("json", json);

        AdvancedUserProto.User.Builder builder = AdvancedUserProto.User.newBuilder();
        JsonFormat.parser()
                .ignoringUnknownFields()
                .merge(json, builder);

        AdvancedUserProto.User parsed = builder.build();
        log("parsedFromJson", parsed);

        assertEquals(user.getId(), parsed.getId());
        assertEquals(user.getNickname(), parsed.getNickname());
        assertEquals(user.getGender(), parsed.getGender());
        assertEquals(user.getName(), parsed.getName());
        assertEquals(user.getTagsList(), parsed.getTagsList());
    }
}
