/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.teamecho.levels;

/**
 * Created by Jeff Grant 22/02/17
 *
 * @author 1622542
 */
import com.teamecho.game.Game;
import com.teamecho.game.objects.Platform;
import com.teamecho.game.objects.Portal;
import com.teamecho.characters.Player;
import com.teamecho.characters.Ember;
import com.teamecho.characters.Enemy;
import com.teamecho.characters.SpikePit;
import com.teamecho.game.Sound;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Font;
import java.awt.Color;
import java.awt.Rectangle;

/**
 * This panel represents the game world It contains all of the objects that take
 * part in the game It uses a timer to update every 10ms It uses a keyAdapter to
 * listen for user key presses and update the player's position
 */
public class Level2 extends JPanel implements ActionListener {

    private int score = 0;
    private int health = 100;
    private Timer timer;
    BufferedImage background;
    private Game game;
    private Player thePlayer;
    private Portal thePortal;
    private Ember[] embers;
    private Enemy[] enemies;
    private SpikePit[] spikepit;
    private Platform[] Platform;
    private int InAir;
    int VIEWPORT_SIZE_X = 800;
    int offsetMaxX = 3600 - VIEWPORT_SIZE_X;
    int offsetMinX = 0;
    int camX = 0;
    int camY = 0;
    public int CurrentCollisionDelay = 0;
    public int MaxCollisionDelay = 15;

