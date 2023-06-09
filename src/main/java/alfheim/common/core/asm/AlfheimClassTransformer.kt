package alfheim.common.core.asm

import alfheim.api.ModInfo.OBF
import alfheim.common.core.handler.AlfheimConfigHandler
import net.minecraft.launchwrapper.IClassTransformer
import org.lwjgl.opengl.GL11
import org.objectweb.asm.*
import org.objectweb.asm.Opcodes.*
import org.objectweb.asm.tree.*
import vazkii.botania.api.subtile.SubTileEntity

@Suppress("NAME_SHADOWING", "ClassName", "unused", "LocalVariableName", "PrivatePropertyName", "MayBeConstant")
class AlfheimClassTransformer: IClassTransformer {
	
	val additionalInterfaces = mapOf(
		"net.minecraft.entity.monster.EntityCreeper" to setOf("alfheim/common/core/helper/IElementalEntity"),
		"net.minecraft.entity.monster.EntitySkeleton" to setOf("alfheim/common/core/helper/IElementalEntity"),
		"thaumcraft.common.entities.golems.EntityGolemBase" to setOf("alfheim/common/core/helper/IElementalEntity"),
		"thaumcraft.common.entities.monster.EntityWisp" to setOf("alfheim/common/core/helper/IElementalEntity"),
									)
	
	/** name for logging */
	var transformedName = ""
	var basicClass = byteArrayOf()
	
	override fun transform(name: String, transformedName: String, basicClass: ByteArray?): ByteArray? {
		@Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")
		transformedName as java.lang.String // fix of java.lang.ClassCircularityError: kotlin/text/StringsKt
		
		if (transformedName.startsWith("kotlin") || transformedName.startsWith("gloomyfolken")) return basicClass
		
		if (basicClass == null || basicClass.isEmpty()) return basicClass
		
		this.transformedName = transformedName
		this.basicClass = basicClass
		
		additionalInterfaces[transformedName]?.let { iface ->
			println("Appending interface(s) $iface to $transformedName")
			val cr = ClassReader(basicClass)
			val cw = ClassWriter(ClassWriter.COMPUTE_MAXS)
			
			val cn = ClassNode()
			cr.accept(cn, ClassReader.EXPAND_FRAMES)
			
			cn.interfaces.addAll(iface)
			
			cn.accept(cw)
			return cw.toByteArray()
		}
		
		return when (transformedName) {
			"net.minecraft.client.renderer.RenderGlobal"                       -> core { `RenderGlobal$ClassVisitor`(it) }
			"net.minecraft.entity.EntityLivingBase"                            -> core { `EntityLivingBase$ClassVisitor`(it) }
			"net.minecraft.entity.EntityTrackerEntry"                          -> core { `EntityTrackerEntry$ClassVisitor`(it) }
			"net.minecraft.potion.Potion"                                      -> core { `Potion$ClassVisitor`(it) }
			"thaumcraft.common.items.ItemNugget"                               -> core { `ItemNugget$ClassVisitor`(it) }
			"vazkii.botania.client.core.handler.BaubleRenderHandler"           -> core { `BaubleRenderHandler$ClassVisitor`(it) }
			"vazkii.botania.client.core.handler.LightningHandler"              -> core { `LightningHandler$ClassVisitor`(it) }
			"vazkii.botania.client.core.handler.TooltipAdditionDisplayHandler" -> core { `TooltipAdditionDisplayHandler$ClassVisitor`(it) }
			"vazkii.botania.client.render.tile.RenderTileFloatingFlower"       -> core { `RenderTileFloatingFlower$ClassVisitor`(it) }
			
			"vazkii.botania.common.block.decor.IFloatingFlower\$IslandType"    -> tree {
				if (OBF || it.methods.any { m -> m.name == "getColor" && m.desc == "()I"}) return@tree
				println("Transforming $transformedName")
				
				val mn = MethodNode(ACC_PUBLIC, "getColor", "()I", null, null)
				mn.instructions.add(LdcInsnNode(Integer(16777215)))
				mn.instructions.add(InsnNode(IRETURN))
				it.methods.add(mn)
			}
			
			"vazkii.botania.common.block.tile.TileManaFlame"                   -> core { `TileManaFlame$ClassVisitor`(it) }
			"vazkii.botania.common.block.tile.TileSpecialFlower"               -> core { `TileSpecialFlower$ClassVisitor`(it) }
			"vazkii.botania.common.entity.EntityDoppleganger"                  -> core(ClassReader.SKIP_FRAMES) { `EntityDoppleganger$ClassVisitor`(it) }
			"vazkii.botania.common.item.ItemFlowerBag"                         -> core { `ItemFlowerBag$ClassVisitor`(it) }
			"vazkii.botania.common.item.equipment.bauble.ItemMiningRing",
			"vazkii.botania.common.item.equipment.bauble.ItemWaterRing"        -> core { `ItemInfiniEffect$ClassVisitor`(transformedName.split("\\.".toRegex())[6], it) }
			"vazkii.botania.common.item.lens.ItemLens"                         -> core { `ItemLens$ClassVisitor`(it) }
			"vazkii.botania.common.item.relic.ItemAesirRing"                   -> core { `ItemAesirRing$ClassVisitor`(it) }
			"vazkii.botania.common.item.rod.ItemTerraformRod"                  -> core { `ItemTerraformRod$ClassVisitor`(it) }
			"vazkii.botania.common.lib.LibItemNames"                           -> core { `LibItemNames$ClassVisitor`(it) }
			// fixes for stupid coders:
			"cofh.thermalfoundation.fluid.TFFluids"                            -> core { `TFFluids$ClassVisitor`(it) }
			"com.emoniph.witchery.client.ClientEvents\$GUIOverlay"             -> core { `ClientEvents$GUIOverlay$ClassVisitor`(it) }
			else                                                               -> basicClass
		}
	}
	
	inline fun core(frames: Int = ClassReader.EXPAND_FRAMES, lambda: (ClassVisitor) -> ClassVisitor): ByteArray {
		println("Transforming $transformedName")
		val cr = ClassReader(basicClass)
		val cw = ClassWriter(ClassWriter.COMPUTE_MAXS)
		val transformer = lambda(cw)
		cr.accept(transformer, frames)
		return cw.toByteArray()
	}
	
	inline fun tree(lambda: (ClassNode) -> Unit): ByteArray {
		println("Transforming $transformedName")
		val cr = ClassReader(basicClass)
		val it = ClassWriter(ClassWriter.COMPUTE_MAXS)
		val cn = ClassNode()
		cr.accept(cn, ClassReader.EXPAND_FRAMES)
		
		lambda(cn)
		
		cn.accept(it)
		return it.toByteArray()
	}
	
	// Gleipnir hook
	internal class `RenderGlobal$ClassVisitor`(cv: ClassVisitor): ClassVisitor(ASM5, cv) {
		
