package com.example.testproject;

import com.example.testproject.proto.versioning.VersioningProto.*;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class VersioningProtoTest {

    /**
     * new fields added to message
     */
    @Test
    public void newFieldAdded_forwardCompat() throws Exception {
        UserV2 v2 = UserV2.newBuilder()
                .setId(1)
                .setName("Mahmood")
                .setGender(GenderV2.MALE_V2)
                .setNickname("Employee")
                .build();

        byte[] bytes = v2.toByteArray();

        // old client still uses v1
        UserV1 v1 = UserV1.parseFrom(bytes);
        assertThat(v1.getId()).isEqualTo(1);
        assertThat(v1.getName()).isEqualTo("Mahmood");
        // nickname is ignored
        System.out.println("[USER V1]" + v1);
    }

    /**
     * Removed Fields
     */
    @Test
    public void removeField_backwardCompat() throws Exception {
        UserV1 v1 = UserV1.newBuilder()
                .setId(2)
                .setName("Old")
                .setGender(GenderV1.FEMALE_V1)
                .build();

        byte[] bytes = v1.toByteArray();

        // New client uses version 2
        UserV2 v2 = UserV2.parseFrom(bytes);
        assertThat(v2.getId()).isEqualTo(2);
        assertThat(v2.getName()).isEqualTo("Old");
        System.out.println("[USER V2]" + v2);
    }

    /**
     * Enum evolution
     */
    @Test
    public void enumEvolution_unknownValue() throws Exception {
        UserV2 v2 = UserV2.newBuilder()
                .setId(3)
                .setName("EnumUser")
                .setGender(GenderV2.OTHER_V2)
                .build();

        byte[] bytes = v2.toByteArray();

        // Old enum only knows 0,1,2
        UserV1 v1 = UserV1.parseFrom(bytes);

        assertThat(v1.getId()).isEqualTo(3);
        assertThat(v1.getGender()).isEqualTo(GenderV1.UNRECOGNIZED);
        System.out.println("[USER V1]" + v1);
    }

    /**
     * backward & forward compatability
     */
    @Test
    public void backwardForwardCompatabilityRoundTrip() throws Exception {
        // New service A (V2) -> Old service B (V1) -> New service A (V2)
        UserV2 original = UserV2.newBuilder()
                .setId(4)
                .setName("RoundTrip")
                .setGender(GenderV2.MALE_V2)
                .setNickname("Nick")
                .build();

        byte[] bytes = original.toByteArray();

        UserV1 seenByOld = UserV1.parseFrom(bytes);
        byte[] reEmitted = seenByOld.toByteArray();

        UserV2 seenBack = UserV2.parseFrom(reEmitted);

        assertThat(seenBack.getId()).isEqualTo(4);
        assertThat(seenBack.getName()).isEqualTo("RoundTrip");
        assertThat(seenBack.getGender()).isEqualTo(GenderV2.MALE_V2);
        assertThat(seenBack.getNickname()).isEqualTo("Nick");
        System.out.println("[USER V2]" + seenBack);
    }
}
