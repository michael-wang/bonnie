package com.studioirregular.gltexturetest;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

public class GlTextureTestActivity extends Activity {
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        GLSurfaceView view = (GLSurfaceView)findViewById(R.id.surfaceView);
        view.setRenderer(new MyRenderer(this));
    }
}