		override fun visitMethod(access: Int, name: String, desc: String, signature: String?, exceptions: Array<String>?): MethodVisitor {
			if (name == "renderEntities" || name == "a" && desc == "(Lsv;Lbmv;F)V") {
				println("Visiting RenderGlobal#renderEntities: $name$desc")
				return `RenderGlobal$addEffect$MethodVisitor`(super.visitMethod(access, name, desc, signature, exceptions))
			}
			return super.visitMethod(access, name, desc, signature, exceptions)
		}
		
		internal class `RenderGlobal$addEffect$MethodVisitor`(mv: MethodVisitor): MethodVisitor(ASM5, mv) {
			
			var inject = true
			
			override fun visitVarInsn(opcode: Int, `var`: Int) {
				super.visitVarInsn(opcode, `var`)
				
				if (inject && opcode == ISTORE && `var` == 21) {
					inject = false
					
					mv.visitFieldInsn(GETSTATIC, "alfheim/common/item/relic/LeashingHandler", "INSTANCE", "Lalfheim/common/item/relic/LeashingHandler;")
					mv.visitVarInsn(ILOAD, 21)
					mv.visitVarInsn(ALOAD, 20)
					mv.visitVarInsn(ALOAD, 2)
					mv.visitMethodInsn(INVOKEVIRTUAL, "alfheim/common/item/relic/LeashingHandler", "isBoundInRender", if (OBF) "(ZLsa;Lbmv;)Z" else "(ZLnet/minecraft/entity/Entity;Lnet/minecraft/client/renderer/culling/ICamera;)Z", false)
					mv.visitVarInsn(ISTORE, 21)
				}
			}
		}
	}
	
	internal class `EntityLivingBase$ClassVisitor`(cv: ClassVisitor): ClassVisitor(ASM5, cv) {
		
		override fun visitMethod(access: Int, name: String, desc: String, signature: String?, exceptions: Array<String>?): MethodVisitor {
			if (name == (if (OBF) "e" else "moveEntityWithHeading") && desc == "(FF)V") {
				println("Visiting EntityLivingBase#moveEntityWithHeading: $name$desc")
				return `EntityLivingBase$moveEntityWithHeading$MethodVisitor`(super.visitMethod(access, name, desc, signature, exceptions))
			}
			return super.visitMethod(access, name, desc, signature, exceptions)
		}
		
		internal class `EntityLivingBase$moveEntityWithHeading$MethodVisitor`(mv: MethodVisitor): MethodVisitor(ASM5, mv) {
			
			override fun visitFieldInsn(opcode: Int, owner: String?, name: String?, desc: String?) {
				if (opcode == GETFIELD && owner == (if (OBF) "aji" else "net/minecraft/block/Block") && name == (if (OBF) "K" else "slipperiness") && desc == "F") {
					mv.visitVarInsn(ALOAD, 0)
					mv.visitTypeInsn(CHECKCAST, if (OBF) "sa" else "net/minecraft/entity/Entity")
					mv.visitMethodInsn(INVOKEVIRTUAL, if (OBF) "aji" else "net/minecraft/block/Block", "getRelativeSlipperiness", if (OBF) "(Lsa;)F" else "(Lnet/minecraft/entity/Entity;)F", false)
				} else
					super.visitFieldInsn(opcode, owner, name, desc)
			}
		}
	}
	
	internal class `EntityTrackerEntry$ClassVisitor`(cv: ClassVisitor): ClassVisitor(ASM5, cv) {
		
		override fun visitMethod(access: Int, name: String, desc: String, signature: String?, exceptions: Array<String>?): MethodVisitor {
			if (name == "tryStartWachingThis" || name == "b" && desc == "(Lmw;)V") {
				println("Visiting EntityTrackerEntry#tryStartWachingThis: $name$desc")
				return `EntityTrackerEntry$tryStartWachingThis$MethodVisitor`(super.visitMethod(access, name, desc, signature, exceptions))
			}
			return super.visitMethod(access, name, desc, signature, exceptions)
		}
		
		internal class `EntityTrackerEntry$tryStartWachingThis$MethodVisitor`(mv: MethodVisitor): MethodVisitor(ASM5, mv) {
			
			private var sended = false
			
			override fun visitVarInsn(opcode: Int, `var`: Int) {
				super.visitVarInsn(opcode, `var`)
				if (opcode == ALOAD && `var` == 6 && !sended) {
					sended = true
					visitVarInsn(ALOAD, 0)
					visitFieldInsn(GETFIELD, if (OBF) "my" else "net/minecraft/entity/EntityTrackerEntry", if (OBF) "a" else "myEntity", if (OBF) "Lsa;" else "Lnet/minecraft/entity/Entity;")
					visitMethodInsn(INVOKESTATIC, "alfheim/common/core/handler/CardinalSystem\$PartySystem", "notifySpawn", if (OBF) "(Lsa;)V" else "(Lnet/minecraft/entity/Entity;)V", false)
				}
			}
		}
	}
	
	internal class `Potion$ClassVisitor`(cv: ClassVisitor): ClassVisitor(ASM5, cv) {
		
		override fun visitMethod(access: Int, name: String, desc: String, signature: String?, exceptions: Array<String>?): MethodVisitor {
			if (name == "performEffect" || name == "a" && desc == "(Lsv;I)V") {
				println("Visiting Potion#performEffect: $name$desc")
				return `Potion$performEffect$MethodVisitor`(super.visitMethod(access, name, desc, signature, exceptions))
			}
			return super.visitMethod(access, name, desc, signature, exceptions)
		}
		
		internal class `Potion$performEffect$MethodVisitor`(mv: MethodVisitor): MethodVisitor(ASM5, mv) {
			
			var flag = false
			
			override fun visitFieldInsn(opcode: Int, owner: String, name: String, desc: String) {
				if (flag && opcode == GETSTATIC && (owner == "net/minecraft/util/DamageSource" || owner == "ro") && (name == "magic" || name == "k") && (desc == "Lnet/minecraft/util/DamageSource;" || desc == "Lro;")) {
					flag = false
					super.visitFieldInsn(GETSTATIC, "alfheim/common/core/util/DamageSourceSpell", "Companion", "Lalfheim/common/core/util/DamageSourceSpell\$Companion;")
					super.visitMethodInsn(INVOKEVIRTUAL, "alfheim/common/core/util/DamageSourceSpell\$Companion", "getPoison", if (OBF) "()Lro;" else "()Lnet/minecraft/util/DamageSource;", false)
					return
				} else if (opcode == GETSTATIC && (owner == "net/minecraft/potion/Potion" || owner == "rv") && (name == "poison" || name == "u") && (desc == "Lnet/minecraft/potion/Potion;" || desc == "Lrv;")) flag = true
				
				super.visitFieldInsn(opcode, owner, name, desc)
			}
		}
	}
	
