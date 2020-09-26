package calemiutils.inventory.base;

import calemiutils.tileentity.base.CUItemHandler;
import calemiutils.tileentity.base.TileEntityInventoryBase;
import calemiutils.util.helper.LogHelper;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SlotFilter extends SlotItemHandler {

    private final List<Item> itemFilters;

    public SlotFilter (CUItemHandler itemHandler, int id, int x, int y, Item... filters) {
        super(itemHandler, id, x, y);
        this.itemFilters = new ArrayList<>();
        this.itemFilters.addAll(Arrays.asList(filters));
    }

    @Override
    public boolean isItemValid (ItemStack stack) {
        return isFilter(stack);
    }

    private boolean isFilter (ItemStack stack) {

        if (this.itemFilters != null) {
            for (Item itemFilter : this.itemFilters) {
                if (itemFilter == stack.getItem()) return true;
            }
        }

        return false;
    }
}
