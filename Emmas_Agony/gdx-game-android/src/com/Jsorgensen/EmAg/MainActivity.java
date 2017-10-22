package com.Jsorgensen.EmAg;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import android.view.*;
import android.widget.*;
import android.transition.*;
import android.opengl.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import java.util.*;

public class MainActivity extends AndroidApplication {
	AndroidApplicationConfiguration cfg;
	MyGdxGame game_activity;
	
    java.util.List<TextView> letter_textview;
	java.util.List<String> letter_strings;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		
        cfg = new AndroidApplicationConfiguration();
        
		game_activity = new MyGdxGame();
		
        initialize(game_activity, cfg);

		setContentView(R.layout.main);
		
		letter_strings = new ArrayList<>(Arrays.asList(
			getResources().getStringArray(R.array.letter_string_array)));
		
		letter_textview = new ArrayList<>();
		letter_textview.add((TextView)findViewById(R.id.tv1_0));
		letter_textview.add((TextView)findViewById(R.id.tv1_1));
		letter_textview.add((TextView)findViewById(R.id.tv1_2));
		
		for(int i=0; i<letter_textview.size(); i++){
			letter_textview.get(i).setText(letter_strings.get(i));
		}
    }
	
	static int letter_phrase_count= 0;
	public void letterClickMethod(View v){
		initialize(game_activity, cfg);
		/*
		letter_phrase_count++;
		switch(letter_phrase_count%3){
			case(0):
				if(letter_phrase_count>=letter_strings.size()){
					initialize(game_activity, cfg);
					letter_phrase_count=0;
					break;
				}
				for(int i=0; i<3; i++){
					letter_textview.get(i).setText(letter_strings.get(i+letter_phrase_count));
				}
				letter_textview.get(1).setVisibility(View.INVISIBLE);
				letter_textview.get(2).setVisibility(View.INVISIBLE);
				break;
			case(1):
				letter_textview.get(1).setVisibility(View.VISIBLE);
				break;
			case(2):
				letter_textview.get(2).setVisibility(View.VISIBLE);
				break;
			default:
			
				break;
		}*/
	}
}