	internal class `ItemNugget$ClassVisitor`(cv: ClassVisitor): ClassVisitor(ASM5, cv) {
		
		override fun visitMethod(access: Int, name: String, desc: String, signature: String?, exceptions: Array<String>?): MethodVisitor {
			if (name == "registerIcons" || (name == "func_94581_a")) {
				println("Visiting ItemNugget#registerIcons: $name$desc")
				return `ItemNugget$registerIcons$MethodVisitor`(super.visitMethod(access, name, desc, signature, exceptions))
			}
			if (name == "getSubItems" || (name == "func_150895_a")) {
				println("Visiting ItemNugget#getSubItems: $name$desc")
				return `ItemNugget$getSubItems$MethodVisitor`(super.visitMethod(access, name, desc, signature, exceptions))
			}
			return super.visitMethod(access, name, desc, signature, exceptions)
		}
		
		internal class `ItemNugget$registerIcons$MethodVisitor`(mv: MethodVisitor): MethodVisitor(ASM5, mv) {
			
			override fun visitInsn(opcode: Int) {
				if (opcode == RETURN) {
					visitVarInsn(ALOAD, 0)
					visitFieldInsn(GETFIELD, "thaumcraft/common/items/ItemNugget", "icon", if (OBF) "[Lrf;" else "[Lnet/minecraft/util/IIcon;")
					visitIntInsn(BIPUSH, AlfheimConfigHandler.elementiumClusterMeta)
					visitVarInsn(ALOAD, 1)
					visitLdcInsn("thaumcraft:clusterelementium")
					visitMethodInsn(INVOKEINTERFACE, if (OBF) "rg" else "net/minecraft/client/renderer/texture/IIconRegister", if (OBF) "a" else "registerIcon", if (OBF) "(Ljava/lang/String;)Lrf;" else "(Ljava/lang/String;)Lnet/minecraft/util/IIcon;", true)
					visitInsn(AASTORE)
					val l15_5 = Label()
					visitLabel(l15_5)
					visitLineNumber(47, l15_5)
				}
				super.visitInsn(opcode)
			}
		}
		
		internal class `ItemNugget$getSubItems$MethodVisitor`(mv: MethodVisitor): MethodVisitor(ASM5, mv) {
			
			override fun visitInsn(opcode: Int) {
				if (opcode == RETURN) {
					visitVarInsn(ALOAD, 3)
					visitTypeInsn(NEW, if (OBF) "add" else "net/minecraft/item/ItemStack")
					visitInsn(DUP)
					visitVarInsn(ALOAD, 0)
					visitInsn(ICONST_1)
					visitIntInsn(BIPUSH, AlfheimConfigHandler.elementiumClusterMeta)
					visitMethodInsn(INVOKESPECIAL, if (OBF) "add" else "net/minecraft/item/ItemStack", "<init>", if (OBF) "(Ladb;II)V" else "(Lnet/minecraft/item/Item;II)V", false)
					visitMethodInsn(INVOKEINTERFACE, "java/util/List", "add", "(Ljava/lang/Object;)Z", true)
					visitInsn(POP)
				}
				super.visitInsn(opcode)
			}
		}
	}
	
	internal class `LightningHandler$ClassVisitor`(cv: ClassVisitor): ClassVisitor(ASM5, cv) {
		
		override fun visitMethod(access: Int, name: String, desc: String, signature: String?, exceptions: Array<String>?): MethodVisitor {
			if (name == "onRenderWorldLast") {
				println("Visiting LightningHandler#onRenderWorldLast: $name$desc")
				return `LightningHandler$onRenderWorldLast$MethodVisitor`(super.visitMethod(access, name, desc, signature, exceptions))
			}
			return super.visitMethod(access, name, desc, signature, exceptions)
		}
		
		internal class `LightningHandler$onRenderWorldLast$MethodVisitor`(mv: MethodVisitor): MethodVisitor(ASM5, mv) {
			
			override fun visitMethodInsn(opcode: Int, owner: String, name: String, desc: String, itf: Boolean) {
				super.visitMethodInsn(opcode, owner, name, desc, itf)
				
				if (opcode == INVOKESTATIC) {
					if (name == "glPushMatrix") {
						mv.visitIntInsn(SIPUSH, GL11.GL_CULL_FACE)
						mv.visitMethodInsn(INVOKESTATIC, "org/lwjgl/opengl/GL11", "glDisable", "(I)V", false)
					} else if (name == "glPopmatrix") {
						mv.visitIntInsn(SIPUSH, GL11.GL_CULL_FACE)
						mv.visitMethodInsn(INVOKESTATIC, "org/lwjgl/opengl/GL11", "glEnable", "(I)V", false)
					}
				}
			}
		}
	}
	
	internal class `TooltipAdditionDisplayHandler$ClassVisitor`(cv: ClassVisitor): ClassVisitor(ASM5, cv) {
		
		override fun visitMethod(access: Int, name: String, desc: String, signature: String?, exceptions: Array<String>?): MethodVisitor {
			if (name == "render") {
				println("Visiting TooltipAdditionDisplayHandler#render: $name$desc")
				return `TooltipAdditionDisplayHandler$render$MethodVisitor`(super.visitMethod(access, name, desc, signature, exceptions))
			}
			return super.visitMethod(access, name, desc, signature, exceptions)
		}
		
		internal class `TooltipAdditionDisplayHandler$render$MethodVisitor`(mv: MethodVisitor): MethodVisitor(ASM5, mv) {
			
			var gets = 0
			
			override fun visitFieldInsn(opcode: Int, owner: String?, name: String?, desc: String?) {
				if (opcode == GETFIELD && name == "entry" && ++gets == 2) return
				super.visitFieldInsn(opcode, owner, name, desc)
			}
			
			override fun visitMethodInsn(opcode: Int, owner: String?, name: String?, desc: String?, itf: Boolean) {
				var newName = name
				var newDesc = desc
				
				if (name == "setEntryToOpen") {
					newName = "setEntryDataToOpen"
					newDesc = "(Lvazkii/botania/api/lexicon/LexiconRecipeMappings\$EntryData;)V"
				}
				
				super.visitMethodInsn(opcode, owner, newName, newDesc, itf)
			}
		}
	}
	
	internal class `BaubleRenderHandler$ClassVisitor`(cv: ClassVisitor): ClassVisitor(ASM5, cv) {
		
		override fun visitMethod(access: Int, name: String, desc: String, signature: String?, exceptions: Array<String>?): MethodVisitor {
			if (name == "renderManaTablet") {
				println("Visiting BaubleRenderHandler#renderManaTablet: $name$desc")
				return `BaubleRenderHandler$renderManaTablet$MethodVisitor`(super.visitMethod(access, name, desc, signature, exceptions))
			}
			return super.visitMethod(access, name, desc, signature, exceptions)
		}
		
		internal class `BaubleRenderHandler$renderManaTablet$MethodVisitor`(mv: MethodVisitor): MethodVisitor(ASM5, mv) {
			
