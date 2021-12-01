package com.gmail.berndivader.mythicmobsext.compatibilitylib;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Skull;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

import com.google.common.collect.Multimap;

@SuppressWarnings("deprecation")
public class InventoryUtils extends NMSUtils
{
    public static int MAX_LORE_LENGTH = 24;
    public static int MAX_PROPERTY_DISPLAY_LENGTH = 50;
    public static UUID SKULL_UUID = UUID.fromString("3f599490-ca3e-49b5-8e75-78181ebf4232");

    public static boolean saveTagsToItem(ConfigurationSection tags, ItemStack item)
    {
        Object handle = getHandle(item);
        if (handle == null) return false;
        Object tag = getTag(handle);
        if (tag == null) return false;

        return addTagsToNBT(getMap(tags), tag);
    }

    public static boolean saveTagsToItem(Map<String, Object> tags, ItemStack item)
    {
        Object handle = getHandle(item);
        if (handle == null) return false;
        Object tag = getTag(handle);
        if (tag == null) return false;

        return addTagsToNBT(tags, tag);
    }

    public static boolean configureSkillItem(ItemStack skillItem, String skillClass, ConfigurationSection skillConfig) {
        if (skillItem == null) return false;
        Object handle = getHandle(skillItem);
        if (handle == null) return false;
        Object tag = getTag(handle);
        if (tag == null) return false;

        setMetaBoolean(tag, "skill", true);

        Object spellNode = InventoryUtils.getNode(skillItem, "spell");
        if (skillClass != null && spellNode != null) {
            InventoryUtils.setMeta(spellNode, "class", skillClass);
        }
        if (skillConfig == null) {
            return true;
        }

        if (skillConfig.getBoolean("undroppable", false)) {
            setMetaBoolean(tag, "undroppable", true);
        }
        if (skillConfig.getBoolean("keep", false)) {
            setMetaBoolean(tag, "keep", true);
        }
        boolean quickCast = skillConfig.getBoolean("quick_cast", true);
        if (!quickCast && spellNode != null) {
            InventoryUtils.setMetaBoolean(spellNode, "quick_cast", false);
        }

        return true;
    }

    public static boolean saveTagsToNBT(ConfigurationSection tags, Object node)
    {
        return saveTagsToNBT(tags, node, null);
    }

    public static boolean saveTagsToNBT(ConfigurationSection tags, Object node, Set<String> tagNames)
    {
        return saveTagsToNBT(getMap(tags), node, tagNames);
    }

    public static boolean addTagsToNBT(Map<String, Object> tags, Object node)
    {
        if (node == null) {
            getLogger().warning("Trying to save tags to a null node");
            return false;
        }
        if (!class_NBTTagCompound.isAssignableFrom(node.getClass())) {
            getLogger().warning("Trying to save tags to a non-CompoundTag");
            return false;
        }

        for (Map.Entry<String, Object> tag : tags.entrySet()) {
            Object value = tag.getValue();
            try {
                Object wrappedTag = wrapInTag(value);
                if (wrappedTag == null) continue;
                class_NBTTagCompound_setMethod.invoke(node, tag.getKey(), wrappedTag);
            } catch (Exception ex) {
                getLogger().log(Level.WARNING, "Error saving item data tag " + tag.getKey(), ex);
            }
        }

        return true;
    }
    
