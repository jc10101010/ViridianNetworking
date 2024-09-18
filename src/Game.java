import client.MPGameClient;
import colours.InverseSqrShadow;
import core.RenderObject;
import core.Scene;
import java.awt.Color;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import objects.Vertex;
import objects.Vertex2D;

public class Game {
    private Scene scene; //The game scene containing objects and rendering logic
    private JPanel panel; //The panel where the game is rendered

    private RenderObject enemy; //Enemy object in the game
    private RenderObject plane; //Plane object in the game (likely the ground or platform)
    private Vertex playerPosition = new Vertex(0, 2, -10); //Player's initial position in 3D space
    private Vertex playerRotation = new Vertex(0, 0, 0); //Player's rotation (pitch, yaw, roll)
    
    private Vertex moveDir = new Vertex(0, 0, 0); //Movement direction for the player
    private Vertex2D mousePosition = new Vertex2D(0, 0); //Mouse position in 2D (normalized)

    private float moveSpeed = 8.0f; //Player movement speed
    private float rotationSpeed = 80.0f; //Player rotation speed
    private double lastFrameTime; //Timestamp of the last frame in nanoseconds
    private double timeSinceLast; //Time since the last frame

    private boolean isWDown = false; //State if 'W' is pressed
    private boolean isADown = false; //State if 'A' is pressed        
    private boolean isSDown = false; //State if 'S' is pressed
    private boolean isDDown = false; //State if 'D' is pressed

    private Action wDown; //Action for pressing 'W'
    private Action wUp; //Action for releasing 'W'
    private Action aDown; //Action for pressing 'A'
    private Action aUp; //Action for releasing 'A'
    private Action sDown; //Action for pressing 'S'
    private Action sUp; //Action for releasing 'S'
    private Action dDown; //Action for pressing 'D'
    private Action dUp; //Action for releasing 'D'

    private MPGameClient client; //Client for multiplayer interaction

    //Constructor that initializes the game, scene, and starts the client
    public Game(Scene scene, GPanel panel) {
        this.scene = scene;
        this.panel = panel;
        this.lastFrameTime = System.nanoTime(); //Capture the initial timestamp

        try {
            //Create the multiplayer client
            this.client = new MPGameClient(scene);
        } catch (Exception e) {
            e.printStackTrace(); //Handle exceptions if client initialization fails
        }
        
        //Start the client in a separate thread after a 1-second delay
        new Thread(() -> {
            try {
                Thread.sleep(1000); //Delay client start
                client.run_client(); //Run the client in a loop
                
            } catch (Exception e) {
                e.printStackTrace(); //Handle exceptions during client run
            }
        }).start();

        //Set a custom cursor for the game panel (invisible cursor)
        panel.setCursor(panel.getToolkit().createCustomCursor(
            new BufferedImage(3, 3, BufferedImage.TYPE_INT_ARGB), new Point(0, 0),
            "null"));

        //Setup the game objects and event listeners
        setupScene();
        setKeyListeners();
        setMouseListener();
    }

    //Set up the initial scene with game objects
    private void setupScene() {
        //Load enemy and plane models into the scene with corresponding shadows
        enemy = RenderObject.loadObject("data/teapot.obj", "enemy", new InverseSqrShadow(new Color(255,0,0), scene), new Vertex(0,0f,0));
        plane  = RenderObject.loadObject("data/plane.obj", "plane", new InverseSqrShadow(new Color(255, 255, 255), scene), new Vertex(0,0,0));

        //Set scaling for the plane object
        float planeScale = 3;
        plane.setScale(new Vertex(planeScale, planeScale, planeScale));

        //Add enemy and plane objects to the scene
        scene.addObject(enemy);
        scene.addObject(plane);

        //Define keyboard actions for player movement
        defineAction();
    }

