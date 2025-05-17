package com.hlysine.create_connected.content.sequencedpulsegenerator;

import com.hlysine.create_connected.CCBlocks;
import com.hlysine.create_connected.CCGuiTextures;
import com.hlysine.create_connected.CCPackets;
import com.hlysine.create_connected.ConnectedLang;
import com.hlysine.create_connected.content.sequencedpulsegenerator.instructions.EndInstruction;
import com.hlysine.create_connected.content.sequencedpulsegenerator.instructions.Instruction;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.gui.widget.ScrollInput;
import com.simibubi.create.foundation.gui.widget.SelectionScrollInput;
import net.createmod.catnip.gui.AbstractSimiScreen;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Vector;
import java.util.function.Function;

import static com.hlysine.create_connected.content.sequencedpulsegenerator.SequencedPulseGeneratorBlockEntity.INSTRUCTION_CAPACITY;

public class SequencedPulseGeneratorScreen extends AbstractSimiScreen {

    private final ItemStack renderedItem = CCBlocks.SEQUENCED_PULSE_GENERATOR.asStack();
    private final CCGuiTextures background = CCGuiTextures.SEQUENCER;
    private IconButton confirmButton;
    private final SequencedPulseGeneratorBlockEntity be;

    private final ListTag compareTag;
    private final Vector<Instruction> instructions;

    private Vector<Vector<ScrollInput>> inputs;

    public SequencedPulseGeneratorScreen(SequencedPulseGeneratorBlockEntity be) {
        super(ConnectedLang.translateDirect("gui.sequenced_pulse_generator.title"));
        this.instructions = be.instructions;
        this.be = be;
        compareTag = Instruction.serializeAll(instructions);
    }

    @Override
    protected void init() {
        setWindowSize(background.width, background.height);
        setWindowOffset(-20, 0);
        super.init();

        int x = guiLeft;
        int y = guiTop;

        inputs = new Vector<>(INSTRUCTION_CAPACITY);
        for (int row = 0; row < inputs.capacity(); row++)
            inputs.add(new Vector<>(3));

        for (int row = 0; row < instructions.size(); row++)
            initInputsOfRow(row, x, y);

        confirmButton = new IconButton(x + background.width - 33, y + background.height - 24, AllIcons.I_CONFIRM);
        confirmButton.withCallback(this::onClose);
        addRenderableWidget(confirmButton);
    }

    public void initInputsOfRow(int row, int backgroundX, int backgroundY) {
        int x = backgroundX + 30;
        int y = backgroundY + 20;
        int rowHeight = 22;

        Vector<ScrollInput> rowInputs = inputs.get(row);
        removeWidgets(rowInputs);
        rowInputs.clear();
        Instruction instruction = instructions.get(row);

        ScrollInput type =
                new SelectionScrollInput(x, y + rowHeight * row, 50, 18)
                        .forOptions(Instruction.getOptions())
                        .calling(state -> instructionUpdated(row, state))
                        .setState(instruction.getOrdinal())
                        .titled(ConnectedLang.translateDirect("gui.sequenced_pulse_generator.instruction"));
        ScrollInput value =
                new ScrollInput(x + 58, y + rowHeight * row, 28, 18)
                        .calling(state -> instructions.get(row).setValue(state));
        ScrollInput signal =
                new ScrollInput(x + 88, y + rowHeight * row, 28, 18)
                        .withRange(0, 16)
                        .setState(instruction.getSignal())
                        .calling(state -> instructions.get(row).setSignal(state))
                        .titled(ConnectedLang.translateDirect("gui.sequenced_pulse_generator.signal"));

        rowInputs.add(type);
        rowInputs.add(value);
        rowInputs.add(signal);

        addRenderableWidgets(rowInputs);
        updateParamsOfRow(row);
    }