    public static boolean saveTagsToNBT(Map<String, Object> tags, Object node, Set<String> tagNames)
    {
        if (node == null) {
            getLogger().warning("Trying to save tags to a null node");
            return false;
        }
        if (!class_NBTTagCompound.isAssignableFrom(node.getClass())) {
            getLogger().warning("Trying to save tags to a non-CompoundTag");
            return false;
        }
        
        if (tagNames == null) {
            tagNames = tags.keySet();
        }

        // Remove tags that were not included
        Set<String> currentTags = getTagKeys(node);
        if (currentTags != null && !tagNames.containsAll(currentTags)) {
            // Need to copy this, getKeys returns a live list and bad things can happen.
            currentTags = new HashSet<>(currentTags);
        } else {
            currentTags = null;
        }
        
        for (String tagName : tagNames)
        {
            if (currentTags != null) currentTags.remove(tagName);
            Object value = tags.get(tagName);
            try {
                Object wrappedTag = wrapInTag(value);
                if (wrappedTag == null) continue;
                class_NBTTagCompound_setMethod.invoke(node, tagName, wrappedTag);
            } catch (Exception ex) {
                getLogger().log(Level.WARNING, "Error saving item data tag " + tagName, ex);
            }
        }

        // Finish removing any remaining properties
        if (currentTags != null) {
            for (String currentTag : currentTags) {
                removeMeta(node, currentTag);
            }
        }

        return true;
    }

    public static Object wrapInTag(Object value)
        throws IllegalAccessException, InvocationTargetException, InstantiationException {
        if (value == null) return null;
        Object wrappedValue = null;
        if (value instanceof Boolean) {
            wrappedValue = class_NBTTagByte_constructor.newInstance((byte)((boolean)value ? 1 : 0));
        } else if (value instanceof Double) {
            wrappedValue = class_NBTTagDouble_constructor.newInstance((Double)value);
        } else if (value instanceof Float) {
            wrappedValue = class_NBTTagFloat_constructor.newInstance((Float)value);
        } else if (value instanceof Integer) {
            wrappedValue = class_NBTTagInt_constructor.newInstance((Integer)value);
        } else if (value instanceof Long) {
            wrappedValue = class_NBTTagLong_constructor.newInstance((Long)value);
        } else if (value instanceof ConfigurationSection) {
            wrappedValue = class_NBTTagCompound_constructor.newInstance();
            saveTagsToNBT((ConfigurationSection)value, wrappedValue, null);
        } else if (value instanceof Map) {
            wrappedValue = class_NBTTagCompound_constructor.newInstance();
            @SuppressWarnings("unchecked")
            Map<String, Object> valueMap = (Map<String, Object>)value;
            addTagsToNBT(valueMap, wrappedValue);
        } else if (value instanceof Collection) {
            @SuppressWarnings("unchecked")
            Collection<Object> list = (Collection<Object>)value;
            Object listMeta = class_NBTTagList_constructor.newInstance();
            if (list.size() > 1 && list instanceof List) {
                @SuppressWarnings("unchecked")
                List<Object> checkList = (List<Object>)value;
                Object first = checkList.get(0);
                Object second = checkList.get(1);
                if (first instanceof String && !(second instanceof String)) {
                    list = new ArrayList<>();
                    for (int i = 1; i < checkList.size(); i++) {
                        if (first.equals("I")) {
                            list.add(convertToInteger(checkList.get(i)));
                        } else if (first.equals("L")) {
                            list.add(convertToLong(checkList.get(i)));
                        } else if (first.equals("B")) {
                            list.add(convertToByte(checkList.get(i)));
                        } else {
                            list.add(checkList.get(i));
                        }
                    }
                    if (first.equals("B")) {
                        wrappedValue = class_NBTTagByteArray_constructor.newInstance(makeByteArray((List<Object>)list));
                    } else if (first.equals("I") || class_NBTTagLongArray_constructor == null) {
                        wrappedValue = class_NBTTagIntArray_constructor.newInstance(makeIntArray((List<Object>)list));
                    } else if (first.equals("L")) {
                        wrappedValue = class_NBTTagLongArray_constructor.newInstance(makeLongArray((List<Object>)list));
                    }
                }
            }
            if (wrappedValue == null) {
                for (Object item : list) {
                    if (item != null) {
                        addToList(listMeta, wrapInTag(item));
                    }
                }
                wrappedValue = listMeta;
            }
        } else {
            wrappedValue = class_NBTTagString_consructor.newInstance(value.toString());
        }

        return wrappedValue;
    }

    protected static byte[] makeByteArray(List<Object> list) {
        byte[] a = new byte[list.size()];

        for (int i = 0; i < list.size(); ++i) {
            Byte b = (Byte)list.get(i);
            a[i] = b == null ? 0 : b;
        }

        return a;
    }