			override fun visitLdcInsn(cst: Any) {
				var cst = cst
				if (cst is Float && cst == 0.2f) cst = 0.33f
				super.visitLdcInsn(cst)
			}
		}
	}
	
	internal class `RenderTileFloatingFlower$ClassVisitor`(cv: ClassVisitor): ClassVisitor(ASM5, cv) {
		
		override fun visitMethod(access: Int, name: String, desc: String, signature: String?, exceptions: Array<String>?): MethodVisitor {
			if (name == "renderTileEntityAt") {
				println("Visiting RenderTileFloatingFlower#renderTileEntityAt: $name$desc")
				return `RenderTileFloatingFlower$renderTileEntityAt$MethodVisitor`(super.visitMethod(access, name, desc, signature, exceptions))
			}
			return super.visitMethod(access, name, desc, signature, exceptions)
		}
		
		internal class `RenderTileFloatingFlower$renderTileEntityAt$MethodVisitor`(mv: MethodVisitor): MethodVisitor(ASM5, mv) {
			
			var before = false
			var after = true
			
			override fun visitMethodInsn(opcode: Int, owner: String, name: String, desc: String?, itf: Boolean) {
				if (name == "glPushMatrix") {
					if (before) {
						mv.visitTypeInsn(NEW, "java/awt/Color")
						mv.visitInsn(DUP)
						mv.visitVarInsn(ALOAD, 9)
						mv.visitMethodInsn(INVOKEINTERFACE, "vazkii/botania/common/block/decor/IFloatingFlower", "getIslandType", "()Lvazkii/botania/common/block/decor/IFloatingFlower\$IslandType;", true)
						mv.visitMethodInsn(INVOKEVIRTUAL, "vazkii/botania/common/block/decor/IFloatingFlower\$IslandType", "getColor", "()I", false)
						mv.visitMethodInsn(INVOKESPECIAL, "java/awt/Color", "<init>", "(I)V", false)
						mv.visitVarInsn(ASTORE, 12)
						
						mv.visitVarInsn(ALOAD, 12)
						mv.visitMethodInsn(INVOKEVIRTUAL, "java/awt/Color", "getRed", "()I", false)
						mv.visitInsn(I2F)
						mv.visitLdcInsn(java.lang.Float("255.0"))
						mv.visitInsn(FDIV)
						mv.visitVarInsn(ALOAD, 12)
						mv.visitMethodInsn(INVOKEVIRTUAL, "java/awt/Color", "getGreen", "()I", false)
						mv.visitInsn(I2F)
						mv.visitLdcInsn(java.lang.Float("255.0"))
						mv.visitInsn(FDIV)
						mv.visitVarInsn(ALOAD, 12)
						mv.visitMethodInsn(INVOKEVIRTUAL, "java/awt/Color", "getBlue", "()I", false)
						mv.visitInsn(I2F)
						mv.visitLdcInsn(java.lang.Float("255.0"))
						mv.visitInsn(FDIV)
						mv.visitMethodInsn(INVOKESTATIC, "org/lwjgl/opengl/GL11", "glColor3f", "(FFF)V", false)
					} else before = true
				} else if (name == "glPopMatrix") {
					if (after) {
						mv.visitInsn(FCONST_1)
						mv.visitInsn(FCONST_1)
						mv.visitInsn(FCONST_1)
						mv.visitMethodInsn(INVOKESTATIC, "org/lwjgl/opengl/GL11", "glColor3f", "(FFF)V", false)
					} else after = false
				}
				
				super.visitMethodInsn(opcode, owner, name, desc, itf)
			}
		}
	}
	
	internal class `TileManaFlame$ClassVisitor`(cv: ClassVisitor): ClassVisitor(ASM5, cv) {
		
		override fun visitMethod(access: Int, name: String?, desc: String?, signature: String?, exceptions: Array<out String>?): MethodVisitor {
			val mv = super.visitMethod(access, name, desc, signature, exceptions)
			return if (name != "getColor" && name != "writeCustomNBT") `TileManaFlame$MethodVisitor`(mv) else mv
		}
		
		// Вазки ты еблан :з
		internal class `TileManaFlame$MethodVisitor`(mv: MethodVisitor): MethodVisitor(ASM5, mv) {
			
			override fun visitFieldInsn(opcode: Int, owner: String?, name: String?, desc: String?) {
				if (opcode == GETFIELD && name == "color")
					super.visitMethodInsn(INVOKEVIRTUAL, "vazkii/botania/common/block/tile/TileManaFlame", "getColor", "()I", false)
				else
					super.visitFieldInsn(opcode, owner, name, desc)
			}
		}
	}
	
	internal class `TileSpecialFlower$ClassVisitor`(cv: ClassVisitor): ClassVisitor(ASM5, cv) {

		override fun visitField(access: Int, name: String?, desc: String?, signature: String?, value: Any?): FieldVisitor {
			val newVal = if (value == "subTileName") SubTileEntity.TAG_TYPE else value
			return super.visitField(access, name, desc, signature, newVal)
		}

		override fun visitMethod(access: Int, name: String?, desc: String?, signature: String?, exceptions: Array<out String>?): MethodVisitor {
			return `TileSpecialFlower$MethodVisitor`(super.visitMethod(access, name, desc, signature, exceptions))
		}

		internal class `TileSpecialFlower$MethodVisitor`(mv: MethodVisitor): MethodVisitor(ASM5, mv) {

			override fun visitLdcInsn(cst: Any?) {
				val newCst = if (cst == "subTileName") SubTileEntity.TAG_TYPE else cst

				super.visitLdcInsn(newCst)
			}
		}
	}
	
	internal class `EntityDoppleganger$ClassVisitor`(cv: ClassVisitor): ClassVisitor(ASM5, cv) {
		
		override fun visit(version: Int, access: Int, name: String?, signature: String?, superName: String?, interfaces: Array<out String>?) {
			super.visit(version, access, name, signature, superName, arrayOf("alfheim/api/boss/IBotaniaBossWithShaderAndName"))
		}
		
