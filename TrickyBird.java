import javax.swing.*;
import javax.swing.Timer;
import java.awt.event.*;
import java.util.*;
import java.awt.*;
import javax.sound.sampled.*;
import java.lang.Thread;
import java.io.*;

public class TrickyBird extends JPanel implements ActionListener, KeyListener{
    int fWidth=360;
    int fheight=640;
    Image bgImg;
    Image birdImg;
    Image TopPipe;
    Image BottomPipe;
    Image tk;
    Image spacebutton;
    
    public class Bird
    {
    int birdX=fWidth/8;
    int birdY=fheight/2;
    int birdWidth=54;
    int birdHeight=54;
    Image img;
    Bird(Image img)
    {
        this.img=img;
    }
    }

        int pipeX=fWidth;
        int pipeY=0;
        int pipeWidth=64;
        int pipeHeight=512;

    public class Pipe{
        int x=pipeX;
        int y=pipeY;
        int width=pipeWidth;
        int height=pipeHeight;
        Boolean passed=false;
        Image img;
        Pipe(Image img)
        {
            this.img=img;
        }
    }
    
    public static class Sound
    {
        Clip GameStartClip;
    	Clip GameOverClip;

    	public Sound() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
    	    File gameover = new File("GameOver.wav");
            File gamestart = new File("GameStart2.wav");
            AudioInputStream GameStartAudio = AudioSystem.getAudioInputStream(gamestart);
    	    AudioInputStream GameOverAudio = AudioSystem.getAudioInputStream(gameover);
            GameStartClip = AudioSystem.getClip();
            GameStartClip.open(GameStartAudio);
    	    GameOverClip = AudioSystem.getClip();
    	    GameOverClip.open(GameOverAudio);
    	}

        public void GameStart()
        {
            if(GameStartClip.isRunning())
            {
                GameStartClip.stop();
            }
            GameStartClip.setFramePosition(0);
            GameStartClip.start();
        }

    	public void GameOver() {
    	    if (GameOverClip.isRunning()) {
    	        GameOverClip.stop();
    	    }
    	    GameOverClip.setFramePosition(1);
    	    GameOverClip.start();
    	}

    	
    }

    Bird bird;
    Timer gameloop;
    Timer PlacePipesTimer;
    int VelocityX=-4;
    int VelocityY=0;
    int gravity=1;
    double score=0;
    Boolean gameOver=false;
    Random random = new Random();
    ArrayList<Pipe> pipes;

    TrickyBird()
    {
        setPreferredSize(new Dimension(fWidth,fheight));
        setFocusable(true);
        addKeyListener(this);
        bgImg=new ImageIcon(getClass().getResource("./beach.jpg")).getImage();
        birdImg=new ImageIcon(getClass().getResource("./yellowbird2.png")).getImage();
        TopPipe=new ImageIcon(getClass().getResource("./toppipe.png")).getImage();
        BottomPipe=new ImageIcon(getClass().getResource("./bottompipe.png")).getImage();
        tk = new ImageIcon(getClass().getResource("./tk.png")).getImage();
        spacebutton = new ImageIcon(getClass().getResource("./spacebutton.png")).getImage();
        bird = new Bird(birdImg);
        gameloop = new Timer(1000/60,this);
        gameloop.start();

        pipes = new ArrayList<>();
        PlacePipesTimer = new Timer(2000,new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                placePipes();
            }
        });
        try {
            Sound sound = new Sound();
            sound.GameStart();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        PlacePipesTimer.start();
    }

    public void placePipes()
    {
        int openspace = fheight/4;
        int randomPipeY=(int) (pipeY-pipeHeight/4-Math.random()*(pipeHeight/2));
        Pipe topPipe = new Pipe(TopPipe);
        topPipe.y=randomPipeY;
        pipes.add(topPipe);

        Pipe bottomPipe = new Pipe(BottomPipe);
        bottomPipe.y=topPipe.y+pipeHeight+openspace;
        pipes.add(bottomPipe);
    }
    public void paintComponent(Graphics g1)
    {
        Graphics2D g = (Graphics2D)g1;
        super.paintComponent(g);
        draw(g);
    }
    public void draw(Graphics2D g)
    {
        // System.out.println("Running");
        g.drawImage(bgImg, 0, 0, fWidth,fheight,null);
        g.drawImage(bird.img, bird.birdX, bird.birdY, bird.birdWidth, bird.birdHeight, null);
        
        
        
        for(int i=0; i<pipes.size(); i++)
        {
            Pipe pipe = pipes.get(i);
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }
        g.setColor(Color.black);
        g.setFont(new Font("Arial",Font.PLAIN,32));
        if(gameOver)
        {
            g.drawString("Game Over : "+String.valueOf((int)score), fWidth/6, fheight/2);
            // g.setColor(Color.decode("#FFFFE8"));
            // g.setFont(new Font("Arial",Font.PLAIN,22));
            // g.drawString("Hit SpaceBar to Start :) ", fWidth/6, (fheight/2)+100);
            g.drawImage(spacebutton, fWidth/6, (fheight/2)+100,null);
        }
        else
        {
            g.drawString(String.valueOf((int)score), 10,32 );
        }
        g.drawImage(tk, (fWidth/2)+40, fheight-30,null);
        // g.setFont(new Font("MV Boli",Font.PLAIN,14));
        // g.setColor(Color.decode("#000000"));
        // // g.setStroke(new BasicStroke(2));
        // g.drawString("Created By Tharun", (fWidth/2)+35, fheight-10);
       
    }
    public void move() throws UnsupportedAudioFileException, IOException, LineUnavailableException, InterruptedException
    {
        VelocityY+=gravity;
        bird.birdY+=VelocityY;
        bird.birdY=Math.max(bird.birdY, 0);
        for(int i=0; i<pipes.size(); i++)
        {
            Pipe pipe = pipes.get(i);
            pipe.x+=VelocityX;
            if(!pipe.passed &&bird.birdX > pipe.x + pipe.width)
            {
            	
                pipe.passed=true;
                score +=0.5;
                
            }
            
            if(hit(bird, pipe))
            {
                gameOver=true;
            }
        }
        if(bird.birdY>fheight)
        {
            gameOver=true;
        }
    }
    public Boolean hit(Bird a , Pipe b)
    {
        return a.birdX<b.x+b.width &&
               a.birdX+a.birdWidth>b.x &&
               a.birdY<b.y+b.height &&
               a.birdY+a.birdHeight>b.y;
    }
    @Override
    public void actionPerformed(ActionEvent e) {
       
        try {
			move();
		} catch (UnsupportedAudioFileException | IOException | LineUnavailableException | InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        repaint();
        if(gameOver)
        {
            gameloop.stop();
            PlacePipesTimer.stop();
            Sound sound;
            try {
                sound = new Sound();
                sound.GameOver();
            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            
        }
    }


    
    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode()==KeyEvent.VK_SPACE)
        {
            VelocityY=-9;
            if(gameOver)
             {
                bird.birdY = fheight / 2;

            VelocityY=0;
            pipes.clear();
            gameOver=false;
            score=0;
            gameloop.start();
            PlacePipesTimer.start();
            try {
                Sound sound = new Sound();
                sound.GameStart();
            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

        }
        }
        
        
    }
    @Override
    public void keyTyped(KeyEvent e) {}
    @Override
    public void keyReleased(KeyEvent e) {}
}
