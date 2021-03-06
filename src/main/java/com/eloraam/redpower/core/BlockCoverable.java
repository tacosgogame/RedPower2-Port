package com.eloraam.redpower.core;

import com.eloraam.redpower.core.BlockMultipart;
import com.eloraam.redpower.core.CoreLib;
import com.eloraam.redpower.core.TileCoverable;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public abstract class BlockCoverable extends BlockMultipart {
	
	public BlockCoverable(Material m) {
		super(m);
	}
	
	@Override
	public boolean isSideSolid(IBlockAccess world, int i, int j, int k, ForgeDirection side) {
		TileCoverable tc = (TileCoverable) CoreLib.getTileEntity(world, i, j, k, TileCoverable.class);
		return tc == null ? false : tc.isSideNormal(side.ordinal());
	}
	
	@Override
	public float getExplosionResistance(Entity exploder, World world, int X, int Y, int Z, double srcX, double srcY, double srcZ) {
		Vec3 org = Vec3.createVectorHelper(srcX, srcY, srcZ);
		Vec3 end = Vec3.createVectorHelper(X + 0.5D, Y + 0.5D, Z + 0.5D);
		Block bl = world.getBlock(X, Y, Z);
		if (bl == null) {
			return 0.0F;
		} else {
			MovingObjectPosition mop = bl.collisionRayTrace(world, X, Y, Z, org, end);
			if (mop == null) {
				return bl.getExplosionResistance(exploder);
			} else {
				TileCoverable tl = (TileCoverable) CoreLib.getTileEntity(world, X, Y, Z, TileCoverable.class);
				if (tl == null) {
					return bl.getExplosionResistance(exploder);
				} else {
					float er = tl.getExplosionResistance(mop.subHit, mop.sideHit, exploder);
					return er < 0.0F ? bl.getExplosionResistance(exploder) : er;
				}
			}
		}
	}
}
