/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;

/**
 *
 * @author Bob
 */
public class physicalAppState extends AbstractAppState {
    
private SimpleApplication app;
private Node              rootNode;
private AssetManager      assetManager;
private AppStateManager   stateManager;
private BulletAppState    physics;

private Spatial           gun;
public  Node              shootables;

public Node sceneModel;
public Node Model;

    
      @Override
  public void initialize(AppStateManager stateManager, Application app) {
    super.initialize(stateManager, app);
    
    this.app          = (SimpleApplication) app; // can cast Application to something more specific
    this.rootNode     = this.app.getRootNode();
    this.assetManager = this.app.getAssetManager();
    this.stateManager = this.app.getStateManager();
    this.physics      = this.stateManager.getState(BulletAppState.class);
    
    shootables = new Node("Shootables");
    rootNode.attachChild(shootables);
    shootables.attachChild(initScene());
    shootables.attachChild(initGun());   
    shootables.attachChild(makeCube("Billy", 5f, 5f, 5f));
    }
      
    public Spatial initScene(){
          
        sceneModel = (Node) assetManager.loadModel("Scenes/Bob'sTestCell.j3o");
        sceneModel.setLocalTranslation(5f, 0f, 1f);
        physics.getPhysicsSpace().add(sceneModel);
        return sceneModel;
        }
   

  /** A cube object for target practice */
  protected Geometry makeCube(String name, float x, float y, float z) {
    Box box = new Box(1, 1, 1);
    Geometry cube = new Geometry(name, box);
    cube.setLocalTranslation(x, y, z);
    Material mat1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    mat1.setColor("Color", ColorRGBA.randomColor());
    cube.setMaterial(mat1);
    return cube;
  }
    
    protected Spatial initGun(){
        
    gun = assetManager.loadModel("Models/Gun/Gun.j3o");
    gun.setLocalTranslation(85f, 0f, 15f);
    gun.setLocalScale(.3f);
    
    CollisionShape gunShape =
    CollisionShapeFactory.createDynamicMeshShape(gun);
    RigidBodyControl gunPhys = new RigidBodyControl(gunShape);
    gun.addControl(gunPhys);
    gunPhys.setMass(1f);
    gunPhys.setKinematic(false);
    physics.getPhysicsSpace().add(gunPhys);
    return gun;
    }

  
    
}