		override fun visitMethod(access: Int, name: String, desc: String, signature: String?, exceptions: Array<String>?): MethodVisitor {
			
			// ################################################################################################################
			// # NO IT CAN'T BE SO EASILY DONE OTHERWISE !!! STOP DELETING THIS BEFORE YOU ACTUALLY MADE A BETTER VERSION !!! #
			// ################################################################################################################
			
			if (name == "attackEntityFrom" || name == "a" && desc == "(Lro;F)Z") {
				println("Visiting EntityDoppleganger#attackEntityFrom: $name$desc")
				val mv = cv.visitMethod(ACC_PUBLIC, if (OBF) "a" else "attackEntityFrom", if (OBF) "(Lro;F)Z" else "(Lnet/minecraft/util/DamageSource;F)Z", null, null)
				mv.visitCode()
				val l0 = Label()
				mv.visitLabel(l0)
				mv.visitLineNumber(371, l0)
				mv.visitVarInsn(ALOAD, 1)
				mv.visitMethodInsn(INVOKEVIRTUAL, if (OBF) "ro" else "net/minecraft/util/DamageSource", if (OBF) "j" else "getEntity", if (OBF) "()Lsa;" else "()Lnet/minecraft/entity/Entity;", false)
				mv.visitVarInsn(ASTORE, 3)
				val l1 = Label()
				mv.visitLabel(l1)
				mv.visitLineNumber(372, l1)
				mv.visitVarInsn(ALOAD, 1)
				mv.visitFieldInsn(GETFIELD, if (OBF) "ro" else "net/minecraft/util/DamageSource", if (OBF) "o" else "damageType", "Ljava/lang/String;")
				mv.visitLdcInsn("player")
				mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "equals", "(Ljava/lang/Object;)Z", false)
				val l2 = Label()
				mv.visitJumpInsn(IFNE, l2)
				mv.visitVarInsn(ALOAD, 1)
				mv.visitTypeInsn(INSTANCEOF, "alfheim/common/core/util/DamageSourceSpell")
				mv.visitJumpInsn(IFNE, l2)
				mv.visitVarInsn(ALOAD, 3)
				mv.visitTypeInsn(INSTANCEOF, "vazkii/botania/common/entity/EntityPixie")
				val l3 = Label()
				mv.visitJumpInsn(IFEQ, l3)
				mv.visitLabel(l2)
				mv.visitFrame(F_APPEND, 1, arrayOf<Any>(if (OBF) "sa" else "net/minecraft/entity/Entity"), 0, null)
				mv.visitVarInsn(ALOAD, 3)
				mv.visitJumpInsn(IFNULL, l3)
				mv.visitVarInsn(ALOAD, 3)
				mv.visitMethodInsn(INVOKESTATIC, "vazkii/botania/common/entity/EntityDoppleganger", "isTruePlayer", if (OBF) "(Lsa;)Z" else "(Lnet/minecraft/entity/Entity;)Z", false)
				mv.visitJumpInsn(IFEQ, l3)
				mv.visitVarInsn(ALOAD, 0)
				mv.visitMethodInsn(INVOKEVIRTUAL, "vazkii/botania/common/entity/EntityDoppleganger", "getInvulTime", "()I", false)
				mv.visitJumpInsn(IFNE, l3)
				val l4 = Label()
				mv.visitLabel(l4)
				mv.visitLineNumber(373, l4)
				mv.visitVarInsn(ALOAD, 3)
				mv.visitTypeInsn(CHECKCAST, if (OBF) "yz" else "net/minecraft/entity/player/EntityPlayer")
				mv.visitVarInsn(ASTORE, 4)
				val l5 = Label()
				mv.visitLabel(l5)
				mv.visitLineNumber(374, l5)
				mv.visitVarInsn(ALOAD, 0)
				mv.visitFieldInsn(GETFIELD, "vazkii/botania/common/entity/EntityDoppleganger", "playersWhoAttacked", "Ljava/util/List;")
				mv.visitVarInsn(ALOAD, 4)
				mv.visitMethodInsn(INVOKEVIRTUAL, if (OBF) "yz" else "net/minecraft/entity/player/EntityPlayer", if (OBF) "b_" else "getCommandSenderName", "()Ljava/lang/String;", false)
				mv.visitMethodInsn(INVOKEINTERFACE, "java/util/List", "contains", "(Ljava/lang/Object;)Z", true)
				val l6 = Label()
				mv.visitJumpInsn(IFNE, l6)
				val l7 = Label()
				mv.visitLabel(l7)
				mv.visitLineNumber(375, l7)
				mv.visitVarInsn(ALOAD, 0)
				mv.visitFieldInsn(GETFIELD, "vazkii/botania/common/entity/EntityDoppleganger", "playersWhoAttacked", "Ljava/util/List;")
				mv.visitVarInsn(ALOAD, 4)
				mv.visitMethodInsn(INVOKEVIRTUAL, if (OBF) "yz" else "net/minecraft/entity/player/EntityPlayer", if (OBF) "b_" else "getCommandSenderName", "()Ljava/lang/String;", false)
				mv.visitMethodInsn(INVOKEINTERFACE, "java/util/List", "add", "(Ljava/lang/Object;)Z", true)
				mv.visitInsn(POP)
				mv.visitLabel(l6)
				mv.visitLineNumber(377, l6)
				mv.visitFrame(F_APPEND, 1, arrayOf<Any>(if (OBF) "yz" else "net/minecraft/entity/player/EntityPlayer"), 0, null)
				mv.visitVarInsn(FLOAD, 2)
				mv.visitVarInsn(FSTORE, 5)
				val l8 = Label()
				mv.visitLabel(l8)
				mv.visitLineNumber(378, l8)
				mv.visitInsn(ICONST_0)
				mv.visitVarInsn(ISTORE, 6)
				val l9 = Label()
				mv.visitLabel(l9)
				mv.visitLineNumber(379, l9)
				mv.visitVarInsn(ALOAD, 3)
				mv.visitTypeInsn(INSTANCEOF, if (OBF) "yz" else "net/minecraft/entity/player/EntityPlayer")
				val l10 = Label()
				mv.visitJumpInsn(IFEQ, l10)
				val l11 = Label()
				mv.visitLabel(l11)
				mv.visitLineNumber(380, l11)
				mv.visitVarInsn(ALOAD, 3)
				mv.visitTypeInsn(CHECKCAST, if (OBF) "yz" else "net/minecraft/entity/player/EntityPlayer")
				mv.visitVarInsn(ASTORE, 7)
				val l12 = Label()
				mv.visitLabel(l12)
				mv.visitLineNumber(381, l12)
				mv.visitVarInsn(ALOAD, 7)
				mv.visitFieldInsn(GETFIELD, if (OBF) "yz" else "net/minecraft/entity/player/EntityPlayer", if (OBF) "R" else "fallDistance", "F")
				mv.visitInsn(FCONST_0)
				mv.visitInsn(FCMPL)
				val l13 = Label()
				mv.visitJumpInsn(IFLE, l13)
				mv.visitVarInsn(ALOAD, 7)
				mv.visitFieldInsn(GETFIELD, if (OBF) "yz" else "net/minecraft/entity/player/EntityPlayer", if (OBF) "D" else "onGround", "Z")
				mv.visitJumpInsn(IFNE, l13)
				mv.visitVarInsn(ALOAD, 7)
				mv.visitMethodInsn(INVOKEVIRTUAL, if (OBF) "yz" else "net/minecraft/entity/player/EntityPlayer", if (OBF) "h_" else "isOnLadder", "()Z", false)
				mv.visitJumpInsn(IFNE, l13)
				mv.visitVarInsn(ALOAD, 7)
				mv.visitMethodInsn(INVOKEVIRTUAL, if (OBF) "yz" else "net/minecraft/entity/player/EntityPlayer", if (OBF) "M" else "isInWater", "()Z", false)
				mv.visitJumpInsn(IFNE, l13)
				mv.visitVarInsn(ALOAD, 7)
				mv.visitFieldInsn(GETSTATIC, if (OBF) "rv" else "net/minecraft/potion/Potion", if (OBF) "q" else "blindness", if (OBF) "Lrv;" else "Lnet/minecraft/potion/Potion;")
				mv.visitMethodInsn(INVOKEVIRTUAL, if (OBF) "yz" else "net/minecraft/entity/player/EntityPlayer", if (OBF) "a" else "isPotionActive", if (OBF) "(Lrv;)Z" else "(Lnet/minecraft/potion/Potion;)Z", false)
				mv.visitJumpInsn(IFNE, l13)
				mv.visitVarInsn(ALOAD, 7)
				mv.visitFieldInsn(GETFIELD, if (OBF) "yz" else "net/minecraft/entity/player/EntityPlayer", if (OBF) "m" else "ridingEntity", if (OBF) "Lsa;" else "Lnet/minecraft/entity/Entity;")
				mv.visitJumpInsn(IFNONNULL, l13)
				mv.visitInsn(ICONST_1)
				val l14 = Label()
				mv.visitJumpInsn(GOTO, l14)
				mv.visitLabel(l13)
				mv.visitFrame(F_APPEND, 3, arrayOf<Any>(FLOAT, INTEGER, if (OBF) "yz" else "net/minecraft/entity/player/EntityPlayer"), 0, null)
				mv.visitInsn(ICONST_0)
				mv.visitLabel(l14)
				mv.visitFrame(F_SAME1, 0, null, 1, arrayOf<Any>(INTEGER))
				mv.visitVarInsn(ISTORE, 6)
				mv.visitLabel(l10)
				mv.visitLineNumber(384, l10)
				mv.visitFrame(F_CHOP, 1, null, 0, null)
				mv.visitVarInsn(ILOAD, 6)
				val l15 = Label()
				mv.visitJumpInsn(IFEQ, l15)
				mv.visitIntInsn(BIPUSH, 60)
				val l16 = Label()
				mv.visitJumpInsn(GOTO, l16)
				mv.visitLabel(l15)
				mv.visitFrame(F_SAME, 0, null, 0, null)
				mv.visitIntInsn(BIPUSH, 40)
				mv.visitLabel(l16)
				mv.visitFrame(F_SAME1, 0, null, 1, arrayOf<Any>(INTEGER))
				mv.visitVarInsn(ISTORE, 7)
				val l17 = Label()
				mv.visitLabel(l17)
				mv.visitLineNumber(385, l17)
				mv.visitVarInsn(ALOAD, 0)
				mv.visitVarInsn(ALOAD, 1)
				mv.visitVarInsn(ILOAD, 7)
				mv.visitInsn(I2F)
				mv.visitVarInsn(FLOAD, 5)
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Math", "min", "(FF)F", false)
				mv.visitVarInsn(ALOAD, 0)
				mv.visitMethodInsn(INVOKEVIRTUAL, "vazkii/botania/common/entity/EntityDoppleganger", "isHardMode", "()Z", false)
				val l18 = Label()
				mv.visitJumpInsn(IFEQ, l18)
				mv.visitLdcInsn(0.6f)
				val l19 = Label()
				mv.visitJumpInsn(GOTO, l19)
				mv.visitLabel(l18)
				mv.visitFrame(F_FULL, 8, arrayOf<Any>("vazkii/botania/common/entity/EntityDoppleganger", if (OBF) "ro" else "net/minecraft/util/DamageSource", FLOAT, if (OBF) "sa" else "net/minecraft/entity/Entity", if (OBF) "yz" else "net/minecraft/entity/player/EntityPlayer", FLOAT, INTEGER, INTEGER), 3, arrayOf<Any>("vazkii/botania/common/entity/EntityDoppleganger", if (OBF) "ro" else "net/minecraft/util/DamageSource", FLOAT))
				mv.visitInsn(FCONST_1)
				mv.visitLabel(l19)
				mv.visitFrame(F_FULL, 8, arrayOf<Any>("vazkii/botania/common/entity/EntityDoppleganger", if (OBF) "ro" else "net/minecraft/util/DamageSource", FLOAT, if (OBF) "sa" else "net/minecraft/entity/Entity", if (OBF) "yz" else "net/minecraft/entity/player/EntityPlayer", FLOAT, INTEGER, INTEGER), 4, arrayOf<Any>("vazkii/botania/common/entity/EntityDoppleganger", if (OBF) "ro" else "net/minecraft/util/DamageSource", FLOAT, FLOAT))
				mv.visitInsn(FMUL)
				mv.visitMethodInsn(INVOKESPECIAL, if (OBF) "td" else "net/minecraft/entity/EntityCreature", if (OBF) "a" else "attackEntityFrom", if (OBF) "(Lro;F)Z" else "(Lnet/minecraft/util/DamageSource;F)Z", false)
				mv.visitInsn(IRETURN)
				mv.visitLabel(l3)
				mv.visitLineNumber(387, l3)
				mv.visitFrame(F_FULL, 4, arrayOf<Any>("vazkii/botania/common/entity/EntityDoppleganger", if (OBF) "ro" else "net/minecraft/util/DamageSource", FLOAT, if (OBF) "sa" else "net/minecraft/entity/Entity"), 0, arrayOf())
				mv.visitInsn(ICONST_0)
				mv.visitInsn(IRETURN)
				val l20 = Label()
				mv.visitLabel(l20)
				mv.visitLocalVariable("this", "Lvazkii/botania/common/entity/EntityDoppleganger;", null, l0, l20, 0)
				mv.visitLocalVariable("par1DamageSource", if (OBF) "Lro;" else "Lnet/minecraft/util/DamageSource;", null, l0, l20, 1)
				mv.visitLocalVariable("par2", "F", null, l0, l20, 2)
				mv.visitLocalVariable("e", if (OBF) "Lsa;" else "Lnet/minecraft/entity/Entity;", null, l1, l20, 3)
				mv.visitLocalVariable("player", if (OBF) "yz" else "Lnet/minecraft/entity/player/EntityPlayer;", null, l5, l3, 4)
				mv.visitLocalVariable("dmg", "F", null, l8, l3, 5)
				mv.visitLocalVariable("crit", "Z", null, l9, l3, 6)
				mv.visitLocalVariable("p", if (OBF) "yz" else "Lnet/minecraft/entity/player/EntityPlayer;", null, l12, l10, 7)
				mv.visitLocalVariable("cap", "I", null, l17, l3, 7)
				mv.visitMaxs(4, 8)
				mv.visitEnd()
				return mv
			} else if (name == "getBossBarTextureRect") {
				println("Visiting EntityDoppleganger#getBossBarTextureRect: $name$desc")
				return `EntityDoppleganger$getBossBarTextureRect$MethodVisitor`(super.visitMethod(access, name, desc, signature, exceptions))
			}
			return super.visitMethod(access, name, desc, signature, exceptions)
		}
		
