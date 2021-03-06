package mygame;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.input.ChaseCamera;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;

/**
 *
 * @author Bob
 */
public class InteractionManager extends AbstractAppState implements ActionListener {
    
private SimpleApplication app;
private AppStateManager   stateManager;
public InputManager       inputManager;
public  ViewPort          viewPort;
private Camera            cam;

public BetterCharacterControl playerControl;
public Node                   shootables;

private Vector3f            walkDirection = new Vector3f();
private Vector3f            camDir = new Vector3f();
private Vector3f            camLeft = new Vector3f();
private boolean             left = false, right = false, up = false, down = false;

private boolean             initInteraction = false;
private boolean             shoot = false;
private boolean             inventory = false;

public Node                 sceneModel;
public Node                 monsterNode;
public String               armAnim;
public String               legAnim;
private LightManager        lightInteract;

public Player               player;
public GUI                  GUI;
public ChaseCamera          chaseCam;
public int                  attackDelay;
public SceneManager         item;
public SoundManager         audio;
public boolean              isDead;
public boolean              isInverted;

    
  @Override
  public void initialize(AppStateManager stateManager, Application app) {
    super.initialize(stateManager, app);
    
    this.app = (SimpleApplication) app; // can cast Application to something more specific
    this.stateManager  = this.app.getStateManager();
    this.inputManager  = this.app.getInputManager();
    this.viewPort      = this.app.getViewPort();
    this.cam           = this.app.getCamera();
    this.item          = this.stateManager.getState(SceneManager.class);
    this.monsterNode   = this.stateManager.getState(MonsterManager.class).monsterNode;
    this.lightInteract = this.stateManager.getState(LightManager.class);
    this.player        = this.stateManager.getState(Player.class).player;
    this.GUI           = this.stateManager.getState(GUI.class).GUI;
    this.chaseCam      = this.stateManager.getState(CameraManager.class).chaseCam;
    this.audio         = this.stateManager.getState(SoundManager.class);
    
    attackDelay = 0;
    setUpKeys();
    isDead = false;
    armAnim = "StillArms";
    legAnim = "StillLegs";
    initInteraction = true;
    isInverted = true;
    }
  
  //Set up the KeyBindings for Awesomeness
  
  private void setUpKeys() {
    inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_A));
    inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_D));
    inputManager.addMapping("Up", new KeyTrigger(KeyInput.KEY_W));
    inputManager.addMapping("Down", new KeyTrigger(KeyInput.KEY_S));
    inputManager.addMapping("Jump", new KeyTrigger(KeyInput.KEY_SPACE));
    inputManager.addMapping("Shoot", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
    inputManager.addMapping("Grab", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
    inputManager.addMapping("FlashLight", new KeyTrigger(KeyInput.KEY_F));
    inputManager.addMapping("Inventory", new KeyTrigger(KeyInput.KEY_E));
    inputManager.addMapping("Inversion", new KeyTrigger(KeyInput.KEY_Y));
    inputManager.addListener(this, "Inventory");
    inputManager.addListener(this, "Inversion");
    inputManager.addListener(this, "FlashLight");
    inputManager.addListener(this, "Grab");
    inputManager.addListener(this, "Shoot");
    inputManager.addListener(this, "Left");
    inputManager.addListener(this, "Right");
    inputManager.addListener(this, "Up");
    inputManager.addListener(this, "Down");
    inputManager.addListener(this, "Jump");
    
  }
  
  //Action listener for listening when keys are pressed and released
  
  public void onAction(String binding, boolean isPressed, float tpf) {
  if(!isDead){
    if (binding.equals("Left")) {
        
      left = isPressed;
      
    } else if (binding.equals("Right")) {
        
      right= isPressed;
      
    } else if (binding.equals("Up")) {
      
      up = isPressed;

      if (isPressed){
        if (player.getItemInHand().equals("Gun"))
        armAnim = "PistolHold";
        else
        armAnim = "UnarmedRun";
        
        legAnim = "RunAction";
        player.animInteract.animChange(armAnim, legAnim, player.Model);
        
        } else {
        if (player.getItemInHand().equals("Gun"))
        armAnim = "PistolHold";
        else
        armAnim = "StillArms";
        legAnim = "StillLegs";
        player.animInteract.animChange(armAnim, legAnim, player.Model);
        }

    } else if (binding.equals("Down")) {
      down = isPressed;
      
    } else if (binding.equals("Jump")) {          
      player.Jump(player.playerControl);
      
    } else if (binding.equals("Shoot")) {
        
        shoot = isPressed;
        if (isPressed){
        
        } else {
        if(player.heldItem.equals("Gun"))
        armAnim = "PistolHold";
        else
        armAnim = "StillArms";
        legAnim = "StillLegs";
        player.animInteract.animChange(armAnim, legAnim, player.Model);
        }
        
    } else if (binding.equals("Grab") && !isPressed) {
      player.grabItem(item, cam, player);
        
          
    } else if (binding.equals("FlashLight") && !isPressed){
      //lightInteract.flashlightOn();

    } else if (binding.equals("Inversion") && !isPressed){
      if(isInverted){
        chaseCam.setInvertVerticalAxis(false);
        isInverted = false;
        } else{
        chaseCam.setInvertVerticalAxis(true);
        isInverted = true;
        }
        
    } else if (binding.equals("Inventory")) {
      inventory = isPressed;
      player.getInventory(player, GUI);
            

      if (isPressed) {
        inputManager.setCursorVisible(true);
        chaseCam.setDragToRotate(true);
            
        } else {
        inputManager.setCursorVisible(false);
        chaseCam.setDragToRotate(false);
        }
      }
    }
  }
  
    @Override
    public void update(float tpf) {
      if(!isDead)  {
        if (shoot)
        player.attackChecker(cam, player, player.animInteract, legAnim, monsterNode, item, audio);
        GUI.updateInventoryWindow(player, GUI);
        GUI.updateHUDWindow(player, GUI);
        camDir.set(cam.getDirection()).multLocal(10.0f, 0.0f, 10.0f);
        camLeft.set(cam.getLeft()).multLocal(10.0f);
        walkDirection.set(0, 0, 0);
        if (left) {
            walkDirection.addLocal(camLeft);
        }
        if (right) {
            walkDirection.addLocal(camLeft.negate());
        }
        if (up) {
            walkDirection.addLocal(camDir);
        }
        if (down) {
            walkDirection.addLocal(camDir.negate());
        }
       if(initInteraction){
       player.playerControl.setWalkDirection(walkDirection.multLocal(player.speedBonus,0,player.speedBonus));
       }
       player.playerControl.setViewDirection(camDir);
    }
   }
}
    


