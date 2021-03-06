/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.LoopMode;
import com.jme3.animation.SkeletonControl;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.math.Matrix3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.Spatial.CullHint;

/**
 *
 * @author Bob
 */
public class AnimationManager extends AbstractAppState {
    
private SimpleApplication app;
private AppStateManager   stateManager;
public String             armAnim;
public String             legAnim;
public Player             player;
public SkeletonControl    skelControl;

    
 @Override
  public void initialize(AppStateManager stateManager, Application app) {
    super.initialize(stateManager, app);
    
    this.app          = (SimpleApplication) app; // can cast Application to something more specific
    this.stateManager = this.app.getStateManager();
    this.player       = this.stateManager.getState(Player.class).player;
    }
    
    public void animationInit(Spatial Model){
        
    AnimControl animControl = findAnimControl(Model);
    AnimChannel legChannel  = animControl.createChannel();
    legChannel.addFromRootBone("BottomSpine") ;
    AnimChannel armChannel  = animControl.createChannel();
    armChannel.addFromRootBone("TopSPine");
    armChannel.setAnim("StillArms");
    legChannel.setAnim("StillLegs");


 }

    public void animChange(String armAnim, String legAnim, Spatial model){
      AnimControl animControl = findAnimControl(model);
      AnimChannel legChannel = animControl.getChannel(0);
      AnimChannel armChannel = animControl.getChannel(1);
      if (armAnim.equals("PistolHold") && !armChannel.getAnimationName().equals(armAnim)){
        armChannel.setAnim(armAnim);
        armChannel.setLoopMode(LoopMode.DontLoop);
        }
        else if (!armChannel.getAnimationName().equals(armAnim)) {
        armChannel.setAnim(armAnim);
        armChannel.setLoopMode(LoopMode.Loop);
        }
      
      if (!legChannel.getAnimationName().equals(legAnim)) {
        legChannel.setAnim(legAnim);
        legChannel.setLoopMode(LoopMode.Loop);
        }
     }
    
    
    
    public AnimControl findAnimControl(final Spatial parent){
    
    AnimControl animControl = parent.getControl(AnimControl.class);
    if (animControl != null) {
      return animControl;
    }
 
    if (parent instanceof Node) {
      for (final Spatial s : ((Node) parent).getChildren()) {
        final AnimControl animControl2 = findAnimControl(s);
        if (animControl2 != null) {
        return animControl2;
        }
      }
    }
    return null;
  }
   
  public void billyEquip(Player player){
    skelControl = findSkelControl(player.Model);
    Node hand = skelControl.getAttachmentsNode(skelControl.getSkeleton().getBone(5).getName());
    hand.attachChild(player.Model.getChild("Billy"));
     
    }
  
  public void gunEquip(Player player) {
    player.Model.getChild("Gun").setLocalTranslation(1, 2, 1);
    player.Model.getChild("Gun").setLocalScale(.3f);
    player.Model.getChild("Gun").setLocalRotation
        (new Matrix3f(1f, 5f, 5f, 1f, 1f, 1f, 1f, 5f, 1f));
    player.Model.getChild("Gun").setLocalTranslation(-.5f, 5f, 2.3f);
    player.Model.setCullHint(CullHint.Never);
    animChange("PistolHold", "RunAction", player.Model);
      
    }
  
  public SkeletonControl findSkelControl(final Spatial parent){
    
    skelControl = parent.getControl(SkeletonControl.class);
    if (skelControl != null) {
      return skelControl;
    }
 
    if (parent instanceof Node) {
      for (final Spatial s : ((Node) parent).getChildren()) {
        final SkeletonControl skelControl2 = findSkelControl(s);
        if (skelControl2 != null) {
        return skelControl2;
        }
      }
    }
    return null;
  }
}