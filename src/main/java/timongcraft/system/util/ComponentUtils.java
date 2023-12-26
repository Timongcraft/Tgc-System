package timongcraft.system.util;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.function.Consumer;

public class ComponentUtils {

    public static void findAllTextComponents(BaseComponent component, Consumer<TextComponent> consumer) {
        if (component instanceof TextComponent textComponent)
            consumer.accept(textComponent);

        if (component.getExtra() == null) return;

        for (BaseComponent c : component.getExtra()) {
            if (c instanceof TextComponent textComponent)
                consumer.accept(textComponent);

            findAllTextComponents(c, consumer);
        }
    }

}
