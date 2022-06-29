package com.fabbe50.bettertooltips;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.tag.ItemTags;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.Collection;
import java.util.List;

public class BetterTooltips implements ModInitializer {
    @Override
    public void onInitialize() {
        ServerPlayNetworking.registerGlobalReceiver(Constants.ITEM_TAG_PACKET_ID, (server, player, handler, buf, responseSender) -> {
            ItemStack stack = buf.readItemStack();
            List<TagKey<Item>> tags = stack.streamTags().toList();
            PacketByteBuf newBuf = PacketByteBufs.create();
            StringBuilder builder = new StringBuilder();
            for (TagKey<Item> tag : tags) {
                builder.append(tag.id().toString()).append("\n");
            }
            newBuf.writeByteArray(builder.toString().getBytes());
            ServerPlayNetworking.send(player, Constants.ITEM_TAG_PACKET_ID, newBuf);
        });
    }
}
