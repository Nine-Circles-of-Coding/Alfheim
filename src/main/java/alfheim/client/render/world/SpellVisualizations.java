package alfheim.client.render.world;

import static org.lwjgl.opengl.GL11.GL_BACK;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_FRONT;
import static org.lwjgl.opengl.GL11.GL_LEQUAL;
import static org.lwjgl.opengl.GL11.GL_LIGHTING;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_DST_COLOR;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_COLOR;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glColor4d;
import static org.lwjgl.opengl.GL11.glColorMask;
import static org.lwjgl.opengl.GL11.glCullFace;
import static org.lwjgl.opengl.GL11.glDepthFunc;
import static org.lwjgl.opengl.GL11.glDepthMask;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glScaled;
import static org.lwjgl.opengl.GL11.glTranslated;

import org.lwjgl.opengl.GL12;

import alexsocol.asjlib.ASJUtilities;
import alexsocol.asjlib.render.RenderPostShaders;
import alexsocol.asjlib.render.ShadedObject;
import alfheim.api.lib.LibShaderIDs;
import alfheim.client.core.handler.CardinalSystemClient.TimeStopSystemClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.EntityLivingBase;

public class SpellVisualizations {

	public static ShadedObject so;
	
	static {
		so = new ShadedObject(LibShaderIDs.idNoise, RenderPostShaders.getNextAvailableRenderObjectMaterialID(), null) {
			
			@Override
			public void preRender() {
				glEnable(GL12.GL_RESCALE_NORMAL); 

				glDisable(GL_LIGHTING); 
				glDisable(GL_TEXTURE_2D); 
				glColor4d(0, 0, 0, 1); 
				
				glEnable(GL_CULL_FACE);
				glCullFace(GL_BACK);
			}
			
			@Override
			public void drawMesh() {
				renderSphere(Tessellator.instance, 240.0, (float) 240.0/3.6F, 0.0D, 1.0F, 0); 
			}
			
			@Override
			public void postRender() {
				glColor4d(1, 1, 1, 1); 
				glEnable(GL_TEXTURE_2D); 
				glEnable(GL_LIGHTING); 

				glDisable(GL12.GL_RESCALE_NORMAL); 
			}
		};
		
		RenderPostShaders.registerShadedObject(so);
	}
	
	public static void redSphere(double x, double y, double z) { 
		glPushMatrix(); 
		ASJUtilities.interpolatedTranslationReverse(Minecraft.getMinecraft().thePlayer); 
		boolean inside = TimeStopSystemClient.inside(Minecraft.getMinecraft().thePlayer);
		glTranslated(x, y, z); 
		glEnable(GL12.GL_RESCALE_NORMAL); 

		glDisable(GL_LIGHTING); 
		glDisable(GL_TEXTURE_2D); 
		glColor4d(0, 0, 0, 1); 
		double size = 240.0; 

		glScaled(0.5, 0.5, 0.5); 
		so.addTranslation();
		glColorMask(false, true, true, false); 
		glCullFace(GL_FRONT);
		if (inside)  glDisable(GL_DEPTH_TEST); 
		renderSphere(Tessellator.instance, size, (float)size/3.6F, 0.0D, 1.0F, 0); 
		if (inside)  glEnable(GL_DEPTH_TEST); 
		glColorMask(true, true, true, true); 
		glCullFace(GL_BACK);
		
		glColor4d(1, 1, 1, 1); 
		glEnable(GL_TEXTURE_2D); 
		glEnable(GL_LIGHTING); 

		glDisable(GL12.GL_RESCALE_NORMAL); 
		glPopMatrix(); 
	}
	
