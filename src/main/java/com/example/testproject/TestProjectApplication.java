package com.example.testproject;

import com.example.testproject.proto.UserApiProto;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

@SpringBootApplication
public class TestProjectApplication {

    public static void main(String[] args) {
        repeatedField();
        SpringApplication.run(TestProjectApplication.class, args);
    }

    private static void mutableAndImmutable() {
        // Immutable & Mutable
        // builder -> mutable
        // user -> immutable

        UserApiProto.User.Builder builder = UserApiProto.User.newBuilder();
        builder.setId(1).setName("Mahmood");
        UserApiProto.User user1 = builder.build();

        builder.setId(2).setName("Sina");
        UserApiProto.User user2 = builder.build();

        System.out.println(user1);
        System.out.println(user2);

        System.out.println("********************************************");
        //Copy
        UserApiProto.User user3 = user1.toBuilder().setId(3).setNickname("Pouria").build();
        System.out.println(user3);
        System.out.println(user1);
    }

    private static void serializationAndDeserialization() {
        // Create User
        UserApiProto.User user1 = UserApiProto.User.newBuilder()
                .setId(1)
                .setName("Mahmood")
                .setNickname("Arash")
                .setEmail("Mahmoodsaneian1@gmail.com")
                .build();

        // Serialize to bytes
        byte[] bytes = user1.toByteArray();
        System.out.println("Size : " + bytes.length);

        // Serialize & write to a file
        try (OutputStream outputStream = new FileOutputStream("user.bin")) {
            user1.writeTo(outputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Deserialize from bytes
        try {
            UserApiProto.User user2 = UserApiProto.User.parseFrom(bytes);
            System.out.println(user2);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("********************************************");

        // Deserialize from file
        try (InputStream inputStream = new FileInputStream("user.bin")) {
            UserApiProto.User user2 = UserApiProto.User.parseFrom(inputStream);
            System.out.println(user2);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private static void defaultValues() {
        UserApiProto.User defaultUser = UserApiProto.User.newBuilder().build();

        // Default value
        System.out.println(defaultUser.getId());
        System.out.println(defaultUser.getName());
        System.out.println(defaultUser.getNickname());
        System.out.println(defaultUser.getEmail());

        // Distinguish default values and empty string for optional

        System.out.println("defaultUser.hasNickname() : " + defaultUser.hasNickname());
        System.out.println("defaultUser.getNickname() : " + defaultUser.getNickname());

        UserApiProto.User user1 = UserApiProto.User.newBuilder()
                .setNickname("")
                .build();
        System.out.println("user1.hasNickname() : " + user1.hasNickname());
        System.out.println("user1.getNickname() : " + user1.getNickname());

        UserApiProto.User user2 = UserApiProto.User.newBuilder()
                .setNickname("Jahrom")
                .build();
        System.out.println("user2.hasNickname() : " + user2.hasNickname());
        System.out.println("user2.getNickname() : " + user2.getNickname());


        // Clear
        UserApiProto.User.Builder builder = UserApiProto.User.newBuilder().setNickname("Jahrom");
        builder.clear();
        UserApiProto.User user3 = builder.build();
        System.out.println("user3.hasNickname() : " + user3.hasNickname());
        System.out.println("user3.getNickname() : " + user3.getNickname());
    }

    private static void repeatedField(){
        UserApiProto.User user = UserApiProto.User.newBuilder().build();

        // It must show an empty list, not null
        List<String> tags = user.getTagsList();
        System.out.println("size : " + tags.size());
        System.out.println("tags : " + tags);

        // Keep order, just can modify using builder
        UserApiProto.User.Builder builder = UserApiProto.User.newBuilder();
        builder.addTags("tag1")
                .addTags("tag2");
        builder.addAllTags(List.of("tag3", "tag4"));
        UserApiProto.User user2 = builder.build();
        System.out.println(user2.getTagsList());
    }

}