    protected static int[] makeIntArray(List<Object> list) {
        int[] a = new int[list.size()];

        for (int i = 0; i < list.size(); ++i) {
            Integer value = (Integer)list.get(i);
            a[i] = value == null ? 0 : value;
        }

        return a;
    }

    protected static long[]  makeLongArray(List<Object> list) {
        long[] a = new long[list.size()];

        for (int i = 0; i < list.size(); ++i) {
            Long l = (Long)list.get(i);
            a[i] = l == null ? 0 : l;
        }

        return a;
    }

    protected static Long convertToLong(Object o) {
        if (o == null) return null;
        if (o instanceof Long) return (Long)o;
        if (o instanceof Integer) return (long)(int)(Integer)o;
        if (o instanceof Byte) return (long)(Byte)o;
        if (o instanceof Double) return (long)(double)(Double)o;
        if (o instanceof String) return Long.parseLong((String)o);
        return null;
    }

    protected static Integer convertToInteger(Object o) {
        Long intVal = convertToLong(o);
        return intVal == null ? null : (int)(long)intVal;
    }

    protected static Byte convertToByte(Object o) {
        Long intVal = convertToLong(o);
        return intVal == null ? null : (byte)(long)intVal;
    }

    protected static Short convertToShort(Object o) {
        Long intVal = convertToLong(o);
        return intVal == null ? null : (short)(long)intVal;
    }

    protected static Double convertToDouble(Object o) {
        if (o == null) return null;
        if (o instanceof Double) return (Double)o;
        if (o instanceof Integer) return (double)(Integer)o;
        if (o instanceof Long) return (double)(long)(Long)o;
        if (o instanceof Byte) return (double)(Byte)o;
        if (o instanceof String) return Double.parseDouble((String)o);
        return null;
    }