	public static void negateSphere(EntityLivingBase caster, double s) {
		glPushMatrix();
		ASJUtilities.interpolatedTranslation(Minecraft.getMinecraft().thePlayer);
		Tessellator tes = Tessellator.instance;
		
		glEnable(GL12.GL_RESCALE_NORMAL);
		glDisable(GL_CULL_FACE);
		glDepthMask(false);
		glDepthFunc(GL_LEQUAL);
		glEnable(GL_BLEND);
		glBlendFunc(GL_ONE_MINUS_DST_COLOR, GL_ONE_MINUS_SRC_COLOR);
		glDisable(GL_TEXTURE_2D);
		glDisable(GL_LIGHTING);
		glColor4d(1, 1, 1, 1);
		double size = 240.0;
		
		glPushMatrix();
		glLoadIdentity();
		double z = -0.1;
		tes.startDrawingQuads();
		tes.addVertex(-10, -10, z);
		tes.addVertex(-10,  10, z);
		tes.addVertex( 10,  10, z);
		tes.addVertex( 10, -10, z);
		tes.draw();
		glPopMatrix();
		
		glScaled(s, s, s);
		renderSphere(tes, size, (float)size/3.6F, 0.0D, 1.0F, 0);
		glScaled(1/s, 1/s, 1/s);

		glEnable(GL_LIGHTING);
		glEnable(GL_TEXTURE_2D);
		glDisable(GL_BLEND);
		glDepthMask(true);
		glEnable(GL_CULL_FACE);
		glDisable(GL12.GL_RESCALE_NORMAL);
		
		glPopMatrix();
	}
	
	/**
	 * @author thKaguya
	 */
	protected static void renderSphere(Tessellator tessellator, double length, float width, double zPos, float alpha, int time) {
		float maxWidth = (float)width / 2.0F;
		int zAngleDivNum = 18;
		float zSpan = 360F / zAngleDivNum;
		double angleZ = 0F;
		double angleSpanZ = Math.PI * 2.0D / (double)zAngleDivNum;
		int zDivNum = 9;
		double zLength = width;
		double zDivLength = zLength / (double)(zDivNum - 1);
		double zLength2 = zLength / 2.0D;
		zPos = Math.sin(-Math.PI / 2.0D) * maxWidth;
		double zPosOld = zPos;
		float xPos = 0F;
		float yPos = 0F;
		float xPos2 = 0F;
		float yPos2 = 0F;
		float xPosOld = xPos;
		float yPosOld = yPos;
		float xPos2Old = xPos2;
		float yPos2Old = yPos2;
		float angle = -(float)Math.PI / 2.0F;
		float angleSpan = (float)Math.PI / (float)(zDivNum);
		angle += angleSpan;
		float widthOld = 0.0F;
		for(int j = 0; j < zDivNum; j++) {
			zPos = Math.sin(angle) * maxWidth;
			width = (float)Math.cos(angle) * maxWidth;
			xPos = width;
			yPos = 0F;
			angleZ = 0F;
			xPosOld = (float)Math.cos(angleZ) * width;
			yPosOld = (float)Math.sin(angleZ) * width;
			xPos2Old = (float)Math.cos(angleZ) * widthOld;
			yPos2Old = (float)Math.sin(angleZ) * widthOld;
			angleZ = angleSpanZ;
			for(int i = 1; i <= zAngleDivNum; i++) {
				xPos = (float)Math.cos(angleZ) * width;
				yPos = (float)Math.sin(angleZ) * width;
				xPos2 = (float)Math.cos(angleZ) * widthOld;
				yPos2 = (float)Math.sin(angleZ) * widthOld;
				double colorVar = 0.0D;
				if(time != 0) colorVar = (time + j) / 10.0D;
				tessellator.startDrawingQuads();
				//tessellator.setColorRGBA_F(1.0F, 1.0F, 1.0F , alpha);
				tessellator.setNormal(0.0F, 1.0F, 0.0F);
				tessellator.addVertexWithUV(  xPos	, yPos	, zPos   , 1.0F, 0.0F);
				tessellator.addVertexWithUV(  xPosOld , yPosOld , zPos   , 0.0F, 0.0F);
				tessellator.addVertexWithUV(  xPos2Old, yPos2Old, zPosOld, 0.0F, 1.0F);
				tessellator.addVertexWithUV(  xPos2   , yPos2   , zPosOld, 1.0F, 1.0F);
				tessellator.draw();
				xPosOld = xPos;
				yPosOld = yPos;
				xPos2Old = xPos2;
				yPos2Old = yPos2;
				angleZ += angleSpanZ;
			}
			zPosOld = zPos;
			angle += angleSpan;
			widthOld = width;
		}
	}

}
