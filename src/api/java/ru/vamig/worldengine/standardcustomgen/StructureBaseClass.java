package ru.vamig.worldengine.standardcustomgen;

import java.util.Random;

import net.minecraft.world.World;

public abstract class StructureBaseClass {

    public abstract boolean generate(World world, Random rand, int x, int y, int z);
}