    private final int NUMBER_OF_ENEMIES = 11;
    private final int[] EnemyX = {5, 8, 13, 21, 22, 26, 32, 40, 43, 46, 49};
    private final int[] EnemyY = {2, 2, 5, 3, 9, 3, 3, 8, 4, 4, 8};
    private final String[] EnemyDirection = {"UP", "UP", "RIGHT", "UP", "DOWN", "LEFT", "UP", "DOWN", "UP", "UP", "DOWN"};
    private final int[] EnemyDistance = {4, 2, 2, 6, 7, 2, 5, 4, 4, 4, 4};
    private final int NUMBER_OF_EMBERS = 10;
    private final int[] EmberX = {4, 8, 12, 18, 18, 23, 38, 44, 47, 53};
    private final int[] EmberY = {6, 5, 6, 3, 7, 5, 8, 7, 7, 8};
    private final int NUMBER_OF_SPIKEPITS = 31;
    private final int[] SpikepitX = {5, 6, 8, 12, 16, 20, 22, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53};
    private final int[] SpikepitY = {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
    private final int NUMBER_OF_PLATFORMS = 26;
    private final int[] PlatformX = {3, 4, 4, 4, 6, 7, 9, 10, 11, 13, 14, 15, 24, 25, 26, 27, 29, 31, 33, 34, 36, 39, 42, 45, 48, 51};
    private final int[] PlatformY = {1, 1, 2, 3, 3, 3, 3, 3, 3, 4, 4, 4, 2, 2, 2, 1, 3, 4, 5, 5, 6, 5, 5, 5, 5, 6};

    private final int PortalX = 53;
    private final int PortalY = 2;
    private final int blockspace = 64;
    private final int startline = 100;

    private final int GroundLevel = 552;

    public Level2(Game theGame) {

        game = theGame;

        reset();
        init();
    }

    //reset sets all objects and variables to default values or creates new ones
    public void reset() {
        health = 100;
        thePlayer = new Player();
        thePlayer.setX(64);
        thePlayer.setY(GroundLevel);
        thePlayer.Land();
        thePlayer.setdX();
        score = 0;
        health = 100;
        thePortal = new Portal(startline + (PortalX * blockspace), GroundLevel - (PortalY * blockspace));
        embers = new Ember[NUMBER_OF_EMBERS];
        enemies = new Enemy[NUMBER_OF_ENEMIES];
        spikepit = new SpikePit[NUMBER_OF_SPIKEPITS];
        Platform = new Platform[NUMBER_OF_PLATFORMS];

        //Initialise all embers
        for (int i = 0; i < NUMBER_OF_EMBERS; i++) {

            embers[i] = new Ember(startline + (EmberX[i] * blockspace), GroundLevel - (EmberY[i] * blockspace), 30);
        }

        // Initialise all Monster Objects
        for (int j = 0; j < NUMBER_OF_ENEMIES; j++) {
            enemies[j] = new Enemy(startline + (EnemyX[j] * blockspace), GroundLevel - (EnemyY[j] * blockspace), EnemyDirection[j], EnemyDistance[j] * blockspace);
        }

        // Initialise all Spike Pits
        for (int k = 0; k < NUMBER_OF_SPIKEPITS; k++) {
            spikepit[k] = new SpikePit(startline + (SpikepitX[k] * blockspace), GroundLevel - (SpikepitY[k] * blockspace));
        }

        for (int i = 0; i < NUMBER_OF_PLATFORMS; i++) {
            Platform[i] = new Platform(startline + (PlatformX[i] * blockspace), GroundLevel - (PlatformY[i] * blockspace));
        }
    }

//This is the private init method that we use to set the defaults for the 3. * level.
//We can call this method to reset the level (if required) - we can't do that
//with the constructor method - that can only be called once.
    public void init() {
        score = 0;
        health = 100;
        addKeyListener(new TAdapter());
        setFocusable(true);
        setDoubleBuffered(true);

        try {
            background = ImageIO.read(getClass().getResource("/Images/Screens/level2_background.png"));
        } catch (Exception ex) {
            System.err.println("Error loading Level 2 background image");
        }

        timer = new Timer(10, this);
        timer.start();

        //Starts the background music
        Sound.play(getClass().getResourceAsStream("/Sounds/music.wav"), true);

    }

    /**
     * This method is called in response to the timer firing Every 10ms, this
     * method will update the state of the game in response to changes such as
     * key presses and to generate computer movement
     *
     * @param ae
     */
    @Override
    public void actionPerformed(ActionEvent ae) {
        /**
         * The repaint method starts the process of updating the screen -
         * calling /our version of the paintComponent method, which has the code
         * for drawing /our characters and objects
         */
        DoCameraMove();
        DoMovement();
        checkCollisions();
        DoAnimate();
        repaint();
        // this reduces the value of collision delay if it is above 0
        if (CurrentCollisionDelay > 0) {
            CurrentCollisionDelay--;
        }
    }

    private void DoCameraMove() {
        /**
         * this is where the calculations for camera movement are handled only x
         * is included here as level one does not need to move up or down so
         * including x would be redundant camX is set to the player's x value it
         * is then adjusted by how much you can see of the screen to centre it
         * on the player
         *
         *
         * the camX value is then checked to make sure that it is within the
         * bounds of the drawn level and adjusted if it is not
         */
        camX = thePlayer.getX() - VIEWPORT_SIZE_X / 2;
        if (camX > offsetMaxX) {
            camX = offsetMaxX;
        } else if (camX < offsetMinX) {
            camX = offsetMinX;
        }

    }

    /**
     * This method calls the movement methods on characters and NPCs
     */
    public void DoMovement() {
        thePlayer.updateMove();

        for (int i = 0; i < NUMBER_OF_ENEMIES; i++) {
            enemies[i].move();
        }
    }

    /**
     * This method will be called to check for collisions
     */
    public void checkCollisions() {
        Rectangle playerBounds = thePlayer.getBounds();//this gets the player's bounds
        //these variable will be updated with the bounds of each object in a loop
        Rectangle currentEmberBounds;
        Rectangle currentEnemyBounds;
        Rectangle currentSpikePitBounds;

        InAir = 1;
        //this ckecks to see if the player is lower in the ground than ground level
        if (thePlayer.getY() > GroundLevel - thePlayer.getSpriteHeight()) {
            //the player is set to landed and they are moved out of the ground
            thePlayer.Land();
            thePlayer.setY(GroundLevel - thePlayer.getSpriteHeight());
            InAir = 2;

        }

        for (int i = 0; i < NUMBER_OF_PLATFORMS; i++) {
            //this ckecks to see if the player is lower in the block than the blocks y level but higher than the bottom and within the left and right side of the object
            if (thePlayer.getY() < Platform[i].getY() && thePlayer.getY() > (Platform[i].getY() - thePlayer.getSpriteHeight()) && thePlayer.getX() > Platform[i].getX() - thePlayer.getSpriteWidth() - 1 && thePlayer.getX() < (Platform[i].getX() + Platform[i].getSpriteWidth())) {
                //the player is set to landed and they are moved out of the ground
                thePlayer.Land();
                thePlayer.setY(Platform[i].getY() - thePlayer.getSpriteHeight());
                InAir = 2;
            }

        }
        //this checks to see if the player is above the ground
        if (thePlayer.getY() == GroundLevel - thePlayer.getSpriteHeight()) {
            InAir = 2;
        }
        //this checks to see if the player is above any platforms
        for (int k = 0; k < NUMBER_OF_PLATFORMS; k++) {
            if (thePlayer.getY() == Platform[k].getY() - thePlayer.getSpriteHeight() && thePlayer.getX() > Platform[k].getX() && thePlayer.getX() < (Platform[k].getX() + Platform[k].getSpriteWidth())) {
                InAir = 2;
            }
        }
        // if the player is in the air then
        if (InAir == 1) {
            thePlayer.falling();

        }
        // Check to see if the player boundary (rectangle) intersects
        // with the ember boundary (i.e. there is a collision)
        for (int i = 0; i < NUMBER_OF_EMBERS; i++) {
            if (embers[i].getVisible() == true) {
                currentEmberBounds = embers[i].getBounds();

                if (playerBounds.intersects(currentEmberBounds) == true) {
                    score += embers[i].getScore();
                    embers[i].setVisible(false);
                }
            }
        }

        //this checks to see if the player is able to collide again
        if (CurrentCollisionDelay <= 0) {
            //the player is tested to see if they are within either object and does damage accordingly
            for (int j = 0; j < NUMBER_OF_ENEMIES; j++) {
                currentEnemyBounds = enemies[j].getBounds();
                if (enemies[j].getVisible() == true) {
                    if (playerBounds.intersects(currentEnemyBounds)) {
                        DamagePlayer(25);
                    }
                }
            }

            for (int k = 0; k < NUMBER_OF_SPIKEPITS; k++) {
                currentSpikePitBounds = spikepit[k].getBounds();

                if (spikepit[k].getVisible() == true) {
                    if (playerBounds.intersects(currentSpikePitBounds)) {
                        DamagePlayer(25);
                    }
                }
                //this is used to delay the next collision
                CurrentCollisionDelay = MaxCollisionDelay;
            }
        }
        // this changes the screen when the player reaches the end of the level
        if (playerBounds.intersects(thePortal.getBounds())) {
            reset();
            game.SelectScreen(4);
            pause();
        }

        //this handles the collision for the left and right side of the platforms
        for (int i = 0; i < NUMBER_OF_PLATFORMS; i++) {
            /**
             * they check to see if the y value is between the top of the
             * platform and the bottom and if the player is clipping into the
             * left or right then forceably places them outside
             *
             */
            if (thePlayer.getX() > (Platform[i].getX() - thePlayer.getSpriteWidth()) && thePlayer.getX() < (Platform[i].getX() - thePlayer.getSpriteWidth()) + 32 && thePlayer.getY() >= Platform[i].getY() && thePlayer.getY() <= (Platform[i].getY() + Platform[i].getSpriteHeight())) {
                thePlayer.setX(Platform[i].getX() - thePlayer.getSpriteWidth());
            }
            if (thePlayer.getX() < (Platform[i].getX() + Platform[i].getSpriteWidth()) && thePlayer.getX() > (Platform[i].getX() + Platform[i].getSpriteWidth() - 32) && thePlayer.getY() >= Platform[i].getY() && thePlayer.getY() <= (Platform[i].getY() + Platform[i].getSpriteHeight())) {
                thePlayer.setX(Platform[i].getX() + Platform[i].getSpriteWidth());
            }
        }

        //this checks to see if the player has gone outside the level to the left or right, and then sets them to be inside
        if (thePlayer.getX() < 1) {
            thePlayer.setX(1);
        }
        if (thePlayer.getX() > (3600 - thePlayer.getSpriteWidth())) {
            thePlayer.setX(3600 - thePlayer.getSpriteWidth());
        }

    }

    //this causes the animatins to advance in player and ember
    public void DoAnimate() {
        for (int i = 0; i < NUMBER_OF_EMBERS; i++) {
            embers[i].Animate();
        }
        thePlayer.Animate();
    }

    /**
     * This method initiates the in game drawing. The graphics parameter allows
     * drawing operations to be carried out on the component. We use this method
     * to draw all of the game components - layering them from front to back
     *
     * @param g
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.translate(-camX, 0);
        //Draw Background
        g.drawImage(background, 0, 0, null);
        //Draw Obsticles

        //Draw the player Character
        g.drawImage(thePlayer.getSprite(), thePlayer.getX(), thePlayer.getY(), null);
        g.drawImage(thePortal.getSprite(), thePortal.getX(), thePortal.getY(), null);
        //Draw the ember if it has not been picked up
        for (int i = 0; i < NUMBER_OF_EMBERS; i++) {
            if (embers[i].getVisible() == true) {
                g.drawImage(embers[i].getSprite(), embers[i].getX(), embers[i].getY(), null);
            }
        }

        //Draw each monster on screen if it is alive
        for (int j = 0; j < NUMBER_OF_ENEMIES; j++) {
            if (enemies[j].getVisible() == true) {
                g.drawImage(enemies[j].getSprite(), enemies[j].getX(), enemies[j].getY(), null);
            }
        }

        //Draw Spike Pits on screen
        for (int k = 0; k < NUMBER_OF_SPIKEPITS; k++) {
            g.drawImage(spikepit[k].getSprite(), spikepit[k].getX(), spikepit[k].getY(), null);
        }
        for (int i = 0; i < NUMBER_OF_PLATFORMS; i++) {

            g.drawImage(Platform[i].getSprite(), Platform[i].getX(), Platform[i].getY(), null);

        }

        //Code to draw the score and health on screen
        Font uiFont = new Font("Arial", Font.PLAIN, 20);
        g.setColor(Color.black);
        g.setFont(uiFont);
        g.drawString("Score: " + score, camX, 20);
        g.drawString("Health " + health + "/100", camX, 42);
        g.dispose();
    }

    /**
     * This is a private KeyAdapter Class that we use to process keypresses it
     * assigns each keypress a value then sends it to the player class to be
     * translated into movement
     */
    private class TAdapter extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {
            int move = 0;
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    move = 1;
                    break;
                case KeyEvent.VK_RIGHT:
                    move = 2;
                    break;
                case KeyEvent.VK_SPACE:
                    move = 3;
                    break;

                default:
                    break;
            }
            thePlayer.move(move);
        }

        @Override
        public void keyReleased(KeyEvent e) {

            int stop = 0;
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    stop = 1;
                    break;
                case KeyEvent.VK_RIGHT:
                    stop = 2;
                    break;

                default:
                    break;
            }
            //the key release for jump is not included here as it would have no effect
            thePlayer.stop(stop);
        }
    }

    private void DamagePlayer(int Damage) {
        health -= Damage;
        if (health <= 0) {
            reset();
            game.SelectScreen(5);
        }

    }

    public void start() {
        timer.start();
    }

    public void pause() {
        timer.stop();
    }
}