    @SuppressWarnings("unchecked")
    public static Set<String> getTagKeys(Object tag) {
        if (tag == null || class_NBTTagCompound_getKeysMethod == null) {
            return null;
        }

        try {
            return (Set<String>) class_NBTTagCompound_getKeysMethod.invoke(tag);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static Object getMetaObject(Object tag, String key) {
        try {
            Object metaBase = class_NBTTagCompound_getMethod.invoke(tag, key);
            return getTagValue(metaBase);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static Object getTagValue(Object tag) throws IllegalAccessException, InvocationTargetException {
        if (tag == null) return null;
        Object value = null;
        if (class_NBTTagDouble.isAssignableFrom(tag.getClass())) {
            value = class_NBTTagDouble_dataField.get(tag);
        } else if (class_NBTTagInt.isAssignableFrom(tag.getClass())) {
            value = class_NBTTagInt_dataField.get(tag);
        } else if (class_NBTTagLong.isAssignableFrom(tag.getClass())) {
            value = class_NBTTagLong_dataField.get(tag);
        } else if (class_NBTTagFloat.isAssignableFrom(tag.getClass())) {
            value = class_NBTTagFloat_dataField.get(tag);
        } else if (class_NBTTagShort.isAssignableFrom(tag.getClass())) {
            value = class_NBTTagShort_dataField.get(tag);
        } else if (class_NBTTagByte.isAssignableFrom(tag.getClass())) {
            // This is kind of nasty. Really need a type-juggling container class for config properties.
            value = class_NBTTagByte_dataField.get(tag);
            if (value != null && value.equals((byte)0)) {
                value = false;
            } else if (value != null && value.equals((byte)1)) {
                value = true;
            }
        } else if (class_NBTTagList.isAssignableFrom(tag.getClass())) {
            List<?> items = (List<?>)class_NBTTagList_list.get(tag);
            List<Object> converted = new ArrayList<>();
            for (Object baseTag : items) {
                Object convertedBase = getTagValue(baseTag);
                if (convertedBase != null) {
                    converted.add(convertedBase);
                }
            }
            value = converted;
        } else if (class_NBTTagString.isAssignableFrom(tag.getClass())) {
            value = class_NBTTagString_dataField.get(tag);
        } else if (class_NBTTagCompound.isAssignableFrom(tag.getClass())) {
            Map<String, Object> compoundMap = new HashMap<>();
            Set<String> keys = getTagKeys(tag);
            for (String key : keys) {
                Object baseTag = class_NBTTagCompound_getMethod.invoke(tag, key);
                Object convertedBase = getTagValue(baseTag);
                if (convertedBase != null) {
                    compoundMap.put(key, convertedBase);
                }
            }
            value = compoundMap;
        }

        return value;
    }

    public static boolean inventorySetItem(Inventory inventory, int index, ItemStack item) {
        try {
            Method setItemMethod = class_CraftInventoryCustom.getMethod("setItem", Integer.TYPE, ItemStack.class);
            setItemMethod.invoke(inventory, index, item);
            return true;
        } catch(Throwable ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public static boolean setInventoryResults(Inventory inventory, ItemStack item) {
        try {
            Method getResultsMethod = inventory.getClass().getMethod("getResultInventory");
            Object inv = getResultsMethod.invoke(inventory);
            Method setItemMethod = inv.getClass().getMethod("setItem", Integer.TYPE, class_ItemStack);
            setItemMethod.invoke(inv, 0, getHandle(item));
            return true;
        } catch(Throwable ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public static ItemStack setSkullURL(ItemStack itemStack, String url) {
        try {
            // Using a fixed non-random UUID here so skulls of the same type can stack
            return setSkullURL(itemStack, new URL(url), SKULL_UUID);
        } catch (MalformedURLException e) {
            getLogger().log(Level.WARNING, "Malformed URL: " + url, e);
        }
        return itemStack;
    }

    public static ItemStack setSkullURLAndName(ItemStack itemStack, URL url, String ownerName, UUID id) {
        try {
            itemStack = makeReal(itemStack);
            Object skullOwner = createNode(itemStack, "SkullOwner");
            setMeta(skullOwner, "Name", ownerName);
            return setSkullURL(itemStack, url, id);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return itemStack;
    }

    public static ItemStack setSkullURL(ItemStack itemStack, URL url, UUID id) {
        // Old versions of Bukkit would NPE trying to save a skull without an owner name
        // So we'll use MHF_Question, why not.
        return setSkullURL(itemStack, url, id, "MHF_Question");
    }

    public static ItemStack setSkullURL(ItemStack itemStack, URL url, UUID id, String name) {
        try {
            if (CompatibilityUtils.isEmpty(itemStack)) {
                return itemStack;
            }

            Object gameProfile = class_GameProfile_constructor.newInstance(id, name);
            @SuppressWarnings("unchecked")
            Multimap<String, Object> properties = (Multimap<String, Object>)class_GameProfile_properties.get(gameProfile);
            if (properties == null) {
                return itemStack;
            }
            itemStack = makeReal(itemStack);
            if (CompatibilityUtils.isEmpty(itemStack)) {
                return itemStack;
            }

            String textureJSON = "{textures:{SKIN:{url:\"" + url + "\"}}}";
            String encoded = Base64Coder.encodeString(textureJSON);

            properties.put("textures", class_GameProfileProperty_noSignatureConstructor.newInstance("textures", encoded));

            ItemMeta skullMeta = itemStack.getItemMeta();
            setSkullProfile(skullMeta, gameProfile);

            itemStack.setItemMeta(skullMeta);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return itemStack;
    }

    public static String getSkullURL(ItemStack skull) {
        return SkinUtils.getProfileURL(getSkullProfile(skull.getItemMeta()));
    }
    
    @Deprecated
    public static String getPlayerSkullURL(String playerName)
    {
        return SkinUtils.getOnlineSkinURL(playerName);
    }

    public static Object getSkullProfile(ItemMeta itemMeta)
    {
        Object profile = null;
        try {
            if (itemMeta == null || !class_CraftMetaSkull.isInstance(itemMeta)) return null;
            profile = class_CraftMetaSkull_profile.get(itemMeta);
        } catch (Exception ex) {

        }
        return profile;
    }

    public static boolean setSkullProfile(ItemMeta itemMeta, Object data)
    {
        try {
            if (itemMeta == null || !class_CraftMetaSkull.isInstance(itemMeta)) return false;
            class_CraftMetaSkull_profile.set(itemMeta, data);
            return true;
        } catch (Exception ex) {

        }
        return false;
    }

    public static Object getSkullProfile(Skull state)
    {
        Object profile = null;
        try {
            if (state == null || !class_CraftSkull.isInstance(state)) return false;
            profile = class_CraftSkull_profile.get(state);
        } catch (Exception ex) {

        }
        return profile;
    }

    public static boolean setSkullProfile(Skull state, Object data)
    {
        try {
            if (state == null || !class_CraftSkull.isInstance(state)) return false;
            class_CraftSkull_profile.set(state, data);
            return true;
        } catch (Exception ex) {

        }

        return false;
    }

    public static void wrapText(String text, Collection<String> list)
    {
        wrapText(text, MAX_LORE_LENGTH, list);
    }

    public static void wrapText(String text, String prefix, Collection<String> list)
    {
        wrapText(text, prefix, MAX_LORE_LENGTH, list);
    }

    public static void wrapText(String text, int maxLength, Collection<String> list)
    {
        wrapText(text, "", maxLength, list);
    }

    public static void wrapText(String text, String prefix, int maxLength, Collection<String> list)
    {
        String colorPrefix = "";
        String[] lines = StringUtils.split(text, "\n\r");
        for (String line : lines) {
            line = prefix + line;
            while (line.length() > maxLength)
            {
                int spaceIndex = line.lastIndexOf(' ', maxLength);
                if (spaceIndex <= 0) {
                    list.add(colorPrefix + line);
                    return;
                }
                String colorText = colorPrefix + line.substring(0, spaceIndex);
                colorPrefix = ChatColor.getLastColors(colorText);
                list.add(colorText);
                line = line.substring(spaceIndex);
            }

            list.add(colorPrefix + line);
        }
    }

    public static boolean hasItem(Inventory inventory, String itemName) {
        ItemStack itemStack = getItem(inventory, itemName);
        return itemStack != null;
    }

    public static ItemStack getItem(Inventory inventory, String itemName) {
        if (inventory == null) {
            return null;
        }
        ItemStack[] items = inventory.getContents();
        for (ItemStack item : items) {
            if (item != null && item.hasItemMeta()) {
                String displayName = item.getItemMeta().getDisplayName();
                if (displayName != null && displayName.equals(itemName)) {
                    return item;
                }
            }
        }
        return null;
    }

    public static void openSign(Player player, Location signBlock) {
        try {
            Object tileEntity = getTileEntity(signBlock);
            Object playerHandle = getHandle(player);
            if (tileEntity != null && playerHandle != null) {
                class_EntityPlayer_openSignMethod.invoke(playerHandle, tileEntity);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void makeKeep(ItemStack itemStack) {
        setMetaBoolean(itemStack, "keep", true);
    }

    public static boolean isKeep(ItemStack itemStack) {
        return hasMeta(itemStack, "keep");
    }
    
    public static void applyAttributes(ItemStack item, ConfigurationSection attributeConfig, String slot) {
        if (item == null) return;
        CompatibilityUtils.removeItemAttributes(item);
        if (attributeConfig == null) return;
        Collection<String> attributeKeys = attributeConfig.getKeys(false);
        for (String attributeKey : attributeKeys)
        {
            // Note that there is some duplication here with BaseMageModifier.getAttributeModifiers
            // We want to keep the syntax the same, however there are some fundamental differences in how
            // entity vs item modifiers work, enough that it makes sense to keep the two separate
            try {
                double value = 0;
                int operation = 0;
                ConfigurationSection attributeConfiguration = attributeConfig.getConfigurationSection(attributeKey);
                if (attributeConfiguration != null) {
                    attributeKey = attributeConfiguration.getString("attribute", attributeKey);
                    value = attributeConfiguration.getDouble("value");
                    slot = attributeConfiguration.getString("slot", slot);
                    String operationKey = attributeConfiguration.getString("operation");
                    if (operationKey != null && !operationKey.isEmpty()) {
                        try {
                            AttributeModifier.Operation eOperation = AttributeModifier.Operation.valueOf(operationKey.toUpperCase());
                            operation = eOperation.ordinal();
                        } catch (Exception ex) {
                            getLogger().warning("Invalid operation " + operationKey);
                        }
                    }
                } else {
                    value = attributeConfig.getDouble(attributeKey);
                }
                Attribute attribute = Attribute.valueOf(attributeKey.toUpperCase());
                if (!CompatibilityUtils.setItemAttribute(item, attribute, value, slot, operation)) {
                    getLogger().warning("Failed to set attribute: " + attributeKey);
                }
            } catch (Exception ex) {
                getLogger().warning("Invalid attribute: " + attributeKey);
            }
        }
    }
    
    public static void applyEnchantments(ItemStack item, ConfigurationSection enchantConfig) {
        if (item == null) return;

        Set<Enchantment> keep = null;
        if (enchantConfig != null) {
            keep = new HashSet<>();
            Collection<String> enchantKeys = enchantConfig.getKeys(false);
            for (String enchantKey : enchantKeys)
            {
                try {
                    Enchantment enchantment = Enchantment.getByName(enchantKey.toUpperCase());
                    item.addUnsafeEnchantment(enchantment, enchantConfig.getInt(enchantKey));
                    keep.add(enchantment);
                } catch (Exception ex) {
                    getLogger().warning("Invalid enchantment: " + enchantKey);
                }
            }
        }
        Collection<Enchantment> existing = new ArrayList<>(item.getEnchantments().keySet());
        for (Enchantment enchantment : existing) {
            if (keep == null || !keep.contains(enchantment)) {
                item.removeEnchantment(enchantment);
            }
        }
    }

    public static String describeProperty(Object property) {
        return describeProperty(property, 0);
    }

    public static String describeProperty(Object property, int maxLength) {
        if (property == null) return "(Empty)";
        String propertyString;
        if (property instanceof ConfigurationSection) {
            ConfigurationSection section = (ConfigurationSection)property;
            Set<String> keys = section.getKeys(false);
            StringBuilder full = new StringBuilder("{");
            boolean first = true;
            for (String key : keys) {
                if (!first) {
                    full.append(',');
                }
                first = false;
                full.append(key).append(':').append(describeProperty(section.get(key)));
            }
            propertyString = full.append('}').toString();
        } else {
            propertyString = property.toString();
        }
        if (maxLength > 0 && propertyString.length() > maxLength - 3) {
            propertyString = propertyString.substring(0, maxLength - 3) + "...";
        }
        return propertyString;
    }

    @SuppressWarnings("EqualsReference")
    public static boolean isSameInstance(ItemStack one, ItemStack two) {
        return one == two;
    }

    public static int getMapId(ItemStack mapItem) {
        if (isCurrentVersion()) {
            return getMetaInt(mapItem, "map", 0);
        }

        return mapItem.getDurability();
    }

    public static void setMapId(ItemStack mapItem, int id) {
        if (isCurrentVersion()) {
            setMetaInt(mapItem, "map", id);
        } else {
            mapItem.setDurability((short)id);
        }
    }

    public static void convertIntegers(Map<String, Object> m) {
        for (Map.Entry<String, Object> entry : m.entrySet()) {
            Object value = entry.getValue();
            if (value != null && value instanceof Double) {
                double d = (Double) value;
                if (d == (int)d) {
                    entry.setValue((int)d);
                }
            } else if (value != null && value instanceof Float) {
                float f = (Float) value;
                if (f == (int)f) {
                    entry.setValue((int)f);
                }
            } else if (value != null && value instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> map = (Map<String, Object>)value;
                convertIntegers(map);
            }
        }
    }
}