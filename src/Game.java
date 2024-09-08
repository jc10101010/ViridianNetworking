

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
    private Scene scene;
    private JPanel panel;

    private RenderObject enemy; 
    private RenderObject plane; 
    private Vertex playerPosition = new Vertex(0, 2, -10);
    private Vertex playerRotation = new Vertex(0, 0, 0);
    
    private Vertex moveDir = new Vertex(0, 0, 0);
    private Vertex2D mousePosition = new Vertex2D(0, 0);

    private float moveSpeed = 8.0f;
    private float rotationSpeed = 80.0f;
    private double lastFrameTime;
    private double timeSinceLast;

    private boolean isWDown = false;
    private boolean isADown = false;        
    private boolean isSDown = false;
    private boolean isDDown = false;

    private Action wDown;
    private Action wUp;
    private Action aDown;
    private Action aUp;
    private Action sDown;
    private Action sUp;
    private Action dDown;
    private Action dUp;

    private MPGameClient client;


    public Game(Scene scene, GPanel panel) {
        this.scene = scene;
        this.panel = panel;
        this.lastFrameTime = System.nanoTime();
        try {
            
            this.client = new MPGameClient(scene);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        new Thread(() -> {
            try {
                Thread.sleep(1000);
                client.run_client();
                
            } catch (Exception e) {
                e.printStackTrace();

            }
            
        }).start();
        
        panel.setCursor(panel.getToolkit().createCustomCursor(
            new BufferedImage(3, 3, BufferedImage.TYPE_INT_ARGB), new Point(0, 0),
            "null"));
        setupScene();
        setKeyListeners();
        setMouseListener();
    }

    private void setupScene() {
        enemy = RenderObject.loadObject("data/teapot.obj", "enemy", new InverseSqrShadow(new Color(255,0,0), scene), new Vertex(0,0f,0));
        plane  = RenderObject.loadObject("data/plane.obj", "enemy", new InverseSqrShadow(new Color(255, 255, 255), scene), new Vertex(0,0,0));

        float planeScale = 3;
        plane.setScale(new Vertex(planeScale, planeScale, planeScale));

        scene.addObject(enemy);
        scene.addObject(plane);

        defineAction();
    }

    public void defineAction() {
        
        wDown = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (!isWDown) {
                    moveDir.z += 1;
                    isWDown = true;
                }
            } };
    
        wUp = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (isWDown) {
                    moveDir.z -= 1;
                    isWDown = false;
                }
            } };
    
        aDown = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (!isADown) {
                    moveDir.x -= 1;
                    isADown = true;
                }
            } };
    
        aUp = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (isADown) {
                    moveDir.x += 1;
                    isADown = false;
                }
            } };
    
        sDown = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (!isSDown) {
                    moveDir.z -= 1;
                    isSDown = true;
                }
            } };
    
        sUp = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (isSDown) {
                    moveDir.z += 1;
                    isSDown = false;
                }
            } };
    
        dDown = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (!isDDown) {
                    moveDir.x += 1;
                    isDDown = true;
                }
            } };
    
        dUp = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (isDDown) {
                    moveDir.x -= 1;
                    isDDown = false;
                }
            } };
    }

    public void tick() {
        timeSinceLast = ( (double) System.nanoTime() - lastFrameTime)/ (1000000000.0);

        setRotation();
        setPosition();

        // enemy.alterPosition(Vertex.multiply(new Vertex(0f, 0,0.5f), new Vertex((float) timeSinceLast,(float) timeSinceLast,(float) timeSinceLast)) );
        // enemy.alterRotation(Vertex.multiply(new Vertex(0f, 3f,0f), new Vertex((float) timeSinceLast,(float) timeSinceLast,(float) timeSinceLast)) );
        // enemy.alterScale(Vertex.multiply(new Vertex(0.5f, 0.5f,0.5f), new Vertex((float) timeSinceLast,(float) timeSinceLast,(float) timeSinceLast)) );
        
        scene.setCamRot(playerRotation);
        scene.setCamPos(playerPosition);

        client.setPlayerPosition(playerPosition);
        client.setPlayerRotation(playerRotation);

        lastFrameTime = System.nanoTime();
    }

    private void setRotation() {
        playerRotation.y = mousePosition.x * rotationSpeed * 0.1f;
        playerRotation.x = mousePosition.y * rotationSpeed * 0.1f;
        if (playerRotation.x > Math.PI/2) {
            playerRotation.x = (float) Math.PI/2;
        } else if (playerRotation.x <  -Math.PI/2) {
            playerRotation.x = (float) -Math.PI/2;
        }
    }

    private void setPosition() {
        float moveDirMagnitude = moveDir.magnitude();
        Vertex moveDirNormalized = null;

        if (moveDirMagnitude != 0) {
            moveDirNormalized = Vertex.divide(moveDir, moveDirMagnitude);
        } else {
            moveDirNormalized = new Vertex(0, 0, 0);
        }
        Vertex playerRotationY = new Vertex(0, playerRotation.y, 0);
        Vertex moveDirNormalRotated = Vertex.rotate(moveDirNormalized, playerRotationY);

        playerPosition.x += moveDirNormalRotated.x * moveSpeed * timeSinceLast;
        //playerPosition.y += moveDirNormalRotated.y * moveSpeed * timeSinceLast;
        playerPosition.z += moveDirNormalRotated.z * moveSpeed * timeSinceLast;
    }

    private void setMouseListener() {
        //Adds listener events for when mouse is moved and when it is clicked
        panel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent mouseEvent) { 
                
            }
        });
        panel.addMouseMotionListener(new MouseAdapter() {
            public void mouseMoved(MouseEvent mouseEvent) { 
                mouseListener(mouseEvent);
            }
        });
        
    }

    private void mouseListener(MouseEvent mouseEvent) {
        mousePosition = new Vertex2D(mouseEvent.getX()/ ((float) panel.getWidth()) - 0.5f, mouseEvent.getY()/((float) panel.getHeight()) - 0.5f);
    }

    private void setKeyListeners() {
        panel.getInputMap().put(KeyStroke.getKeyStroke("W"),
                            "pressedW");
        panel.getInputMap().put(KeyStroke.getKeyStroke("released W"),
                                    "releasedW");
        panel.getInputMap().put(KeyStroke.getKeyStroke("A"),
                            "pressedA");
        panel.getInputMap().put(KeyStroke.getKeyStroke("released A"),
                                    "releasedA");
        panel.getInputMap().put(KeyStroke.getKeyStroke("S"),
                            "pressedS");
        panel.getInputMap().put(KeyStroke.getKeyStroke("released S"),
                                    "releasedS");
        panel.getInputMap().put(KeyStroke.getKeyStroke("D"),
                            "pressedD");
        panel.getInputMap().put(KeyStroke.getKeyStroke("released D"),
                                    "releasedD");
                                
        panel.getActionMap().put("pressedW",
                                    wDown);
        panel.getActionMap().put("releasedW",
                                    wUp);
        panel.getActionMap().put("pressedA",
                                    aDown);
        panel.getActionMap().put("releasedA",
                                    aUp);
        panel.getActionMap().put("pressedS",
                                    sDown);
        panel.getActionMap().put("releasedS",
                                    sUp);
        panel.getActionMap().put("pressedD",
                                    dDown);
        panel.getActionMap().put("releasedD",
                                    dUp);
    }

    

}


