package cat.yoink.dream.impl.module.combat;

import cat.yoink.dream.api.module.Category;
import cat.yoink.dream.api.module.Module;
import cat.yoink.dream.api.setting.Setting;
import cat.yoink.dream.api.setting.SettingType;
import cat.yoink.dream.api.util.PlayerUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AutoTrap extends Module
{
    private final Setting blocksPerTick = new Setting.Builder(SettingType.INTEGER)
            .setName("BPT")
            .setModule(this)
            .setIntegerValue(1)
            .setMinIntegerValue(1)
            .setMaxIntegerValue(10)
            .build();

    private final Setting disable = new Setting.Builder(SettingType.BOOLEAN)
            .setName("Disable")
            .setModule(this)
            .setBooleanValue(true)
            .build();

    private final List<Vec3d> positions = new ArrayList<>(Arrays.asList(
            new Vec3d(0, -1, -1),
            new Vec3d(1, -1, 0),
            new Vec3d(0, -1, 1),
            new Vec3d(-1, -1, 0),
            new Vec3d(0, 0, -1),
            new Vec3d(1, 0, 0),
            new Vec3d(0, 0, 1),
            new Vec3d(-1, 0, 0),
            new Vec3d(0, 1, -1),
            new Vec3d(1, 1, 0),
            new Vec3d(0, 1, 1),
            new Vec3d(-1, 1, 0),
            new Vec3d(0, 2, -1),
            new Vec3d(0, 2, 1),
            new Vec3d(0, 2, 0)
    ));

    private boolean finished;

    public AutoTrap(String name, String description, Category category)
    {
        super(name, description, category);

        addSetting(blocksPerTick);
        addSetting(disable);
    }

    @Override
    public void onEnable()
    {
        finished = false;
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event)
    {
        if (mc.player == null || mc.world == null) return;

        if (finished && disable.getBooleanValue()) disable();

        int blocksPlaced = 0;

        for (Vec3d position : positions)
        {
            EntityPlayer closestPlayer = getClosestPlayer();
            if (closestPlayer != null)
            {
                BlockPos pos = new BlockPos(position.add(getClosestPlayer().getPositionVector()));

                if (mc.world.getBlockState(pos).getBlock().equals(Blocks.AIR))
                {
                    int oldSlot = mc.player.inventory.currentItem;
                    mc.player.inventory.currentItem = PlayerUtil.getSlot(Blocks.OBSIDIAN);
                    PlayerUtil.placeBlock(pos);
                    mc.player.inventory.currentItem = oldSlot;
                    blocksPlaced++;

                    if (blocksPlaced == blocksPerTick.getIntegerValue()) return;
                }
            }
        }
        if (blocksPlaced == 0) finished = true;
    }

    private EntityPlayer getClosestPlayer()
    {
        EntityPlayer closestPlayer = null;
        double range = 1000;
        for (EntityPlayer playerEntity : mc.world.playerEntities)
        {
            if (!playerEntity.equals(mc.player))
            {
                double distance = mc.player.getDistance(playerEntity);
                if (distance < range)
                {
                    closestPlayer = playerEntity;
                    range = distance;
                }
            }
        }
        return closestPlayer;
    }
}
