package alfheim.client.model.entity

import alfheim.common.block.AlfheimBlocks
import net.minecraft.block.Block
import net.minecraft.client.model.ModelRenderer
import net.minecraft.init.Blocks

object ModelEntityThrym: ModelEntityPrimalBoss() {
	
	override lateinit var head: ModelRenderer
	override lateinit var body: ModelRenderer
	override lateinit var rightarm: ModelRenderer
	override lateinit var leftarm: ModelRenderer
	override lateinit var rightleg: ModelRenderer
	override lateinit var leftleg: ModelRenderer
	
	var shape20: ModelRenderer
	var shape91: ModelRenderer
	var shape95: ModelRenderer
	var shape99: ModelRenderer
	var shape112: ModelRenderer
	var shape118: ModelRenderer
	var shape124: ModelRenderer
	var shape21: ModelRenderer
	var shape22: ModelRenderer
	var shape23: ModelRenderer
	var shape24: ModelRenderer
	var shape26: ModelRenderer
	var shape27: ModelRenderer
	var shape28: ModelRenderer
	var shape29: ModelRenderer
	var shape25: ModelRenderer
	var shape92: ModelRenderer
	var shape93: ModelRenderer
	var shape94: ModelRenderer
	var shape96: ModelRenderer
	var shape97: ModelRenderer
	var shape98: ModelRenderer
	var shape100: ModelRenderer
	var shape101: ModelRenderer
	var shape103: ModelRenderer
	var shape105: ModelRenderer
	var shape107: ModelRenderer
	var shape108: ModelRenderer
	var shape110: ModelRenderer
	var shape109: ModelRenderer
	var shape104: ModelRenderer
	var shape113: ModelRenderer
	var shape114: ModelRenderer
	var shape116: ModelRenderer
	var shape115: ModelRenderer
	var shape117: ModelRenderer
	var shape119: ModelRenderer
	var shape120: ModelRenderer
	var shape122: ModelRenderer
	var shape121: ModelRenderer
	var shape123: ModelRenderer
	var shape125: ModelRenderer
	var shape126: ModelRenderer
	var shape130: ModelRenderer
	var shape127: ModelRenderer
	var shape128: ModelRenderer
	var shape132: ModelRenderer
	var shape129: ModelRenderer
	var shape133: ModelRenderer
	var shape131: ModelRenderer
	var body2: ModelRenderer
	var shape139: ModelRenderer
	var shape158: ModelRenderer
	var shape140: ModelRenderer
	var shape141: ModelRenderer
	var shape142: ModelRenderer
	var shape143: ModelRenderer
	var shape159: ModelRenderer
	var shape160: ModelRenderer
	var shape161: ModelRenderer
	var shape162: ModelRenderer
	var shape163: ModelRenderer
	var shape164: ModelRenderer
	var shape165: ModelRenderer
	var shape166: ModelRenderer
	var shape167: ModelRenderer
	var shape168: ModelRenderer
	var shape169: ModelRenderer
	var shape170: ModelRenderer
	var shape176: ModelRenderer
	var shape171: ModelRenderer
	var shape172: ModelRenderer
	var shape173: ModelRenderer
	var shape174: ModelRenderer
	var shape175: ModelRenderer
	var shape177: ModelRenderer
	var shape178: ModelRenderer
	var shape179: ModelRenderer
	var shape180: ModelRenderer
	var shape181: ModelRenderer
	var shape187: ModelRenderer
	var shape189: ModelRenderer
	var shape194: ModelRenderer
	var rightarm2: ModelRenderer
	var shape188: ModelRenderer
	var shape190: ModelRenderer
	var shape191: ModelRenderer
	var shape195: ModelRenderer
	var shape182: ModelRenderer
	var shape184: ModelRenderer
	var shape192: ModelRenderer
	var leftarm2: ModelRenderer
	var shape183: ModelRenderer
	var shape185: ModelRenderer
	var shape186: ModelRenderer
	var shape193: ModelRenderer
	var shape151: ModelRenderer
	var shape152: ModelRenderer
	var shape153: ModelRenderer
	var shape154: ModelRenderer
	var shape155: ModelRenderer
	var shape156: ModelRenderer
	var shape157: ModelRenderer
	var rightleg2: ModelRenderer
	var shape144: ModelRenderer
	var shape145: ModelRenderer
	var shape146: ModelRenderer
	var shape147: ModelRenderer
	var shape148: ModelRenderer
	var shape149: ModelRenderer
	var shape150: ModelRenderer
	var leftleg2: ModelRenderer
	
