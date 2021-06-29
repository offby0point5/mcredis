package com.github.offby0point5.mcredis.datatype;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;

import java.util.Collections;
import java.util.List;

public class ItemStack {
    @Expose public final String material;
    @Expose public final int amount;
    @Expose public final boolean glowing;
    @Expose public final String name;
    @Expose public final List<String> lore;

    private ItemStack(Builder builder) {
        this.material = builder.material;
        this.amount = builder.amount;
        this.glowing = builder.glowing;
        this.name = builder.name;
        this.lore = builder.lore;
    }

    public static ItemStack deserialize(String serialized) {
        return new Gson().fromJson(serialized, ItemStack.class);
    }

    public String serialize() {
        return new Gson().toJson(this);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ItemStack) {
            ItemStack other = (ItemStack) o;
            return (this.material.equals(other.material) &&
                    this.amount == other.amount &&
                    this.glowing == other.glowing &&
                    this.name.equals(other.name) &&
                    this.lore.equals(other.lore));
        }
        return false;
    }

    public static class Builder {
        private final String material;
        private final String name;

        private int amount = 1;
        private boolean glowing = false;
        private List<String> lore = Collections.emptyList();

        public Builder(String material, String name) {
            this.material = material;
            this.name = name;
        }

        public Builder amount(int amount) {
            this.amount = amount;
            return this;
        }

        public Builder glowing(boolean glowing) {
            this.glowing = glowing;
            return this;
        }

        public Builder lore(List<String> lore) {
            this.lore = lore;
            return this;
        }

        public ItemStack build() {
            return new ItemStack(this);
        }
    }
}