    public void updateParamsOfRow(int row) {
        Instruction instruction = instructions.get(row);
        Vector<ScrollInput> rowInputs = inputs.get(row);
        boolean hasValue = instruction.parameter != null;
        boolean hasSignal = instruction.hasSignal;

        ScrollInput value = rowInputs.get(1);
        value.active = value.visible = hasValue;
        if (hasValue) {
            value.withRange(instruction.parameter.minValue(), instruction.parameter.maxValue() + 1)
                    .titled(ConnectedLang.translateDirect(instruction.getParameterLangKey()))
                    .withShiftStep(instruction.parameter.shiftStepValue())
                    .setState(instruction.getValue())
                    .onChanged();
            if (instruction.parameter.stepFunction() != null) {
                value.withStepFunction(instruction.parameter.stepFunction());
            } else
                value.withStepFunction(value.standardStep());
        }

        ScrollInput signal = rowInputs.get(2);
        signal.active = signal.visible = hasSignal;
        if (hasSignal)
            signal.setState(instruction.getSignal());
    }

    @Override
    protected void renderWindow(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        int x = guiLeft;
        int y = guiTop;

        background.render(graphics, x, y);

        for (int row = 0; row < INSTRUCTION_CAPACITY; row++) {
            CCGuiTextures toDraw = CCGuiTextures.SEQUENCER_EMPTY;
            int yOffset = toDraw.height * row;

            toDraw.render(graphics, x, y + 16 + yOffset);
        }

        for (int row = 0; row < INSTRUCTION_CAPACITY; row++) {
            CCGuiTextures toDraw = CCGuiTextures.SEQUENCER_EMPTY;
            int yOffset = toDraw.height * row;
            if (row >= instructions.size()) {
                toDraw.render(graphics, x, y + 16 + yOffset);
                continue;
            }

            Instruction instruction = instructions.get(row);
            instruction.getBackground().render(graphics, x, y + 16 + yOffset);

            label(graphics, 36, yOffset - 1, ConnectedLang.translateDirect(instruction.getLangKey()));
            if (instruction.parameter != null) {
                Function<Integer, String> formatter = instruction.parameter.formatter();
                String text = formatter == null ? String.valueOf(instruction.getValue()) : formatter.apply(instruction.getValue());
                int stringWidth = font.width(text);
                label(graphics, 90 + (12 - stringWidth / 2), yOffset - 1, Component.literal(text));
            }
            if (instruction.hasSignal)
                label(graphics, 127, yOffset - 1, Component.literal(String.valueOf(instruction.getSignal())));
        }

        graphics.drawString(font, title, x + (background.width - 8) / 2 - font.width(title) / 2, y + 4, 0x592424, false);
        renderAdditional(graphics, mouseX, mouseY, partialTicks, x, y, background);
    }

    private void renderAdditional(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks, int guiLeft, int guiTop,
                                  CCGuiTextures background) {
        GuiGameElement.of(renderedItem).<GuiGameElement
                        .GuiRenderBuilder>at(guiLeft + background.width + 6, guiTop + background.height - 56, 100)
                .scale(5)
                .render(graphics);
    }

    private void label(GuiGraphics graphics, int x, int y, Component text) {
        graphics.drawString(font, text, guiLeft + x, guiTop + 26 + y, 0xFFFFEE);
    }

    public void sendPacket() {
        ListTag serialized = Instruction.serializeAll(instructions);
        if (serialized.equals(compareTag))
            return;
        PacketDistributor.sendToServer(new ConfigureSequencedPulseGeneratorPacket(be.getBlockPos(), serialized));
    }

    @Override
    public void removed() {
        sendPacket();
    }

    private void instructionUpdated(int index, int state) {
        Instruction newValue = Instruction.getByOrdinal(state);
        instructions.set(index, newValue);
        updateParamsOfRow(index);
        if (newValue.terminal) {
            for (int i = instructions.size() - 1; i > index; i--) {
                instructions.remove(i);
                Vector<ScrollInput> rowInputs = inputs.get(i);
                removeWidgets(rowInputs);
                rowInputs.clear();
            }
        } else {
            if (index + 1 < INSTRUCTION_CAPACITY && index + 1 == instructions.size()) {
                instructions.add(new EndInstruction());
                initInputsOfRow(index + 1, guiLeft, guiTop);
            }
        }
    }

}

