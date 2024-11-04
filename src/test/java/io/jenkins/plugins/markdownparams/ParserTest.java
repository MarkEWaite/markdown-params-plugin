package io.jenkins.plugins.markdownparams;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class ParserTest {

    @Test
    public void testGetCheckboxItemsOf() {
        Parser parser = new Parser("# Title\n- [x] Checkbox item 1\n* [ ] Checkbox item 2");
        List<String> checkboxItems = parser.getCheckboxItemsOf("Title");
        assertEquals(2, checkboxItems.size());
        assertEquals("Checkbox item 1", checkboxItems.get(0));
        assertEquals("Checkbox item 2", checkboxItems.get(1));
    }

    @Test
    public void testGetCheckedItemsOf() {
        Parser parser = new Parser("# Title\n- [x] Checkbox item 1\n* [ ] Checkbox item 2");
        List<String> checkedItems = parser.getCheckedItemsOf("Title");
        assertEquals(1, checkedItems.size());
        assertEquals("Checkbox item 1", checkedItems.get(0));
    }

    @ParameterizedTest
    @CsvSource({
        "'# Title\n- [ ] Checkbox item 1\n* [ ] Checkbox item 2', false, true",
        "'# Title\n- [x] Checkbox item 1\n* [x] Checkbox item 2', true, false",
        "'# Title\n- [x] Checkbox item 1\n* [ ] Checkbox item 2', false, false",
        "'# Title\n No items', true, true"
    })
    void testAllAndNoneItemsChecked(String input, boolean expectedAllChecked, boolean expectedNoneChecked) {
        Parser parser = new Parser(input);
        assertEquals(expectedAllChecked, parser.isAllItemsCheckedOf("Title"));
        assertEquals(expectedNoneChecked, parser.isNoneItemsCheckedOf("Title"));
    }

    @Test
    public void testGetUncheckedItemsOf() {
        Parser parser = new Parser("# Title\n- [x] Checkbox item 1\n* [ ] Checkbox item 2");
        List<String> uncheckedItems = parser.getUncheckedItemsOf("Title");
        assertEquals(1, uncheckedItems.size());
        assertEquals("Checkbox item 2", uncheckedItems.get(0));
    }

    @Test
    public void testGetListItemsOf() {
        Parser parser = new Parser("# Title\n- List item 1\n* List item 2");
        List<String> listItems = parser.getUnorderedListItemsOf("Title");
        assertEquals(2, listItems.size());
        assertEquals("List item 1", listItems.get(0));
        assertEquals("List item 2", listItems.get(1));
    }

    @Test
    public void testGetOrderedListItemsOf() {
        Parser parser = new Parser("# Title\n1. Ordered item 1\n2. Ordered item 2");
        List<String> orderedItems = parser.getOrderedListItemsOf("Title");
        assertEquals(2, orderedItems.size());
        assertEquals("Ordered item 1", orderedItems.get(0));
        assertEquals("Ordered item 2", orderedItems.get(1));
    }

    @Test
    public void testMixOfItems() {
        Parser parser =
                new Parser("# Title\n1. Ordered item\n* Unordered item\n- [ ] unchecked item\n* [x] checked item");
        List<String> orderedItems = parser.getOrderedListItemsOf("Title");
        assertEquals(1, orderedItems.size());
        assertEquals("Ordered item", orderedItems.get(0));

        List<String> listItems = parser.getUnorderedListItemsOf("Title");
        assertEquals(1, listItems.size());
        assertEquals("Unordered item", listItems.get(0));

        List<String> checkedItems = parser.getCheckedItemsOf("Title");
        assertEquals(1, checkedItems.size());
        assertEquals("checked item", checkedItems.get(0));

        List<String> uncheckedItems = parser.getUncheckedItemsOf("Title");
        assertEquals(1, uncheckedItems.size());
        assertEquals("unchecked item", uncheckedItems.get(0));

        List<String> checkboxItems = parser.getCheckboxItemsOf("Title");
        assertEquals(2, checkboxItems.size());
        assertEquals("unchecked item", checkboxItems.get(0));
        assertEquals("checked item", checkboxItems.get(1));
    }

    @Test
    public void testUntitledItems() {
        Parser parser = new Parser("* an item");
        List<String> items = parser.getUnorderedListItemsOf("");
        assertEquals(1, items.size());
        assertEquals("an item", items.get(0));
    }

    @Test
    public void testUntitledAndTitleItems() {
        Parser parser = new Parser("* an item\n ##Title\n* another item");
        List<String> items = parser.getUnorderedListItemsOf("");
        assertEquals(2, items.size());
        assertEquals("an item", items.get(0));
        assertEquals("another item", items.get(1));
    }
}
