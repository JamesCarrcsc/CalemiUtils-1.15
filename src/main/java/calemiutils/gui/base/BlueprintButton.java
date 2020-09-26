package calemiutils.gui.base;

import calemiutils.util.helper.ScreenHelper;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;

@OnlyIn(Dist.CLIENT)
public class BlueprintButton extends Button {

    private final int id;
    private final ScreenRect rect;
    private final ItemStack stack;
    private final ItemRenderer itemRender;

    public BlueprintButton (int id, int x, int y, ItemRenderer itemRender, ItemStack stack, Button.IPressable pressable) {
        super(x, y, 16, 16, "", pressable);
        this.id = id;
        rect = new ScreenRect(this.x, this.y, width, height);
        this.stack = stack;
        this.itemRender = itemRender;
    }

    @Override
    public void renderButton (int mouseX, int mouseY, float partialTicks) {

        if (this.visible && active) {

            isHovered = rect.contains(mouseX, mouseY);

            ScreenHelper.drawItemStack(itemRender, stack, rect.x, rect.y);
            ScreenHelper.drawHoveringTextBox(mouseX, mouseY, 150, rect, DyeColor.byId(id).getName().toUpperCase());

            GL11.glColor4f(1, 1, 1, 1);
        }
    }
}