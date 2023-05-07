package alfheim.client.model.entity

import alexsocol.asjlib.*
import alfheim.common.block.AlfheimBlocks
import alfheim.common.entity.boss.primal.EntitySurtr
import net.minecraft.block.Block
import net.minecraft.client.model.*
import net.minecraft.entity.Entity
import net.minecraft.init.Blocks
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL11.*

object ModelEntitySurtr: ModelEntityPrimalBoss() {
	
	override lateinit var head: ModelRenderer
	override lateinit var body: ModelRenderer
	override lateinit var rightarm: ModelRenderer
	override lateinit var leftarm: ModelRenderer
	override lateinit var rightleg: ModelRenderer
	override lateinit var leftleg: ModelRenderer
	
	var shape8: ModelRenderer
	var shape17: ModelRenderer
	var shape20: ModelRenderer
	var shape10: ModelRenderer
	var shape13: ModelRenderer
	var shape18: ModelRenderer
	var shape19: ModelRenderer
	var shape21: ModelRenderer
	var shape22: ModelRenderer
	var shape23: ModelRenderer
	var shape24: ModelRenderer
	var shape26: ModelRenderer
	var shape27: ModelRenderer
	var shape28: ModelRenderer
	var shape29: ModelRenderer
	var shape25: ModelRenderer
	var shape44: ModelRenderer
	var shape45: ModelRenderer
	var shape46: ModelRenderer
	var shape47: ModelRenderer
	var shape48: ModelRenderer
	var shape49: ModelRenderer
	var shape50: ModelRenderer
	var shape51: ModelRenderer
	var shape52: ModelRenderer
	var shape53: ModelRenderer
	var shape54: ModelRenderer
	var shape55: ModelRenderer
	var shape56: ModelRenderer
	var shape57: ModelRenderer
	var shape58: ModelRenderer
	var shape59: ModelRenderer
	var shape60: ModelRenderer
	var shape61: ModelRenderer
	var shape62: ModelRenderer
	var shape63: ModelRenderer
	var shape64: ModelRenderer
	var shape65: ModelRenderer
	var shape66: ModelRenderer
	var shape67: ModelRenderer
	var shape68: ModelRenderer
	var shape69: ModelRenderer
	var shape70: ModelRenderer
	var shape71: ModelRenderer
	var shape72: ModelRenderer
	var shape73: ModelRenderer
	var shape74: ModelRenderer
	var shape33: ModelRenderer
	var shape34: ModelRenderer
	var shape35: ModelRenderer
	var shape30: ModelRenderer
	var shape31: ModelRenderer
	var shape32: ModelRenderer
	var shape83: ModelRenderer
	var shape84: ModelRenderer
	var shape85: ModelRenderer
	var shape86: ModelRenderer
	var shape87: ModelRenderer
	var shape88: ModelRenderer
	var shape89: ModelRenderer
	var shape90: ModelRenderer
	var shape75: ModelRenderer
	var shape76: ModelRenderer
	var shape77: ModelRenderer
	var shape78: ModelRenderer
	var shape79: ModelRenderer
	var shape80: ModelRenderer
	var shape81: ModelRenderer
	var shape82: ModelRenderer
	
