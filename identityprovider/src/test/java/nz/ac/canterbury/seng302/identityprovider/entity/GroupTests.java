package nz.ac.canterbury.seng302.identityprovider.entity;

import com.google.protobuf.Timestamp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class GroupTests {

    User user1;
    Group group1;

    @BeforeEach
    void setup() {
        Instant time = Instant.now();
        Timestamp timestamp = Timestamp.newBuilder().setSeconds(time.getEpochSecond())
                .setNanos(time.getNano()).build();
        user1 = new User(1, "bauerjac", "Jack", "Brown", "Bauer", "Jack-Jack", "howdy", "HE/HIM", "jack@gmail.com", "password", timestamp);
        group1 = new Group("Team400", "Bad Request", 1);
    }


    @Test
    void givenGroupExists_addUserToGroup() {
        group1.addMember(user1);
        assertEquals(1, group1.getMembers().size());
        assertEquals(1, user1.getGroups().size());
    }

    @Test
    void givenGroupExists_removeUserFromGroup() {
        group1.addMember(user1);
        group1.removeMember(user1);
        assertEquals(0, group1.getMembers().size());
        assertEquals(0, user1.getGroups().size());
    }

    @Test
    void givenGroupExists_getGroupToString() {
        assertEquals("Group{shortName='Team400', longName='Bad Request', parentProject=1}", group1.toString());
    }

    @Test
    void givenGroupExists_getAndSetParentProject(){
        assertEquals(1, group1.getParentProject());
        group1.setParentProject(10);
        assertEquals(10, group1.getParentProject());
    }


}