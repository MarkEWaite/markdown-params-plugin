package io.jenkins.plugins.markdownparams;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.jenkinsci.plugins.scriptsecurity.sandbox.whitelists.Whitelisted;

public class Parser implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final Pattern REGEX_HEADER = Pattern.compile("^#+\\s+(.*)");
    private static final Pattern REGEX_ALT_HEADER = Pattern.compile("^(=+|-+)\\s*$");
    private static final Pattern REGEX_ALT_HEADER_NOT_VALID_TEXT = Pattern.compile("^((\\s*[#*->+]|\\d\\.)\\s+.*)$");
    private static final Pattern REGEX_CHECKBOX_ITEM = Pattern.compile("^(\\s*)([-\\*])\\s\\[([xX\\s])]\\s+(.*)");
    private static final Pattern REGEX_LIST_ITEM = Pattern.compile("^(\\s*)([-\\*])\\s+(.*)");
    private static final Pattern REGEX_ORDERED_LIST_ITEM = Pattern.compile("^(\\s*)(\\d)\\.\\s+(.*)");
    private final String md;

    private Map<String, Map<String, Item>> items;

    public Parser(String md) {
        this.md = md;
        parse();
    }

    private void parse() {
        items = new LinkedHashMap<>();
        List<String> lines = md.lines().collect(Collectors.toList());
        String lastHeader = "";
        String previousLine = "";
        boolean itWasAHeader = false;
        for (String line : lines) {
            String h = getHeader(line);
            if (h != null) {
                items.put(h, new LinkedHashMap<>());
                lastHeader = h;
                itWasAHeader = true;
            } else if (!itWasAHeader && getAlternativeHeader(line, previousLine) != null) {
                items.put(previousLine, new LinkedHashMap<>());
                lastHeader = previousLine;
                itWasAHeader = true;
            } else {
                itWasAHeader = false;
                Item it = getItem(line);
                if (it != null) {
                    items.computeIfAbsent(lastHeader, k -> new LinkedHashMap<>());
                    items.get(lastHeader).put(it.getName(), it);
                }
            }
            previousLine = line;
        }
    }

    private Item getItem(String line) {
        Item it = getCheckboxItem(line);
        if (it != null) {
            return it;
        }
        it = getListItem(line);
        if (it != null) {
            return it;
        }
        it = getOrderedItem(line);
        return it;
    }

    private Item getListItem(String line) {
        Matcher matcher = REGEX_LIST_ITEM.matcher(line);
        if (!matcher.find()) {
            return null;
        }
        return new Item(matcher.group(1).length(), matcher.group(2), matcher.group(3));
    }

    private Item getCheckboxItem(String line) {
        Matcher matcher = REGEX_CHECKBOX_ITEM.matcher(line);
        if (!matcher.find()) {
            return null;
        }
        return new Item(
                matcher.group(1).length(), matcher.group(2), matcher.group(3).equalsIgnoreCase("x"), matcher.group(4));
    }

    private Item getOrderedItem(String line) {
        Matcher matcher = REGEX_ORDERED_LIST_ITEM.matcher(line);
        if (!matcher.find()) {
            return null;
        }
        return new Item(matcher.group(1).length(), matcher.group(2), matcher.group(3));
    }

    private String getHeader(String line) {
        Matcher matcher = REGEX_HEADER.matcher(line);
        if (!matcher.find()) {
            return null;
        }
        return matcher.group(1);
    }

    private String getAlternativeHeader(String line, String previousLine) {
        Matcher matcher = REGEX_ALT_HEADER.matcher(line);
        if (!matcher.find()) {
            return null;
        }
        if (previousLine.trim().isEmpty()) {
            return null;
        }
        if (REGEX_ALT_HEADER_NOT_VALID_TEXT.matcher(previousLine).find()) {
            return null;
        }

        return matcher.group(1);
    }

    private List<String> searchAndFilter(String title, Predicate<Item> filterPredicate) {
        ArrayList<String> result = new ArrayList<>();
        for (Map.Entry<String, Map<String, Item>> entry : items.entrySet()) {
            if (entry.getKey().equals(title)) {
                for (Map.Entry<String, Item> it : entry.getValue().entrySet()) {
                    if (filterPredicate.test(it.getValue())) {
                        result.add(it.getKey());
                    }
                }
            }
        }
        return result;
    }

    @Whitelisted
    public List<String> getCheckboxItemsOf(String title) {
        Predicate<Item> filterPredicate = Item::isCheckbox;
        return searchAndFilter(title, filterPredicate);
    }

    @Whitelisted
    public List<String> getCheckedItemsOf(String title) {
        Predicate<Item> filterPredicate = it -> it.isCheckbox() && it.isChecked();
        return searchAndFilter(title, filterPredicate);
    }

    @Whitelisted
    public List<String> getUncheckedItemsOf(String title) {
        Predicate<Item> filterPredicate = it -> it.isCheckbox() && !it.isChecked();
        return searchAndFilter(title, filterPredicate);
    }

    @Whitelisted
    public boolean isAllItemsCheckedOf(String title) {
        Predicate<Item> filterPredicate = it -> it.isCheckbox() && !it.isChecked();
        return searchAndFilter(title, filterPredicate).isEmpty();
    }

    @Whitelisted
    public boolean isNoneItemsCheckedOf(String title) {
        Predicate<Item> filterPredicate = it -> it.isCheckbox() && it.isChecked();
        return searchAndFilter(title, filterPredicate).isEmpty();
    }

    @Whitelisted
    public List<String> getUnorderedListItemsOf(String title) {
        Predicate<Item> filterPredicate = it -> !it.isCheckbox() && !it.isOrdered();
        return searchAndFilter(title, filterPredicate);
    }

    @Whitelisted
    public List<String> getOrderedListItemsOf(String title) {
        Predicate<Item> filterPredicate = it -> !it.isCheckbox() && it.isOrdered();
        return searchAndFilter(title, filterPredicate);
    }
}
