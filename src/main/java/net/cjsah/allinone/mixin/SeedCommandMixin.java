package net.cjsah.allinone.mixin;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.SeedCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(SeedCommand.class)
public class SeedCommandMixin {
    /**
     * @reason 去除 seed 指令权限
     * @author Cjsah
     */
    @Overwrite
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, boolean dedicated) {
        dispatcher.register(CommandManager.literal("seed").executes((context) -> {
            long l = context.getSource().getWorld().getSeed();
            Text text = Texts.bracketed((new LiteralText(String.valueOf(l))).styled((style) ->
                    style.withColor(Formatting.GREEN)
                            .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, String.valueOf(l)))
                            .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslatableText("chat.copy.click")))
                            .withInsertion(String.valueOf(l))
            ));
            context.getSource().sendFeedback(new TranslatableText("commands.seed.success", text), false);
            return (int)l;
        }));
    }

}
