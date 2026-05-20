package de.unhappycodings.quarry.common.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;

public class TextUtil {

    private static final String ELLIPSIS = "‥";

    public static String truncateWithEllipsis(String text, int maxWidth) {
        Font font = Minecraft.getInstance().font;
        if (text == null) return "";

        if (font.width(text) <= maxWidth) {
            return text;
        }

        int ellipsisWidth = font.width(ELLIPSIS);

        StringBuilder result = new StringBuilder();

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);

            String test = result.toString() + c;

            if (font.width(test) + ellipsisWidth > maxWidth) {
                break;
            }

            result.append(c);
        }

        return result + ELLIPSIS;
    }
}