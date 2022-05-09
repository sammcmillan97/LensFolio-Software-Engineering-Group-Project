package nz.ac.canterbury.seng302.portfolio.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserListControllerTests {

    @Autowired
    UserListController controller;

    @Test
    void testGoodPageOnNotNumber() {
        assertFalse(controller.goodPage("a string"));
    }

    @Test
    void testGoodPageOnNegative() {
        assertFalse(controller.goodPage("-10"));
    }

    @Test
    void testGoodPageOnZero() {
        assertFalse(controller.goodPage("0"));
    }

    @Test
    void testGoodPageOnOne() {
        assertTrue(controller.goodPage("1"));
    }

    @Test
    void testGoodPageOnLarge() {
        assertTrue(controller.goodPage("999999"));
    }

    @Test
    void testGoodSortTypeOnUsername() {
        assertTrue(controller.isGoodSortType("username"));
    }

    @Test
    void testBadSortTypeOnRandom() {
        assertFalse(controller.isGoodSortType("random"));
    }

    @Test
    void testGoodAscendingTypeOnTrue() {
        assertTrue(controller.isGoodAscending("true"));
    }

    @Test
    void testBadAscendingTypeOnRandom() {
        assertFalse(controller.isGoodAscending("random"));
    }

    @Test
    void testSortingSuffix() {
        assertEquals("?sortType=x&isAscending=y", controller.sortingSuffix("x", "y"));
    }

}
