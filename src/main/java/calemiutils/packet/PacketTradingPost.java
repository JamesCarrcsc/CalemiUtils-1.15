package calemiutils.packet;

import calemiutils.config.CUConfig;
import calemiutils.tileentity.TileEntityTradingPost;
import calemiutils.util.Location;
import calemiutils.util.helper.ChatHelper;
import calemiutils.util.helper.ItemHelper;
import calemiutils.util.helper.StringHelper;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketTradingPost {

    private String command;
    private BlockPos pos;
    private int stackStrSize, nbtStrSize;
    private String stack, nbt;
    private boolean buyMode;
    private int amount;
    private int price;

    public PacketTradingPost () {}

    /**
     * Used to sync the data of the Trading Post.
     * @param command Used to determine the type of packet to send.
     * @param pos The Block position of the Tile Entity.
     * @param stackStrSize The String size of the Item Stack packet.
     * @param nbtStrSize The String size of the Item Stack's NBT packet.
     * @param stack The Item Stack's string conversion.
     * @param nbt The Item Stack's NBT string conversion.
     * @param buyMode The state of the buyMode option.
     * @param amount The number of the amount option.
     * @param price The number of the price option.
     */
    public PacketTradingPost (String command, BlockPos pos, int stackStrSize, int nbtStrSize, String stack, String nbt, boolean buyMode, int amount, int price) {
        this.command = command;
        this.pos = pos;
        this.stackStrSize = stackStrSize;
        this.nbtStrSize = nbtStrSize;
        this.stack = stack;
        this.nbt = nbt;
        this.buyMode = buyMode;
        this.amount = amount;
        this.price = price;
    }

    /**
     * Use this constructor to broadcast.
     */
    public PacketTradingPost (String command, BlockPos pos) {
        this(command, pos, 0, 0, "", "", false, 0, 0);
    }

    /**
     * Use this constructor to sync the current mode.
     */
    public PacketTradingPost (String command, BlockPos pos, boolean buyMode) {
        this(command, pos, 0, 0, "", "", buyMode, 0, 0);
    }

    /**
     * Use this constructor to sync the stack for sale.
     */
    public PacketTradingPost (String command, BlockPos pos, String stack, String nbt) {
        this(command, pos, stack.length(), nbt.length(), stack, nbt, false, 0, 0);
    }

    /**
     * Use this constructor to sync the options
     */
    public PacketTradingPost (String command, BlockPos pos, int amount, int price) {
        this(command, pos, 0, 0, "", "", false, amount, price);
    }

    public PacketTradingPost (PacketBuffer buf) {
        this.command = buf.readString(11).trim();
        pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
        this.stackStrSize = buf.readInt();
        this.nbtStrSize = buf.readInt();
        this.stack = buf.readString(stackStrSize);
        this.nbt = buf.readString(nbtStrSize);
        this.buyMode = buf.readBoolean();
        this.amount = buf.readInt();
        this.price = buf.readInt();
    }

    public void toBytes (PacketBuffer buf) {
        buf.writeString(command, 11);
        buf.writeInt(pos.getX());
        buf.writeInt(pos.getY());
        buf.writeInt(pos.getZ());
        buf.writeInt(stackStrSize);
        buf.writeInt(nbtStrSize);
        buf.writeString(stack, stackStrSize);
        buf.writeString(nbt, nbtStrSize);
        buf.writeBoolean(buyMode);
        buf.writeInt(amount);
        buf.writeInt(price);
    }

    public void handle (Supplier<NetworkEvent.Context> ctx) {

        ctx.get().enqueueWork(() -> {

            ServerPlayerEntity player = ctx.get().getSender();

            if (player != null) {

                Location location = new Location(player.world, pos);

                //Checks if the Tile Entity is a Trading Post.
                if (location.getTileEntity() instanceof TileEntityTradingPost) {

                    TileEntityTradingPost tePost = (TileEntityTradingPost) location.getTileEntity();

                    //Handles broadcasting.
                    if (command.equalsIgnoreCase("broadcast")) {

                        if (tePost.hasValidTradeOffer)  {

                            if (tePost.broadcastDelay <= 0) {

                                StringBuilder message = new StringBuilder();
                                message.append(player.getDisplayName().getFormattedText());
                                message.append(" is ").append(tePost.buyMode ? "buying" : "selling");
                                message.append(" x").append(tePost.amountForSale);
                                message.append(" ").append(TextFormatting.AQUA).append(tePost.getStackForSale().getDisplayName().getFormattedText());
                                message.append(TextFormatting.RESET).append(" for ").append(tePost.salePrice > 0 ? TextFormatting.GOLD + StringHelper.printCurrency(tePost.salePrice) : "free");
                                message.append(TextFormatting.RESET).append(" at ").append(TextFormatting.AQUA).append(tePost.getLocation().toString());

                                ChatHelper.broadcastMessage(player.world, message.toString());

                                tePost.broadcastDelay = CUConfig.misc.tradingPostBroadcastDelay.get();
                            }

                            else tePost.getUnitName(player).printMessage(TextFormatting.RED, "You must wait " + StringHelper.printCommas(tePost.broadcastDelay) + " second(s) before broadcasting again!");
                        }

                        else tePost.getUnitName(player).printMessage(TextFormatting.RED, "The Trading Post is not set up properly!");
                    }

                    //Handles syncing the buyMode option.
                    if (command.equalsIgnoreCase("syncmode")) {
                        tePost.buyMode = this.buyMode;
                    }

                    //Handles syncing the Item Stack for sale.
                    else if (command.equalsIgnoreCase("syncstack")) {

                        ItemStack stackForSale = ItemHelper.getStackFromString(stack);

                        if (!nbt.isEmpty()) {
                            ItemHelper.attachNBTFromString(stackForSale, nbt);
                        }

                        tePost.setStackForSale(stackForSale);
                    }

                    //Handles syncing the options on the server.
                    else if (command.equalsIgnoreCase("syncoptions")) {
                        tePost.amountForSale = this.amount;
                        tePost.salePrice = this.price;
                    }

                    tePost.markForUpdate();
                }
            }
        });

        ctx.get().setPacketHandled(true);
    }
}
