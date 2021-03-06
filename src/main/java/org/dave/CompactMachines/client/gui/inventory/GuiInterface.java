package org.dave.CompactMachines.client.gui.inventory;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import org.dave.CompactMachines.inventory.ContainerInterface;
import org.dave.CompactMachines.network.MessageHoppingModeChange;
import org.dave.CompactMachines.network.PacketHandler;
import org.dave.CompactMachines.reference.Names;
import org.dave.CompactMachines.reference.Textures;
import org.dave.CompactMachines.tileentity.TileEntityInterface;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiInterface extends GuiContainer {
	private TileEntityInterface tileEntityInterface;

	private static int tankHeight = 16;

	private GuiButton hoppingButton;

	public GuiInterface(InventoryPlayer inventoryPlayer, TileEntityInterface tileEntityInterface) {
		super(new ContainerInterface(inventoryPlayer, tileEntityInterface));
		this.tileEntityInterface = tileEntityInterface;
		xSize = 176;
		ySize = 187;
	}

	@Override
	public void initGui() {
		super.initGui();
		int buttonWidth = 80;

        int xStart = (width - xSize) / 2;
        int yStart = (height - ySize) / 2;

		hoppingButton = new GuiButton(0, xStart + (xSize / 2 - buttonWidth / 2), yStart + 17, buttonWidth, 20, "Import");
		this.buttonList.add(hoppingButton);
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		int nextHoppingMode = tileEntityInterface._hoppingmode + 1;
		if(nextHoppingMode > 3) { nextHoppingMode = 0; }

		MessageHoppingModeChange packet = new MessageHoppingModeChange(tileEntityInterface.coords, tileEntityInterface.side, nextHoppingMode);
		PacketHandler.INSTANCE.sendToServer(packet);
	}

    @Override
    protected void drawGuiContainerForegroundLayer(int x, int y)
    {
        String containerName = StatCollector.translateToLocal(tileEntityInterface.getInventoryName());
        fontRendererObj.drawString(containerName, xSize / 2 - fontRendererObj.getStringWidth(containerName) / 2, 6, 4210752);
        fontRendererObj.drawString(StatCollector.translateToLocal(Names.Containers.VANILLA_INVENTORY), 8, ySize - 96 + 2, 4210752);

        if(tileEntityInterface._fluidamount > 0) {
        	FluidStack fluid = new FluidStack(tileEntityInterface._fluidid, tileEntityInterface._fluidamount);
			int tankSize = tileEntityInterface._fluidamount * tankHeight / 1000;

			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			drawTank(76, 61, fluid, tankSize);
        }

        if(tileEntityInterface._energy > 0) {
        	int energySize = tileEntityInterface._energy * tankHeight / 10000;
        	GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        	drawEnergy(96, 61, energySize);
        }

        String hoppingText = StatCollector.translateToLocal("container.cm:hoppingMode.disabled");
        if(tileEntityInterface._hoppingmode == 1) {
        	hoppingText = StatCollector.translateToLocal("container.cm:hoppingMode.importing");
        } else if(tileEntityInterface._hoppingmode == 2) {
        	hoppingText = StatCollector.translateToLocal("container.cm:hoppingMode.exporting");
        } else if(tileEntityInterface._hoppingmode == 3) {
        	hoppingText = StatCollector.translateToLocal("container.cm:hoppingMode.auto");
        }
        hoppingButton.displayString = hoppingText;
    }

    protected void drawEnergy(int xOffset, int yOffset, int level)
    {
		int vertOffset = 0;

		while (level > 0)
		{
			int texHeight = 0;

			if (level > 4)
			{
				texHeight = 4;
				level -= 4;
			}
			else
			{
				texHeight = level;
				level = 0;
			}

			bindTexture(Textures.Gui.INTERFACE);
			this.drawTexturedModalRect(xOffset, yOffset - texHeight - vertOffset, 176, 0, 4, texHeight);
			vertOffset = vertOffset + 4;
		}
    }

	protected void drawTank(int xOffset, int yOffset, FluidStack stack, int level)
	{
		if (stack == null) {
			return;
		}
		Fluid fluid = stack.getFluid();
		if (fluid == null) {
			return;
		}

		IIcon icon = fluid.getIcon(stack);
		if (icon == null) {
			icon = Blocks.flowing_lava.getIcon(0, 0);
		}

		int vertOffset = 0;

		while (level > 0)
		{
			int texHeight = 0;

			if (level > 4)
			{
				texHeight = 4;
				level -= 4;
			}
			else
			{
				texHeight = level;
				level = 0;
			}

			bindTexture(fluid);

			drawTexturedModelRectFromIcon(xOffset, yOffset - texHeight - vertOffset, icon, 4, texHeight);
			vertOffset = vertOffset + 4;
		}
	}

	protected void bindTexture(ResourceLocation tex)
	{
		this.mc.renderEngine.bindTexture(tex);
	}

	protected void bindTexture(Fluid fluid)
	{
		if (fluid.getSpriteNumber() == 0) {
			this.mc.renderEngine.bindTexture(TextureMap.locationBlocksTexture);
		} else {
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, fluid.getSpriteNumber());
		}
	}

    @Override
    protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        this.mc.getTextureManager().bindTexture(Textures.Gui.INTERFACE);

        int xStart = (width - xSize) / 2;
        int yStart = (height - ySize) / 2;
        this.drawTexturedModalRect(xStart, yStart, 0, 0, xSize, ySize);
    }

}
