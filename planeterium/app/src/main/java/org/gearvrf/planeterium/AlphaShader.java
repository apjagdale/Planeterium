package org.gearvrf.planeterium;

/**
 * Created by a.jagdale on 5/6/2017.
 */

import android.content.Context;

import org.gearvrf.GVRContext;
import org.gearvrf.GVRShaderTemplate;
import org.gearvrf.utility.TextFile;

public class AlphaShader extends GVRShaderTemplate{

    public static final String STATE1_TEXTURE = "state1";
    public static final String TEXTURE_SWITCH = "textureSwitch";

    public AlphaShader(GVRContext gvrContext) {
        super("float textureSwitch, sampler2D state1");

        Context context = gvrContext.getContext();
        setSegment("FragmentTemplate", TextFile.readTextFile(context, R.raw.alpha_shader_fragment));
        setSegment("VertexTemplate", TextFile.readTextFile(context,R.raw.alpha_image_shader_vertex));

    }
}