	init {
		textureWidth = 96
		textureHeight = 96
		shape76 = ModelRenderer(this, 30, 80)
		shape76.setRotationPoint(2.9f, 0.0f, -3.0f)
		shape76.addBox(-1.0f, -2.0f, 0.0f, 2, 7, 4, 0.0f)
		setRotateAngle(shape76, -0.12217305f, -0.87266463f, 0.0f)
		shape81 = ModelRenderer(this, 35, 86)
		shape81.setRotationPoint(-2.0f, 5.5f, 4.4f)
		shape81.addBox(-1.0f, -1.0f, -1.0f, 3, 7, 1, 0.0f)
		setRotateAngle(shape81, 0.08726646f, 0.08726646f, 0.0f)
		shape75 = ModelRenderer(this, 30, 80)
		shape75.mirror = true
		shape75.setRotationPoint(-2.0f, -1.0f, -4.4f)
		shape75.addBox(-1.0f, -1.0f, 0.5f, 5, 6, 3, 0.0f)
		setRotateAngle(shape75, -0.12217305f, -0.08726646f, 0.0f)
		body = ModelRenderer(this, 24, 24)
		body.setRotationPoint(0.0f, -12.0f, 0.0f)
		body.addBox(-6.0f, 0.0f, -3.0f, 12, 18, 6, 0.0f)
		shape89 = ModelRenderer(this, 35, 86)
		shape89.mirror = true
		shape89.setRotationPoint(2.0f, 5.5f, 4.4f)
		shape89.addBox(-2.0f, -1.0f, -1.0f, 3, 7, 1, 0.0f)
		setRotateAngle(shape89, 0.08726646f, -0.08726646f, 0.0f)
		head = ModelRenderer(this, 0, 0)
		head.setRotationPoint(0.0f, -12.0f, 0.0f)
		head.addBox(-6.0f, -12.0f, -6.0f, 12, 12, 12, 0.0f)
		shape8 = ModelRenderer(this, 0, 74)
		shape8.setRotationPoint(-4.2f, -10.5f, -6.1f)
		shape8.addBox(-2.0f, -1.0f, -1.0f, 5, 6, 2, 0.0f)
		setRotateAngle(shape8, 0.2645919f, -0.023736477f, -1.2039281f)
		shape28 = ModelRenderer(this, 5, 79)
		shape28.setRotationPoint(0.0f, -3.0f, -1.0f)
		shape28.addBox(0.5f, 6.0f, 0.0f, 2, 3, 1, 0.0f)
		setRotateAngle(shape28, 0.034906585f, -0.13962634f, 0.12217305f)
		shape57 = ModelRenderer(this, 84, 84)
		shape57.setRotationPoint(0.0f, 3.0f, 0.0f)
		shape57.addBox(0.0f, 0.0f, 0.0f, 4, 2, 1, 0.0f)
		setRotateAngle(shape57, -0.17453292f, -0.034906585f, 0.0f)
		shape34 = ModelRenderer(this, 10, 80)
		shape34.setRotationPoint(0.0f, -2.0f, 0.5f)
		shape34.addBox(-0.5f, -2.0f, -2.0f, 3, 3, 4, 0.0f)
		setRotateAngle(shape34, 0.0f, 0.0f, 0.28797933f)
		shape10 = ModelRenderer(this, 0, 76)
		shape10.setRotationPoint(0.0f, -5.3f, 1.6f)
		shape10.addBox(-1.5f, 0.0f, -1.0f, 4, 5, 2, 0.0f)
		setRotateAngle(shape10, -0.31817353f, 0.0f, 0.0f)
		shape19 = ModelRenderer(this, 0, 80)
		shape19.setRotationPoint(0.1f, -5.5f, 0.5f)
		shape19.addBox(-2.0f, -5.0f, 0.5f, 3, 5, 1, 0.0f)
		setRotateAngle(shape19, -0.7330383f, 0.091106184f, -0.21816616f)
		leftleg = ModelRenderer(this, 0, 24)
		leftleg.setRotationPoint(3.0f, 6.0f, 0.0f)
		leftleg.addBox(-3.0f, 0.0f, -3.0f, 6, 18, 6, 0.0f)
		shape30 = ModelRenderer(this, 4, 80)
		shape30.setRotationPoint(4.5f, -1.6f, 0.5f)
		shape30.addBox(-3.0f, -1.5f, -3.0f, 4, 2, 5, 0.0f)
		setRotateAngle(shape30, 0.0f, 0.0f, 0.5934119f)
		shape84 = ModelRenderer(this, 30, 80)
		shape84.setRotationPoint(-2.9f, 0.0f, -3.0f)
		shape84.addBox(-1.0f, -2.0f, 0.0f, 2, 7, 3, 0.0f)
		setRotateAngle(shape84, -0.12217305f, 0.87266463f, 0.0f)
		shape87 = ModelRenderer(this, 30, 80)
		shape87.mirror = true
		shape87.setRotationPoint(2.0f, -1.0f, 4.0f)
		shape87.addBox(-4.0f, -1.0f, -3.0f, 5, 7, 3, 0.0f)
		setRotateAngle(shape87, 0.12217305f, -0.08726646f, 0.0f)
		shape67 = ModelRenderer(this, 30, 80)
		shape67.mirror = true
		shape67.setRotationPoint(1.0f, 3.0f, 0.5f)
		shape67.addBox(-1.0f, -1.0f, 0.0f, 5, 4, 1, 0.0f)
		setRotateAngle(shape67, -0.17453292f, -0.08726646f, 0.0f)
		shape61 = ModelRenderer(this, 80, 80)
		shape61.setRotationPoint(-6.1f, 1.0f, 1.5f)
		shape61.addBox(-1.0f, -3.0f, 0.0f, 2, 6, 1, 0.0f)
		setRotateAngle(shape61, 0.0f, 0.87266463f, 0.0f)
		shape59 = ModelRenderer(this, 67, 82)
		shape59.setRotationPoint(0.0f, 2.0f, 0.7f)
		shape59.addBox(-5.5f, -4.0f, 0.0f, 11, 6, 1, 0.0f)
		shape68 = ModelRenderer(this, 30, 80)
		shape68.setRotationPoint(-1.0f, 3.0f, 0.5f)
		shape68.addBox(-4.0f, -1.0f, 0.0f, 5, 4, 1, 0.0f)
		setRotateAngle(shape68, -0.17453292f, 0.08726646f, 0.0f)
		shape31 = ModelRenderer(this, 5, 80)
		shape31.setRotationPoint(0.0f, -2.0f, -0.5f)
		shape31.addBox(-2.5f, -2.0f, -2.0f, 3, 3, 4, 0.0f)
		setRotateAngle(shape31, 0.0f, 0.0f, -0.28797933f)
		shape51 = ModelRenderer(this, 80, 82)
		shape51.setRotationPoint(4.0f, 2.0f, 8.0f)
		shape51.addBox(-3.0f, -2.0f, -0.3f, 5, 4, 1, 0.0f)
		setRotateAngle(shape51, -0.05235988f, 0.20943952f, 0.0f)
		shape88 = ModelRenderer(this, 30, 80)
		shape88.setRotationPoint(-3.0f, 0.0f, 3.0f)
		shape88.addBox(-1.0f, -2.0f, -3.0f, 2, 8, 3, 0.0f)
		setRotateAngle(shape88, 0.12217305f, -0.87266463f, 0.0f)
		shape69 = ModelRenderer(this, 30, 80)
		shape69.setRotationPoint(6.0f, 4.0f, 1.4f)
		shape69.addBox(-1.0f, -1.0f, 0.0f, 2, 3, 1, 0.0f)
		setRotateAngle(shape69, -0.17453292f, -0.87266463f, 0.0f)
		shape62 = ModelRenderer(this, 80, 80)
		shape62.setRotationPoint(-6.2f, 1.0f, 7.2f)
		shape62.addBox(-1.0f, -3.0f, 0.0f, 2, 6, 1, 0.0f)
		setRotateAngle(shape62, 0.0f, 2.268928f, 0.0f)
		shape18 = ModelRenderer(this, 0, 76)
		shape18.setRotationPoint(0.0f, -5.3f, 1.6f)
		shape18.addBox(-2.5f, 0.0f, -1.0f, 4, 5, 2, 0.0f)
		setRotateAngle(shape18, -0.31817353f, 0.0f, 0.0f)
		shape48 = ModelRenderer(this, 85, 82)
		shape48.setRotationPoint(-4.0f, 1.0f, 0.0f)
		shape48.addBox(-2.0f, -2.0f, 0.0f, 2, 4, 1, 0.0f)
		setRotateAngle(shape48, 0.0f, 0.13962634f, 0.0f)
		shape65 = ModelRenderer(this, 33, 80)
		shape65.setRotationPoint(6.5f, 1.5f, 4.5f)
		shape65.addBox(-2.0f, -3.0f, 0.0f, 4, 5, 1, 0.0f)
		setRotateAngle(shape65, 0.0f, -1.5707964f, 0.0f)
		shape29 = ModelRenderer(this, 5, 80)
		shape29.setRotationPoint(-0.5f, -3.0f, -1.0f)
		shape29.addBox(-2.0f, 5.9f, 0.0f, 2, 3, 1, 0.0f)
		setRotateAngle(shape29, 0.034906585f, 0.13962634f, -0.12217305f)
		rightarm = ModelRenderer(this, 60, 24)
		rightarm.mirror = true
		rightarm.setRotationPoint(-2.0f, -9.0f, 0.0f)
		rightarm.addBox(-5.0f, -3.0f, -3.0f, 6, 18, 6, 0.0f)
		shape23 = ModelRenderer(this, 10, 90)
		shape23.setRotationPoint(0.0f, -3.0f, -1.0f)
		shape23.addBox(-4.0f, 1.0f, 0.0f, 2, 3, 2, 0.0f)
		setRotateAngle(shape23, -0.08726646f, 0.15899949f, -0.10471976f)
		shape77 = ModelRenderer(this, 30, 80)
		shape77.setRotationPoint(1.0f, 4.9f, -4.0f)
		shape77.addBox(-1.0f, -1.0f, 0.0f, 2, 5, 1, 0.0f)
		setRotateAngle(shape77, -0.08726646f, -0.17453292f, 0.0f)
		shape64 = ModelRenderer(this, 80, 80)
		shape64.setRotationPoint(6.2f, 1.0f, 7.2f)
		shape64.addBox(-1.0f, -3.0f, 0.0f, 2, 6, 1, 0.0f)
		setRotateAngle(shape64, 0.0f, -2.268928f, 0.0f)
		shape72 = ModelRenderer(this, 30, 80)
		shape72.mirror = true
		shape72.setRotationPoint(0.1f, 3.0f, 7.5f)
		shape72.addBox(-5.0f, 0.0f, 0.0f, 5, 3, 1, 0.0f)
		setRotateAngle(shape72, 0.17453292f, -0.08726646f, 0.0f)
		shape46 = ModelRenderer(this, 72, 84)
		shape46.setRotationPoint(0.0f, 3.0f, 0.0f)
		shape46.addBox(-4.0f, 0.0f, 0.0f, 4, 2, 1, 0.0f)
		setRotateAngle(shape46, -0.17453292f, 0.034906585f, 0.0f)
		shape32 = ModelRenderer(this, 10, 76)
		shape32.setRotationPoint(-1.2f, -5.5f, -0.5f)
		shape32.addBox(-2.0f, -1.0f, -1.0f, 2, 3, 2, 0.0f)
		setRotateAngle(shape32, 0.0f, 0.0f, -0.57595867f)
		shape83 = ModelRenderer(this, 30, 80)
		shape83.setRotationPoint(2.0f, -1.0f, -4.4f)
		shape83.addBox(-4.0f, -1.0f, 0.5f, 5, 6, 3, 0.0f)
		setRotateAngle(shape83, -0.12217305f, 0.08726646f, 0.0f)
		shape22 = ModelRenderer(this, 0, 90)
		shape22.setRotationPoint(0.1f, -3.0f, 0.0f)
		shape22.addBox(-4.0f, 0.0f, -1.0f, 4, 1, 1, 0.0f)
		setRotateAngle(shape22, -0.17453292f, 0.17453292f, -0.3090978f)
		shape86 = ModelRenderer(this, 30, 76)
		shape86.setRotationPoint(2.0f, 4.5f, -4.4f)
		shape86.addBox(-2.0f, -1.0f, 0.0f, 3, 7, 1, 0.0f)
		setRotateAngle(shape86, -0.08726646f, 0.08726646f, 0.0f)
		shape33 = ModelRenderer(this, 0, 80)
		shape33.setRotationPoint(-4.5f, -1.6f, -0.5f)
		shape33.addBox(-1.0f, -1.5f, -2.0f, 4, 2, 5, 0.0f)
		setRotateAngle(shape33, 0.0f, 0.0f, -0.5934119f)
		shape55 = ModelRenderer(this, 79, 85)
		shape55.setRotationPoint(-3.5f, -1.6f, 8.6f)
		shape55.addBox(-1.0f, -1.0f, -2.0f, 4, 3, 2, 0.0f)
		setRotateAngle(shape55, 0.13962634f, -0.17453292f, 0.0f)
		shape13 = ModelRenderer(this, 0, 80)
		shape13.setRotationPoint(0.1f, -5.5f, 0.5f)
		shape13.addBox(-1.5f, -5.0f, 0.5f, 3, 5, 1, 0.0f)
		setRotateAngle(shape13, -0.7330383f, 0.091106184f, 0.21816616f)
		shape78 = ModelRenderer(this, 30, 76)
		shape78.mirror = true
		shape78.setRotationPoint(-2.0f, 4.5f, -4.4f)
		shape78.addBox(-1.0f, -1.0f, 0.0f, 3, 7, 1, 0.0f)
		setRotateAngle(shape78, -0.08726646f, -0.08726646f, 0.0f)
		shape24 = ModelRenderer(this, 10, 90)
		shape24.setRotationPoint(0.0f, -3.0f, -1.0f)
		shape24.addBox(2.0f, 1.0f, 0.0f, 2, 3, 2, 0.0f)
		setRotateAngle(shape24, -0.08726646f, -0.15899949f, 0.10471976f)
		shape74 = ModelRenderer(this, 30, 80)
		shape74.setRotationPoint(-6.1f, 4.0f, 7.3f)
		shape74.addBox(-1.0f, -1.0f, -1.0f, 2, 3, 1, 0.0f)
		setRotateAngle(shape74, 0.17453292f, -0.87266463f, 0.0f)
		shape17 = ModelRenderer(this, 0, 74)
		shape17.setRotationPoint(4.2f, -10.5f, -6.1f)
		shape17.addBox(-3.0f, -1.0f, -1.0f, 5, 6, 2, 0.0f)
		setRotateAngle(shape17, 0.2645919f, -0.023736477f, 1.2039281f)
		shape82 = ModelRenderer(this, 30, 80)
		shape82.setRotationPoint(1.0f, 4.9f, 4.0f)
		shape82.addBox(-1.0f, -1.0f, -1.0f, 2, 6, 1, 0.0f)
		setRotateAngle(shape82, 0.08726646f, 0.17453292f, 0.0f)
		shape44 = ModelRenderer(this, 80, 91)
		shape44.setRotationPoint(0.0f, 13.0f, -4.4f)
		shape44.addBox(-4.0f, -1.0f, 0.0f, 8, 4, 1, 0.0f)
		shape54 = ModelRenderer(this, 30, 84)
		shape54.setRotationPoint(0.0f, 1.0f, -0.3f)
		shape54.addBox(-3.0f, -1.0f, 0.0f, 3, 2, 1, 0.0f)
		setRotateAngle(shape54, 0.0f, 0.10471976f, 0.0f)
		shape45 = ModelRenderer(this, 75, 82)
		shape45.setRotationPoint(0.0f, -1.0f, -0.3f)
		shape45.addBox(-4.0f, -2.0f, 0.0f, 4, 2, 1, 0.0f)
		setRotateAngle(shape45, -0.20943952f, 0.06981317f, 0.0f)
		shape52 = ModelRenderer(this, 80, 88)
		shape52.setRotationPoint(0.0f, 0.0f, 9.2f)
		shape52.addBox(-2.0f, -1.0f, -1.7f, 4, 5, 2, 0.0f)
		setRotateAngle(shape52, -0.06981317f, 0.0f, 0.0f)
		shape70 = ModelRenderer(this, 30, 80)
		shape70.setRotationPoint(-6.0f, 4.0f, 1.4f)
		shape70.addBox(-1.0f, -1.0f, 0.0f, 2, 3, 1, 0.0f)
		setRotateAngle(shape70, -0.17453292f, 0.87266463f, 0.0f)
		shape50 = ModelRenderer(this, 80, 82)
		shape50.setRotationPoint(-4.0f, 2.0f, 8.0f)
		shape50.addBox(-2.0f, -2.0f, -0.3f, 5, 4, 1, 0.0f)
		setRotateAngle(shape50, -0.05235988f, -0.20943952f, 0.0f)
		shape66 = ModelRenderer(this, 33, 80)
		shape66.setRotationPoint(-6.5f, 1.5f, 4.5f)
		shape66.addBox(-2.0f, -3.0f, 0.0f, 4, 5, 1, 0.0f)
		setRotateAngle(shape66, 0.0f, 1.5707964f, 0.0f)
		shape56 = ModelRenderer(this, 80, 86)
		shape56.setRotationPoint(3.5f, -1.6f, 8.6f)
		shape56.addBox(-3.0f, -1.0f, -2.0f, 4, 3, 2, 0.0f)
		setRotateAngle(shape56, 0.13962634f, 0.17453292f, 0.0f)
		shape58 = ModelRenderer(this, 86, 86)
		shape58.setRotationPoint(0.0f, -1.0f, -0.3f)
		shape58.addBox(0.0f, -2.0f, 0.0f, 4, 2, 1, 0.0f)
		setRotateAngle(shape58, -0.20943952f, -0.06981317f, 0.0f)
		shape63 = ModelRenderer(this, 80, 80)
		shape63.setRotationPoint(6.1f, 1.0f, 1.5f)
		shape63.addBox(-1.0f, -3.0f, 0.0f, 2, 6, 1, 0.0f)
		setRotateAngle(shape63, 0.0f, -0.87266463f, 0.0f)
		leftarm = ModelRenderer(this, 60, 24)
		leftarm.setRotationPoint(2.0f, -9.0f, 0.0f)
		leftarm.addBox(-1.0f, -3.0f, -3.0f, 6, 18, 6, 0.0f)
		rightleg = ModelRenderer(this, 0, 24)
		rightleg.mirror = true
		rightleg.setRotationPoint(-3.0f, 6.0f, 0.0f)
		rightleg.addBox(-3.0f, 0.0f, -3.0f, 6, 18, 6, 0.0f)
		shape20 = ModelRenderer(this, 0, 84)
		shape20.setRotationPoint(0.0f, -0.5f, -5.7f)
		shape20.addBox(-2.0f, -1.0f, -0.8f, 4, 2, 2, 0.0f)
		shape27 = ModelRenderer(this, 15, 77)
		shape27.setRotationPoint(0.0f, -3.0f, -1.0f)
		shape27.addBox(1.5f, 3.0f, 0.0f, 2, 4, 1, 0.0f)
		setRotateAngle(shape27, 0.02617994f, -0.10471976f, 0.17453292f)
		shape25 = ModelRenderer(this, 15, 90)
		shape25.setRotationPoint(0.0f, -3.0f, -1.0f)
		shape25.addBox(-1.0f, 2.4f, -0.3f, 2, 4, 1, 0.0f)
		setRotateAngle(shape25, 0.061086524f, 0.0f, 0.0f)
		shape47 = ModelRenderer(this, 73, 82)
		shape47.setRotationPoint(4.0f, 1.0f, 0.0f)
		shape47.addBox(0.0f, -2.0f, 0.0f, 2, 4, 1, 0.0f)
		setRotateAngle(shape47, 0.0f, -0.13962634f, 0.0f)
		shape60 = ModelRenderer(this, 70, 80)
		shape60.setRotationPoint(0.0f, 2.0f, 8.0f)
		shape60.addBox(-5.5f, -4.0f, -1.0f, 11, 6, 1, 0.0f)
		shape71 = ModelRenderer(this, 30, 80)
		shape71.setRotationPoint(-0.1f, 3.0f, 7.5f)
		shape71.addBox(0.0f, 0.0f, 0.0f, 5, 3, 1, 0.0f)
		setRotateAngle(shape71, 0.17453292f, 0.08726646f, 0.0f)
		shape73 = ModelRenderer(this, 30, 80)
		shape73.setRotationPoint(6.1f, 4.0f, 7.3f)
		shape73.addBox(-1.0f, -1.0f, -1.0f, 2, 3, 1, 0.0f)
		setRotateAngle(shape73, 0.17453292f, 0.87266463f, 0.0f)
		shape21 = ModelRenderer(this, 0, 90)
		shape21.setRotationPoint(-0.1f, -3.0f, 0.0f)
		shape21.addBox(0.0f, 0.0f, -1.0f, 4, 1, 2, 0.0f)
		setRotateAngle(shape21, -0.17453292f, -0.17453292f, 0.3090978f)
		shape80 = ModelRenderer(this, 30, 80)
		shape80.setRotationPoint(3.0f, 0.0f, 3.0f)
		shape80.addBox(-1.0f, -2.0f, -3.0f, 2, 8, 3, 0.0f)
		setRotateAngle(shape80, 0.12217305f, 0.87266463f, 0.0f)
		shape53 = ModelRenderer(this, 86, 86)
		shape53.setRotationPoint(0.0f, -2.1f, 6.2f)
		shape53.addBox(-1.0f, -1.0f, -0.1f, 2, 3, 3, 0.0f)
		setRotateAngle(shape53, 0.2617994f, 0.0f, 0.0f)
		shape35 = ModelRenderer(this, 15, 76)
		shape35.setRotationPoint(1.2f, -5.5f, 0.5f)
		shape35.addBox(0.0f, -1.0f, -1.0f, 2, 3, 2, 0.0f)
		setRotateAngle(shape35, 0.0f, 0.0f, 0.57595867f)
		shape85 = ModelRenderer(this, 30, 80)
		shape85.setRotationPoint(-1.0f, 4.9f, -4.0f)
		shape85.addBox(-1.0f, -1.0f, 0.0f, 2, 5, 1, 0.0f)
		setRotateAngle(shape85, -0.08726646f, 0.17453292f, 0.0f)
		shape90 = ModelRenderer(this, 30, 80)
		shape90.setRotationPoint(-1.0f, 4.9f, 4.0f)
		shape90.addBox(-1.0f, -1.0f, -1.0f, 2, 6, 1, 0.0f)
		setRotateAngle(shape90, 0.08726646f, -0.17453292f, 0.0f)
		shape79 = ModelRenderer(this, 30, 80)
		shape79.setRotationPoint(-2.0f, -1.0f, 4.0f)
		shape79.addBox(-1.0f, -1.0f, -3.0f, 5, 7, 3, 0.0f)
		setRotateAngle(shape79, 0.12217305f, 0.08726646f, 0.0f)
		shape49 = ModelRenderer(this, 30, 89)
		shape49.setRotationPoint(0.0f, 1.0f, -0.3f)
		shape49.addBox(0.0f, -1.0f, 0.0f, 3, 2, 1, 0.0f)
		setRotateAngle(shape49, 0.0f, -0.10471976f, 0.0f)
		shape26 = ModelRenderer(this, 15, 76)
		shape26.setRotationPoint(0.5f, -3.0f, -1.0f)
		shape26.addBox(-4.0f, 3.0f, -0.2f, 2, 4, 1, 0.0f)
		setRotateAngle(shape26, 0.02617994f, 0.10471976f, -0.17453292f)
		
		reassignDefaultParts()
		
		leftleg.addChild(shape76)
		leftleg.addChild(shape81)
		leftleg.addChild(shape75)
		rightleg.addChild(shape89)
		head.addChild(shape8)
		shape20.addChild(shape28)
		shape44.addChild(shape57)
		shape33.addChild(shape34)
		shape8.addChild(shape10)
		shape17.addChild(shape19)
		leftarm.addChild(shape30)
		rightleg.addChild(shape84)
		rightleg.addChild(shape87)
		shape44.addChild(shape67)
		shape44.addChild(shape61)
		shape44.addChild(shape59)
		shape44.addChild(shape68)
		shape30.addChild(shape31)
		shape44.addChild(shape51)
		rightleg.addChild(shape88)
		shape44.addChild(shape69)
		shape44.addChild(shape62)
		shape17.addChild(shape18)
		shape44.addChild(shape48)
		shape44.addChild(shape65)
		shape20.addChild(shape29)
		shape20.addChild(shape23)
		leftleg.addChild(shape77)
		shape44.addChild(shape64)
		shape44.addChild(shape72)
		shape44.addChild(shape46)
		shape30.addChild(shape32)
		rightleg.addChild(shape83)
		shape20.addChild(shape22)
		rightleg.addChild(shape86)
		rightarm.addChild(shape33)
		shape44.addChild(shape55)
		shape8.addChild(shape13)
		leftleg.addChild(shape78)
		shape20.addChild(shape24)
		shape44.addChild(shape74)
		head.addChild(shape17)
		leftleg.addChild(shape82)
		body.addChild(shape44)
		shape44.addChild(shape54)
		shape44.addChild(shape45)
		shape44.addChild(shape52)
		shape44.addChild(shape70)
		shape44.addChild(shape50)
		shape44.addChild(shape66)
		shape44.addChild(shape56)
		shape44.addChild(shape58)
		shape44.addChild(shape63)
		head.addChild(shape20)
		shape20.addChild(shape27)
		shape20.addChild(shape25)
		shape44.addChild(shape47)
		shape44.addChild(shape60)
		shape44.addChild(shape71)
		shape44.addChild(shape73)
		shape20.addChild(shape21)
		leftleg.addChild(shape80)
		shape44.addChild(shape53)
		shape33.addChild(shape35)
		rightleg.addChild(shape85)
		rightleg.addChild(shape90)
		leftleg.addChild(shape79)
		shape44.addChild(shape49)
		shape20.addChild(shape26)
	}
	