    //Define actions for key presses and releases
    public void defineAction() {
        //'W' key press moves the player forward (positive z-axis)
        wDown = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (!isWDown) {
                    moveDir.z += 1;
                    isWDown = true;
                }
            } };
    
        //'W' key release stops the forward movement
        wUp = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (isWDown) {
                    moveDir.z -= 1;
                    isWDown = false;
                }
            } };

        //'A' key press moves the player left (negative x-axis)
        aDown = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (!isADown) {
                    moveDir.x -= 1;
                    isADown = true;
                }
            } };

        //'A' key release stops the leftward movement
        aUp = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (isADown) {
                    moveDir.x += 1;
                    isADown = false;
                }
            } };

        //'S' key press moves the player backward (negative z-axis)
        sDown = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (!isSDown) {
                    moveDir.z -= 1;
                    isSDown = true;
                }
            } };

        //'S' key release stops the backward movement
        sUp = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (isSDown) {
                    moveDir.z += 1;
                    isSDown = false;
                }
            } };

        //'D' key press moves the player right (positive x-axis)
        dDown = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (!isDDown) {
                    moveDir.x += 1;
                    isDDown = true;
                }
            } };

        //'D' key release stops the rightward movement
        dUp = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (isDDown) {
                    moveDir.x -= 1;
                    isDDown = false;
                }
            } };
    }

    //Main game loop that updates every frame
    public void tick() {
        //Calculate time elapsed since the last frame
        timeSinceLast = ( (double) System.nanoTime() - lastFrameTime)/ (1000000000.0);

        //Update player rotation and position
        setRotation();
        setPosition();

        //Uncomment to add transformations to the enemy object for testing
        //enemy.alterPosition(Vertex.multiply(new Vertex(0f, 0,0.5f), new Vertex((float) timeSinceLast,(float) timeSinceLast,(float) timeSinceLast)) );
        //enemy.alterRotation(Vertex.multiply(new Vertex(0f, 3f,0f), new Vertex((float) timeSinceLast,(float) timeSinceLast,(float) timeSinceLast)) );
        //enemy.alterScale(Vertex.multiply(new Vertex(0.5f, 0.5f,0.5f), new Vertex((float) timeSinceLast,(float) timeSinceLast,(float) timeSinceLast)) );
        
        //Update the camera position and rotation in the scene
        scene.setCamRot(playerRotation);
        scene.setCamPos(playerPosition);

        //Send player position and rotation to the multiplayer client
        client.setPlayerPosition(playerPosition);
        client.setPlayerRotation(playerRotation);

        //Update the last frame time
        lastFrameTime = System.nanoTime();
    }

    //Updates the player rotation based on mouse movement
    private void setRotation() {
        playerRotation.y = mousePosition.x * rotationSpeed * 0.1f; //Yaw rotation
        playerRotation.x = mousePosition.y * rotationSpeed * 0.1f; //Pitch rotation

        //Clamp pitch rotation to avoid flipping over
        if (playerRotation.x > Math.PI/2) {
            playerRotation.x = (float) Math.PI/2;
        } else if (playerRotation.x <  -Math.PI/2) {
            playerRotation.x = (float) -Math.PI/2;
        }
    }

    //Updates the player position based on movement direction and time elapsed
    private void setPosition() {
        float moveDirMagnitude = moveDir.magnitude(); //Calculate the magnitude of movement direction
        Vertex moveDirNormalized = null;

        if (moveDirMagnitude != 0) {
            moveDirNormalized = Vertex.divide(moveDir, moveDirMagnitude); //Normalize direction
        } else {
            moveDirNormalized = new Vertex(0, 0, 0); //If no movement, set to zero
        }

        //Rotate movement direction based on player yaw (Y-axis rotation)
        Vertex playerRotationY = new Vertex(0, playerRotation.y, 0);
        Vertex moveDirNormalRotated = Vertex.rotate(moveDirNormalized, playerRotationY);

        //Move player position based on direction, speed, and time
        playerPosition.x += moveDirNormalRotated.x * moveSpeed * timeSinceLast;
        playerPosition.z += moveDirNormalRotated.z * moveSpeed * timeSinceLast;
    }

    //Setup mouse listeners for interaction
    private void setMouseListener() {
        //Add listener for mouse clicks (not used currently)
        panel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent mouseEvent) { 
                //Handle mouse clicks (currently no action)
            }
        });

        //Add listener for mouse movement
        panel.addMouseMotionListener(new MouseAdapter() {
            public void mouseMoved(MouseEvent mouseEvent) { 
                mouseListener(mouseEvent); //Update mouse position when moved
            }
        });
    }

    //Update the 2D mouse position when the mouse is moved
    private void mouseListener(MouseEvent mouseEvent) {
        mousePosition = new Vertex2D(mouseEvent.getX()/ ((float) panel.getWidth()) - 0.5f, 
                                     mouseEvent.getY()/((float) panel.getHeight()) - 0.5f);
    }

    //Setup key listeners for movement controls (WASD)
    private void setKeyListeners() {
        //Bind 'W' key press and release events
        panel.getInputMap().put(KeyStroke.getKeyStroke("W"), "pressedW");
        panel.getInputMap().put(KeyStroke.getKeyStroke("released W"), "releasedW");

        //Bind 'A' key press and release events
        panel.getInputMap().put(KeyStroke.getKeyStroke("A"), "pressedA");
        panel.getInputMap().put(KeyStroke.getKeyStroke("released A"), "releasedA");

        //Bind 'S' key press and release events
        panel.getInputMap().put(KeyStroke.getKeyStroke("S"), "pressedS");
        panel.getInputMap().put(KeyStroke.getKeyStroke("released S"), "releasedS");

        //Bind 'D' key press and release events
        panel.getInputMap().put(KeyStroke.getKeyStroke("D"), "pressedD");
        panel.getInputMap().put(KeyStroke.getKeyStroke("released D"), "releasedD");

        //Map actions for key press and release to movement directions
        panel.getActionMap().put("pressedW", wDown);
        panel.getActionMap().put("releasedW", wUp);
        panel.getActionMap().put("pressedA", aDown);
        panel.getActionMap().put("releasedA", aUp);
        panel.getActionMap().put("pressedS", sDown);
        panel.getActionMap().put("releasedS", sUp);
        panel.getActionMap().put("pressedD", dDown);
        panel.getActionMap().put("releasedD", dUp);
    }

}
