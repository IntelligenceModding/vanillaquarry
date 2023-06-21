package de.unhappycodings.quarry.common.container.base;


import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModEditBox extends EditBox {

    public ModEditBox(Font font, int x, int y, int widthX, int widthY, Component component) {
        super(font, x, y, widthX, widthY, component);
    }

}
