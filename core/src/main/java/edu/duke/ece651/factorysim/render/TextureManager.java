package edu.duke.ece651.factorysim.render;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import java.util.HashMap;
import java.util.Map;

/**
 * A manager for texture.
 */
public class TextureManager {
    private final TextureAtlas textureAtlas;
    private final Map<String, TextureRegion> cachedTextureRegions;

    /**
     * Constructor for the TextureManager class.
     * Initializes the texture atlas and the cached texture regions.
     */
    public TextureManager() {
        // TODO: use path of the real atlas file
        this.textureAtlas = new TextureAtlas("textures/factory.atlas");
        this.cachedTextureRegions = new HashMap<>();
    }

    /**
     * Get a texture region from the texture atlas.
     * @param name the name of the texture region to get
     * @return the texture region
     * @throws RuntimeException if the texture region is not found
     */
    public TextureRegion getTexture(String name) {
        if (cachedTextureRegions.containsKey(name)) {
            return cachedTextureRegions.get(name);
        }

        TextureRegion textureRegion = textureAtlas.findRegion(name);
        if (textureRegion == null) {
            throw new RuntimeException("Texture not found: " + name);
        }
        cachedTextureRegions.put(name, textureRegion);
        return textureRegion;
    }

    /**
     * Dispose of the texture atlas.
     */
    public void dispose() {
        textureAtlas.dispose();
    }
}