	override fun render(entity: Entity?, f: Float, f1: Float, f2: Float, f3: Float, f4: Float, f5: Float) {
		glPushMatrix()
		glTranslatef(0f, -2.25f, 0f)
		glScalef(2.5f)
		super.render(entity, f, f1, f2, f3, f4, f5)
		glPopMatrix()
	}
	
	override fun setRotationAngles(f: Float, f1: Float, f2: Float, f3: Float, f4: Float, f5: Float, entity: Entity?) {
		super.setRotationAngles(f, f1, f2, f3, f4, f5, entity)
		entity as EntitySurtr
		
		val rotate = ASJBitwiseHelper.getBit(entity.ultAnimationTicks, 9)
		val ult = if (!rotate) entity.ultAnimationTicks else 0
		
		if (ult in 1..85) return
		
		rightarm.rotationPointX /= 3.6f
		leftarm.rotationPointX /= 3.6f
		
		rightarm.rotationPointX *= 1.4f
		leftarm.rotationPointX *= 1.4f
	}
	
	override fun getSuperSmashParticlesColor() = floatArrayOf(1f, 0.3f, 0.5f)
	
	override fun getSuperSmashParticlesBlockIDs() = arrayOf(Blocks.lava, Blocks.fire, AlfheimBlocks.redFlame).map { Block.getIdFromBlock(it) }
}