		internal class `EntityDoppleganger$getBossBarTextureRect$MethodVisitor`(mv: MethodVisitor): MethodVisitor(ASM5, mv) {
			
			var inject = 2
			
			override fun visitInsn(opcode: Int) {
				if (opcode == ICONST_0 && --inject == 0)
					super.visitIntInsn(BIPUSH, AlfheimConfigHandler.gaiaBarOffset * 22)
				else
					super.visitInsn(opcode)
			}
		}
	}
	
	internal class `ItemFlowerBag$ClassVisitor`(cv: ClassVisitor): ClassVisitor(ASM5, cv) {
		
		override fun visitMethod(access: Int, name: String, desc: String, signature: String?, exceptions: Array<String>?): MethodVisitor {
			if (name == "loadStacks") {
				println("Visiting ItemFlowerBag#loadStacks: $name$desc")
				return `ItemFlowerBag$loadStacks$MethodVisitor`(super.visitMethod(access, name, desc, signature, exceptions))
			}
			return super.visitMethod(access, name, desc, signature, exceptions)
		}
		
		internal class `ItemFlowerBag$loadStacks$MethodVisitor`(mv: MethodVisitor): MethodVisitor(ASM5, mv) {
			
			override fun visitIntInsn(opcode: Int, operand: Int) {
				val oper = if (opcode == BIPUSH && operand == 16) 34 else operand
				super.visitIntInsn(opcode, oper)
			}
		}
	}
	
	internal class `ItemInfiniEffect$ClassVisitor`(val className: String, cv: ClassVisitor): ClassVisitor(ASM5, cv) {
		
		override fun visitMethod(access: Int, name: String, desc: String, signature: String?, exceptions: Array<String>?): MethodVisitor {
			if (name == "onWornTick") {
				println("Visiting $className#onWornTick: $name$desc")
				return `ItemInfiniEffect$onWornTick$MethodVisitor`(super.visitMethod(access, name, desc, signature, exceptions))
			}
			return super.visitMethod(access, name, desc, signature, exceptions)
		}
		
		internal class `ItemInfiniEffect$onWornTick$MethodVisitor`(mv: MethodVisitor): MethodVisitor(ASM5, mv) {
			
			override fun visitLdcInsn(cst: Any?) {
				if (cst == Integer.MAX_VALUE)
					super.visitLdcInsn(20)
				else
					super.visitLdcInsn(cst)
			}
		}
	}
	
	internal class `ItemLens$ClassVisitor`(cv: ClassVisitor): ClassVisitor(ASM5, cv) {
		
		override fun visitField(access: Int, name: String, desc: String, signature: String?, value: Any?): FieldVisitor {
			var value = value
			if (name == "SUBTYPES") {
				value = 22 + moreLenses
			}
			return super.visitField(access, name, desc, signature, value)
		}
		
		override fun visitMethod(access: Int, name: String, desc: String, signature: String?, exceptions: Array<String>?): MethodVisitor {
			val mv = super.visitMethod(access, name, desc, signature, exceptions)
			
			println("Visiting ItemLens#$name: $name$desc")
			return `ItemLens$MethodVisitor`(mv)
		}
		
		internal class `ItemLens$MethodVisitor`(mv: MethodVisitor): MethodVisitor(ASM5, mv) {
			
			companion object {
				
				var left = 2
			}
			
			override fun visitIntInsn(opcode: Int, operand: Int) {
				var operand = operand
				if (opcode == BIPUSH) {
					if (operand == 22) {        // 4 injections for #SUBTYPES
						operand += moreLenses
					} else if (operand == 21) { // 2 injections for #SUBTYPES-1
						if (left-- > 0) {       // 4 injections total
							operand += moreLenses
						}
					}
				}
				
				super.visitIntInsn(opcode, operand)
			}
		}
	}
	
	internal class `ItemAesirRing$ClassVisitor`(cv: ClassVisitor): ClassVisitor(ASM5, cv) {
		
		override fun visitMethod(access: Int, name: String, desc: String, signature: String?, exceptions: Array<String>?): MethodVisitor {
			if (name == "onDropped") {
				println("Visiting ItemAesirRing#onDropped: $name$desc")
				return `ItemAesirRing$onDropped$MethodVisitor`(super.visitMethod(access, name, desc, signature, exceptions))
			}
			return super.visitMethod(access, name, desc, signature, exceptions)
		}
		
		internal class `ItemAesirRing$onDropped$MethodVisitor`(mv: MethodVisitor): MethodVisitor(ASM5, mv) {
			
			override fun visitInsn(opcode: Int) {
				if (opcode == ICONST_3)
					super.visitIntInsn(BIPUSH, 6)
				else
					super.visitInsn(opcode)
			}
			
			override fun visitVarInsn(opcode: Int, `var`: Int) {
				if (opcode == ASTORE && `var` == 4) {
					mv.visitInsn(DUP)
					mv.visitInsn(ICONST_3)
					mv.visitFieldInsn(GETSTATIC, "alfheim/common/item/AlfheimItems", "INSTANCE", "Lalfheim/common/item/AlfheimItems;")
					mv.visitMethodInsn(INVOKEVIRTUAL, "alfheim/common/item/AlfheimItems", "getPriestRingSif", if (OBF) "()Ladb;" else "()Lnet/minecraft/item/Item;", false)
					mv.visitInsn(AASTORE)
					mv.visitInsn(DUP)
					mv.visitInsn(ICONST_4)
					mv.visitFieldInsn(GETSTATIC, "alfheim/common/item/AlfheimItems", "INSTANCE", "Lalfheim/common/item/AlfheimItems;")
					mv.visitMethodInsn(INVOKEVIRTUAL, "alfheim/common/item/AlfheimItems", "getPriestRingNjord", if (OBF) "()Ladb;" else "()Lnet/minecraft/item/Item;", false)
					mv.visitInsn(AASTORE)
					mv.visitInsn(DUP)
					mv.visitInsn(ICONST_5)
					mv.visitFieldInsn(GETSTATIC, "alfheim/common/item/AlfheimItems", "INSTANCE", "Lalfheim/common/item/AlfheimItems;")
					mv.visitMethodInsn(INVOKEVIRTUAL, "alfheim/common/item/AlfheimItems", "getPriestRingHeimdall", if (OBF) "()Ladb;" else "()Lnet/minecraft/item/Item;", false)
					mv.visitInsn(AASTORE)
				}
				
				super.visitVarInsn(opcode, `var`)
			}
		}
	}
	
	internal class `ItemTerraformRod$ClassVisitor`(cv: ClassVisitor): ClassVisitor(ASM5, cv) {
		
		override fun visitMethod(access: Int, name: String, desc: String, signature: String?, exceptions: Array<String>?): MethodVisitor {
			if (name == "terraform") {
				println("Visiting ItemTerraformRod#terraform: $name$desc")
				return `ItemTerraformRod$terraform$MethodVisitor`(super.visitMethod(access, name, desc, signature, exceptions))
			}
			
			return super.visitMethod(access, name, desc, signature, exceptions)
		}
		
		internal class `ItemTerraformRod$terraform$MethodVisitor`(mv: MethodVisitor): MethodVisitor(ASM5, mv) {
			
			var put = true
			
			override fun visitVarInsn(opcode: Int, `var`: Int) {
				super.visitVarInsn(opcode, `var`)
				
				if (opcode == ISTORE && `var` == 4 && put) {
					put = false
					
					mv.visitFieldInsn(GETSTATIC, "alfheim/common/core/asm/hook/fixes/GodAttributesHooks", "INSTANCE", "Lalfheim/common/core/asm/hook/fixes/GodAttributesHooks;")
					mv.visitVarInsn(ALOAD, 3)
					mv.visitVarInsn(ILOAD, 4)
					mv.visitMethodInsn(INVOKEVIRTUAL, "alfheim/common/core/asm/hook/fixes/GodAttributesHooks", "getRange", "(Lnet/minecraft/entity/player/EntityPlayer;I)I", false)
					mv.visitVarInsn(ISTORE, 4)
				}
			}
		}
	}
	
	internal class `LibItemNames$ClassVisitor`(cv: ClassVisitor): ClassVisitor(ASM5, cv) {
		
		override fun visitMethod(access: Int, name: String, desc: String, signature: String?, exceptions: Array<String>?): MethodVisitor {
			if (name == "<clinit>") {
				println("Visiting LibItemNames#<clinit>: $name$desc")
				return `LibItemNames$clinit$MethodVisitor`(super.visitMethod(access, name, desc, signature, exceptions))
			}
			return super.visitMethod(access, name, desc, signature, exceptions)
		}
		
		internal class `LibItemNames$clinit$MethodVisitor`(mv: MethodVisitor): MethodVisitor(ASM5, mv) {
			
			private var twotwo_twofour = true
			private var add = false
			private var twoone = true
			
			override fun visitIntInsn(opcode: Int, operand: Int) {
				var operand = operand
				if (opcode == BIPUSH) {
					if (operand == 22) {
						if (twotwo_twofour) {
							twotwo_twofour = false
							operand += moreLenses
						}
					}
					
					if (operand == 21) {
						if (twoone) {
							twoone = false
							add = true
						}
					}
				}
				
				super.visitIntInsn(opcode, operand)
			}
			
			override fun visitInsn(opcode: Int) {
				super.visitInsn(opcode)
				
				if (opcode == AASTORE) {
					if (add) {
						add = false
						mv.visitInsn(DUP)
						mv.visitIntInsn(BIPUSH, 22)
						mv.visitLdcInsn("lensMessenger")
						mv.visitInsn(AASTORE)
						mv.visitInsn(DUP)
						mv.visitIntInsn(BIPUSH, 23)
						mv.visitLdcInsn("lensTripwire")
						mv.visitInsn(AASTORE)
						mv.visitInsn(DUP)
						mv.visitIntInsn(BIPUSH, 24)
						mv.visitLdcInsn("lensPush")
						mv.visitInsn(AASTORE)
						mv.visitInsn(DUP)
						mv.visitIntInsn(BIPUSH, 25)
						mv.visitLdcInsn("lensSmelt")
						mv.visitInsn(AASTORE)
						mv.visitInsn(DUP)
						mv.visitIntInsn(BIPUSH, 26)
						mv.visitLdcInsn("lensSuperconductor")
						mv.visitInsn(AASTORE)
						mv.visitInsn(DUP)
						mv.visitIntInsn(BIPUSH, 27)
						mv.visitLdcInsn("lensTrack")
						mv.visitInsn(AASTORE)
						mv.visitInsn(DUP)
						mv.visitIntInsn(BIPUSH, 28)
						mv.visitLdcInsn("lensDaisy")
						mv.visitInsn(AASTORE)
					}
				}
			}
		}
	}
	
	internal class `TFFluids$ClassVisitor`(cv: ClassVisitor): ClassVisitor(ASM5, cv) {
		
		override fun visitMethod(access: Int, name: String, desc: String, signature: String?, exceptions: Array<String>?): MethodVisitor {
			if (name == "registerFluid") {
				println("Visiting ThermalFoundation's TFFluids#registerFluid: $name$desc")
				return `TFFluids$registerFluid$MethodVisitor`(super.visitMethod(access, name, desc, signature, exceptions))
			}
			return super.visitMethod(access, name, desc, signature, exceptions)
		}
		
		internal class `TFFluids$registerFluid$MethodVisitor`(mv: MethodVisitor): MethodVisitor(ASM5, mv) {
			
			override fun visitVarInsn(opcode: Int, i: Int) {
				if (opcode == ASTORE && i == 0) {
					super.visitInsn(POP)
					
					super.visitVarInsn(ALOAD, 1)
					super.visitMethodInsn(INVOKESTATIC, "alfheim/common/integration/ThermalFoundationIntegration\$Hooks", "postRegisterFluid", "(Ljava/lang/String;)V", false)
					return
				}
				
				super.visitVarInsn(opcode, i)
			}
		}
	}
	
	internal class `ClientEvents$GUIOverlay$ClassVisitor`(cv: ClassVisitor): ClassVisitor(ASM5, cv) {
		
		override fun visitMethod(access: Int, name: String, desc: String, signature: String?, exceptions: Array<String>?): MethodVisitor {
			if (name == "renderHotbar") {
				println("Visiting witchery's ClientEvents\$GUIOverlay#renderHotbar: $name$desc")
				return `ClientEvents$GUIOverlay$renderHotbar$MethodVisitor`(super.visitMethod(access, name, desc, signature, exceptions))
			}
			return super.visitMethod(access, name, desc, signature, exceptions)
		}
		
		internal class `ClientEvents$GUIOverlay$renderHotbar$MethodVisitor`(mv: MethodVisitor): MethodVisitor(ASM5, mv) {
			
			var aload1 = false
			
			override fun visitVarInsn(opcode: Int, operand: Int) {
				if (opcode == ALOAD) {
					aload1 = operand == 1
				}
				
				super.visitVarInsn(opcode, operand)
			}
			
			override fun visitInsn(opcode: Int) {
				super.visitInsn(if (opcode == ICONST_1) ICONST_0 else opcode)
			}
		}
	}
	
	companion object {
		
		val moreLenses = 7
	}
}