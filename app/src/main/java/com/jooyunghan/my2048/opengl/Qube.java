package com.jooyunghan.my2048.opengl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

/**
* Created by wonyoung.jang on 2014-05-19.
*/
public class Qube extends Shape {
    float color[] = {
            0,1,1,1,
            1,1,1,1,
            1,1,0,1,
            0,1,0,1,
            0,0,1,1,
            1,0,1,1,
            1,0,0,1,
            0,0,0,1,
    };

    byte index[] = {
            2, 0, 3, 3, 0, 1,
            3, 1, 5, 3, 5, 7,
            2, 3, 6, 2, 7, 6,

            6, 7, 4, 4, 7, 5,
            2, 6, 4, 2, 4, 0,
            0, 4, 1, 1, 4, 5
    };

    float tex[] = {
            0.0f ,1.0f,
            1.0f,1.0f,
            0.0f ,0.0f,
            1.0f,0.0f,

            0.0f ,1.0f,
            1.0f,1.0f,
            0.0f ,0.0f,
            1.0f,0.0f,

            0.0f ,1.0f,
            1.0f,1.0f,
            0.0f ,0.0f,
            1.0f,0.0f,

            0.0f ,1.0f,
            1.0f,1.0f,
            0.0f ,0.0f,
            1.0f,0.0f,

            0.0f ,1.0f,
            1.0f,1.0f,
            0.0f ,0.0f,
            1.0f,0.0f,

            0.0f ,1.0f,
            1.0f,1.0f,
            0.0f ,0.0f,
            1.0f,0.0f,

    };
    int one = 100000;
    float colors[] = {
            one,    one,    one,  one,
            one,    one,    one,  one,
            one,  one,    one,  one,
            one,  one,    one,  one,

            one,    one,    one,  one,
            one,    one,    one,  one,
            one,  one,    one,  one,
            one,  one,    one,  one,

            one,    one,    one,  one,
            one,    one,    one,  one,
            one,  one,    one,  one,
            one,  one,    one,  one,

            one,    one,    one,  one,
            one,    one,    one,  one,
            one,  one,    one,  one,
            one,  one,    one,  one,

            one,    one,    one,  one,
            one,    one,    one,  one,
            one,  one,    one,  one,
            one,  one,    one,  one,

            one,    one,    one,  one,
            one,    one,    one,  one,
            one,  one,    one,  one,
            one,  one,    one,  one,
    };

    private ByteBuffer indexbuf;
    private FloatBuffer colorbuf;
    private FloatBuffer vertbuf;
    private int texture;
    private FloatBuffer textbuf;

    public Qube(int texture) {
        this.texture = texture;
        indexbuf = toBuffer(index);
        colorbuf = toBuffer(colors);
        textbuf = toBuffer(tex);
    }

    @Override
    public void draw(GL10 gl) {
        gl.glFrontFace(GL10.GL_CW);
        gl.glEnable(GL10.GL_TEXTURE_2D);
        gl.glColor4f(1f, 1f, 1f, 1f);
        gl.glTexEnvx(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE,
                GL10.GL_MODULATE);

        gl.glBindTexture(GL10.GL_TEXTURE_2D, texture);

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glActiveTexture(GL10.GL_TEXTURE0);

        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textbuf);
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertbuf);
        gl.glColorPointer(4, GL10.GL_FLOAT, 0, colorbuf);
        gl.glDrawElements(GL10.GL_TRIANGLES, index.length, GL10.GL_UNSIGNED_BYTE, indexbuf);
    }

    public void setPosition(int x, int y) {
        vertbuf = toBuffer(x, y);
    }

    private FloatBuffer toBuffer(int x, int y) {
        ByteBuffer tempBuffer = ByteBuffer.allocateDirect(8*3 * 4);
        tempBuffer.order(ByteOrder.nativeOrder());
        FloatBuffer buffer = tempBuffer.asFloatBuffer();
        for (float _z : new float[] { -0.5f, 0.0f }) {
            for (float _y : new float[]{0.5f, 1.0f}) {
                for (float _x : new float[]{-1.0f, -0.5f}) {
                    buffer.put(_x+0.5f*x);
                    buffer.put(_y-0.5f*y);
                    buffer.put(_z);
                }
            }
        }
        buffer.position(0);
        return buffer;
    }
}
