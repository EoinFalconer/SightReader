package openCVImpl;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sound.midi.Sequence;
import javax.swing.JOptionPane;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
 
public final class Main extends Application {
 
    private Desktop desktop = Desktop.getDesktop();
    
    private Scene scene;
    
    public int count;
 
    @Override
    public void start(final Stage stage) {
        stage.setTitle("SIGHT READER");
 
        final FileChooser fileChooser = new FileChooser();
 
        final Button openButton = new Button("Open a .bmp file of your score");
        
        final Label niceTitle = new Label("Welcome to Sight Reader!");
 
        openButton.setOnAction(
            new EventHandler<ActionEvent>() {
                @Override
                public void handle(final ActionEvent e) {
                    configureFileChooser(fileChooser);
                    File file = fileChooser.showOpenDialog(stage);
                    if (file != null) {
                        readScore(file, stage);
                    }
                }
            });
 
 
        final GridPane inputGridPane = new GridPane();
        
        GridPane.setConstraints(openButton, 0, 0);
        inputGridPane.setHgap(6);
        inputGridPane.setVgap(6);
        inputGridPane.getChildren().addAll(openButton);
        final Pane rootGroup = new VBox(12);
        rootGroup.getChildren().addAll(niceTitle, inputGridPane);
        rootGroup.setPadding(new Insets(12, 12, 12, 12));
        this.scene = new Scene(rootGroup,200,200);
        stage.setScene(scene);
        stage.show();
    }
 
    public static void main(String[] args) {
        Application.launch(args);
    }
         
        private static void configureFileChooser(final FileChooser fileChooser){                           
        fileChooser.setTitle("View Pictures");
        fileChooser.setInitialDirectory(
            new File(System.getProperty("user.home"))
        ); 
    }
            
 
    private void readScore(File file, Stage stage) {
    	Score score = new Score(file.getAbsolutePath());
    	final Pane rootGroup = new VBox(12);
    	ArrayList<Sequence> sequences = score.buildSymbols(file.getAbsolutePath());
    	ArrayList<Button> playButtons = new ArrayList<Button>();
    	ArrayList<Button> pauseButtons = new ArrayList<Button>();
    	ArrayList<MidiPlayer> players = new ArrayList<MidiPlayer>();
    	for(int i=0;i<sequences.size();i++){
    		playButtons.add(new Button("Play Part " + i));
    		pauseButtons.add(new Button("Pause Part " + i));
    		players.add(new MidiPlayer());
    	}
    	
    	for(int i=0;i<sequences.size();i++){
    		this.count = i;
    		playButtons.get(i).setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(final ActionEvent e) {
                	Button currentButton = (Button)e.getSource();
                	String name = currentButton.getText();
                	char currentTrack = name.charAt(name.length()-1);
                	String trackNumber = "" + currentTrack;
                    players.get(Integer.parseInt(trackNumber)).play(sequences.get(Integer.parseInt(trackNumber)), true);
                }
            });
    		pauseButtons.get(i).setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(final ActionEvent e) {
                	Button currentButton = (Button)e.getSource();
                	String name = currentButton.getText();
                	char currentTrack = name.charAt(name.length()-1);
                	String trackNumber = "" + currentTrack;
                    players.get(Integer.parseInt(trackNumber)).stop();
                }
            });
    		rootGroup.getChildren().addAll(playButtons.get(i),pauseButtons.get(i));
    	}
    	rootGroup.setPadding(new Insets(12, 12, 12, 12));
    	this.scene = new Scene(rootGroup, 600, 600);
    	stage.setScene(scene);
        stage.show();
    }
}