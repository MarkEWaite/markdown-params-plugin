package io.jenkins.plugins.markdownparams;

import static org.junit.Assert.*;

import org.junit.Test;

public class ItemTest {

    @Test
    public void testGetBullet() {
        Item item1 = new Item(1, "*", true, "Checkbox Item");
        assertEquals("*", item1.getBullet());

        Item item2 = new Item(1, "1.", false, "Checkbox Item");
        assertEquals("1.", item2.getBullet());
    }

    @Test
    public void testGetLevel() {
        Item item1 = new Item(1, "*", true, "Checkbox Item");
        assertEquals(1, item1.getLevel());

        Item item2 = new Item(2, "1.", false, "Checkbox Item");
        assertEquals(2, item2.getLevel());
    }

    @Test
    public void testIsCheckbox() {
        Item item1 = new Item(1, "*", true, "Checkbox Item");
        assertTrue(item1.isCheckbox());

        Item item2 = new Item(1, "*", false, "Checkbox Item");
        assertTrue(item2.isCheckbox());

        Item item3 = new Item(1, "1.", false, "Checkbox Item");
        assertTrue(item3.isCheckbox());

        Item item4 = new Item(1, "*", null, "Not a checkbox Item");
        assertFalse(item4.isCheckbox());
    }

    @Test
    public void testIsOrdered() {
        Item item1 = new Item(1, "*", "Not ordered Item");
        assertFalse(item1.isOrdered());

        Item item2 = new Item(1, "-", "Not ordered Item");
        assertFalse(item2.isOrdered());

        Item item3 = new Item(1, "+", "Not ordered Item");
        assertFalse(item3.isOrdered());

        Item item4 = new Item(1, "1.", "Ordered Item");
        assertTrue(item4.isOrdered());
    }

    @Test
    public void testIsUnordered() {
        Item item1 = new Item(1, "*", "Not ordered Item");
        assertTrue(item1.isUnordered());

        Item item2 = new Item(1, "-", "Not ordered Item");
        assertTrue(item2.isUnordered());

        Item item3 = new Item(1, "+", "Not ordered Item");
        assertTrue(item3.isUnordered());

        Item item4 = new Item(1, "1.", "Ordered Item");
        assertFalse(item4.isUnordered());
    }

    @Test
    public void testIsChecked() {
        Item item1 = new Item(1, "Checkbox Item", true);
        assertTrue(item1.isChecked());

        Item item2 = new Item(1, "Checkbox Item", false);
        assertFalse(item2.isChecked());

        Item item3 = new Item(1, "*", "Checkbox Item");
        assertFalse(item3.isChecked());
    }
}
