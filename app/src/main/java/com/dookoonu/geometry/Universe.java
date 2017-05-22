package com.dookoonu.geometry;

import com.dookoonu.animationframework.GameView3D;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by cowell on 7/21/15.
 */
public class Universe {
    private GLWorld[] worlds;
    private GameView3D gameView3D;
    private TreeMap<String, GLWorld> worldTreeMap;
    private ArrayList<GLShape> autoAnimatedShapes = new ArrayList<>();
    private boolean mapped;

    /**
     * Constructor for Universe
     * @param gameView3D
     * @param worlds array of Open GL worlds
     */
    public Universe(GameView3D gameView3D, GLWorld[] worlds) {
        this.gameView3D = gameView3D;
        this.worlds = worlds;
        mapped = false;
    }

    /**
     * Constructor for Universe
     * A new tree map is automatically created.
     * @param gameView3D
     */
    public Universe(GameView3D gameView3D) {
        this.gameView3D = gameView3D;
        worldTreeMap = new TreeMap<>();
        mapped = true;
    }

    /**
     * Constructor for Universe
     * @param gameView3D
     * @param worldTreeMap Caller provides the TreeMap
     */
    public Universe(GameView3D gameView3D, TreeMap<String, GLWorld> worldTreeMap) {
        this.gameView3D = gameView3D;
        this.worldTreeMap = worldTreeMap;
        mapped = true;
    }

    public void setWorlds(GLWorld[] worlds) {
        this.worlds = worlds;
    }

    public void addWorld(String worldName, GLWorld world){
        worldTreeMap.put(worldName,world);
    }

    public void removeWorld(String worldName){
        worldTreeMap.remove(worldName);
    }

    public void setGameView3D(GameView3D gameView3D) {
        this.gameView3D = gameView3D;
    }

    /**
     * Draw all the worlds in this universe
     */
    public void draw(){
        GL10 surfaceGL = gameView3D.getSurfaceGL();
        if(mapped){
            for (Map.Entry<String, GLWorld> worldEntry : worldTreeMap.entrySet()) {
                worldEntry.getValue().draw(surfaceGL);
            }
        }else{
            for (GLWorld world:worlds){
                world.draw(surfaceGL);
            }
        }
    }

    /**
     *
     */
    public void autoDraw(){
        GL10 surfaceGL = gameView3D.getSurfaceGL();
        for(GLShape shape : autoAnimatedShapes){
            shape.startAnimation();
        }

        if(mapped){
            for (Map.Entry<String, GLWorld> worldEntry : worldTreeMap.entrySet()) {
                worldEntry.getValue().draw(surfaceGL);
            }
        }else{
            for (GLWorld world:worlds){
                world.draw(surfaceGL);
            }
        }

        for(GLShape shape : autoAnimatedShapes){
            shape.endAnimation();
        }
    }

    public void addWorld(String worldName) {
        worldTreeMap.put(worldName, new GLWorld());
    }

    public GLWorld getWorld(int i){
        return worlds[i];
    }

    public GLWorld getWorld(String worldName){
        return worldTreeMap.get(worldName);
    }

    /**
     *
     * @param worldName
     * @param glShape
     */
    public void addShape(String worldName, GLShape glShape){
        worldTreeMap.get(worldName).addShape(glShape);
    }

    /**
     *
     * @param worldName the name of the world
     * @param glShape
     * @param autoAnimate
     */
    public void addShape(String worldName, GLShape glShape, boolean autoAnimate){
        worldTreeMap.get(worldName).addShape(glShape);
        glShape.setAutoAnimate(true);
        autoAnimatedShapes.add(glShape);
    }

    /**
     * Generate all the gl primitives for specific objects. Must be called after all objects
     * have been added to the universe.
     */
    public void close() {
        if(mapped){
            for (Map.Entry<String, GLWorld> worldEntry : worldTreeMap.entrySet()) {
                worldEntry.getValue().generate();
            }
        }else{
            for (GLWorld world:worlds){
                world.generate();
            }
        }
    }
}