	init {
		textureWidth = 192
		textureHeight = 192
		shape124 = ModelRenderer(this, 60, 170)
		shape124.setRotationPoint(-7f, -12f, 3.5f)
		shape124.addBox(-16.5f, -5f, -5f, 14, 10, 9, 0f)
		setRotateAngle(shape124, 0.2268928f, -0.31415927f, -0.08726646f)
		shape121 = ModelRenderer(this, 60, 180)
		shape121.setRotationPoint(-7f, 0f, 0f)
		shape121.addBox(-6f, -1.5f, -1.5f, 6, 2, 3, 0f)
		setRotateAngle(shape121, 0f, 0.12217305f, 0.61086524f)
		shape123 = ModelRenderer(this, 60, 180)
		shape123.setRotationPoint(-2.5f, -0.5f, 0f)
		shape123.addBox(-4f, -1.5f, -1f, 3, 2, 2, 0f)
		setRotateAngle(shape123, 0f, 0.12217305f, -0.2617994f)
		shape100 = ModelRenderer(this, 60, 170)
		shape100.setRotationPoint(14f, 0f, -0.5f)
		shape100.addBox(0f, -4.5f, -4.5f, 12, 9, 9, 0f)
		setRotateAngle(shape100, 0f, 0.2268928f, -0.4712389f)
		shape128 = ModelRenderer(this, 60, 170)
		shape128.setRotationPoint(-10.5f, -1.5f, -0.5f)
		shape128.addBox(-9f, -2.5f, -2.5f, 9, 5, 5, 0f)
		setRotateAngle(shape128, 0.08726646f, 0.34906584f, 0.55850536f)
		shape122 = ModelRenderer(this, 60, 180)
		shape122.setRotationPoint(-7.7f, 1.5f, -0.5f)
		shape122.addBox(-4f, -2f, -1.5f, 5, 3, 3, 0f)
		setRotateAngle(shape122, 0f, 0.19198622f, -0.2617994f)
		shape193 = ModelRenderer(this, 0, 148)
		shape193.setRotationPoint(0f, -2f, 0f)
		shape193.addBox(-0.5f, -2f, -0.5f, 1, 3, 1, 0f)
		setRotateAngle(shape193, 0.27925268f, 0.13962634f, -0.05235988f)
		shape112 = ModelRenderer(this, 60, 180)
		shape112.setRotationPoint(9.5f, -21.7f, 2f)
		shape112.addBox(0f, -2.5f, -2.5f, 8, 6, 6, 0f)
		setRotateAngle(shape112, 0f, 0.2268928f, -0.6981317f)
		shape187 = ModelRenderer(this, 0, 148)
		shape187.setRotationPoint(-8f, 0f, 0f)
		shape187.addBox(-3f, -1f, -1f, 3, 2, 2, 0f)
		setRotateAngle(shape187, 0f, 0.27925268f, 0.87266463f)
		shape154 = ModelRenderer(this, 58, 148)
		shape154.setRotationPoint(0.2f, 14f, -6f)
		shape154.addBox(-3f, 0f, -1f, 9, 7, 6, 0f)
		setRotateAngle(shape154, -0.017453292f, 0.017453292f, 0.02617994f)
		shape171 = ModelRenderer(this, 80, 148)
		shape171.setRotationPoint(2f, 0f, 0f)
		shape171.addBox(0f, -1.5f, -1.5f, 4, 3, 4, 0f)
		setRotateAngle(shape171, -0.034906585f, -0.19198622f, 0.6806784f)
		shape153 = ModelRenderer(this, 58, 148)
		shape153.setRotationPoint(-0.6f, 6f, -6f)
		shape153.addBox(-5f, 0f, -1f, 12, 8, 6, 0f)
		setRotateAngle(shape153, -0.034906585f, 0.017453292f, 0.034906585f)
		shape188 = ModelRenderer(this, 0, 148)
		shape188.setRotationPoint(-3f, 0f, 0f)
		shape188.addBox(-2f, -0.5f, -0.5f, 3, 1, 1, 0f)
		setRotateAngle(shape188, 0f, 0.13962634f, 0.34906584f)
		shape182 = ModelRenderer(this, 0, 148)
		shape182.setRotationPoint(8f, 0f, 0f)
		shape182.addBox(0f, -1f, -1f, 3, 2, 2, 0f)
		setRotateAngle(shape182, 0f, -0.27925268f, -0.87266463f)
		shape189 = ModelRenderer(this, 0, 148)
		shape189.setRotationPoint(-3f, -1f, 6f)
		shape189.addBox(0f, -1.5f, -2f, 3, 3, 4, 0f)
		setRotateAngle(shape189, 0.83775806f, 0f, -0.08726646f)
		shape24 = ModelRenderer(this, 10, 170)
		shape24.setRotationPoint(0f, -6f, -2f)
		shape24.addBox(4f, 2f, 0f, 4, 10, 4, 0f)
		setRotateAngle(shape24, -0.05235988f, -0.15899949f, 0.06981317f)
		shape186 = ModelRenderer(this, 0, 148)
		shape186.setRotationPoint(2.5f, 0f, 3f)
		shape186.addBox(-0.5f, -0.5f, -1f, 1, 1, 3, 0f)
		setRotateAngle(shape186, 0.17453292f, -0.19198622f, 0f)
		shape109 = ModelRenderer(this, 60, 180)
		shape109.setRotationPoint(7f, 0f, 0f)
		shape109.addBox(0f, -2f, -2f, 9, 4, 4, 0f)
		setRotateAngle(shape109, 0f, -0.34906584f, -0.4537856f)
		shape195 = ModelRenderer(this, 0, 148)
		shape195.setRotationPoint(0f, -2f, 0f)
		shape195.addBox(-0.5f, -2f, -0.5f, 1, 3, 1, 0f)
		setRotateAngle(shape195, 0.27925268f, -0.13962634f, 0.05235988f)
		shape91 = ModelRenderer(this, 0, 180)
		shape91.setRotationPoint(-4.4f, -12.4f, -12f)
		shape91.addBox(-2f, -1f, -0.5f, 4, 2, 1, 0f)
		setRotateAngle(shape91, -0.10471976f, -0.10471976f, 0.31415927f)
		shape174 = ModelRenderer(this, 80, 148)
		shape174.setRotationPoint(2.5f, 0f, 0.5f)
		shape174.addBox(0f, -1f, -1f, 3, 2, 2, 0f)
		setRotateAngle(shape174, -0.2443461f, 0.17453292f, 0.57595867f)
		shape152 = ModelRenderer(this, 58, 148)
		shape152.setRotationPoint(-0.3f, 0f, -6f)
		shape152.addBox(-7f, -1f, -1f, 14, 8, 6, 0f)
		setRotateAngle(shape152, -0.06981317f, 0.017453292f, 0.08726646f)
		shape162 = ModelRenderer(this, 0, 148)
		shape162.setRotationPoint(0f, 2f, -2f)
		shape162.addBox(-1.5f, 0f, 0f, 3, 4, 1, 0f)
		setRotateAngle(shape162, 0.034906585f, 0f, 0f)
		shape194 = ModelRenderer(this, 0, 148)
		shape194.setRotationPoint(-5f, 5f, 5.5f)
		shape194.addBox(-1f, -2f, -1f, 2, 3, 2, 0f)
		setRotateAngle(shape194, -0.57595867f, -0.05235988f, -0.17453292f)
		leftleg2 = ModelRenderer(this, 0, 60)
		leftleg2.setRotationPoint(0f, 0f, 0f)
		leftleg2.addBox(-6f, 22f, -6.2f, 14, 22, 13, 0f)
		shape170 = ModelRenderer(this, 80, 148)
		shape170.setRotationPoint(3f, -2f, 2f)
		shape170.addBox(-1f, -1.5f, -1.5f, 4, 4, 4, 0f)
		setRotateAngle(shape170, 0.29670596f, 0.38397244f, -0.5061455f)
		shape148 = ModelRenderer(this, 58, 148)
		shape148.setRotationPoint(0.3f, 0f, 6f)
		shape148.addBox(-7f, -1f, -5f, 14, 9, 6, 0f)
		setRotateAngle(shape148, 0.06981317f, 0.017453292f, -0.08726646f)
		leftarm2 = ModelRenderer(this, 140, 61)
		leftarm2.setRotationPoint(0f, 0f, 0f)
		leftarm2.addBox(-3.5f, 14f, -6.5f, 13, 22, 13, 0f)
		head = ModelRenderer(this, 0, 0)
		head.setRotationPoint(0f, -61f, 0f)
		head.addBox(-12f, -24f, -12f, 24, 24, 24, 0f)
		leftleg = ModelRenderer(this, 0, 48)
		leftleg.setRotationPoint(6f, -20f, 0f)
		leftleg.addBox(-6f, 0f, -6f, 13, 24, 12, 0f)
		shape172 = ModelRenderer(this, 80, 148)
		shape172.setRotationPoint(4f, 0f, 0.5f)
		shape172.addBox(-0.6f, -1.5f, -1.5f, 3, 3, 3, 0f)
		setRotateAngle(shape172, -0.15707964f, -0.05235988f, 0.5235988f)
		shape94 = ModelRenderer(this, 0, 180)
		shape94.setRotationPoint(-5.4f, 2.5f, 1f)
		shape94.addBox(-0.7f, -1f, -0.4f, 1, 3, 2, 0f)
		setRotateAngle(shape94, 0.10471976f, 0.27925268f, -0.10471976f)
		shape115 = ModelRenderer(this, 60, 180)
		shape115.setRotationPoint(7f, 0f, 0f)
		shape115.addBox(0f, -1.5f, -1.5f, 6, 2, 3, 0f)
		setRotateAngle(shape115, 0f, -0.12217305f, -0.61086524f)
		shape147 = ModelRenderer(this, 58, 148)
		shape147.setRotationPoint(-0.2f, 14f, -6f)
		shape147.addBox(-6f, 0f, -1f, 9, 7, 6, 0f)
		setRotateAngle(shape147, -0.017453292f, -0.017453292f, -0.02617994f)
		shape151 = ModelRenderer(this, 58, 148)
		shape151.setRotationPoint(-6.5f, -0.5f, 0f)
		shape151.addBox(-1f, 0f, -6.5f, 1, 5, 13, 0f)
		setRotateAngle(shape151, 0f, 0f, 0.08726646f)
		shape101 = ModelRenderer(this, 60, 170)
		shape101.setRotationPoint(9.7f, 0f, 1f)
		shape101.addBox(0f, -4.5f, -4.5f, 12, 8, 8, 0f)
		setRotateAngle(shape101, 0.12217305f, 0.12217305f, -0.4712389f)
		shape23 = ModelRenderer(this, 10, 170)
		shape23.setRotationPoint(0f, -6f, -2f)
		shape23.addBox(-8f, 2f, 0f, 4, 10, 4, 0f)
		setRotateAngle(shape23, -0.05235988f, 0.15899949f, -0.06981317f)
		shape129 = ModelRenderer(this, 60, 180)
		shape129.setRotationPoint(-7f, 0f, 0f)
		shape129.addBox(-9f, -2f, -2f, 9, 4, 4, 0f)
		setRotateAngle(shape129, 0f, 0.34906584f, 0.4537856f)
		shape26 = ModelRenderer(this, 15, 175)
		shape26.setRotationPoint(1f, -6f, -2f)
		shape26.addBox(-8f, 6f, -0.4f, 4, 11, 2, 0f)
		setRotateAngle(shape26, 0.02617994f, 0.10471976f, -0.17453292f)
		shape145 = ModelRenderer(this, 58, 148)
		shape145.setRotationPoint(0.3f, 0f, -6f)
		shape145.addBox(-7f, -1f, -1f, 14, 8, 6, 0f)
		setRotateAngle(shape145, -0.06981317f, -0.017453292f, -0.08726646f)
		shape95 = ModelRenderer(this, 0, 180)
		shape95.setRotationPoint(4.4f, -12.4f, -12f)
		shape95.addBox(-2f, -1f, -0.5f, 4, 2, 1, 0f)
		setRotateAngle(shape95, -0.10471976f, 0.10471976f, -0.31415927f)
		body2 = ModelRenderer(this, 68, 60)
		body2.setRotationPoint(0f, 17f, 0f)
		body2.addBox(-12f, 0f, -6f, 24, 24, 12, 0f)
		setRotateAngle(body2, -0.017453292f, 0f, 0f)
		shape157 = ModelRenderer(this, 58, 148)
		shape157.setRotationPoint(0f, 13.5f, 6f)
		shape157.addBox(-3f, 0f, -5f, 9, 8, 6, 0f)
		setRotateAngle(shape157, 0.017453292f, -0.017453292f, 0f)
		shape22 = ModelRenderer(this, 0, 180)
		shape22.setRotationPoint(0.2f, -6f, 0f)
		shape22.addBox(-8f, 0f, -2f, 8, 2, 2, 0f)
		setRotateAngle(shape22, -0.17453292f, 0.17453292f, -0.3090978f)
		shape181 = ModelRenderer(this, 80, 148)
		shape181.setRotationPoint(-2.2f, 0f, 0f)
		shape181.addBox(-3f, -1f, -1f, 3, 2, 2, 0f)
		setRotateAngle(shape181, 0.43633232f, -0.2268928f, -0.55850536f)
		shape155 = ModelRenderer(this, 58, 148)
		shape155.setRotationPoint(-0.3f, 0f, 6f)
		shape155.addBox(-7f, -1f, -5f, 14, 9, 6, 0f)
		setRotateAngle(shape155, 0.06981317f, -0.017453292f, 0.08726646f)
		shape169 = ModelRenderer(this, 0, 148)
		shape169.setRotationPoint(0f, -1.3f, 0.6f)
		shape169.addBox(-2f, -1f, 0f, 4, 3, 4, 0f)
		setRotateAngle(shape169, 0.5061455f, 0f, 0f)
		shape97 = ModelRenderer(this, 0, 180)
		shape97.setRotationPoint(4f, 1f, 1f)
		shape97.addBox(-1f, -1f, -1f, 3, 2, 2, 0f)
		setRotateAngle(shape97, -0.13962634f, -0.20943952f, 0.6632251f)
		leftarm = ModelRenderer(this, 140, 48)
		leftarm.setRotationPoint(18f, -54f, 0.5f)
		leftarm.addBox(-3f, -6f, -6f, 12, 23, 12, 0f)
		shape103 = ModelRenderer(this, 60, 180)
		shape103.setRotationPoint(11.3f, 2.1f, -0.7f)
		shape103.addBox(0f, -2f, -2.5f, 7, 4, 6, 0f)
		setRotateAngle(shape103, 0.13962634f, -0.13962634f, 0.54105204f)
		shape107 = ModelRenderer(this, 60, 180)
		shape107.setRotationPoint(11f, 0.8f, -0.4f)
		shape107.addBox(0f, -1.5f, -2.5f, 6, 3, 5, 0f)
		setRotateAngle(shape107, -0.017453292f, -0.34906584f, 0.4712389f)
		shape143 = ModelRenderer(this, 52, 148)
		shape143.setRotationPoint(13.5f, 2.5f, -1f)
		shape143.addBox(-13.7f, -1f, -7f, 14, 3, 15, 0f)
		setRotateAngle(shape143, 0f, -0.008347632f, -0.08726646f)
		shape163 = ModelRenderer(this, 0, 148)
		shape163.setRotationPoint(0f, 3.7f, -1.6f)
		shape163.addBox(0.5f, 0f, -1f, 3, 5, 1, 0f)
		setRotateAngle(shape163, 0f, -0.87266463f, 0f)
		shape139 = ModelRenderer(this, 52, 148)
		shape139.setRotationPoint(0f, 37f, 0f)
		shape139.addBox(-13.5f, 0f, -7f, 27, 5, 13, 0f)
		shape110 = ModelRenderer(this, 60, 180)
		shape110.setRotationPoint(4f, 0.1f, 0f)
		shape110.addBox(1f, -1.5f, -1.5f, 5, 2, 3, 0f)
		setRotateAngle(shape110, 0f, -0.05235988f, 0.17453292f)
		shape113 = ModelRenderer(this, 60, 180)
		shape113.setRotationPoint(6f, 0f, 1f)
		shape113.addBox(0f, -2f, -3f, 8, 5, 5, 0f)
		setRotateAngle(shape113, 0f, 0.06981317f, -0.61086524f)
		shape114 = ModelRenderer(this, 60, 180)
		shape114.setRotationPoint(5f, 1f, -0.5f)
		shape114.addBox(1f, -2f, -2f, 7, 3, 4, 0f)
		setRotateAngle(shape114, 0f, -0.19198622f, -0.61086524f)
		shape192 = ModelRenderer(this, 0, 148)
		shape192.setRotationPoint(5f, 5f, 5.5f)
		shape192.addBox(-1f, -2f, -1f, 2, 3, 2, 0f)
		setRotateAngle(shape192, -0.57595867f, 0.05235988f, 0.17453292f)
		shape146 = ModelRenderer(this, 58, 148)
		shape146.setRotationPoint(0.6f, 6f, -6f)
		shape146.addBox(-7f, 0f, -1f, 12, 8, 6, 0f)
		setRotateAngle(shape146, -0.034906585f, -0.017453292f, -0.034906585f)
		shape21 = ModelRenderer(this, 0, 180)
		shape21.setRotationPoint(-0.2f, -6f, 0f)
		shape21.addBox(0f, 0f, -2f, 8, 2, 4, 0f)
		setRotateAngle(shape21, -0.17453292f, -0.17453292f, 0.3090978f)
		shape108 = ModelRenderer(this, 60, 180)
		shape108.setRotationPoint(10.5f, -1.5f, -0.5f)
		shape108.addBox(0f, -2.5f, -2.5f, 9, 5, 5, 0f)
		setRotateAngle(shape108, 0.08726646f, -0.34906584f, -0.55850536f)
		shape141 = ModelRenderer(this, 52, 148)
		shape141.setRotationPoint(13.5f, -2f, -0.5f)
		shape141.addBox(-14f, -1f, -7f, 14, 3, 14, 0f)
		setRotateAngle(shape141, 0f, 0f, -0.06981317f)
		shape150 = ModelRenderer(this, 58, 148)
		shape150.setRotationPoint(0f, 13.5f, 6f)
		shape150.addBox(-6f, 0f, -5f, 9, 8, 6, 0f)
		setRotateAngle(shape150, 0.017453292f, 0.017453292f, 0f)
		shape99 = ModelRenderer(this, 60, 160)
		shape99.setRotationPoint(7f, -12f, 3.5f)
		shape99.addBox(2.5f, -5f, -5f, 14, 10, 9, 0f)
		setRotateAngle(shape99, 0.2268928f, 0.31415927f, 0.08726646f)
		rightleg = ModelRenderer(this, 0, 48)
		rightleg.mirror = true
		rightleg.setRotationPoint(-6f, -20f, 0f)
		rightleg.addBox(-7f, 0f, -6f, 13, 24, 12, 0f)
		shape126 = ModelRenderer(this, 60, 170)
		shape126.setRotationPoint(-9.7f, 0f, 1f)
		shape126.addBox(-12f, -4.5f, -4.5f, 12, 8, 8, 0f)
		setRotateAngle(shape126, 0.12217305f, -0.12217305f, 0.4712389f)
		shape144 = ModelRenderer(this, 58, 148)
		shape144.setRotationPoint(6.5f, -0.5f, 0f)
		shape144.addBox(0f, 0f, -6.5f, 1, 5, 13, 0f)
		setRotateAngle(shape144, 0f, 0f, -0.08726646f)
		shape180 = ModelRenderer(this, 80, 148)
		shape180.setRotationPoint(-2.5f, 0f, 0.5f)
		shape180.addBox(-3f, -1f, -1f, 3, 2, 2, 0f)
		setRotateAngle(shape180, -0.2443461f, -0.17453292f, -0.57595867f)
		shape118 = ModelRenderer(this, 60, 180)
		shape118.setRotationPoint(-9.5f, -21.7f, 2f)
		shape118.addBox(-8f, -2.5f, -2.5f, 8, 6, 6, 0f)
		setRotateAngle(shape118, 0f, -0.2268928f, 0.6981317f)
		shape149 = ModelRenderer(this, 58, 148)
		shape149.setRotationPoint(0.6f, 7f, 6f)
		shape149.addBox(-7f, 0f, -5f, 12, 7, 6, 0f)
		setRotateAngle(shape149, 0.05235988f, 0.017453292f, -0.02617994f)
		shape116 = ModelRenderer(this, 60, 180)
		shape116.setRotationPoint(7.7f, 1.5f, -0.5f)
		shape116.addBox(-1f, -2f, -1.5f, 5, 3, 3, 0f)
		setRotateAngle(shape116, 0f, -0.19198622f, 0.2617994f)
		shape119 = ModelRenderer(this, 60, 180)
		shape119.setRotationPoint(-6f, 0f, 1f)
		shape119.addBox(-8f, -2f, -3f, 8, 5, 5, 0f)
		setRotateAngle(shape119, 0f, -0.06981317f, 0.61086524f)
		shape165 = ModelRenderer(this, 0, 148)
		shape165.setRotationPoint(0f, 7.7f, -1.6f)
		shape165.addBox(-3.5f, 0f, -1f, 3, 4, 1, 0f)
		setRotateAngle(shape165, 0f, 0.87266463f, -0.13962634f)
		shape158 = ModelRenderer(this, 0, 148)
		shape158.setRotationPoint(0f, 37f, -11.5f)
		shape158.addBox(-1f, -1f, 0f, 2, 2, 1, 0f)
		setRotateAngle(shape158, 0.10471976f, 0f, 0f)
		shape184 = ModelRenderer(this, 0, 148)
		shape184.setRotationPoint(0f, -1f, 6f)
		shape184.addBox(0f, -1.5f, -2f, 3, 3, 4, 0f)
		setRotateAngle(shape184, 0.83775806f, 0f, 0.08726646f)
		shape191 = ModelRenderer(this, 0, 148)
		shape191.setRotationPoint(2.5f, 0f, 3f)
		shape191.addBox(-0.5f, -0.5f, -1f, 1, 1, 3, 0f)
		setRotateAngle(shape191, 0.17453292f, 0.19198622f, 0f)
		shape133 = ModelRenderer(this, 60, 170)
		shape133.setRotationPoint(-4f, 0.1f, 0f)
		shape133.addBox(-6f, -1.5f, -1.5f, 5, 2, 3, 0f)
		setRotateAngle(shape133, 0f, 0.05235988f, -0.17453292f)
		shape173 = ModelRenderer(this, 80, 148)
		shape173.setRotationPoint(1.9f, 0f, -0.5f)
		shape173.addBox(0f, -1f, -1f, 3, 2, 3, 0f)
		setRotateAngle(shape173, -0.20943952f, 0.13962634f, 0.4537856f)
		shape179 = ModelRenderer(this, 80, 148)
		shape179.setRotationPoint(-1.9f, 0f, -0.5f)
		shape179.addBox(-3f, -1f, -1f, 3, 2, 3, 0f)
		setRotateAngle(shape179, -0.20943952f, -0.13962634f, -0.4537856f)
		shape29 = ModelRenderer(this, 5, 180)
		shape29.setRotationPoint(-1f, -4f, -2f)
		shape29.addBox(-4f, 12f, 0f, 4, 9, 2, 0f)
		setRotateAngle(shape29, 0.034906585f, 0.13962634f, -0.12217305f)
		shape127 = ModelRenderer(this, 60, 160)
		shape127.setRotationPoint(-9.5f, 0f, 0f)
		shape127.addBox(-12f, -4.4f, -4f, 12, 7, 7, 0f)
		setRotateAngle(shape127, 0.17453292f, 0.34906584f, 0.5934119f)
		shape175 = ModelRenderer(this, 80, 148)
		shape175.setRotationPoint(2.2f, 0f, 0f)
		shape175.addBox(0f, -1f, -1f, 3, 2, 2, 0f)
		setRotateAngle(shape175, 0.43633232f, 0.2268928f, 0.55850536f)
		shape160 = ModelRenderer(this, 0, 148)
		shape160.setRotationPoint(-0.9f, 0f, 0f)
		shape160.addBox(-4f, -2f, -1f, 4, 4, 1, 0f)
		setRotateAngle(shape160, -0.34906584f, 1.0471976f, 0.06981317f)
		shape105 = ModelRenderer(this, 60, 170)
		shape105.setRotationPoint(9.5f, 0f, 0f)
		shape105.addBox(0f, -4.4f, -4f, 12, 7, 7, 0f)
		setRotateAngle(shape105, 0.17453292f, -0.34906584f, -0.5934119f)
		shape166 = ModelRenderer(this, 0, 148)
		shape166.setRotationPoint(0f, 7.7f, -1.6f)
		shape166.addBox(0.5f, 0f, -1f, 3, 4, 1, 0f)
		setRotateAngle(shape166, 0f, -0.87266463f, 0.13962634f)
		rightleg2 = ModelRenderer(this, 0, 60)
		rightleg2.mirror = true
		rightleg2.setRotationPoint(0f, 0f, 0f)
		rightleg2.addBox(-8f, 22f, -6.2f, 14, 22, 13, 0f)
		rightarm2 = ModelRenderer(this, 140, 61)
		rightarm2.mirror = true
		rightarm2.setRotationPoint(0f, 0f, 0f)
		rightarm2.addBox(-9.5f, 14f, -6.5f, 13, 22, 13, 0f)
		shape183 = ModelRenderer(this, 0, 148)
		shape183.setRotationPoint(3f, 0f, 0f)
		shape183.addBox(-1f, -0.5f, -0.5f, 3, 1, 1, 0f)
		setRotateAngle(shape183, 0f, -0.13962634f, -0.34906584f)
		shape28 = ModelRenderer(this, 5, 180)
		shape28.setRotationPoint(0f, -4f, -2f)
		shape28.addBox(1f, 12f, 0f, 4, 9, 2, 0f)
		setRotateAngle(shape28, 0.034906585f, -0.13962634f, 0.12217305f)
		shape132 = ModelRenderer(this, 60, 170)
		shape132.setRotationPoint(-11f, 0.8f, -0.4f)
		shape132.addBox(-6f, -1.5f, -2.5f, 6, 3, 5, 0f)
		setRotateAngle(shape132, -0.017453292f, 0.34906584f, -0.4712389f)
		shape25 = ModelRenderer(this, 17, 175)
		shape25.setRotationPoint(0f, -6f, -2f)
		shape25.addBox(-2f, 4.8f, -0.6f, 4, 12, 2, 0f)
		setRotateAngle(shape25, 0.061086524f, 0f, 0f)
		shape161 = ModelRenderer(this, 0, 148)
		shape161.setRotationPoint(0f, 0f, 0.2f)
		shape161.addBox(-2f, -2f, -1f, 4, 5, 1, 0f)
		setRotateAngle(shape161, -0.40142572f, 0f, 0f)
		shape167 = ModelRenderer(this, 0, 148)
		shape167.setRotationPoint(3f, 1f, 1f)
		shape167.addBox(0f, 0f, 0f, 2, 9, 1, 0f)
		setRotateAngle(shape167, -0.2617994f, -0.6981317f, 0.35904709f)
		shape104 = ModelRenderer(this, 60, 180)
		shape104.setRotationPoint(2.3f, 0f, 0f)
		shape104.addBox(3f, -2.2f, -1.5f, 7, 3, 4, 0f)
		setRotateAngle(shape104, 0f, -0.13962634f, 0.19198622f)
		shape159 = ModelRenderer(this, 0, 148)
		shape159.mirror = true
		shape159.setRotationPoint(0.9f, 0f, 0f)
		shape159.addBox(0f, -2f, -1f, 4, 4, 1, 0f)
		setRotateAngle(shape159, -0.34906584f, -1.0471976f, -0.06981317f)
		shape164 = ModelRenderer(this, 0, 148)
		shape164.setRotationPoint(0f, 3.7f, -1.6f)
		shape164.addBox(-3.5f, 0f, -1f, 3, 5, 1, 0f)
		setRotateAngle(shape164, 0f, 0.87266463f, 0f)
		shape98 = ModelRenderer(this, 0, 180)
		shape98.setRotationPoint(5.4f, 2.5f, 1f)
		shape98.addBox(-0.3f, -1f, -0.4f, 1, 3, 2, 0f)
		setRotateAngle(shape98, 0.10471976f, -0.27925268f, 0.10471976f)
		shape96 = ModelRenderer(this, 0, 180)
		shape96.setRotationPoint(2f, 0f, 0f)
		shape96.addBox(-1f, -1f, -0.5f, 3, 2, 2, 0f)
		setRotateAngle(shape96, -0.10471976f, -0.20943952f, 0.36651915f)
		shape142 = ModelRenderer(this, 52, 148)
		shape142.setRotationPoint(-13.5f, 2.5f, -1f)
		shape142.addBox(-0.3f, -1f, -7f, 14, 3, 15, 0f)
		setRotateAngle(shape142, 0f, 0f, 0.08726646f)
		shape190 = ModelRenderer(this, 0, 148)
		shape190.setRotationPoint(-1f, 0f, 1f)
		shape190.addBox(1.5f, -1f, 0f, 2, 2, 3, 0f)
		setRotateAngle(shape190, 0.34906584f, 0.10471976f, 0f)
		shape185 = ModelRenderer(this, 0, 148)
		shape185.setRotationPoint(-1f, 0f, 1f)
		shape185.addBox(1.5f, -1f, 0f, 2, 2, 3, 0f)
		setRotateAngle(shape185, 0.34906584f, -0.10471976f, 0f)
		shape92 = ModelRenderer(this, 0, 180)
		shape92.setRotationPoint(-2f, 0f, 0f)
		shape92.addBox(-2f, -1f, -0.5f, 3, 2, 2, 0f)
		setRotateAngle(shape92, -0.10471976f, 0.20943952f, -0.36651915f)
		shape125 = ModelRenderer(this, 60, 170)
		shape125.setRotationPoint(-14f, 0f, -0.5f)
		shape125.addBox(-12f, -4.5f, -4.5f, 12, 9, 9, 0f)
		setRotateAngle(shape125, 0f, -0.2268928f, 0.4712389f)
		shape117 = ModelRenderer(this, 60, 180)
		shape117.setRotationPoint(2.5f, -0.5f, 0f)
		shape117.addBox(1f, -1.5f, -1f, 3, 2, 2, 0f)
		setRotateAngle(shape117, 0f, -0.12217305f, 0.2617994f)
		rightarm = ModelRenderer(this, 140, 48)
		rightarm.mirror = true
		rightarm.setRotationPoint(-18f, -54f, 0.5f)
		rightarm.addBox(-9f, -6f, -6f, 12, 23, 12, 0f)
		shape130 = ModelRenderer(this, 60, 170)
		shape130.setRotationPoint(-11.3f, 2.1f, -0.7f)
		shape130.addBox(-7f, -2f, -2.5f, 7, 4, 6, 0f)
		setRotateAngle(shape130, 0.13962634f, -0.13962634f, -0.54105204f)
		shape140 = ModelRenderer(this, 52, 148)
		shape140.setRotationPoint(-13.5f, -2f, -0.5f)
		shape140.addBox(0f, -1f, -7f, 14, 3, 14, 0f)
		setRotateAngle(shape140, 0f, 0f, 0.06981317f)
		shape156 = ModelRenderer(this, 58, 148)
		shape156.setRotationPoint(-0.6f, 7f, 6f)
		shape156.addBox(-5f, 0f, -5f, 12, 7, 6, 0f)
		setRotateAngle(shape156, 0.05235988f, -0.017453292f, 0.02617994f)
		shape176 = ModelRenderer(this, 80, 148)
		shape176.setRotationPoint(-3f, -2f, 2f)
		shape176.addBox(-3f, -1.5f, -1.5f, 4, 4, 4, 0f)
		setRotateAngle(shape176, 0.29670596f, -0.38397244f, 0.5061455f)
		shape93 = ModelRenderer(this, 0, 180)
		shape93.setRotationPoint(-4f, 1f, 1f)
		shape93.addBox(-2f, -1f, -1f, 3, 2, 2, 0f)
		setRotateAngle(shape93, -0.13962634f, 0.20943952f, -0.6632251f)
		shape177 = ModelRenderer(this, 80, 148)
		shape177.setRotationPoint(-2f, 0f, 0f)
		shape177.addBox(-4f, -1.5f, -1.5f, 4, 3, 4, 0f)
		setRotateAngle(shape177, -0.034906585f, 0.19198622f, -0.6806784f)
		shape27 = ModelRenderer(this, 15, 176)
		shape27.setRotationPoint(0f, -6f, -2f)
		shape27.addBox(3f, 6f, 0f, 4, 11, 2, 0f)
		setRotateAngle(shape27, 0.02617994f, -0.10471976f, 0.17453292f)
		shape20 = ModelRenderer(this, 0, 180)
		shape20.setRotationPoint(0f, -1f, -11.4f)
		shape20.addBox(-4f, -2f, -1.6f, 8, 4, 4, 0f)
		shape120 = ModelRenderer(this, 60, 180)
		shape120.setRotationPoint(-5f, 1f, -0.5f)
		shape120.addBox(-8f, -2f, -2f, 7, 3, 4, 0f)
		setRotateAngle(shape120, 0f, 0.19198622f, 0.61086524f)
		shape131 = ModelRenderer(this, 60, 170)
		shape131.setRotationPoint(-2.3f, 0f, 0f)
		shape131.addBox(-10f, -2.2f, -1.5f, 7, 3, 4, 0f)
		setRotateAngle(shape131, 0f, -0.13962634f, -0.19198622f)
		shape178 = ModelRenderer(this, 80, 148)
		shape178.setRotationPoint(-4f, 0f, 0.5f)
		shape178.addBox(-2.4f, -1.5f, -1.5f, 3, 3, 3, 0f)
		setRotateAngle(shape178, -0.15707964f, 0.05235988f, -0.5235988f)
		body = ModelRenderer(this, 96, 10)
		body.setRotationPoint(0f, -61f, 0f)
		body.addBox(-15f, 0f, -7f, 30, 22, 16, 0f)
		setRotateAngle(body, 0f, -0f, 0f)
		shape168 = ModelRenderer(this, 0, 148)
		shape168.setRotationPoint(-3f, 1f, 1f)
		shape168.addBox(-2f, 0f, 0f, 2, 9, 1, 0f)
		setRotateAngle(shape168, -0.2617994f, 0.6981317f, -0.36651915f)
		
		reassignDefaultParts()
		
		head.addChild(shape124)
		shape120.addChild(shape121)
		shape122.addChild(shape123)
		shape99.addChild(shape100)
		shape127.addChild(shape128)
		shape119.addChild(shape122)
		shape192.addChild(shape193)
		head.addChild(shape112)
		rightarm.addChild(shape187)
		rightleg.addChild(shape154)
		shape170.addChild(shape171)
		rightleg.addChild(shape153)
		shape187.addChild(shape188)
		leftarm.addChild(shape182)
		rightarm.addChild(shape189)
		shape20.addChild(shape24)
		shape185.addChild(shape186)
		shape108.addChild(shape109)
		shape194.addChild(shape195)
		head.addChild(shape91)
		shape173.addChild(shape174)
		rightleg.addChild(shape152)
		shape158.addChild(shape162)
		rightarm.addChild(shape194)
		leftleg.addChild(leftleg2)
		shape158.addChild(shape170)
		leftleg.addChild(shape148)
		leftarm.addChild(leftarm2)
		shape171.addChild(shape172)
		shape91.addChild(shape94)
		shape114.addChild(shape115)
		leftleg.addChild(shape147)
		rightleg.addChild(shape151)
		shape100.addChild(shape101)
		shape20.addChild(shape23)
		shape128.addChild(shape129)
		shape20.addChild(shape26)
		leftleg.addChild(shape145)
		head.addChild(shape95)
		body.addChild(body2)
		rightleg.addChild(shape157)
		shape20.addChild(shape22)
		shape180.addChild(shape181)
		rightleg.addChild(shape155)
		shape158.addChild(shape169)
		shape95.addChild(shape97)
		shape100.addChild(shape103)
		shape105.addChild(shape107)
		shape139.addChild(shape143)
		shape158.addChild(shape163)
		body.addChild(shape139)
		shape107.addChild(shape110)
		shape112.addChild(shape113)
		shape113.addChild(shape114)
		leftarm.addChild(shape192)
		leftleg.addChild(shape146)
		shape20.addChild(shape21)
		shape105.addChild(shape108)
		shape139.addChild(shape141)
		leftleg.addChild(shape150)
		head.addChild(shape99)
		shape125.addChild(shape126)
		leftleg.addChild(shape144)
		shape179.addChild(shape180)
		head.addChild(shape118)
		leftleg.addChild(shape149)
		shape113.addChild(shape116)
		shape118.addChild(shape119)
		shape158.addChild(shape165)
		body.addChild(shape158)
		leftarm.addChild(shape184)
		shape190.addChild(shape191)
		shape132.addChild(shape133)
		shape172.addChild(shape173)
		shape178.addChild(shape179)
		shape20.addChild(shape29)
		shape126.addChild(shape127)
		shape174.addChild(shape175)
		shape158.addChild(shape160)
		shape101.addChild(shape105)
		shape158.addChild(shape166)
		rightleg.addChild(rightleg2)
		rightarm.addChild(rightarm2)
		shape182.addChild(shape183)
		shape20.addChild(shape28)
		shape127.addChild(shape132)
		shape20.addChild(shape25)
		shape158.addChild(shape161)
		shape158.addChild(shape167)
		shape103.addChild(shape104)
		shape158.addChild(shape159)
		shape158.addChild(shape164)
		shape95.addChild(shape98)
		shape95.addChild(shape96)
		shape139.addChild(shape142)
		shape189.addChild(shape190)
		shape184.addChild(shape185)
		shape91.addChild(shape92)
		shape124.addChild(shape125)
		shape116.addChild(shape117)
		shape125.addChild(shape130)
		shape139.addChild(shape140)
		rightleg.addChild(shape156)
		shape158.addChild(shape176)
		shape91.addChild(shape93)
		shape176.addChild(shape177)
		shape20.addChild(shape27)
		head.addChild(shape20)
		shape119.addChild(shape120)
		shape130.addChild(shape131)
		shape177.addChild(shape178)
		shape158.addChild(shape168)
	}
	
	override fun getSuperSmashParticlesColor() = floatArrayOf(0.5f, 0.75f, 1f)
	
	override fun getSuperSmashParticlesBlockIDs() = arrayOf(Blocks.ice, Blocks.snow, AlfheimBlocks.poisonIce).map { Block.getIdFromBlock(it) }
}