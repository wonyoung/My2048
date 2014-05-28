package com.jooyunghan.my2048.opengl;

import java.nio.Buffer;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by wonyoung.jang on 2014-05-28.
 */
public class Cube extends Shape {

    private FloatBuffer vertexBuffer;
    private FloatBuffer texBuffer;
    private final int textureId;
    private final float[] vertices = {
            -1.0f, -1.0f, 0.0f,  // 0. left-bottom-front
            1.0f, -1.0f, 0.0f,  // 1. right-bottom-front
            -1.0f,  1.0f, 0.0f,  // 2. left-top-front
            1.0f,  1.0f, 0.0f   // 3. right-top-front
    };
    private final float[] texCoords = {
            0.0f, 1.0f,  // A. left-bottom (NEW)
            1.0f, 1.0f,  // B. right-bottom (NEW)
            0.0f, 0.0f,  // C. left-top (NEW)
            1.0f, 0.0f   // D. right-top (NEW)
    };
    private int x = 0;
    private int y = 0;
    private boolean isHidden = false;

    public Cube(int textureId) {
        this.textureId = textureId;
        vertexBuffer = toBuffer(vertices);
        texBuffer = toBuffer(texCoords);
    }

    @Override
    public void draw(GL10 gl) {
        if (isHidden) {
            return;
        }
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glPushMatrix();
        gl.glLoadIdentity();

        gl.glFrontFace(GL10.GL_CCW);
        gl.glEnable(GL10.GL_CULL_FACE);
        gl.glCullFace(GL10.GL_BACK);

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, texBuffer);
        moveTo(gl, x, y);
        rotateYAndDraw(gl, 0.0f);
        rotateYAndDraw(gl, 270.0f);
        rotateYAndDraw(gl, 180.0f);
        rotateYAndDraw(gl, 90.0f);
        rotateXAndDraw(gl, 270.0f);
        rotateXAndDraw(gl, 90.0f);


        gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glDisable(GL10.GL_CULL_FACE);
        gl.glPopMatrix();
    }

    private void moveTo(GL10 gl, int x, int y) {
        gl.glTranslatef(x*0.5f-1.0f, 1.0f-y*0.5f, 1.0f);
    }

    private void rotateXAndDraw(GL10 gl, float angle) {
        gl.glPushMatrix();
        gl.glScalef(0.25f, 0.25f, 0.25f);
        gl.glRotatef(angle, 1.0f, 0.0f, 0.0f);
        gl.glTranslatef(0.0f, 0.0f, 1.0f);
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textureId);
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
        gl.glPopMatrix();
    }
    private void rotateYAndDraw(GL10 gl, float angle) {
        gl.glPushMatrix();
        gl.glScalef(0.25f, 0.25f, 0.25f);
        gl.glRotatef(angle, 0.0f, 1.0f, 0.0f);
        gl.glTranslatef(0.0f, 0.0f, 1.0f);
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textureId);
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
        gl.glPopMatrix();
    }

    public void hide(int x, int y) {
        isHidden = true;
    }

    public void move(int fromX, int fromY, int toX, int toY) {
        setPosition(toX, toY);
    }

    private void setPosition(int x, int y) {
        this.isHidden = false;
        this.x = x;
        this.y = y;
    }

    public void show(int x, int y) {
        setPosition(x, y);
    }
}
